/** GPL >= 3.0
 * Copyright (C) 2006-2010 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 * Copyright (C) 2004 Ang Kok Chai
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
import java.util.Date;
import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import net.eiroca.j2me.app.Application;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.rms.RMSTable;
import net.eiroca.j2me.rms.Settings;
import net.eiroca.log4j2me.Configurator;
import net.eiroca.log4j2me.FormAppender;
import net.eiroca.log4j2me.Properties;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

public final class AppDemo extends Application implements RecordListener {

  /** The log. */
  static Category log = Category.getInstance("ME");

  /** The Constant COUNT. */
  static final int COUNT = 15;

  /** The value a. */
  private final TextBox valueA = new TextBox("ValueA", null, 100, TextField.ANY);

  /** The value b. */
  private final TextBox valueB = new TextBox("ValueB", null, 100, TextField.EMAILADDR);

  /** The value c. */
  private final TextBox valueC = new TextBox("ValueC", null, 100, Item.HYPERLINK);

  /** The value d. */
  private final TextBox valueD = new TextBox("ValueD", null, 100, TextField.DECIMAL);

  /** The value e. */
  private final TextBox valueE = new TextBox("ValueE", null, 100, TextField.INITIAL_CAPS_SENTENCE);

  /** The value f. */
  private final TextBox valueF = new TextBox("ValueF", null, 100, TextField.PHONENUMBER);

  /** The CSAVE. */
  private final Command CSAVE = new Command("Save", Command.OK, 0);

  /** The CEXIT. */
  private final Command CEXIT = new Command("Exit", Command.CANCEL, 0);

  /** The CBACK. */
  private final Command CBACK = new Command("Back", Command.BACK, 0);

  /** The CCLEAR. */
  private final Command CCLEAR = new Command("Clear", Command.SCREEN, 0);

  /** The menu. */
  private List menu;

  /** The list00. */
  private List list00;

  /** The list10. */
  private List list10;

  /** The list11. */
  private List list11;

  /** The list12. */
  private List list12;

  /** The list20. */
  private List list20;

  /** The list21. */
  private List list21;

  /** The log form. */
  private Form logForm;

  /** The Constant AC_SHOWPROPA. */
  private final static int AC_SHOWPROPA = 1;

  /** The Constant AC_SHOWPROPB. */
  private final static int AC_SHOWPROPB = 2;

  /** The Constant AC_SHOWPROPC. */
  private final static int AC_SHOWPROPC = 3;

  /** The Constant AC_SHOWPROPD. */
  private final static int AC_SHOWPROPD = 4;

  /** The Constant AC_SHOWPROPE. */
  private final static int AC_SHOWPROPE = 5;

  /** The Constant AC_SHOWPROPF. */
  private final static int AC_SHOWPROPF = 6;

  /** The Constant AC_SHOWRMS. */
  private final static int AC_SHOWRMS = 7;

  /** The Constant AC_LIST00. */
  private final static int AC_LIST00 = 8;

  /** The Constant AC_LIST10. */
  private final static int AC_LIST10 = 9;

  /** The Constant AC_LIST20. */
  private final static int AC_LIST20 = 10;

  /** The Constant AC_LIST11. */
  private final static int AC_LIST11 = 11;

  /** The Constant AC_LIST12. */
  private final static int AC_LIST12 = 12;

  /** The Constant AC_LIST21. */
  private final static int AC_LIST21 = 13;

  /** The Constant AC_FORM01. */
  private final static int AC_FORM01 = 14;

  /** The Constant AC_FORM02. */
  private final static int AC_FORM02 = 15;

  /** The Constant AC_LOGFORM. */
  private final static int AC_LOGFORM = 16;

  /** The Constant AC_SAVE. */
  private final static int AC_SAVE = 17;

  /** The Constant AC_CLEAR. */
  private final static int AC_CLEAR = 18;

  /**
   * Instantiates a new app demo.
   */
  public AppDemo() {
    super();
    final Properties props = new Properties();
    System.out.println("MHello start to config log4j2me.");
    Configurator.load(props, "logJ2ME.conf");
    Configurator.load(props, this);
    final Vector vFormAppenders = PropertyConfigurator.configure(props);
    System.out.println("MHello finished configuration of log4j2me.");
    for (int i = 0; vFormAppenders.size() > i; i++) {
      final String title = ((FormAppender) vFormAppenders.elementAt(i)).getTitle();
      if ((null != title) && (title.equals("mylog"))) {
        logForm = ((FormAppender) vFormAppenders.elementAt(i)).getForm();
      }
    }
    Application.registerCommand(CBACK, Application.AC_BACK);
    Application.registerCommand(CEXIT, Application.AC_EXIT);
    BaseApp.setRecordListener(this);
    AppDemo.log.debug("init()");
    init();
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.app.Application#done()
   */
  public void done() {
    BaseApp.closeRecordStores();
    super.done();
  }

  /**
   * Record added.
   * 
   * @param recordStore the record store
   * @param recordId the record id
   */
  public void recordAdded(final RecordStore recordStore, final int recordId) {
    AppDemo.log.warn("User has added properties");
  }

  /**
   * Record changed.
   * 
   * @param recordStore the record store
   * @param recordId the record id
   */
  public void recordChanged(final RecordStore recordStore, final int recordId) {
    AppDemo.log.warn("User has saved properties");
  }

  /**
   * Record deleted.
   * 
   * @param recordStore the record store
   * @param recordId the record id
   */
  public void recordDeleted(final RecordStore recordStore, final int recordId) {
    AppDemo.log.warn("Record deleted");
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.app.BaseApp#changed(int, javax.microedition.lcdui.Displayable, javax.microedition.lcdui.Displayable)
   */
  public void changed(final int event, final Displayable previous, final Displayable next) {
    if (event == Application.EV_AFTERCHANGE) {
      if ((previous != null) && (next != null)) {
        AppDemo.log.warn("From: " + previous.getTitle() + " To: " + next.getTitle());
      }
    }
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.app.Application#handleAction(int, javax.microedition.lcdui.Displayable, javax.microedition.lcdui.Command)
   */
  public boolean handleAction(final int action, final Displayable d, final Command cmd) {
    boolean processed = true;
    switch (action) {
      case AC_SHOWPROPA:
        valueA.setString(Settings.get("ValueA"));
        Application.show(null, valueA, true);
        break;
      case AC_SHOWPROPB:
        valueB.setString(Settings.get("ValueB"));
        Application.show(null, valueB, true);
        break;
      case AC_SHOWPROPC:
        valueC.setString(Settings.get("ValueC"));
        Application.show(null, valueC, true);
        break;
      case AC_SHOWPROPD:
        valueD.setString(Settings.get("ValueD"));
        Application.show(null, valueD, true);
        break;
      case AC_SHOWPROPE:
        valueE.setString(Settings.get("ValueE"));
        Application.show(null, valueE, true);
        break;
      case AC_SHOWPROPF:
        valueF.setString(Settings.get("ValueF"));
        Application.show(null, valueF, true);
        break;
      case AC_SHOWRMS:
        Application.show(null, initForm03(), true);
        break;
      case AC_LIST00:
        Application.show(null, list00, true);
        break;
      case AC_LIST10:
        Application.show(null, list10, true);
        break;
      case AC_LIST20:
        Application.show(null, list20, true);
        break;
      case AC_LIST11:
        Application.show(null, list11, true);
        break;
      case AC_LIST12:
        Application.show(null, list12, true);
        break;
      case AC_LIST21:
        Application.show(null, list21, true);
        break;
      case AC_FORM01:
        Application.show(null, initForm01(), true);
        break;
      case AC_FORM02:
        Application.show(null, initForm02(), true);
        break;
      case AC_LOGFORM:
        Application.show(null, logForm, true);
        break;
      case AC_SAVE:
        if (d == valueA) {
          Settings.put("ValueA", valueA.getString());
        }
        else if (d == valueB) {
          Settings.put("ValueB", valueB.getString());
        }
        else if (d == valueC) {
          Settings.put("ValueC", valueC.getString());
        }
        else if (d == valueD) {
          Settings.put("ValueD", valueD.getString());
        }
        else if (d == valueE) {
          Settings.put("ValueE", valueE.getString());
        }
        else if (d == valueF) {
          Settings.put("ValueF", valueF.getString());
        }
        Settings.save();
        Application.back(null);
        break;
      case AC_CLEAR:
        logForm.deleteAll();
        break;
      default:
        processed = false;
        break;
    }
    return processed;
  }

  /**
   * init.
   */
  protected void init() {
    super.init();
    Application.setup(logForm, CBACK, CCLEAR);
    final Image[] icons = BaseApp.splitImages("/icons.png", 5, 16, 16);
    menu = new List("Main Menu", Choice.IMPLICIT, new String[] {
        "Settings", "Scale Demo", "IsoDate test", "RMS table (can be slow)", "show Log"
    }, icons);
    list00 = new List("Props 0.0", Choice.IMPLICIT, new String[] {
        "1.0", "2.0"
    }, null);
    list10 = new List("Props 1.0", Choice.IMPLICIT, new String[] {
        "1.1", "1.2"
    }, null);
    list11 = new List("Props 1.1", Choice.IMPLICIT, new String[] {
        "ValueA", "ValueB", "ValueC"
    }, null);
    list12 = new List("Props 1.2", Choice.IMPLICIT, new String[] {
        "ValueD", "ValueE"
    }, null);
    list20 = new List("Props 2.0", Choice.IMPLICIT, new String[] {
      "2.1"
    }, null);
    list21 = new List("Props 2.1", Choice.IMPLICIT, new String[] {
      "ValueF"
    }, null);
    Application.registerCommand(CSAVE, AppDemo.AC_SAVE);
    Application.registerCommand(CCLEAR, AppDemo.AC_CLEAR);
    Application.setup(menu, CEXIT, null);
    Application.setup(list00, CBACK, null);
    Application.setup(list10, CBACK, null);
    Application.setup(list11, CBACK, null);
    Application.setup(list12, CBACK, null);
    Application.setup(list20, CBACK, null);
    Application.setup(list21, CBACK, null);
    Application.setup(valueA, CSAVE, CBACK);
    Application.setup(valueB, CSAVE, CBACK);
    Application.setup(valueC, CSAVE, CBACK);
    Application.setup(valueD, CSAVE, CBACK);
    Application.setup(valueE, CSAVE, CBACK);
    Application.setup(valueF, CSAVE, CBACK);
    Application.registerListItem(menu, 0, AppDemo.AC_LIST00);
    Application.registerListItem(menu, 1, AppDemo.AC_FORM01);
    Application.registerListItem(menu, 2, AppDemo.AC_FORM02);
    Application.registerListItem(menu, 3, AppDemo.AC_SHOWRMS);
    Application.registerListItem(menu, 4, AppDemo.AC_LOGFORM);
    Application.registerListItem(list00, 0, AppDemo.AC_LIST10);
    Application.registerListItem(list00, 1, AppDemo.AC_LIST20);
    Application.registerListItem(list10, 0, AppDemo.AC_LIST11);
    Application.registerListItem(list10, 1, AppDemo.AC_LIST12);
    Application.registerListItem(list20, 0, AppDemo.AC_LIST21);
    Application.registerListItem(list11, 0, AppDemo.AC_SHOWPROPA);
    Application.registerListItem(list11, 1, AppDemo.AC_SHOWPROPB);
    Application.registerListItem(list11, 2, AppDemo.AC_SHOWPROPC);
    Application.registerListItem(list12, 0, AppDemo.AC_SHOWPROPD);
    Application.registerListItem(list12, 1, AppDemo.AC_SHOWPROPE);
    Application.registerListItem(list21, 0, AppDemo.AC_SHOWPROPF);
    if (Settings.size() == 0) {
      Settings.put("ValueA", "");
      Settings.put("ValueB", "");
      Settings.put("ValueC", "");
      Settings.put("ValueD", "");
      Settings.put("ValueE", "");
      Settings.put("ValueF", "");
      Settings.save();
    }
    AppDemo.log.debug("startApp()");
    Settings.load();
    Application.show(null, menu, true);
  }

  /**
   * Read.
   * 
   * @param form the form
   * @param i the i
   * @param ri the ri
   * @throws RecordStoreException the record store exception
   */
  protected void read(final Form form, int i, final RMSTable ri) throws RecordStoreException {
    i = i - 1;
    if (i >= AppDemo.COUNT) {
      i = AppDemo.COUNT - 1;
    }
    final String sKey = "Key " + i;
    final String data = ri.get(sKey);
    form.append(data + "=" + i + BaseApp.CR);
  }

  /** The form01. */
  private Form form01;

  /**
   * Inits the form01.
   * 
   * @return the form
   */
  private Form initForm01() {
    if (form01 == null) {
      form01 = new Form("ScaleDemo");
      final Image img = BaseApp.createImage("/icon.png");
      form01.append(Image.createImage(img)); // immutable vers.
      final Image big = BaseApp.scaleImage(img, 30, 30);
      form01.append(Image.createImage(big)); // immutable vers.
      final Image small = BaseApp.scaleImage(img, 5, 5);
      form01.append(Image.createImage(small)); // immutable vers.
      Application.setup(form01, CBACK, null);
    }
    return form01;
  }

  /** The form02. */
  private Form form02;

  /**
   * Inits the form02.
   * 
   * @return the form
   */
  private Form initForm02() {
    if (form02 == null) {
      form02 = new Form("Iso Date");
      final Date date = new Date();
      final String id = BaseApp.dateToString(date, BaseApp.DATE_TIME);
      form02.append("ISO Date Now:    " + id + BaseApp.CR);
      final Date date2 = BaseApp.stringToDate(id, BaseApp.DATE_TIME);
      final String id2 = BaseApp.dateToString(date2, BaseApp.DATE_TIME);
      form02.append("After Roundtrip: " + id2 + BaseApp.CR);
      form02.append("Equals:          " + date.equals(date2) + BaseApp.CR);
      Application.setup(form02, CBACK, null);
    }
    return form02;
  }

  /** The form03. */
  private Form form03;

  /**
   * Inits the form03.
   * 
   * @return the form
   */
  private Form initForm03() {
    if (form03 == null) {
      form03 = new Form("RMS Index");
      RMSTable ri = null;
      try {
        ri = new RMSTable("idxtest_9");
        form03.append("Sequential write" + BaseApp.CR);
        for (int element = 0; element < AppDemo.COUNT; element++) {
          final String sKey = "Key " + element;
          final String data = "Element " + element;
          ri.put(sKey, data);
        }
        form03.append("Random read" + BaseApp.CR);
        read(form03, 1, ri);
        read(form03, AppDemo.COUNT / 3, ri);
        read(form03, AppDemo.COUNT / 2, ri);
        read(form03, AppDemo.COUNT - 4, ri);
        read(form03, AppDemo.COUNT - 3, ri);
        read(form03, AppDemo.COUNT - 2, ri);
        read(form03, AppDemo.COUNT, ri);
        ri.close();
      }
      catch (final RecordStoreException e) {
        Debug.ignore(e);
      }
      Application.setup(form03, CBACK, null);
    }
    return form03;
  }

}