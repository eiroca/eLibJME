/** GPL >= 3.0
 * Copyright (C) 2006-2010 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/
 */
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/**
 * The Class FireDemo.
 */
public final class FireDemo extends MIDlet implements Runnable {

  /** The canvas. */
  FireCanvas canvas;

  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#startApp()
   */
  public void startApp() {
    final Display display = Display.getDisplay(this);
    canvas = new FireCanvas(this, Display.getDisplay(this));
    display.setCurrent(canvas);
    // Starts painting thread
    canvas.start();
    // Starts frame computing thread
    new Thread(this).start();
  }

  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#pauseApp()
   */
  public void pauseApp() {
    //
  }

  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
   */
  public void destroyApp(final boolean unconditional) {
    //
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run() {
    while (true) {
      canvas.computePixels();
      try {
        Thread.sleep(10);
      }
      catch (final Exception e) {
        //
      }
    }
  }

}
