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

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.media.Player;
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.app.SplashScreen;

/**
 * The Class GameApp.
 */
public abstract class GameApp extends Application {

  /** The Constant ME_MAINMENU. */
  public static final short ME_MAINMENU = 0;

  /** The Constant MSG_LABEL_OK. */
  public final static short MSG_LABEL_OK = 0;

  /** The Constant MSG_LABEL_BACK. */
  public final static short MSG_LABEL_BACK = 1;

  /** The Constant MSG_LABEL_EXIT. */
  public final static short MSG_LABEL_EXIT = 2;

  /** The Constant MSG_LABEL_YES. */
  public final static short MSG_LABEL_YES = 3;

  /** The Constant MSG_LABEL_NO. */
  public final static short MSG_LABEL_NO = 4;

  /** The Constant MSG_TEXT_GAMEOVER_01. */
  public final static short MSG_TEXT_GAMEOVER_01 = 5;

  /** The Constant MSG_TEXT_GAMEOVER_02. */
  public final static short MSG_TEXT_GAMEOVER_02 = 6;

  /** The Constant MSG_TEXT_GAMEOVER_03. */
  public final static short MSG_TEXT_GAMEOVER_03 = 7;

  /** The Constant MSG_TEXT_HIGHSCORE_01. */
  public final static short MSG_TEXT_HIGHSCORE_01 = 8;

  /** The Constant MSG_TEXT_HIGHSCORE_02. */
  public final static short MSG_TEXT_HIGHSCORE_02 = 9;

  /** The Constant MSG_TEXT_HIGHSCORE_03. */
  public final static short MSG_TEXT_HIGHSCORE_03 = 10;

  /** The Constant MSG_TEXT_HIGHSCORE_04. */
  public final static short MSG_TEXT_HIGHSCORE_04 = 11;

  /** The Constant MSG_MENU_MAIN_CONTINUE. */
  public final static short MSG_MENU_MAIN_CONTINUE = 12;

  /** The Constant MSG_MENU_MAIN_NEWGAME. */
  public final static short MSG_MENU_MAIN_NEWGAME = 13;

  /** The Constant MSG_MENU_MAIN_HIGHSCORE. */
  public final static short MSG_MENU_MAIN_HIGHSCORE = 14;

  /** The Constant MSG_MENU_MAIN_SETTINGS. */
  public final static short MSG_MENU_MAIN_SETTINGS = 15;

  /** The Constant MSG_MENU_MAIN_OPTIONS. */
  public final static short MSG_MENU_MAIN_OPTIONS = 16;

  /** The Constant MSG_MENU_MAIN_HELP. */
  public final static int MSG_MENU_MAIN_HELP = 17;

  /** The Constant MSG_MENU_MAIN_ABOUT. */
  public final static int MSG_MENU_MAIN_ABOUT = 18;

  /** The Constant MSG_MENU_SETTINGS_VOLUME. */
  public final static short MSG_MENU_SETTINGS_VOLUME = 19;

  /** The Constant MSG_MENU_SETTINGS_VIBRATE. */
  public final static short MSG_MENU_SETTINGS_VIBRATE = 20;

  /** The Constant MSG_MENU_SETTINGS_BACKLIGHT. */
  public final static short MSG_MENU_SETTINGS_BACKLIGHT = 21;

  /** The Constant MSG_USERDEF. */
  public final static short MSG_USERDEF = 22;

  /** The Constant GA_NONE. */
  public static final short GA_NONE = 0;

  /** The Constant GA_STARTUP. */
  public static final short GA_STARTUP = 1;

  /** The Constant GA_CONTINUE. */
  public static final short GA_CONTINUE = 2;

  /** The Constant GA_NEWGAME. */
  public static final short GA_NEWGAME = 3;

  /** The Constant GA_HIGHSCORE. */
  public static final short GA_HIGHSCORE = 4;

  /** The Constant GA_OPTIONS. */
  public static final short GA_OPTIONS = 5;

  /** The Constant GA_SETTINGS. */
  public static final short GA_SETTINGS = 6;

  /** The Constant GA_HELP. */
  public static final short GA_HELP = 7;

  /** The Constant GA_ABOUT. */
  public static final short GA_ABOUT = 8;

  /** The Constant GA_APPLYSETTINGS. */
  public static final short GA_APPLYSETTINGS = 9;

  /** The Constant GA_APPLYOPTIONS. */
  public static final short GA_APPLYOPTIONS = 10;

  /** The Constant GA_NEWHIGHSCORE. */
  public static final short GA_NEWHIGHSCORE = 11;

  /** The Constant GA_USERDEF. */
  public static final short GA_USERDEF = 12;

  /** The Constant FT_SOUNDFX. */
  public static final short FT_SOUNDFX = 1;

  /** The Constant FT_MUSIC. */
  public static final short FT_MUSIC = 2;

  /** The Constant FT_AUDIO. */
  public static final short FT_AUDIO = GameApp.FT_MUSIC + GameApp.FT_SOUNDFX;

  /** The Constant FT_VIBRATE. */
  public static final short FT_VIBRATE = 4;

  /** The Constant FT_LIGHT. */
  public static final short FT_LIGHT = 8;

  /** The Constant RES_MENUICON. */
  public static final String RES_MENUICON = "menu.png";

  /** The Constant RES_ABOUT. */
  public static final String RES_ABOUT = "about.txt";

  /** The Constant RES_HELP. */
  public static final String RES_HELP = "help.txt";

  /** The Constant RES_MSGS. */
  public static final String RES_MSGS = "messages.txt";

  /** The Constant RES_SPLASH. */
  public static String resSplash = "splash.png";

  /** The us vibrate. */
  public static boolean usVibrate = true;

  /** The us back light. */
  public static boolean usBackLight = true;

  /** The us volume. */
  public static int usVolume = 100;

  /** The RM s_ highscore. */
  public static String RMS_HIGHSCORE = "HighScore";

  /** The hs name. */
  public static String hsName = "HighScore";

  /** The hs level. */
  public static int hsLevel = 0;

  /** The hs max level. */
  public static int hsMaxLevel = 1;

  /** The hs max score. */
  public static int hsMaxScore = 5;

  /** The game. */
  public static GameScreen game;

  /** The highscore. */
  public static ScoreManager highscore;

  /** The game menu. */
  protected List gameMenu;

  /** The game settings. */
  protected Displayable gameSettings;

  /** The game options. */
  protected Displayable gameOptions;

  /** The game new high score. */
  protected Displayable gameNewHighScore;

  /** The t name. */
  private TextField tName;

  /** The t score. */
  private StringItem tScore;

  /**
   * Initialize the game.
   */
  public void init() {
    super.init();
    Application.messages = BaseApp.readStrings(GameApp.RES_MSGS);
    Application.icons = BaseApp.splitImages(GameApp.RES_MENUICON, 7, 12, 12);
    GameApp.highscore = new ScoreManager(GameApp.RMS_HIGHSCORE, GameApp.hsName, GameApp.hsMaxLevel, GameApp.hsMaxScore, true);
    GameApp.game = getGameScreen();
    Application.cOK = Application.newCommand(GameApp.MSG_LABEL_OK, Command.OK, 30, Application.AC_NONE);
    Application.cBACK = Application.newCommand(GameApp.MSG_LABEL_BACK, Command.BACK, 20, Application.AC_BACK);
    Application.cEXIT = Application.newCommand(GameApp.MSG_LABEL_EXIT, Command.EXIT, 10, Application.AC_EXIT);
    gameMenu = Application.getMenu(GameApp.game.name, GameApp.ME_MAINMENU, GameApp.GA_CONTINUE, Application.cEXIT);
    processGameAction(GameApp.GA_STARTUP);
  }

  /**
   * Resume the game.
   */
  public void resume() {
    Application.show(null, gameMenu, true);
  }

  /**
   * Pause the game.
   */
  public void pause() {
    doGamePause();
  }

  /**
   * Destroy the game.
   */
  public void done() {
    doGameAbort();
    BaseApp.closeRecordStores();
    super.done();
  }

  /**
   * The game is active?.
   * 
   * @return true, if is active
   */
  private final boolean isActive() {
    return (GameApp.game != null) && (GameApp.game.isActive());
  }

  /**
   * Command dispatcher.
   * 
   * @param c the c
   * @param d the d
   */
  public void commandAction(final Command c, final Displayable d) {
    int gameAction = GameApp.GA_NONE;
    if (d == gameMenu) {
      int index = gameMenu.getSelectedIndex();
      if (!isActive()) {
        if (index >= Application.pSpecial) {
          index++;
        }
      }
      gameAction = Application.menu[index][Application.MD_MENUAC];
    }
    else if (c == Application.cOK) {
      if (d == gameSettings) {
        gameAction = GameApp.GA_APPLYSETTINGS;
      }
      else if (d == gameOptions) {
        gameAction = GameApp.GA_APPLYOPTIONS;
      }
      else if (d == gameNewHighScore) {
        gameAction = GameApp.GA_NEWHIGHSCORE;
      }
    }
    if (c == Application.cEXIT) {
      doExit();
    }
    else if (c == Application.cBACK) {
      doBack();
    }
    else {
      processGameAction(gameAction);
    }
  }

  /**
   * Implements the game action.
   * 
   * @param action the action
   */
  abstract public void processGameAction(int action);

  /* (non-Javadoc)
   * @see net.eiroca.j2me.app.Application#handleAction(int, javax.microedition.lcdui.Displayable, javax.microedition.lcdui.Command)
   */
  public boolean handleAction(final int action, final Displayable d, final Command cmd) {
    return false;
  }

  /**
   * Game Startup.
   */
  public void doStartup() {
    final Displayable splash = getSplash();
    if (splash != null) {
      BaseApp.setDisplay(splash);
    }
    else {
      Application.show(null, gameMenu, true);
    }
  }

  /**
   * Abort the game.
   */
  public void doGameAbort() {
    if (isActive()) {
      GameApp.game.done();
      gameMenu.delete(Application.pSpecial);
    }
  }

  /**
   * Start the game.
   */
  public void doGameStart() {
    doGameAbort();
    Application.insertMenuItem(gameMenu, Application.pSpecial, Application.menu[Application.pSpecial]);
    GameApp.game.init();
    GameApp.game.show();
    Application.show(null, GameApp.game, true);
  }

  /**
   * Resume the game.
   */
  public void doGameResume() {
    if (isActive()) {
      GameApp.game.show();
      Application.show(null, GameApp.game, true);
    }
  }

  /**
   * Pause the game.
   */
  public void doGamePause() {
    if (isActive()) {
      GameApp.game.hide();
      Application.back(null, gameMenu, true);
    }
  }

  /**
   * Stop the game.
   */
  public void doGameStop() {
    if (isActive()) {
      GameApp.game.hide();
      GameApp.game.done();
      gameMenu.delete(Application.pSpecial);
      int score = GameApp.game.score.getScore();
      Score hs = null;
      if (GameApp.hsLevel >= GameApp.hsMaxLevel) {
        score = 0;
      }
      else {
        hs = GameApp.highscore.getHighScore(GameApp.hsLevel);
      }
      if ((score > 0) && (GameApp.highscore.isHighScore(GameApp.hsLevel, GameApp.game.score))) {
        gameNewHighScore = getNewHighScore();
        final StringBuffer buf = new StringBuffer(80);
        buf.append(Application.messages[GameApp.MSG_TEXT_HIGHSCORE_03]).append(GameApp.game.score.getScore()).append(BaseApp.CR);
        tScore.setLabel(buf.toString());
        tName.setString(GameApp.game.score.name);
        BaseApp.setDisplay(gameNewHighScore);
      }
      else {
        final String[] msg = new String[3];
        msg[0] = Application.messages[GameApp.MSG_TEXT_GAMEOVER_01];
        if (score > 0) {
          msg[1] = Application.messages[GameApp.MSG_TEXT_GAMEOVER_02] + score;
        }
        if (hs != null) {
          msg[2] = Application.messages[GameApp.MSG_TEXT_GAMEOVER_03] + hs.getScore();
        }
        GameApp.flashBacklight(1000);
        BaseApp.setDisplay(new GameUIMessage(msg, gameMenu));
      }
    }
    else {
      Application.back(null, gameMenu, false);
    }
  }

  /**
   * Set new high score.
   */
  public void doSetNewHighScore() {
    GameApp.game.score.name = tName.getString();
    GameApp.highscore.addNewScore(GameApp.hsLevel, GameApp.game.score);
    Application.back(null, gameMenu, true);
  }

  /**
   * Show About.
   */
  public void doAbout() {
    final Displayable d = Application.getTextForm(GameApp.MSG_MENU_MAIN_ABOUT, GameApp.RES_ABOUT);
    Application.show(null, d, true);
  }

  /**
   * Show help.
   */
  public void doHelp() {
    final Displayable d = Application.getTextForm(GameApp.MSG_MENU_MAIN_HELP, GameApp.RES_HELP);
    Application.show(null, d, true);
  }

  /**
   * Show high score.
   */
  public void doHighScore() {
    final Displayable d = getHighScore();
    Application.show(null, d, true);
  }

  /**
   * Show game options.
   */
  public void doShowOptions() {
    if (gameOptions == null) {
      gameOptions = getOptions();
    }
    Application.show(null, gameOptions, true);
  }

  /**
   * Apply the game options.
   */
  public void doApplyOptions() {
    Application.back(null);
  }

  /**
   * Show game settings.
   */
  public void doShowSettings() {
    if (gameSettings == null) {
      gameSettings = getSettings();
    }
    GameUISettings.setVals();
    Application.show(null, gameSettings, true);
  }

  /**
   * Apply new game settings.
   */
  public void doApplySettings() {
    GameUISettings.getVals();
    Application.back(null);
  }

  /**
   * Build Splash display.
   * 
   * @return the splash
   */
  protected Displayable getSplash() {
    return new SplashScreen(GameApp.resSplash, gameMenu, 3000, Application.background);
  }

  /**
   * Build High score Display.
   * 
   * @return the high score
   */
  protected Displayable getHighScore() {
    final Form form = new Form(Application.messages[GameApp.MSG_MENU_MAIN_HIGHSCORE]);
    final Vector scores = GameApp.highscore.getList(GameApp.hsLevel);
    if (scores.size() == 0) {
      form.append(Application.messages[GameApp.MSG_TEXT_HIGHSCORE_01]);
    }
    else {
      Score s;
      for (int i = 0; i < scores.size(); i++) {
        s = (Score)scores.elementAt(i);
        final StringBuffer buf = new StringBuffer(80);
        buf.append((i + 1)).append(": ").append(s.getScore()).append(" (").append(s.name).append(')').append(BaseApp.CR);
        form.append(buf.toString());
      }
    }
    Application.setup(form, Application.cBACK, null);
    return form;
  }

  /**
   * Build new high score display.
   * 
   * @return the new high score
   */
  protected Displayable getNewHighScore() {
    final Form form = new Form(Application.messages[GameApp.MSG_TEXT_HIGHSCORE_02]);
    tScore = new StringItem(Application.messages[GameApp.MSG_TEXT_HIGHSCORE_03], null);
    tName = new TextField(Application.messages[GameApp.MSG_TEXT_HIGHSCORE_04], "", 8, TextField.INITIAL_CAPS_SENTENCE);
    form.append(tScore);
    form.append(tName);
    Application.setup(form, Application.cOK, null);
    return form;
  }

  /**
   * Build Options display.
   * 
   * @return the options
   */
  protected Displayable getOptions() {
    return null;
  }

  /**
   * Build Settings display.
   * 
   * @return the settings
   */
  protected GameUISettings getSettings() {
    return null;
  }

  /**
   * Build main game display.
   * 
   * @return the game screen
   */
  abstract protected GameScreen getGameScreen();

  /**
   * Vibrate the handset.
   * 
   * @param millis the millis
   */
  public static void vibrate(final int millis) {
    if (GameApp.usVibrate) {
      BaseApp.display.vibrate(millis);
    }
  }

  /**
   * Flash backlight.
   * 
   * @param millis the millis
   */
  public static void flashBacklight(final int millis) {
    if (GameApp.usBackLight) {
      BaseApp.display.flashBacklight(millis);
    }
  }

  /**
   * Play a sound.
   * 
   * @param p the p
   */
  public static void play(final Player p) {
    if (GameApp.usVolume > 0) {
      try {
        p.start();
      }
      catch (final Exception e) {
        // Nothing to do
      }
    }
  }

}
