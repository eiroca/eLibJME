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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.debug.Debug;

/**
 * The Class ScoreManager.
 */
public class ScoreManager {

  /** The record name. */
  private final String recordName;

  /** The game name. */
  private final String gameName;

  /** The scores. */
  private final Vector[] scores;

  /** The list length. */
  private final int listLength;

  /**
   * Instantiates a new score manager.
   * 
   * @param recordName the record name
   * @param gameName the game name
   * @param difficulty the difficulty
   * @param listLength the list length
   * @param readIt the read it
   */
  public ScoreManager(final String recordName, final String gameName, final int difficulty, final int listLength, final boolean readIt) {
    this.recordName = recordName;
    this.listLength = listLength;
    this.gameName = gameName;
    scores = new Vector[difficulty];
    for (int i = 0; i < difficulty; i++) {
      scores[i] = new Vector(listLength);
    }
    if (readIt) {
      readHighScoreList();
    }
  }

  /**
   * Gets the high score.
   * 
   * @param difficulty the difficulty
   * @return the high score
   */
  public Score getHighScore(final int difficulty) {
    final int size = scores[difficulty].size();
    if (size == 0) { return null; }
    return (Score)scores[difficulty].elementAt(0);
  }

  /**
   * Checks for high score.
   * 
   * @param difficulty the difficulty
   * @param score the score
   * @return true, if successful
   */
  public boolean hasHighScore(final int difficulty, final Score score) {
    return scores[difficulty].size() > 0;
  }

  /**
   * Checks if is high score.
   * 
   * @param difficulty the difficulty
   * @param score the score
   * @return true, if is high score
   */
  public boolean isHighScore(final int difficulty, final Score score) {
    final int size = scores[difficulty].size();
    if (size < listLength) { return true; }
    final Score last = (Score)scores[difficulty].elementAt(size - 1);
    return (score.score > last.score);
  }

  /**
   * Gets the list.
   * 
   * @param difficulty the difficulty
   * @return the list
   */
  public Vector getList(final int difficulty) {
    return scores[difficulty];
  }

  /**
   * Sort.
   * 
   * @param difficulty the difficulty
   */
  private void sort(final int difficulty) {
    boolean flipped;
    Score a;
    Score b;
    for (int i = scores[difficulty].size(); --i >= 0;) {
      flipped = false;
      for (int j = 0; j < i; j++) {
        a = (Score)scores[difficulty].elementAt(j);
        b = (Score)scores[difficulty].elementAt(j + 1);
        if (a.score < b.score) {
          scores[difficulty].setElementAt(b, j);
          scores[difficulty].setElementAt(a, j + 1);
          flipped = true;
        }
      }
      if (!flipped) { return; }
    }
  }

  /**
   * Save score list.
   */
  public synchronized void saveScoreList() {
    final RecordStore rs = BaseApp.getRecordStore(recordName, true, false);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final DataOutputStream dos = new DataOutputStream(baos);
    try {
      dos.writeUTF(gameName);
      dos.writeInt(scores.length);
      for (int l = 0; l < scores.length; l++) {
        dos.writeInt(scores[l].size());
        for (int i = 0; i < scores[l].size(); i++) {
          final Score se = (Score)scores[l].elementAt(i);
          dos.writeUTF(se.name);
          dos.writeInt(se.level);
          dos.writeInt(se.score);
        }
      }
    }
    catch (final IOException e) {
      Debug.ignore(e);
    }
    BaseApp.writeData(rs, baos);
    BaseApp.close(rs, null, dos);
  }

  /**
   * Read high score list.
   */
  private void readHighScoreList() {
    final RecordStore rs = BaseApp.getRecordStore(recordName, false, false);
    final DataInputStream dis = BaseApp.readRecord(rs, 1);
    if (dis != null) {
      try {
        final String tmp = dis.readUTF();
        if (gameName.equals(tmp)) {
          final int t = dis.readInt();
          if (t == scores.length) {
            for (int l = 0; l < scores.length; l++) {
              final int n = dis.readInt();
              int ps = 0;
              for (int i = 0; i < n; i++) {
                final Score se = new Score(dis.readUTF(), dis.readInt(), dis.readInt());
                if (ps < listLength) {
                  scores[l].addElement(se);
                  ps++;
                }
              }
            }
          }
        }
      }
      catch (final IOException e) {
        //
      }
    }
    BaseApp.close(rs, dis, null);
  }

  /**
   * Adds the new score.
   * 
   * @param difficulty the difficulty
   * @param score the score
   */
  public void addNewScore(final int difficulty, final Score score) {
    if (score == null) { return; }
    final Score s = new Score(score.name, score.level, score.score);
    scores[difficulty].addElement(s);
    sort(difficulty);
    if (scores[difficulty].size() > listLength) {
      scores[difficulty].removeElementAt(scores[difficulty].size() - 1);
    }
    saveScoreList();
    return;
  }

}
