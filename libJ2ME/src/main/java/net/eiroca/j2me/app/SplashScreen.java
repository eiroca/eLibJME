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

import java.util.Timer;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.eiroca.j2me.util.ScheduledWaekup;
import net.eiroca.j2me.util.SchedulerNotify;

/**
 * The Class SplashScreen.
 */
public class SplashScreen extends Canvas implements SchedulerNotify {

  /** The next. */
  protected Displayable next;

  /** The dismissed. */
  private volatile boolean dismissed = false;

  /** The time. */
  private final int time;

  /** The timer. */
  private Timer timer;

  /** The splash image. */
  private Image splashImage;

  private int background;

  /**
   * Instantiates a new splash screen.
   * 
   * @param image the image
   * @param next the next
   * @param time the time
   */
  public SplashScreen(final String image, final Displayable next, final int time, final int background) {
    this.next = next;
    this.time = time;
    this.background = background;
    setFullScreenMode(true);
    if (image != null) {
      splashImage = BaseApp.createImage(image);
    }
    show();
  }

  /**
   * Show.
   */
  public void show() {
    Application.show(null, this, false);
  }

  /**
   * Hide.
   */
  public void hide() {
    Application.show(null, next, true);
  }

  /**
   * Dismiss.
   */
  protected void dismiss() {
    if (!dismissed) {
      dismissed = true;
      if (timer != null) {
        timer.cancel();
        timer = null;
      }
      hide();
    }
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.Canvas#keyPressed(int)
   */
  public void keyPressed(final int keyCode) {
    dismiss();
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
   */
  public void pointerPressed(final int x, final int y) {
    dismiss();
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.Canvas#showNotify()
   */
  public void showNotify() {
    timer = ScheduledWaekup.setup(this, time);
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.util.SchedulerNotify#wakeup()
   */
  public void wakeup() {
    dismiss();
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
   */
  public void paint(final Graphics g) {
    final int width = getWidth();
    final int height = getHeight();
    g.setColor(background);
    g.fillRect(0, 0, width, height);
    if (splashImage != null) {
      g.drawImage(splashImage, width / 2, height / 2, Graphics.VCENTER | Graphics.HCENTER);
    }
  }

}
