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

import static games.stendhal.common.constants.Actions.JAIL;
import static games.stendhal.common.constants.Actions.MINUTES;
import static games.stendhal.common.constants.Actions.TARGET;

import java.sql.SQLException;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

public class JailAction extends AdministrationAction {

	private static final String USAGE_JAIL_NAME_MINUTES_REASON = "Usage: /jail <name> <minutes> <reason>";

	public static void register() {
		CommandCenter.register(JAIL, new JailAction(), 400);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (!action.has(TARGET) || !action.has(MINUTES)) {
			player.sendPrivateText(USAGE_JAIL_NAME_MINUTES_REASON);
			return;
		}

		// extract and validate minutes
		int minutes;
		try {
			minutes = action.getInt(MINUTES);
		} catch (final NumberFormatException e) {
			player.sendPrivateText(USAGE_JAIL_NAME_MINUTES_REASON);
			return;
		}

		// extract and validate player
		final String target = action.get(TARGET);
		if (StendhalRPRuleProcessor.get().getPlayer(target) == null) {
			try {
				if (!DAORegister.get().get(CharacterDAO.class).hasCharacter(target)) {
					player.sendPrivateText("No character with that name: " + target);
					return;
				}
			} catch (SQLException e) {
				logger.error(e, e);
			}
		}

		// extract reason
		String reason = "";
		if (action.has("reason")) {
			reason = action.get("reason");
		}

		SingletonRepository.getJail().imprison(target, player, minutes, reason);
		new GameEvent(player.getName(), JAIL, target, Integer.toString(minutes), reason).raise();

	}

}
