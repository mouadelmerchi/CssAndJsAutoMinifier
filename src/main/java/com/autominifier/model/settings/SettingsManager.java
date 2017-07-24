package com.autominifier.model.settings;

import java.util.Stack;

import com.autominifier.model.settings.command.SettingsOperation;
import com.autominifier.model.settings.command.UndoableSettingsOperation;

public final class SettingsManager {

   private Stack<UndoableSettingsOperation> operations;

   private static class SettingsManagerHolder {
      private static SettingsManager instance;
      static {
         instance = new SettingsManager();
         instance.operations = new Stack<>();
      }
   }

   private SettingsManager() {
   }

   public static SettingsManager getInstance() {
      return SettingsManagerHolder.instance;
   }

   public void executeOperation(SettingsOperation operation) {
      operation.execute();
      if (operation instanceof UndoableSettingsOperation) {
         operations.push((UndoableSettingsOperation) operation);
      }
   }

   public void undoOperation() {
      if (!operations.isEmpty()) {
         UndoableSettingsOperation op = operations.pop();
         op.undo();
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
   }
}