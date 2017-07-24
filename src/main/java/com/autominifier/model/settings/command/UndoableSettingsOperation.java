package com.autominifier.model.settings.command;

public interface UndoableSettingsOperation extends SettingsOperation {
   void undo();
}
