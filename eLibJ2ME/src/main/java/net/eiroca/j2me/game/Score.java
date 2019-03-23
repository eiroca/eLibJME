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

/**
 * The Class Score.
 */
public class Score {

  /** The name. */
  public String name;

  /** The score. */
  protected int score = 0;

  /** The level. */
  protected int level = 0;

  /** The lives. */
  private int lives = 0;

  /**
   * Instantiates a new score.
   */
  public Score() {
    name = "-";
  }

  /**
   * Instantiates a new score.
   * 
   * @param name the name
   * @param level the level
   * @param score the score
   */
  public Score(final String name, final int level, final int score) {
    this.name = name;
    this.level = level;
    this.score = score;
  }

  /**
   * Begin game.
   * 
   * @param aLives the a lives
   * @param aLevel the a level
   * @param aScore the a score
   */
  public void beginGame(final int aLives, final int aLevel, final int aScore) {
    score = aScore;
    lives = aLives;
    level = aLevel;
  }

  /**
   * End game.
   */
  public void endGame() {
    //
  }

  /**
   * Next level.
   */
  public void nextLevel() {
    level++;
  }

  /**
   * Next level.
   * 
   * @param step the step
   */
  public void nextLevel(final int step) {
    level = level + step;
  }

  /**
   * Adds the score.
   * 
   * @param val the val
   */
  public void addScore(final int val) {
    score += val;
  }

  /**
   * Gets the level.
   * 
   * @return the level
   */
  public int getLevel() {
    return level;
  }

  /**
   * Killed.
   * 
   * @return true, if successful
   */
  public boolean killed() {
    lives--;
    return (lives > 0);
  }

  /**
   * Gets the lives.
   * 
   * @return the lives
   */
  public int getLives() {
    return lives;
  }

  /**
   * Gets the score.
   * 
   * @return the score
   */
  public int getScore() {
    return Math.abs(score);
  }

}
