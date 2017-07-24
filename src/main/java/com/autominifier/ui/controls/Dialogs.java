package com.autominifier.ui.controls;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Dialogs {

   public static Alert createSimpleDialog(String title, String text, DialogOptions options) {
      Alert dialog = new Alert(options.getDialogType());
      dialog.setTitle(title);
      dialog.getDialogPane().setContentText(text);
      if (StringUtils.isNotBlank(options.getMastHead())) {
         dialog.getDialogPane().setHeaderText(options.getMastHead());
      }
      if (StringUtils.isNotBlank(options.getGraphicFileName())) {
         dialog.getDialogPane()
               .setGraphic(new ImageView(new Image(Dialogs.class.getResourceAsStream(options.getGraphicFileName()))));
      }
      dialog.setResizable(options.isResizable());
      dialog.initModality(options.getModality());
      dialog.initStyle(options.getStageStyle());
      dialog.initOwner(options.getOwner());

      return dialog;
   }

   public static Alert createCustomDialog(String title, Node content, DialogOptions options, ButtonType[] buttons,
         ButtonType okButton) {
      Alert dialog = new Alert(Alert.AlertType.NONE);
      dialog.setTitle(title);
      dialog.getDialogPane().setContent(content);
      dialog.getButtonTypes().addAll(buttons);

      dialog.setResizable(options.isResizable());
      dialog.initModality(options.getModality());
      dialog.initStyle(options.getStageStyle());
      dialog.initOwner(options.getOwner());

      return dialog;
   }
}
