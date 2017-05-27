/***************************************************************************
 *                   (C) Copyright 2012-2013 Faiumoni                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.validator;

import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

/**
 * extracts the entity
 *
 * @author hendrik
 */
public class ExtractEntityValidator implements ActionValidator {

	@Override
	public String validate(Player player, RPAction action, ActionData data) {
		Entity entity = null;
		if (action.has(Actions.TARGET_PATH)) {
			entity = EntityHelper.getEntityFromPath(player, action.getList(Actions.TARGET_PATH));
		} else {
			entity = EntityHelper.entityFromSlot(player, action);

			if (entity == null) {
				entity = EntityHelper.entityFromTargetName(action.get(TARGET), player);
			}
		}

		if (entity instanceof Player) {
			if (((Player) entity).isGhost()
					&& (player.getAdminLevel() < AdministrationAction.getLevelForCommand(Actions.GHOSTMODE))) {
				entity = null;
			}
		}
		data.setEntity(entity);
		return null;
	}

}
