package com.autominifier.model.settings.beans;

public enum MinificationSeparatorChar {
   DOT("."), HYPHEN("-"), UNDER_SCORE("_");

   private String separator;

   private MinificationSeparatorChar(String separator) {
      this.separator = separator;
   }

   public static MinificationSeparatorChar fromString(String separator) {
      switch (separator) {
         case ".":
            return DOT;
         case "-":
            return HYPHEN;
         case "_":
            return UNDER_SCORE;
         default:
            return null;
      }
   }

   public boolean equals(MinificationSeparatorChar other) {
      return this.separator.equals(other.separator);
   }

   @Override
   public String toString() {
      return separator;
   }
}
