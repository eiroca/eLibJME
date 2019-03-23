package org.kxml.parser;

import org.kxml.Xml;

/**
 * A class for events indicating the end of an element
 */

public class Tag extends ParseEvent {

  StartTag parent;
  String namespace;
  String name;

  public Tag(final int type, final StartTag parent, final String namespace, final String name) {
    super(type, null);
    this.parent = parent;
    this.namespace = namespace == null ? Xml.NO_NAMESPACE : namespace;
    this.name = name;
  }

  /** returns the (local) name of the element */

  public String getName() {
    return name;
  }

  /** returns the namespace */

  public String getNamespace() {
    return namespace;
  }

  /**
   * Returns the (corresponding) start tag or the start tag of the parent element, depending on the event type.
   */

  public StartTag getParent() {
    return parent;
  }

  public String toString() {
    return "EndTag </" + name + ">";
  }
}
