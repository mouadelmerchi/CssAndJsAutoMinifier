package com.autominifier.model.settings.command;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.autominifier.model.settings.beans.Charset;
import com.autominifier.model.settings.beans.CssSettings;
import com.autominifier.model.settings.beans.JsSettings;
import com.autominifier.model.settings.beans.MinificationSeparatorChar;
import com.autominifier.model.settings.beans.Settings;
import com.autominifier.util.ConfigurationUtils;

public class RestoreDefaults implements SettingsOperation {

   private Settings draftSettings;

   public RestoreDefaults(Settings draftSettings) {
      this.draftSettings = draftSettings;
   }

   @Override
   public void execute() {
      try {
         PropertiesConfiguration configs = ConfigurationUtils.getPropertiesConfig();
         Charset c = Charset.fromString(configs.getString("minification.charset"));
         MinificationSeparatorChar cssMinSep = MinificationSeparatorChar
               .fromString(configs.getString("minification.css.separator.char"));
         String cssM = configs.getString("minification.css.extension");
         int cssLb = configs.getInt("minification.css.linebreak");
         MinificationSeparatorChar jsMinSep = MinificationSeparatorChar
               .fromString(configs.getString("minification.js.separator.char"));
         String jsM = configs.getString("minification.js.extension");
         int jsLb = configs.getInt("minification.js.linebreak");
         boolean m = configs.getBoolean("minification.js.munge");
         boolean pSC = configs.getBoolean("minification.js.perserve.all.semicolons");
         boolean dO = configs.getBoolean("minification.js.disable.optimization");

         CssSettings cssS = new CssSettings();
         cssS.init(cssMinSep, cssM, cssLb, null);
         JsSettings jsS = new JsSettings();
         jsS.init(jsMinSep, jsM, jsLb, null, m, pSC, dO);

         this.draftSettings.init(c, false, null, null, cssS, jsS);
      } catch (ConfigurationException e) {
      }
   }
}
