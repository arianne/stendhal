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
package games.stendhal.server.entity.item;

import games.stendhal.server.entity.RPEntity;


/**
 * Class representing an item owned by an entity.
 */
public interface OwnedItem {

	/**
	 * Override to retrieve owner name.
	 *
	 * @return
	 * 		Name of owner.
	 */
	public String getOwner();

	/**
	 * Override to check if item has owner.
	 *
	 * @return
	 * 		<code>true</code> if has owner.
	 */
	public boolean hasOwner();

	/**
	 * Override to check if an entity can equip to slot.
	 *
	 * @param entity
	 * 		Entity attempting to equip.
	 * @param slot
	 * 		Slot where item is being equipped.
	 * @return
	 * 		<code>true</code> if can be equipped, <code>false</code> otherwise.
	 */
	public boolean canEquipToSlot(final RPEntity entity, final String slot);

	/**
	 * Override for action to take when an entity cannot equip to specified slot.
	 *
	 * @param entity
	 * 		Entity attempting to equip.
	 * @param slot
	 * 		Slot where item could not be equipped.
	 */
	public void onEquipFail(final RPEntity entity, final String slot);
}
