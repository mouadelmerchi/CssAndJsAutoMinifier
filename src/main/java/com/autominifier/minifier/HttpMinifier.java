package com.autominifier.minifier;

import static com.autominifier.util.AutoMinifierUtils.isValidContentType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autominifier.model.FilePathWrapper;
import com.autominifier.model.FileType;
import com.autominifier.model.settings.InternalParameters;
import com.autominifier.model.settings.beans.Settings;

public class HttpMinifier implements Minifier {

   private static final Logger LOGGER = LogManager.getLogger(HttpMinifier.class);

   private Settings settings;

   public HttpMinifier(Settings settings) {
      this.settings = settings;
   }

   // HTTP POST request
   public void compress(FilePathWrapper fileWrapper) throws Exception {
      String charsetName = settings.getCharset().toString();
      String userAgent = settings.getUserAgent();
      String acceptLanguage = settings.getAcceptLanguage();

      String filePath = fileWrapper.getFilePath();
      String contentType = fileWrapper.getContentType();
      String ext = FilenameUtils.getExtension(filePath);
      String url = null;
      String minSep = null;
      String minExt = null;

      if (isValidContentType(FileType.CSS, contentType)) {
         url = settings.getCssSettings().getApiUrl();
         minSep = settings.getCssSettings().getMinSeparator().toString();
         minExt = settings.getCssSettings().getMinExtension();
      } else if (isValidContentType(FileType.JS, contentType)) {
         url = settings.getJsSettings().getApiUrl();
         minSep = settings.getJsSettings().getMinSeparator().toString();
         minExt = settings.getJsSettings().getMinExtension();
      } else {
         LOGGER.error(String.format("Content Type not valid for '%s'", filePath));
         return;
      }

      URL obj;
      try {
         obj = new URL(url);
      } catch (MalformedURLException e) {
         LOGGER.error(String.format("Oops! Something came up. Cause: %s", e.getCause()));
         return;
      }

      Charset charset = Charset.forName(charsetName);
      byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
      StringBuilder data = new StringBuilder();
      data.append(URLEncoder.encode("input", charset.name()));
      data.append('=');
      data.append(URLEncoder.encode(new String(bytes), charset.name()));

      bytes = data.toString().getBytes(charset.name());

      int responseCode;
      do {
         HttpsURLConnection con = cast(obj.openConnection());

         // add request header
         con.setRequestMethod("POST");
         con.setDoOutput(true);
         con.setRequestProperty("User-Agent", userAgent);
         con.setRequestProperty("Accept-Language", acceptLanguage);
         con.setRequestProperty("Content-Type",
               String.format("application/x-www-form-urlencoded; charset=%s", charset.name()));
         con.setRequestProperty("Content-Length", Integer.toString(bytes.length));

         // Send post request
         try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(bytes);
         }

         responseCode = con.getResponseCode();
         LOGGER.debug(String.format("\nSending 'POST' request to URL : %s", url));
         LOGGER.debug(String.format("Response Code : %s", responseCode));

         if (responseCode == 200) {
            String newFileName = String.format("%s%s%s%s", FilenameUtils.removeExtension(filePath), minSep, minExt,
                  StringUtils.isNoneEmpty(ext) ? String.format(".%s", ext) : "");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));) {
               String inputLine;
               data = new StringBuilder();
               while ((inputLine = in.readLine()) != null) {
                  data.append(inputLine);
               }
               in.close();
            }
            FileUtils.write(new File(newFileName), data.toString(), charset);
         } else {
            LOGGER.debug(String.format("Oops! Something came up with the minification server. response code: %d",
                  responseCode));
            Thread.sleep(InternalParameters.MILLIS_TO_WAIT_BEFORE_RETRY);
         }
      } while (responseCode != 200);
   }

   @SuppressWarnings("unchecked")
   private static <T extends URLConnection> T cast(URLConnection connection) {
      return (T) connection;
   }
}
