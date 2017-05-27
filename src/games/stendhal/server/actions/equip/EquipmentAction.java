/***************************************************************************
 *                   (C) Copyright 2003-1016 - Marauroa                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.equip;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.item.Container;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * This listener handles all entity movements from a slot to either another slot
 * or the ground.
 *
 * The source can be: baseitem - object id of the item which should be moved
 *
 * (optional, only when the source is inside a slot) baseobject - (only when the
 * item is in a slot) object id of the object containing the slot where the item
 * is in baseslot - (only when the item is in a slot) slot name where the item
 * is in (/optional)
 *
 *
 * The target can be either an 'equip': type - "equip" targetobject - object id
 * of the container object targetslot - slot name where the item should be moved
 * to
 *
 * or a 'drop': type - "drop" x - the x-coordinate on the ground y - the
 * y-coordinate on the ground
 */
public abstract class EquipmentAction implements ActionListener {

	protected static final Logger logger = Logger.getLogger(EquipmentAction.class);

	/** the list of valid container classes. */
	private static final Class< ? >[] validContainerClasses = new Class< ? >[] {
		Player.class, Chest.class, Corpse.class, Container.class };

	/** List of the valid container classes for easy access. */
	protected final List<Class< ? >> validContainerClassesList = Arrays.asList(validContainerClasses);



	@Override
	public void onAction(final Player player, final RPAction action) {

		if (!isValidAction(action, player)) {
			return;
		}


		String noItemMessage = player.getZone().getNoItemMoveMessage();
		if (noItemMessage != null) {
			player.sendPrivateText(noItemMessage);
			return;
		}


		//		isValidSource();
		//		isValidItem();
		//		isValidDestination();
		//




		logger.debug("Checking source object conditions: " + action);
		final SourceObject source = SourceObject.createSourceObject(action, player);
		if (source.isInvalidMoveable(player, EquipActionConsts.MAXDISTANCE, validContainerClassesList)) {
			return;
		}

		execute(player, action, source);
	}

	protected abstract void execute(final Player player, final RPAction action, final  SourceObject source);

	private boolean isValidAction(RPAction action, Player player) {
		if (action != null) {
			String actionZone = action.get("zone");
			// Always accept actions without the zone. Old clients send those.
			if (actionZone == null || actionZone.equals(player.getZone().getName())) {
				return true;
			}
		}
		return false;
	}

}
