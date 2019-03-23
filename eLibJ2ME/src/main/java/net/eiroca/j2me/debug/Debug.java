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
package net.eiroca.j2me.debug;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;

/**
 * The Class Debug.
 */
public class Debug {

  /** The Constant MAX_MESSAGES. */
  private final static int MAX_MESSAGES = 50;

  /** The Constant PRIORITY_TRACE. */
  public final static int PRIORITY_TRACE = 100;

  /** The Constant PRIORITY_MESSAGE. */
  public final static int PRIORITY_MESSAGE = 200;

  /** The Constant PRIORITY_EXCEPTION. */
  public final static int PRIORITY_EXCEPTION = 300;

  /** The Constant PRIORITY_CRTITICAL. */
  public final static int PRIORITY_CRTITICAL = 500;

  /** The messages. */
  private final static Vector messages = new Vector();

  /**
   * Show message screen.
   * 
   * @param app the app
   * @param back the back
   */
  public static void showMessageScreen(final BaseApp app, final Command back) {
    final Form f = new Form("Debug messages");
    DebugMessage msg;
    StringItem si;
    for (int i = Debug.messages.size() - 1; i >= 0; i--) {
      msg = (DebugMessage)Debug.messages.elementAt(i);
      si = new StringItem((msg.err == null) ? null : msg.err.toString(), msg.message);
      f.append(si);
    }
    f.addCommand(back);
    f.setCommandListener(app);
    Application.show(null, f, true);
  }

  /**
   * Adds low priority message.
   * 
   * @param message the message
   */
  public static void addTrace(final String message) {
    Debug.addMessage(new DebugMessage(Debug.PRIORITY_TRACE, message, null));
  }

  /**
   * Adds the message.
   * 
   * @param message the message
   */
  public static void addMessage(final String message) {
    Debug.addMessage(new DebugMessage(Debug.PRIORITY_MESSAGE, message, null));
  }

  /**
   * Adds the exception.
   * 
   * @param e the e
   */
  public static void addException(final Throwable e) {
    Debug.addMessage(new DebugMessage(Debug.PRIORITY_TRACE, null, e));
  }

  /**
   * Adds the exception.
   * 
   * @param message the message
   * @param e the e
   */
  public static void addCritical(final String message, final Throwable e) {
    Debug.addMessage(new DebugMessage(Debug.PRIORITY_CRTITICAL, message, e));
  }

  /**
   * Adds the message.
   * 
   * @param msg the msg
   */
  public static synchronized void addMessage(final DebugMessage msg) {
    Debug.checkSize(msg.priority);
    Debug.messages.addElement(msg);
  }

  /**
   * Check size.
   * 
   * @param priority the priority
   */
  public static void checkSize(final int priority) {
    if (Debug.messages.size() > Debug.MAX_MESSAGES) {
      DebugMessage msg;
      boolean spaceDone = false;
      for (int i = 0; i < Debug.messages.size(); i++) {
        msg = (DebugMessage)Debug.messages.elementAt(i);
        if (msg.priority < priority) {
          Debug.messages.removeElementAt(i);
          spaceDone = true;
          break;
        }
      }
      if (!spaceDone) {
        Debug.messages.removeElementAt(0);
      }
    }
  }

  /**
   * Ignore.
   * 
   * @param e the e
   */
  public static void ignore(final Throwable e) {
    ignore(e, false);
  }

  public static void propagate(final Throwable e) {
    ignore(e, true);
  }

  private static void ignore(final Throwable e, boolean printStack) {
    System.err.println("Ignored " + e.toString());
    if (printStack) e.printStackTrace();
  }

}
