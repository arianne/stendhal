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
package games.stendhal.client;

public final class World {

	private static World instance = new World();

	private World() {}

	public static World get() {
		return instance;
	}

	private final PlayerList playerList = new PlayerList();

	public PlayerList getPlayerList() {
		return playerList;
	}

	public void removePlayerLoggingOut(final String player) {
		playerList.removePlayer(player);
	}

	public void addPlayerLoggingOn(final String player) {
		playerList.addPlayer(player);
	}

}
