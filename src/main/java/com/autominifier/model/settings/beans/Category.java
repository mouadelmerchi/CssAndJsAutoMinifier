package com.autominifier.model.settings.beans;

public enum Category {
   COMMON_SETTINGS("1. Common Settings"), CSS_SETTINGS("2. CSS Settings"), JS_SETTINGS("3. JavaScript Settings");

   private String category;

   private Category(String category) {
      this.category = category;
   }

   @Override
   public String toString() {
      return category;
   }
}
