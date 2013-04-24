/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;

/**
 * Handles a time out for the game
 *
 * @author hendrik
 */
public class GameBoardTimer implements TurnListener {
	private GameBoard gameBoard;
	private int seconds;

	/**
	 * creates a new game board timer
	 *
	 * @param gameBoard gameBoard to time out
	 * @param seconds maximum number of seconds for one game
	 */
	public GameBoardTimer(GameBoard gameBoard, int seconds) {
		this.gameBoard = gameBoard;
		this.seconds = seconds;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		gameBoard.timeOut();
	}

	/**
	 * starts the timer
	 */
	public void start() {
		TurnNotifier.get().notifyInSeconds(seconds, this);
	}

	/**
	 * stops the timer
	 */
	public void stop() {
		TurnNotifier.get().dontNotify(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GameBoardTimer other = (GameBoardTimer) obj;
		if (!gameBoard.equals(other.gameBoard)) {
			return false;
		}
		if (seconds != other.seconds) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return gameBoard.hashCode() * prime + seconds;
	}

}
