/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import static games.stendhal.common.constants.Actions.BESTIARY;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.BestiaryEvent;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPEvent;

public class BestiaryAction implements ActionListener {

	public static void register() {
		CommandCenter.register(BESTIARY, new BestiaryAction());
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		final RPEvent event = new BestiaryEvent(player);
		player.addEvent(event);
		player.notifyWorldAboutChanges();
	}
}
