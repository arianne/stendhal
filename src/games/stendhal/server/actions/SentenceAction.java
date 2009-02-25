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
import static games.stendhal.common.constants.Actions.SENTENCE;
import static games.stendhal.common.constants.Actions.VALUE;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class SentenceAction implements ActionListener {


	public static void register() {
		CommandCenter.register(SENTENCE, new SentenceAction());
	}

	public void onAction(final Player player, final RPAction action) {
		if (action.has(VALUE)) {
			player.setSentence(action.get(VALUE));
		}
	}
}
