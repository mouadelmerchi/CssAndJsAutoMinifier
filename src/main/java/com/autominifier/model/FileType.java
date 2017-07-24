package com.autominifier.model;

public enum FileType {
   CSS("css"), JS("js"), BOTH("both");

   private final String type;

   private FileType(String type) {
      this.type = type;
   }

   public boolean equals(String otherType) {
      return type.equals(otherType);
   }

   public static FileType fromString(String type) {
      switch (type) {
         case "css":
            return CSS;
         case "js":
            return JS;
         case "both":
            return BOTH;
         default:
            return null;
      }
   }

   @Override
   public String toString() {
      return this.type;
   }
}
