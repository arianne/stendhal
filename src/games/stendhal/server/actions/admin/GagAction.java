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

import static games.stendhal.common.constants.Actions.GAG;
import static games.stendhal.common.constants.Actions.MINUTES;
import static games.stendhal.common.constants.Actions.REASON;
import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class GagAction extends AdministrationAction {
	private static final String USAGE_GAG_NAME_MINUTES_REASON = "Usage: /gag name minutes reason";

	public static void register() {
		CommandCenter.register(GAG, new GagAction(), 200);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (action.has(TARGET) && action.has(MINUTES)) {
			final String target = action.get(TARGET);
			String reason = "";
			if (action.has(REASON)) {
				reason = action.get(REASON);
			}
			try {
				final int minutes = action.getInt(MINUTES);
				new GameEvent(player.getName(), GAG, target, Integer.toString(minutes), reason).raise();
				SingletonRepository.getGagManager().gag(target, player, minutes, reason);
			} catch (final NumberFormatException e) {
				player.sendPrivateText(USAGE_GAG_NAME_MINUTES_REASON);
			}
		} else {
			player.sendPrivateText(USAGE_GAG_NAME_MINUTES_REASON);
		}
	}

}
