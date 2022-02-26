/***************************************************************************
 *                   Copyright (C) 2003-2022 - Arianne                     *
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

import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.events.SoundEvent;


public class AreaUseItem extends Item {

	protected String use_sound;


	public AreaUseItem(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		if (attributes.containsKey("use_sound")) {
			use_sound = attributes.get("use_sound");
		}
	}

	/**
	 * Copy constructor.
	 *
	 * @param item Item to copy.
	 */
	public AreaUseItem(final AreaUseItem item) {
		super(item);
		use_sound = item.use_sound;
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (use_sound != null) {
			user.addEvent(new SoundEvent(use_sound, SoundLayer.FIGHTING_NOISE));
			user.notifyWorldAboutChanges();
		}

		return onUsedInArea(user);
	}

	/**
	 * Inheriting classes can override this to determine action to execute
	 * when item is used in correct area.
	 *
	 * @param user
	 *     Entity using the item.
	 * @param zone
	 *     Zone the entity is currently in.
	 * @param x
	 *     X coordinate of entity's position.
	 * @param y
	 *     Y coordinate of entity's position.
	 * @return
	 *     <code>true</item> if item used successfully.
	 */
	protected boolean onUsedInArea(final RPEntity user, final StendhalRPZone zone, final int x, final int y) {
		return true;
	}

	/**
	 * Inheriting classes can override this to determine action to execute
	 * when item is used in correct area.
	 *
	 * @param user
	 *     Entity using the item.
	 * @return
	 *     <code>true</item> if item used successfully.
	 */
	protected boolean onUsedInArea(final RPEntity user) {
		return onUsedInArea(user, user.getZone(), user.getX(), user.getY());
	}
}
