/* Copyright (c) 2002,2003,2004 Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

// Contributors: Bjorn Aadland, Chris Bartley, Nicola Fankhauser,
//               Victor Havin,  Christian Kurzke, Bogdan Onoiu,
//                Elias Ross, Jain Sanjay, David Santoro.
package org.kxml2.wap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class WbxmlParser implements XmlPullParser {

  static final String HEX_DIGITS = "0123456789abcdef";

  /**
   * Parser event type for Wbxml-specific events. The Wbxml event code can be accessed with getWapCode()
   */

  public static final int WAP_EXTENSION = 64;

  static final private String UNEXPECTED_EOF = "Unexpected EOF";
  static final private String ILLEGAL_TYPE = "Wrong event type";

  private InputStream in;

  private final int TAG_TABLE = 0;
  private final int ATTR_START_TABLE = 1;
  private final int ATTR_VALUE_TABLE = 2;

  private String[] attrStartTable;
  private String[] attrValueTable;
  private String[] tagTable;
  private byte[] stringTable;
  private Hashtable cacheStringTable = null;
  private boolean processNsp;

  private int depth;
  private String[] elementStack = new String[16];
  private String[] nspStack = new String[8];
  private int[] nspCounts = new int[4];

  private int attributeCount;
  private String[] attributes = new String[16];
  private int nextId = -2;

  private final Vector tables = new Vector();

  private int publicIdentifierId;

  // StartTag current;
  // ParseEvent next;

  private String prefix;
  private String namespace;
  private String name;
  private String text;

  private Object wapExtensionData;
  private int wapCode;

  private int type;

  private boolean degenerated;
  private boolean isWhitespace;
  private String encoding;
  public int version;

  public boolean getFeature(final String feature) {
    if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(feature)) { return processNsp; }
    return false;
  }

  public String getInputEncoding() {
    return encoding;
  }

  public void defineEntityReplacementText(final String entity, final String value) throws XmlPullParserException {

    // just ignore, has no effect
  }

  public Object getProperty(final String property) {
    return null;
  }

  public int getNamespaceCount(final int depth) {
    if (depth > this.depth) { throw new IndexOutOfBoundsException(); }
    return nspCounts[depth];
  }

  public String getNamespacePrefix(final int pos) {
    return nspStack[pos << 1];
  }

  public String getNamespaceUri(final int pos) {
    return nspStack[(pos << 1) + 1];
  }

  public String getNamespace(final String prefix) {

    if ("xml".equals(prefix)) { return "http://www.w3.org/XML/1998/namespace"; }
    if ("xmlns".equals(prefix)) { return "http://www.w3.org/2000/xmlns/"; }

    for (int i = (getNamespaceCount(depth) << 1) - 2; i >= 0; i -= 2) {
      if (prefix == null) {
        if (nspStack[i] == null) { return nspStack[i + 1]; }
      }
      else if (prefix.equals(nspStack[i])) { return nspStack[i + 1]; }
    }
    return null;
  }

  public int getDepth() {
    return depth;
  }

  public String getPositionDescription() {

    final StringBuffer buf = new StringBuffer(type < XmlPullParser.TYPES.length ? XmlPullParser.TYPES[type] : "unknown");
    buf.append(' ');

    if ((type == XmlPullParser.START_TAG) || (type == XmlPullParser.END_TAG)) {
      if (degenerated) {
        buf.append("(empty) ");
      }
      buf.append('<');
      if (type == XmlPullParser.END_TAG) {
        buf.append('/');
      }

      if (prefix != null) {
        buf.append("{" + namespace + "}" + prefix + ":");
      }
      buf.append(name);

      final int cnt = attributeCount << 2;
      for (int i = 0; i < cnt; i += 4) {
        buf.append(' ');
        if (attributes[i + 1] != null) {
          buf.append("{" + attributes[i] + "}" + attributes[i + 1] + ":");
        }
        buf.append(attributes[i + 2] + "='" + attributes[i + 3] + "'");
      }

      buf.append('>');
    }
    else if (type == XmlPullParser.IGNORABLE_WHITESPACE) {
      ;
    }
    else if (type != XmlPullParser.TEXT) {
      buf.append(getText());
    }
    else if (isWhitespace) {
      buf.append("(whitespace)");
    }
    else {
      String text = getText();
      if (text.length() > 16) {
        text = text.substring(0, 16) + "...";
      }
      buf.append(text);
    }

    return buf.toString();
  }

  public int getLineNumber() {
    return -1;
  }

  public int getColumnNumber() {
    return -1;
  }

  public boolean isWhitespace() throws XmlPullParserException {
    if ((type != XmlPullParser.TEXT) && (type != XmlPullParser.IGNORABLE_WHITESPACE) && (type != XmlPullParser.CDSECT)) {
      exception(WbxmlParser.ILLEGAL_TYPE);
    }
    return isWhitespace;
  }

  public String getText() {
    return text;
  }

  public char[] getTextCharacters(final int[] poslen) {
    if (type >= XmlPullParser.TEXT) {
      poslen[0] = 0;
      poslen[1] = text.length();
      final char[] buf = new char[text.length()];
      text.getChars(0, text.length(), buf, 0);
      return buf;
    }

    poslen[0] = -1;
    poslen[1] = -1;
    return null;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
  }

  public String getPrefix() {
    return prefix;
  }

  public boolean isEmptyElementTag() throws XmlPullParserException {
    if (type != XmlPullParser.START_TAG) {
      exception(WbxmlParser.ILLEGAL_TYPE);
    }
    return degenerated;
  }

  public int getAttributeCount() {
    return attributeCount;
  }

  public String getAttributeType(final int index) {
    return "CDATA";
  }

  public boolean isAttributeDefault(final int index) {
    return false;
  }

  public String getAttributeNamespace(final int index) {
    if (index >= attributeCount) { throw new IndexOutOfBoundsException(); }
    return attributes[index << 2];
  }

  public String getAttributeName(final int index) {
    if (index >= attributeCount) { throw new IndexOutOfBoundsException(); }
    return attributes[(index << 2) + 2];
  }

  public String getAttributePrefix(final int index) {
    if (index >= attributeCount) { throw new IndexOutOfBoundsException(); }
    return attributes[(index << 2) + 1];
  }

  public String getAttributeValue(final int index) {
    if (index >= attributeCount) { throw new IndexOutOfBoundsException(); }
    return attributes[(index << 2) + 3];
  }

  public String getAttributeValue(final String namespace, final String name) {

    for (int i = (attributeCount << 2) - 4; i >= 0; i -= 4) {
      if (attributes[i + 2].equals(name) && ((namespace == null) || attributes[i].equals(namespace))) { return attributes[i + 3]; }
    }

    return null;
  }

  public int getEventType() throws XmlPullParserException {
    return type;
  }

  // TODO: Reuse resolveWapExtension here? Raw Wap extensions would still be
  // accessible
  // via nextToken(); ....?

  public int next() throws XmlPullParserException, IOException {

    isWhitespace = true;
    int minType = 9999;

    while (true) {

      final String save = text;

      nextImpl();

      if (type < minType) {
        minType = type;
      }

      if (minType > XmlPullParser.CDSECT) {
        continue; // no "real" event so far
      }

      if (minType >= XmlPullParser.TEXT) { // text, see if accumulate

        if (save != null) {
          text = text == null ? save : save + text;
        }

        switch (peekId()) {
          case Wbxml.ENTITY:
          case Wbxml.STR_I:
          case Wbxml.STR_T:
          case Wbxml.LITERAL:
          case Wbxml.LITERAL_C:
          case Wbxml.LITERAL_A:
          case Wbxml.LITERAL_AC:
            continue;
        }
      }

      break;
    }

    type = minType;

    if (type > XmlPullParser.TEXT) {
      type = XmlPullParser.TEXT;
    }

    return type;
  }

  public int nextToken() throws XmlPullParserException, IOException {

    isWhitespace = true;
    nextImpl();
    return type;
  }

  public int nextTag() throws XmlPullParserException, IOException {

    next();
    if ((type == XmlPullParser.TEXT) && isWhitespace) {
      next();
    }

    if ((type != XmlPullParser.END_TAG) && (type != XmlPullParser.START_TAG)) {
      exception("unexpected type");
    }

    return type;
  }

  public String nextText() throws XmlPullParserException, IOException {
    if (type != XmlPullParser.START_TAG) {
      exception("precondition: START_TAG");
    }

    next();

    String result;

    if (type == XmlPullParser.TEXT) {
      result = getText();
      next();
    }
    else {
      result = "";
    }

    if (type != XmlPullParser.END_TAG) {
      exception("END_TAG expected");
    }

    return result;
  }

  public void require(final int type, final String namespace, final String name) throws XmlPullParserException, IOException {

    if ((type != this.type) || ((namespace != null) && !namespace.equals(getNamespace())) || ((name != null) && !name.equals(getName()))) {
      exception("expected: " + (type == WbxmlParser.WAP_EXTENSION ? "WAP Ext." : (XmlPullParser.TYPES[type] + " {" + namespace + "}" + name)));
    }
  }

  public void setInput(final Reader reader) throws XmlPullParserException {
    exception("InputStream required");
  }

  public void setInput(final InputStream in, final String enc) throws XmlPullParserException {

    this.in = in;

    try {
      version = readByte();
      publicIdentifierId = readInt();

      if (publicIdentifierId == 0) {
        readInt();
      }

      final int charset = readInt(); // skip charset

      if (null == enc) {
        switch (charset) {
          case 4:
            encoding = "ISO-8859-1";
            break;
          case 106:
            encoding = "UTF-8";
            break;
          // add more if you need them
          // http://www.iana.org/assignments/character-sets
          // case MIBenum: encoding = Name break;
          default:
            throw new UnsupportedEncodingException("" + charset);
        }
      }
      else {
        encoding = enc;
      }

      final int strTabSize = readInt();
      stringTable = new byte[strTabSize];

      int ok = 0;
      while (ok < strTabSize) {
        final int cnt = in.read(stringTable, ok, strTabSize - ok);
        if (cnt <= 0) {
          break;
        }
        ok += cnt;
      }

      selectPage(0, true);
      selectPage(0, false);
    }
    catch (final IOException e) {
      exception("Illegal input format");
    }
  }

  public void setFeature(final String feature, final boolean value) throws XmlPullParserException {
    if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(feature)) {
      processNsp = value;
    }
    else {
      exception("unsupported feature: " + feature);
    }
  }

  public void setProperty(final String property, final Object value) throws XmlPullParserException {
    throw new XmlPullParserException("unsupported property: " + property);
  }

  // ---------------------- private / internal methods

  private final boolean adjustNsp() throws XmlPullParserException {

    boolean any = false;

    for (int i = 0; i < attributeCount << 2; i += 4) {
      // * 4 - 4; i >= 0; i -= 4) {

      String attrName = attributes[i + 2];
      final int cut = attrName.indexOf(':');
      String prefix;

      if (cut != -1) {
        prefix = attrName.substring(0, cut);
        attrName = attrName.substring(cut + 1);
      }
      else if (attrName.equals("xmlns")) {
        prefix = attrName;
        attrName = null;
      }
      else {
        continue;
      }

      if (!prefix.equals("xmlns")) {
        any = true;
      }
      else {
        final int j = (nspCounts[depth]++) << 1;

        nspStack = ensureCapacity(nspStack, j + 2);
        nspStack[j] = attrName;
        nspStack[j + 1] = attributes[i + 3];

        if ((attrName != null) && attributes[i + 3].equals("")) {
          exception("illegal empty namespace");
        }

        // prefixMap = new PrefixMap (prefixMap, attrName, attr.getValue ());

        // System.out.println (prefixMap);

        System.arraycopy(attributes, i + 4, attributes, i, ((--attributeCount) << 2) - i);

        i -= 4;
      }
    }

    if (any) {
      for (int i = (attributeCount << 2) - 4; i >= 0; i -= 4) {

        String attrName = attributes[i + 2];
        final int cut = attrName.indexOf(':');

        if (cut == 0) {
          throw new RuntimeException("illegal attribute name: " + attrName + " at " + this);
        }
        else if (cut != -1) {
          final String attrPrefix = attrName.substring(0, cut);

          attrName = attrName.substring(cut + 1);

          final String attrNs = getNamespace(attrPrefix);

          if (attrNs == null) { throw new RuntimeException("Undefined Prefix: " + attrPrefix + " in " + this); }

          attributes[i] = attrNs;
          attributes[i + 1] = attrPrefix;
          attributes[i + 2] = attrName;

          for (int j = (attributeCount << 2) - 4; j > i; j -= 4) {
            if (attrName.equals(attributes[j + 2]) && attrNs.equals(attributes[j])) {
              exception("Duplicate Attribute: {" + attrNs + "}" + attrName);
            }
          }
        }
      }
    }

    final int cut = name.indexOf(':');

    if (cut == 0) {
      exception("illegal tag name: " + name);
    }
    else if (cut != -1) {
      prefix = name.substring(0, cut);
      name = name.substring(cut + 1);
    }

    namespace = getNamespace(prefix);

    if (namespace == null) {
      if (prefix != null) {
        exception("undefined prefix: " + prefix);
      }
      namespace = XmlPullParser.NO_NAMESPACE;
    }

    return any;
  }

  private final void setTable(final int page, final int type, final String[] table) {
    if (stringTable != null) { throw new RuntimeException("setXxxTable must be called before setInput!"); }
    while (tables.size() < 3 * page + 3) {
      tables.addElement(null);
    }
    tables.setElementAt(table, page * 3 + type);
  }

  private final void exception(final String desc) throws XmlPullParserException {
    throw new XmlPullParserException(desc, this, null);
  }

  private void selectPage(final int nr, final boolean tags) throws XmlPullParserException {
    if ((tables.size() == 0) && (nr == 0)) { return; }

    if (nr * 3 > tables.size()) {
      exception("Code Page " + nr + " undefined!");
    }

    if (tags) {
      tagTable = (String[]) tables.elementAt(nr * 3 + TAG_TABLE);
    }
    else {
      attrStartTable = (String[]) tables.elementAt(nr * 3 + ATTR_START_TABLE);
      attrValueTable = (String[]) tables.elementAt(nr * 3 + ATTR_VALUE_TABLE);
    }
  }

  private final void nextImpl() throws IOException, XmlPullParserException {

    if (type == XmlPullParser.END_TAG) {
      depth--;
    }

    if (degenerated) {
      type = XmlPullParser.END_TAG;
      degenerated = false;
      return;
    }

    text = null;
    prefix = null;
    name = null;

    int id = peekId();
    while (id == Wbxml.SWITCH_PAGE) {
      nextId = -2;
      selectPage(readByte(), true);
      id = peekId();
    }
    nextId = -2;

    switch (id) {
      case -1:
        type = XmlPullParser.END_DOCUMENT;
        break;

      case Wbxml.END: {
        final int sp = (depth - 1) << 2;

        type = XmlPullParser.END_TAG;
        namespace = elementStack[sp];
        prefix = elementStack[sp + 1];
        name = elementStack[sp + 2];
      }
        break;

      case Wbxml.ENTITY: {
        type = XmlPullParser.ENTITY_REF;
        final char c = (char) readInt();
        text = "" + c;
        name = "#" + ((int) c);
      }

        break;

      case Wbxml.STR_I:
        type = XmlPullParser.TEXT;
        text = readStrI();
        break;

      case Wbxml.EXT_I_0:
      case Wbxml.EXT_I_1:
      case Wbxml.EXT_I_2:
      case Wbxml.EXT_T_0:
      case Wbxml.EXT_T_1:
      case Wbxml.EXT_T_2:
      case Wbxml.EXT_0:
      case Wbxml.EXT_1:
      case Wbxml.EXT_2:
      case Wbxml.OPAQUE:

        type = WbxmlParser.WAP_EXTENSION;
        wapCode = id;
        wapExtensionData = parseWapExtension(id);
        break;

      case Wbxml.PI:
        throw new RuntimeException("PI curr. not supp.");
        // readPI;
        // break;

      case Wbxml.STR_T: {
        type = XmlPullParser.TEXT;
        text = readStrT();
      }
        break;

      default:
        parseElement(id);
    }
    // }
    // while (next == null);

    // return next;
  }

  /** Overwrite this method to intercept all wap events */

  public Object parseWapExtension(final int id) throws IOException, XmlPullParserException {

    switch (id) {
      case Wbxml.EXT_I_0:
      case Wbxml.EXT_I_1:
      case Wbxml.EXT_I_2:
        return readStrI();

      case Wbxml.EXT_T_0:
      case Wbxml.EXT_T_1:
      case Wbxml.EXT_T_2:
        return new Integer(readInt());

      case Wbxml.EXT_0:
      case Wbxml.EXT_1:
      case Wbxml.EXT_2:
        return null;

      case Wbxml.OPAQUE: {
        int count = readInt();
        final byte[] buf = new byte[count];

        while (count > 0) {
          count -= in.read(buf, buf.length - count, count);
        }

        return buf;
      } // case OPAQUE

      default:
        exception("illegal id: " + id);
        return null; // dead code
    } // SWITCH
  }

  public void readAttr() throws IOException, XmlPullParserException {

    int id = readByte();
    int i = 0;

    while (id != 1) {

      while (id == Wbxml.SWITCH_PAGE) {
        selectPage(readByte(), false);
        id = readByte();
      }

      String name = resolveId(attrStartTable, id);
      StringBuffer value;

      final int cut = name.indexOf('=');

      if (cut == -1) {
        value = new StringBuffer();
      }
      else {
        value = new StringBuffer(name.substring(cut + 1));
        name = name.substring(0, cut);
      }

      id = readByte();
      while ((id > 128) || (id == Wbxml.SWITCH_PAGE) || (id == Wbxml.ENTITY) || (id == Wbxml.STR_I) || (id == Wbxml.STR_T) || ((id >= Wbxml.EXT_I_0) && (id <= Wbxml.EXT_I_2))
          || ((id >= Wbxml.EXT_T_0) && (id <= Wbxml.EXT_T_2))) {

        switch (id) {
          case Wbxml.SWITCH_PAGE:
            selectPage(readByte(), false);
            break;

          case Wbxml.ENTITY:
            value.append((char) readInt());
            break;

          case Wbxml.STR_I:
            value.append(readStrI());
            break;

          case Wbxml.EXT_I_0:
          case Wbxml.EXT_I_1:
          case Wbxml.EXT_I_2:
          case Wbxml.EXT_T_0:
          case Wbxml.EXT_T_1:
          case Wbxml.EXT_T_2:
          case Wbxml.EXT_0:
          case Wbxml.EXT_1:
          case Wbxml.EXT_2:
          case Wbxml.OPAQUE:
            value.append(resolveWapExtension(id, parseWapExtension(id)));
            break;

          case Wbxml.STR_T:
            value.append(readStrT());
            break;

          default:
            value.append(resolveId(attrValueTable, id));
        }

        id = readByte();
      }

      attributes = ensureCapacity(attributes, i + 4);

      attributes[i++] = "";
      attributes[i++] = null;
      attributes[i++] = name;
      attributes[i++] = value.toString();

      attributeCount++;
    }
  }

  private int peekId() throws IOException {
    if (nextId == -2) {
      nextId = in.read();
    }
    return nextId;
  }

  /**
   * overwrite for own WAP extension handling in attributes and high level parsing (above nextToken() level)
   */

  protected String resolveWapExtension(final int id, final Object data) {

    if (data instanceof byte[]) {
      final StringBuffer sb = new StringBuffer();
      final byte[] b = (byte[]) data;

      for (int i = 0; i < b.length; i++) {
        sb.append(WbxmlParser.HEX_DIGITS.charAt((b[i] >> 4) & 0x0f));
        sb.append(WbxmlParser.HEX_DIGITS.charAt(b[i] & 0x0f));
      }
      return sb.toString();
    }

    return "$(" + data + ")";
  }

  String resolveId(final String[] tab, final int id) throws IOException {
    final int idx = (id & 0x07f) - 5;
    if (idx == -1) {
      wapCode = -1;
      return readStrT();
    }
    if ((idx < 0) || (tab == null) || (idx >= tab.length) || (tab[idx] == null)) { throw new IOException("id " + id + " undef."); }

    wapCode = idx + 5;

    return tab[idx];
  }

  void parseElement(final int id) throws IOException, XmlPullParserException {

    type = XmlPullParser.START_TAG;
    name = resolveId(tagTable, id & 0x03f);

    attributeCount = 0;
    if ((id & 128) != 0) {
      readAttr();
    }

    degenerated = (id & 64) == 0;

    final int sp = depth++ << 2;

    // transfer to element stack

    elementStack = ensureCapacity(elementStack, sp + 4);
    elementStack[sp + 3] = name;

    if (depth >= nspCounts.length) {
      final int[] bigger = new int[depth + 4];
      System.arraycopy(nspCounts, 0, bigger, 0, nspCounts.length);
      nspCounts = bigger;
    }

    nspCounts[depth] = nspCounts[depth - 1];

    for (int i = attributeCount - 1; i > 0; i--) {
      for (int j = 0; j < i; j++) {
        if (getAttributeName(i).equals(getAttributeName(j))) {
          exception("Duplicate Attribute: " + getAttributeName(i));
        }
      }
    }

    if (processNsp) {
      adjustNsp();
    }
    else {
      namespace = "";
    }

    elementStack[sp] = namespace;
    elementStack[sp + 1] = prefix;
    elementStack[sp + 2] = name;

  }

  private final String[] ensureCapacity(final String[] arr, final int required) {
    if (arr.length >= required) { return arr; }
    final String[] bigger = new String[required + 16];
    System.arraycopy(arr, 0, bigger, 0, arr.length);
    return bigger;
  }

  int readByte() throws IOException {
    final int i = in.read();
    if (i == -1) { throw new IOException("Unexpected EOF"); }
    return i;
  }

  int readInt() throws IOException {
    int result = 0;
    int i;

    do {
      i = readByte();
      result = (result << 7) | (i & 0x7f);
    }
    while ((i & 0x80) != 0);

    return result;
  }

  String readStrI() throws IOException {
    final ByteArrayOutputStream buf = new ByteArrayOutputStream();
    boolean wsp = true;
    while (true) {
      final int i = in.read();
      if (i == 0) {
        break;
      }
      if (i == -1) { throw new IOException(WbxmlParser.UNEXPECTED_EOF); }
      if (i > 32) {
        wsp = false;
      }
      buf.write(i);
    }
    isWhitespace = wsp;
    final String result = new String(buf.toByteArray(), encoding);
    buf.close();
    return result;
  }

  String readStrT() throws IOException {
    final int pos = readInt();
    // As the main reason of stringTable is compression we build a cache of
    // Strings
    // stringTable is supposed to help create Strings from parts which means
    // some cache hit rate
    // This will help to minimize the Strings created when invoking readStrT()
    // repeatedly
    if (cacheStringTable == null) {
      // Lazy init if device is not using StringTable but inline 0x03 strings
      cacheStringTable = new Hashtable();
    }
    String forReturn = (String) cacheStringTable.get(new Integer(pos));
    if (forReturn == null) {

      int end = pos;
      while ((end < stringTable.length) && (stringTable[end] != '\0')) {
        end++;
      }
      forReturn = new String(stringTable, pos, end - pos, encoding);
      cacheStringTable.put(new Integer(pos), forReturn);
    }
    return forReturn;
  }

  /**
   * Sets the tag table for a given page. The first string in the array defines tag 5, the second tag 6 etc.
   */

  public void setTagTable(final int page, final String[] table) {
    setTable(page, TAG_TABLE, table);

    // this.tagTable = tagTable;
    // if (page != 0)
    // throw new RuntimeException("code pages curr. not supp.");
  }

  /**
   * Sets the attribute start Table for a given page. The first string in the array defines attribute 5, the second attribute 6 etc. Please use the character '=' (without quote!) as delimiter between
   * the attribute name and the (start of the) value
   */

  public void setAttrStartTable(final int page, final String[] table) {

    setTable(page, ATTR_START_TABLE, table);
  }

  /**
   * Sets the attribute value Table for a given page. The first string in the array defines attribute value 0x85, the second attribute value 0x86 etc.
   */

  public void setAttrValueTable(final int page, final String[] table) {

    setTable(page, ATTR_VALUE_TABLE, table);
  }

  /**
   * Returns the token ID for start tags or the event type for wap proprietary events such as OPAQUE.
   */

  public int getWapCode() {
    return wapCode;
  }

  public Object getWapExtensionData() {
    return wapExtensionData;
  }

}