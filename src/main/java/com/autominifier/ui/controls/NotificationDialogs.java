package com.autominifier.ui.controls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import javafx.scene.Node;

public class NotificationDialogs {

   private static final Logger LOGGER = LogManager.getLogger(NotificationDialogs.class);

   private Notifications notificationBuilder;

   public NotificationDialogs(String title, String text, NotificationOptions options) {
      Node graphic = null;
      switch (options.getGraphicMode()) {
         default:
         case NO_GRAPHIC:
         case WARNING:
         case INFO:
         case CONFIRM:
         case ERROR:
            break;
         case CUSTOM:
            graphic = options.getCustomGraphic();
            break;
      }

      notificationBuilder = Notifications.create().title(title).text(text).graphic(graphic)
            .hideAfter(options.getHideAfter()).position(options.getPos()).onAction(event -> {
               LOGGER.debug("Notification clicked on!");
            });

      if (options.getOwner() != null) {
         notificationBuilder.owner(options.getOwner());
      }

      if (options.isHideCloseButton()) {
         notificationBuilder.hideCloseButton();
      }

      if (options.isDarkStyle()) {
         notificationBuilder.darkStyle();
      }
   }

   public void setTitle(String title) {
      notificationBuilder.title(title);
   }
   
   public void setText(String text) {
      notificationBuilder.text(text);
   }
   
   public void showNotification(GraphicMode mode) {
      switch (mode) {
         case WARNING:
            notificationBuilder.showWarning();
            break;
         case INFO:
            notificationBuilder.showInformation();
            break;
         case CONFIRM:
            notificationBuilder.showConfirm();
            break;
         case ERROR:
            notificationBuilder.showError();
            break;
         default:
            notificationBuilder.show();
      }
   }
}
