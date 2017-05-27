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
package games.stendhal.server.maps.semos.kanmararn;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

/**
 * Configure Kanmararn Prison to include a Duergar King Creature who carries a key.
 * Then it should give a key that is bound to the player.
 */
public class DuergarKingCreature implements ZoneConfigurator {

	private final EntityManager manager = SingletonRepository.getEntityManager();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildPrisonArea(zone);
	}

	private void buildPrisonArea(final StendhalRPZone zone) {
		final Creature creature = new ItemGuardCreature(manager.getCreature("duergar king"), "kanmararn prison key");
		final CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 50, 15, creature, 1);
		zone.add(point);
	}
}
