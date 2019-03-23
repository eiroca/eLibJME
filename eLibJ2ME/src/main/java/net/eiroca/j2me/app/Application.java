/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - GPL >= 3.0
 * 
 * Portion Copyright (C) 2002 Eugene Morozov (xonixboy@hotmail.com)
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

import java.util.Hashtable;
import java.util.Stack;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDletStateChangeException;
import net.eiroca.j2me.debug.Debug;
import net.eiroca.j2me.rms.Settings;

/**
 * The Class Application. Support an application. Have menus, actions, confirm.
 */
public abstract class Application extends BaseApp {

  /*
   * UI Manager
   */

  /** The Constant MD_MENUID. */
  public static final short MD_MENUID = 0;

  /** The Constant MD_MENUTX. */
  public static final short MD_MENUTX = 1;

  /** The Constant MD_MENUAC. */
  public static final short MD_MENUAC = 2;

  /** The Constant MD_MENUIC. */
  public static final short MD_MENUIC = 3;

  /** The c back. */
  public static Command cBACK;

  /** The c exit. */
  public static Command cEXIT;

  /** The c ok. */
  public static Command cOK;

  /** The background. */
  public static int background = 0x00000000;

  /** The foreground. */
  public static int foreground = 0x00FFFFFF;

  /** The menu. */
  public static short[][] menu;

  /** The messages. */
  public static String[] messages;

  /** The icons. */
  public static Image[] icons;

  /** The p special. */
  public static int pSpecial;

  /**
   * Go and back for all Displayables.
   */
  private static final Stack displayableStack = new Stack();

  /** The Constant commands. */
  private static final Hashtable commands = new Hashtable();

  /** The Constant listItems. */
  private static final Hashtable listItems = new Hashtable();

  /** The Constant AC_NONE. */
  public static final int AC_NONE = 0;

  /** The Constant AC_BACK. */
  public static final int AC_BACK = -101;

  /** The Constant AC_EXIT. */
  public static final int AC_EXIT = -100;

  /** The Constant EV_BEFORECHANGE. */
  public static final int EV_BEFORECHANGE = 1;

  /** The Constant EV_AFTERCHANGE. */
  public static final int EV_AFTERCHANGE = 2;

  /**
   * Go back to previous Displayable with one return code. Back to A Displayable from B Displayable,
   * so the Stack's size must be more or equals to 2.
   *
   * @param alert the alert
   * @return the displayable
   */
  public static Displayable back(final Alert alert) {
    if (Application.displayableStack.size() >= 2) {
      final Displayable previous = (Displayable)Application.displayableStack.pop();
      // get the instance of the previous one but remain it in the stack.
      final Displayable next = (Displayable)Application.displayableStack.peek();
      BaseApp.midlet.changed(Application.EV_BEFORECHANGE, previous, next);
      if (alert == null) {
        BaseApp.setDisplay(next);
      }
      else {
        BaseApp.setDisplay(alert, next);
      }
      BaseApp.midlet.changed(Application.EV_AFTERCHANGE, previous, next);
      return next;
    }
    return null;
  }

  /**
   * Go back to specify Displayable. It's same like calling go() to add one new Displayable, if the
   * next is not existing in the stack.
   *
   * @param alert the alert
   * @param next the next
   * @param keepPrevious the keep previous
   */
  public static void back(final Alert alert, final Displayable next, final boolean keepPrevious) {
    if (!Application.displayableStack.empty()) {
      final int index = Application.displayableStack.search(next);
      final Displayable previous = (Displayable)Application.displayableStack.pop();
      for (int i = index - 1; i >= 1; i--) {
        Application.displayableStack.pop();
      }
      if (keepPrevious) {
        Application.displayableStack.push(previous);
      }
      Application.show(alert, next, true);
    }
  }

  /**
   * Add one new Displayable. save is used to set the Displayable to the stack or not.
   *
   * @param alert the alert
   * @param next the next
   * @param save the save
   */
  public static void show(final Alert alert, Displayable next, final boolean save) {
    Displayable previous = null;
    if (!Application.displayableStack.empty()) {
      previous = (Displayable)Application.displayableStack.peek();
    }
    if (next == null) {
      next = BaseApp.currentDisplay();
    }
    else {
      final boolean isNew = (previous == null) || (previous != next);
      final boolean isAlert = (next instanceof Alert) && (((Alert)next).getTimeout() != Alert.FOREVER);
      if (save && isNew && !isAlert) {
        Application.displayableStack.push(next);
      }
    }
    BaseApp.midlet.changed(Application.EV_BEFORECHANGE, previous, next);
    if (alert == null) {
      BaseApp.setDisplay(next);
    }
    else {
      BaseApp.setDisplay(alert, next);
    }
    BaseApp.midlet.changed(Application.EV_AFTERCHANGE, previous, next);
  }

  /**
   * New command.
   *
   * @param label the label
   * @param commandType the command type
   * @param priority the priority
   * @param action the action
   * @return the command
   */
  public static Command newCommand(final int label, final int commandType, final int priority, final int action) {
    final Command cmd = new Command(Application.messages[label], commandType, priority);
    Application.commands.put(cmd, new Integer(action));
    return cmd;
  }

  /**
   * Register command.
   *
   * @param cmd the cmd
   * @param action the action
   */
  public static void registerCommand(final Command cmd, final int action) {
    Application.commands.put(cmd, new Integer(action));
  }

  /**
   * Register list.
   *
   * @param list the list
   * @param action the action
   */
  public static void registerList(final List list, final int action) {
    Application.listItems.put(list, new Integer(action));
  }

  /**
   * Register list item.
   *
   * @param list the list
   * @param index the index
   * @param action the action
   */
  public static void registerListItem(final List list, final int index, final int action) {
    Application.listItems.put(list + "#" + index, new Integer(action));
  }

  /**
   * Handle action.
   *
   * @param action the action
   * @param d the d
   * @param cmd the cmd
   * @return true, if successful
   */
  abstract public boolean handleAction(int action, Displayable d, Command cmd);

  /* (non-Javadoc)
   * @see net.eiroca.j2me.app.BaseApp#process(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable, javax.microedition.lcdui.Item)
   */
  @Override
  public void process(final Command cmd, final Displayable d, final Item i) {
    // if cmd is list selection, we change cmd to actual command
    Object at = null;
    if (cmd == List.SELECT_COMMAND) {
      if ((d != null) && (d instanceof List)) {
        final List list = (List)d;
        final int index = list.getSelectedIndex();
        at = Application.listItems.get(list + "#" + index);
        if (at == null) {
          at = Application.listItems.get(list);
        }
      }
    }
    if ((at == null) && (cmd != null)) {
      at = Application.commands.get(cmd);
    }
    if (at != null) {
      final int action = ((Integer)at).intValue();
      boolean processed = handleAction(action, d, cmd);
      if (!processed) {
        switch (action) {
          case AC_BACK: {
            doBack();
            processed = true;
            break;
          }
          case AC_EXIT: {
            doExit();
            processed = true;
            break;
          }
        }
      }
    }
  }

  /**
   * Do exit.
   */
  public void doExit() {
    try {
      BaseApp.midlet.destroyApp(true);
    }
    catch (final MIDletStateChangeException e) {
      Debug.ignore(e);
    }
    BaseApp.midlet.notifyDestroyed();
  }

  /**
   * Do back.
   */
  public void doBack() {
    Application.back(null);
  }

  /**
   * Setup.
   *
   * @param d the d
   * @param c1 the c1
   * @param c2 the c2
   */
  public static void setup(final Displayable d, final Command c1, final Command c2) {
    d.setCommandListener(BaseApp.midlet);
    if (c1 != null) {
      d.addCommand(c1);
    }
    if (c2 != null) {
      d.addCommand(c2);
    }
  }

  /**
   * Insert menu item.
   *
   * @param list the list
   * @param ps the ps
   * @param def the def
   */
  public static void insertMenuItem(final List list, final int ps, final short[] def) {
    Image icon = null;
    if (def[Application.MD_MENUIC] >= 0) {
      icon = Application.icons[def[Application.MD_MENUIC]];
    }
    list.insert(ps, Application.messages[def[Application.MD_MENUTX]], icon);
  }

  /**
   * Gets the action.
   *
   * @param id the id
   * @param list the list
   * @return the action
   */
  public static short getAction(final int id, final List list) {
    final int idx = list.getSelectedIndex();
    short[] def;
    int ps = 0;
    for (final short[] element : Application.menu) {
      def = element;
      if (def[Application.MD_MENUID] == id) {
        if (ps == idx) { return def[Application.MD_MENUAC]; }
        ps++;
      }
    }
    return -1;
  }

  /**
   * Gets the menu.
   *
   * @param title the title
   * @param menuID the menu id
   * @param special the special
   * @param cmd the cmd
   * @return the menu
   */
  public static List getMenu(final String title, final int menuID, final int special, final Command cmd) {
    final List list = new List(title, Choice.IMPLICIT);
    short[] def;
    int ps = 0;
    for (int i = 0; i < Application.menu.length; i++) {
      def = Application.menu[i];
      final int action = def[Application.MD_MENUAC];
      if (def[Application.MD_MENUID] == menuID) {
        if (action == special) {
          Application.pSpecial = i;
        }
        else {
          Application.insertMenuItem(list, ps, def);
          if (action != Application.AC_NONE) {
            Application.registerListItem(list, ps, action);
          }
          ps++;
        }
      }
    }
    Application.setup(list, cmd, null);
    return list;
  }

  /**
   * Gets the text form.
   *
   * @param title the title
   * @param textRes the text res
   * @return the text form
   */
  public static Displayable getTextForm(final int title, final String textRes) {
    final Form form = new Form(Application.messages[title]);
    final String msg = BaseApp.readString(textRes);
    if (msg != null) {
      form.append(msg);
    }
    Application.setup(form, Application.cBACK, null);
    return form;
  }

  /**
   * Gets the text form.
   *
   * @param title the title
   * @param textRes the text res
   * @param o the o
   * @return the text form
   */
  public static Displayable getTextForm(final int title, final String textRes, final Object[] o) {
    final Form form = new Form(Application.messages[title]);
    final String msg = BaseApp.readString(textRes);
    if (msg != null) {
      form.append(BaseApp.format(msg, o));
    }
    Application.setup(form, Application.cBACK, null);
    return form;
  }

  /**
   * Displays the alert.
   *
   * @param alertTitle the alert title
   * @param alertMessage the alert message
   * @param alertImage the alert image
   * @param alertType the alert type
   * @param alertNext the alert next
   * @param timeOut the time out
   */
  public static void showAlert(final int alertTitle, final int alertMessage, final Image alertImage, final AlertType alertType, final Displayable alertNext, final int timeOut) {
    final Alert alert = new Alert(Application.messages[alertTitle], Application.messages[alertMessage], alertImage, alertType);
    alert.setTimeout(timeOut);
    Application.back(alert, alertNext, true);
  }

  /**
   * Confirm.
   *
   * @param title the title
   * @param question the question
   * @param yes the yes
   * @param no the no
   */
  public void confirm(final int title, final int question, final Command yes, final Command no) {
    final Form qform = new Form(Application.messages[title]);
    qform.append(Application.messages[question]);
    qform.addCommand(yes);
    qform.addCommand(no);
    qform.setCommandListener(this);
    Application.show(null, qform, true);
  }

  /**
   * Format.
   *
   * @param msg the msg
   * @param o the o
   * @return the string
   */
  public static String format(final int msg, final Object[] o) {
    return BaseApp.format(Application.messages[msg], o);
  }

  /**
   * Application destroy.
   */
  @Override
  protected void done() {
    Application.displayableStack.removeAllElements();
    super.done();
  }

  //--
  /**
   * If the new settings value (newValue) != the previous value (prevValue) update the value in
   * settings. This allows valuesChanged to be set in settings, and to store only if changed.
   *
   * @param newValue
   * @param settingsKey
   * @param prevValue
   * @return int
   * @author Irv Bunton
   */
  public static int settingsUpd(final int newValue, final String settingsKey, final int prevValue) {
    try {
      if (newValue != prevValue) {
        Settings.putInt(settingsKey, newValue);
      }
      return newValue;
    }
    catch (final Throwable e) {
      Debug.ignore(e);
      return prevValue;
    }
  }

  public static ChoiceGroup createChoiceGroup(final int msgNbr, final int choiceType, final int[] choiceMsgNbrs) {
    final ChoiceGroup choiceGrp = new ChoiceGroup(Application.messages[msgNbr], choiceType);
    for (final int choiceMsgNbr : choiceMsgNbrs) {
      choiceGrp.append(Application.messages[choiceMsgNbr], null);
    }
    return choiceGrp;
  }

  public static ChoiceGroup createNumRange(final int msgNbr, final int start, final int end, final int incr) {
    final ChoiceGroup numRange = new ChoiceGroup(Application.messages[msgNbr], Choice.POPUP);
    final int aend = Math.abs(end);
    for (int i = start; i <= aend;) {
      numRange.append(Integer.toString(i), null);
      i += incr;
    }
    return numRange;
  }

}
