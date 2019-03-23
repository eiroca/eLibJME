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
package net.eiroca.j2me.game;

import net.eiroca.j2me.debug.Debug;

/**
 * The Class GameThread.
 */
public class GameThread extends Thread {

  /** The Constant MILLIS_PER_TICK. */
  private int millsPerTick = 1000 / 25;

  /** The screen. */
  public GameScreen screen;

  /** The stopped. */
  public boolean stopped = false;

  /** The frozen. */
  public int frozen = 0;

  /**
   * Instantiates a new game thread.
   * 
   * @param canvas the canvas
   */
  public GameThread(final GameScreen canvas, int frameRate) {
    screen = canvas;
    setFrameRete(frameRate);
  }

  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  public void run() {
    try {
      while (!stopped) {
        if (frozen > 0) {
          synchronized (this) {
            wait(frozen);
            frozen = 0;
          }
        }
        final long drawStartTime = System.currentTimeMillis();
        if ((screen.isShown()) && (screen.tick())) {
          screen.flushGraphics();
        }
        final long timeTaken = System.currentTimeMillis() - drawStartTime;
        if (timeTaken < millsPerTick) {
          synchronized (this) {
            wait(millsPerTick - timeTaken);
          }
        }
        else {
          Thread.yield();
        }
      }
    }
    catch (final InterruptedException e) {
      Debug.ignore(e);
    }
    screen = null;
  }

  public void setFrameRete(int frameRate) {
    millsPerTick = (1000 / frameRate);
    if (millsPerTick < 16) { // max 60FPS
      millsPerTick = 16;
    }
  }

  /**
   * Freeze the redraw for x milliseconds.
   * 
   * @param msec the msec
   */
  public void freeze(int msec) {
    synchronized (this) {
      frozen += msec;
    }
  }

}
