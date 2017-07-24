package com.autominifier.model.settings;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.autominifier.model.settings.beans.CssSettings;
import com.autominifier.model.settings.beans.JsSettings;
import com.autominifier.model.settings.beans.MinificationSeparatorChar;
import com.autominifier.model.settings.beans.Settings;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class SettingsTypeAdapter extends TypeAdapter<Set<Settings>> {

   @Override
   public Set<Settings> read(JsonReader in) throws IOException {
      Set<Settings> setOfSettings = new HashSet<>();
      readSettingsArray(in, setOfSettings);
      return setOfSettings;
   }

   private void readSettingsArray(JsonReader in, Set<Settings> setOfSettings) throws IOException {
      in.beginArray();
      while (in.hasNext()) {
         setOfSettings.add(readSettingsObject(in));
      }
      in.endArray();
   }

   private Settings readSettingsObject(JsonReader in) throws IOException {
      CssSettings cssSettings = null;
      JsSettings jsSettings = null;

      in.beginObject();
      while (in.hasNext()) {
         String name = in.nextName();
         if (name.equals("c")) {
            cssSettings = readCssSettingsObject(in);
         } else if (name.equals("j")) {
            jsSettings = readJsSettingsObject(in);
         } else {
            in.skipValue();
         }
      }
      in.endObject();

      Settings settings = new Settings();
      settings.init(null, false, null, null, cssSettings, jsSettings);
      return settings;
   }

   private CssSettings readCssSettingsObject(JsonReader in) throws IOException {
      String[] values = readStringValues(in);
      CssSettings cssSettings = new CssSettings();
      cssSettings.init(MinificationSeparatorChar.fromString(values[0]), values[1], -1, null);

      return cssSettings;
   }

   private JsSettings readJsSettingsObject(JsonReader in) throws IOException {
      String[] values = readStringValues(in);
      JsSettings jsSettings = new JsSettings();
      jsSettings.init(MinificationSeparatorChar.fromString(values[0]), values[1], -1, null);

      return jsSettings;
   }

   private String[] readStringValues(JsonReader in) throws IOException {
      String minSeparator = null;
      String minExtension = null;

      in.beginObject();
      while (in.hasNext()) {
         String name = in.nextName();
         if (name.equals("s")) {
            minSeparator = in.nextString();
         } else if (name.equals("e")) {
            minExtension = in.nextString();
         } else {
            in.skipValue();
         }
      }
      in.endObject();

      return new String[] { minSeparator, minExtension };
   }

   @Override
   public void write(JsonWriter out, Set<Settings> partialSettingsSet) throws IOException {
      writeSettingsArray(out, partialSettingsSet);
   }

   private void writeSettingsArray(JsonWriter out, Set<Settings> value) throws IOException {
      out.beginArray();
      for (Settings s : value) {
         writeSettingsObject(out, s);
      }
      out.endArray();
   }

   private void writeSettingsObject(JsonWriter out, Settings s) throws IOException {
      out.beginObject();
      out.name("c");
      writeStringValues(out, s.getCssSettings());
      out.name("j");
      writeStringValues(out, s.getJsSettings());
      out.endObject();
   }

   private void writeStringValues(JsonWriter out, CssSettings settings) throws IOException {
      out.beginObject();
      out.name("s").value(settings.getMinSeparator().toString());
      out.name("e").value(settings.getMinExtension());
      out.endObject();
   }
}
