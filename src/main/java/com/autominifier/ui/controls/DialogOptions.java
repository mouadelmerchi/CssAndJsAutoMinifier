package com.autominifier.ui.controls;

import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DialogOptions {

   private AlertType  dialogType;
   private String     mastHead;
   private String     graphicFileName;
   private boolean    resizable;
   private Modality   modality;
   private StageStyle stageStyle;
   private Window     owner;

   public DialogOptions(AlertType dialogType, String mastHead, String graphicFileName, boolean resizable,
         Modality modality, StageStyle stageStyle, Window owner) {
      this.dialogType = dialogType;
      this.mastHead = mastHead;
      this.graphicFileName = graphicFileName;
      this.resizable = resizable;
      this.modality = modality;
      this.stageStyle = stageStyle;
      this.owner = owner;
   }

   public AlertType getDialogType() {
      return dialogType;
   }

   public String getMastHead() {
      return mastHead;
   }

   public String getGraphicFileName() {
      return graphicFileName;
   }

   public boolean isResizable() {
      return resizable;
   }

   public void setResizable(boolean resizable) {
      this.resizable = resizable;
   }

   public Modality getModality() {
      return modality;
   }

   public StageStyle getStageStyle() {
      return stageStyle;
   }

   public Window getOwner() {
      return owner;
   }
}
