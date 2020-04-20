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

import static games.stendhal.common.constants.Actions.INSPECTKILL;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;


/**
 * Checks kill counts of a player for a specified creature.
 */
public class InspectKillAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(INSPECTKILL, new InspectKillAction(), 600);
	}

	@Override
	protected void perform(final Player admin, final RPAction action) {

		if (!action.has("target")) {
			admin.sendPrivateText("\"target\" parameter required: " + action);
			return;
		}

		final Entity target = getTargetAnyZone(admin, action);
		if (target == null) {
			admin.sendPrivateText("Player \"" + action.get("target") + "\" not found: " + action);
			return;
		}

		if (!(target instanceof Player)) {
			admin.sendPrivateText("Cannot calculate kills for non-player entity \"" + target.getName() + "\": " + action);
			return;
		}

		if (!action.has("creature")) {
			admin.sendPrivateText("\"creature\" parameter required: " + action);
			return;
		}

		final String creature = Grammar.singular(action.get("creature"));
		if (!SingletonRepository.getEntityManager().isCreature(creature)) {
			admin.sendPrivateText("\"" + creature + "\" is not a valid creature name: " + action);
			return;
		}

		final Player player = (Player) target;
		final int solokill = player.getSoloKill(creature);
		final int sharedkill = player.getSharedKill(creature);

		admin.sendPrivateText(Grammar.plural(creature) + " killed by " + player.getName() + ": solo: " + solokill + "; shared: " + sharedkill + "; total: " + (solokill + sharedkill));
	}
}
