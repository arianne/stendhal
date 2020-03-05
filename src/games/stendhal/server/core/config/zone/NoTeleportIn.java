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
package games.stendhal.server.core.config.zone;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

public class NoTeleportIn implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		if ((attributes != null) && attributes.containsKey("x")) {
			int x = Integer.parseInt(attributes.get("x"));
			int y = Integer.parseInt(attributes.get("y"));
			int width = Integer.parseInt(attributes.get("width"));
			int height = Integer.parseInt(attributes.get("height"));

			zone.disallowIn(x, y, width, height);
		} else {
			zone.disallowIn();
		}
	}

	public void configureZone(final StendhalRPZone zone, final Rectangle area) {
		final Map<String, String> attributes = new HashMap<>();
		attributes.put("x", Integer.toString(area.x));
		attributes.put("y", Integer.toString(area.y));
		attributes.put("width", Integer.toString(area.width));
		attributes.put("height", Integer.toString(area.height));

		configureZone(zone, attributes);
	}
}
