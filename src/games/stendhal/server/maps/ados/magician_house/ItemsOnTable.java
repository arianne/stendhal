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
package games.stendhal.server.maps.ados.magician_house;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

import java.util.Map;

/**
 * Creates the items on the table in the magician house.
 *
 * @author hendrik
 */
public class ItemsOnTable implements ZoneConfigurator {
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMagicianHouseArea(zone);
	}

	private void buildMagicianHouseArea(final StendhalRPZone zone) {
		final Item item = addPersistentItem("summon scroll", zone, 7, 6);
		item.setInfoString("giant_red_dragon");

		// Plant grower for poison
		final PassiveEntityRespawnPoint plantGrower = new PassiveEntityRespawnPoint("poison", 1500);
		plantGrower.setPosition(3, 6);
		plantGrower.setDescription("Haizen tends to put his magic drinks here.");
		zone.add(plantGrower);

		plantGrower.setToFullGrowth();

		// grower for an empty slim bottle (see Koboldish Torcibud quest)
		final PassiveEntityRespawnPoint bottleGrower = new PassiveEntityRespawnPoint("slim bottle", 1500);
		bottleGrower.setPosition(10, 5);
		bottleGrower.setDescription("A spot where a bottle could be standing.");
		zone.add(bottleGrower);

		bottleGrower.setToFullGrowth();

	}

	private Item addPersistentItem(final String name, final StendhalRPZone zone, final int x, final int y) {
		final Item item = SingletonRepository.getEntityManager().getItem(name);
		item.setPosition(x, y);
		zone.add(item, false);
		
		return item;
	}
}
