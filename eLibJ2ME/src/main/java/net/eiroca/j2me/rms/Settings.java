/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - GPL >= 3.0
 *
 * Portion Copyright (c) 2004 Ang Kok Chai
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
package net.eiroca.j2me.rms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.debug.Debug;

/**
 * The Class Settings.
 */
public class Settings {

  /** The Constant RMS_PROPERTIES. */
  private static final String RMS_PROPERTIES = "properties";

  /** The Constant properties. */
  private static final Hashtable properties = new Hashtable();

  /** The values changed. */
  private static boolean valuesChanged = false;

  /**
   * Load properties.
   */
  public static void load() {
    Settings.properties.clear();
    final RecordStore rs = BaseApp.getRecordStore(Settings.RMS_PROPERTIES, true, true);
    try {
      final RecordEnumeration e = rs.enumerateRecords(null, null, false);
      if (e.hasNextElement()) {
        final byte[] data = e.nextRecord();
        final DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
        for (int i = dataIn.readInt() - 1; i >= 0; i--) {
          Settings.properties.put(dataIn.readUTF(), dataIn.readUTF());
        }
        dataIn.close();
      }
    }
    catch (final Exception e) {
      Debug.ignore(e);
    }
    Settings.valuesChanged = false;
  }

  /**
   * Set property.
   * 
   * @param name the name
   * @param value the value
   */
  public static void put(final String name, final String value) {
    if (value != null) {
      Settings.properties.put(name, value);
    }
    else {
      Settings.properties.remove(name);
    }
    Settings.valuesChanged = true;
  }

  /**
   * Put int.
   * 
   * @param name the name
   * @param value the value
   */
  public static void putInt(final String name, final int value) {
    Settings.properties.put(name, new Integer(value));
    Settings.valuesChanged = true;
  }

  /**
   * Gets the setting value
   * 
   * @param name the name
   * @return the string
   */
  public static String get(final String name) {
    return (String)Settings.properties.get(name);
  }

  /**
   * Get integer property.
   * 
   * @param name the name
   * @param defaultValue the default value
   * @return the int
   */
  public static int getInt(final String name, final int defaultValue) {
    try {
      return ((Integer)Settings.properties.get(name)).intValue();
    }
    catch (final Exception e) {
      Debug.ignore(e);
    }
    return defaultValue;
  }

  /**
   * Get property count.
   * 
   * @return the int
   */
  public static int size() {
    return Settings.properties.size();
  }

  /**
   * Save properties into RecordStore.
   * 
   * @return true, if successful
   */
  public static boolean save() {
    return Settings.save(false);
  }

  /**
   * Save the settings only is they are changed or the forced setting is true.
   * 
   * @param forced if true the settings are save also if not changed.
   * @return true, if successful
   */
  public static boolean save(final boolean forced) {
    if (forced || Settings.valuesChanged) {
      try {
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        final DataOutputStream dataOut = new DataOutputStream(byteOut);
        dataOut.writeInt(Settings.properties.size());
        final Enumeration keys = Settings.properties.keys();
        while (keys.hasMoreElements()) {
          final String key = keys.nextElement().toString();
          dataOut.writeUTF(key);
          dataOut.writeUTF(Settings.properties.get(key).toString());
        }
        final byte[] data = byteOut.toByteArray();
        byteOut.close();
        dataOut.close();
        try {
          final RecordStore rs = BaseApp.getRecordStore(Settings.RMS_PROPERTIES, true, true);
          final RecordEnumeration e = rs.enumerateRecords(null, null, false);
          if (e.hasNextElement()) {
            rs.setRecord(e.nextRecordId(), data, 0, data.length);
          }
          else {
            rs.addRecord(data, 0, data.length);
          }
          Settings.valuesChanged = false;
          return true;
        }
        catch (final RecordStoreException e) {
          Debug.ignore(e);
        }
      }
      catch (final IOException e) {
        Debug.ignore(e);
      }
    }
    return false;
  }

}
