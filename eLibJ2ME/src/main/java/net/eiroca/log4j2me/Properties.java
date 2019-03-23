/**
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License version 1.1, a copy of
 * which has been included with this distribution in the LICENSE.txt file.
 *
 */
package net.eiroca.log4j2me;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * My Properties for J2ME with App properties getting option.
 * @author Witmate
 */
public class Properties {

  /** The data. */
  private final Hashtable data = new Hashtable();

  /**
   * Instantiates a new properties.
   */
  public Properties() {
    //
  }

  /**
   * Gets the property.
   * 
   * @param key the key
   * @return the property
   */
  public String getProperty(final Object key) {
    return (String)data.get(key);
  }

  /**
   * Put.
   * 
   * @param key the key
   * @param val the val
   */
  public void put(final Object key, final Object val) {
    data.put(key, val);
  }

  /**
   * Property names.
   * 
   * @return the enumeration
   */
  public Enumeration propertyNames() {
    return data.keys();
  }

}
