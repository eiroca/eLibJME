/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */
//Modifiers:	Witmate [Nov,2004: Modified for log4j2me]
package org.apache.log4j.helpers;

import net.eiroca.log4j2me.Properties;

public class OptionConverter {

  public static boolean toBoolean(final String value, final boolean defaultVal) {
    if (value == null) { return defaultVal; }
    final String trimmedVal = value.trim();
    if ("true".equals(trimmedVal.toLowerCase())) { return true; }
    if ("false".equals(trimmedVal.toLowerCase())) { return false; }
    return defaultVal;
  }

  public static Object instantiateByKey(final Properties props, final String key, final Class superClass, final Object defaultValue) {
    // Get the value of the property in string form
    final String className = props.getProperty(key);
    if (className == null) {
      LogLog.error("Could not find value for " + key);
      return defaultValue;
    }
    // Trim className to avoid trailing spaces that cause problems.
    /*
     * Object obj=OptionConverter.instantiateByClassName(className.trim(),
     * superClass, defaultValue); LogLog.debug("OKOK:"+obj.toString()); return
     * obj;
     */
    return OptionConverter.instantiateByClassName(className.trim(), superClass, defaultValue);
  }

  /**
   * Instantiate an object given a class name. Check that the <code>className</code> is a subclass of <code>superClass</code>.
   */
  public static Object instantiateByClassName(final String className, final Class superClass, final Object defaultValue) {
    LogLog.debug("instantiateByClassName Here:" + className + "," + superClass.toString());
    if (className != null) {
      try {
        final Class classObj = Class.forName(className);
        if (!superClass.isAssignableFrom(classObj)) {
          LogLog.error("A \"" + className + "\" object is not assignable to a \"" + superClass.getName() + "\" object.");
        }
        /*
         * Object obj=classObj.newInstance(); LogLog.debug(obj.toString());
         * return obj;
         */
        return classObj.newInstance();
      }
      catch (final Exception e) {
        LogLog.error("Could not instantiate class [" + className + "].", e);
      }
    }
    return defaultValue;
  }

}
