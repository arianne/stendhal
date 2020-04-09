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
import java.util.Map;

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.BestiaryEvent;


/**
 * Item class that shows some basic information about enemies around Faiumoni.
 */
public class Bestiary extends OwnedItem {

	public Bestiary(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		setMenu("Read|Use");

		setBlacklistSlots(Arrays.asList("trade"));
	}

	public Bestiary(final Item item) {
		super(item);

		setBlacklistSlots(Arrays.asList("trade"));
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (!(user instanceof Player)) {
			return false;
		}

		final Player player = (Player) user;

		if (!super.onUsed(player)) {
			player.sendPrivateText(NotificationType.RESPONSE, "You read: This bestiary is the property of " + getOwner() + ". Please return it to it's rightful owner.");
			return false;
		}

		player.addEvent(new BestiaryEvent(player));
		player.notifyWorldAboutChanges();

		return true;
	}

	/**
	 * Sets the owner of the item.
	 *
	 * @param name
	 * 		Owner's name.
	 */
	@Override
	public void setOwner(final String name) {
		put("infostring", name);
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
}
