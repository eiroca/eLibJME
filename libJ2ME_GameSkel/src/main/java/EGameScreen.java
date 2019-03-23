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
import javax.microedition.lcdui.game.GameCanvas;
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;

/**
 * The Class EGameScreen.
 */
public final class EGameScreen extends GameScreen {

  /**
   * Instantiates a new e game screen.
   * 
   * @param midlet the midlet
   * @param suppressKeys the suppress keys
   * @param fullScreen the full screen
   */
  public EGameScreen(final GameApp midlet, final boolean suppressKeys, final boolean fullScreen) {
    super(midlet, suppressKeys, fullScreen, 20);
    name = "EGame";
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.game.GameScreen#initGraphics()
   */
  public void initGraphics() {
    super.initGraphics();
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.game.GameScreen#init()
   */
  public void init() {
    super.init();
    score.beginGame(1, 0, 0);
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.game.GameScreen#tick()
   */
  public boolean tick() {
    score.addScore(1);
    final int keyStates = getKeyStates();
    if (keyStates == GameCanvas.FIRE_PRESSED) {
      midlet.doGamePause();
    }
    else if (keyStates != 0) {
      midlet.doGameStop();
    }
    draw();
    return true;
  }

  /**
   * Draw.
   */
  public void draw() {
    // clear screen to black
    screen.setColor(Application.background);
    screen.fillRect(0, 0, screenWidth, screenHeight);
    screen.setColor(Application.foreground);
    screen.fillArc(0, 0, 50, 50, 45, 270);
    screen.drawString("Score: " + score.getScore(), 0, 75, 0);
  }

}
