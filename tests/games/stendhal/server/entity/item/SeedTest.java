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
package games.stendhal.server.entity.item;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.area.Allotment;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
import utilities.RPClass.GrowingPassiveEntityRespawnPointTestHelper;

public class SeedTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		GrowingPassiveEntityRespawnPointTestHelper.generateRPClasses();
	}



	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {
		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		SingletonRepository.getRPWorld().addRPZone(zone);
		final Allotment all = new Allotment();
		all.setPosition(1, 0);
		all.setSize(20, 20);
		zone.add(all);
		zone.add(player);

		assertNotNull(seed);
		zone.add(seed);
		seed.setPosition(1, 0);

		assertTrue(seed.onUsed(player));
		FlowerGrower grower = null;
		for (final Entity ent: player.getZone().getEntitiesAt(1, 0)) {
			if (ent instanceof FlowerGrower) {
				grower = (FlowerGrower) ent;
				break;
			}
		}
		assertNotNull(grower);
	}


	/**
	 * Tests for executeSeedInBag.
	 */
	@Test
	public void testExecuteSeedInBag() {
		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		SingletonRepository.getRPWorld().addRPZone(zone);
		final Allotment all = new Allotment();
		all.setPosition(0, 0);
		all.setSize(20, 20);
		zone.add(all);
		zone.add(player);

		assertNotNull(seed);
		player.equip("bag", seed);

		assertFalse(seed.onUsed(player));
	}

	/**
	 * Tests for executeNonameSeed.
	 */
	@Test
	public void testExecuteNonameSeed() {
		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		SingletonRepository.getRPWorld().addRPZone(zone);
		final Allotment all = new Allotment();
		all.setPosition(0, 0);
		all.setSize(20, 20);
		zone.add(all);
		zone.add(player);

		assertNotNull(seed);
		zone.add(seed);
		seed.setPosition(1, 0);

		assertTrue(seed.onUsed(player));

		FlowerGrower flg = null;
		for (final Entity ent: player.getZone().getEntitiesAt(1, 0)) {
			if (ent instanceof FlowerGrower) {
				flg = (FlowerGrower) ent;
				break;
			}
		}
		assertNotNull(flg);
		flg.setToFullGrowth();
		flg.onUsed(player);
		assertFalse(player.getZone().getEntitiesAt(1, 0).contains(flg));
		assertTrue(player.isEquipped("lilia"));
	}

	/**
	 * Tests for executeDaisiesSeed.
	 */
	@Test
	public void testExecuteDaisiesSeed() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		SingletonRepository.getRPWorld().addRPZone(zone);
		final Allotment all = new Allotment();
		all.setPosition(0, 0);
		all.setSize(20, 20);
		zone.add(all);
		zone.add(player);

		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		assertNotNull(seed);
		seed.setInfoString("daisies");
		zone.add(seed);
		seed.setPosition(1, 0);

		assertTrue(seed.onUsed(player));

		FlowerGrower flg = null;
		for (final Entity ent: player.getZone().getEntitiesAt(1, 0)) {
			if (ent instanceof FlowerGrower) {
				flg = (FlowerGrower) ent;
				break;
			}
		}
		assertNotNull(flg);
		flg.setToFullGrowth();
		flg.onUsed(player);
		assertFalse(player.getZone().getEntitiesAt(1, 0).contains(flg));
		assertTrue("player has daisies", player.isEquipped("daisies"));
	}
}
