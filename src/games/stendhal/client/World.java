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

	private static World instance;
	
	public static World get() {
		if(instance == null) {
			instance = new World();
		}
		return instance;
	}
	
	private PlayerList playerList = new PlayerList();

	public PlayerList getPlayerList() {
		return playerList;
	}
	
	public void removePlayerLoggingOut(String player) {
		playerList.removePlayer(player);
	}
	
	public void addPlayerLoggingOn(String player) {
		playerList.addPlayer(player);
	}
}
