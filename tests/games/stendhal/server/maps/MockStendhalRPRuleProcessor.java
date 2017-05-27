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
package games.stendhal.server.maps;

import games.stendhal.server.core.engine.PlayerList;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;

public class MockStendhalRPRuleProcessor extends StendhalRPRuleProcessor {
	private int turn;

	public static MockStendhalRPRuleProcessor get() {
		if (!(instance instanceof MockStendhalRPRuleProcessor)) {
			instance = new MockStendhalRPRuleProcessor();
		}

		return (MockStendhalRPRuleProcessor) instance;
	}

	@Override
	public int getTurn() {
		return turn;
	}

	/**
	 * Set the current fake game turn.
	 *
	 * @param turn
	 */
	public void setTurn(int turn) {
		this.turn = turn;
	}

	/**
	 * Adds a player object to the list of players.
	 *
	 * @param player Player
	 */
	public void addPlayer(final Player player) {
		this.onlinePlayers.add(player);
	}

	/**
	 * reset the list of online players.
	 */
	public void clearPlayers() {
		onlinePlayers = new PlayerList();
	}
}
