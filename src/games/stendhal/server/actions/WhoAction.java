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

import static games.stendhal.common.constants.Actions.WHO;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.entity.player.Player;

import java.util.TreeSet;

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

	public void onAction(final Player player, final RPAction action) {
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


}
