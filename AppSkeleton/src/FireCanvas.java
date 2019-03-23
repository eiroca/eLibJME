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
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;

/**
 * The Class FireCanvas.
 */
public final class FireCanvas extends Canvas implements CommandListener, Runnable {

  /** The midlet. */
  private final FireDemo midlet;

  /** The display. */
  private final Display display;

  /** The paused. */
  private boolean paused;

  /** The paint thread. */
  private Thread paintThread;

  /** The painting. */
  private boolean painting;

  /** The computing. */
  private boolean computing;

  /** The erase. */
  private boolean erase;

  /** The pixels. */
  private int pixels[][];

  /** The random. */
  private long random;

  /** The info form. */
  private final Form infoForm;

  /** The info command. */
  private final Command infoCommand;

  /** The exit command. */
  private final Command exitCommand;

  /** The ok command. */
  private final Command okCommand;

  /** The width. */
  private static int width;

  /** The height. */
  private static int height;

  /** The block size. */
  private static int blockSize;

  /** The x pixel nb. */
  private static int xPixelNb;

  /** The y pixel nb. */
  private static int yPixelNb;

  /** The x screen pos. */
  private static int xScreenPos;

  /** The y screen pos. */
  private static int yScreenPos;

  /** The is color. */
  private static boolean isColor;

  /** The Constant INFO_TEXT. */
  private final static String INFO_TEXT = "[UP/DOWN] changes screen size\n[LEFT/RIGHT] changes resolution\n\nv1.0\nmaxence@javatwork.com";

  /** The Constant DEFAULT_BLOCK_SIZE. */
  private final static int DEFAULT_BLOCK_SIZE = 4;

  /** The Constant DEFAULT_MAX_SCREEN_DIM. */
  private final static int DEFAULT_MAX_SCREEN_DIM = 64;

  /** The Constant MIN_SCREEN_DIM. */
  private final static int MIN_SCREEN_DIM = 16;

  /**
   * Instantiates a new fire canvas.
   * 
   * @param midlet the midlet
   * @param display the display
   */
  public FireCanvas(final FireDemo midlet, final Display display) {
    this.midlet = midlet;
    this.display = display;
    FireCanvas.isColor = display.isColor();
    // fire width and height are set to a multiple of 8
    FireCanvas.width = getWidth();
    FireCanvas.width = FireCanvas.width - FireCanvas.width % 8;
    FireCanvas.height = getHeight();
    FireCanvas.height = FireCanvas.height - FireCanvas.height % 8;
    while (((FireCanvas.width > FireCanvas.DEFAULT_MAX_SCREEN_DIM) || (FireCanvas.height > FireCanvas.DEFAULT_MAX_SCREEN_DIM))
        && ((FireCanvas.width >= FireCanvas.MIN_SCREEN_DIM + 8) && (FireCanvas.height >= FireCanvas.MIN_SCREEN_DIM + 8))) {
      FireCanvas.width -= 8;
      FireCanvas.height -= 8;
    }
    FireCanvas.blockSize = FireCanvas.DEFAULT_BLOCK_SIZE;
    computeDimensions();
    infoCommand = new Command("Info", Command.SCREEN, 2);
    exitCommand = new Command("Exit", Command.EXIT, 2);
    addCommand(infoCommand);
    addCommand(exitCommand);
    infoForm = new Form(null);
    infoForm.append(FireCanvas.INFO_TEXT);
    okCommand = new Command("Ok", Command.OK, 2);
    infoForm.addCommand(okCommand);
    infoForm.setCommandListener(this);
    setCommandListener(this);
  }

  /**
   * Starts painting thread.
   */
  public void start() {
    paintThread = new Thread(this);
    paintThread.start();
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
   */
  public void paint(final Graphics g) {
    if (paused) { return; }
    painting = true;
    if (erase) {
      if (FireCanvas.isColor) {
        g.setColor(255, 255, 255);
      }
      else {
        g.setGrayScale(255);
      }
      g.fillRect(0, 0, getWidth(), getHeight());
      erase = false;
    }
    int xPos;
    int yPos = FireCanvas.yScreenPos;
    if (FireCanvas.isColor) {
      for (int y = 0; y < FireCanvas.yPixelNb; y++) {
        xPos = FireCanvas.xScreenPos;
        for (int x = 0; x < FireCanvas.xPixelNb; x++) {
          g.setColor(255, pixels[x][y], 0);
          g.fillRect(xPos, yPos, FireCanvas.blockSize, FireCanvas.blockSize);
          xPos += FireCanvas.blockSize;
        }
        yPos += FireCanvas.blockSize;
      }
    }
    else {
      for (int y = 0; y < FireCanvas.yPixelNb; y++) {
        xPos = FireCanvas.xScreenPos;
        for (int x = 0; x < FireCanvas.xPixelNb; x++) {
          g.setGrayScale(pixels[x][y]);
          g.fillRect(xPos, yPos, FireCanvas.blockSize, FireCanvas.blockSize);
          xPos += FireCanvas.blockSize;
        }
        yPos += FireCanvas.blockSize;
      }
    }
    painting = false;
  }

  /**
   * Compute dimensions.
   */
  private void computeDimensions() {
    FireCanvas.xPixelNb = FireCanvas.width / FireCanvas.blockSize;
    FireCanvas.yPixelNb = FireCanvas.height / FireCanvas.blockSize;
    FireCanvas.xScreenPos = (getWidth() - FireCanvas.width) / 2;
    FireCanvas.yScreenPos = (getHeight() - FireCanvas.height) / 2;
    pixels = new int[FireCanvas.xPixelNb][FireCanvas.yPixelNb + 1];
  }

  /**
   * Computes a new frame.
   */
  public void computePixels() {
    if (paused) { return; }
    computing = true;
    int temp1;
    int temp2;
    int temp3;
    for (int y = 0; y < FireCanvas.yPixelNb; y++) {
      temp2 = pixels[0][y + 1];
      temp3 = pixels[1][y + 1];
      pixels[0][y] = (pixels[0][y] + temp2 + temp3) / 3;
      for (int x = 1; x < FireCanvas.xPixelNb - 1; x++) {
        temp1 = temp2;
        temp2 = temp3;
        temp3 = pixels[x + 1][y + 1];
        // New pixel is an average of 4 surrounding pixels
        // Bit shifting is cheaper than integer division
        pixels[x][y] = (pixels[x][y] + temp1 + temp2 + temp3) >> 2;
      }
      pixels[FireCanvas.xPixelNb - 1][y] = (pixels[FireCanvas.xPixelNb - 1][y] + temp2 + temp3) / 3;
    }
    // Randomize last line (this line is not displayed)
    for (int x = 0; x < FireCanvas.xPixelNb; x++) {
      // Cheap random
      random = random + System.currentTimeMillis();
      pixels[x][FireCanvas.yPixelNb] = (int) (random % 255);
    }
    computing = false;
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
   */
  public void commandAction(final Command c, final Displayable s) {
    if (c == infoCommand) {
      // Waits for current painting and computing jobs to finish
      paused = true;
      while (painting || computing) {
        try {
          Thread.sleep(5);
        }
        catch (final Exception e) {
          //
        }
      }
      display.setCurrent(infoForm);
    }
    else if (c == okCommand) {
      // Let's get back to work
      paused = false;
      display.setCurrent(this);
    }
    else if (c == exitCommand) {
      midlet.destroyApp(true);
      midlet.notifyDestroyed();
    }
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.Canvas#keyPressed(int)
   */
  public void keyPressed(final int keyCode) {
    while (painting) {
      try {
        Thread.sleep(5);
      }
      catch (final Exception e) {
        //
      }
    }
    paused = true;
    switch (getGameAction(keyCode)) {
      case UP:
        if ((FireCanvas.width < getWidth() - 7) && (FireCanvas.height < getHeight() - 7)) {
          FireCanvas.width += 8;
          FireCanvas.height += 8;
        }
        break;
      case DOWN:
        if ((FireCanvas.width >= FireCanvas.MIN_SCREEN_DIM + 8) && (FireCanvas.height >= FireCanvas.MIN_SCREEN_DIM + 8)) {
          FireCanvas.width -= 8;
          FireCanvas.height -= 8;
          erase = true;
        }
        break;
      case LEFT:
        if (FireCanvas.blockSize > 1) {
          FireCanvas.blockSize /= 2;
        }
        break;
      case RIGHT:
        if (FireCanvas.blockSize < 8) {
          FireCanvas.blockSize *= 2;
        }
        break;
      default:
        paused = false;
        return;
    }
    computeDimensions();
    paused = false;
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run() {
    while (true) {
      while (paused) {
        // Displays up to 33 fps
        try {
          Thread.sleep(30);
        }
        catch (final Exception e) {
          //
        }
      }
      repaint();
      // Forces paint requests to be served
      serviceRepaints();
    }
  }

}