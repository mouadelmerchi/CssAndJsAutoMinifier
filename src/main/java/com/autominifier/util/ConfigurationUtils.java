package com.autominifier.util;

import static com.autominifier.model.settings.InternalParameters.APP_PROPERTIES_FILE;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public final class ConfigurationUtils {

   private static final FileBasedConfigurationBuilder<PropertiesConfiguration> PROPERTIES_BUILDER;

   static {
      PROPERTIES_BUILDER = new Configurations().propertiesBuilder(APP_PROPERTIES_FILE);
   }

   private ConfigurationUtils() {
   }

   public static PropertiesConfiguration getPropertiesConfig() throws ConfigurationException {
      return PROPERTIES_BUILDER.getConfiguration();
   }
}
