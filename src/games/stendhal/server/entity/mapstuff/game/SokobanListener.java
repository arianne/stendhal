/***************************************************************************
 *                   (C) Copyright 2013 - Faiumoni e. V.                   *
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

/**
 * listens to events from the Sokoban board
 *
 * @author hendrik
 */
public interface SokobanListener {

	/**
	 * level was completed successfully
	 *
	 * @param playerName name of player
	 * @param level completed level
	 */
	public void onSuccess(String playerName, int level);

	/**
	 * level timed out
	 *
	 * @param playerName name of player
	 * @param level failed level
	 */
	public void onTimeout(String playerName, int level);

	/**
	 * player wants to leave
	 *
	 * @param playerName name of player
	 * @param level failed level
	 */
	public void onLeave(String playerName, int level);
}
