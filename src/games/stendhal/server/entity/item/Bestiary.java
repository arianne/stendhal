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

import java.util.Map;

import games.stendhal.server.actions.BestiaryAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;


/**
 * Item class that shows some basic information about enemies around Faiumoni.
 */
public class Bestiary extends Item {

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
			new BestiaryAction().onAction((Player) user, null);
			return true;
		}

		return false;
	}
}
