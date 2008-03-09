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

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import static games.stendhal.server.actions.WellKnownActionConstants.TYPE;
import games.stendhal.common.ItemTools;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.player.Player;

import java.util.TreeSet;

import marauroa.common.game.RPAction;

public class PlayersQuery implements ActionListener {

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
		StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();
		final TreeSet<String> treeSet = new TreeSet<String>();

		if (player.getAdminLevel() >= AdministrationAction.getLevelForCommand("ghostmode")) {
			rules.getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
				public void execute(Player p) {
					StringBuilder text = new StringBuilder(p.getTitle());

					if (p.isGhost()) {
						text.append("(!");
					} else {
						text.append("(");
					}

					text.append(p.getLevel());

					text.append(") ");
					treeSet.add(text.toString());
				}
			});
		} else {
			rules.getOnlinePlayers().forFilteredPlayersExecute(new Task<Player>() {
				public void execute(Player p) {
					StringBuilder text = new StringBuilder(p.getTitle());
					text.append("(");

					text.append(p.getLevel());
					text.append(") ");
					treeSet.add(text.toString());
				}
			}, new FilterCriteria<Player>() {

				public boolean passes(Player o) {
					return !o.isGhost();
				}
			});
		}

		StringBuilder online = new StringBuilder();
		online.append(treeSet.size() + " Players online: ");
		for (String text : treeSet) {
			online.append(text);
		}
		player.sendPrivateText(online.toString());
		player.notifyWorldAboutChanges();
	}

	public void onWhere(Player player, RPAction action) {
		if (action.has(TARGET)) {
			String whoName = action.get(TARGET);

			StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();

			rules.addGameEvent(player.getName(), _WHERE, whoName);

			Player who = rules.getPlayer(whoName);
			DomesticAnimal animal = player.searchAnimal(whoName, false);

			if (who != null && !who.isGhost()) {
				StendhalRPZone zone = who.getZone();

				if (zone != null) {
					player.sendPrivateText(who.getTitle() + " is in " + zone.getName()
							+ " at (" + who.getX() + "," + who.getY() + ")");
				}
			}

			if (animal != null) {
				player.sendPrivateText("Your " + ItemTools.itemNameToDisplayName(animal.get("type"))
							+ " is at (" + animal.getX() + "," + animal.getY() + ")");
			}

			if (who == null && animal == null) {
				player.sendPrivateText("No player or pet named \"" + whoName + "\" is currently logged in.");
			}

			player.notifyWorldAboutChanges();
		}
	}
}
