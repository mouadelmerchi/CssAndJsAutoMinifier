package com.autominifier.ui.controls;

import java.util.function.UnaryOperator;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

class IntegerField extends TextField {

   private final TextFormatter<Integer> textFormatter;

   public IntegerField() {
      UnaryOperator<Change> integerFilter = change -> {
         String newText = change.getControlNewText();
         if (newText.matches("-?([0-9]*)?")) {
            return change;
         }

         return null;
      };

      // modified version of standard converter that evaluates an empty string
      // as -1 instead of null:
      StringConverter<Integer> converter = new IntegerStringConverter() {
         @Override
         public Integer fromString(String s) {
            if (s == null || s.isEmpty())
               return -1;
            return super.fromString(s.trim());
         }
      };

      textFormatter = new TextFormatter<Integer>(converter, 0, integerFilter);
      setTextFormatter(textFormatter);
   }

   public final ObservableValue<Integer> valueProperty() {
      return textFormatter.valueProperty();
   }
}
