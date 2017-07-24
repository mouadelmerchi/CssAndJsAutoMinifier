package com.autominifier.service;

import com.autominifier.minifier.YuiMinifier;
import com.autominifier.model.settings.beans.Settings;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class AutoMinifierService extends ScheduledService<Void> {

   private StringProperty statusBarText;
   private DoubleProperty progress;
   private Settings       settings;

   public AutoMinifierService(StringProperty statusBarText, DoubleProperty progress, Settings settings) {
      super();
      this.statusBarText = statusBarText;
      this.progress = progress;
      this.settings = settings;
   }

   @Override
   protected Task<Void> createTask() {
      MinifierTask task = new MinifierTask(new YuiMinifier(settings), null);
      statusBarText.bind(task.messageProperty());
      progress.bind(task.progressProperty());
      task.setOnCancelled(event -> {
         statusBarText.unbind();
         progress.unbind();
      });

      return task;
   }
}
