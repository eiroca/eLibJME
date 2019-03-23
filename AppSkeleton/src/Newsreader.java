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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * The Class Newsreader.
 */
public class Newsreader extends MIDlet implements CommandListener {

  /** The Constant URL. */
  static final String URL = "http://www.newsforge.com/newsforge.xml";

  /** The Constant TITLE. */
  static final String TITLE = "NewsForge";

  /** The descriptions. */
  Vector descriptions = new Vector();

  /** The news list. */
  List newsList = new List(Newsreader.TITLE, Choice.IMPLICIT);

  /** The text box. */
  TextBox textBox = new TextBox("", "", 256, TextField.ANY);

  /** The display. */
  Display display;

  /** The back cmd. */
  Command backCmd = new Command("Back", Command.BACK, 0);

  /**
   * The Class ReadThread.
   */
  class ReadThread extends Thread {

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {
      try {
        final HttpConnection httpConnection = (HttpConnection) Connector.open(Newsreader.URL);
        final KXmlParser parser = new KXmlParser();
        parser.setInput(new InputStreamReader(httpConnection.openInputStream()));
        // parser.relaxed = true;
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "backslash");
        while (parser.nextTag() != XmlPullParser.END_TAG) {
          readStory(parser);
        }
        parser.require(XmlPullParser.END_TAG, null, "backslash");
        parser.next();
        parser.require(XmlPullParser.END_DOCUMENT, null, null);
      }
      catch (final Exception e) {
        Debug.ignore(e);
        descriptions.addElement(e.toString());
        newsList.append("Error", null);
      }
    }

    /**
     * Read a story and append it to the list.
     * 
     * @param parser the parser
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws XmlPullParserException the xml pull parser exception
     */

    void readStory(final KXmlParser parser) throws IOException, XmlPullParserException {
      parser.require(XmlPullParser.START_TAG, null, "story");
      String title = null;
      String description = null;
      while (parser.nextTag() != XmlPullParser.END_TAG) {
        parser.require(XmlPullParser.START_TAG, null, null);
        final String name = parser.getName();
        final String text = parser.nextText();
        System.out.println("<" + name + ">" + text);
        if (name.equals("title")) {
          title = text;
        }
        else if (name.equals("description")) {
          description = text;
        }
        parser.require(XmlPullParser.END_TAG, null, name);
      }
      parser.require(XmlPullParser.END_TAG, null, "story");
      if (title != null) {
        descriptions.addElement("" + description);
        newsList.append(title, null);
      }
    }
  }

  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#startApp()
   */
  public void startApp() {
    display = Display.getDisplay(this);
    display.setCurrent(newsList);
    newsList.setCommandListener(this);
    textBox.setCommandListener(this);
    textBox.addCommand(backCmd);
    new ReadThread().start();
  }

  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#pauseApp()
   */
  public void pauseApp() {
    //
  }

  /* (non-Javadoc)
   * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
   */
  public void commandAction(final Command c, final Displayable d) {
    if (c == List.SELECT_COMMAND) {
      final String text = (String) descriptions.elementAt(newsList.getSelectedIndex());
      if (textBox.getMaxSize() < text.length()) {
        textBox.setMaxSize(text.length());
      }
      textBox.setString(text);
      display.setCurrent(textBox);
    }
    else if (c == backCmd) {
      display.setCurrent(newsList);
    }
  }

  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
   */
  public void destroyApp(final boolean really) {
    //
  }

}