package com.autominifier.service;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import com.autominifier.model.FileType;
import com.autominifier.model.settings.beans.Settings;
import com.autominifier.util.AutoMinifierUtils;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class OldMinifiedFilesDeleterService extends Service<Void> {

   private Set<Settings> setOfSettings;
   private File          root;
   private FileType      fileType;
   private boolean       recursive;

   public OldMinifiedFilesDeleterService(Set<Settings> setOfSettings, String root, FileType fileType,
         boolean recursive) {
      this.setOfSettings = setOfSettings;
      this.root = new File(root);
      this.fileType = fileType;
      this.recursive = recursive;
   }

   @Override
   protected Task<Void> createTask() {
      return new Task<Void>() {
         @Override
         protected Void call() throws Exception {
            try {
               String pattern = AutoMinifierUtils.createExtensionsRegEx(setOfSettings, fileType);
               RegexFileFilter fileFilter = new RegexFileFilter(pattern);
               Collection<File> files = FileUtils.listFiles(root, fileFilter,
                     recursive ? DirectoryFileFilter.DIRECTORY : FalseFileFilter.FALSE);

               if (!files.isEmpty()) {
                  files.parallelStream().forEach(FileUtils::deleteQuietly);
               }
            } catch (Exception e) {
               e.printStackTrace();
            }

            return null;
         }
      };
   }
}
