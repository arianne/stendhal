/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
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

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.area.ToolUseArea;
import games.stendhal.server.entity.player.Player;


public class AreaUseItem extends Item {

	public AreaUseItem(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item Item to copy.
	 */
	public AreaUseItem(final AreaUseItem item) {
		super(item);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (user instanceof Player) {
			final Player player = (Player) user;
			final StendhalRPZone zone = player.getZone();

			boolean used = false;
			for (final Entity areaEntity : zone.getEntitiesAt(player.getX(), player.getY())) {
				if (areaEntity instanceof ToolUseArea) {
					used = ((ToolUseArea) areaEntity).use(player, item_name);
				}
			}

			// FIXME: how to deal with multiple enties being used?
			return used;
		}

		return false;
	}
}
