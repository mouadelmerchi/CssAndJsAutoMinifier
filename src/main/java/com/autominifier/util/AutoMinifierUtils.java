package com.autominifier.util;

import static com.autominifier.model.settings.InternalParameters.SETTINGS_DIR;
import static com.autominifier.model.settings.InternalParameters.SETTINGS_HISTORY_DIR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autominifier.model.FilePathWrapper;
import com.autominifier.model.FileType;
import com.autominifier.model.settings.InternalParameters;
import com.autominifier.model.settings.beans.MinificationSeparatorChar;
import com.autominifier.model.settings.beans.Settings;

public final class AutoMinifierUtils {

   private static final Logger LOGGER = LogManager.getLogger(AutoMinifierUtils.class);

   // private static final String EXCLUDE_FILE_PATTERN = "^.*(?<!\\.%s)\\.%s$";
   // private static final String DIR_PATTERN = "[^(/\\.)]*";

   private AutoMinifierUtils() {
   }

   public static Collection<FilePathWrapper> listFiles(Set<Settings> setOfSettings, File root, FileType fileType,
         boolean recursive) {
      FilePathWrapper.setBasePath(root.toString());
      Collection<FilePathWrapper> allCssAndJsFiles = FileUtils
            .listFiles(root, new String[] { FileType.CSS.toString(), FileType.JS.toString() }, recursive)
            .parallelStream().map(f -> new FilePathWrapper(f.toString()))
            .filter(fnw -> verifyFile(setOfSettings, fnw, fileType)).collect(Collectors.toList());
      return allCssAndJsFiles;
   }

   /**
    * Verifies whether 'path' is is a valid css or js.
    * 
    * @param fileNameWrapper
    * @param fileType
    * @return boolean - True if it's a valid css or js file
    */
   public static boolean verifyFile(Set<Settings> setOfSettings, FilePathWrapper fileNameWrapper, FileType fileType) {
      boolean processFile = true;
      File file = new File(fileNameWrapper.getFilePath());
      if (file.isFile()) {
         String fn = file.toString();
         String pattern = createExtensionsRegEx(setOfSettings, fileType);
         if (fn.matches(pattern)) {
            processFile = false;
         } else {
            String contentType = fileNameWrapper.getContentType();
            LOGGER.debug(String.format("File ContentType for '%s': %s", file, contentType));
            if (contentType == null || !isValidContentType(fileType, contentType)) {
               LOGGER.error(String.format("File format of '%s' is not expected.", file));
               processFile = false;
            } else if (file.length() == 0) {
               LOGGER.error(String.format("File '%s' is empty.", file));
               processFile = false;
            }
         }
      } else {
         processFile = false;
      }

      return processFile;
   }

   public static String createExtensionsRegEx(Set<Settings> setOfSettings, FileType fileType) {
      StringJoiner joiner = new StringJoiner("|", "(", ")");
      String sep1 = null;
      Settings s;
      for (Iterator<Settings> iterator = setOfSettings.iterator(); iterator.hasNext();) {
         s = iterator.next();
         switch (fileType) {
            case CSS:
               if (MinificationSeparatorChar.DOT.equals(s.getCssSettings().getMinSeparator())) {
                  sep1 = "\\" + MinificationSeparatorChar.DOT.toString();
               } else {
                  sep1 = s.getCssSettings().getMinSeparator().toString();
               }
               joiner.add(String.format("%s%s(\\.%s)?", sep1, s.getCssSettings().getMinExtension(), FileType.CSS));
               break;
            case JS:
               if (MinificationSeparatorChar.DOT.equals(s.getJsSettings().getMinSeparator())) {
                  sep1 = "\\" + MinificationSeparatorChar.DOT.toString();
               } else {
                  sep1 = s.getJsSettings().getMinSeparator().toString();
               }
               joiner.add(String.format("%s%s(\\.%s)?", sep1, s.getJsSettings().getMinExtension(), FileType.JS));
               break;
            case BOTH:
            default:
               String sep2;
               if (MinificationSeparatorChar.DOT.equals(s.getCssSettings().getMinSeparator())) {
                  sep1 = "\\" + MinificationSeparatorChar.DOT.toString();
               } else {
                  sep1 = s.getCssSettings().getMinSeparator().toString();
               }
               if (MinificationSeparatorChar.DOT.equals(s.getJsSettings().getMinSeparator())) {
                  sep2 = "\\" + MinificationSeparatorChar.DOT.toString();
               } else {
                  sep2 = s.getJsSettings().getMinSeparator().toString();
               }
               joiner.add(String.format("%s%s(\\.%s)?", sep1, s.getCssSettings().getMinExtension(), FileType.CSS));
               joiner.add(String.format("%s%s(\\.%s)?", sep2, s.getJsSettings().getMinExtension(), FileType.JS));
               break;
         }
      }

      return String.format("^.*%s$", joiner.toString());
   }

   public static boolean isValidContentType(FileType fileType, final String contentType) {
      return InternalParameters.ALLOWED_CONTENT_TYPES.get(fileType.toString()).stream().anyMatch(contentType::equals);
   }

   public static String getSettingsDirPath() {
      String absolutePath = null;
      if (SystemUtils.IS_OS_WINDOWS) {
         String userAppData = System.getenv("APPDATA");
         if (userAppData == null) {
            userAppData = SystemUtils.getUserHome().toString();
         }
         absolutePath = String.format("%s%s%s", userAppData, File.separator, SETTINGS_DIR);

      } else if (SystemUtils.IS_OS_MAC_OSX) {
         absolutePath = String.format("%1$s%2$sLibrary%2$sApplication Support%2$s%3$s", SystemUtils.getUserHome(),
               File.separator, SETTINGS_DIR);

      } else if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_SOLARIS) {
         absolutePath = String.format("%1$s%2$s.config%2$s%3$s", SystemUtils.getUserHome(), File.separator,
               SETTINGS_DIR);

      } else {
         throw new IllegalArgumentException(String.format("Unsupported operating system '%s'", SystemUtils.OS_NAME));
      }

      Path p = Paths.get(absolutePath);
      if (!Files.exists(p, LinkOption.NOFOLLOW_LINKS)) {
         try {
            // Create settings directory and history directory as a child
            FileUtils.forceMkdir(p.resolve(SETTINGS_HISTORY_DIR).toFile());
         } catch (IOException ex) {
            LOGGER.error("Cannot create dir ", absolutePath);
         }
      }

      return absolutePath;
   }

   public static void hideFile(File file) throws IOException {
      if (SystemUtils.IS_OS_WINDOWS) {
         Path path = file.toPath();
         DosFileAttributes dos = Files.readAttributes(path, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
         if (!dos.isHidden()) {
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
         }
      } // else the file is assumed to have a leading dot in order to be hidden
        // in Unix-like systems (Linux, MacOs, Solaris, etc.)
   }
}
