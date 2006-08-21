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
import games.stendhal.server.entity.creature.Sheep;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class PlayersQuery extends ActionListener {
	private static final Logger logger = Log4J.getLogger(PlayersQuery.class);

	public static void register() {
		PlayersQuery query = new PlayersQuery();
		StendhalRPRuleProcessor.register("who", query);
		StendhalRPRuleProcessor.register("where", query);
	}

	@Override
	public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		if (action.get("type").equals("who")) {
			onWho(world, rules, player, action);
		} else {
			onWhere(world, rules, player, action);
		}
	}

	public void onWho(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "who");

		rules.addGameEvent(player.getName(), "who");

		String online = "" + rules.getPlayers().size() + " Players online: ";
		for (Player p : rules.getPlayers()) {
			online += p.getName() + "(" + p.getLevel() + ") ";
		}
		player.sendPrivateText(online);
		world.modify(player);
		Log4J.finishMethod(logger, "who");
	}

	public void onWhere(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "where");

		if (action.has("target")) {
			String whoName = action.get("target");

			rules.addGameEvent(player.getName(), "where", whoName);
			
			Player who = rules.getPlayer(whoName);
			if (who != null) {
				player.sendPrivateText(who.getName() + " is in "
						+ who.get("zoneid") + " at (" + who.getx() + ","
						+ who.gety() + ")");
				world.modify(player);
			} else if (whoName.equals("sheep") && player.hasSheep()) {
				Sheep sheep = (Sheep) world.get(player.getSheep());
				player.sendPrivateText("sheep is in " + sheep.get("zoneid")
						+ " at (" + sheep.getx() + "," + sheep.gety() + ")");
				world.modify(player);
			} else {
				player.sendPrivateText(action.get("target")
					+ " is currently not logged in.");
			}
		}

		Log4J.finishMethod(logger, "where");
	}
}
