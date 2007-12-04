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
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import marauroa.common.game.RPAction;

public class PlayersQuery implements ActionListener {

	public static void register() {
		PlayersQuery query = new PlayersQuery();
		CommandCentre.register("who", query);
		CommandCentre.register("where", query);
	}

	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("who")) {
			onWho(player, action);
		} else {
			onWhere(player, action);
		}
	}

	public void onWho(Player player, RPAction action) {

		final int REQUIRED_LEVEL_TO_SEE_GHOST = AdministrationAction.getLevelForCommand("ghostmode");

		StendhalRPRuleProcessor rules = StendhalRPRuleProcessor.get();
		if (player.has("title")) {
			rules.addGameEvent(player.get("title"), "who");
		}
		rules.addGameEvent(player.getName(), "who");

		StringBuilder online = new StringBuilder();
		int amount = 0;
		for (Player p : rules.getPlayers()) {
			if (!p.isGhost()
					|| player.getAdminLevel() > REQUIRED_LEVEL_TO_SEE_GHOST) {
				amount++;
			}
		}

		online.append(amount + " Players online: ");
		for (Player p : getSortedPlayers()) {
			if (!p.isGhost()
					|| player.getAdminLevel() > REQUIRED_LEVEL_TO_SEE_GHOST) {
				String playername = p.getTitle();

				online.append(playername);

				if (p.isGhost()) {
					online.append("(!");
				} else {
					online.append("(");
				}
				online.append(p.getLevel());
				online.append(") ");
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
				return o1.getTitle().compareToIgnoreCase(o2.getTitle());
			}
		});
		return players;
	}

	public void onWhere(Player player, RPAction action) {
		if (action.has("target")) {
			String whoName = action.get("target");

			StendhalRPRuleProcessor rules = StendhalRPRuleProcessor.get();

			rules.addGameEvent(player.getName(), "where", whoName);

			Player who = rules.getPlayer(whoName);
			if (who != null && !who.isGhost()) {
				StendhalRPZone zone = who.getZone();

				if (zone != null) {
					player.sendPrivateText(who.getTitle() + " is in "
							+ zone.getName() + " at (" + who.getX() + ","
							+ who.getY() + ")");
				}
			} else if (whoName.equals("sheep")) {
				Sheep sheep = player.getSheep();

				if (sheep != null) {
					player.sendPrivateText("Your sheep is at (" + sheep.getX()
							+ "," + sheep.getY() + ")");

				} else {
					player.sendPrivateText("You currently have no sheep.");
				}
			} else if (whoName.equals("pet")) {
				Pet pet = player.getPet();

				if (pet != null) {
					player.sendPrivateText("Your pet is at (" + pet.getX()
							+ "," + pet.getY() + ")");

				} else {
					player.sendPrivateText("You currently have no pet.");
				}
			} else {
				player.sendPrivateText("No player named \"" + whoName
						+ "\" is currently logged in.");
			}

			player.notifyWorldAboutChanges();
		}
	}
}
