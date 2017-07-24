package com.autominifier.ui.controls;

import java.lang.reflect.InvocationTargetException;

import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.PropertyEditor;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextInputControl;

final class SettingsEditors {

   private SettingsEditors() {
   }

   public static final PropertyEditor<?> createTextEditor(Item property) {

      return new AbstractPropertyEditor<String, MinExtensionTextField>(property, new MinExtensionTextField()) {

         {
            enableAutoSelectAll(getEditor());
         }

         @Override
         protected ObservableValue<String> getObservableValue() {
            return getEditor().textProperty();
         }

         @Override
         public void setValue(String value) {
            getEditor().setText(value);
         }
      };
   }

   @SuppressWarnings("unchecked")
   public static final PropertyEditor<?> createIntegerEditor(Item property) {

      return new AbstractPropertyEditor<Integer, IntegerField>(property, new IntegerField()) {

         private Class<Integer> sourceClass = (Class<Integer>) property.getType();

         {
            enableAutoSelectAll(getEditor());
         }

         @Override
         protected ObservableValue<Integer> getObservableValue() {
            return getEditor().valueProperty();
         }

         @Override
         public Integer getValue() {
            try {
               return sourceClass.getConstructor(String.class).newInstance(getEditor().getText());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                  | InvocationTargetException | NoSuchMethodException | SecurityException e) {
               e.printStackTrace();
               return null;
            }
         }

         @Override
         public void setValue(Integer value) {
            sourceClass = (Class<Integer>) value.getClass();
            getEditor().setText(value.toString());
         }

      };
   }

   private static void enableAutoSelectAll(final TextInputControl control) {
      control.focusedProperty().addListener((obersvable, oldValue, newValue) -> {
         if (newValue) {
            Platform.runLater(() -> {
               control.selectAll();
            });
         }
      });
   }
}
