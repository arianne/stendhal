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
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;

public class PlayersQuery implements ActionListener {

	private static final Logger logger = Log4J.getLogger(PlayersQuery.class);

	public static void register() {
		PlayersQuery query = new PlayersQuery();
		StendhalRPRuleProcessor.register("who", query);
		StendhalRPRuleProcessor.register("where", query);
	}

	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("who")) {
			onWho(player, action);
		} else {
			onWhere(player, action);
		}
	}

	public void onWho(Player player, RPAction action) {
		final int REQUIRED_LEVEL_TO_SEE_GHOST=AdministrationAction.getLevelForCommand("ghostmode");

		StendhalRPRuleProcessor rules = StendhalRPRuleProcessor.get();
		if (player.has("title")) rules.addGameEvent(player.get("title"), "who");
		rules.addGameEvent(player.getName(), "who");

		StringBuilder online = new StringBuilder();
		int amount=0;
		for (Player p : rules.getPlayers()) {
			if(!p.isGhost() || player.getAdminLevel()>REQUIRED_LEVEL_TO_SEE_GHOST) {
				amount++;
			}
		}
		
		online.append( amount+ " Players online: ");
		for (Player p : getSortedPlayers()) {
			if(!p.isGhost()  || player.getAdminLevel()>REQUIRED_LEVEL_TO_SEE_GHOST ) {
				String playername=p.getName();
				if (p.has("title")) {
					playername=p.get("title");
				}
				
				online.append(playername + "(" + (p.isGhost()?"!":"")+ p.getLevel() + ") ");
			}
		}
		player.sendPrivateText(online.toString());
		player.notifyWorldAboutChanges();
	}

	/**
	 * sorts the list of players
	 *
	 * @return sorted list of players
	 */
	private List<Player> getSortedPlayers() {
		StendhalRPRuleProcessor rules = StendhalRPRuleProcessor.get();
		List<Player> players = new ArrayList<Player>(rules.getPlayers());
		Collections.sort(players, new Comparator<Player>() {

			public int compare(Player o1, Player o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		return players;
	}

	public void onWhere(Player player, RPAction action) {
		StendhalRPRuleProcessor rules = StendhalRPRuleProcessor.get();
		if (action.has("target")) {
			String whoName = action.get("target");

			rules.addGameEvent(player.getName(), "where", whoName);

			Player who = rules.getPlayer(whoName);
			if (who != null && !who.isGhost()) {
				player.sendPrivateText(who.getName() + " is in " + who.get("zoneid") + " at (" + who.getX() + ","
				        + who.getY() + ")");
				player.notifyWorldAboutChanges();
			} else if (whoName.equals("sheep") && player.hasSheep()) {
				Sheep sheep = (Sheep) StendhalRPWorld.get().get(player.getSheep());
				player.sendPrivateText("Your sheep is in " + sheep.get("zoneid") + " at (" + sheep.getX() + ","
				        + sheep.getY() + ")");
				player.notifyWorldAboutChanges();
			} else {
				player.sendPrivateText("No player named \"" + action.get("target") + "\" is currently logged in.");
			}
		}
	}
}
