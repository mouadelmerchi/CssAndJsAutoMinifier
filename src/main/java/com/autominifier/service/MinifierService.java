package com.autominifier.service;

import java.util.Collection;

import com.autominifier.minifier.YuiMinifier;
import com.autominifier.model.FilePathWrapper;
import com.autominifier.model.settings.beans.Settings;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class MinifierService extends Service<Void> {

   private StringProperty statusBarText;
   private DoubleProperty progress;
   private Settings       settings;
   private Collection<FilePathWrapper> filesToCompress;

   public MinifierService(StringProperty statusBarText, DoubleProperty progress, Settings settings, Collection<FilePathWrapper> filesToCompress) {
      super();
      this.statusBarText = statusBarText;
      this.progress = progress;
      this.settings = settings;
      this.filesToCompress = filesToCompress;
   }

   @Override
   protected Task<Void> createTask() {
      MinifierTask task = new MinifierTask(new YuiMinifier(settings), filesToCompress);
      statusBarText.bind(task.messageProperty());
      progress.bind(task.progressProperty());
      task.setOnSucceeded(event -> {
         statusBarText.unbind();
         progress.unbind();
      });

      return task;
   }
}
