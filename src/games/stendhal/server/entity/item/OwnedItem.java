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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;


/**
 * Class representing an item owned by an entity.
 */
public abstract class OwnedItem extends Item {

	// slots to which item cannot be equipped if it has an owner
	private List<String> ownedBlacklistSlots = Arrays.asList("trade");
	// slots to which non-owners cannot equip
	private List<String> ownerOnlySlots;


	public OwnedItem(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public OwnedItem(final OwnedItem item) {
		super(item);
	}

	@Override
	public String describe() {
		String description = super.describe();

		final String owner = getOwner();
		if (owner != null) {
			description += " This " + getTitle() + " belongs to " + owner + " and cannot be used by others.";
		}

		return description;
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		// only allow owner to use
		final String owner = getOwner();
		if (owner != null && !user.getName().equals(owner)) {
			onUseFail(user);
			return false;
		}

		return true;
	}

	/**
	 * Sets the owner of the item.
	 *
	 * @param name
	 * 		Owner's name.
	 */
	public abstract void setOwner(final String name);

	/**
	 * Override to retrieve owner name.
	 *
	 * @return
	 * 		Name of owner or <code>null</code> if not owned.
	 */
	public abstract String getOwner();

	/**
	 * Override to check if item has owner.
	 *
	 * @return
	 * 		<code>true</code> if owned.
	 */
	public boolean hasOwner() {
		return getOwner() != null;
	}

	/**
	 * Checks if the submitted name matches the owner of the item.
	 *
	 * @param name
	 * 		Name to check.
	 * @return
	 * 		<code>true</code> if the name matches the item owner.
	 */
	public boolean isOwner(final String name) {
		return name.equals(getOwner());
	}

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
	public boolean canEquipToSlot(final RPEntity entity, final String slot) {
		if (hasOwner()) {
			if (ownedBlacklistSlots != null && ownedBlacklistSlots.contains(slot)) {
				return false;
			}

			if (ownerOnlySlots != null && ownerOnlySlots.contains(slot)) {
				return entity.getName().equals(getOwner());
			}
		}

		return true;
	}

	/**
	 * Override for action to take when an entity cannot equip to specified slot.
	 *
	 * @param entity
	 * 		Entity attempting to equip.
	 * @param slot
	 * 		Slot where item could not be equipped.
	 */
	public void onEquipFail(final RPEntity entity, final String slot) {
		if (entity instanceof Player) {
			final Player player = (Player) entity;
			if (ownedBlacklistSlots.contains(slot)) {
				player.sendPrivateText("You can't carry this owned " + getTitle() + " on your " + slot + ".");
			} else if (ownerOnlySlots.contains(slot)) {
				player.sendPrivateText("Only " + getOwner() + " can carry this " + getTitle() + " in their " + slot + ".");
			}
		}
	}

	@SuppressWarnings("unused")
	public void onUseFail(final RPEntity user) {
		// override to add behavior
	}

	/**
	 * Sets the slots that this item cannot be equipped to if
	 * it has an owner.
	 *
	 * @param slots
	 * 		List of slot names.
	 */
	public void setBlacklistSlots(final List<String> slots) {
		ownedBlacklistSlots = slots;
	}

	/**
	 * Sets the slots that can be equipped to by owner only if
	 * it has an owner.
	 *
	 * @param slots
	 * 		List of slots names.
	 */
	public void setOwnerOnlySlots(final List<String> slots) {
		ownerOnlySlots = slots;
	}
}
