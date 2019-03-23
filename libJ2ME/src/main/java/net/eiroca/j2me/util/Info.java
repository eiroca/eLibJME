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
package net.eiroca.j2me.util;

/**
 * The Class Info.
 */
public class Info {

  /** The category. */
  public String category;

  /** The name. */
  public String name;

  /** The value. */
  public String value;

  /**
   * Instantiates a new info.
   * 
   * @param category the category
   * @param name the name
   * @param value the value
   */
  public Info(final String category, final String name, final String value) {
    this.category = category;
    this.name = name;
    this.value = value;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return name + ": " + value;
  }

}
