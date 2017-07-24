package com.autominifier.ui.controls;

import org.controlsfx.control.PropertySheet;

import com.autominifier.model.settings.beans.Settings;
import com.autominifier.service.PropertySheetService;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.geometry.Insets;

public class SettingsPropertySheet extends PropertySheet {

   private Settings settings;

   public SettingsPropertySheet(Settings settings, int prefWidth, int prefHeight, Insets insets) {
      this.settings = settings;
      setPrefWidth(prefWidth);
      setPrefHeight(prefHeight);
      setPadding(insets);
      setPropertyEditorFactory(new SettingsPropertyEditorFactory());
      initSheet();
   }

   public void initSheet() {
      Service<ObservableList<Item>> service = new PropertySheetService(settings);
      service.setOnSucceeded(ev -> getItems().setAll(service.valueProperty().get()));
      service.start();
   }
}