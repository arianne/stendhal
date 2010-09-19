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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;

import java.util.Map;

import marauroa.common.game.RPObject;

public class FoodMill extends Item implements UseListener {

	public FoodMill(final String name, final String clazz,
			final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public FoodMill(final FoodMill item) {
		super(item);
	}

	public boolean onUsed(final RPEntity user) {
		if (isContained()) {
			final String slotName = getContainerSlot().getName();
			if (slotName.endsWith("hand")) {
				String otherhand;
				if ("rhand".equals(slotName)) {
					otherhand = "lhand";
				} else {
					otherhand = "rhand";
				}
				final RPObject first = user.getSlot(otherhand).getFirst();
				if (first != null) {
					if ("apple".equals(first.get("name"))) {
						if (user.isEquipped("flask")) {

							final Item item = SingletonRepository
									.getEntityManager().getItem("apple juice");

						
							user.drop("apple");
							user.drop("flask");
							user.equipOrPutOnGround(item);
						}
					}
				}

			}
			return true;
		} else {
			return false;
		}
	}

}
