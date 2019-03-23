/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - GPL >= 3.0
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
package net.eiroca.j2me.app;

import java.util.Hashtable;

/**
 * The Class Device.
 */
public class Device {

  /** The self. */
  private static Device self = null;

  /** The Constant LOCALE. */
  public static final String LOCALE = "lo";

  /** The Constant PLATFORM. */
  public static final String PLATFORM = "pl";

  /** The prop. */
  public static Hashtable prop = new Hashtable();

  /**
   * Instantiates a new device.
   */
  private Device() {
    String locale = BaseApp.readProperty("microedition.locale", null);
    if (locale != null) {
      final int ps = locale.indexOf("-");
      if (ps > 0) {
        locale = locale.substring(0, ps);
      }
      if (locale.length() == 0) {
        locale = null;
      }
      if (locale != null) {
        Device.prop.put(Device.LOCALE, locale);
      }
    }
    final String platform = BaseApp.readProperty("microedition.platform", null);
    Device.prop.put(Device.PLATFORM, platform);
  }

  /**
   * Inits the.
   */
  public static void init() {
    if (Device.self == null) {
      Device.self = new Device();
    }
  }

  /**
   * Gets the locale.
   * 
   * @return the locale
   */
  public static final String getLocale() {
    return (String)Device.prop.get(Device.LOCALE);
  }

  /**
   * Gets the platform.
   * 
   * @return the platform
   */
  public static final String getPlatform() {
    return (String)Device.prop.get(Device.PLATFORM);
  }

}
