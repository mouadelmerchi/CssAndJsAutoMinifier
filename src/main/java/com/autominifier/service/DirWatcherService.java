package com.autominifier.service;

import static com.autominifier.model.BufferWrapper.PROCESSED_FILE_QUEUE;
import static com.autominifier.model.settings.InternalParameters.MAXIMUM_MILLIS_LIMIT_BEFORE_GIVING_UP;
import static com.autominifier.util.AutoMinifierUtils.verifyFile;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autominifier.model.FilePathWrapper;
import com.autominifier.model.FileType;
import com.autominifier.model.settings.InternalParameters;
import com.autominifier.model.settings.beans.Settings;

public class DirWatcherService implements Runnable {

   private static final Logger LOGGER = LogManager.getLogger(DirWatcherService.class);

   private ExecutorService executor;

   /*
    * Use this to prevent duplicates from being added when multiple ENTRY_MODIFY
    * events on the same file arrive in quick succession.
    */
   private final AtomicInteger tracker;

   /*
    * Keep a map that will be used to resolve WatchKeys to the parent directory
    * so that we can resolve the full path to an event file.
    */
   private final Map<WatchKey, Path> keys;

   private final WatchService watcher;

   private final Set<Settings> setOfSettings;
   private final Path          root;
   private final boolean       recursive;
   private final FileType      fileType;
   private boolean             trace = false;

   /**
    * Creates a WatchService
    */
   public DirWatcherService(Set<Settings> setOfSettings, String root, FileType fileType, boolean recursive)
         throws IOException {
      Path path = Paths.get(root);
      // be sure that root is an existing directory
      if (Files.notExists(path) || !Files.isDirectory(path, NOFOLLOW_LINKS)) {
         String msg = String.format("'%s' doesn't exist or is not a directory", path.getFileName().toString());
         throw new NotDirectoryException(msg);
      }

      this.executor = Executors.newSingleThreadExecutor();
      this.tracker = new AtomicInteger(0);

      this.setOfSettings = setOfSettings;
      this.root = path;
      this.fileType = fileType;
      this.watcher = FileSystems.getDefault().newWatchService();
      this.keys = new HashMap<>();
      this.recursive = recursive;

      // enable trace after initial registration
      this.trace = true;
   }

   /**
    * Process all events for keys queued to the watcher
    */
   @Override
   public void run() {
      try {
         processExistingFilesAndDirs(root);
      } catch (IOException e) {
         LOGGER.error("Oops! Something came up. Cause: ", e);
         return;
      }

      for (;;) {
         // wait for key to be signaled
         WatchKey key;
         try {
            key = watcher.take();
         } catch (InterruptedException e) {
            LOGGER.error("Oops! Something came up. Cause: ", e);
            return;
         }

         Path dir = keys.get(key);
         if (dir == null) {
            LOGGER.error("WatchKey not recognized!!");
            continue;
         }

         for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            // handle OVERFLOW event
            if (kind == OVERFLOW) {
               continue;
            }

            // context for directory entry event is the file name of entry
            WatchEvent<Path> ev = cast(event);
            Path name = ev.context();
            Path child = dir.resolve(name);

            if (kind == ENTRY_MODIFY) {
               if (ev.count() > 1) {
                  tracker.set(2);
               }
               if (ev.count() == 1) {
                  tracker.incrementAndGet();
               }
               if (tracker.get() == 2) {
                  tracker.set(0);
                  LOGGER.debug("ENTRY_MODIFY event triggered. Process file: ", child);
                  processFile(setOfSettings, ev.kind(), new FilePathWrapper(child.toString()));
               }
            } else if (kind == ENTRY_CREATE) {
               LOGGER.debug("ENTRY_CREATE event triggered. Process file: ", child);
               // if directory is created, and watching recursively, then
               // register it and its sub-directories
               if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                  if (recursive) {
                     try {
                        registerAll(child);
                     } catch (IOException e) {
                        LOGGER.error("Oops! Something came up. Cause: ", e);
                     }
                  }
               } else {
                  processFile(setOfSettings, ev.kind(), new FilePathWrapper(child.toString()));
               }
            } else if (kind == ENTRY_DELETE) {
               // Process ENTRY_DELETE Event
            }
         }

         // reset key and remove from set if directory no longer accessible
         boolean valid = key.reset();
         if (!valid) {
            keys.remove(key);

            // all directories are inaccessible
            if (keys.isEmpty()) {
               break;
            }
         }
      }
   }

   /**
    * Starts watching the root directory
    */
   public void start() {
      executor.submit(this);
   }

   /**
    * Stops directory watcher thread
    */
   public void stop() {
      try {
         LOGGER.debug("Attempt to shutdown DirWatcher Executor");
         executor.shutdown();
         executor.awaitTermination(MAXIMUM_MILLIS_LIMIT_BEFORE_GIVING_UP, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
         LOGGER.error("Tasks interrupted");
      } finally {
         try {
            watcher.close();
         } catch (IOException ioe) {
            LOGGER.error("Failed to close Watcher Service. Cause: ", ioe);
         }
         if (!executor.isTerminated()) {
            LOGGER.error("Cancel non-finished tasks");
         }
         executor.shutdownNow();
         LOGGER.debug("Shutdown finished");
      }
   }

   /**
    * Utility method used to bypass the type safety warning produced when
    * converting an event.
    * 
    * @param event
    *           - WatchEvent to convert
    * @return - Cast WatchEvent object
    */
   @SuppressWarnings("unchecked")
   private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
      return (WatchEvent<T>) event;
   }

   /**
    * Register the given directory.
    * 
    * @param dir
    *           - A directory to register
    * @throws IOException
    *            - If an I/O error occurs
    */
   private void register(Path dir) throws IOException {
      WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY); // ENTRY_DELETE
      if (trace) {
         Path prev = keys.get(key);
         if (prev == null) {
            LOGGER.debug("Register: ", dir);
         } else {
            if (!dir.equals(prev)) {
               LOGGER.debug(String.format("Update: %s -> %s", prev, dir));
            }
         }
      }
      keys.put(key, dir);
   }

   /**
    * Register the provided directory, and all its sub-directories.
    * 
    * @param start
    *           - Root directory probably containing files to process
    * @throws IOException
    *            - If an I/O error occurs
    */
   private void registerAll(final Path start) throws IOException {
      LOGGER.debug(String.format("Scanning %s ...", start));
      // register directory and sub-directories
      Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
         @Override
         public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            register(dir);
            return FileVisitResult.CONTINUE;
         }
      });
      LOGGER.debug("Scanning done.");
   }

   /**
    * Process existing css and js files when the thread first starts.
    * 
    * @param dir
    *           - Root directory probably containing files to process
    * @throws IOException
    *            - If an I/O error occurs
    */
   private void processExistingFilesAndDirs(Path dir) throws IOException {
      register(dir);
      try (DirectoryStream<Path> children = Files.newDirectoryStream(dir)) {
         for (Path child : children) {
            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
               if (recursive) {
                  processExistingFilesAndDirs(child);
               }
            } else {
               processFile(setOfSettings, null, new FilePathWrapper(child.toString()));
            }
         }
      } catch (DirectoryIteratorException e) {
         throw e.getCause();
      }
   }

   /**
    * Adds the files to the blocking queue for compressing. If a directory is
    * encountered, the method register it and all of its sub-directories when
    * recursive mode is on.
    * 
    * @param kind
    *           - Kind of the event
    * @param filePath
    *           - File path to process
    */
   private synchronized void processFile(Set<Settings> setOfSettings, WatchEvent.Kind<Path> kind,
         FilePathWrapper fileNameWrapper) {
      if (verifyFile(setOfSettings, fileNameWrapper, fileType)) {
         LOGGER.debug(String.format("%s: %s", kind != null ? kind.name() : "<no kind>", fileNameWrapper.getFilePath()));
         offerFileForMinifying(fileNameWrapper);
      }
   }

   /**
    * Put the provided file in the queue for compression.
    * 
    * @param path
    *           - Path to the file to queue
    */
   private void offerFileForMinifying(FilePathWrapper fileNameWrapper) {
      try {
         PROCESSED_FILE_QUEUE.put(fileNameWrapper);
      } catch (InterruptedException ex) {
         LOGGER.debug("Wait a moment before retry ...");
         try {
            Thread.sleep(InternalParameters.MILLIS_TO_WAIT_BEFORE_RETRY);
         } catch (InterruptedException e) {
            LOGGER.error("Oops! Something came up. Cause: ", e);
         }
      }
   }
}