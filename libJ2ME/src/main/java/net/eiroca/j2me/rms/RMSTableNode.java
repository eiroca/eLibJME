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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.rms.RecordStoreException;

/**
 * Node helper class.
 */

public class RMSTableNode {

  /** The DUMMY. */
  static byte[] DUMMY = new byte[0];

  /** The rms index. */
  private final RMSTable rmsIndex;

  /** The index. */
  public int index;

  /** The size. */
  public int size;

  /** The keys. */
  public String[] keys = new String[RMSTable.N + RMSTable.N + 1];

  /** The values. */
  public String[] values = new String[RMSTable.N + RMSTable.N + 1];

  /** The children. */
  public int[] children = new int[RMSTable.N + RMSTable.N + 2];

  /**
   * Create a new Node.
   * 
   * @param rmsIndex the rms index
   * @throws RecordStoreException the record store exception
   */
  public RMSTableNode(final RMSTable rmsIndex) throws RecordStoreException {
    this.rmsIndex = rmsIndex;
    index = this.rmsIndex.store.addRecord(RMSTableNode.DUMMY, 0, 0);
  }

  /**
   * Instantiates a new rMS table node.
   * 
   * @param rmsIndex the rms index
   * @param split the split
   * @throws RecordStoreException the record store exception
   */
  public RMSTableNode(final RMSTable rmsIndex, final RMSTableNode split) throws RecordStoreException {
    this(rmsIndex);
    System.arraycopy(split.keys, RMSTable.N + 1, keys, 0, RMSTable.N);
    System.arraycopy(split.values, RMSTable.N + 1, values, 0, RMSTable.N);
    System.arraycopy(split.children, RMSTable.N + 1, children, 0, RMSTable.N + 1);
    size = RMSTable.N;
    split.size = RMSTable.N;
    split.store();
    store();
  }

  /**
   * Load the node at the given index position.
   * 
   * @param rmsIndex the rms index
   * @param index the index
   * @throws RecordStoreException the record store exception
   */

  public RMSTableNode(final RMSTable rmsIndex, final int index) throws RecordStoreException {
    this.rmsIndex = rmsIndex;
    this.index = index;
    final byte[] data = this.rmsIndex.store.getRecord(index);
    if ((data == null) || (data.length == 0)) { return; }
    try {
      final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
      size = dis.readInt();
      for (int i = 0; i < size; i++) {
        children[i] = dis.readInt();
        keys[i] = dis.readUTF();
        values[i] = dis.readUTF();
      }
      children[size] = dis.readInt();
    }
    catch (final IOException e) {
      throw new RuntimeException(e.toString());
    }
  }

  /**
   * Put.
   * 
   * @param key the key
   * @param value the value
   * @throws RecordStoreException the record store exception
   */
  public void put(String key, String value) throws RecordStoreException {
    int i;
    for (i = 0; i < size; i++) {
      final int cmp = key.compareTo(keys[i]);
      if (cmp == 0) {
        values[i] = value;
        store();
        return;
      }
      else if (cmp < 0) {
        break;
      }
    }
    int newIndex = 0;
    if (children[i] != 0) {
      final RMSTableNode child = new RMSTableNode(rmsIndex, children[i]);
      child.put(key, value);
      if (child.size < RMSTable.N + RMSTable.N + 1) { return; }
      final RMSTableNode split = new RMSTableNode(rmsIndex, child);
      newIndex = split.index;
      key = child.keys[RMSTable.N];
      value = child.values[RMSTable.N];
    }
    System.arraycopy(keys, i, keys, i + 1, size - i);
    System.arraycopy(values, i, values, i + 1, size - i);
    System.arraycopy(children, i + 1, children, i + 2, size - i);
    keys[i] = key;
    values[i] = value;
    children[i + 1] = newIndex;
    size++;
    if (size < RMSTable.N + RMSTable.N + 1) {
      // otherwise, store will be performed by uplink
      store();
    }
  }

  /**
   * Store.
   * 
   * @throws RecordStoreException the record store exception
   */
  public void store() throws RecordStoreException {
    try {
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final DataOutputStream dos = new DataOutputStream(bos);
      dos.writeInt(size);
      for (int i = 0; i < size; i++) {
        dos.writeInt(children[i]);
        dos.writeUTF(keys[i]);
        dos.writeUTF(values[i]);
      }
      dos.writeInt(children[size]);
      final byte[] data = bos.toByteArray();
      rmsIndex.store.setRecord(index, data, 0, data.length);
    }
    catch (final IOException e) {
      throw new RuntimeException(e.toString());
    }
  }

}
