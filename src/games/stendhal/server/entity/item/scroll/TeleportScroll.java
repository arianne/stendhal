/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.scroll;

import java.util.Map;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

/**
 * Represents a general teleport scroll.
 */
public abstract class TeleportScroll extends Scroll {

	/**
	 * Creates a new teleport scroll.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public TeleportScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public TeleportScroll(final TeleportScroll item) {
		super(item);
	}

	/**
	 * Is invoked when a teleporting scroll is actually used.
	 *
	 * @param player
	 *            The player who used the scroll and who will be teleported
	 *
	 * @return true iff teleport was successful
	 */
	protected abstract boolean useTeleportScroll(Player player);

	/**
	 * Is invoked when a teleporting scroll is used. Tries to put the player on
	 * the scroll's destination, or near it.
	 *
	 * @param player
	 *            The player who used the scroll and who will be teleported
	 * @return true iff teleport was successful
	 */
	@Override
	protected boolean useScroll(final Player player) {
		final StendhalRPZone zone = player.getZone();

		if (!zone.isTeleportOutAllowed(player.getX(), player.getY())) {
			player.sendPrivateText("The strong anti magic aura in this area prevents the scroll from working!");
			return false;
		}

		return useTeleportScroll(player);
	}
}
