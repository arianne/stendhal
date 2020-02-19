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

import static games.stendhal.common.constants.Actions.INSPECTQUEST;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;


/**
 * Retrieves quest state information from player slot.
 */
public class InspectQuestAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(INSPECTQUEST, new InspectQuestAction(), 600);
	}

	@Override
	protected void perform(final Player admin, final RPAction action) {

		final Entity target = getTargetAnyZone(admin, action);

		if (target == null) {
			final String text = "Entity not found for action" + action;
			admin.sendPrivateText(text);
			return;
		}

		if (target instanceof Player && action.has("quest_slot")) {
			final Player player = (Player) target;
			final String questSlot = action.get("quest_slot");
			final String questState = player.getQuest(questSlot);

			admin.sendPrivateText(questSlot + " (" + player.getName() + "): " + questState);
		}
	}
}
