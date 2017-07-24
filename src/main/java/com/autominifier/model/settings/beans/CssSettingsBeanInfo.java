package com.autominifier.model.settings.beans;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.property.BeanProperty;

public class CssSettingsBeanInfo extends SimpleBeanInfo {

   private static final Logger LOGGER = LogManager.getLogger(CssSettingsBeanInfo.class);

   private static final BeanDescriptor beanDescriptor = new BeanDescriptor(CssSettingsBeanInfo.class);
   private static PropertyDescriptor[] propDescriptors;

   static {
       beanDescriptor.setDisplayName("CSS Settings Bean");
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
           propDescriptors = new PropertyDescriptor[4];
           try {
               propDescriptors[0] = new PropertyDescriptor("minSeparator", CssSettings.class, "getMinSeparator", "setMinSeparator");
               propDescriptors[0].setDisplayName("CSS Compressing Separator");
               propDescriptors[0].setShortDescription("Character used before the CSS compressing extension (e.g. .min).");
               propDescriptors[0].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.CSS_SETTINGS.toString());
               
               propDescriptors[1] = new PropertyDescriptor("minExtension", CssSettings.class, "getMinExtension", "setMinExtension");
               propDescriptors[1].setDisplayName("CSS Compressing Extension");
               propDescriptors[1].setShortDescription("Suffix used to create the minified CSS filename (max 31 alphanumeric characters).");
               propDescriptors[1].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.CSS_SETTINGS.toString());
               
               propDescriptors[2] = new PropertyDescriptor("lineBreakPos", CssSettings.class, "getLineBreakPos", "setLineBreakPos");
               propDescriptors[2].setDisplayName("CSS Line Break Position");
               propDescriptors[2].setShortDescription("The linebreak option is used to split long lines after a specific column number (A negative value means no linebreaks).");
               propDescriptors[2].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.CSS_SETTINGS.toString());
               
               propDescriptors[3] = new PropertyDescriptor("apiUrl", CssSettings.class, "getApiUrl", "setApiUrl");
               propDescriptors[3].setDisplayName("CSS API URL");
               propDescriptors[3].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.CSS_SETTINGS.toString());
               propDescriptors[3].setHidden(true);
           } catch (IntrospectionException e) {
              LOGGER.error("Oops! Something came up! Cause: ", e);
           }
       }
       return propDescriptors;
   }
}