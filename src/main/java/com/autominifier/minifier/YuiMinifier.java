package com.autominifier.minifier;

import static com.autominifier.util.AutoMinifierUtils.isValidContentType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.autominifier.model.FilePathWrapper;
import com.autominifier.model.FileType;
import com.autominifier.model.settings.beans.CssSettings;
import com.autominifier.model.settings.beans.JsSettings;
import com.autominifier.model.settings.beans.Settings;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class YuiMinifier implements Minifier {

   private static final Logger LOGGER = LogManager.getLogger(YuiMinifier.class);

   private Settings settings;

   public YuiMinifier(Settings settings) {
      this.settings = settings;
   }

   @Override
   public void compress(FilePathWrapper fileWrapper) throws Exception {
      String filePath = fileWrapper.getFilePath();
      String contentType = fileWrapper.getContentType();
      String ext = FilenameUtils.getExtension(filePath);
      String minSep;
      String minExt;
      String newFileName;

      if (isValidContentType(FileType.CSS, contentType)) {
         CssSettings cssSettings = settings.getCssSettings();
         
         minSep = cssSettings.getMinSeparator().toString();
         minExt = cssSettings.getMinExtension();
         newFileName = String.format("%s%s%s%s", FilenameUtils.removeExtension(filePath), minSep, minExt,
               StringUtils.isNotEmpty(ext) ? String.format(".%s", ext) : "");
         LOGGER.debug(String.format("New Filename: %s", newFileName));
         try (Reader in = new InputStreamReader(new FileInputStream(filePath), settings.getCharset().toString());
              Writer out = new OutputStreamWriter(new FileOutputStream(newFileName), settings.getCharset().toString())) {

            CssCompressor compressor = new CssCompressor(in);
            compressor.compress(out, cssSettings.getLineBreakPos());
         }
      } else if (isValidContentType(FileType.JS, contentType)) {
         JsSettings jsSettings = settings.getJsSettings();
         
         minSep = jsSettings.getMinSeparator().toString();
         minExt = jsSettings.getMinExtension();
         newFileName = String.format("%s%s%s%s", FilenameUtils.removeExtension(filePath), minSep, minExt,
               StringUtils.isNotEmpty(ext) ? String.format(".%s", ext) : "");
         LOGGER.debug(String.format("New Filename: %s", newFileName));
         try (Reader in = new InputStreamReader(new FileInputStream(filePath), settings.getCharset().toString());
               Writer out = new OutputStreamWriter(new FileOutputStream(newFileName),
                     settings.getCharset().toString())) {

            JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YUIMinifierErrorReporter());
            compressor.compress(out, jsSettings.getLineBreakPos(), jsSettings.isMunge(), settings.isVerbose(),
                  jsSettings.isPreserveAllSemiColons(), jsSettings.isDisableOptimization());
         }
      } else {
         LOGGER.error(String.format("Minification aborted due to unvalid Content Type for '%s'", filePath));
         return;
      }
   }

   private static class YUIMinifierErrorReporter implements ErrorReporter {
      @Override
      public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
         if (line < 0) {
            LOGGER.warn(message);
         } else {
            LOGGER.warn(String.format("%d:%d:%s", line, lineOffset, message));
         }
      }

      @Override
      public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
         if (line < 0) {
            LOGGER.error(message);
         } else {
            LOGGER.error(String.format("%d:%d:%s", line, lineOffset, message));
         }
      }

      @Override
      public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
            int lineOffset) {
         error(message, sourceName, line, lineSource, lineOffset);
         return new EvaluatorException(message);
      }
   }
}
