package com.autominifier.service;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import org.controlsfx.control.PropertySheet.Item;

import com.autominifier.model.settings.beans.CssSettings;
import com.autominifier.model.settings.beans.CssSettingsBeanInfo;
import com.autominifier.model.settings.beans.JsSettings;
import com.autominifier.model.settings.beans.JsSettingsBeanInfo;
import com.autominifier.model.settings.beans.Settings;
import com.autominifier.model.settings.beans.SettingsBeanInfo;
import com.autominifier.ui.controls.SettingsItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;

public class PropertySheetService extends Service<ObservableList<Item>> {

   private Settings settings;

   public PropertySheetService(Settings settings) {
      this.settings = settings;
   }

   @Override
   protected Task<ObservableList<Item>> createTask() {
      return new Task<ObservableList<Item>>() {
         @Override
         protected ObservableList<Item> call() throws Exception {
            return getSettingsProperties(settings);
         }
      };
   }

   private ObservableList<Item> getSettingsProperties(Settings settings) {
      ObservableList<Item> list = FXCollections.observableArrayList();
      BeanInfo settingsBeanInfo = new SettingsBeanInfo();
      getSettingsProperties(settings, settingsBeanInfo, list);
      return list;
   }

   private <T> void getSettingsProperties(T settings, BeanInfo beanInfo, ObservableList<Item> list) {
      for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
         if (isProperty(p) && !p.isHidden()) {
            if (p.getPropertyType() == CssSettings.class) {
               BeanInfo cssBeanInfo = new CssSettingsBeanInfo();
               CssSettings cssSettings = ((Settings) settings).getCssSettings();
               getSettingsProperties(cssSettings, cssBeanInfo, list);

            } else if (p.getPropertyType() == JsSettings.class) {
               BeanInfo jsBeanInfo = new JsSettingsBeanInfo();
               JsSettings jsSettings = ((Settings) settings).getJsSettings();
               getSettingsProperties(jsSettings, jsBeanInfo, list);

            } else {
               list.add(new SettingsItem(settings, p));
            }
         }
      }
   }

   private static boolean isProperty(final PropertyDescriptor p) {
      return p.getWriteMethod() != null && !p.getPropertyType().isAssignableFrom(EventHandler.class);
   }
}