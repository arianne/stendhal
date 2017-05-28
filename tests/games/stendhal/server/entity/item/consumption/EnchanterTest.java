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
package games.stendhal.server.entity.item.consumption;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class EnchanterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	@Before
	public void setUp() throws Exception {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	@Test

	/**
	 * Tests for mana feed.
	 */
	public final void testFeed() {
		SingletonRepository.getEntityManager();
		ItemTestHelper.generateRPClasses();
		PlayerTestHelper.generatePlayerRPClasses();

		final Map<String, String> attributesAddMana = new HashMap<String, String>();
		attributesAddMana.put("amount", "60");
		attributesAddMana.put("regen", "1");
		attributesAddMana.put("frequency", "0");
		attributesAddMana.put("id", "1");

		final Map<String, String> attributesSubstractMana = new HashMap<String, String>();
		attributesSubstractMana.put("amount", "-200");
		attributesSubstractMana.put("regen", "1");
		attributesSubstractMana.put("frequency", "0");
		attributesSubstractMana.put("id", "1");

		final ConsumableItem c60_1 = new ConsumableItem("mana", "", "", attributesAddMana);
		final ConsumableItem c60_2 = new ConsumableItem("mana", "", "", attributesAddMana);
		final ConsumableItem cNeg200_1 = new ConsumableItem("mana", "", "", attributesSubstractMana);

		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		final StendhalRPZone zone = new StendhalRPZone("test");
		world.addRPZone(zone);
		zone.add(c60_1);
		zone.add(c60_2);
		zone.add(cNeg200_1);

		final Enchanter manaFeeder = new Enchanter();
		final Player bob = PlayerTestHelper.createPlayer("bob");
		bob.setBaseMana(100);
		bob.setMana(10);

		manaFeeder.feed(c60_1, bob);
		assertThat(bob.getMana(), is(70));
		manaFeeder.feed(c60_2, bob);
		assertThat(bob.getMana(), is(100));
		manaFeeder.feed(cNeg200_1, bob);
		assertThat(bob.getMana(), is(0));
	}

}
