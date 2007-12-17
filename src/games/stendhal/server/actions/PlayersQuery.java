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

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import marauroa.common.game.RPAction;
import static games.stendhal.server.actions.WellKnownActionConstants.*;

public class PlayersQuery implements ActionListener {

	private static final String _SHEEP = "sheep";
	private static final String _WHERE = "where";
	private static final String _WHO = "who";

	public static void register() {
		PlayersQuery query = new PlayersQuery();
		CommandCenter.register(_WHO, query);
		CommandCenter.register(_WHERE, query);
	}

	public void onAction(Player player, RPAction action) {
		if (action.get(TYPE).equals(_WHO)) {
			onWho(player, action);
		} else {
			onWhere(player, action);
		}
	}

	public void onWho(Player player, RPAction action) {
		final Collection< ? extends Player> playerlist = StendhalRPRuleProcessor.getPlayers(player.getAdminLevel());
		
		StendhalRPRuleProcessor rules = StendhalRPRuleProcessor.get();

		rules.addGameEvent(player.getName(), _WHO);

		StringBuilder online = new StringBuilder();
		int amount = playerlist.size();

		online.append(amount + " Players online: ");
		
		
		for (Player p : getSortedPlayers(playerlist)) {
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
		player.sendPrivateText(online.toString());
		player.notifyWorldAboutChanges();
	}

	/**
	 * sorts the list of players.
	 * @param playerlist TODO
	 * 
	 * @return sorted list of players<
	 */
	private List<Player> getSortedPlayers(Collection< ? extends Player> playerlist) {
		List<Player> players = new ArrayList<Player>(playerlist);
		Collections.sort(players, new Comparator<Player>() {

			public int compare(Player o1, Player o2) {
				return o1.getTitle().compareToIgnoreCase(o2.getTitle());
			}
		});
		return players;
	}

	public void onWhere(Player player, RPAction action) {
		if (action.has(TARGET)) {
			String whoName = action.get(TARGET);

			StendhalRPRuleProcessor rules = StendhalRPRuleProcessor.get();

			rules.addGameEvent(player.getName(), _WHERE, whoName);

			Player who = rules.getPlayer(whoName);
			if (who != null && !who.isGhost()) {
				StendhalRPZone zone = who.getZone();

				if (zone != null) {
					player.sendPrivateText(who.getTitle() + " is in " + zone.getName() + " at (" + who.getX() + ","
							+ who.getY() + ")");
				}
			} else if (whoName.equals(_SHEEP)) {
				Sheep sheep = player.getSheep();

				if (sheep != null) {
					player.sendPrivateText("Your sheep is at (" + sheep.getX() + "," + sheep.getY() + ")");

				} else {
					player.sendPrivateText("You currently have no sheep.");
				}
			} else if (whoName.equals("pet")) {
				Pet pet = player.getPet();

				if (pet != null) {
					player.sendPrivateText("Your pet is at (" + pet.getX() + "," + pet.getY() + ")");

				} else {
					player.sendPrivateText("You currently have no pet.");
				}
			} else {
				player.sendPrivateText("No player named \"" + whoName + "\" is currently logged in.");
			}

			player.notifyWorldAboutChanges();
		}
	}
}
