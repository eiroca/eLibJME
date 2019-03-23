/**
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License version 1.1, a copy of
 * which has been included with this distribution in the LICENSE.txt file.
 *
 */
package net.eiroca.log4j2me;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * The Class StringTokenizer.
 * 
 * @author Witmate
 */
public class StringTokenizer {

  /** The m_bool return token. */
  protected boolean m_boolReturnToken = false;

  /** The m_n current point. */
  protected int m_nCurrentPoint = 0;

  /** The m_str parsed. */
  protected String m_strParsed = "";

  /** The m_v tokenizer. */
  protected Vector m_vTokenizer = new Vector();

  /**
   * Instantiates a new string tokenizer.
   */
  public StringTokenizer() {
    //
  }

  /**
   * Instantiates a new string tokenizer.
   * 
   * @param strParsed the str parsed
   * @param boolReturnToken the bool return token
   */
  public StringTokenizer(final String strParsed, final boolean boolReturnToken) {
    if (strParsed != null) {
      m_strParsed = strParsed;
    }
    else {
      m_strParsed = "";
    }
    m_boolReturnToken = boolReturnToken;
  }

  /**
   * Instantiates a new string tokenizer.
   * 
   * @param strParsed the str parsed
   * @param strAToken the str a token
   */
  public StringTokenizer(final String strParsed, final String strAToken) {
    if (null != strParsed) {
      m_strParsed = strParsed;
    }
    else {
      m_strParsed = "";
    }
    if (null != strAToken) {
      m_vTokenizer.addElement(strAToken);
    }
  }

  /**
   * Adds the tokenizer.
   * 
   * @param strAToken the str a token
   * @return the int
   */
  public int addTokenizer(final String strAToken) {
    m_vTokenizer.addElement(strAToken);
    return m_vTokenizer.size();
  }

  /**
   * Checks for more tokens.
   * 
   * @return true, if successful
   */
  public boolean hasMoreTokens() {
    return (m_nCurrentPoint < ((null != m_strParsed) ? m_strParsed.length() : 0));
  }

  /**
   * Next token.
   * 
   * @return the string
   * @throws NoSuchElementException the no such element exception
   */
  public String nextToken() throws NoSuchElementException {
    if (!hasMoreTokens()) { throw new NoSuchElementException("There are not more token."); }
    String strRtn = m_strParsed.substring(m_nCurrentPoint);
    String strToken = "";
    String strTookToken = "";
    int nPnt = Integer.MAX_VALUE;
    int nTemp;
    for (int i = 0; m_vTokenizer.size() > i; i++) {
      strToken = (String)m_vTokenizer.elementAt(i);
      nTemp = m_strParsed.indexOf(strToken, m_nCurrentPoint);
      if ((nPnt > nTemp) && (-1 < nTemp)) {
        nPnt = nTemp;
        strTookToken = strToken;
        strRtn = m_strParsed.substring(m_nCurrentPoint, nPnt);
      }
    }
    if (nPnt < Integer.MAX_VALUE) {
      m_nCurrentPoint = nPnt + strTookToken.length();
    }
    else {
      m_nCurrentPoint = m_strParsed.length();
    }
    if (m_boolReturnToken && (nPnt < Integer.MAX_VALUE)) {
      strRtn += strTookToken;
    }
    return strRtn;
  }

}
