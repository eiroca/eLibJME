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
package net.eiroca.j2me.debug;

/**
 * The Class DebugMessage.
 */
public class DebugMessage {

  /** The when. */
  public long when;

  /** The priority. */
  public int priority;

  /** The message. */
  public String message;

  /** The err. */
  public Throwable err;

  /**
   * Instantiates a new debug message.
   * 
   * @param priority the priority
   * @param message the message
   * @param err the err
   */
  public DebugMessage(final int priority, final String message, final Throwable err) {
    when = System.currentTimeMillis();
    this.priority = priority;
    this.message = message;
    this.err = err;
  }

}
