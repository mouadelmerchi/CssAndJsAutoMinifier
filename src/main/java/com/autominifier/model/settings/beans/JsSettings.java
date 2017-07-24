package com.autominifier.model.settings.beans;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class JsSettings extends CssSettings {

   private static final long serialVersionUID = -495565476844495876L;

   private BooleanProperty munge;
   private BooleanProperty preserveAllSemiColons;
   private BooleanProperty disableOptimization;

   public JsSettings() {
      super();
      munge = new SimpleBooleanProperty();
      preserveAllSemiColons = new SimpleBooleanProperty();
      disableOptimization = new SimpleBooleanProperty();
   }

   public void init(final JsSettings jsSettings) {
      init(jsSettings.getMinSeparator(), jsSettings.getMinExtension(), jsSettings.getLineBreakPos(),
            jsSettings.getApiUrl(), jsSettings.isMunge(), jsSettings.isPreserveAllSemiColons(),
            jsSettings.isDisableOptimization());
   }

   public void init(MinificationSeparatorChar minSeparator, String minExtension, int lineBreakPos, String apiUrl,
         boolean munge, boolean perserveAllSemiColons, boolean disableOptimization) {
      super.init(minSeparator, minExtension, lineBreakPos, apiUrl);
      setMunge(munge);
      setPreserveAllSemiColons(perserveAllSemiColons);
      setDisableOptimization(disableOptimization);
   }

   public BooleanProperty mungeProperty() {
      return munge;
   }

   public final boolean isMunge() {
      return munge.get();
   }

   public final void setMunge(boolean mng) {
      munge.set(mng);
   }

   public BooleanProperty preserveAllSemiColonsProperty() {
      return preserveAllSemiColons;
   }

   public final boolean isPreserveAllSemiColons() {
      return preserveAllSemiColons.get();
   }

   public final void setPreserveAllSemiColons(boolean prsrvAllSemiClns) {
      preserveAllSemiColons.set(prsrvAllSemiClns);
   }

   public BooleanProperty disableOptimizationProperty() {
      return disableOptimization;
   }

   public final boolean isDisableOptimization() {
      return disableOptimization.get();
   }

   public final void setDisableOptimization(boolean dsblOpt) {
      disableOptimization.set(dsblOpt);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (munge.get() ? 1231 : 1237);
      result = prime * result + (preserveAllSemiColons.get() ? 1231 : 1237);
      result = prime * result + (disableOptimization.get() ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      JsSettings other = (JsSettings) obj;
      if (munge.get() != other.munge.get())
         return false;
      if (preserveAllSemiColons.get() != other.preserveAllSemiColons.get())
         return false;
      if (disableOptimization.get() != other.disableOptimization.get())
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "JsSettings [" + super.toString() + ", munge=" + isMunge() + ", preserveAllSemiColons="
            + isPreserveAllSemiColons() + ", disableOptimization=" + isDisableOptimization() + "]";
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException {
      super.writeExternal(out);
      out.writeBoolean(isMunge());
      out.writeBoolean(isPreserveAllSemiColons());
      out.writeBoolean(isDisableOptimization());
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      super.readExternal(in);
      Boolean m = in.readBoolean();
      Boolean pSC = in.readBoolean();
      Boolean dO = in.readBoolean();

      if (validateBeforeInit(m, pSC, dO)) {
         setMunge(m);
         setPreserveAllSemiColons(pSC);
         setDisableOptimization(dO);
      } else {
         throw new IllegalArgumentException("Deserialized JS settings must not be null");
      }
   }

   private boolean validateBeforeInit(Boolean m, Boolean pSC, Boolean dO) {
      return ((m != null) && (pSC != null) && (dO != null));
   }
}
