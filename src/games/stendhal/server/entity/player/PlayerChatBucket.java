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
package games.stendhal.server.entity.player;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.util.IntRingBuffer;

/**
 * a bucket to manage chat traffic shaping.
 *
 * @author hendrik
 */
public class PlayerChatBucket {
	private static final int TIMEFRAME_IN_TURNS = 10000 / 300;

	private final IntRingBuffer lastChatTurns = new IntRingBuffer(10);

	/**
	 * checks that the bucket is not full, yet and adds the turn in case there is still room.
	 *
	 * @param length length of chat message
	 * @return true if the bucket is not full, yet; false otherwise.
	 */
	public boolean checkAndAdd(int length) {
		int turn = SingletonRepository.getRuleProcessor().getTurn();
		boolean res = check(turn);
		if (res) {
			add(turn, length);
		}
		return res;
	}

	/**
	 * checks if this player is allowed to chat
	 *
	 * @param turn currentTurn
	 * @return true, if the player is allowed to chat, false otherwise.
	 */
	private boolean check(int turn) {
		lastChatTurns.removeSmaller(turn - TIMEFRAME_IN_TURNS);
		return !lastChatTurns.isFull();
	}

	/**
	 * adds the current turn to the ring buffer
	 *
	 * @param turn currentTurn
	 * @param length length of chat message
	 */
	private void add(int turn, int length) {
		// use <= to ensure at least one addition
		for (int i = 0; i <= length / 50; i++) {
			lastChatTurns.add(turn);
		}
	}
}
