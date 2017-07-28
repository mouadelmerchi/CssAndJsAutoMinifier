package com.autominifier.ui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreenUI extends Application {

   private static final String ASSETS_PATH = "/com/autominifier/%s";

   private static final String APP_ICON = "img/logo-16.png";

   private static final String SPLASH_IMAGE  = "img/splash.png";
   private static final String SPLASH_STYLE  = "-fx-padding:5;-fx-background-color:#f2f2f2; "
         + "-fx-border-width:2;-fx-border-color:linear-gradient(to bottom,#199ae6,derive(#199ae6, 50%));";
   private static final int    SPLASH_WIDTH  = 676;
   private static final int    SPLASH_HEIGHT = 227;

   private Pane        splashLayout;
   private ProgressBar loadProgress;
   private Label       progressText;

   private class SplashTask extends Task<Scene> {
      private final AutoMinifierUI mainView;
      private final Stage          mainStage;

      SplashTask(AutoMinifierUI mainView, Stage mainStage) {
         this.mainView = mainView;
         this.mainStage = mainStage;
      }

      @Override
      protected Scene call() throws InterruptedException {
         updateMessage("Please wait . . .");
         Scene scene = mainView.createAndSetupScene(mainStage);
         updateMessage("Loading complete.");

         return scene;
      }
   }

   @Override
   public void init() {
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

   @Override
   public void start(Stage splashStage) throws Exception {
      final AutoMinifierUI mainView = new AutoMinifierUI();
      final Stage mainStage = new Stage(StageStyle.DECORATED);
      final Task<Scene> sceneTask = new SplashTask(mainView, mainStage);
      showSplash(splashStage, sceneTask, () -> mainView.start(mainStage, sceneTask.valueProperty()));
      new Thread(sceneTask).start();
   }

   private void showSplash(final Stage splashStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
      progressText.textProperty().bind(task.messageProperty());
      loadProgress.progressProperty().bind(task.progressProperty());
      task.stateProperty().addListener((observableValue, oldState, newState) -> {
         if (newState == Worker.State.SUCCEEDED) {
            loadProgress.progressProperty().unbind();
            loadProgress.setProgress(1);
            splashStage.toFront();
            FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1), splashLayout);
            fadeSplash.setFromValue(1.0);
            fadeSplash.setToValue(0.0);
            fadeSplash.setOnFinished(actionEvent -> splashStage.hide());
            fadeSplash.play();

            initCompletionHandler.complete();
         }
      });

      Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
      final Rectangle2D bounds = Screen.getPrimary().getBounds();
      splashStage.setScene(splashScene);
      splashStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
      splashStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
      splashStage.getIcons().add(new Image(getClass().getResourceAsStream(String.format(ASSETS_PATH, APP_ICON))));
      splashStage.initStyle(StageStyle.TRANSPARENT);
      splashStage.setAlwaysOnTop(true);
      splashStage.show();
   }

   @FunctionalInterface
   private interface InitCompletionHandler {
      void complete();
   }
}
