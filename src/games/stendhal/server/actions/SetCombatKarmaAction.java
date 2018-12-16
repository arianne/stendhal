/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

import static games.stendhal.common.Constants.KARMA_SETTINGS;
import static games.stendhal.common.constants.General.COMBAT_KARMA;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Sets entity's combat karma attribute to determine when karma is used on combat.
 */
public class SetCombatKarmaAction implements ActionListener {

	/* logger instance */
	final static Logger logger = Logger.getLogger(SetCombatKarmaAction.class);

	/**
	 * Registers the action.
	 */
	public static void register() {
		CommandCenter.register(COMBAT_KARMA, new SetCombatKarmaAction());
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (System.getProperty("stendhal.karmaconfig") == null) {
			player.sendPrivateText(NotificationType.ERROR, "Experimental karama settings not supported");
			return;
		}
		if (action.has(COMBAT_KARMA)) {
			final String combatKarmaMode = action.get(COMBAT_KARMA);
			// check that mode is valid value
			if (KARMA_SETTINGS.contains(combatKarmaMode)) {
				final List<String> descriptions = Arrays.asList(
						"Karma will not be used in combat.",
						"Karma will be used in combat against stronger enemies.",
						"Karma will always be used in combat.");

				player.put(COMBAT_KARMA, combatKarmaMode);
				player.sendPrivateText(NotificationType.CLIENT, descriptions.get(KARMA_SETTINGS.indexOf(combatKarmaMode)));
				return;
			}

			logger.warn("Could not set combat karma attribute for player " + player.getName() + " to \"" + combatKarmaMode + "\"");
			return;
		}

		logger.warn("Could not set combat karma attribute for player " + player.getName());
	}
}
