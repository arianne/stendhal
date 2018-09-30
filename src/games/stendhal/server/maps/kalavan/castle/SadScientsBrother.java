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
package games.stendhal.server.maps.kalavan.castle;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

public class SadScientsBrother implements ZoneConfigurator {

	private final String brotherName = "Sergej Elos";
	private final String questSlot = "sad_scientist";
	private final String gobletDescr = "You see a goblet filled with " + brotherName + "'s blood.";

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();
		final Creature creature = new ItemGuardCreature(manager.getCreature("imperial scientist"), "goblet", questSlot, gobletDescr, questSlot, "kill_scientist", 0);
		creature.setName(brotherName);
		final CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 43, 85, creature, 1);
		zone.add(point);
	}

}
