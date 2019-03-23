/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j.helpers;

/**
 * This class used to output log statements from within the log4j package.
 * <p>
 * Log4j components cannot make log4j logging calls. However, it is sometimes useful for the user to learn about what log4j is doing. You can enable log4j internal logging by defining the
 * <b>log4j.configDebug</b> variable.
 * <p>
 * All log4j internal debug calls go to <code>System.out</code> where as internal error messages are sent to <code>System.err</code>. All internal messages are prepended with the string "log4j: ".
 * @since 0.8.2
 * @author Ceki G&uuml;lc&uuml;
 */
// Modifiers: Witmate [Nov,2004: Modified for log4j2me]
public class LogLog {

  /**
   * Defining this value makes log4j print log4j-internal debug statements to <code>System.out</code>.
   * <p>
   * The value of this string is <b>log4j.debug</b>.
   * <p>
   * Note that the search for all option names is case sensitive.
   */
  public static final String DEBUG_KEY = "log4j.debug";

  protected static boolean debugEnabled = false;

  /**
   * In quietMode not even errors generate any output.
   */
  private static boolean quietMode = false;

  private static final String PREFIX = "log4j: ";
  private static final String ERR_PREFIX = "log4j:ERROR ";
  private static final String WARN_PREFIX = "log4j:WARN ";

  static {
    final String key = LogLog.getSystemProperty(LogLog.DEBUG_KEY, null);
    if (key != null) {
      LogLog.debugEnabled = OptionConverter.toBoolean(key, true);
    }
  }

  /**
   * Allows to enable/disable log4j internal logging.
   */
  static public void setInternalDebugging(final boolean enabled) {
    LogLog.debugEnabled = enabled;
  }

  /**
   * This method is used to output log4j internal debug statements. Output goes to <code>System.out</code>.
   */
  public static void debug(final String msg) {
    LogLog.debug(msg, null);
  }

  /**
   * This method is used to output log4j internal debug statements. Output goes to <code>System.out</code>.
   */
  public static void debug(final String msg, final Throwable t) {
    if (LogLog.debugEnabled && !LogLog.quietMode) {
      System.out.println(LogLog.PREFIX + msg);
      if (t != null) {
        t.printStackTrace();
      }
    }
  }

  /**
   * This method is used to output log4j internal error statements. There is no way to disable error statements. Output goes to <code>System.err</code>.
   */
  public static void error(final String msg) {
    LogLog.error(msg, null);
  }

  /**
   * This method is used to output log4j internal error statements. There is no way to disable error statements. Output goes to <code>System.err</code>.
   */
  public static void error(final String msg, final Throwable t) {
    if (LogLog.quietMode) { return; }

    System.err.println(LogLog.ERR_PREFIX + msg);
    if (t != null) {
      t.printStackTrace();
    }
  }

  public static String getSystemProperty(final String key, final String def) {
    try {
      String rtn = System.getProperty(key);
      if (null == rtn) {
        rtn = def;
      }
      return rtn;
    }
    catch (final Throwable e) {
      return def;
    }
  }

  /**
   * In quite mode no LogLog generates strictly no output, not even for errors.
   * @param quietMode A true for not
   */
  public static void setQuietMode(final boolean quietMode) {
    LogLog.quietMode = quietMode;
  }

  /**
   * This method is used to output log4j internal warning statements. There is no way to disable warning statements. Output goes to <code>System.err</code>.
   */
  public static void warn(final String msg) {
    LogLog.warn(msg, null);
  }

  /**
   * This method is used to output log4j internal warnings. There is no way to disable warning statements. Output goes to <code>System.err</code>.
   */
  public static void warn(final String msg, final Throwable t) {
    if (LogLog.quietMode) { return; }

    System.err.println(LogLog.WARN_PREFIX + msg);
    if (t != null) {
      t.printStackTrace();
    }
  }
}
