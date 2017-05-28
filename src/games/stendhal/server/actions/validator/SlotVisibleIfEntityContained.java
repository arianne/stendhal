/***************************************************************************
 *                   (C) Copyright 2003-2013 - Faiumoni                    *
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

import org.apache.log4j.Logger;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * checks that the entity is in a visible slots, or not contained at all
 *
 * @author hendrik
 */
public class SlotVisibleIfEntityContained implements ActionValidator {
	private static Logger logger = Logger.getLogger(SlotVisibleIfEntityContained.class);

	/**
	 * validates an RPAction.
	 *
	 * @param player Player
	 * @param action RPAction to validate
	 * @param data   data about this action
	 * @return <code>null</code> if the action is valid; an error message otherwise
	 */
	@Override
	public String validate(Player player, RPAction action, ActionData data) {
		RPObject entity = data.getEntity();
		if (entity == null) {
			return null;
		}

		RPObject base = entity.getBaseContainer();

		while (true) {
			RPSlot slot = entity.getContainerSlot();
			if (slot == null) {
				return null;
			}
			RPObject container = entity.getContainer();
			int flags = container.getRPClass().getDefinition(DefinitionClass.RPSLOT, slot.getName()).getFlags();

			boolean visible = (((flags & Definition.PRIVATE) == 0) || base == player) && ((flags & Definition.HIDDEN) == 0);
			if (!visible) {
				logger.warn("Slot is not visible: " + slot);
				return "You cannot look into that.";
			}
			entity = container;
		}
	}


}
