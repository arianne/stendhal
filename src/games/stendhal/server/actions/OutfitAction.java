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
import games.stendhal.server.entity.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class OutfitAction extends ActionListener {
	private static final Logger logger = Log4J.getLogger(OutfitAction.class);

	public static void register() {
		StendhalRPRuleProcessor.register("outfit", new OutfitAction());
	}

	@Override
	public void onAction(Player player, RPAction action) {
		Log4J.startMethod(logger, "outfit");

		if (action.has("value")) {
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "outfit", action.get("value"));
			player.put("outfit", action.get("value"));
			if (player.has("outfit_org")) {
				player.remove("outfit_org");
			}
			player.notifyWorldAboutChanges();
		}

		Log4J.finishMethod(logger, "outfit");
	}
}
