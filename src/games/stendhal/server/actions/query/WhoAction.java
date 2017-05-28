/***************************************************************************
 *                   (C) Copyright 2003-2015 - Marauroa                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.query;

import static games.stendhal.common.constants.Actions.WHO;

import java.util.Set;
import java.util.TreeSet;

import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Lists all online players with their levels.
 * Administrators in ghostmode are only visible to other admins
 * and are flagged with a "!".
 */
public class WhoAction implements ActionListener {

	public static void register() {
		final WhoAction query = new WhoAction();
		CommandCenter.register(WHO, query);
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		final StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();
		final Set<String> players = new TreeSet<String>();

		new GameEvent(player.getName(), WHO).raise();
		if (player.getAdminLevel() >= AdministrationAction.getLevelForCommand("ghostmode")) {
			rules.getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
				@Override
				public void execute(final Player p) {
					final StringBuilder text = new StringBuilder(p.getTitle());

					if (p.isGhost()) {
						text.append("(!");
					} else {
						text.append("(");
					}
					text.append(p.getLevel());
					text.append(") ");

					players.add(text.toString());
				}
			});
		} else {
			rules.getOnlinePlayers().forFilteredPlayersExecute(new Task<Player>() {
				@Override
				public void execute(final Player p) {
					final StringBuilder text = new StringBuilder(p.getTitle());
					text.append("(");
					text.append(p.getLevel());
					text.append(") ");
					players.add(text.toString());
				}
			}, new FilterCriteria<Player>() {
				@Override
				public boolean passes(final Player o) {
					return !o.isGhost();
				}
			});
		}

		final StringBuilder online = new StringBuilder();
		online.append(players.size() + " Players online: ");
		for (final String name : players) {
			online.append(name);
		}
		player.sendPrivateText(online.toString());
		player.notifyWorldAboutChanges();
	}

}
