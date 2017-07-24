package com.autominifier.model;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

public class FilePathWrapper {

   private static final Logger LOGGER = LogManager.getLogger(FilePathWrapper.class);

   private static Tika tika;

   private static String basePath;
   private String        filePath;
   private String        contentType;

   static {
      tika = new Tika();
   }

   public FilePathWrapper() {
   }

   /**
    * Creates a new FilePathWrapper object. The file's content type
    * is detected under the hood.
    * 
    * @param filePath - File's path to wrap
    */
   public FilePathWrapper(String filePath) {
      this.filePath = filePath;
      this.contentType = detectContentType();
   }

   private String detectContentType() {
      String contentType;
      try {
         contentType = tika.detect(new File(filePath));
      } catch (IOException e) {
         LOGGER.error(String.format("Oops! Something came up. Cause: %s", e));
         contentType = null;
      }
      return contentType;
   }

   public static String getBasePath() {
      return basePath;
   }

   public static void setBasePath(String basePath) {
      FilePathWrapper.basePath = basePath;
   }

   public String getFilePath() {
      return filePath;
   }

   public void setFilePath(String filePath) {
      this.filePath = filePath;
   }

   public String getContentType() {
      return contentType;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
      result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      FilePathWrapper other = (FilePathWrapper) obj;
      if (contentType == null) {
         if (other.contentType != null)
            return false;
      } else if (!contentType.equals(other.contentType))
         return false;
      if (filePath == null) {
         if (other.filePath != null)
            return false;
      } else if (!filePath.equals(other.filePath))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return StringUtils.remove(filePath.toString(), (basePath != null) ? basePath.toString() : "")
            .substring((basePath != null) ? 1 : 0);
   }
}
