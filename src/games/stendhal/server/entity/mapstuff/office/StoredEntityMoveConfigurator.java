/* $Id$ */
/***************************************************************************
 *                 (C) Copyright 2003-2011 - Faiumoni e. V.                *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.office;

import java.util.List;
import java.util.Map;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;

/**
 * Moves a stored entity to another location.
 *
 * @author hendrik
 */
public class StoredEntityMoveConfigurator implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		final int x = MathHelper.parseInt(attributes.get("x"));
		final int y = MathHelper.parseInt(attributes.get("y"));
		final int oldx = MathHelper.parseInt(attributes.get("oldx"));
		final int oldy = MathHelper.parseInt(attributes.get("oldy"));

		final List<Entity> list = zone.getEntitiesAt(oldx, oldy);
		for (Entity entity : list) {
			if (entity.isStorable()) {
				entity.setPosition(x, y);
			}
		}
	}

}
