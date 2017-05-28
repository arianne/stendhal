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
package games.stendhal.server.entity.creature;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;
import utilities.RPClass.CreatureTestHelper;


public class ItemGuardCreatureTest {

	@Before
	public void setUp() {
		MockStendlRPWorld.get();
		PlayerTestHelper.generateNPCRPClasses();
		PlayerTestHelper.generatePlayerRPClasses();
		CreatureTestHelper.generateRPClasses();
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
	}

	@After
	public void tearDown() {
		MockStendlRPWorld.reset();
	}

	@Test
	public void testOnDead() {
		StendhalRPZone zone = new StendhalRPZone("test zone");
		Creature copy = SingletonRepository.getEntityManager().getCreature("rat");
		ItemGuardCreature creature = new ItemGuardCreature(copy, "knife", "test_quest", "start", 0);
		zone.add(creature);
		Player player = PlayerTestHelper.createPlayer("bob");
		creature.onDead(player);
		assertTrue(player.getFirstEquipped("knife") == null);
		player.setQuest("test_quest","notStarted");
		creature.onDead(player);
		assertTrue(player.getFirstEquipped("knife") == null);
		player.setQuest("test_quest","start");
		creature.onDead(player);
		assertTrue(player.getFirstEquipped("knife") != null);
	}

}
