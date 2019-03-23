package org.kxml.io;

import org.kxml.PrefixMap;

public class State {

  public State prev;
  public PrefixMap prefixMap;
  public String namespace;
  public String name;
  public String tag; // for auto-endtag writing

  public State(final State prev, final PrefixMap prefixMap,
  // String namespace, String name,
  final String tag) {

    this.prev = prev;
    this.prefixMap = prefixMap;
    // this.namespace = namespace;
    // this.name = name;
    this.tag = tag;
  }
}
