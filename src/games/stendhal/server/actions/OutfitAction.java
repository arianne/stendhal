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

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class OutfitAction implements ActionListener {


	public static void register() {
		CommandCentre.register("outfit", new OutfitAction());
	}

	public void onAction(Player player, RPAction action) {


		if (action.has("value")) {
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "outfit", action.get("value"));
			Outfit outfit = new Outfit(action.getInt("value"));
			if (outfit.isChoosableByPlayers()) {
				player.setOutfit(outfit, false);
			}
		}


	}
}
