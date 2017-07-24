package com.autominifier.ui.controls;

import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;

class SettingsPropertyEditorFactory extends DefaultPropertyEditorFactory {

   @Override
   public PropertyEditor<?> call(Item item) {
      Class<?> type = item.getType();

      if (type == String.class) {
         return SettingsEditors.createTextEditor(item);
      }

      if (isInteger(type)) {
         return SettingsEditors.createIntegerEditor(item);
      }

      return super.call(item);
   }

   private static boolean isInteger(Class<?> type) {
      return (type == int.class || type == Integer.class);
   }
}
