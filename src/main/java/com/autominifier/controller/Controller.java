package com.autominifier.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autominifier.model.FilePathWrapper;
import com.autominifier.model.FileType;
import com.autominifier.model.settings.SettingsManager;
import com.autominifier.model.settings.SettingsTypeAdapter;
import com.autominifier.model.settings.beans.Settings;
import com.autominifier.model.settings.command.CancelChanges;
import com.autominifier.model.settings.command.CommitChanges;
import com.autominifier.model.settings.command.RestoreDefaults;
import com.autominifier.model.settings.command.SettingsOperation;
import com.autominifier.service.AutoMinifierService;
import com.autominifier.service.DirWatcherService;
import com.autominifier.service.JsonUpdaterService;
import com.autominifier.service.ListingFilesService;
import com.autominifier.service.MinifierService;
import com.autominifier.service.OldMinifiedFilesDeleterService;
import com.autominifier.service.SerializationService;
import com.autominifier.service.SerializationType;
import com.autominifier.service.TerminatorService;
import com.autominifier.ui.AutoMinifierUI;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class Controller {

   private static final Logger LOGGER = LogManager.getLogger(Controller.class);

   private AutoMinifierUI view;

   private GsonBuilder builder;

   private DirWatcherService   watcher;
   private AutoMinifierService autoMinService;

   private StringProperty           root      = new SimpleStringProperty();
   private ObjectProperty<FileType> fileType  = new SimpleObjectProperty<>();
   private BooleanProperty          recursive = new SimpleBooleanProperty();

   public Controller(AutoMinifierUI view) {
      this.view = view;
      rootProperty().bind(view.getDirText().textProperty());
      fileTypeProperty().bind(view.getFileTypeGroup().valueProperty());
      recursiveProperty().bind(view.getRecurciveCheckBox().selectedProperty());
      builder = new GsonBuilder();
      builder.registerTypeAdapter(new TypeToken<Set<Settings>>() {
      }.getType(), new SettingsTypeAdapter());
      // builder = builder.setPrettyPrinting();
   }

   GsonBuilder getGsonBuilder() {
      return builder;
   }

   final StringProperty rootProperty() {
      return root;
   }

   final String getRoot() {
      return root.get();
   }

   final void setRoot(String path) {
      this.root.set(path);
   }

   final ObjectProperty<FileType> fileTypeProperty() {
      return fileType;
   }

   final FileType getFileType() {
      return fileType.get();
   }

   final void setFileType(FileType fileType) {
      this.fileType.set(fileType);
   }

   final BooleanProperty recursiveProperty() {
      return recursive;
   }

   final boolean isRecursive() {
      return recursive.get();
   }

   final void setRecursive(boolean flag) {
      this.recursive.set(flag);
   }

   private void checkSettingsSet() {
      if (view.getPartialSettings() == null) { // Defensive approach
         Settings settings = view.getFinalSettings();
         Settings settingsCopy = new Settings();
         settingsCopy.getCssSettings().init(settings.getCssSettings().getMinSeparator(),
               settings.getCssSettings().getMinExtension(), -1, null);
         settingsCopy.getJsSettings().init(settings.getJsSettings().getMinSeparator(),
               settings.getJsSettings().getMinExtension(), -1, null);
         view.setPartialSettings(new HashSet<>(Arrays.asList(settingsCopy)));
      }
   }

   public void updatePartialSettingsJsonFile() {
      JsonUpdaterService sentinelService = new JsonUpdaterService(getGsonBuilder(), view.getFinalSettings(), getRoot());
      sentinelService.start();
      sentinelService.setOnSucceeded(stateEvent -> {
         LOGGER.debug("JSON FILE UPDATE SUCCEEDED");
         view.setPartialSettings(sentinelService.valueProperty().get());
         startListingFiles();
      });
      sentinelService.setOnFailed(event -> {
         LOGGER.debug("JSON FILE NOT UPDATE FAILED");
         startListingFiles();
      });
   }

   public void startListingFiles() {
      view.enableControls(false);
      view.switchButtonControls(false, false);

      checkSettingsSet();
      Service<ObservableList<FilePathWrapper>> bgrdService = new ListingFilesService(view.getStatusBar().textProperty(),
            view.getStatusBar().progressProperty(), view.getPartialSettings(), getRoot(), getFileType(), isRecursive());
      bgrdService.start();

      bgrdService.setOnSucceeded(stateEvent -> {
         ObservableList<FilePathWrapper> list = bgrdService.valueProperty().get();
         view.getListView().setItems(list);
         view.enableControls(true);
         view.switchButtonControls(!list.isEmpty() & !view.getAutoModeSwitch().isSelected(), true);
      });
      bgrdService.setOnFailed(stateEvent -> {
         view.enableControls(true);
         view.switchButtonControls(true, true);
      });
   }

   public void startMinifying() {
      view.enableControls(false);
      view.switchButtonControls(false, false);

      OldMinifiedFilesDeleterService deleterService = new OldMinifiedFilesDeleterService(view.getPartialSettings(),
            getRoot(), getFileType(), isRecursive());
      deleterService.start();

      deleterService.setOnSucceeded(event -> {
         LOGGER.debug("OLD MINIFIED FILES DELETER SUCCEEDED");

         Service<Void> minService = new MinifierService(view.getStatusBar().textProperty(),
               view.getStatusBar().progressProperty(), view.getFinalSettings(), view.getListView().getItems());
         minService.start();
         EventHandler<WorkerStateEvent> func = ev -> {
            view.enableControls(true);
            view.switchButtonControls(true, true);
         };
         minService.setOnSucceeded(func);
         minService.setOnFailed(func);
      });

      deleterService.setOnFailed(event -> {
         LOGGER.debug("OLD MINIFIED FILES DELETER FAILED");
         view.enableControls(true);
         view.switchButtonControls(true, true);
      });
   }

   public void startAutoMode() {
      view.enableControls(false);
      view.switchButtonControls(false, true);

      // TODO - remove old minified files
      OldMinifiedFilesDeleterService deleterService = new OldMinifiedFilesDeleterService(view.getPartialSettings(),
            getRoot(), getFileType(), isRecursive());
      deleterService.start();

      deleterService.setOnSucceeded(event -> {
         LOGGER.debug("OLD MINIFIED FILES DELETER SUCCEEDED");
         try {
            watcher = new DirWatcherService(view.getPartialSettings(), getRoot(), getFileType(), isRecursive());
            watcher.start();
            autoMinService = new AutoMinifierService(view.getStatusBar().textProperty(),
                  view.getStatusBar().progressProperty(), view.getFinalSettings());
            autoMinService.setDelay(Duration.ZERO);
            autoMinService.start();

            autoMinService.setOnFailed(ev -> {
               LOGGER.debug("AUTO MODE FAILED");
            });
            autoMinService.setOnCancelled(ev -> {
               LOGGER.debug("AUTO MODE CANCELED");
            });
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         }
      });

      deleterService.setOnFailed(event -> {
         LOGGER.debug("OLD MINIFIED FILES DELETER FAILED");
         view.enableControls(true);
         view.switchButtonControls(true, true);
      });
   }

   public void stopAutoMode() {
      Service<Void> terminator = new TerminatorService(watcher, autoMinService);
      terminator.start();
      terminator.setOnSucceeded(stateEvent -> {
         LOGGER.debug("AUTO MODE TERMINATION SUCCEEDED");
         watcher = null;
         autoMinService = null;
      });
      terminator.setOnFailed(event -> {
         LOGGER.debug("AUTO MODE TERMINATION FAILED");
      });
      terminator.setOnCancelled(event -> {
         LOGGER.debug("AUTO MODE TERMINATION CANCELED");
      });
   }

   public void restoreSettings() {
      SettingsOperation restoreDefaults = new RestoreDefaults(view.getDraftSettings());
      manageOp(restoreDefaults);
   }

   public void saveSettings() {
      SettingsOperation commitChanges = new CommitChanges(view.getDraftSettings(), view.getFinalSettings());
      manageOp(commitChanges);
   }

   public void cancelSettings() {
      SettingsOperation cancelChanges = new CancelChanges(view.getDraftSettings(), view.getFinalSettings());
      manageOp(cancelChanges);
   }

   private void manageOp(SettingsOperation operation) {
      SettingsManager manager = SettingsManager.getInstance();
      manager.executeOperation(operation);
   }

   public void loadSettings() {
      SerializationService deserializer = new SerializationService(view.getDraftSettings(), view.getFinalSettings(),
            SerializationType.DESERIALIZE);
      deserializer.start();
      deserializer.setOnSucceeded(stateEvent -> {
         LOGGER.debug("DESERIALZATION SUCCEEDED");
      });
      deserializer.setOnFailed(event -> {
         LOGGER.debug("DESERIALZATION FAILED");
      });
      deserializer.setOnCancelled(event -> {
         LOGGER.debug("DESERIALZATION CANCELED");
      });
   }
   
   public void persistSettingsAndExit() {
      SerializationService serializer = new SerializationService(view.getDraftSettings(), view.getFinalSettings(),
            SerializationType.SERIALIZE);
      serializer.start();
      serializer.setOnSucceeded(stateEvent -> {
         LOGGER.debug("SERIALZATION SUCCEEDED");
         Platform.exit();
      });
      serializer.setOnFailed(event -> {
         LOGGER.debug("SERIALZATION FAILED");
         Platform.exit();
      });
      serializer.setOnCancelled(event -> {
         LOGGER.debug("SERIALZATION CANCELED");
         Platform.exit();
      });
   }
}
