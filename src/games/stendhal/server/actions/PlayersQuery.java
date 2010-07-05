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

import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TYPE;
import static games.stendhal.common.constants.Actions.WHERE;
import static games.stendhal.common.constants.Actions.WHO;
import games.stendhal.common.ItemTools;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.player.Player;

import java.util.TreeSet;

import marauroa.common.game.RPAction;

public class PlayersQuery implements ActionListener {

	

	public static void register() {
		final PlayersQuery query = new PlayersQuery();
		CommandCenter.register(WHO, query);
		CommandCenter.register(WHERE, query);
	}

	public void onAction(final Player player, final RPAction action) {
		if (action.get(TYPE).equals(WHO)) {
			onWho(player, action);
		} else {
			onWhere(player, action);
		}
	}

	public void onWho(final Player player, final RPAction action) {
		final StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();
		final TreeSet<String> treeSet = new TreeSet<String>();

		if (player.getAdminLevel() >= AdministrationAction.getLevelForCommand("ghostmode")) {
			rules.getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
				public void execute(final Player p) {
					final StringBuilder text = new StringBuilder(p.getTitle());

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
				public void execute(final Player p) {
					final StringBuilder text = new StringBuilder(p.getTitle());
					text.append("(");

					text.append(p.getLevel());
					text.append(") ");
					treeSet.add(text.toString());
				}
			}, new FilterCriteria<Player>() {

				public boolean passes(final Player o) {
					return !o.isGhost();
				}
			});
		}

		final StringBuilder online = new StringBuilder();
		online.append(treeSet.size() + " Players online: ");
		for (final String text : treeSet) {
			online.append(text);
		}
		player.sendPrivateText(online.toString());
		player.notifyWorldAboutChanges();
	}

	public void onWhere(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String whoName = action.get(TARGET);

			final StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();
			String[] params = { whoName };

			new GameEvent(player.getName(), WHERE, params).raise();

			final Player who = rules.getPlayer(whoName);
			final DomesticAnimal animal = player.searchAnimal(whoName, false);

			if (who != null) {
				if (who.isGhost() && !who.equals(player)) {
					player.sendPrivateText("No player or pet named \"" + whoName + "\" is currently logged in.");
				} else {
					final StendhalRPZone zone = who.getZone();

					if (zone != null) {
						if (who.equals(player)) {
							player.sendPrivateText("You are in " + zone.getName()
									+ " at (" + who.getX() + "," + who.getY() + ")");
						} else {
							player.sendPrivateText(who.getTitle() + " is in " + zone.getName()
									+ " at (" + who.getX() + "," + who.getY() + ")");
						}
					}
				}
			}

			if (animal != null) {
				player.sendPrivateText("Your " + ItemTools.itemNameToDisplayName(animal.get("type"))
							+ " is at (" + animal.getX() + "," + animal.getY() + ")");
			}

			if ((who == null) && (animal == null)) {
				player.sendPrivateText("No player or pet named \"" + whoName + "\" is currently logged in.");
			}

			player.notifyWorldAboutChanges();
		}
	}
}
