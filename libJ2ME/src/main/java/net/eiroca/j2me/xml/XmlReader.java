/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - GPL >= 3.0
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/
 */
/**
 * portion Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.eiroca.j2me.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import net.eiroca.j2me.app.BaseApp;

/**
 * A minimalistic XML pull parser, similar to kXML, but not supporting namespaces or legacy events.
 * If you need support for namespaces, or access to XML comments or processing instructions, please
 * use kXML(2) instead.
 */

public class XmlReader {

  /** Return value of getType before first call to next(). */
  public final static int START_DOCUMENT = 0;

  /** Signal logical end of xml document. */
  public final static int END_DOCUMENT = 1;

  /** Start tag was just read. */
  public final static int START_TAG = 2;

  /** End tag was just read. */
  public final static int END_TAG = 3;

  /** Text was just read. */
  public final static int TEXT = 4;

  /** The Constant CDSECT. */
  final static int CDSECT = 5;

  /** The Constant ENTITY_REF. */
  final static int ENTITY_REF = 6;

  /** The Constant UNEXPECTED_EOF. */
  static final private String UNEXPECTED_EOF = "Unexpected EOF";

  /** The Constant LEGACY. */
  static final private int LEGACY = 999;

  // general

  /** The relaxed. */
  public boolean relaxed;

  /** The entity map. */
  private final Hashtable entityMap;

  /** The depth. */
  private int depth;

  /** The element stack. */
  private String[] elementStack = new String[4];

  // source
  /** The reader. */
  private final Reader reader;

  /** The src buf. */
  private final char[] srcBuf = new char[Runtime.getRuntime().freeMemory() >= 1048576 ? 8192 : 128];

  /** The src pos. */
  private int srcPos;

  /** The src count. */
  private int srcCount;

  /** The eof. */
  private boolean eof;

  /** The line. */
  private int line;

  /** The column. */
  private int column;

  /** The peek0. */
  private int peek0;

  /** The peek1. */
  private int peek1;

  // txtbuffer
  /** The txt buf. */
  private char[] txtBuf = new char[128];

  /** The txt pos. */
  private int txtPos;

  // Event-related
  /** The type. */
  private int type;

  /** The text. */
  private String text;

  /** The is whitespace. */
  private boolean isWhitespace;

  /** The name. */
  private String name;

  /** The degenerated. */
  private boolean degenerated;

  /** The attribute count. */
  private int attributeCount;

  /** The attributes. */
  private String[] attributes = new String[16];

  /** The TYPES. */
  private final String[] TYPES = {
      "Start Document", "End Document", "Start Tag", "End Tag", "Text"
  };

  /**
   * Read.
   * 
   * @return the int
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final int read() throws IOException {
    final int r = peek0;
    peek0 = peek1;
    if (peek0 == -1) {
      eof = true;
      return r;
    }
    else if ((r == BaseApp.CR) || (r == BaseApp.LF)) {
      line++;
      column = 0;
      if ((r == BaseApp.LF) && (peek0 == BaseApp.CR)) {
        peek0 = 0;
      }
    }
    column++;
    if (srcPos >= srcCount) {
      srcCount = reader.read(srcBuf, 0, srcBuf.length);
      if (srcCount <= 0) {
        peek1 = -1;
        return r;
      }
      srcPos = 0;
    }
    peek1 = srcBuf[srcPos++];
    return r;
  }

  /**
   * Exception.
   * 
   * @param desc the desc
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final void exception(final String desc) throws IOException {
    throw new IOException(desc + " pos: " + getPositionDescription());
  }

  /**
   * Push.
   * 
   * @param c the c
   */
  private final void push(final int c) {
    if (c == 0) { return; }
    if (txtPos == txtBuf.length) {
      final char[] bigger = new char[txtPos * 4 / 3 + 4];
      System.arraycopy(txtBuf, 0, bigger, 0, txtPos);
      txtBuf = bigger;
    }
    txtBuf[txtPos++] = (char)c;
  }

  /**
   * Read.
   * 
   * @param c the c
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final void read(final char c) throws IOException {
    if (read() != c) {
      if (relaxed) {
        if (c <= 32) {
          skip();
          read();
        }
      }
      else {
        exception("expected: '" + c + "'");
      }
    }
  }

  /**
   * Skip.
   * 
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final void skip() throws IOException {
    while (!eof && (peek0 <= ' ')) {
      read();
    }
  }

  /**
   * Pop.
   * 
   * @param pos the pos
   * @return the string
   */
  private final String pop(final int pos) {
    final String result = new String(txtBuf, pos, txtPos - pos);
    txtPos = pos;
    return result;
  }

  /**
   * Read name.
   * 
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final String readName() throws IOException {
    final int pos = txtPos;
    int c = peek0;
    if (((c < 'a') || (c > 'z')) && ((c < 'A') || (c > 'Z')) && (c != '_') && (c != ':') && !relaxed) {
      exception("name expected");
    }
    do {
      push(read());
      c = peek0;
    }
    while (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || ((c >= '0') && (c <= '9')) || (c == '_') || (c == '-') || (c == ':') || (c == '.'));
    return pop(pos);
  }

  /**
   * Parses the legacy.
   * 
   * @param push the push
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final void parseLegacy(final boolean push) throws IOException {
    String req = "";
    int term;
    read(); // <
    int c = read();
    if (c == '?') {
      term = '?';
    }
    else if (c == '!') {
      if (peek0 == '-') {
        req = "--";
        term = '-';
      }
      else {
        req = "DOCTYPE";
        term = -1;
      }
    }
    else {
      if (c != '[') {
        exception("cantreachme: " + c);
      }
      req = "CDATA[";
      term = ']';
    }
    for (int i = 0; i < req.length(); i++) {
      read(req.charAt(i));
    }
    if (term == -1) {
      parseDoctype();
    }
    else {
      while (true) {
        if (eof) {
          exception(XmlReader.UNEXPECTED_EOF);
        }
        c = read();
        if (push) {
          push(c);
        }
        if (((term == '?') || (c == term)) && (peek0 == term) && (peek1 == '>')) {
          break;
        }
      }
      read();
      read();
      if (push && (term != '?')) {
        pop(txtPos - 1);
      }
    }
  }

  /**
   * precondition: &lt! consumed.
   * 
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final void parseDoctype() throws IOException {
    int nesting = 1;
    while (true) {
      final int i = read();
      switch (i) {
        case -1:
          exception(XmlReader.UNEXPECTED_EOF);
        case '<':
          nesting++;
          break;
        case '>':
          if ((--nesting) == 0) { return; }
          break;
      }
    }
  }

  /* precondition: &lt;/ consumed */

  /**
   * Parses the end tag.
   * 
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final void parseEndTag() throws IOException {
    read(); // '<'
    read(); // '/'
    name = readName();
    if ((depth == 0) && !relaxed) {
      exception("element stack empty");
    }
    if (name.equals(elementStack[depth - 1])) {
      depth--;
    }
    else if (!relaxed) {
      exception("expected: " + elementStack[depth]);
    }
    skip();
    read('>');
  }

  /**
   * Peek type.
   * 
   * @return the int
   */
  private final int peekType() {
    switch (peek0) {
      case -1:
        return XmlReader.END_DOCUMENT;
      case '&':
        return XmlReader.ENTITY_REF;
      case '<':
        switch (peek1) {
          case '/':
            return XmlReader.END_TAG;
          case '[':
            return XmlReader.CDSECT;
          case '?':
          case '!':
            return XmlReader.LEGACY;
          default:
            return XmlReader.START_TAG;
        }
      default:
        return XmlReader.TEXT;
    }
  }

  /**
   * Ensure capacity.
   * 
   * @param arr the arr
   * @param required the required
   * @return the string[]
   */
  private static final String[] ensureCapacity(final String[] arr, final int required) {
    if (arr.length >= required) { return arr; }
    final String[] bigger = new String[required + 16];
    System.arraycopy(arr, 0, bigger, 0, arr.length);
    return bigger;
  }

  /**
   * Sets name and attributes.
   * 
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final void parseStartTag() throws IOException {
    read(); // <
    name = readName();
    elementStack = XmlReader.ensureCapacity(elementStack, depth + 1);
    elementStack[depth++] = name;
    while (true) {
      skip();
      final int c = peek0;
      if (c == '/') {
        degenerated = true;
        read();
        skip();
        read('>');
        break;
      }
      if (c == '>') {
        read();
        break;
      }
      if (c == -1) {
        exception(XmlReader.UNEXPECTED_EOF);
      }
      final String attrName = readName();
      if (attrName.length() == 0) {
        exception("attr name expected");
      }
      skip();
      read('=');
      skip();
      int delimiter = read();
      if ((delimiter != '\'') && (delimiter != '"')) {
        if (!relaxed) {
          exception("<" + name + ">: invalid delimiter: " + (char)delimiter);
        }
        delimiter = ' ';
      }
      int i = (attributeCount++) << 1;
      attributes = XmlReader.ensureCapacity(attributes, i + 4);
      attributes[i++] = attrName;
      final int p = txtPos;
      pushText(delimiter);
      attributes[i] = pop(p);
      if (delimiter != ' ') {
        read(); // skip endquote
      }
    }
  }

  /**
   * result: isWhitespace; if the setName parameter is set, the name of the entity is stored in
   * "name".
   * 
   * @return true, if successful
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public final boolean pushEntity() throws IOException {
    read(); // &
    final int pos = txtPos;
    while (!eof && (peek0 != ';')) {
      push(read());
    }
    final String code = pop(pos);
    read();
    if ((code.length() > 0) && (code.charAt(0) == '#')) {
      final int c = (code.charAt(1) == 'x' ? Integer.parseInt(code.substring(2), 16) : Integer.parseInt(code.substring(1)));
      push(c);
      return c <= ' ';
    }
    String result = (String)entityMap.get(code);
    boolean whitespace = true;
    if (result == null) {
      result = "&" + code + ";";
    }
    for (int i = 0; i < result.length(); i++) {
      final char c = result.charAt(i);
      if (c > ' ') {
        whitespace = false;
      }
      push(c);
    }
    return whitespace;
  }

  /**
   * types: '<': parse to any token (for nextToken ()) '"': parse to quote ' ': parse to whitespace
   * or '>'.
   *
   * @param delimiter the delimiter
   * @return true, if successful
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final boolean pushText(final int delimiter) throws IOException {
    boolean whitespace = true;
    int next = peek0;
    while (!eof && (next != delimiter)) { // covers eof, '<', '"'
      if (delimiter == ' ') {
        if ((next <= ' ') || (next == '>')) {
          break;
        }
      }
      if (next == '&') {
        if (!pushEntity()) {
          whitespace = false;
        }
      }
      else {
        if (next > ' ') {
          whitespace = false;
        }
        push(read());
      }
      next = peek0;
    }
    return whitespace;
  }

  // --------------- public part starts here... ---------------
  /**
   * Instantiates a new xml reader.
   * 
   * @param reader the reader
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public XmlReader(final Reader reader) throws IOException {
    this.reader = reader;
    peek0 = reader.read();
    peek1 = reader.read();
    eof = peek0 == -1;
    entityMap = new Hashtable();
    entityMap.put("amp", "&");
    entityMap.put("apos", "'");
    entityMap.put("gt", ">");
    entityMap.put("lt", "<");
    entityMap.put("quot", "\"");
    line = 1;
    column = 1;
  }

  /**
   * Define character entity.
   * 
   * @param entity the entity
   * @param value the value
   */
  public void defineCharacterEntity(final String entity, final String value) {
    entityMap.put(entity, value);
  }

  /**
   * Gets the depth.
   * 
   * @return the depth
   */
  public int getDepth() {
    return depth;
  }

  /**
   * Gets the position description.
   * 
   * @return the position description
   */
  public String getPositionDescription() {
    final StringBuffer buf = new StringBuffer(type < TYPES.length ? TYPES[type] : "Other");
    buf.append(" @" + line + ":" + column + ": ");
    if ((type == XmlReader.START_TAG) || (type == XmlReader.END_TAG)) {
      buf.append('<');
      if (type == XmlReader.END_TAG) {
        buf.append('/');
      }
      buf.append(name);
      buf.append('>');
    }
    else if (isWhitespace) {
      buf.append("[whitespace]");
    }
    else {
      buf.append(getText());
    }
    return buf.toString();
  }

  /**
   * Gets the line number.
   * 
   * @return the line number
   */
  public int getLineNumber() {
    return line;
  }

  /**
   * Gets the column number.
   * 
   * @return the column number
   */
  public int getColumnNumber() {
    return column;
  }

  /**
   * Checks if is whitespace.
   * 
   * @return true, if is whitespace
   */
  public boolean isWhitespace() {
    return isWhitespace;
  }

  /**
   * Gets the text.
   * 
   * @return the text
   */
  public String getText() {
    if (text == null) {
      text = pop(0);
    }
    return text;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Checks if is empty element tag.
   * 
   * @return true, if is empty element tag
   */
  public boolean isEmptyElementTag() {
    return degenerated;
  }

  /**
   * Gets the attribute count.
   * 
   * @return the attribute count
   */
  public int getAttributeCount() {
    return attributeCount;
  }

  /**
   * Gets the attribute name.
   * 
   * @param index the index
   * @return the attribute name
   */
  public String getAttributeName(final int index) {
    if (index >= attributeCount) { throw new IndexOutOfBoundsException(); }
    return attributes[index << 1];
  }

  /**
   * Gets the attribute value.
   * 
   * @param index the index
   * @return the attribute value
   */
  public String getAttributeValue(final int index) {
    if (index >= attributeCount) { throw new IndexOutOfBoundsException(); }
    return attributes[(index << 1) + 1];
  }

  /**
   * Gets the attribute value.
   * 
   * @param name the name
   * @return the attribute value
   */
  public String getAttributeValue(final String name) {
    for (int i = (attributeCount << 1) - 2; i >= 0; i -= 2) {
      if (attributes[i].equals(name)) { return attributes[i + 1]; }
    }
    return null;
  }

  /**
   * Gets the type.
   * 
   * @return the type
   */
  public int getType() {
    return type;
  }

  /**
   * Next.
   * 
   * @return the int
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public int next() throws IOException {
    if (degenerated) {
      type = XmlReader.END_TAG;
      degenerated = false;
      depth--;
      return type;
    }
    txtPos = 0;
    isWhitespace = true;
    do {
      attributeCount = 0;
      name = null;
      text = null;
      type = peekType();
      switch (type) {
        case ENTITY_REF:
          isWhitespace &= pushEntity();
          type = XmlReader.TEXT;
          break;
        case START_TAG:
          parseStartTag();
          break;
        case END_TAG:
          parseEndTag();
          break;
        case END_DOCUMENT:
          break;
        case TEXT:
          isWhitespace &= pushText('<');
          break;
        case CDSECT:
          parseLegacy(true);
          isWhitespace = false;
          type = XmlReader.TEXT;
          break;
        default:
          parseLegacy(false);
      }
    }
    while ((type > XmlReader.TEXT) || ((type == XmlReader.TEXT) && (peekType() >= XmlReader.TEXT)));
    isWhitespace &= type == XmlReader.TEXT;
    return type;
  }

  // -----------------------------------------------------------------------------
  // utility methods to mak XML parsing easier ...

  /**
   * test if the current event is of the given type and if the name do match. null will match any
   * namespace and any name. If the current event is TEXT with isWhitespace()= true, and the
   * required type is not TEXT, next () is called prior to the test. If the test is not passed, an
   * exception is thrown. The exception text indicates the parser position, the expected event and
   * the current event (not meeting the requirement.
   * <p>
   * essentially it does this
   * 
   * <pre>
   * if (getType() == TEXT &amp;&amp; type != TEXT &amp;&amp; isWhitespace ())
   * next ();
   * if (type != getType
   * || (name != null &amp;&amp; !name.equals (getName ())
   * throw new XmlPullParserException ( &quot;....&quot;);
   * </pre>
   * 
   * @param type the type
   * @param name the name
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void require(final int type, final String name) throws IOException {
    if ((this.type == XmlReader.TEXT) && (type != XmlReader.TEXT) && isWhitespace()) {
      next();
    }
    if ((type != this.type) || ((name != null) && !name.equals(getName()))) {
      exception("expected: " + TYPES[type] + "/" + name);
    }
  }

  /**
   * If the current event is text, the value of getText is returned and next() is called. Otherwise,
   * an empty String ("") is returned. Useful for reading element content without needing to
   * performing an additional check if the element is empty.
   * <p>
   * essentially it does this
   * 
   * <pre>
   * if (getType != TEXT) return &quot;&quot;
   * String result = getText ();
   * next ();
   * return result;
   * </pre>
   * 
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */

  public String readText() throws IOException {
    if (type != XmlReader.TEXT) { return ""; }
    final String result = getText();
    next();
    return result;
  }

}
