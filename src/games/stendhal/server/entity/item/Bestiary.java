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

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.BestiaryEvent;


/**
 * Item class that shows some basic information about enemies around Faiumoni.
 */
public class Bestiary extends Item implements OwnedItem {

	// slots to which item cannot be equipped if it has an owner
	private final static List<String> ownedBlacklistSlots = Arrays.asList("trade");
	// slots to which non-owners cannot equip
	private final static List<String> ownerOnlySlots = Arrays.asList();


	public Bestiary(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		setMenu("Read|Use");
	}

	public Bestiary(final Item item) {
		super(item);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (user instanceof Player) {
			final Player player = (Player) user;
			final String infostring = getInfoString();

			// only allow owner to use
			if (infostring != null && !player.getName().equals(infostring)) {
				player.sendPrivateText(NotificationType.RESPONSE, "You read: This bestiary is the property of " + infostring + ". Please return it to it's rightful owner.");
				return false;
			}

			player.addEvent(new BestiaryEvent(player));
			player.notifyWorldAboutChanges();

			return true;
		}

		return false;
	}

	@Override
	public String describe() {
		String description = super.describe();

		final String owner = getOwner();
		if (owner != null) {
			description += " This book belongs to " + owner + " and cannot be used by others.";
		}

		return description;
	}

	/**
	 * Retrieves the owners name if the book has one.
	 *
	 * @return
	 * 		Name of owner or <code>null</code> if not owned.
	 */
	@Override
	public String getOwner() {
		return get("infostring");
	}

	/**
	 * Checks if the book has an owner.
	 *
	 * @return
	 * 		<code>true</code> if the infostring is set.
	 */
	@Override
	public boolean hasOwner() {
		return getOwner() != null;
	}

	@Override
	public boolean canEquipToSlot(final RPEntity entity, final String slot) {
		if (hasOwner()) {
			if (ownedBlacklistSlots.contains(slot)) {
				return false;
			}

			if (ownerOnlySlots.contains(slot)) {
				return entity.getName().equals(getOwner());
			}
		}

		return true;
	}

	@Override
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
}
