/**
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License version 1.1, a copy of
 * which has been included with this distribution in the LICENSE.txt file.
 */
/*
 * Created on 2004/11/11 For Witmate projects
 */
package net.eiroca.log4j2me;

import javax.microedition.lcdui.Form;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * The Class FormAppender.
 * 
 * @author Witmate
 */
public class FormAppender extends AppenderSkeleton {

  /** The m_form. */
  protected Form m_form;

  /** The m_title. */
  protected String m_title;
  /**
   * A string constant used in naming the option for setting the form title. Current value of this
   * string constant is <b>Title</b>.
   * <p>
   * Note that all option keys are case sensitive.
   */
  protected static final String TITLE_OPTION = "Title";

  /**
   * Instantiates a new form appender.
   */
  public FormAppender() {
    //
  }

  /**
   * Instantiates a new form appender.
   * 
   * @param layout the layout
   * @param form the form
   */
  public FormAppender(final Layout layout, final Form form) {
    m_form = form;
    m_form.setTitle(m_title);
    this.layout = layout;
  }

  /**
   * Gets the form.
   * 
   * @return the form
   */
  public Form getForm() {
    return m_form;
  }

  /**
   * Sets the form.
   * 
   * @param form the new form
   */
  public void setForm(final Form form) {
    m_form = form;
  }

  /**
   * Gets the title.
   * 
   * @return the title
   */
  public String getTitle() {
    return m_title;
  }

  /*
   * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
   */
  /* (non-Javadoc)
   * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
   */
  protected void append(final LoggingEvent event) {
    if (checkEntryConditions()) {
      final String log = layout.format(event);
      m_form.append(log);
      if (layout.ignoresThrowable()) {
        final String s = event.getThrowableStr();
        if (s != null) {
          m_form.append(s);
        }
      }
    }
  }

  /*
   * @see org.apache.log4j.Appender#close()
   */
  /* (non-Javadoc)
   * @see org.apache.log4j.Appender#close()
   */
  public void close() {
    m_form = null;
  }

  /**
   * Retuns the option names for this component, namely the string array {{@link #TITLE_OPTION} in
   * addition to the options of its super class {@link AppenderSkeleton}.
   * 
   * @return the option strings
   */
  public String[] getOptionStrings() {
    return new String[] {
        AppenderSkeleton.THRESHOLD_OPTION, FormAppender.TITLE_OPTION
    };
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.AppenderSkeleton#setOption(java.lang.String, java.lang.String)
   */
  public void setOption(final String key, String value) {
    if (value == null) { return; }
    super.setOption(key, value);
    if (key.toUpperCase().equals(FormAppender.TITLE_OPTION.toUpperCase())) {
      value = value.trim();
      m_title = value;
    }
  }

  /**
   * If title is no null, create form by appender self.
   */
  public void activateOptions() {
    if (m_title != null) {
      m_form = new Form(m_title);
    }
  }

  /**
   * This method determines if there is a sense in attempting to append.
   * <p>
   * It checks whether there is a set output target and also if there is a set layout. If these
   * checks fail, then the boolean value <code>false</code> is returned.
   * 
   * @return true, if successful
   */
  protected boolean checkEntryConditions() {
    if (m_form == null) {
      error("No output target set for \"" + name + "\".");
      return false;
    }

    if (layout == null) {
      error("No layout set for \"" + name + "\".");
      return false;
    }
    return true;
  }

}
