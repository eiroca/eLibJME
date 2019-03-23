/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - GPL >= 3.0
 * 
 * Portion Copyright (C) 2002 Eugene Morozov (xonixboy@hotmail.com)
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
package net.eiroca.j2me.util;

import net.eiroca.j2me.app.BaseApp;

/**
 * Implementation of the DES algorithm.
 */
public class CipherDES {

  // Note: declared private to reduce the JAR size after the obfuscation.
  /** The key reduce perm. */
  private final int keyReducePerm[] = {
      60, 52, 44, 36, 59, 51, 43, 35, 27, 19, 11, 3, 58, 50, 42, 34, 26, 18, 10, 2, 57, 49, 41, 33, 25, 17, 9, 1, 28, 20, 12, 4, 61, 53, 45, 37, 29, 21, 13, 5, 62, 54, 46, 38, 30, 22, 14, 6, 63, 55,
      47, 39, 31, 23, 15, 7
  };

  /** The key compress perm. */
  private final int keyCompressPerm[] = {
      24, 27, 20, 6, 14, 10, 3, 22, 0, 17, 7, 12, 8, 23, 11, 5, 16, 26, 1, 9, 19, 25, 4, 15, 54, 43, 36, 29, 49, 40, 48, 30, 52, 44, 37, 33, 46, 35, 50, 41, 28, 53, 51, 55, 32, 45, 39, 42
  };

  /** The key rot. */
  private final int keyRot[] = {
      1, 2, 4, 6, 8, 10, 12, 14, 15, 17, 19, 21, 23, 25, 27, 28
  };

  /** The init perm. */
  private final int initPerm[] = {
      57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7, 56, 48, 40, 32, 24, 16, 8, 0, 58, 50, 42, 34, 26, 18, 10, 2, 60, 52,
      44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6
  };

  /** The fin perm. */
  private final int finPerm[] = {
      39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1,
      41, 9, 49, 17, 57, 25, 32, 0, 40, 8, 48, 16, 56, 24
  };

  /** The s box p. */
  private final int sBoxP[][] = {
      {
          0x00808200, 0x00000000, 0x00008000, 0x00808202, 0x00808002, 0x00008202, 0x00000002, 0x00008000, 0x00000200, 0x00808200, 0x00808202, 0x00000200, 0x00800202, 0x00808002, 0x00800000,
          0x00000002, 0x00000202, 0x00800200, 0x00800200, 0x00008200, 0x00008200, 0x00808000, 0x00808000, 0x00800202, 0x00008002, 0x00800002, 0x00800002, 0x00008002, 0x00000000, 0x00000202,
          0x00008202, 0x00800000, 0x00008000, 0x00808202, 0x00000002, 0x00808000, 0x00808200, 0x00800000, 0x00800000, 0x00000200, 0x00808002, 0x00008000, 0x00008200, 0x00800002, 0x00000200,
          0x00000002, 0x00800202, 0x00008202, 0x00808202, 0x00008002, 0x00808000, 0x00800202, 0x00800002, 0x00000202, 0x00008202, 0x00808200, 0x00000202, 0x00800200, 0x00800200, 0x00000000,
          0x00008002, 0x00008200, 0x00000000, 0x00808002
      },
      {
          0x40084010, 0x40004000, 0x00004000, 0x00084010, 0x00080000, 0x00000010, 0x40080010, 0x40004010, 0x40000010, 0x40084010, 0x40084000, 0x40000000, 0x40004000, 0x00080000, 0x00000010,
          0x40080010, 0x00084000, 0x00080010, 0x40004010, 0x00000000, 0x40000000, 0x00004000, 0x00084010, 0x40080000, 0x00080010, 0x40000010, 0x00000000, 0x00084000, 0x00004010, 0x40084000,
          0x40080000, 0x00004010, 0x00000000, 0x00084010, 0x40080010, 0x00080000, 0x40004010, 0x40080000, 0x40084000, 0x00004000, 0x40080000, 0x40004000, 0x00000010, 0x40084010, 0x00084010,
          0x00000010, 0x00004000, 0x40000000, 0x00004010, 0x40084000, 0x00080000, 0x40000010, 0x00080010, 0x40004010, 0x40000010, 0x00080010, 0x00084000, 0x00000000, 0x40004000, 0x00004010,
          0x40000000, 0x40080010, 0x40084010, 0x00084000
      },
      {
          0x00000104, 0x04010100, 0x00000000, 0x04010004, 0x04000100, 0x00000000, 0x00010104, 0x04000100, 0x00010004, 0x04000004, 0x04000004, 0x00010000, 0x04010104, 0x00010004, 0x04010000,
          0x00000104, 0x04000000, 0x00000004, 0x04010100, 0x00000100, 0x00010100, 0x04010000, 0x04010004, 0x00010104, 0x04000104, 0x00010100, 0x00010000, 0x04000104, 0x00000004, 0x04010104,
          0x00000100, 0x04000000, 0x04010100, 0x04000000, 0x00010004, 0x00000104, 0x00010000, 0x04010100, 0x04000100, 0x00000000, 0x00000100, 0x00010004, 0x04010104, 0x04000100, 0x04000004,
          0x00000100, 0x00000000, 0x04010004, 0x04000104, 0x00010000, 0x04000000, 0x04010104, 0x00000004, 0x00010104, 0x00010100, 0x04000004, 0x04010000, 0x04000104, 0x00000104, 0x04010000,
          0x00010104, 0x00000004, 0x04010004, 0x00010100
      },
      {
          0x80401000, 0x80001040, 0x80001040, 0x00000040, 0x00401040, 0x80400040, 0x80400000, 0x80001000, 0x00000000, 0x00401000, 0x00401000, 0x80401040, 0x80000040, 0x00000000, 0x00400040,
          0x80400000, 0x80000000, 0x00001000, 0x00400000, 0x80401000, 0x00000040, 0x00400000, 0x80001000, 0x00001040, 0x80400040, 0x80000000, 0x00001040, 0x00400040, 0x00001000, 0x00401040,
          0x80401040, 0x80000040, 0x00400040, 0x80400000, 0x00401000, 0x80401040, 0x80000040, 0x00000000, 0x00000000, 0x00401000, 0x00001040, 0x00400040, 0x80400040, 0x80000000, 0x80401000,
          0x80001040, 0x80001040, 0x00000040, 0x80401040, 0x80000040, 0x80000000, 0x00001000, 0x80400000, 0x80001000, 0x00401040, 0x80400040, 0x80001000, 0x00001040, 0x00400000, 0x80401000,
          0x00000040, 0x00400000, 0x00001000, 0x00401040
      },
      {
          0x00000080, 0x01040080, 0x01040000, 0x21000080, 0x00040000, 0x00000080, 0x20000000, 0x01040000, 0x20040080, 0x00040000, 0x01000080, 0x20040080, 0x21000080, 0x21040000, 0x00040080,
          0x20000000, 0x01000000, 0x20040000, 0x20040000, 0x00000000, 0x20000080, 0x21040080, 0x21040080, 0x01000080, 0x21040000, 0x20000080, 0x00000000, 0x21000000, 0x01040080, 0x01000000,
          0x21000000, 0x00040080, 0x00040000, 0x21000080, 0x00000080, 0x01000000, 0x20000000, 0x01040000, 0x21000080, 0x20040080, 0x01000080, 0x20000000, 0x21040000, 0x01040080, 0x20040080,
          0x00000080, 0x01000000, 0x21040000, 0x21040080, 0x00040080, 0x21000000, 0x21040080, 0x01040000, 0x00000000, 0x20040000, 0x21000000, 0x00040080, 0x01000080, 0x20000080, 0x00040000,
          0x00000000, 0x20040000, 0x01040080, 0x20000080
      },
      {
          0x10000008, 0x10200000, 0x00002000, 0x10202008, 0x10200000, 0x00000008, 0x10202008, 0x00200000, 0x10002000, 0x00202008, 0x00200000, 0x10000008, 0x00200008, 0x10002000, 0x10000000,
          0x00002008, 0x00000000, 0x00200008, 0x10002008, 0x00002000, 0x00202000, 0x10002008, 0x00000008, 0x10200008, 0x10200008, 0x00000000, 0x00202008, 0x10202000, 0x00002008, 0x00202000,
          0x10202000, 0x10000000, 0x10002000, 0x00000008, 0x10200008, 0x00202000, 0x10202008, 0x00200000, 0x00002008, 0x10000008, 0x00200000, 0x10002000, 0x10000000, 0x00002008, 0x10000008,
          0x10202008, 0x00202000, 0x10200000, 0x00202008, 0x10202000, 0x00000000, 0x10200008, 0x00000008, 0x00002000, 0x10200000, 0x00202008, 0x00002000, 0x00200008, 0x10002008, 0x00000000,
          0x10202000, 0x10000000, 0x00200008, 0x10002008
      },
      {
          0x00100000, 0x02100001, 0x02000401, 0x00000000, 0x00000400, 0x02000401, 0x00100401, 0x02100400, 0x02100401, 0x00100000, 0x00000000, 0x02000001, 0x00000001, 0x02000000, 0x02100001,
          0x00000401, 0x02000400, 0x00100401, 0x00100001, 0x02000400, 0x02000001, 0x02100000, 0x02100400, 0x00100001, 0x02100000, 0x00000400, 0x00000401, 0x02100401, 0x00100400, 0x00000001,
          0x02000000, 0x00100400, 0x02000000, 0x00100400, 0x00100000, 0x02000401, 0x02000401, 0x02100001, 0x02100001, 0x00000001, 0x00100001, 0x02000000, 0x02000400, 0x00100000, 0x02100400,
          0x00000401, 0x00100401, 0x02100400, 0x00000401, 0x02000001, 0x02100401, 0x02100000, 0x00100400, 0x00000000, 0x00000001, 0x02100401, 0x00000000, 0x00100401, 0x02100000, 0x00000400,
          0x02000001, 0x02000400, 0x00000400, 0x00100001
      },
      {
          0x08000820, 0x00000800, 0x00020000, 0x08020820, 0x08000000, 0x08000820, 0x00000020, 0x08000000, 0x00020020, 0x08020000, 0x08020820, 0x00020800, 0x08020800, 0x00020820, 0x00000800,
          0x00000020, 0x08020000, 0x08000020, 0x08000800, 0x00000820, 0x00020800, 0x00020020, 0x08020020, 0x08020800, 0x00000820, 0x00000000, 0x00000000, 0x08020020, 0x08000020, 0x08000800,
          0x00020820, 0x00020000, 0x00020820, 0x00020000, 0x08020800, 0x00000800, 0x00000020, 0x08020020, 0x00000800, 0x00020820, 0x08000800, 0x00000020, 0x08000020, 0x08020000, 0x08020020,
          0x08000000, 0x00020000, 0x08000820, 0x00000000, 0x08020820, 0x00020020, 0x08000020, 0x08020000, 0x08000800, 0x08000820, 0x00000000, 0x08020820, 0x00020800, 0x00020800, 0x00000820,
          0x00000820, 0x00020020, 0x08000000, 0x08020800
      }
  };

  /** The keys. */
  protected long[] keys;

  /**
   * Creates the instance of the <code>DesCipher</code>.
   */
  public CipherDES() {
    keys = makeKeys(0x00L);
  }

  /**
   * Sets the key to use for the nex encryption/decryption.
   * @param key The key to be used.
   * @param keyOffset The key offset.
   */
  public final void setKey(final byte[] key, final int keyOffset) {
    keys = makeKeys(BaseApp.encodeBytesToLong(key, keyOffset));
  }

  /**
   * Encrypts the block of bytes using the key set for the DesCipher.
   * @param plainBytes The bytes of the plaintext.
   * @param plainBytesOffset The plaintext offset.
   * @param cipherBytes The bytes of the ciphertext.
   * @param cipherBytesOffset The ciphertext offset.
   */
  public final void encryptBlock(final byte[] plainBytes, final int plainBytesOffset, final byte[] cipherBytes, final int cipherBytesOffset) {
    final long plainText = BaseApp.encodeBytesToLong(plainBytes, plainBytesOffset);
    final long cipherText = encrypt(plainText);
    BaseApp.encodeLongToBytes(cipherText, cipherBytes, cipherBytesOffset);
  }

  /**
   * Decrypts the block of bytes using the key set for the DesCipher.
   * @param cipherBytes The bytes of the ciphertext.
   * @param cipherBytesOffset The ciphertext offset.
   * @param plainBytes The plaintext bytes.
   * @param plainBytesOffset The plaintext offset.
   */
  public void decryptBlock(final byte[] cipherBytes, final int cipherBytesOffset, final byte[] plainBytes, final int plainBytesOffset) {
    final long cipherText = BaseApp.encodeBytesToLong(cipherBytes, cipherBytesOffset);
    final long plainText = decrypt(cipherText);
    BaseApp.encodeLongToBytes(plainText, plainBytes, plainBytesOffset);
  }

  /**
   * Encrypts the 8 byte using the currect cypher key.
   * 
   * @param plain the plain
   * @return the long
   */
  private final long encrypt(final long plain) {
    // Initial permutation
    final long x = initialPerm(plain);
    int l = (int)(x >>> 32);
    int r = (int)x;
    int tmp;
    for (int i = 0; i < 16; i++) {
      tmp = desFunc(r, keys[i]) ^ l;
      l = r;
      r = tmp;
    }
    // Final permutation
    final long y = ((long)r << 32) | (l & 0xffffffffL);
    return finalPerm(y);
  }

  /**
   * Decrypts the 8 bytes using the current key.
   * 
   * @param cipher the cipher
   * @return the long
   */
  private final long decrypt(final long cipher) {
    // Initial permutation
    final long x = initialPerm(cipher);
    int l = (int)(x >>> 32);
    int r = (int)x;
    int tmp;
    for (int i = 15; i >= 0; i--) {
      tmp = desFunc(r, keys[i]) ^ l;
      l = r;
      r = tmp;
    }
    // Final permutation
    final long y = ((long)r << 32) | (l & 0xffffffffL);
    return finalPerm(y);
  }

  /**
   * Sets the parity.
   * 
   * @param key the key
   * @return the long
   */
  private final long paritySet(final long key) {
    final long pKey = (key >> 1) ^ (key >> 2) ^ (key >> 3) ^ (key >> 4) ^ (key >> 5) ^ (key >> 6) ^ (key >> 7);
    return (key | 0x0101010101010101L) ^ (pKey & 0x0101010101010101L);
  }

  /**
   * Checks for the key parity.
   * 
   * @param key the key
   * @return true, if is parity
   */
  public final boolean isParity(final long key) {
    return (key == paritySet(key));
  }

  /**
   * Creates the key array.
   * 
   * @param key the key
   * @return the long[]
   */
  private final long[] makeKeys(final long key) {
    final long reduced = perm(key, keyReducePerm);
    final int l = (int)(reduced >> 28);
    final int r = (int)(reduced & 0xfffffff);
    final long[] tempKeys = new long[16];
    for (int i = 0; i < 16; ++i) {
      tempKeys[i] = perm(rotate(l, r, keyRot[i]), keyCompressPerm);
    }
    return tempKeys;
  }

  /**
   * Performs the initial permutation.
   * 
   * @param x the x
   * @return the long
   */
  private final long initialPerm(final long x) {
    return perm(x, initPerm);
  }

  /**
   * Expansion pBox and sBox functions.
   * 
   * @param x the x
   * @param k the k
   * @return the int
   */
  private final int desFunc(final int x, final long k) {
    int p = x >>> 27;
    final int q = (p & 3) << 4;
    int r = x << 5;
    p |= r;
    r = sBoxP[0][(int)((k >> 42) ^ p) & 0x3f];
    p >>>= 4;
    r |= sBoxP[7][(int)((k >> 0) ^ p) & 0x3f];
    p >>>= 4;
    r |= sBoxP[6][(int)((k >> 6) ^ p) & 0x3f];
    p >>>= 4;
    r |= sBoxP[5][(int)((k >> 12) ^ p) & 0x3f];
    p >>>= 4;
    r |= sBoxP[4][(int)((k >> 18) ^ p) & 0x3f];
    p >>>= 4;
    r |= sBoxP[3][(int)((k >> 24) ^ p) & 0x3f];
    p >>>= 4;
    r |= sBoxP[2][(int)((k >> 30) ^ p) & 0x3f];
    p >>>= 4;
    r |= sBoxP[1][(int)((k >> 36) ^ (p | q)) & 0x3f];
    return r;
  }

  /**
   * Performs the final permutation.
   * 
   * @param x the x
   * @return the long
   */
  private final long finalPerm(final long x) {
    return perm(x, finPerm);
  }

  /**
   * Performs the permutation.
   * 
   * @param k the k
   * @param p the p
   * @return the long
   */
  private final long perm(final long k, final int p[]) {
    long s = 0;
    final int pLength = p.length;
    for (int i = 0; i < pLength; i++) {
      if ((k & (1L << p[i])) != 0) {
        s |= 1L << i;
      }
    }
    return s;
  }

  /**
   * Performs the rotation.
   * 
   * @param l the l
   * @param r the r
   * @param s the s
   * @return the long
   */
  private final long rotate(final int l, final int r, final int s) {
    return ((long)(((l << s) & 0xfffffff) | (l >>> (28 - s))) << 28) | ((r << s) & 0xfffffff) | (r >> (28 - s));
  }

  /**
   * Encode.
   * 
   * @param plainText the plain text
   * @param key the key
   * @return the byte[]
   */
  public byte[] encode(final byte[] plainText, final byte[] key) {
    // Create the result and complement the plainText array with 0x00s
    final int plainTextLength = plainText.length;
    final byte[] cipherText = new byte[(plainTextLength / 8 + 1) * 8];
    final byte[] completePlainText = new byte[(plainTextLength / 8 + 1) * 8];
    System.arraycopy(plainText, 0, completePlainText, 0, plainTextLength);
    // Encript the plaintext
    synchronized (this) {
      final int blocksNumber = plainTextLength / 8 + 1;
      for (int i = 0; i < blocksNumber; i++) {
        setKey(key, 0);
        encryptBlock(completePlainText, i * 8, cipherText, i * 8);
        setKey(key, 8);
        decryptBlock(completePlainText, i * 8, cipherText, i * 8);
        setKey(key, 0);
        encryptBlock(completePlainText, i * 8, cipherText, i * 8);
      }
    }
    return cipherText;
  }

  /**
   * Decode.
   * 
   * @param cipherText the cipher text
   * @param key the key
   * @return the byte[]
   */
  public byte[] decode(final byte[] cipherText, final byte[] key) {
    // Create the result and complement the plainText array with 0x00s
    final int cipherTextLength = cipherText.length;
    final byte[] plainText = new byte[(cipherTextLength / 8 + 1) * 8];
    final byte[] completeCipherText = new byte[(cipherTextLength / 8 + 1) * 8];
    System.arraycopy(cipherText, 0, completeCipherText, 0, cipherTextLength);
    // Decript the plain text
    synchronized (this) {
      final int blocksNumber = cipherTextLength / 8 + 1;
      for (int i = 0; i < blocksNumber; i++) {
        setKey(key, 0);
        decryptBlock(completeCipherText, i * 8, plainText, i * 8);
        setKey(key, 8);
        encryptBlock(completeCipherText, i * 8, plainText, i * 8);
        setKey(key, 0);
        decryptBlock(completeCipherText, i * 8, plainText, i * 8);
      }
    }
    // Detect EOL as 0x00 byte
    final int plainTextLength = plainText.length;
    int completePlainTextLength = 0;
    while ((completePlainTextLength < plainTextLength) && (plainText[completePlainTextLength] != 0)) {
      completePlainTextLength++;
    }
    // Copy the resulting array
    final byte[] completePlainText = new byte[completePlainTextLength];
    System.arraycopy(plainText, 0, completePlainText, 0, completePlainTextLength);
    return completePlainText;
  }

}
