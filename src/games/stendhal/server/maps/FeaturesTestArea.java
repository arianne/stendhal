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
package games.stendhal.server.maps;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

public class FeaturesTestArea implements ZoneConfigurator {

	private final EntityManager manager;

	public FeaturesTestArea() {
		manager = SingletonRepository.getEntityManager();
	}

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createDoorAndKey(zone);
		attackableAnimal(zone);
	}

	private void createDoorAndKey(final StendhalRPZone zone) {
		final List<String> slots = new LinkedList<String>();
		slots.add("bag");

		DefaultItem item = new DefaultItem("key", "gold", "golden key", -1);
		item.setImplementation(Item.class);
		item.setWeight(1);
		item.setEquipableSlots(slots);
		manager.addItem(item);

		final Creature creature = new ItemGuardCreature(manager.getCreature("rat"), "golden key");
		final CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 40, 5, creature, 1);
		zone.add(point);
	}

	private void attackableAnimal(final StendhalRPZone zone) {
		Creature creature = new AttackableCreature(manager.getCreature("orc"));
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 4, 56, creature, 1);
		point.setRespawnTime(60 * 60 * 3);
		zone.add(point);

		creature = manager.getCreature("deer");
		point = new CreatureRespawnPoint(zone, 14, 56, creature, 1);
		point.setRespawnTime(60 * 60 * 3);
		zone.add(point);
	}
}
