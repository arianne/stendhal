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
package games.stendhal.tools;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import games.stendhal.server.core.config.CreatureGroupsXMLLoader;
import games.stendhal.server.core.config.ZoneGroupsXMLLoader;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import marauroa.common.game.IRPZone;

public class GenerateMonsterUsage {
	public static void main(final String[] args) throws URISyntaxException,
			SAXException, IOException {
		final ZoneGroupsXMLLoader loader = new ZoneGroupsXMLLoader(new URI(
				"/data/conf/zones.xml"));
		loader.load();

		final Map<String, Integer> count = new HashMap<String, Integer>();

		final CreatureGroupsXMLLoader creatureLoader = new CreatureGroupsXMLLoader("/data/conf/creatures.xml");
		final List<DefaultCreature> creatures = creatureLoader.load();

		for (final DefaultCreature c : creatures) {
			count.put(c.getCreatureName(), 0);
		}

		for (final IRPZone zone : SingletonRepository.getRPWorld()) {
			for (final CreatureRespawnPoint p : ((StendhalRPZone) zone).getRespawnPointList()) {
				final Creature c = p.getPrototypeCreature();
				int creatureCount = 1;
				if (count.containsKey(c.getName())) {
					creatureCount = count.get(c.getName()) + 1;
				}

				count.put(c.getName(), creatureCount);
			}
		}

		Integer total = Integer.valueOf(0);
		for (final Map.Entry<String, Integer> e : count.entrySet()) {
				System.out.println(e.getKey() + ";" + e.getValue());
				total += e.getValue();
		}
		System.out.println("total amount of respawners: " + total);
	}
}
