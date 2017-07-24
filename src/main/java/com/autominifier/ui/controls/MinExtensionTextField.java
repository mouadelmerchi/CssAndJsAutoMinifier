package com.autominifier.ui.controls;

import javafx.scene.control.TextField;

public class MinExtensionTextField extends TextField {

   public MinExtensionTextField() {
      super();
   }

   public void replaceText(int start, int end, String text) {
      String oldValue = getText();
      if (text.matches("[a-zA-Z0-9]?")) {
         super.replaceText(start, end, text);
      }
      if (getText().length() > 31) {
         setText(oldValue);
      }
   }

   public void replaceSelection(String text) {
      String oldValue = getText();
      if (text.matches("[a-zA-Z0-9]?")) {
         super.replaceSelection(text);
      }
      if (getText().length() > 31) {
         setText(oldValue);
      }
   }
}
