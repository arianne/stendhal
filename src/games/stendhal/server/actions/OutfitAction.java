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
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class OutfitAction implements ActionListener {

	private static final Logger logger = Log4J.getLogger(OutfitAction.class);

	public static void register() {
		StendhalRPRuleProcessor.register("outfit", new OutfitAction());
	}

	public void onAction(Player player, RPAction action) {
		Log4J.startMethod(logger, "outfit");

		if (action.has("value")) {
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "outfit", action.get("value"));
			Outfit outfit = new Outfit(action.getInt("value"));
			player.setOutfit(outfit, false);
		}

		Log4J.finishMethod(logger, "outfit");
	}
}
