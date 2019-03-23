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

import java.util.Timer;
import java.util.TimerTask;

/**
 * The Class ScheduledWaekup.
 */
public class ScheduledWaekup extends TimerTask {

  /**
   * Setup.
   * 
   * @param listener the listener
   * @param delay the delay
   * @return the timer
   */
  public static Timer setup(final SchedulerNotify listener, final int delay) {
    final Timer myTimer = new Timer();
    myTimer.schedule(new ScheduledWaekup(listener), delay);
    return myTimer;
  }

  /** The listener. */
  private final SchedulerNotify listener;

  /**
   * Instantiates a new scheduled waekup.
   * 
   * @param listener the listener
   */
  public ScheduledWaekup(final SchedulerNotify listener) {
    this.listener = listener;
  }

  /* (non-Javadoc)
   * @see java.util.TimerTask#run()
   */
  public void run() {
    listener.wakeup();
  }

}
