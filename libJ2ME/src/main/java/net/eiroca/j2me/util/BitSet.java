/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - LGPL >= 3.0
 * 
 * Portion Copyright (C) 2002-2004 Salamon Andras
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/
 */
package net.eiroca.j2me.util;

/**
 * Something like java.util.BitSet, which is not available int J2ME. This version is much more
 * simple, but good enough
 */
public class BitSet {

  /** The bits. */
  private final long bits[]; // this will store the bits

  /** The bit num. */
  private int bitNum;

  /** The Constant LONG_SIZE. */
  private static final int LONG_SIZE = 6;

  /** The Constant BITS_PER_BLOCK. */
  private final static int BITS_PER_BLOCK = 1 << BitSet.LONG_SIZE;

  /** The Constant BIT_INDEX_MASK. */
  private final static int BIT_INDEX_MASK = BitSet.BITS_PER_BLOCK - 1;

  /**
   * Instantiates a new bit set.
   * 
   * @param bitNum the bit num
   */
  public BitSet(final int bitNum) {
    bits = new long[(BitSet.blockIndex(bitNum - 1) + 1)];
    this.bitNum = bitNum;
  }

  /**
   * Copy constuctor.
   * 
   * @param old the old
   */
  public BitSet(final BitSet old) {
    this(old.bitNum);
    System.arraycopy(old.bits, 0, bits, 0, bitNum >> BitSet.LONG_SIZE);
  }

  /**
   * Instantiates a new bit set.
   * 
   * @param bitNum the bit num
   * @param byteArray the byte array
   * @param offset the offset
   */
  public BitSet(final int bitNum, final byte[] byteArray, final int offset) {
    this(bitNum);
    int retIndex = offset;
    int i = 0;
    while (i < bits.length) {
      bits[i] = 0;
      for (int j = 56; j >= 0; j -= 8) {
        long octet = byteArray[retIndex++];
        if (octet < 0) {
          octet += 256;
        }
        bits[i] |= octet << j;
      }
      ++i;
    }
  }

  /**
   * Copy data from 'src' BitSet to 'dst' BitSet. We assume, that there is enough room for this.
   * 
   * @param src the src
   * @param dst the dst
   */
  public static void copy(final BitSet src, final BitSet dst) {
    dst.bitNum = src.bitNum;
    System.arraycopy(src.bits, 0, dst.bits, 0, src.length() >> BitSet.LONG_SIZE);
  }

  /**
   * Length.
   * 
   * @return the int
   */
  public int length() {
    return bitNum;
  }

  /**
   * Block index.
   * 
   * @param bitIndex the bit index
   * @return the int
   */
  private static int blockIndex(final int bitIndex) {
    return bitIndex >> BitSet.LONG_SIZE;
  }

  /**
   * Given a bit index, return a block that masks that bit in its block.
   * 
   * @param bitIndex the bit index
   * @return the long
   */
  private static long bit(final int bitIndex) {
    return 1L << (bitIndex & BitSet.BIT_INDEX_MASK);
  }

  /**
   * Sets the.
   * 
   * @param bitIndex the bit index
   * @param value the value
   */
  public void set(final int bitIndex, final boolean value) {
    if (value) {
      set(bitIndex);
    }
    else {
      clear(bitIndex);
    }
  }

  /**
   * Sets the bit specified by the index to <code>true</code>.
   * 
   * @param bitIndex the bit index
   */
  public void set(final int bitIndex) {
    if ((bitIndex < 0) || (bitIndex > bitNum)) { throw new IndexOutOfBoundsException("" + bitIndex); }
    final int blockIndex = BitSet.blockIndex(bitIndex);
    bits[blockIndex] |= BitSet.bit(bitIndex);
  }

  /**
   * Sets the bit specified by the index to <code>false</code>.
   * 
   * @param bitIndex the bit index
   */
  public void clear(final int bitIndex) {
    if ((bitIndex < 0) || (bitIndex > bitNum)) { throw new IndexOutOfBoundsException("" + bitIndex); }
    final int blockIndex = BitSet.blockIndex(bitIndex);
    bits[blockIndex] &= ~BitSet.bit(bitIndex);
  }

  /**
   * Returns the value of the bit with the specified index.
   * 
   * @param bitIndex the bit index
   * @return true, if successful
   */
  public boolean get(final int bitIndex) {
    if ((bitIndex < 0) || (bitIndex > bitNum)) { throw new IndexOutOfBoundsException("" + bitIndex); }
    final int blockIndex = BitSet.blockIndex(bitIndex);
    return ((bits[blockIndex] & BitSet.bit(bitIndex)) != 0);
  }

  /**
   * String representation.
   * 
   * @return the string
   */
  public String toString() {
    final StringBuffer buffer = new StringBuffer(8 * bitNum + 2);
    buffer.append('{');
    for (int i = 0; i < bitNum; i++) {
      if (get(i)) {
        buffer.append("1");
      }
      else {
        buffer.append("0");
      }
    }
    buffer.append('}');
    return buffer.toString();
  }

  /**
   * To byte array.
   * 
   * @param byteArray the byte array
   * @param offset the offset
   */
  public void toByteArray(final byte[] byteArray, final int offset) {
    int arrayIndex = offset;
    for (int i = 0; i < bits.length; ++i) {
      for (int j = 56; j >= 0; j -= 8) {
        byteArray[arrayIndex++] = (byte)(bits[i] >> j);
      }
    }
  }

  /**
   * To byte array.
   * 
   * @return the byte[]
   */
  public byte[] toByteArray() {
    final byte[] ret = new byte[8 * bits.length];
    toByteArray(ret, 0);
    return ret;
  }

}
