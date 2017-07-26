package com.autominifier.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang3.SerializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autominifier.model.settings.InternalParameters;
import com.autominifier.model.settings.SettingsManager;
import com.autominifier.model.settings.beans.Settings;
import com.autominifier.model.settings.command.CommitChanges;
import com.autominifier.model.settings.command.RestoreDefaults;
import com.autominifier.model.settings.command.SettingsOperation;
import com.autominifier.util.AutoMinifierUtils;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SerializationService extends Service<Void> {

   private static final Logger LOGGER = LogManager.getLogger(SerializationService.class);

   private final Settings          draftSettings;
   private final Settings          finalSettings;
   private final SerializationType serialize;

   public SerializationService(Settings draftSettings, Settings finalSettings, SerializationType serialize) {
      this.draftSettings = draftSettings;
      this.finalSettings = finalSettings;
      this.serialize = serialize;
   }

   @Override
   protected Task<Void> createTask() {
      switch (serialize) {
         case DESERIALIZE:
            return new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                  String dir = AutoMinifierUtils.getSettingsDirPath();
                  String settingsSer = String.format("%s%s%s", dir, File.separator, InternalParameters.SETTINGS_FILE);
                  try (FileInputStream fis = new FileInputStream(settingsSer);
                        ObjectInputStream ois = new ObjectInputStream(fis)) {
                     draftSettings.readExternal(ois);
                  } catch (Exception e) {
                     SettingsOperation restoreDefaults = new RestoreDefaults(draftSettings);
                     manageOp(restoreDefaults);
                     LOGGER.error("Oops! Something came up while deserializing settings. Cause: ", e);
                  } finally {
                     SettingsOperation commitChanges = new CommitChanges(draftSettings, finalSettings);
                     manageOp(commitChanges);
                  }

                  return null;
               }

               private void manageOp(SettingsOperation operation) {
                  SettingsManager manager = SettingsManager.getInstance();
                  manager.executeOperation(operation);
               }
            };
         case SERIALIZE:
         default:
            return new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                  String dir = AutoMinifierUtils.getSettingsDirPath();
                  String settingsSer = String.format("%s%s%s", dir, File.separator, InternalParameters.SETTINGS_FILE);
                  try (FileOutputStream fos = new FileOutputStream(settingsSer);
                        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                     finalSettings.writeExternal(oos);
                  } catch (SerializationException e) {
                     LOGGER.error("Oops! Something came up while serializing settings. Cause: ", e);
                  }

                  return null;
               }
            };
      }
   }
}
