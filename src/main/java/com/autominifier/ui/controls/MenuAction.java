package com.autominifier.ui.controls;

import java.util.function.Consumer;

import org.controlsfx.control.action.Action;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCodeCombination;

public class MenuAction extends Action {
   
   public MenuAction(String name, Node image, KeyCodeCombination keyCombination) {
      super(name);
      setGraphic(image);
      setAccelerator(keyCombination);
   }

   public MenuAction(String name) {
      super(name);
   }

   public void setHandler(Consumer<ActionEvent> eventHandler) {
      setEventHandler(eventHandler);
   }

   @Override
   public String toString() {
      return getText();
   }
}
