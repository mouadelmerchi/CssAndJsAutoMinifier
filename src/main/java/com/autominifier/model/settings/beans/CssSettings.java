package com.autominifier.model.settings.beans;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CssSettings implements Externalizable {

   private static final long serialVersionUID = 24662000376896378L;

   private ObjectProperty<MinificationSeparatorChar> minSeparator;
   private StringProperty                            minExtension;
   private IntegerProperty                           lineBreakPos;
   private transient StringProperty                  apiUrl;

   public CssSettings() {
      minSeparator = new SimpleObjectProperty<>();
      minExtension = new SimpleStringProperty();
      lineBreakPos = new SimpleIntegerProperty();
      apiUrl = new SimpleStringProperty();
   }

   public void init(final CssSettings cssSettings) {
      init(cssSettings.getMinSeparator(), cssSettings.getMinExtension(), cssSettings.getLineBreakPos(),
            cssSettings.getApiUrl());
   }

   public void init(MinificationSeparatorChar minSeparator, String minExtension, int lineBreakPos, String apiUrl) {
      setMinSeparator(minSeparator);
      setMinExtension(minExtension);
      setLineBreakPos(lineBreakPos);
      setApiUrl(apiUrl);
   }

   public ObjectProperty<MinificationSeparatorChar> minSeparatorProperty() {
      return minSeparator;
   }

   public final MinificationSeparatorChar getMinSeparator() {
      return minSeparator.get();
   }

   public final void setMinSeparator(MinificationSeparatorChar minSep) {
      minSeparator.set(minSep);
   }

   public StringProperty minExtensionProperty() {
      return minExtension;
   }

   public final String getMinExtension() {
      return minExtension.get();
   }

   public final void setMinExtension(String minExt) {
      minExtension.set(minExt);
   }

   public IntegerProperty lineBreakPosProperty() {
      return lineBreakPos;
   }

   public final int getLineBreakPos() {
      return lineBreakPos.get();
   }

   public final void setLineBreakPos(int lneBrkPos) {
      lineBreakPos.set(lneBrkPos);
   }

   public StringProperty apiUrlProperty() {
      return apiUrl;
   }

   public final String getApiUrl() {
      return apiUrl.get();
   }

   public final void setApiUrl(String url) {
      apiUrl.set(url);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + lineBreakPos.get();
      result = prime * result + ((minExtension == null) ? 0 : minExtension.get().hashCode());
      result = prime * result + ((minSeparator.get() == null) ? 0 : minSeparator.get().toString().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      CssSettings other = (CssSettings) obj;
      if (lineBreakPos.get() != other.lineBreakPos.get())
         return false;
      if (minExtension.get() == null) {
         if (other.minExtension.get() != null)
            return false;
      } else if (!minExtension.get().equals(other.minExtension.get()))
         return false;
      if (minSeparator.get() == null) {
         if (other.minSeparator.get() != null)
            return false;
      } else if (!minSeparator.get().toString().equals(other.minSeparator.get().toString()))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "CssSettings [minSeparator=" + getMinSeparator() + ", minExtension=" + getMinExtension()
            + ", lineBreakPos=" + getLineBreakPos() + "]";
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeUTF(getMinSeparator().toString());
      out.writeUTF(getMinExtension());
      out.writeInt(getLineBreakPos());
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      MinificationSeparatorChar minSep = MinificationSeparatorChar.fromString(in.readUTF());
      String minExt = in.readUTF();
      Integer lnBrk = in.readInt();

      if (validateBeforeInit(minSep, minExt, lnBrk)) {
         init(minSep, minExt, lnBrk, null);
      } else {
         throw new IllegalArgumentException("Deserialized CSS settings must not be null");
      }
   }

   private boolean validateBeforeInit(MinificationSeparatorChar minSep, String minExt, Integer lnBrk) {
      return ((minSep != null) && StringUtils.isNotBlank(minExt) && (lnBrk != null));
   }
}
