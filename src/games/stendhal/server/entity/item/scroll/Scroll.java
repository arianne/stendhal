/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * Represents a scroll.
 */
public class Scroll extends StackableItem {

	private static final Logger logger = Logger.getLogger(Scroll.class);

	/**
	 * Creates a new scroll.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Scroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public Scroll(final Scroll item) {
		super(item);
	}

	@Override
	public final boolean onUsed(final RPEntity user) {
		RPObject base = getBaseContainer();

		if (user.nextTo((Entity) base)) {
			// We need to remove the scroll before using it. Makes space in
			// the bag in the case of last empty scrolls, and prevents
			// the player getting free replacement scrolls from bank vaults.
			// Save the necessary information for backtracking:
			Scroll clone = (Scroll) clone();
			Scroll splitted = (Scroll) splitOff(1);
			StendhalRPZone zone = getZone();

			if (user instanceof Player && useScroll((Player)user)) {
				user.notifyWorldAboutChanges();
				return true;
			} else {
				if (getQuantity() != 0) {
					// Return what we just failed to use
					add(splitted);
				} else {
					// Used the last scroll, but failed. Return the
					// scroll to where it used to be
					if (clone.isContained()) {
						clone.getContainerSlot().add(clone);
					} else {
						// unset the zone first to avoid it looking like adding it to two zones
						clone.onRemoved(zone);
						zone.add(clone);
					}
				}

				return false;
			}
		} else {
			logger.debug("Scroll is too far away.");
			return false;
		}
	}

	/**
	 * Use a scroll.
	 *
	 * @param player
	 *            The player using scroll.
	 *
	 * @return <code>true</code> if successful, <code>false</code>
	 *         otherwise.
	 */
	protected boolean useScroll(final Player player) {
		player.sendPrivateText("What a strange scroll! You can't make heads or tails of it.");
		return false;
	}

}
