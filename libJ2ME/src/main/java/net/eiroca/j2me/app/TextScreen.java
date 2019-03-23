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
package net.eiroca.j2me.app;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

/**
 * The Class TextScreen.
 */
public class TextScreen extends Form implements CommandListener {

  /** The next. */
  private final Displayable next;

  /**
   * Instantiates a new text screen.
   * 
   * @param title the title
   * @param next the next
   * @param label the label
   * @param msg the msg
   */
  public TextScreen(final String title, final Displayable next, final String label, final String[] msg) {
    super(title);
    Command backCommand;
    this.next = next;
    for (int i = 0; i < msg.length; i++) {
      append(msg[i]);
      append(BaseApp.sCR);
    }
    backCommand = new Command(label, Command.BACK, 1);
    addCommand(backCommand);
    setCommandListener(this);
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
   */
  public void commandAction(final Command c, final Displayable d) {
    BaseApp.setDisplay(next);
  }

}
