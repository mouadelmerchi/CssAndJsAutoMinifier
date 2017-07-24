package com.autominifier.model.settings.command;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.autominifier.model.settings.beans.Settings;

public class CancelChanges implements SettingsOperation {

   private Settings draftSettings;
   private Settings finalSettings;

   public CancelChanges(Settings draftSettings, Settings finalSettings) {
      this.draftSettings = draftSettings;
      this.finalSettings = finalSettings;
   }

   @Override
   public void execute() {
      try {
         PropertyUtils.copyProperties(this.draftSettings, this.finalSettings);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
      }
   }

}
