package org.apache.log4j.spi;

/**
 * A string based interface to configure package components.
 * @author Ceki G&uuml;lc&uuml;, Witmate
 */
// Modifiers: Witmate [Nov,2004: Modified for log4j2me]
public interface OptionHandler {

  /**
   * Activate the options that were previously set with calls to option setters.
   * <p>
   * This allows to defer activiation of the options until all options have been set. This is required for components which have related options that remain ambigous until all are set.
   */
  public void activateOptions();

  public String[] getOptionStrings();

  public void setOption(String key, String value);
}
