/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - GPL >= 3.0
 * 
 * portion Copyright (C) 2002 Eugene Morozov (xonixboy@hotmail.com)
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
package net.eiroca.j2me.rms;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * An Rms based BTree index. Currently, the functionality is similar to a persistent hashtable.
 */

public class RMSTable {

  /** The N. */
  public static int N = 32;

  /** The store. */
  public RecordStore store;

  /** The root. */
  public RMSTableNode root;

  /** The cached key. */
  public String cachedKey;

  /** The cached node. */
  public RMSTableNode cachedNode;

  /** The cached index. */
  public int cachedIndex;

  /**
   * Creates a new index with the given Name.
   * 
   * @param name the name
   * @throws RecordStoreException the record store exception
   */
  public RMSTable(final String name) throws RecordStoreException {
    store = RecordStore.openRecordStore(name, true);
    root = store.getNumRecords() == 0 ? new RMSTableNode(this) : new RMSTableNode(this, 1);
  }

  /**
   * Returns the value for the given key.
   * 
   * @param key the key
   * @return String: The value associated with the key, or null if none.
   * @throws RecordStoreException the record store exception
   */
  public String get(final String key) throws RecordStoreException {
    RMSTableNode current = root;
    while (true) {
      int i;
      for (i = 0; i < current.size; i++) {
        final int cmp = key.compareTo(current.keys[i]);
        if (cmp == 0) {
          return current.values[i];
        }
        else if (cmp < 0) {
          break;
        }
      }
      if (current.children[i] == 0) { return null; }
      current = new RMSTableNode(this, current.children[i]);
    }
  }

  /**
   * Assigns the given value to the given key persistently.
   * 
   * @param key the key
   * @param value the value
   * @throws RecordStoreException the record store exception
   */
  public void put(final String key, final String value) throws RecordStoreException {
    root.put(key, value);
    if (root.size < RMSTable.N + RMSTable.N + 1) { return; }
    // split root....
    final RMSTableNode left = root;
    root = new RMSTableNode(this);
    left.index = root.index;
    root.index = 1;
    final RMSTableNode right = new RMSTableNode(this, left);
    root.keys[0] = left.keys[RMSTable.N];
    root.values[0] = left.values[RMSTable.N];
    root.children[0] = left.index;
    root.children[1] = right.index;
    root.size = 1;
    root.store();
  }

  /**
   * Close.
   * 
   * @throws RecordStoreException the record store exception
   */
  public void close() throws RecordStoreException {
    store.closeRecordStore();
  }

}
