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
package games.stendhal.server.maps.ados.tavern;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

/**
 * Creates bottles in Ados tavern
 */
public class ItemsOnTable implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildTavernArea(zone);
	}

	private void buildTavernArea(final StendhalRPZone zone) {

		// grower for an empty eared bottle (1h)
		final PassiveEntityRespawnPoint bottleGrower1 = new PassiveEntityRespawnPoint("eared bottle", 12000);
		bottleGrower1.setPosition(9, 10);
		bottleGrower1.setDescription("This seems a likely spot where a bottle could be found.");
		zone.add(bottleGrower1);

		bottleGrower1.setToFullGrowth();

		// grower for another empty eared bottle (1h)
		final PassiveEntityRespawnPoint bottleGrower2 = new PassiveEntityRespawnPoint("eared bottle", 12000);
		bottleGrower2.setPosition(28, 3);
		bottleGrower2.setDescription("This seems a likely spot where a bottle could be found.");
		zone.add(bottleGrower2);

		bottleGrower2.setToFullGrowth();
	}
}
