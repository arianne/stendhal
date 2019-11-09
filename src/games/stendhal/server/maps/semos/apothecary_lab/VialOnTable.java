/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.apothecary_lab;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

/**
 * Creates vial on table in apothecary's lab.
 */
public class VialOnTable implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildVialGrower(zone);
	}

	private void buildVialGrower(final StendhalRPZone zone) {
		final PassiveEntityRespawnPoint vialGrower = new PassiveEntityRespawnPoint("vial", 1000);
		vialGrower.setPosition(18, 13);
		vialGrower.setDescription("It looks like there was a small item sitting on this table at some point.");
		zone.add(vialGrower);
		vialGrower.setToFullGrowth();
	}
}
