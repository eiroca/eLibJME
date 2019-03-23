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
package net.eiroca.j2me.game.tpg;

/**
 * The Interface GameTable.
 */
public interface GameTable {

  /**
   * Copy from.
   * 
   * @return the game table
   */
  public GameTable copyFrom();

  /**
   * Gets the empty move.
   * 
   * @return the empty move
   */
  public GameMove getEmptyMove();

}
