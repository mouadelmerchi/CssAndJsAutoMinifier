package com.autominifier.service;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TerminatorService extends Service<Void> {

   private final DirWatcherService   watcher;
   private final AutoMinifierService autoMinService;

   public TerminatorService(DirWatcherService watcher, AutoMinifierService autoMinService) {
      this.watcher = watcher;
      this.autoMinService = autoMinService;
   }

   @Override
   protected Task<Void> createTask() {
      return new Task<Void>() {
         @Override
         protected Void call() throws Exception {
            watcher.stop();
            Platform.runLater(() -> {
               autoMinService.cancel();
            });
            
            return null;
         }
      };
   }

}
