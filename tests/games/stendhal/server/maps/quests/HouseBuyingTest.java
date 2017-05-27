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
package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.quests.houses.HouseUtilities;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class HouseBuyingTest extends ZonePlayerAndNPCTestImpl {
	private static final String ZONE_NAME = "0_kalavan_city";
	private static final String ZONE_NAME2 = "int_ados_town_hall_3";
	private static final String ZONE_NAME3 = "int_kirdneh_townhall";

	private HousePortal housePortal;
	private StoredChest chest;

	private static final String[] CITY_ZONES = {
		"0_kalavan_city",
		"0_kirdneh_city",
		"0_ados_city_n",
		"0_ados_city",
		"0_ados_city_s",
		"0_ados_wall",
		"0_athor_island"	};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		HousePortal.generateRPClass();
		Chest.generateRPClass();

		setupZone(ZONE_NAME);
		setupZone(ZONE_NAME2);
		setupZone(ZONE_NAME3);

		for (String zone : CITY_ZONES) {
			setupZone(zone);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		HouseUtilities.clearCache();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		SingletonRepository.getNPCList().add(new SpeakerNPC("Mr Taxman"));

		new HouseBuying().addToWorld();
	}

	/**
	 * Remove added stored entities.
	 * <p>
	 * stored entities can pollute the database
	 * if a server is ran on the same system as the tests.
	 */
	@After
	public void clearStored() {
		if (housePortal != null) {
			StendhalRPZone zone = housePortal.getZone();
			if (zone != null) {
				zone.remove(housePortal);
				housePortal = null;
			}
		}

		if (chest != null) {
			StendhalRPZone zone = chest.getZone();
			if (zone != null) {
				zone.remove(chest);
				chest = null;
			}
		}

		PlayerTestHelper.removeNPC("Cyk");
		PlayerTestHelper.removeNPC("Mr Taxman");
		PlayerTestHelper.removeNPC("Roger Frampton");
	}

	public HouseBuyingTest() {
		super(ZONE_NAME, "Barrett Holmes", "Reg Denson", "Mr Taxman");
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		assertNotNull(npc);
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hello"));
		assertEquals("Hello, player.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Goodbye.", getReply(npc));
	}

	/**
	 * Tests for generalStuff.
	 */
	@Test
	public void testGeneralStuff() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hello, player.", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm an estate agent. In simple terms, I sell houses for the city of Ados. Please ask about the #cost if you are interested. Our brochure is at #https://stendhalgame.org/wiki/StendhalHouses.", getReply(npc));

		assertTrue(en.step(player, "offer"));
		assertEquals("I sell houses, please look at #https://stendhalgame.org/wiki/StendhalHouses for examples of how they look inside. Then ask about the #cost when you are ready.", getReply(npc));

		assertTrue(en.step(player, "quest"));
		assertEquals("You may buy houses from me, please ask the #cost if you are interested. Perhaps you would first like to view our brochure, #https://stendhalgame.org/wiki/StendhalHouses.", getReply(npc));
	}

	/**
	 * Tests for buyHouse.
	 */
	@Test
	public void testBuyHouse() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hello, player.", getReply(npc));

		assertTrue(en.step(player, "cost"));
		assertTrue(getReply(npc).startsWith("The cost of a new house in Ados is 120000 money. But I am afraid I cannot trust you with house ownership just yet,"));

		player.setAge(3700000);
		assertTrue(en.step(player, "cost"));
		assertEquals("The cost of a new house in Ados is 120000 money. But I am afraid I cannot sell you a house yet as you must first prove yourself a worthy #citizen.", getReply(npc));

		// satisfy the rest of the Ados conditions
		player.setQuest("daily_item", "done");
		player.setQuest("toys_collector", "done");
		player.setQuest("hungry_joshua", "done");
		player.setQuest("find_ghosts", "done");
		player.setQuest("get_fishing_rod", "done");
		player.setQuest("suntan_cream_zara", "done");
		assertTrue(en.step(player, "buy"));
		assertEquals("The cost of a new house in Ados is 120000 money. Also, you must pay a house tax of 1000 money,"
				+ " every month. If you have a house in mind, please tell me the number now. I will check availability. "
				+ "The Ados houses are numbered from 50 to 77.", getReply(npc));

		// add a portal to the maps so that there's something to check and sell
		Portal destination = new Portal();
		destination.setIdentifier("dest");
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(destination);
		chest = new StoredChest();
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(chest);

		housePortal = new HousePortal("ados house 50");
		housePortal.setIdentifier("keep rpzone happy");
		housePortal.setDestination(ZONE_NAME, "dest");
		SingletonRepository.getRPWorld().getRPZone("0_ados_city").add(housePortal);
		HouseUtilities.clearCache();

		assertTrue(en.step(player, "50"));
		assertEquals("You do not have enough money to buy a house!", getReply(npc));

		final StackableItem money = (StackableItem)SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(120000);
		player.equipToInventoryOnly(money);

		// don't answer anything
		assertFalse(en.step(player, "42"));

		assertTrue(en.step(player, "buy"));
		assertTrue(en.step(player, "50"));
		assertEquals("Congratulations, here is your key to ados house 50! Make sure you change the locks if you ever lose it."
				+ " Do you want to buy a spare key, at a price of 1000 money?", getReply(npc));

		assertTrue(player.isEquipped("player's house key"));

		Item item = player.getFirstEquipped("player's house key");
		assertNotNull(item);
		assertEquals("ados house 50;0;player", item.get("infostring"));
		assertFalse(item.isBound());

		assertTrue(en.step(player, "no"));
		assertEquals("No problem! Just so you know, if you need to #change your locks, I can do that, "
				+ "and you can also #resell your house to me if you want to.", getReply(npc));
	}

	/**
	 * Tests for really.
	 */
	@Test
	public void testReally() {
		final SpeakerNPC npc = getNPC("Reg Denson");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Reg Denson"));
		assertEquals("Hello, player.", getReply(npc));

		assertTrue(en.step(player, "really"));
		assertEquals("That's right, really, really, really. Really.", getReply(npc));

		assertTrue(en.step(player, "cost"));
		assertTrue(getReply(npc).startsWith("The cost of a new house in Ados is 120000 money. But I am afraid I cannot trust you with house ownership just yet,"));
		assertFalse(en.step(player, "ok"));
	}

}
