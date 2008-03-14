/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class SentenceAction implements ActionListener {

	private static final String _VALUE = "value";
	private static final String _SENTENCE = "sentence";

	public static void register() {
		CommandCenter.register(_SENTENCE, new SentenceAction());
	}

	public void onAction(Player player, RPAction action) {
		if (action.has(_VALUE)) {
			player.setSentence(action.get(_VALUE));
		}
	}
}