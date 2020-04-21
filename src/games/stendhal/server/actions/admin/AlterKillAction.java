/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import static games.stendhal.common.constants.Actions.ALTERKILL;

import java.util.Arrays;

import games.stendhal.common.NotificationType;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;


/**
 * Changes solo or shared kill count of specified creature for player.
 */
public class AlterKillAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(ALTERKILL, new AlterKillAction(), 900);
	}

	@Override
	protected void perform(final Player admin, final RPAction action) {
		for (final String param: Arrays.asList("target", "killtype", "count", "creature")) {
			if (!action.has(param)) {
				admin.sendPrivateText("\"" + param + "\" parameter required: " + action);
				return;
			}
		}

		final Entity target = getTargetAnyZone(admin, action);
		if (target == null) {
			admin.sendPrivateText("Player \"" + action.get("target") + "\" not found: " + action);
			return;
		}
		if (!(target instanceof Player)) {
			admin.sendPrivateText("Cannot alter kills for non-player entity \"" + target.getName() + "\": " + action);
			return;
		}

		final String killtype = action.get("killtype").toLowerCase();
		if (!Arrays.asList("solo", "shared").contains(killtype)) {
			admin.sendPrivateText("Kill type must be either \"solo\" or \"shared\": " + action);
			return;
		}
		final boolean solo = killtype.equals("solo");

		final int count;
		try {
			count = Integer.parseInt(action.get("count"));
		} catch (final NumberFormatException e) {
			admin.sendPrivateText("Kill count must be a number: " + action);
			return;
		}

		final String creatureOrig = action.get("creature");
		final String creature = Grammar.singular(creatureOrig);
		if (!SingletonRepository.getEntityManager().isCreature(creature)) {
			admin.sendPrivateText("\"" + creatureOrig + "\" is not a valid creature name: " + action);
			return;
		}

		final Player player = (Player) target;

		if (solo) {
			player.setSoloKillCount(creature, count);
		} else {
			player.setSharedKillCount(creature, count);
		}

		// Notify player of changes
		player.sendPrivateText(NotificationType.SUPPORT, "Your " + killtype + " kill count for " + creature + " was changed to "
				+ count + " by " + admin.getTitle());
	}
}
