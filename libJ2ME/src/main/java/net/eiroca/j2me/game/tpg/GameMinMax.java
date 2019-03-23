/**
 * Copyright (C) 2006-2019 eIrOcA (eNrIcO Croce & sImOnA Burzio) - LGPL >= 3.0
 * 
 * Portion Copyright (C) 2002-2004 Salamon Andras
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/
 */
package net.eiroca.j2me.game.tpg;

import java.util.Vector;
import net.eiroca.j2me.app.BaseApp;

/**
 * The Class GameMinMax.
 */
public class GameMinMax {

  /** The Constant MAX_POINT. */
  public static final int MAX_POINT = 1000000;

  /** The cancelled. */
  protected boolean cancelled;

  /** The precalculated_ opponent moves. */
  protected Vector precalculated_OpponentMoves = new Vector();

  /** The precalculated_ response moves. */
  protected Vector precalculated_ResponseMoves = new Vector();

  /**
   * Cancel.
   * 
   * @param cancel the cancel
   */
  public void cancel(final boolean cancel) {
    cancelled = cancel;
  }

  /**
   * Minimax.
   * 
   * @param depth the depth
   * @param state the state
   * @param player the player
   * @param tpg the tpg
   * @param alphabeta the alphabeta
   * @param alpha the alpha
   * @param order the order
   * @param kill the kill
   * @param killerMove the killer move
   * @return the game move
   */
  public GameMove minimax(final int depth, final GameTable state, final byte player, final TwoPlayerGame tpg, final boolean alphabeta, final int alpha, final boolean order, final boolean kill, final GameMove killerMove) {
    if (cancelled) {
      cancelled = false;
      return null;
    }
    GameMove bestMove;
    if (depth == 0) {
      bestMove = state.getEmptyMove();
      bestMove.setPoint(tpg.point(state, player));
      return bestMove;
    }
    GameMove actMove;
    GameMove kMove;
    boolean cut = false;
    int actPoint;
    int maxPoint = -GameMinMax.MAX_POINT; /* -Integer.MIN_VALUE ?? */
    int bestNum = 0;
    final GameMove pMoves[] = tpg.possibleMoves(state, player);
    if (pMoves == null) {
      // game ended
      bestMove = state.getEmptyMove();
      bestMove.setPoint(tpg.point(state, player));
      return bestMove;
    }
    final int[] bestMoves = new int[pMoves.length];
    final GameTable newState = state.copyFrom();
    if ((depth > 2) && order && (pMoves.length > 1)) {
      final int points[] = new int[pMoves.length];
      for (int oindex = 0; oindex < pMoves.length; ++oindex) {
        tpg.turn(state, player, pMoves[oindex], newState);
        points[oindex] = tpg.point(newState, player);
      }
      int oindex3 = 0;
      for (int oindex1 = 0; oindex1 < pMoves.length - 1; ++oindex1) {
        // maxsearch
        for (int oindex2 = oindex1; oindex2 < pMoves.length; ++oindex2) {
          if ((oindex2 == oindex1) || (points[oindex2] > points[oindex3])) {
            oindex3 = oindex2;
          }
        }
        if (oindex3 != oindex1) {
          final GameMove swapMove = pMoves[oindex3];
          pMoves[oindex3] = pMoves[oindex1];
          pMoves[oindex1] = swapMove;
        }
      }
    }
    if (kill && (killerMove != null) && (pMoves.length > 1)) {
      int kindex = 0;
      while ((kindex < pMoves.length) && !pMoves[kindex].equals(killerMove)) {
        ++kindex;
      }
      if ((kindex < pMoves.length) && (kindex != 0)) {
        // we have a killermove, but it's not the first one
        final GameMove swapMove = pMoves[0];
        pMoves[0] = pMoves[kindex];
        pMoves[kindex] = swapMove;
      }
    }
    actMove = null;
    for (int i = 0; !cut && (i < pMoves.length); ++i) {
      tpg.turn(state, player, pMoves[i], newState);
      if (depth == 1) {
        actPoint = tpg.point(newState, player);
      }
      else {
        if (kill && (i != 0)) {
          kMove = actMove;
        }
        else {
          kMove = null;
        }
        actMove = minimax(depth - 1, newState, (byte)(1 - player), tpg, alphabeta, -maxPoint, order, kill, kMove);
        if (actMove == null) { return null; }
        actPoint = -actMove.getPoint();
      }
      if ((i == 0) || (actPoint > maxPoint)) {
        // better move
        maxPoint = actPoint;
        if (alphabeta && (alpha < maxPoint)) {
          cut = true;
        }
        bestNum = 1;
        bestMoves[0] = i;
      }
      else if (actPoint == maxPoint) {
        // same as the better
        bestMoves[bestNum++] = i;
      }
    }
    int bestIndex;
    if (bestNum > 1) {
      bestIndex = bestMoves[BaseApp.rand(bestNum)];
    }
    else {
      bestIndex = bestMoves[0];
    }
    bestMove = pMoves[bestIndex];
    bestMove.setPoint(maxPoint);
    return bestMove;
  }

  /**
   * Fore minimax.
   * 
   * @param depth the depth
   * @param state the state
   * @param player the player
   * @param tpg the tpg
   * @param alphabeta the alphabeta
   * @param alpha the alpha
   * @param order the order
   * @param kill the kill
   */
  public void foreMinimax(final int depth, final GameTable state, final byte player, final TwoPlayerGame tpg, final boolean alphabeta, final int alpha, final boolean order, final boolean kill) {
    cancelled = false;
    precalculated_OpponentMoves.removeAllElements();
    precalculated_ResponseMoves.removeAllElements();
    final GameMove pMoves[] = tpg.possibleMoves(state, (byte)(1 - player));
    if (pMoves == null) { return; }
    final GameTable newState = state.copyFrom();
    GameMove bestMove;
    for (int i = 0; i < pMoves.length; ++i) {
      if (cancelled) {
        break;
      }
      tpg.turn(state, (byte)(1 - player), pMoves[i], newState);
      bestMove = minimax(depth, newState, player, tpg, alphabeta, alpha, order, kill, null);
      if (bestMove == null) { return; }
      precalculated_OpponentMoves.addElement(pMoves[i]);
      precalculated_ResponseMoves.addElement(bestMove);
    }
  }

  /**
   * Clear precalculated moves.
   */
  public void clearPrecalculatedMoves() {
    precalculated_OpponentMoves.removeAllElements();
    precalculated_ResponseMoves.removeAllElements();
  }

  /**
   * Precalculated best move.
   * 
   * @param move the move
   * @return the game move
   */
  public GameMove precalculatedBestMove(final GameMove move) {
    for (int i = 0; i < precalculated_OpponentMoves.size(); i++) {
      if (move.equals(precalculated_OpponentMoves.elementAt(i))) { return (GameMove)precalculated_ResponseMoves.elementAt(i); }
    }
    return null;
  }

}
