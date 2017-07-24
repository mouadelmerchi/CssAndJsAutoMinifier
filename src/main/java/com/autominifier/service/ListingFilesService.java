package com.autominifier.service;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import com.autominifier.model.FilePathWrapper;
import com.autominifier.model.FileType;
import com.autominifier.model.settings.beans.Settings;
import com.autominifier.util.AutoMinifierUtils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ListingFilesService extends Service<ObservableList<FilePathWrapper>> {

   private StringProperty statusBarText;
   private DoubleProperty progress;
   private Set<Settings>  setOfSettings;
   private File           root;
   private FileType       fileType;
   private boolean        recursive;

   public ListingFilesService(StringProperty statusBarText, DoubleProperty progress, Set<Settings> setOfSettings,
         String root, FileType fileType, boolean recursive) {
      super();
      this.statusBarText = statusBarText;
      this.progress = progress;
      this.setOfSettings = setOfSettings;
      this.root = new File(root);
      this.fileType = fileType;
      this.recursive = recursive;
   }

   @Override
   protected Task<ObservableList<FilePathWrapper>> createTask() {
      Task<ObservableList<FilePathWrapper>> task = new Task<ObservableList<FilePathWrapper>>() {
         @Override
         protected ObservableList<FilePathWrapper> call() throws Exception {
            updateMessage("Searching...");

            Collection<FilePathWrapper> cssAndJsFiles = AutoMinifierUtils.listFiles(setOfSettings, root, fileType,
                  recursive);
            int size = cssAndJsFiles.size();

            updateMessage(
                  String.format("%d %s files found", size, (fileType == FileType.BOTH ? "css and js" : fileType)));
            updateProgress(0, 0);
            done();

            return FXCollections.observableArrayList(cssAndJsFiles);
         }
      };
      statusBarText.bind(task.messageProperty());
      progress.bind(task.progressProperty());
      task.setOnSucceeded(event -> {
         statusBarText.unbind();
         progress.unbind();
      });

      return task;
   }
}