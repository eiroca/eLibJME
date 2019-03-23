/**
 * GPL >= 3.0 Copyright (C) 2006-2010 eIrOcA (eNrIcO Croce & sImOnA Burzio)
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
package net.eiroca.j2me.eGame;

import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.game.GameApp;
import net.eiroca.j2me.game.GameScreen;

/**
 * The Class EGame.
 */
public class EGameMIDlet extends GameApp {

  /**
   * Instantiates a new e game.
   */
  public EGameMIDlet() {
    super();
    Application.background = 0x00000000;
    Application.foreground = 0x00FFFFFF;
    BaseApp.resPrefix = "eg"; // resource dedicated directory
    GameApp.hsName = "eGame";
    Application.menu = new short[][] {
        {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_CONTINUE, GameApp.GA_CONTINUE, 0
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_NEWGAME, GameApp.GA_NEWGAME, 1
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_HIGHSCORE, GameApp.GA_HIGHSCORE, 2
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_OPTIONS, GameApp.GA_OPTIONS, 3
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_SETTINGS, GameApp.GA_SETTINGS, 4
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_HELP, GameApp.GA_HELP, 5
        }, {
            GameApp.ME_MAINMENU, GameApp.MSG_MENU_MAIN_ABOUT, GameApp.GA_ABOUT, 6
        }
    };
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.game.GameApp#getGameScreen()
   */
  @Override
  public GameScreen getGameScreen() {
    return new EGameScreen(this, true, true);
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.game.GameApp#processGameAction(int)
   */
  @Override
  public void processGameAction(final int action) {
    switch (action) {
      case GA_STARTUP: // Continue
        doStartup();
        break;
      case GA_CONTINUE: // Continue
        doGameResume();
        break;
      case GA_NEWGAME: // New game
        doGameStart();
        break;
      case GA_HIGHSCORE: // High score
        doHighScore();
        break;
      case GA_OPTIONS:
        doShowOptions();
        break;
      case GA_SETTINGS:
        doShowSettings();
        break;
      case GA_HELP:
        doHelp();
        break;
      case GA_ABOUT:
        doAbout();
        break;
      case GA_APPLYSETTINGS:
        doApplySettings();
        break;
      case GA_APPLYOPTIONS:
        doApplyOptions();
        break;
      case GA_NEWHIGHSCORE:
        doSetNewHighScore();
        break;
      default:
        break;
    }
  }

}
