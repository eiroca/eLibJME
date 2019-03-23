package org.kxml;

/** Like Attribute, this class is immutable for similar reasons */

public class PrefixMap {

  public static final PrefixMap DEFAULT = new PrefixMap(null, "", "");

  String prefix;
  String namespace;
  PrefixMap previous;

  public PrefixMap(final PrefixMap previous, final String prefix, final String namespace) {
    this.previous = previous;
    this.prefix = prefix;
    this.namespace = namespace;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getPrefix() {
    return prefix;
  }

  public PrefixMap getPrevious() {
    return previous;
  }

  /**
   * returns the namespace associated with the given prefix, or null, if none is assigned
   */

  public String getNamespace(final String prefix) {
    PrefixMap current = this;
    do {
      if (prefix.equals(current.prefix)) { return current.namespace; }
      current = current.previous;
    }
    while (current != null);
    return null;
  }

  public String getPrefix(final String namespace) {
    PrefixMap current = this;

    do {
      // System.err.println ("found: "+current.namespace +"/"+ current.prefix +
      // "/" +getNamespace (current.prefix));
      if (namespace.equals(current.namespace) && namespace.equals(getNamespace(current.prefix))) { return current.prefix; }

      current = current.previous;
    }
    while (current != null);
    return null;
  }
}
