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

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import net.eiroca.j2me.app.Application;

/**
 * The Class GameUISettings.
 */
public class GameUISettings extends Form {

  /** The st vibrate. */
  public static ChoiceGroup stVibrate = null;

  /** The st back light. */
  public static ChoiceGroup stBackLight = null;

  /** The st volume. */
  public static Gauge stVolume = null;

  /**
   * Instantiates a new game ui settings.
   * 
   * @param owner the owner
   * @param gmFeature the gm feature
   */
  public GameUISettings(final GameApp owner, final int gmFeature) {
    super(Application.messages[GameApp.MSG_MENU_MAIN_SETTINGS]);
    if ((gmFeature & GameApp.FT_AUDIO) != 0) {
      GameUISettings.stVolume = new Gauge(Application.messages[GameApp.MSG_MENU_SETTINGS_VOLUME], true, 5, 0);
      append(GameUISettings.stVolume);
    }
    if ((gmFeature & GameApp.FT_VIBRATE) != 0) {
      GameUISettings.stVibrate = new ChoiceGroup(Application.messages[GameApp.MSG_MENU_SETTINGS_VIBRATE], Choice.EXCLUSIVE);
      GameUISettings.stVibrate.append(Application.messages[GameApp.MSG_LABEL_YES], null);
      GameUISettings.stVibrate.append(Application.messages[GameApp.MSG_LABEL_NO], null);
      append(GameUISettings.stVibrate);
    }
    if ((gmFeature & GameApp.FT_LIGHT) != 0) {
      GameUISettings.stBackLight = new ChoiceGroup(Application.messages[GameApp.MSG_MENU_SETTINGS_BACKLIGHT], Choice.EXCLUSIVE);
      GameUISettings.stBackLight.append(Application.messages[GameApp.MSG_LABEL_YES], null);
      GameUISettings.stBackLight.append(Application.messages[GameApp.MSG_LABEL_NO], null);
      append(GameUISettings.stBackLight);
    }
    Application.setup(this, Application.cBACK, Application.cOK);
  }

  /**
   * Sets the vals.
   */
  public static void setVals() {
    if (GameUISettings.stVolume != null) {
      GameUISettings.stVolume.setValue(GameApp.usVolume / 20);
    }
    if (GameUISettings.stVibrate != null) {
      if (GameApp.usVibrate) {
        GameUISettings.stVibrate.setSelectedIndex(0, true);
      }
      else {
        GameUISettings.stVibrate.setSelectedIndex(1, true);
      }
    }
    if (GameUISettings.stBackLight != null) {
      if (GameApp.usBackLight) {
        GameUISettings.stBackLight.setSelectedIndex(0, true);
      }
      else {
        GameUISettings.stBackLight.setSelectedIndex(1, true);
      }
    }
  }

  /**
   * Gets the vals.
   * 
   * @return the vals
   */
  public static void getVals() {
    if (GameUISettings.stVolume != null) {
      GameApp.usVolume = GameUISettings.stVolume.getValue() * 20;
    }
    if (GameUISettings.stVibrate != null) {
      if (GameUISettings.stVibrate.getSelectedIndex() == 0) {
        GameApp.usVibrate = true;
      }
      else {
        GameApp.usVibrate = false;
      }
    }
    if (GameUISettings.stBackLight != null) {
      if (GameUISettings.stBackLight.getSelectedIndex() == 0) {
        GameApp.usBackLight = true;
      }
      else {
        GameApp.usBackLight = false;
      }
    }
  }

}
