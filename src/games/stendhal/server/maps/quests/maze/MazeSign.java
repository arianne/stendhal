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
package games.stendhal.server.maps.quests.maze;

import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.mapstuff.sign.SignFromHallOfFameLoader;

public class MazeSign extends Sign {
	private static final int SIGN_LENGTH = 10;

	/**
	 * creates a new maze sign.
	 */
	public MazeSign() {
		updatePlayers();
		put("class", "book_blue");
	}

	/**
	 * Update the player list written on the sign.
	 */
	public void updatePlayers() {
		String introduction = "The best maze runners:\n";
		SignFromHallOfFameLoader loader = new SignFromHallOfFameLoader(this, introduction, "M", SIGN_LENGTH, false, true);
		TurnNotifier.get().notifyInTurns(0, loader);
	}
}
