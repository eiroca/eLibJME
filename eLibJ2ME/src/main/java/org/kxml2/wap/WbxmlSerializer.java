/*
 * Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany Permission is hereby
 * granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions: The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED
 * "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

// Contributors: Jonathan Cox, Bogdan Onoiu, Jerry Tian
package org.kxml2.wap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Vector;
import org.xmlpull.v1.XmlSerializer;

// TODO: make some of the "direct" WBXML token writing methods public??

/**
 * A class for writing WBXML.
 */

public class WbxmlSerializer implements XmlSerializer {

  Hashtable stringTable = new Hashtable();

  OutputStream out;

  ByteArrayOutputStream buf = new ByteArrayOutputStream();
  ByteArrayOutputStream stringTableBuf = new ByteArrayOutputStream();

  String pending;
  int depth;
  String name;
  String namespace;
  Vector attributes = new Vector();

  Hashtable attrStartTable = new Hashtable();
  Hashtable attrValueTable = new Hashtable();
  Hashtable tagTable = new Hashtable();

  private int attrPage;
  private int tagPage;

  private String encoding;

  @Override
  public XmlSerializer attribute(final String namespace, final String name, final String value) {
    attributes.addElement(name);
    attributes.addElement(value);
    return this;
  }

  @Override
  public void cdsect(final String cdsect) throws IOException {
    text(cdsect);
  }

  /* silently ignore comment */

  @Override
  public void comment(final String comment) {
    //
  }

  @Override
  public void docdecl(final String docdecl) {
    throw new RuntimeException("Cannot write docdecl for WBXML");
  }

  @Override
  public void entityRef(final String er) {
    throw new RuntimeException("EntityReference not supported for WBXML");
  }

  @Override
  public int getDepth() {
    return depth;
  }

  @Override
  public boolean getFeature(final String name) {
    return false;
  }

  @Override
  public String getNamespace() {
    throw new RuntimeException("NYI");
  }

  @Override
  public String getName() {
    throw new RuntimeException("NYI");
  }

  @Override
  public String getPrefix(final String nsp, final boolean create) {
    throw new RuntimeException("NYI");
  }

  @Override
  public Object getProperty(final String name) {
    return null;
  }

  @Override
  public void ignorableWhitespace(final String sp) {
    //
  }

  @Override
  public void endDocument() throws IOException {
    WbxmlSerializer.writeInt(out, stringTableBuf.size());

    // write StringTable

    out.write(stringTableBuf.toByteArray());

    // write buf

    out.write(buf.toByteArray());

    // ready!

    out.flush();
  }

  /**
   * ATTENTION: flush cannot work since Wbxml documents require buffering. Thus, this call does
   * nothing.
   */

  @Override
  public void flush() {
    //
  }

  public void checkPending(final boolean degenerated) throws IOException {
    if (pending == null) { return; }

    final int len = attributes.size();

    int[] idx = (int[])tagTable.get(pending);

    // if no entry in known table, then add as literal
    if (idx == null) {
      buf.write(len == 0 ? (degenerated ? Wbxml.LITERAL : Wbxml.LITERAL_C) : (degenerated ? Wbxml.LITERAL_A : Wbxml.LITERAL_AC));

      writeStrT(pending, false);
    }
    else {
      if (idx[0] != tagPage) {
        tagPage = idx[0];
        buf.write(Wbxml.SWITCH_PAGE);
        buf.write(tagPage);
      }

      buf.write(len == 0 ? (degenerated ? idx[1] : idx[1] | 64) : (degenerated ? idx[1] | 128 : idx[1] | 192));

    }

    for (int i = 0; i < len;) {
      idx = (int[])attrStartTable.get(attributes.elementAt(i));

      if (idx == null) {
        buf.write(Wbxml.LITERAL);
        writeStrT((String)attributes.elementAt(i), false);
      }
      else {
        if (idx[0] != attrPage) {
          attrPage = idx[0];
          buf.write(0);
          buf.write(attrPage);
        }
        buf.write(idx[1]);
      }
      idx = (int[])attrValueTable.get(attributes.elementAt(++i));
      if (idx == null) {
        writeStr((String)attributes.elementAt(i));
      }
      else {
        if (idx[0] != attrPage) {
          attrPage = idx[0];
          buf.write(0);
          buf.write(attrPage);
        }
        buf.write(idx[1]);
      }
      ++i;
    }

    if (len > 0) {
      buf.write(Wbxml.END);
    }

    pending = null;
    attributes.removeAllElements();
  }

  @Override
  public void processingInstruction(final String pi) {
    throw new RuntimeException("PI NYI");
  }

  @Override
  public void setFeature(final String name, final boolean value) {
    throw new IllegalArgumentException("unknown feature " + name);
  }

  @Override
  public void setOutput(final Writer writer) {
    throw new RuntimeException("Wbxml requires an OutputStream!");
  }

  @Override
  public void setOutput(final OutputStream out, final String encoding) throws IOException {

    this.encoding = encoding == null ? "UTF-8" : encoding;
    this.out = out;

    buf = new ByteArrayOutputStream();
    stringTableBuf = new ByteArrayOutputStream();

    // ok, write header
  }

  @Override
  public void setPrefix(final String prefix, final String nsp) {
    throw new RuntimeException("NYI");
  }

  @Override
  public void setProperty(final String property, final Object value) {
    throw new IllegalArgumentException("unknown property " + property);
  }

  @Override
  public void startDocument(final String s, final Boolean b) throws IOException {
    out.write(0x03); // version 1.3
    // http://www.openmobilealliance.org/tech/omna/omna-wbxml-public-docid.htm
    out.write(0x01); // unknown or missing public identifier

    // default encoding is UTF-8

    if (s != null) {
      encoding = s;
    }

    if (encoding.toUpperCase().equals("UTF-8")) {
      out.write(106);
    }
    else if (encoding.toUpperCase().equals("ISO-8859-1")) {
      out.write(0x04);
    }
    else {
      throw new UnsupportedEncodingException(s);
    }
  }

  @Override
  public XmlSerializer startTag(final String namespace, final String name) throws IOException {

    if ((namespace != null) && !"".equals(namespace)) { throw new RuntimeException("NSP NYI"); }

    // current = new State(current, prefixMap, name);

    checkPending(false);
    pending = name;
    depth++;

    return this;
  }

  @Override
  public XmlSerializer text(final char[] chars, final int start, final int len) throws IOException {

    checkPending(false);

    writeStr(new String(chars, start, len));

    return this;
  }

  @Override
  public XmlSerializer text(final String text) throws IOException {

    checkPending(false);

    writeStr(text);

    return this;
  }

  /** Used in text() and attribute() to write text */

  private void writeStr(final String text) throws IOException {
    int p0 = 0;
    int lastCut = 0;
    final int len = text.length();

    while (p0 < len) {
      while ((p0 < len) && (text.charAt(p0) < 'A')) { // skip interpunctation
        p0++;
      }
      int p1 = p0;
      while ((p1 < len) && (text.charAt(p1) >= 'A')) {
        p1++;
      }

      if ((p1 - p0) > 10) {

        if ((p0 > lastCut) && (text.charAt(p0 - 1) == ' ') && (stringTable.get(text.substring(p0, p1)) == null)) {

          buf.write(Wbxml.STR_T);
          writeStrT(text.substring(lastCut, p1), false);
        }
        else {

          if ((p0 > lastCut) && (text.charAt(p0 - 1) == ' ')) {
            p0--;
          }

          if (p0 > lastCut) {
            buf.write(Wbxml.STR_T);
            writeStrT(text.substring(lastCut, p0), false);
          }
          buf.write(Wbxml.STR_T);
          writeStrT(text.substring(p0, p1), true);
        }
        lastCut = p1;
      }
      p0 = p1;
    }

    if (lastCut < len) {
      buf.write(Wbxml.STR_T);
      writeStrT(text.substring(lastCut, len), false);
    }
  }

  @Override
  public XmlSerializer endTag(final String namespace, final String name) throws IOException {

    // current = current.prev;

    if (pending != null) {
      checkPending(true);
    }
    else {
      buf.write(Wbxml.END);
    }

    depth--;

    return this;
  }

  /**
   * @throws IOException
   */

  public void writeWapExtension(final int type, final Object data) throws IOException {
    checkPending(false);
    buf.write(type);
    switch (type) {
      case Wbxml.EXT_0:
      case Wbxml.EXT_1:
      case Wbxml.EXT_2:
        break;

      case Wbxml.OPAQUE:
        final byte[] bytes = (byte[])data;
        WbxmlSerializer.writeInt(buf, bytes.length);
        buf.write(bytes);
        break;

      case Wbxml.EXT_I_0:
      case Wbxml.EXT_I_1:
      case Wbxml.EXT_I_2:
        writeStrI(buf, (String)data);
        break;

      case Wbxml.EXT_T_0:
      case Wbxml.EXT_T_1:
      case Wbxml.EXT_T_2:
        writeStrT((String)data, false);
        break;

      default:
        throw new IllegalArgumentException();
    }
  }

  // ------------- internal methods --------------------------

  static void writeInt(final OutputStream out, int i) throws IOException {
    final byte[] buf = new byte[5];
    int idx = 0;

    do {
      buf[idx++] = (byte)(i & 0x7f);
      i = i >> 7;
    }
    while (i != 0);

    while (idx > 1) {
      out.write(buf[--idx] | 0x80);
    }
    out.write(buf[0]);
  }

  void writeStrI(final OutputStream out, final String s) throws IOException {
    final byte[] data = s.getBytes(encoding);
    out.write(data);
    out.write(0);
  }

  private final void writeStrT(String s, final boolean mayPrependSpace) throws IOException {

    final Integer idx = (Integer)stringTable.get(s);

    if (idx != null) {
      WbxmlSerializer.writeInt(buf, idx.intValue());
    }
    else {
      final int i = stringTableBuf.size();
      if ((s.charAt(0) >= '0') && mayPrependSpace) {
        s = ' ' + s;
        WbxmlSerializer.writeInt(buf, i + 1);
      }
      else {
        WbxmlSerializer.writeInt(buf, i);
      }

      stringTable.put(s, new Integer(i));
      if (s.charAt(0) == ' ') {
        stringTable.put(s.substring(1), new Integer(i + 1));
      }
      final int j = s.lastIndexOf(' ');
      if (j > 1) {
        stringTable.put(s.substring(j), new Integer(i + j));
        stringTable.put(s.substring(j + 1), new Integer(i + j + 1));
      }

      writeStrI(stringTableBuf, s);
      stringTableBuf.flush();
    }

  }

  /**
   * Sets the tag table for a given page. The first string in the array defines tag 5, the second
   * tag 6 etc.
   */

  public void setTagTable(final int page, final String[] tagTable) {
    // TODO: clear entries in tagTable?

    for (int i = 0; i < tagTable.length; i++) {
      if (tagTable[i] != null) {
        final Object idx = new int[] {
            page, i + 5
        };
        this.tagTable.put(tagTable[i], idx);
      }
    }
  }

  /**
   * Sets the attribute start Table for a given page. The first string in the array defines
   * attribute 5, the second attribute 6 etc. Please use the character '=' (without quote!) as
   * delimiter between the attribute name and the (start of the) value
   */
  public void setAttrStartTable(final int page, final String[] attrStartTable) {

    for (int i = 0; i < attrStartTable.length; i++) {
      if (attrStartTable[i] != null) {
        final Object idx = new int[] {
            page, i + 5
        };
        this.attrStartTable.put(attrStartTable[i], idx);
      }
    }
  }

  /**
   * Sets the attribute value Table for a given page. The first string in the array defines
   * attribute value 0x85, the second attribute value 0x86 etc.
   */
  public void setAttrValueTable(final int page, final String[] attrValueTable) {
    // clear entries in this.table!
    for (int i = 0; i < attrValueTable.length; i++) {
      if (attrValueTable[i] != null) {
        final Object idx = new int[] {
            page, i + 0x085
        };
        this.attrValueTable.put(attrValueTable[i], idx);
      }
    }
  }
}
