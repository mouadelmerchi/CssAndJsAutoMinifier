package com.autominifier.ui;

import static javafx.geometry.Orientation.VERTICAL;

import java.io.File;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.control.action.ActionUtils;

import com.autominifier.controller.Controller;
import com.autominifier.model.FilePathWrapper;
import com.autominifier.model.FileType;
import com.autominifier.model.settings.beans.Settings;
import com.autominifier.ui.controls.DialogOptions;
import com.autominifier.ui.controls.Dialogs;
import com.autominifier.ui.controls.GraphicMode;
import com.autominifier.ui.controls.MenuAction;
import com.autominifier.ui.controls.NotificationDialogs;
import com.autominifier.ui.controls.NotificationOptions;
import com.autominifier.ui.controls.SettingsPropertySheet;
import com.autominifier.ui.controls.ToggleGroupValue;
import com.autominifier.util.AnimatedGif;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AutoMinifierUI extends Application {

   private static final Logger LOGGER = LogManager.getLogger(AutoMinifierUI.class);

   private static final String SPLASH_IMAGE  = "img/splash.png";
   private static final String SPLASH_STYLE  = "-fx-padding:5;-fx-background-color:#f2f2f2; "
         + "-fx-border-width:2;-fx-border-color:linear-gradient(to bottom,#199ae6,derive(#199ae6, 50%));";
   private static final int    SPLASH_WIDTH  = 676;
   private static final int    SPLASH_HEIGHT = 227;

   private static final String ASSETS_PATH = "/com/autominifier/%s";

   private static final String BOOTSTRAP_STYLE_SHEET = "css/bootstrap3.css";
   private static final String STYLE_SHEET           = "css/style.css";

   private static final String FILE_MENU_ICON     = "img/menu-24.png";
   private static final String SETTINGS_ICON      = "img/settings-16.png";
   private static final String QUIT_ICON          = "img/power-off-16.png";
   private static final String APP_ICON           = "img/logo-16.png";
   private static final String APP_LOGO           = "img/logo-48.png";
   private static final String HELP_ICON          = "img/help-16.png";
   private static final String INFO_GRAPHIC_S     = "img/info-32.png";
   private static final String INFO_GRAPHIC_M     = "img/info-48.png";
   private static final String AUTO_MODE_OFF_ICON = "img/static-gear-24.png";
   private static final String AUTO_MODE_ON_ICON  = "img/animated-gear-24.gif";

   private static final String TITLE = " CSS And Javascript Auto-minifier";

   private static final String NO_DIR_SELECTION_TEXT    = "No directory selected!";
   private static final String INFO_TOOLTIP_TEXT        = "Check this if you want a recursive search\nin the selected directory";
   private static final String STATIC_GEAR_TOOLTIP_TEXT = "Automatic Mode Off";
   private static final String NOTIFICATION_TITLE       = "Auto Mode On";
   private static final String NOTIFICATION_TEXT        = "The selected directory is now being monitored.\n"
         + "Created/Updated files matching the filter\nwill be compressed automatically.";

   private static final String CONFIRM_DIALOG_TEXT = "Auto Mode is on. Do you want to leave?";
   private static final String ERROR_DIALOG_TEXT   = "Either the selected directory doesn't exist\n"
         + "or a system error has occured.\nPlease try later.";

   private static final String READY_TEXT     = "Ready";
   private static final String AUTO_MODE_TEXT = "Automatic Mode On";

   private Pane        splashLayout;
   private ProgressBar loadProgress;
   private Label       progressText;

   private MenuBar     menuBar;
   private ActionGroup fileActionGroup;
   private ContextMenu contextMenu;
   private MenuAction  settingsAction;
   private MenuAction  exitAction;

   private SettingsPropertySheet propertySheet;

   private NotificationDialogs notifications;

   private Label                      titleLabel;
   private Label                      dirLabel;
   private TextField                  dirText;
   private Button                     btnOpenDirectoryChooser;
   private Label                      fileTypeLabel;
   private ToggleGroupValue<FileType> fileTypeGroup;
   private RadioButton                cssRadio;
   private RadioButton                jsRadio;
   private RadioButton                bothRadio;
   private CheckBox                   recursiveCheckBox;
   private Label                      helpIcon;
   private Label                      listViewLabel;
   private ListView<FilePathWrapper>  listView;
   private ToggleSwitch               autoModeSwitch;
   private Button                     startButton;
   private StatusBar                  statusBar;
   private Label                      staticGear;
   private AnimatedGif                animation;

   private Settings      draftSettings;
   private Settings      finalSettings;
   private Set<Settings> partialSettingsSet;

   private Controller controller;

   @Override
   public void init() {
      this.draftSettings = new Settings();
      this.finalSettings = new Settings();

      ImageView splash = new ImageView(
            new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, SPLASH_IMAGE))));
      loadProgress = new ProgressBar();
      loadProgress.setPrefWidth(SPLASH_WIDTH);
      progressText = new Label("Loading in progress . . .");
      splashLayout = new VBox(5);
      splashLayout.getChildren().addAll(splash, loadProgress, progressText);
      progressText.setAlignment(Pos.CENTER);
      splashLayout.setStyle(SPLASH_STYLE);
      splashLayout.setEffect(new DropShadow());
   }

   private class SplashTask extends Task<Scene> {
      private final Stage mainStage;

      SplashTask(Stage mainStage) {
         this.mainStage = mainStage;
      }

      @Override
      protected Scene call() throws InterruptedException {
         updateMessage("Please wait . . .");
         Scene scene = createAndSetupScene(mainStage);
         updateMessage("Loading complete.");

         return scene;
      }
   }

   @Override
   public void start(Stage initStage) throws Exception {
      Stage mainStage = new Stage(StageStyle.DECORATED);
      final Task<Scene> sceneTask = new SplashTask(mainStage);
      showSplash(initStage, sceneTask, () -> showMainStage(mainStage, sceneTask.valueProperty()));
      new Thread(sceneTask).start();
   }

   private void showMainStage(Stage mainStage, ReadOnlyObjectProperty<Scene> scene) {
      controller = new Controller(this);
      controller.loadSettings();
      mainStage.setTitle(TITLE);
      mainStage.getIcons().add(new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, APP_ICON))));
      mainStage.setResizable(false);
      mainStage.setScene(scene.get());
      mainStage.sizeToScene();
      mainStage.show();
   }

   private void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
      progressText.textProperty().bind(task.messageProperty());
      loadProgress.progressProperty().bind(task.progressProperty());
      task.stateProperty().addListener((observableValue, oldState, newState) -> {
         if (newState == Worker.State.SUCCEEDED) {
            loadProgress.progressProperty().unbind();
            loadProgress.setProgress(1);
            initStage.toFront();
            FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1), splashLayout);
            fadeSplash.setFromValue(1.0);
            fadeSplash.setToValue(0.0);
            fadeSplash.setOnFinished(actionEvent -> initStage.hide());
            fadeSplash.play();

            initCompletionHandler.complete();
         }
      });

      Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
      final Rectangle2D bounds = Screen.getPrimary().getBounds();
      initStage.setScene(splashScene);
      initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
      initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
      initStage.getIcons().add(new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, APP_ICON))));
      initStage.initStyle(StageStyle.TRANSPARENT);
      initStage.setAlwaysOnTop(true);
      initStage.show();
   }

   @FunctionalInterface
   private interface InitCompletionHandler {
      void complete();
   }

   /*
    * ************************* Main Components *************************
    */

   public Settings getDraftSettings() {
      return draftSettings;
   }

   public Settings getFinalSettings() {
      return finalSettings;
   }

   public Set<Settings> getPartialSettings() {
      return partialSettingsSet;
   }

   public void setPartialSettings(Set<Settings> partialSettings) {
      this.partialSettingsSet = partialSettings;
   }

   public TextField getDirText() {
      return dirText;
   }

   public ToggleGroupValue<FileType> getFileTypeGroup() {
      return fileTypeGroup;
   }

   public CheckBox getRecurciveCheckBox() {
      return recursiveCheckBox;
   }

   public ToggleSwitch getAutoModeSwitch() {
      return autoModeSwitch;
   }

   public ListView<FilePathWrapper> getListView() {
      return listView;
   }

   public StatusBar getStatusBar() {
      return statusBar;
   }

   public void switchButtonControls(boolean enableStart, boolean enableAutoSwitch) {
      startButton.setDisable(!enableStart);
      autoModeSwitch.setDisable(!enableAutoSwitch);
   }

   public void enableControls(boolean enable) {
      settingsAction.setDisabled(!enable);
      btnOpenDirectoryChooser.setDisable(!enable);
      cssRadio.setDisable(!enable);
      jsRadio.setDisable(!enable);
      bothRadio.setDisable(!enable);
      recursiveCheckBox.setDisable(!enable);
   }

   /* *** Setting up The Scene *** */
   private Scene createAndSetupScene(Stage primaryStage) {
      initNodes();
      setListeners(primaryStage);
      styleNodes();
      GridPane gridPane = createGridPane();
      Scene scene = new Scene(gridPane);
      scene.getStylesheets().addAll(String.format(ASSETS_PATH, BOOTSTRAP_STYLE_SHEET),
            String.format(ASSETS_PATH, STYLE_SHEET));

      return scene;
   }

   /* *** Creating Menus *** */
   private void createMenus() {
      ImageView settingsImgView = new ImageView(
            new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, SETTINGS_ICON))));
      ImageView quitImgView = new ImageView(
            new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, QUIT_ICON))));
      ImageView fileMenuImgView = new ImageView(
            new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, FILE_MENU_ICON))));

      settingsAction = new MenuAction("Settings", settingsImgView,
            new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
      exitAction = new MenuAction("Exit", quitImgView, new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));

      fileActionGroup = new ActionGroup("", fileMenuImgView, settingsAction, ActionUtils.ACTION_SEPARATOR, exitAction);

      menuBar = ActionUtils.createMenuBar(Arrays.asList(fileActionGroup));
      menuBar.setPrefWidth(568);
      menuBar.setPrefHeight(27);

      contextMenu = ActionUtils.createContextMenu(Arrays.asList(settingsAction));

      propertySheet = new SettingsPropertySheet(draftSettings, 350, 400, new Insets(5, 0, 0, -10));
   }

   /* *** Initializing UI Controls *** */
   private void initNodes() {
      createMenus();

      titleLabel = new Label(TITLE,
            new ImageView(new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, APP_LOGO)))));
      titleLabel.setId("titleLabel");

      // Directory Label
      dirLabel = new Label("Directory:");
      dirLabel.setId("dirLabel");

      // Directory Text field
      dirText = new TextField(NO_DIR_SELECTION_TEXT);
      dirText.setId("dirText");
      dirText.setPrefWidth(340);
      // dirText.setPrefHeight(25);
      dirText.setEditable(false);
      dirText.setFocusTraversable(false);
      dirText.setCursor(Cursor.DEFAULT);

      // Directory Chooser
      btnOpenDirectoryChooser = new Button();
      btnOpenDirectoryChooser.setId("btnOpenDirectoryChooser");
      // btnOpenDirectoryChooser.setPrefHeight(25);
      btnOpenDirectoryChooser.setText("Browse Directory");

      // File Type Label
      fileTypeLabel = new Label("File Type:");
      fileTypeLabel.setId("fileTypeLabel");

      // Toggle group of file type radio buttons
      fileTypeGroup = new ToggleGroupValue<>();
      cssRadio = new RadioButton(FileType.CSS.toString());
      cssRadio.setId("cssRadio");
      fileTypeGroup.add(cssRadio, FileType.CSS);
      jsRadio = new RadioButton(FileType.JS.toString());
      jsRadio.setId("jsRadio");
      fileTypeGroup.add(jsRadio, FileType.JS);
      bothRadio = new RadioButton(FileType.BOTH.toString());
      bothRadio.setId("bothRadio");
      fileTypeGroup.add(bothRadio, FileType.BOTH);
      bothRadio.setSelected(true);

      // Recursive Check box
      recursiveCheckBox = new CheckBox("Recursive");
      recursiveCheckBox.setId("recursiveCheckBox");
      recursiveCheckBox.setIndeterminate(false);
      recursiveCheckBox.setSelected(true);

      // Info Icon
      helpIcon = new Label("",
            new ImageView(new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, HELP_ICON)))));
      helpIcon.setId("helpIcon");

      // Info Icon Tooltip
      final Tooltip recursiveTooltip = new Tooltip();
      hackTooltipStartTiming(recursiveTooltip, 50);
      Image infoIcon = new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, INFO_GRAPHIC_S)));
      recursiveTooltip.setGraphic(new ImageView(infoIcon));
      recursiveTooltip.setText(INFO_TOOLTIP_TEXT);
      Tooltip.install(helpIcon, recursiveTooltip);

      // List View Label
      listViewLabel = new Label("Files Found:");
      listViewLabel.setId("listViewLabel");

      // Automatic Mode Switch
      autoModeSwitch = new ToggleSwitch("Auto Mode");
      autoModeSwitch.setId("autoModeSwitch");
      autoModeSwitch.setSelected(false);

      NotificationOptions options = new NotificationOptions(GraphicMode.CUSTOM,
            new ImageView(new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, INFO_GRAPHIC_M)))),
            Duration.seconds(10), Pos.BASELINE_RIGHT, null, false, true);
      notifications = new NotificationDialogs(NOTIFICATION_TITLE, NOTIFICATION_TEXT, options);

      // Found Files List View
      listView = new ListView<>();
      listView.setId("listView");
      listView.setPrefWidth(545);
      listView.setPrefHeight(250);

      // Start Button
      startButton = new Button("Compress");
      startButton.setId("startButton");
      startButton.setPrefWidth(546);
      startButton.setPrefHeight(25);

      switchButtonControls(false, false);

      // Status Bar
      statusBar = new StatusBar();
      statusBar.setId("statusBar");
      statusBar.setText(READY_TEXT);
      // statusBar.prefWidthProperty().bind(primaryStage.widthProperty());
      statusBar.setPrefWidth(570);
      staticGear = new Label("",
            new ImageView(new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, AUTO_MODE_OFF_ICON)))));
      final Tooltip staticGearTooltip = new Tooltip();
      hackTooltipStartTiming(staticGearTooltip, 30);
      staticGearTooltip.setGraphic(new ImageView(infoIcon));
      staticGearTooltip.setText(STATIC_GEAR_TOOLTIP_TEXT);
      Tooltip.install(staticGear, staticGearTooltip);
      statusBar.getLeftItems().addAll(staticGear, new Separator(VERTICAL));
      animation = new AnimatedGif(
            getClass().getResource(String.format(ASSETS_PATH, AUTO_MODE_ON_ICON)).toExternalForm(), 1000);
      animation.setCycleCount(AnimatedGif.INDEFINITE);
   }

   /* *** Settings up event handlers *** */
   private void setListeners(Stage primaryStage) {
      btnOpenDirectoryChooser.setOnAction(event -> {
         DirectoryChooser directoryChooser = new DirectoryChooser();
         File selectedDirectory = directoryChooser.showDialog(primaryStage);
         if (selectedDirectory != null) {
            dirText.setText(selectedDirectory.getAbsolutePath());
            controller.updatePartialSettingsJsonFile();
         }
      });
      fileTypeGroup.selectedToggleProperty().addListener((ov, oldToggle, newToggle) -> {
         if (!dirText.getText().equals(NO_DIR_SELECTION_TEXT)) {
            controller.startListingFiles();
         }
      });
      recursiveCheckBox.setOnAction(event -> {
         if (!dirText.getText().equals(NO_DIR_SELECTION_TEXT)) {
            controller.startListingFiles();
         }
      });
      startButton.setOnAction(evt -> {
         controller.startMinifying();
      });
      autoModeSwitch.selectedProperty().addListener(event -> {
         if (autoModeSwitch.isSelected()) {
            listView.getItems().clear();
            animation.play();
            statusBar.getLeftItems().set(0, animation.getView());
            if (!statusBar.textProperty().isBound()) {
               statusBar.setText(AUTO_MODE_TEXT);
            }
            listView.getStyleClass().add("list-view-auto-mode");
            notifications.showNotification(GraphicMode.CUSTOM);
            try {
               controller.startAutoMode();
            } catch (UncheckedIOException e) {
               showErrorDialog(primaryStage);
            }
         } else {
            controller.stopAutoMode();
            controller.startListingFiles();
            animation.pause();
            statusBar.getLeftItems().set(0, staticGear);
            if (!statusBar.textProperty().isBound()) {
               statusBar.setText(READY_TEXT);
            }
            listView.getStyleClass().remove("list-view-auto-mode");
         }
      });
      dirText.focusedProperty().addListener((observable, oldValue, newValue) -> {
         dirLabel.requestFocus(); // Delegate the focus to the label
      });
      listView.focusedProperty().addListener((observable, oldValue, newValue) -> {
         listViewLabel.requestFocus(); // Delegate the focus to the label
      });
      settingsAction.setHandler(event -> {
         DialogOptions opts = new DialogOptions(Alert.AlertType.NONE, null, null, false, Modality.APPLICATION_MODAL,
               StageStyle.DECORATED, primaryStage);
         ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
         ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
         ButtonType restoreDefaultsButton = new ButtonType("Restore Defaults", ButtonBar.ButtonData.OTHER);
         Alert dialog = Dialogs.createCustomDialog("Settings", propertySheet, opts,
               new ButtonType[] { cancelButton, restoreDefaultsButton, saveButton }, saveButton);

         DialogPane dialogPane = dialog.getDialogPane();
         ((Button) dialogPane.lookupButton(restoreDefaultsButton)).getStyleClass().add("info");
         ((Button) dialogPane.lookupButton(saveButton)).getStyleClass().add("success");
         ((Button) dialogPane.lookupButton(cancelButton)).getStyleClass().add("warning");

         final Button rstBtn = (Button) dialogPane.lookupButton(restoreDefaultsButton);
         rstBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            controller.restoreSettings();
            ev.consume();
         });

         dialog.showAndWait().ifPresent(result -> {
            if (result == saveButton) {
               controller.saveSettings();
               if (!dirText.getText().equals(NO_DIR_SELECTION_TEXT)) {
                  controller.updatePartialSettingsJsonFile();
               }
            } else if (result == cancelButton) {
               controller.cancelSettings();
            }
         });
      });
      exitAction.setHandler(actionEvent -> {
         if (autoModeSwitch.isSelected()) {
            showConfirmDialog(primaryStage);
         } else {
            controller.persistSettingsAndExit();
         }
      });
      primaryStage.setOnCloseRequest(event -> {
         if (autoModeSwitch.isSelected()) {
            event.consume();
            showConfirmDialog(primaryStage);
         } else {
            controller.persistSettingsAndExit();
         }
      });
      primaryStage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
         if (event.getButton() == MouseButton.SECONDARY || event.isControlDown()) {
            contextMenu.show(primaryStage, event.getScreenX(), event.getScreenY());
         } else {
            contextMenu.hide();
         }
      });
   }

   private void showErrorDialog(Stage primaryStage) {
      DialogOptions opts = new DialogOptions(AlertType.ERROR, null, null, false, Modality.APPLICATION_MODAL,
            StageStyle.DECORATED, primaryStage);
      Alert dialog = Dialogs.createSimpleDialog(TITLE, ERROR_DIALOG_TEXT, opts);

      DialogPane dialogPane = dialog.getDialogPane();
      ((Button) dialogPane.lookupButton(ButtonType.OK)).getStyleClass().add("primary");

      dialog.show();
   }

   private void showConfirmDialog(Stage primaryStage) {
      DialogOptions opts = new DialogOptions(AlertType.CONFIRMATION, null, null, false, Modality.APPLICATION_MODAL,
            StageStyle.DECORATED, primaryStage);
      Alert dialog = Dialogs.createSimpleDialog(TITLE, CONFIRM_DIALOG_TEXT, opts);

      DialogPane dialogPane = dialog.getDialogPane();
      ((Button) dialogPane.lookupButton(ButtonType.OK)).getStyleClass().add("primary");
      ((Button) dialogPane.lookupButton(ButtonType.CANCEL)).getStyleClass().add("default");

      dialog.showAndWait().ifPresent(result -> {
         if (result == ButtonType.OK) {
            controller.stopAutoMode();
            controller.persistSettingsAndExit();
         }
      });
   }

   /* *** Styling nodes *** */
   private void styleNodes() {
      titleLabel.getStyleClass().add("title-label");
      dirLabel.getStyleClass().add("cntrl-label");
      fileTypeLabel.getStyleClass().add("cntrl-label");
      listViewLabel.getStyleClass().add("cntrl-label");
      autoModeSwitch.getStyleClass().add("cntrl-label");
      btnOpenDirectoryChooser.getStyleClass().add("primary");
      startButton.getStyleClass().add("success");
   }

   /* *** Creating a Grid Pane *** */
   private GridPane createGridPane() {
      GridPane gridPane = new GridPane();

      // Setting size for the pane
      gridPane.setMinSize(400, 150);

      // Setting the padding
      gridPane.setPadding(new Insets(10, 0, 0, 0));

      // Setting the vertical and horizontal gaps between the columns
      gridPane.setVgap(12);
      gridPane.setHgap(5);

      // Setting the Grid alignment
      gridPane.setAlignment(Pos.CENTER_LEFT);

      // Arranging all of the nodes in the grid
      HBox menuBarHBox = new HBox();
      menuBarHBox.setPadding(new Insets(-9, 0, 10, 0));
      menuBarHBox.getChildren().add(menuBar);
      gridPane.add(menuBarHBox, 0, 0);
      GridPane.setColumnSpan(menuBarHBox, 3);

      HBox titleLabelHBox = new HBox();
      titleLabelHBox.setAlignment(Pos.CENTER);
      titleLabelHBox.setPadding(new Insets(5, 0, 15, 0));
      titleLabelHBox.getChildren().add(titleLabel);
      gridPane.add(titleLabelHBox, 0, 1);
      GridPane.setColumnSpan(titleLabelHBox, 3);

      HBox dirLabelHBox = new HBox();
      dirLabelHBox.setAlignment(Pos.CENTER_LEFT);
      dirLabelHBox.setPadding(new Insets(-2, 0, 0, 10));
      dirLabelHBox.getChildren().add(dirLabel);
      gridPane.add(dirLabelHBox, 0, 2);
      gridPane.add(dirText, 1, 2);
      HBox btnOpenDirectoryChooserHBox = new HBox();
      btnOpenDirectoryChooserHBox.setPadding(new Insets(1, 10, 0, 0));
      btnOpenDirectoryChooserHBox.getChildren().add(btnOpenDirectoryChooser);
      gridPane.add(btnOpenDirectoryChooserHBox, 2, 2);

      HBox fileTypeLabelHBox = new HBox();
      fileTypeLabelHBox.setAlignment(Pos.TOP_LEFT);
      fileTypeLabelHBox.setPadding(new Insets(0, 0, 0, 10));
      fileTypeLabelHBox.getChildren().add(fileTypeLabel);
      gridPane.add(fileTypeLabelHBox, 0, 3);
      HBox radiosHBox = new HBox(20);
      radiosHBox.getChildren().addAll(cssRadio, jsRadio, bothRadio);
      gridPane.add(radiosHBox, 1, 3);
      HBox recursiveCheckBoxHBox = new HBox(5);
      recursiveCheckBoxHBox.setAlignment(Pos.TOP_LEFT);
      recursiveCheckBoxHBox.setPadding(new Insets(-2, 0, 0, 0));
      helpIcon.setPadding(new Insets(4, 0, 0, 0));
      recursiveCheckBoxHBox.getChildren().addAll(recursiveCheckBox, helpIcon);
      gridPane.add(recursiveCheckBoxHBox, 2, 3);

      HBox listViewLabelHBox = new HBox();
      listViewLabelHBox.setAlignment(Pos.BOTTOM_LEFT);
      listViewLabelHBox.setPadding(new Insets(6, 0, 0, 10));
      listViewLabelHBox.getChildren().add(listViewLabel);
      gridPane.add(listViewLabelHBox, 0, 4);
      HBox autoModeSwitchHBox = new HBox();
      autoModeSwitchHBox.setAlignment(Pos.BOTTOM_LEFT);
      autoModeSwitchHBox.setPadding(new Insets(6, 10, 0, 0));
      autoModeSwitchHBox.getChildren().add(autoModeSwitch);
      gridPane.add(autoModeSwitchHBox, 2, 4);

      HBox listViewHBox = new HBox();
      listViewHBox.setAlignment(Pos.CENTER);
      listViewHBox.setPadding(new Insets(0, 10, 0, 10));
      listViewHBox.getChildren().add(listView);
      gridPane.add(listViewHBox, 0, 5);
      GridPane.setColumnSpan(listViewHBox, 3);

      HBox buttonsHBox = new HBox();
      buttonsHBox.setPadding(new Insets(10, 10, 0, 10));
      buttonsHBox.getChildren().addAll(startButton);
      gridPane.add(buttonsHBox, 0, 6);
      GridPane.setColumnSpan(buttonsHBox, 3);

      HBox statusBarHBox = new HBox();
      statusBarHBox.setPadding(new Insets(10, 0, 0, 0));
      statusBarHBox.getChildren().add(statusBar);
      gridPane.add(statusBarHBox, 0, 7);
      GridPane.setColumnSpan(statusBarHBox, 3);

      // Setting the background color
      gridPane.getStyleClass().add("grid-pane");

      return gridPane;
   }

   private static void hackTooltipStartTiming(Tooltip tooltip, int durationMs) {
      try {
         Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
         fieldBehavior.setAccessible(true);
         Object objBehavior = fieldBehavior.get(tooltip);

         Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
         fieldTimer.setAccessible(true);
         Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

         objTimer.getKeyFrames().clear();
         objTimer.getKeyFrames().add(new KeyFrame(new Duration(durationMs)));
      } catch (Exception e) {
         LOGGER.error(String.format("Oops! Something came up. Cause: %s", e));
      }
   }
}