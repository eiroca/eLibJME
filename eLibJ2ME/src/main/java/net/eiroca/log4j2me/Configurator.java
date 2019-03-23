/**
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License version 1.1, a copy of
 * which has been included with this distribution in the LICENSE.txt file.
 */
package net.eiroca.log4j2me;

import java.util.Vector;
import javax.microedition.midlet.MIDlet;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.app.Pair;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;

/**
 * PropertyConfigurator for J2ME to get properties by getAppProperty.
 * 
 * @author Witmate
 */
public class Configurator {

  /** The Constant CATEGORY_LIST. */
  static final String CATEGORY_LIST = "log4j.categories";// Witmate: List all

  // category names

  /**
   * Load.
   * 
   * @param props the props
   * @param midlet the midlet
   * @return FormAppenders.
   */
  static public void load(final Properties props, final MIDlet midlet) {
    // Get CATEGORY_LIST
    final Vector categories = new Vector();
    final String catLst = midlet.getAppProperty(Configurator.CATEGORY_LIST);
    final StringTokenizer st = new StringTokenizer(catLst, ",");
    LogLog.debug(Configurator.CATEGORY_LIST + ":" + catLst);
    while (st.hasMoreTokens()) {
      categories.addElement(st.nextToken().trim());
    }
    LogLog.debug("categories:" + categories.toString());
    String temp;
    String key;
    // Get log4j.category.*
    for (int i = 0; categories.size() > i; i++) {
      key = PropertyConfigurator.CATEGORY_PREFIX + categories.elementAt(i);
      temp = midlet.getAppProperty(key);
      if (temp != null) {
        props.put(key, temp);
      }
    }
    LogLog.debug(props.toString());
  }

  /**
   * Load.
   * 
   * @param props the props
   * @param resPath the res path
   */
  static public void load(final Properties props, final String resPath) {
    final Pair[] pairs = BaseApp.readPairs(resPath, ':');
    Pair p;
    for (int i = 0; i < pairs.length; i++) {
      p = pairs[i];
      props.put(p.name, p.value);
    }
  }

}
