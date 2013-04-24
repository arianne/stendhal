/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.equipping.validator;

import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;

/**
 * checks that the item is either not bound, or the bound rules are respected
 *
 * @author hendrik
 */
public class IsBoundStatusRespected implements Validator {

	@Override
	public boolean validate(EquipmentActionData data) {

		// get boundTo, return on unbound items
		String boundTo = getItemBound(data);
		if (boundTo == null) {
			return true;
		}

		// check that the player to whom this item is bound, performed
		// the action
		if (!data.getPlayer().getName().equals(boundTo)) {
			data.setErrorMessage("This " + data.getItemName()
						+ " is a special reward for " + boundTo
						+ ". You do not deserve to use it.");
			return false;
		}

		// check that the target does not decline item bound to anyone
		if (data.getTargetSlot().isTargetBoundCheckRequired()) {
			data.setErrorMessage("You cannot put this special quest reward there because it can only be used by you.");
			return false;
		}
		return true;
	}

	/**
	 * gets the boundTo of the items.
	 *
	 * @param data EquipmentActionData
	 * @return name of player this item is bound to or <code>null</code> if unbound
	 */
	private String getItemBound(EquipmentActionData data) {

		// It can happen that we have both bound and unbound items,
		// but we can be sure that all the bound items are bound to the
		// same person
		for (Entity entity : data.getSourceItems()) {
			if (entity instanceof Item) {
				if (((Item) entity).isBound()) {
					return ((Item) entity).getBoundTo();
				}
			}
		}
		return null;
	}

}
