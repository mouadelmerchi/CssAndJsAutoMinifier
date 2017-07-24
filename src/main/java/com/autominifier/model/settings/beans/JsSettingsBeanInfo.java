package com.autominifier.model.settings.beans;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.property.BeanProperty;

public class JsSettingsBeanInfo extends SimpleBeanInfo {

   private static final Logger LOGGER = LogManager.getLogger(JsSettingsBeanInfo.class);

   private static final BeanDescriptor beanDescriptor = new BeanDescriptor(JsSettingsBeanInfo.class);
   private static PropertyDescriptor[] propDescriptors;

   static {
       beanDescriptor.setDisplayName("JS Settings Bean");
   }

   @Override
   public BeanDescriptor getBeanDescriptor() {
       return beanDescriptor;
   }

   @Override
   public int getDefaultPropertyIndex() {
       return 0;
   }

   @Override
   public PropertyDescriptor[] getPropertyDescriptors() {
       if (propDescriptors == null) {
           propDescriptors = new PropertyDescriptor[7];
           try {
              propDescriptors[0] = new PropertyDescriptor("minSeparator", JsSettings.class, "getMinSeparator", "setMinSeparator");
              propDescriptors[0].setDisplayName("JS Compressing Separator");
              propDescriptors[0].setShortDescription("Character used before the JS compressing extension (e.g. .min).");
              propDescriptors[0].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.JS_SETTINGS.toString());
              
              propDescriptors[1] = new PropertyDescriptor("minExtension", JsSettings.class, "getMinExtension", "setMinExtension");
              propDescriptors[1].setDisplayName("JS Compressing Extension");
              propDescriptors[1].setShortDescription("Suffix used to create the minified JS filename (max 31 alphanumeric characters).");
              propDescriptors[1].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.JS_SETTINGS.toString());
              
              propDescriptors[2] = new PropertyDescriptor("lineBreakPos", JsSettings.class, "getLineBreakPos", "setLineBreakPos");
              propDescriptors[2].setDisplayName("JS Line Break Position");
            propDescriptors[2].setShortDescription(
                  "The linebreak option is used to split long lines after a specific column number (A negative value means no linebreaks).");
              propDescriptors[2].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.JS_SETTINGS.toString());
              
              propDescriptors[3] = new PropertyDescriptor("apiUrl", JsSettings.class, "getApiUrl", "setApiUrl");
              propDescriptors[3].setDisplayName("JS API URL");
              propDescriptors[3].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.JS_SETTINGS.toString());
              propDescriptors[3].setHidden(true);
              
              propDescriptors[4] = new PropertyDescriptor("munge", JsSettings.class, "isMunge", "setMunge");
              propDescriptors[4].setDisplayName("JS Munge");
              propDescriptors[4].setShortDescription("Minify and obfuscate javascript symbols.");
              propDescriptors[4].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.JS_SETTINGS.toString());
              
              propDescriptors[5] = new PropertyDescriptor("preserveAllSemiColons", JsSettings.class, "isPreserveAllSemiColons", "setPreserveAllSemiColons");
              propDescriptors[5].setDisplayName("JS Preserve Semicolons");
              propDescriptors[5].setShortDescription("Preserve unnecessary javascript semicolons (such as right before a '}').");
              propDescriptors[5].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.JS_SETTINGS.toString());
              
              propDescriptors[6] = new PropertyDescriptor("disableOptimization", JsSettings.class, "isDisableOptimization", "setDisableOptimization");
              propDescriptors[6].setDisplayName("JS Disable Optimization");
              propDescriptors[6].setShortDescription("Disable all the built-in micro optimizations for javascript.");
              propDescriptors[6].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.JS_SETTINGS.toString());
           } catch (IntrospectionException e) {
              LOGGER.error("Oops! Something came up! Cause: ", e);
           }
       }
       return propDescriptors;
   }
}