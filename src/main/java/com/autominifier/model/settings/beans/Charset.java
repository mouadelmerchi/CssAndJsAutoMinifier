package com.autominifier.model.settings.beans;

public enum Charset {
   UTF_8("UTF-8"), UTF_16("UTF-16"), ISO_8859_1("ISO-8859-1"), US_ASCII("US-ASCII"), UTF_16BE("UTF-16BE"), UTF_16LE(
         "UTF-16LE");

   private String charset;

   private Charset(String charset) {
      this.charset = charset;
   }

   public static Charset fromString(String charset) {
      switch (charset) {
         case "UTF-8":
            return UTF_8;
         case "UTF-16":
            return UTF_16;
         case "ISO-8859-1":
            return ISO_8859_1;
         case "US-ASCII":
            return US_ASCII;
         case "UTF-16BE":
            return UTF_16BE;
         case "UTF-16LE":
            return UTF_16LE;
         default:
            return null;
      }
   }

   @Override
   public String toString() {
      return this.charset;
   }
}
