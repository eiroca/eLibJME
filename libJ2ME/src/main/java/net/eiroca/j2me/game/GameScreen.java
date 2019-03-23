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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

/**
 * The Class GameScreen.
 */
abstract public class GameScreen extends GameCanvas {

  /** The midlet. */
  protected final GameApp midlet;

  /** The screen. */
  protected Graphics screen;

  /** The screen width. */
  protected int screenWidth;

  /** The screen height. */
  protected int screenHeight;

  /** The full screen mode. */
  protected final boolean fullScreenMode;

  /** The active. */
  protected boolean active = false;

  /** The animation thread. */
  protected GameThread animationThread;

  /** The name. */
  public String name;

  /** The score. */
  public Score score;

  public int frameRate = 25;

  /**
   * Instantiates a new game screen.
   * 
   * @param aMidlet the a midlet
   * @param suppressKeys the suppress keys
   * @param fullScreen the full screen
   */
  public GameScreen(final GameApp aMidlet, final boolean suppressKeys, final boolean fullScreen, int frameRate) {
    super(suppressKeys);
    this.frameRate = frameRate;
    midlet = aMidlet;
    fullScreenMode = fullScreen;
    setFullScreenMode(fullScreenMode);
    score = new Score();
  }

  /**
   * Inits the graphics.
   * 
   * @return the graphics
   */
  public void initGraphics() {
    screen = getGraphics();
    screenWidth = screen.getClipWidth();
    screenHeight = screen.getClipHeight();
  }

  /**
   * Inits the.
   */
  public void init() {
    active = true;
    initGraphics();
  }

  /**
   * Show.
   */
  public void show() {
    setFullScreenMode(fullScreenMode);
    synchronized (this) {
      animationThread = new GameThread(this, frameRate);
      // animationThread.freeze(200);
      animationThread.start();
    }
  }

  /**
   * Tick.
   * 
   * @return true, if successful
   */
  abstract public boolean tick();

  /**
   * Hide.
   */
  public void hide() {
    synchronized (this) {
      if (animationThread != null) {
        animationThread.stopped = true;
        animationThread = null;
        setFullScreenMode(false);
      }
    }
  }

  /**
   * Done.
   */
  public void done() {
    hide();
    active = false;
    score.endGame();
  }

  /**
   * Checks if is active.
   * 
   * @return true, if is active
   */
  public final boolean isActive() {
    return active;
  }

}
