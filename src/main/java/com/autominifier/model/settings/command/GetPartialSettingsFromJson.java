package com.autominifier.model.settings.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autominifier.model.settings.beans.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GetPartialSettingsFromJson implements SettingsOperation {

   private static final Logger LOGGER = LogManager.getLogger(AppendPartialSettingsToJson.class);

   private GsonBuilder   builder;
   private File          jSonFile;
   private final Set<Settings> out;

   /**
    * Creates a new operation that extracts partial settings from json file
    * 
    * @param builder - Gson builder
    * @param jSonFile - json file
    * @param out - An empty set that will be filled with json partial settings
    */
   public GetPartialSettingsFromJson(GsonBuilder builder, File jSonFile, final Set<Settings> out) {
      this.builder = builder;
      this.jSonFile = jSonFile;
      this.out = out;
   }

   @Override
   public void execute() {
      Gson gson = builder.create();
      try (Reader reader = new InputStreamReader(new FileInputStream(jSonFile), Charset.forName("UTF-8"))) {
         Set<Settings> partialSettingsFromJson = gson.fromJson(reader, new TypeToken<Set<Settings>>() {}.getType());
         if (partialSettingsFromJson != null) {
            out.addAll(partialSettingsFromJson);
         }
      } catch (IOException e) {
         LOGGER.debug("Oops! Something came up. Cause: ", e);
      }
   }
}
