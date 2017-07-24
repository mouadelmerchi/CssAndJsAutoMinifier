package com.autominifier.service;

import static com.autominifier.model.BufferWrapper.PROCESSED_FILE_QUEUE;

import java.io.IOException;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autominifier.minifier.Minifier;
import com.autominifier.model.FilePathWrapper;

import javafx.concurrent.Task;

public class MinifierTask extends Task<Void> {

   private static final Logger LOGGER = LogManager.getLogger(MinifierTask.class);

   private final Minifier                    minifier;
   private final Collection<FilePathWrapper> filesToCompress;

   public MinifierTask(Minifier minifier, Collection<FilePathWrapper> filesToCompress) {
      this.minifier = minifier;
      this.filesToCompress = filesToCompress;
   }

   public MinifierTask(Minifier minifier) throws IOException {
      this(minifier, null);
   }

   @Override
   protected Void call() throws Exception {
      if (filesToCompress != null) { // Manual compression
         updateMessage("Compressing files...");
         int i = 1;
         int size = filesToCompress.size();
         for (FilePathWrapper fileToCompress : filesToCompress) {
            if (isCancelled()) {
               updateMessage("Compressing Tasks cancelled");
               break;
            }
            LOGGER.debug(String.format("===> [Manual Mode] Compressing file: %s", fileToCompress));
            updateMessage(String.format("Compressing file '%s'", fileToCompress));
            updateProgress(i, size);
            ++i;

            minifier.compress(fileToCompress);
         }
         updateMessage("Finished");
         updateProgress(0, 0);
      } else { // Auto Mode is on
         updateMessage("Auto Mode is on...");
         FilePathWrapper fileToCompress;
         try {
            // Blocks until an element is available
            fileToCompress = PROCESSED_FILE_QUEUE.take();
            LOGGER.debug(String.format("===> [Auto Mode] Compressing file: %s", fileToCompress));
            updateMessage(String.format("Compressing file '%s'", fileToCompress));

            minifier.compress(fileToCompress);
         } catch (InterruptedException e) {
            if (isCancelled()) {
               updateMessage("Compressing Tasks cancelled");
            }
         }
      }
      return null;
   }
}
