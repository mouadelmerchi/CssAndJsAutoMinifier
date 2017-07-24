package com.autominifier.ui.controls;

public enum GraphicMode {
   WARNING("warning"), INFO("information"), CONFIRM("confirmation"), ERROR("error"), NO_GRAPHIC("no graphic"), CUSTOM(
         "custom");

   private String mode;

   private GraphicMode(String mode) {
      this.mode = mode;
   }

   @Override
   public String toString() {
      return mode;
   }
}
