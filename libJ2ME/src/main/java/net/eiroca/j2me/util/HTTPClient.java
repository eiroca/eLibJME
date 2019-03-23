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
package net.eiroca.j2me.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.app.Pair;
import net.eiroca.j2me.observable.Observable;
import net.eiroca.j2me.observable.Observer;
import net.eiroca.j2me.observable.ObserverManager;

/**
 * The Class HTTPClient.
 */
public class HTTPClient implements Observable, Runnable {

  /** The Constant BOUNDARY. */
  private static final String BOUNDARY = "eiroca123XYZ123";

  /** The Constant BOUNDARY_PRE. */
  private static final String BOUNDARY_PRE = "--";

  /** The Constant CONTENT_TYPE. */
  private static final String CONTENT_TYPE = "Content-type";

  /** The Constant SEP. */
  private static final String SEP = "" + BaseApp.NL;

  /** The Constant MODE_GET. */
  public static final int MODE_GET = 0;

  /** The Constant MODE_POST. */
  public static final int MODE_POST = 1;

  /** The Constant MODE_MULTIPART. */
  public static final int MODE_MULTIPART = 2;

  /** The user agent. */
  public String userAgent = "eIrOcAMIDlet";

  /** The accept language. */
  public String acceptLanguage = null;

  /** The use keep alive. */
  public boolean useKeepAlive = false;

  /** The mode. */
  public int mode = HTTPClient.MODE_GET;

  /** The result. */
  public String result = null;

  /** The status. */
  public int status = -1;

  /** The url. */
  private String url;

  /** The host. */
  private String host;

  /** The params. */
  private final Vector params = new Vector();

  /** The attach. */
  private final Vector attach = new Vector();

  /** The manager. */
  private final ObserverManager manager = new ObserverManager();

  /**
   * Instantiates a new hTTP client.
   */
  public HTTPClient() {
  }

  /**
   * Clear.
   */
  public void clear() {
    params.removeAllElements();
    attach.removeAllElements();
  }

  /**
   * Adds the parameter.
   * 
   * @param parameter the parameter
   * @param value the value
   */
  public void addParameter(final String parameter, final String value) {
    final Pair p = new Pair();
    p.name = parameter;
    p.value = BaseApp.URLEncode(value);
    params.addElement(p);
  }

  /**
   * Adds the attach.
   * 
   * @param data the data
   */
  public void addAttach(final HTTPAttach data) {
    attach.addElement(data);
  }

  /**
   * Gets the post data.
   * 
   * @return the post data
   */
  public String getPostData() {
    final StringBuffer postData = new StringBuffer(100);
    if (params.size() > 0) {
      boolean first = true;
      for (int i = 0; i < params.size(); i++) {
        final Pair p = (Pair)params.elementAt(i);
        if (!first) {
          postData.append('&');
        }
        postData.append(BaseApp.URLEncode(String.valueOf(p.name)));
        if (p.value != null) {
          postData.append('=').append(p.value);
        }
        first = false;
      }
    }
    return postData.toString();
  }

  /**
   * Gets the connection.
   * 
   * @return the connection
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private HttpConnection getConnection() throws IOException {
    HttpConnection connection = null;
    String uri;
    switch (mode) {
      case MODE_GET:
        final String postData = getPostData();
        if (url.indexOf('?') > 0) {
          uri = url + '&' + postData;
        }
        else {
          uri = url + '?' + postData;
        }
        break;
      default:
        uri = url;
        break;
    }
    connection = (HttpConnection)Connector.open(uri, Connector.READ_WRITE);
    switch (mode) {
      case MODE_GET:
        connection.setRequestMethod(HttpConnection.GET);
        break;
      default:
        connection.setRequestMethod(HttpConnection.POST);
        break;
    }
    connection.setRequestProperty("User-Agent", userAgent);
    if (acceptLanguage != null) {
      connection.setRequestProperty("Accept-Language", acceptLanguage);
    }
    if (host != null) {
      connection.setRequestProperty("Host", host);
    }
    if (useKeepAlive) {
      connection.setRequestProperty("Connection", "keep-alive");
      connection.setRequestProperty("Keep-Alive", "300");
    }
    switch (mode) {
      case MODE_MULTIPART:
        connection.setRequestProperty(HTTPClient.CONTENT_TYPE, "multipart/form-data; boundary=" + HTTPClient.BOUNDARY);
        break;
      case MODE_POST:
        connection.setRequestProperty(HTTPClient.CONTENT_TYPE, "application/x-www-form-urlencoded");
        break;
      case MODE_GET:
        break;
    }
    return connection;
  }

  /**
   * Send post.
   * 
   * @param connection the connection
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void sendPost(final HttpConnection connection) throws IOException {
    final String postData = getPostData();
    final OutputStream dos = connection.openOutputStream();
    dos.write(postData.getBytes());
    dos.close();
  }

  /**
   * Send multipart.
   * 
   * @param connection the connection
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void sendMultipart(final HttpConnection connection) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    StringBuffer buf;
    if (params.size() > 0) {
      for (int i = 0; i < params.size(); i++) {
        final Pair p = (Pair)params.elementAt(i);
        buf = new StringBuffer(200);
        buf.append(HTTPClient.BOUNDARY_PRE).append(HTTPClient.BOUNDARY).append(HTTPClient.SEP);
        buf.append("Content-Disposition: form-data; name=").append('"').append(p.name).append('"').append(HTTPClient.SEP);
        buf.append(HTTPClient.SEP).append((p.value != null ? p.value : "")).append(HTTPClient.SEP);
        out.write(buf.toString().getBytes());
      }
    }
    for (int i = 0; i < attach.size(); i++) {
      final HTTPAttach sendable = (HTTPAttach)attach.elementAt(i);
      final String mimeType = sendable.getMimeType();
      final byte[] data = sendable.getData();
      final String name = "file_" + Integer.toString(i);
      buf = new StringBuffer(200);
      buf.append(HTTPClient.BOUNDARY_PRE).append(HTTPClient.BOUNDARY).append(HTTPClient.SEP);
      buf.append("Content-Disposition: form-data;");
      buf.append(" name=").append('"').append(name).append('"');
      buf.append("; filename=").append('"').append(name).append('"');
      buf.append(HTTPClient.SEP);
      buf.append(HTTPClient.CONTENT_TYPE + ": ").append(mimeType).append(HTTPClient.SEP);
      buf.append(HTTPClient.SEP);
      out.write(buf.toString().getBytes());
      out.write(data);
      out.write(HTTPClient.SEP.getBytes());
    }
    buf = new StringBuffer(200);
    buf.append(HTTPClient.BOUNDARY_PRE).append(HTTPClient.BOUNDARY).append(HTTPClient.BOUNDARY_PRE).append(HTTPClient.SEP);
    out.write(buf.toString().getBytes());
    final OutputStream dos = connection.openOutputStream();
    final byte[] b = out.toByteArray();
    dos.write(b);
    dos.close();
  }

  /**
   * Send get.
   * 
   * @param connection the connection
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void sendGet(final HttpConnection connection) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (int i = 0; i < attach.size(); i++) {
      final HTTPAttach sendable = (HTTPAttach)attach.elementAt(i);
      final String mimeType = sendable.getMimeType();
      final byte[] data = sendable.getData();
      if (i == 0) {
        connection.setRequestProperty(HTTPClient.CONTENT_TYPE, mimeType);
      }
      out.write(data);
    }
    final OutputStream dos = connection.openOutputStream();
    final byte[] b = out.toByteArray();
    System.out.println(new String(b));
    dos.write(b);
    dos.close();
  }

  /**
   * Read result.
   * 
   * @param connection the connection
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private String readResult(final HttpConnection connection) throws IOException {
    InputStream dis;
    final StringBuffer buf = new StringBuffer(1024);
    dis = connection.openDataInputStream();
    int chr;
    while ((chr = dis.read()) != -1) {
      buf.append((char)chr);
    }
    dis.close();
    return buf.toString();
  }

  /**
   * Execute.
   */
  public void execute() {
    result = null;
    setStatus(0);
    try {
      final HttpConnection httpConn = getConnection();
      switch (mode) {
        case MODE_MULTIPART:
          sendMultipart(httpConn);
          break;
        case MODE_POST:
          sendPost(httpConn);
          break;
        case MODE_GET:
          sendGet(httpConn);
          break;
      }
      final int responseCode = httpConn.getResponseCode();
      result = readResult(httpConn);
      setStatus(responseCode);
      httpConn.close();
    }
    catch (final IOException e) {
      result = e.getMessage();
      setStatus(999);
    }
  }

  /**
   * Submit.
   * 
   * @param url the url
   * @param addHost the add host
   * @param async the async
   */
  public void submit(final String url, final boolean addHost, final boolean async) {
    this.url = url;
    if (addHost) {
      final int first = url.indexOf('/');
      host = url.substring(first + 2, url.indexOf('/', first + 2));
    }
    if (async) {
      new Thread(this).start();
    }
    else {
      execute();
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run() {
    execute();
  }

  /**
   * Gets the status.
   * 
   * @return the status
   */
  public int getStatus() {
    return status;
  }

  /**
   * Sets the status.
   * 
   * @param status the new status
   */
  public void setStatus(final int status) {
    this.status = status;
    manager.notifyObservers(this);
  }

  /* (non-Javadoc)
   * @see net.eiroca.j2me.observable.Observable#getObserverManager()
   */
  public ObserverManager getObserverManager() {
    return manager;
  }

  /**
   * Adds the observer.
   * 
   * @param observer the observer
   */
  public void addObserver(final Observer observer) {
    manager.addObserver(observer);
  }

  /**
   * Removes the observer.
   * 
   * @param observer the observer
   */
  public void removeObserver(final Observer observer) {
    manager.removeObserver(observer);
  }

  /**
   * Gets the result.
   * 
   * @return the result
   */
  public String getResult() {
    return result;
  }

}
