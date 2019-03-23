/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - GPL >= 3.0
 *
 * Based upon RSS Reader MIDlet
 * 
 * Portion Copyright (C) 2004 Gösta Jonasson
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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * The Class NewsReader.
 */
public class MyMIDlet extends Application {

  // Nome Applicazione
  /** The Constant MSG_APPLICATION. */
  public static final int MSG_APPLICATION = 0;
  // Comandi di default
  public static final int MSG_CM_OK = 1;
  public static final int MSG_CM_BACK = 2;
  public static final int MSG_CM_EXIT = 3;
  public static final int MSG_CM_YES = 4;
  public static final int MSG_CM_NO = 5;

  // Message for Menu
  public static final int MSG_ME_ABOUT = 6;
  public static final int MSG_ME_NOTHING = 7;

  // Main Menu 
  public static final int ME_MAINMENU = 0;

  // Actions
  public static final int AC_DOABOUT = 1;
  public static final int AC_DONOTHING = 2;

  // External resources
  public static final String RES_MESSAGES = "messages.txt";
  public static final String RES_ABOUT = "about.txt";

  private List sMainMenu;

  /**
   * Instantiates a new news reader.
   */
  public MyMIDlet() {
    super();
    BaseApp.resPrefix = "app";
    Application.messages = BaseApp.readStrings(MyMIDlet.RES_MESSAGES);
    Application.cOK = Application.newCommand(MyMIDlet.MSG_CM_OK, Command.OK, 30, Application.AC_NONE);
    Application.cBACK = Application.newCommand(MyMIDlet.MSG_CM_BACK, Command.BACK, 20, Application.AC_BACK);
    Application.cEXIT = Application.newCommand(MyMIDlet.MSG_CM_EXIT, Command.EXIT, 10, Application.AC_EXIT);
    Application.menu = new short[][] {
        {
            MyMIDlet.ME_MAINMENU, MyMIDlet.MSG_ME_ABOUT, MyMIDlet.AC_DOABOUT, -1
        }, {
            MyMIDlet.ME_MAINMENU, MyMIDlet.MSG_ME_NOTHING, MyMIDlet.AC_DONOTHING, -1
        }
    };
  }

  private List getMainMenu() {
    if (sMainMenu == null) {
      sMainMenu = Application.getMenu(Application.messages[MSG_APPLICATION], ME_MAINMENU, -1, Application.cEXIT);
    }
    return sMainMenu;
  }

  /**
   * Start the MIDlet.
   */
  public void init() {
    super.init();
    Application.show(null, getMainMenu(), true);
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.app.Application#handleAction(int, javax.microedition.lcdui.Displayable, javax.microedition.lcdui.Command)
   */
  public boolean handleAction(int action, final Displayable d, final Command c) {
    switch (action) {
      case AC_EXIT:
        return false;
      case AC_BACK:
        doBack();
        return false;
      case AC_DOABOUT:
        Application.show(null, Application.getTextForm(MyMIDlet.MSG_ME_ABOUT, MyMIDlet.RES_ABOUT), true);
        break;
    }
    return true;
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.app.BaseApp#changed(int, javax.microedition.lcdui.Displayable, javax.microedition.lcdui.Displayable)
   */
  public void changed(final int event, final Displayable previous, final Displayable next) {
    if (event == Application.EV_BEFORECHANGE) {
    }
  }

}
