/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.consumption;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class PoisonerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		Log4J.init();
	}


	/**
	 * Tests for feed.
	 */
	@Test
	public final void testFeed() {
		SingletonRepository.getEntityManager();
		ItemTestHelper.generateRPClasses();
		PlayerTestHelper.generatePlayerRPClasses();
		final Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("amount", "1000");
		attributes.put("regen", "200");
		attributes.put("frequency", "1");
		attributes.put("id", "1");
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		final StendhalRPZone zone = new StendhalRPZone("test");
		world.addRPZone(zone);
		final ConsumableItem c200_1 = new ConsumableItem("cheese", "", "", attributes);
		zone.add(c200_1);
		final Poisoner poisoner = new Poisoner();
		final Player bob = PlayerTestHelper.createPlayer("player");
		poisoner.feed(c200_1, bob);
		assertTrue(bob.hasStatus(StatusType.POISONED));
	}

}
