package org.kxml.kdom;

import org.kxml.Xml;
import org.kxml.parser.AbstractXmlParser;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.StartTag;
import org.kxml.parser.Tag;

/** a pull parser that re-parses a kdom tree. */

public class TreeParser extends AbstractXmlParser {

  class Position {

    Node node;
    StartTag start;
    int index = -1;
    Position previous;

    Position(final Node node) {
      this.node = node;
      index = 0;
    }

    Position(final Element element, final Position prev) {
      node = element;

      previous = prev;
      start = new StartTag(prev == null ? null : prev.start, element.getNamespace(), element.getName(), element.getAttributes(), element.getChildCount() == 0, false);

      start.setPrefixMap(element.getPrefixMap());
    }
  }

  Position current;
  ParseEvent next;

  public TreeParser(final Node node) {
    current = new Position(node);
  }

  public TreeParser(final Element element, final boolean skipRoot) {

    current = new Position(element, null);
    if (skipRoot) {
      current.index = 0;
      current.start = null;
    }
  }

  public ParseEvent read() {
    if (next == null) {
      peek();
    }
    final ParseEvent result = next;
    next = null;
    return result;
  }

  public ParseEvent peek() {

    if (next == null) {

      final int i = current.index++;
      final Node node = current.node;

      if (i < 0) {
        next = current.start;
      }
      else if (i >= node.getChildCount()) {

        if ((current.start == null) || (i > node.getChildCount())) {
          next = new ParseEvent(Xml.END_DOCUMENT, null);
        }
        else {
          next = new Tag(Xml.END_TAG, current.start, current.start.getNamespace(), current.start.getName());

          if (current.previous != null) {
            current = current.previous;
          }
        }
      }
      else {
        final int type = node.getType(i);

        if (type == Xml.ELEMENT) {
          current = new Position(node.getElement(i), current);
          next = peek(); // sets next....
        }
        else {
          next = new ParseEvent(type, (String) node.getChild(i));
        }
      }
    }

    return next;
  }
}
