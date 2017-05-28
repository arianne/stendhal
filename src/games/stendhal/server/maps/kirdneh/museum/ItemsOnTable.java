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
package games.stendhal.server.maps.kirdneh.museum;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

/**
 * Creates the items on the table in the museum.
 *
 * @author kymara
 */
public class ItemsOnTable implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildBasementArea(zone);
	}

	private void buildBasementArea(final StendhalRPZone zone) {

		final PassiveEntityRespawnPoint plantGrower = new PassiveEntityRespawnPoint("emerald", 4000);
		plantGrower.setPosition(26, 38);
		plantGrower.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower);

		plantGrower.setToFullGrowth();

		final PassiveEntityRespawnPoint plantGrower2 = new PassiveEntityRespawnPoint("sapphire", 4000);
		plantGrower2.setPosition(26, 39);
		plantGrower2.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower2);

		plantGrower2.setToFullGrowth();

		final PassiveEntityRespawnPoint plantGrower3 = new PassiveEntityRespawnPoint("carbuncle", 4000);
		plantGrower3.setPosition(26, 40);
		plantGrower3.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower3);

		plantGrower3.setToFullGrowth();

		final PassiveEntityRespawnPoint plantGrower4 = new PassiveEntityRespawnPoint("obsidian", 4000);
		plantGrower4.setPosition(26, 41);
		plantGrower4.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower4);

		plantGrower4.setToFullGrowth();

		final PassiveEntityRespawnPoint plantGrower5 = new PassiveEntityRespawnPoint("diamond", 4000);
		plantGrower5.setPosition(26, 42);
		plantGrower5.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower5);

		plantGrower5.setToFullGrowth();
	}

}
