/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class JailReportAction extends AdministrationAction {
	private static final String JAILREPORT = "jailreport";

	public static void register() {
		CommandCenter.register(JAILREPORT, new JailReportAction(), 50);

	}

	@Override
	protected void perform(final Player player, final RPAction action) {
		final Jail jail = SingletonRepository.getJail();
		final String playerName = action.get(TARGET);

		if (playerName != null) {
			final ArrestWarrant warrant = jail.getWarrant(playerName);

			if (warrant != null) {
				player.sendPrivateText(warrant.getCriminal() + ": "
						+ warrant.getMinutes() + " Minutes because: "
						+ warrant.getReason());
			} else {
				player.sendPrivateText(playerName + " is not in jail");
			}
		} else {
			player.sendPrivateText(jail.listJailed());
		}

		player.notifyWorldAboutChanges();
	}

}
