package com.autominifier.ui.controls;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Window;
import javafx.util.Duration;

public class NotificationOptions {

   private GraphicMode graphicMode;
   private Node        customGraphic;
   private Duration    hideAfter;
   private Pos         pos;
   private Window       owner;
   private boolean     hideCloseButton;
   private boolean     darkStyle;

   public NotificationOptions() {
      this(GraphicMode.NO_GRAPHIC, null, Duration.seconds(5), Pos.BOTTOM_RIGHT, null, false, false);
   }

   public NotificationOptions(GraphicMode graphicMode, Node customGraphic, Duration hideAfter, Pos pos, Window owner,
         boolean hideCloseButton, boolean darkStyle) {
      this.graphicMode = graphicMode;
      if (graphicMode == GraphicMode.CUSTOM && customGraphic == null)
         throw new IllegalArgumentException("Custom Graphic node is missing");
      this.customGraphic = customGraphic;
      this.hideAfter = hideAfter;
      this.pos = pos;
      this.owner = owner;
      this.hideCloseButton = hideCloseButton;
      this.darkStyle = darkStyle;
   }

   public GraphicMode getGraphicMode() {
      return graphicMode;
   }

   public Duration getHideAfter() {
      return hideAfter;
   }

   public Pos getPos() {
      return pos;
   }

   public Window getOwner() {
      return owner;
   }

   public boolean isHideCloseButton() {
      return hideCloseButton;
   }

   public boolean isDarkStyle() {
      return darkStyle;
   }

   public Node getCustomGraphic() {
      return customGraphic;
   }

   @Override
   public String toString() {
      return "NotificationOptions [graphicMode=" + graphicMode + ", hideAfter=" + hideAfter + ", pos=" + pos
            + ", hideCloseButton=" + hideCloseButton + ", darkStyle=" + darkStyle + "]";
   }
}
