package com.autominifier.model.settings.beans;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Settings implements Externalizable {

   private static final long serialVersionUID = -7886826304333798147L;

   // Common Settings Category
   private ObjectProperty<Charset>   charset;
   private transient BooleanProperty verbose;
   private transient StringProperty  acceptLanguage;
   private transient StringProperty  userAgent;

   // CSS Settings Category
   private CssSettings cssSettings;

   // JS Settings Group
   private JsSettings jsSettings;

   public Settings() {
      charset = new SimpleObjectProperty<>();
      verbose = new SimpleBooleanProperty();
      acceptLanguage = new SimpleStringProperty();
      userAgent = new SimpleStringProperty();

      cssSettings = new CssSettings();
      jsSettings = new JsSettings();
   }

   public void init(final Settings settings) {
      init(settings.getCharset(), settings.isVerbose(), settings.getAcceptLanguage(), settings.getUserAgent(),
            settings.getCssSettings(), settings.getJsSettings());
   }

   public void init(Charset charset, boolean verbose, String acceptLanguage, String userAgent, CssSettings cssSettings,
         JsSettings jsSettings) {
      setCharset(charset);
      setVerbose(verbose);
      setAcceptLanguage(acceptLanguage);
      setUserAgent(userAgent);
      setCssSettings(cssSettings);
      setJsSettings(jsSettings);
   }

   /* +++++++++++ Getters and Setters +++++++++++ */

   public ObjectProperty<Charset> charsetProperty() {
      return charset;
   }

   public final Charset getCharset() {
      return charset.get();
   }

   public final void setCharset(Charset c) {
      charset.set(c);
   }

   public BooleanProperty verboseProperty() {
      return verbose;
   }

   public final boolean isVerbose() {
      return verbose.get();
   }

   public final void setVerbose(boolean v) {
      verbose.set(v);
   }

   public StringProperty acceptLanguageProperty() {
      return acceptLanguage;
   }

   public final String getAcceptLanguage() {
      return acceptLanguage.get();
   }

   public final void setAcceptLanguage(String aL) {
      acceptLanguage.set(aL);
   }

   public StringProperty userAgentProperty() {
      return userAgent;
   }

   public final String getUserAgent() {
      return userAgent.get();
   }

   public final void setUserAgent(String uA) {
      userAgent.set(uA);
   }

   public CssSettings getCssSettings() {
      return cssSettings;
   }

   public void setCssSettings(CssSettings cssSettings) {
      this.cssSettings.init(cssSettings);
   }

   public JsSettings getJsSettings() {
      return jsSettings;
   }

   public void setJsSettings(JsSettings jsSettings) {
      this.jsSettings.init(jsSettings);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((charset.get() == null) ? 0 : charset.get().toString().hashCode());
      result = prime * result + ((cssSettings == null) ? 0 : cssSettings.hashCode());
      result = prime * result + ((jsSettings == null) ? 0 : jsSettings.hashCode());
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
      Settings other = (Settings) obj;
      if (charset.get() == null) {
         if (other.charset.get() != null)
            return false;
      } else if (!charset.get().toString().equals(other.charset.get().toString()))
         return false;
      if (cssSettings == null) {
         if (other.cssSettings != null)
            return false;
      } else if (!cssSettings.equals(other.cssSettings))
         return false;
      if (jsSettings == null) {
         if (other.jsSettings != null)
            return false;
      } else if (!jsSettings.equals(other.jsSettings))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Settings [charset=" + getCharset() + ", cssSettings=" + cssSettings + ", jsSettings=" + jsSettings + "]";
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeUTF(getCharset().toString());
      this.getCssSettings().writeExternal(out);
      this.getJsSettings().writeExternal(out);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      Charset c = Charset.fromString(in.readUTF());
      this.cssSettings.readExternal(in);
      this.jsSettings.readExternal(in);

      if (c != null) {
         setCharset(c);
      } else {
         throw new IllegalArgumentException("Deserialized settings must not be null");
      }
   }
}