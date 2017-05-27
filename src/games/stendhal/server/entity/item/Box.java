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
package games.stendhal.server.entity.item;

import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * a box which can be unwrapped.
 *
 * @author hendrik
 */
public class Box extends Item {

	private final static Logger logger = Logger.getLogger(Box.class);

	/**
	 * Creates a new box.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Box(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public Box(final Box item) {
		super(item);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (this.isContained()) {
			// We modify the base container if the object change.
			RPObject base = this.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			if (!user.nextTo((Entity) base)) {
				logger.debug("Consumable item is too far");
				user.sendPrivateText("The item is too far away.");
				return false;
			}
		} else {
			if (!user.nextTo(this)) {
				logger.debug("Consumable item is too far");
				user.sendPrivateText("The item is too far away.");
				return false;
			}
		}

		if (user instanceof Player) {
			return useMe((Player)user);
		} else {
			logger.error("user is not a instance of Player but: " + user, new Throwable());
			return false;
		}
	}

	// this would be overridden in the subclass
	protected boolean useMe(final Player player) {
		logger.warn("A box that didn't have a use method failed to open.");
		player.sendPrivateText("What a strange box! You don't know how to open it.");
		return false;
	}

}
