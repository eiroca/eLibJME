/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

/**
 * CategoryKey is heavily used internally to accelerate hash table searches.
 * @author Ceki G&uuml;lc&uuml;, Witmate
 */
// Modifiers: Witmate [Nov,2004: Modified for log4j2me]
class CategoryKey {

  String name;
  int hashCache;

  CategoryKey(final String name) {
    this.name = name.toString();// name.intern();[Witmate]
    hashCache = name.hashCode();
  }

  final public int hashCode() {
    return hashCache;
  }

  final public boolean equals(final Object rArg) {
    if (this == rArg) { return true; }
    if ((rArg != null) && (rArg instanceof CategoryKey)) { return name.equals(((CategoryKey) rArg).name); }
    return false;
  }
}
