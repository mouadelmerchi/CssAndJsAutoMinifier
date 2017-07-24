package com.autominifier.model.settings;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

public final class InternalParameters {

   public static final String APP_PROPERTIES_FILE     = "application.properties";
   public static final String SETTINGS_DIR            = ".AutoMinifierSettings";
   public static final String SETTINGS_HISTORY_DIR    = "History";
   public static final String SETTINGS_FILE           = "settings.ser";
   public static final String SENTINEL_FILE_EXTENSION = "sent";

   public static final int FILE_QUEUE_SIZE                       = 500;
   public static final int MAXIMUM_MILLIS_LIMIT_BEFORE_GIVING_UP = 2000;
   public static final int MILLIS_TO_WAIT_BEFORE_RETRY           = 2000;

   public static final MultiValuedMap<String, String> ALLOWED_CONTENT_TYPES = new HashSetValuedHashMap<>();

   static {
      ALLOWED_CONTENT_TYPES.put("css", "text/css");
      ALLOWED_CONTENT_TYPES.put("js", "application/javascript");
      ALLOWED_CONTENT_TYPES.put("js", "application/x-javascript");
      ALLOWED_CONTENT_TYPES.put("js", "text/javascript");
      ALLOWED_CONTENT_TYPES.put("both", "text/css");
      ALLOWED_CONTENT_TYPES.put("both", "application/javascript");
      ALLOWED_CONTENT_TYPES.put("both", "application/x-javascript");
      ALLOWED_CONTENT_TYPES.put("both", "text/javascript");
   }

   private InternalParameters() {
   }
}