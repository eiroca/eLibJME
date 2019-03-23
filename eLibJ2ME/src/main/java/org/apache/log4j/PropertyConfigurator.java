/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

// Contibutors: "Luke Blanshard" <Luke@quiq.com>
//              "Mark DONSZELMANN" <Mark.Donszelmann@cern.ch>
//              Anders Kristensen <akristensen@dynamicsoft.com>
// Modifiers:	Witmate [Nov,2004: Modified for log4j2me]
package org.apache.log4j;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.eiroca.log4j2me.Properties;
import net.eiroca.log4j2me.StringTokenizer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.OptionHandler;

/**
 * Allows the log4j configuration from an external file. See <b> {@link #doConfigure(String, Hierarchy)}</b> for the expected format.
 * <p>
 * It is sometimes useful to see how log4j is reading configuration files. You can enable log4j internal logging by defining the <b>log4j.debug </b> variable.
 * <P>
 * At the initialization of the Category class, the file <b>log4j.properties </b> will be searched from the search path used to load classes. If the file can be found, then it will be fed to the
 * {@link PropertyConfigurator#configure(java.net.URL)}method.
 * @author Ceki G&uuml;lc&uuml;, Witmate
 * @since log4jME 1.0
 */
public class PropertyConfigurator {

  /**
   * Used internally to keep track of configured appenders.
   */
  protected Hashtable registry = new Hashtable(11);
  static final public String CATEGORY_PREFIX = "log4j.category.";
  static final public String ADDITIVITY_PREFIX = "log4j.additivity.";
  static final public String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
  static final public String APPENDER_PREFIX = "log4j.appender.";
  static final public String CATEGORY_FACTORY_KEY = "log4j.categoryFactory";
  static final private String INHERITED = "inherited";
  static final private String INTERNAL_ROOT_NAME = "root";
  static protected Vector m_vFormAppenders = new Vector();

  /**
   * Read configuration options from <code>properties</code>.
   * <p>
   * The configuration file consists of staments in the format <code>key=value</code>.
   * <h3>Appender configuration</h3>
   * <p>
   * Appender configuration syntax is:
   * 
   * <pre>
   * 
   *                	 # For appender named &lt;i&gt;appenderName&lt;/i&gt;, set its class.
   *                	 # Note: The appender name can contain dots.
   *                	 log4j.appender.appenderName=fully.qualified.name.of.appender.class
   * 
   *                	 # Set appender specific options.
   *                	 log4j.appender.appenderName.option1=value1
   *                	 ...
   *                	 log4j.appender.appenderName.optionN=valueN
   * 
   * 
   * 
   * </pre>
   * 
   * For each named appender you can configure its {@link Layout}. The syntax for configuring an appender's layout is:
   * 
   * <pre>
   * 
   *                	 log.appender.appenderName.layout=fully.qualified.name.of.layout.class
   *                	 log.appender.appenderName.layout.option1=value1
   *                	 ....
   *                	 log.appender.appenderName.layout.optionN=valueN
   * 
   * 
   * 
   * </pre>
   * 
   * <h3>Configuring categories</h3>
   * <p>
   * The syntax for configuring the root category is:
   * 
   * <pre>
   * 
   *                	 log4j.rootCategory=[FATAL|ERROR|WARN|INFO|DEBUG], appenderName, appenderName, ...
   * 
   * 
   * 
   * </pre>
   * 
   * <p>
   * This syntax means that one of the strings values ERROR, WARN, INFO or DEBUG can be supplied followed by appender names separated by commas.
   * <p>
   * If one of the optional priority values ERROR, WARN, INFO or DEBUG is given, the root priority is set to the corresponding priority. If no priority value is specified, then the root priority
   * remains untouched.
   * <p>
   * The root category can be assigned multiple appenders.
   * <p>
   * Each <i>appenderName </i> (seperated by commas) will be added to the root category. The named appender is defined using the appender syntax defined above.
   * <p>
   * For non-root categories the syntax is almost the same:
   * 
   * <pre>
   * 
   *                	 log4j.category.category_name=[INHERITED|FATAL|ERROR|WARN|INFO|DEBUG], appenderName, appenderName, ...
   * 
   * 
   * 
   * </pre>
   * 
   * <p>
   * Thus, one of the usual priority values FATAL, ERROR, WARN, INFO, or DEBUG can be optionally specified. For any any of these values the named category is assigned the corresponding priority. In
   * addition however, the value INHERITED can be optionally specified which means that named category should inherit its priority from the category hierarchy.
   * <p>
   * If no priority value is supplied, then the priority of the named category remains untouched.
   * <p>
   * By default categories inherit their priority from the hierarchy. However, if you set the priority of a category and later decide that that category should inherit its priority, then you should
   * specify INHERITED as the value for the priority value.
   * <p>
   * Similar to the root category syntax, each <i>appenderName </i> (seperated by commas) will be attached to the named category.
   * <p>
   * See the <a href="../../manual.html#additivity">appender additivity rule </a> in the user manual for the meaning of the <code>additivity</code> flag.
   * <h3>Example</h3>
   * <p>
   * An example configuration is given below. Other configuration file examples are given in {@link org.apache.log4j.examples.Sort}class documentation.
   * 
   * <pre>
   * 
   * 
   *                	 # Set options for appender named &quot;A1&quot;.
   *                	 # Appender &quot;A1&quot; will be a FileAppender
   *                	 log4j.appender.A1=org.apache.log4j.FileAppender
   * 
   *                	 # It will send its output to System.out
   *                	 log4j.appender.A1.File=System.out
   * 
   *                	 # A1's layout is a PatternLayout, using the conversion pattern
   *                	 # &lt;b&gt;%-4r %-5p %c{2} - %m%n&lt;/b&gt;. Thus, the log output will
   *                	 # include the relative time since the start of the application in
   *                	 # milliseconds, followed by the priority of the log request,
   *                	 # followed by the two rightmost components of the category name
   *                	 # and finally the message itself.
   *                	 # Refer to the documentation of {@link PatternLayout} for further information
   *                	 # on the syntax of the ConversionPattern key.
   *                	 log4j.appender.A1.layout=org.apache.log4j.PatternLayout
   *                	 log4j.appender.A1.layout.ConversionPattern=%-4r %-5p %c{2} - %m%n
   * 
   *                	 # Set options for appender named &quot;A2&quot;
   *                	 # A2 should be a
   * <code>
   * FileAppender
   * </code>
   *                 printing to the
   *                	 # file
   * <code>
   * temp
   * </code>
   *                .
   *                	 log4j.appender.A2=org.apache.log4j.FileAppender
   *                	 log4j.appender.A2.File=temp
   * 
   *                	 log4j.appender.A2.layout=org.apache.log4j.PatternLayout
   *                	 log4j.appender.A2.layout.ConversionPattern=%-4r %-5p %c - %m%n
   * 
   *                	 # Root category set to DEBUG using the A2 appender defined above.
   *                	 log4j.rootCategory=DEBUG, A2
   * 
   *                	 # Category definions:
   *                	 # The SECURITY category inherits is priority from root. However, it's output
   *                	 # will go to A1 appender defined above. It's additivity is non-cumulative.
   *                	 log4j.category.SECURITY=INHERIT, A1
   *                	 log4j.additivity.SECURITY=false
   * 
   *                	 # Only warnings or above will be logged for the category &quot;SECURITY.access&quot;.
   *                	 # Output will go to A1.
   *                	 log4j.category.SECURITY.access=WARN
   * 
   * 
   *                	 # The category &quot;class.of.the.day&quot; inherits its priority from the
   *                	 # category hierrarchy.  Output will go to the appender's of the root
   *                	 # category, A2 in this case.
   *                	 log4j.category.class.of.the.day=INHERIT
   * 
   * 
   * 
   * </pre>
   * 
   * <p>
   * Refer to the <b>setOption </b> method in each Appender and Layout for class specific options.
   * <p>
   * Use the <code>#</code> or <code>!</code> characters at the beginning of a line for comments.
   * <p>
   * @param configFileName The name of the configuration file where the configuration information is stored.
   * @return MIDLet formappenders. Extended for log4j2me.
   */
  static public Vector configure(final Properties properties) {
    new PropertyConfigurator().doConfigure(properties, Category.defaultHierarchy);
    return PropertyConfigurator.m_vFormAppenders;
  }

  /**
   * Read configuration options from <code>properties</code>. See {@link #doConfigure(String, Hierarchy)}for the expected format.
   * @return MIDLet formappenders. Extended for log4j2me.
   */
  public Vector doConfigure(final Properties properties, final Hierarchy hierarchy) {
    LogLog.debug(hierarchy.toString());
    final String value = properties.getProperty(LogLog.DEBUG_KEY);
    if (value != null) {
      LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
    }
    configureRootCategory(properties, hierarchy);
    parseCats(properties, hierarchy);
    // We don't want to hold references to appenders preventing their
    // garbage collection.
    registry.clear();
    return PropertyConfigurator.m_vFormAppenders;
  }

  // -------------------------------------------------------------------------------
  // Internal stuff
  // -------------------------------------------------------------------------------

  void configureOptionHandler(final OptionHandler oh, final String prefix, final Properties props) {
    final String[] options = oh.getOptionStrings();
    if (options == null) { return; }

    String value;
    for (int i = 0; i < options.length; i++) {
      value = props.getProperty(prefix + options[i]);
      LogLog.debug("Option " + options[i] + "=[" + (value == null ? "null" : value) + "].");
      // Some option handlers assume that null value are not passed to
      // them.
      // So don't remove this check
      if (value != null) {
        oh.setOption(options[i], value);
      }
    }
    oh.activateOptions();
  }

  void configureRootCategory(final Properties props, final Hierarchy hierarchy) {
    final String value = props.getProperty(PropertyConfigurator.ROOT_CATEGORY_PREFIX);
    if (value == null) {
      LogLog.debug("Could not find root category information. Is this OK?");
    }
    else {
      final Category root = hierarchy.getRoot();
      LogLog.debug(hierarchy.toString());
      LogLog.debug(root.toString());
      synchronized (root) {
        parseCategory(props, root, PropertyConfigurator.ROOT_CATEGORY_PREFIX, PropertyConfigurator.INTERNAL_ROOT_NAME, value);
      }
    }
  }

  /**
   * Parse non-root elements, such non-root categories and renderers.
   */
  protected void parseCats(final Properties props, final Hierarchy hierarchy) {
    final Enumeration enumeration = props.propertyNames();
    while (enumeration.hasMoreElements()) {
      final String key = (String) enumeration.nextElement();
      if (key.startsWith(PropertyConfigurator.CATEGORY_PREFIX)) {
        final String categoryName = key.substring(PropertyConfigurator.CATEGORY_PREFIX.length());
        final String value = props.getProperty(key);
        final Category cat = hierarchy.getInstance(categoryName);
        synchronized (cat) {
          parseCategory(props, cat, key, categoryName, value);
          parseAdditivityForCategory(props, cat, categoryName);
        }
      }
    }
  }

  /**
   * Parse the additivity option for a non-root category.
   */
  void parseAdditivityForCategory(final Properties props, final Category cat, final String categoryName) {
    final String value = props.getProperty(PropertyConfigurator.ADDITIVITY_PREFIX + categoryName);
    LogLog.debug("Handling " + PropertyConfigurator.ADDITIVITY_PREFIX + categoryName + "=[" + value + "]");
    // touch additivity only if necessary
    if ((value != null) && (!value.equals(""))) {
      final boolean additivity = OptionConverter.toBoolean(value, true);
      LogLog.debug("Setting additivity for \"" + categoryName + "\" to " + additivity);
      cat.setAdditivity(additivity);
    }

  }

  /**
   * This method must work for the root category as well.
   */
  void parseCategory(final Properties props, final Category cat, final String optionKey, final String catName, final String value) {

    LogLog.debug("Parsing for [" + catName + "] with value=[" + value + "]." + cat.toString());
    // We must skip over ',' but not white space
    final StringTokenizer st = new StringTokenizer(value, ",");

    // If value is not in the form ", appender.." or "", then we should set
    // the priority of the category.

    if (!(value.startsWith(",") || value.equals(""))) {

      // just to be on the safe side...
      if (!st.hasMoreTokens()) { return; }

      final String priorityStr = st.nextToken();
      LogLog.debug("Priority token is [" + priorityStr + "].");

      // If the priority value is inherited, set category priority value
      // to
      // null. We also check that the user has not specified inherited for
      // the
      // root category.
      if (priorityStr.toUpperCase().equals(PropertyConfigurator.INHERITED.toUpperCase()) && !catName.equals(PropertyConfigurator.INTERNAL_ROOT_NAME)) {
        cat.setPriority(null);
      }
      else {
        cat.setPriority(Priority.toPriority(priorityStr));
      }
      LogLog.debug("Category " + catName + " set to " + cat.getPriority());
    }

    // Remove all existing appenders. They will be reconstructed below.
    cat.removeAllAppenders();

    Appender appender;
    String appenderName;
    while (st.hasMoreTokens()) {
      appenderName = st.nextToken().trim();
      if ((appenderName == null) || appenderName.equals(",")) {
        continue;
      }
      LogLog.debug("Parsing appender named \"" + appenderName + "\".");
      appender = parseAppender(props, appenderName);
      if (appender != null) {
        cat.addAppender(appender);
      }
    }
  }

  Appender parseAppender(final Properties props, final String appenderName) {
    Appender appender = registryGet(appenderName);
    try {
      if ((appender != null)) {
        LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
        return appender;
      }
      // Appender was not previously initialized.
      final String prefix = PropertyConfigurator.APPENDER_PREFIX + appenderName;
      final String layoutPrefix = prefix + ".layout";

      appender = (Appender) OptionConverter.instantiateByKey(props, prefix, Class.forName("org.apache.log4j.Appender"), null);
      if (appender == null) {
        LogLog.error("Could not instantiate appender named \"" + appenderName + "\".");
        return null;
      }
      appender.setName(appenderName);

      if (appender instanceof OptionHandler) {
        configureOptionHandler((OptionHandler) appender, prefix + ".", props);
        LogLog.debug("Parsed \"" + appenderName + "\" options.");

        final Layout layout = (Layout) OptionConverter.instantiateByKey(props, layoutPrefix, Class.forName("org.apache.log4j.Layout"), null);

        if (layout != null) {
          appender.setLayout(layout);
          LogLog.debug("Parsing layout options for \"" + appenderName + "\".");
          configureOptionHandler(layout, layoutPrefix + ".", props);
          LogLog.debug("End of parsing for \"" + appenderName + "\".");
        }
      }

      if (appender instanceof net.eiroca.log4j2me.FormAppender) {
        PropertyConfigurator.m_vFormAppenders.addElement(appender);
      }

      registryPut(appender);
    }
    catch (final ClassNotFoundException e) {
      LogLog.error("Could not find class [" + e.getMessage() + "].", e);
    }
    catch (final Exception e) {// Witmate
      LogLog.error(e.getClass().toString());
      e.printStackTrace();
    }
    return appender;
  }

  void registryPut(final Appender appender) {
    registry.put(appender.getName(), appender);
  }

  Appender registryGet(final String name) {
    return (Appender) registry.get(name);
  }
}
