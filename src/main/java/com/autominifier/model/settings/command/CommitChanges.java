package com.autominifier.model.settings.command;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

import com.autominifier.model.settings.beans.Settings;
import com.autominifier.util.ConfigurationUtils;

public class CommitChanges implements SettingsOperation {

   private Settings draftSettings;
   private Settings finalSettings;

   public CommitChanges(Settings draftSettings, Settings finalSettings) {
      this.draftSettings = draftSettings;
      this.finalSettings = finalSettings;
   }

   @Override
   public void execute() {
      try {
         validateBeforeCommit();
         PropertyUtils.copyProperties(this.finalSettings, this.draftSettings);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
            | ConfigurationException e) {
      }
   }

   private void validateBeforeCommit() throws ConfigurationException {
      PropertiesConfiguration configs = ConfigurationUtils.getPropertiesConfig();
      if (StringUtils.isBlank(this.draftSettings.getCssSettings().getMinExtension())) {
         this.draftSettings.getCssSettings().setMinExtension(configs.getString("minification.css.extension"));
      }
      if (this.draftSettings.getCssSettings().getLineBreakPos() < 0) {
         this.draftSettings.getCssSettings().setLineBreakPos(configs.getInt("minification.css.linebreak"));
      }
      if (StringUtils.isBlank(this.draftSettings.getJsSettings().getMinExtension())) {
         this.draftSettings.getJsSettings().setMinExtension(configs.getString("minification.js.extension"));
      }
      if (this.draftSettings.getJsSettings().getLineBreakPos() < 0) {
         this.draftSettings.getJsSettings().setLineBreakPos(configs.getInt("minification.js.linebreak"));
      }
   }
}
