package com.autominifier.model.settings.beans;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.property.BeanProperty;

public class SettingsBeanInfo extends SimpleBeanInfo {
   
   private static final Logger LOGGER = LogManager.getLogger(SettingsBeanInfo.class);

    private static final BeanDescriptor beanDescriptor = new BeanDescriptor(SettingsBeanInfo.class);
    private static PropertyDescriptor[] propDescriptors;

    static {
        beanDescriptor.setDisplayName("Settings Bean");
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
            propDescriptors = new PropertyDescriptor[6];
            try {
                propDescriptors[0] = new PropertyDescriptor("charset", Settings.class, "getCharset", "setCharset");
                propDescriptors[0].setDisplayName("Charset");
                propDescriptors[0].setShortDescription("Character set used to read CSS and JS files. The output file is encoded using the same character set.");
                propDescriptors[0].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.COMMON_SETTINGS.toString());
                
                propDescriptors[1] = new PropertyDescriptor("verbose", Settings.class, "isVerbose", "setVerbose");
                propDescriptors[1].setDisplayName("Verbose");
                propDescriptors[1].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.COMMON_SETTINGS.toString());
                propDescriptors[1].setHidden(true);
                
                propDescriptors[2] = new PropertyDescriptor("acceptLanguage", Settings.class, "getAcceptLanguage", "setAcceptLanguage");
                propDescriptors[2].setDisplayName("Accept Language");
                propDescriptors[2].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.COMMON_SETTINGS.toString());
                propDescriptors[2].setHidden(true);
                
                propDescriptors[3] = new PropertyDescriptor("userAgent", Settings.class, "getUserAgent", "setUserAgent");
                propDescriptors[3].setDisplayName("User Agent");
                propDescriptors[3].setValue(BeanProperty.CATEGORY_LABEL_KEY, Category.COMMON_SETTINGS.toString());
                propDescriptors[3].setHidden(true);
                
                propDescriptors[4] = new PropertyDescriptor("cssSettings", Settings.class, "getCssSettings", "setCssSettings");
                propDescriptors[4].setDisplayName("CSS Settings");
                
                propDescriptors[5] = new PropertyDescriptor("jsSettings", Settings.class, "getJsSettings", "setJsSettings");
                propDescriptors[5].setDisplayName("JS Settings");
            } catch (IntrospectionException e) {
               LOGGER.error("Oops! Something came up! Cause: ", e);
            }
        }
        return propDescriptors;
    }
}