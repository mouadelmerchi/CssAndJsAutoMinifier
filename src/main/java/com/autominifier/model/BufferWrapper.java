package com.autominifier.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.autominifier.model.settings.InternalParameters;

public final class BufferWrapper {

   /*
    * PROCESSED_FILE_QUEUE **** will be used externally by other threads to
    * retrieve queued files.
    */
   public static final BlockingQueue<FilePathWrapper> PROCESSED_FILE_QUEUE;

   static {
      PROCESSED_FILE_QUEUE = new ArrayBlockingQueue<>(InternalParameters.FILE_QUEUE_SIZE, false);
   }

   private BufferWrapper() {
   }
}
