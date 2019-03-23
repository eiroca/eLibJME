/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - GPL >= 3.0
 *
 * Portion Copyright (C) M. Serhat Cinar, http://graviton.de
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
package net.eiroca.j2me.observable;

import java.util.Vector;

/**
 * The Class ObserverManager.
 */
public class ObserverManager {

  /** The observers. */
  private final Vector observers;

  /**
   * Instantiates a new observer manager.
   */
  public ObserverManager() {
    observers = new Vector();
  }

  /**
   * Adds the observer.
   * 
   * @param observer the observer
   */
  public void addObserver(final Observer observer) {
    if (!observers.contains(observer)) {
      observers.addElement(observer);
    }
  }

  /**
   * Removes the observer.
   * 
   * @param observer the observer
   */
  public void removeObserver(final Observer observer) {
    if (observers.contains(observer)) {
      observers.removeElement(observer);
    }
  }

  /**
   * Notify observers.
   * 
   * @param observable the observable
   */
  public void notifyObservers(final Observable observable) {
    for (int i = 0; i < observers.size(); i++) {
      ((Observer)observers.elementAt(i)).changed(observable);
    }
  }

}
