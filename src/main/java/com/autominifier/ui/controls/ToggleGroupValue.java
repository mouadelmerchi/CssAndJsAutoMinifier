package com.autominifier.ui.controls;

import java.util.Optional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 * An extended ToggleGroup that adds a value property. Toggles should be added
 * to this group via the add method, which takes a toggle and a value. Whenever
 * the selected toggle changes, the corresponding value is set in the value
 * property. Vice versa, when the value property is set, the corresponding
 * toggle is selected. Note: of course values have to be unique and null is used
 * for when no toggle is selected.
 * 
 * @author Tom Eugelink
 * @author Mouad El Merchichi
 * 
 * @param <T>
 */
public class ToggleGroupValue<T> extends ToggleGroup {

   private final ObjectProperty<T> valueObjectProperty = new SimpleObjectProperty<T>(this, "value", null) {
      @Override
      public void set(T value) {
         super.set(value);

         // if null deselect
         if (value == null) {
            selectToggle(null);
         } else {
            Optional<Toggle> toggleOpt = getToggles().stream()
                  .filter(lToggle -> (lToggle.getUserData() != null && lToggle.getUserData().equals(value))
                        && getSelectedToggle() != lToggle)
                  .findFirst();

            if (toggleOpt.isPresent()) {
               selectToggle(toggleOpt.get());
            }
         }
      }
   };

   public ObjectProperty<T> valueProperty() {
      return this.valueObjectProperty;
   }

   // java bean API
   public final T getValue() {
      return this.valueObjectProperty.getValue();
   }

   public final void setValue(T value) {
      this.valueObjectProperty.setValue(value);
   }

   public ToggleGroupValue<T> withValue(T value) {
      setValue(value);
      return this;
   }

   public ToggleGroupValue() {
      construct();
   }

   /**
    * Convenience method for toggle's setToggleGroup and setUserData.
    * 
    * @param toggle
    * @param userData
    */
   public void add(Toggle toggle, T userData) {
      toggle.setToggleGroup(this);
      toggle.setUserData(userData);
   }

   @SuppressWarnings("unchecked")
   private void construct() {
      selectedToggleProperty().addListener(toggleProperty -> {
         // get selected toggle
         Toggle lToggle = selectedToggleProperty().get();
         if (lToggle == null) {
            valueObjectProperty.set(null);
         } else {
            T lValue = (T) lToggle.getUserData();
            valueObjectProperty.set(lValue);
         }
      });
   }
}