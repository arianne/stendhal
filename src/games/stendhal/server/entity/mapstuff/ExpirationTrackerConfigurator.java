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
package games.stendhal.server.entity.mapstuff;

import java.util.List;
import java.util.Map;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;

/**
 * A configurator for <code>ExpirationTracker</code> objects.
 */
public class ExpirationTrackerConfigurator implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		final int x = MathHelper.parseInt(attributes.get("x"));
		final int y = MathHelper.parseInt(attributes.get("y"));

		if (!trackerAt(zone, x, y)) {
			final ExpirationTracker tracker = new ExpirationTracker();

			if (attributes.containsKey("identifier")) {
				tracker.setIdentifier(attributes.get("identifier"));
			}

			tracker.setPosition(x, y);
			zone.add(tracker);
		}
	}

	/**
	 * Checks to see if an ExpirationTracker is already at a given coordinate to
	 * prevent multiple one from accumulating in the database
	 *
	 * @param zone The zone to check for trackers
	 * @param x the x coordinate to check
	 * @param y the y coordinate to check
	 *
	 * @return true if there is a tracker at the coordinates, false otherwise
	 */
	private boolean trackerAt(StendhalRPZone zone, int x, int y) {
		final List<Entity> list = zone.getEntitiesAt(x, y);
		for (Entity entity : list) {
			if (entity instanceof ExpirationTracker) {
				return true;
			}
		}
		return false;
	}
}
