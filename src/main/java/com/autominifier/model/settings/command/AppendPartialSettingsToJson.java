package com.autominifier.model.settings.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autominifier.model.settings.beans.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class AppendPartialSettingsToJson implements SettingsOperation {

   private static final Logger LOGGER = LogManager.getLogger(AppendPartialSettingsToJson.class);

   private GsonBuilder         builder;
   private File                jSonFile;
   private final Set<Settings> partialSettings;

   public AppendPartialSettingsToJson(GsonBuilder builder, File jSonFile, final Set<Settings> partialSettings) {
      this.builder = builder;
      this.jSonFile = jSonFile;
      this.partialSettings = partialSettings;
   }

   @Override
   public void execute() {
      Gson gson = builder.create();
      try (Writer writer = new OutputStreamWriter(new FileOutputStream(jSonFile, false), Charset.forName("UTF-8"))) {
         gson.toJson(partialSettings, new TypeToken<Set<Settings>>() {}.getType(), writer);
      } catch (IOException e) {
         LOGGER.debug("Oops! Something came up. Cause: ", e);
      }
   }
}
