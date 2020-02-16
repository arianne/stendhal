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

import static games.stendhal.common.constants.Actions.REMOVEDETAIL;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;


/**
 * Action that players can execute to manually remove the detail outfit layer.
 */
public class RemoveDetailAction implements ActionListener {

	public static void register() {
		CommandCenter.register(REMOVEDETAIL, new RemoveDetailAction());
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		final Outfit outfit = player.getOutfit();
		outfit.setLayer("detail", 0);
		player.setOutfit(outfit);
	}
}
