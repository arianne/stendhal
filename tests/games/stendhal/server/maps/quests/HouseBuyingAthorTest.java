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

import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
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

public class HouseBuyingAthorTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_kalavan_city";
	private static final String ZONE_NAME2 = "int_ados_town_hall_3";
	private static final String ZONE_NAME3 = "int_kirdneh_townhall";

	private static final String[] CITY_ZONES = {
		"0_kalavan_city",
		"0_kirdneh_city",
		"0_ados_city_n",
		"0_ados_city",
		"0_ados_city_s",
		"0_ados_wall",
		"0_athor_island"	};

	private LinkedList<HousePortal> portals = new LinkedList<HousePortal>();
	private StoredChest chest;

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

		SpeakerNPC taxman = new SpeakerNPC("Mr Taxman");
		SingletonRepository.getNPCList().add(taxman);

		new HouseBuying().addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		HouseUtilities.clearCache();
	}

	/**
	 * Remove added stored entities.
	 * <p>
	 * stored entities can pollute the database
	 * if a server is ran on the same system as the tests.
	 */
	@After
	public void clearStored() {
		for (HousePortal portal : portals) {
			StendhalRPZone zone = portal.getZone();
			if (zone != null) {
				zone.remove(portal);
			}
		}
		portals.clear();

		if (chest != null) {
			StendhalRPZone zone = chest.getZone();
			if (zone != null) {
				zone.remove(chest);
				chest = null;
			}
		}
	}


	public HouseBuyingAthorTest() {
		super(ZONE_NAME, "Barrett Holmes", "Reg Denson", "Mr Taxman", "Cyk");
	}

	/**
	 * Tests for athorNPC.
	 */
	@Test
	public void testAthorNPC() {
		final SpeakerNPC npc = getNPC("Cyk");
		final Engine en = npc.getEngine();
		player.setAge(3700000);

		en.step(player, "hi");
		assertEquals("Hello, player.", getReply(npc));
		en.step(player, "cost");
		assertEquals("What do you want with an apartment on Athor when you're not even a good #fisherman? We are trying to attract owners who will spend time on the island. Come back when you have proved yourself a better fisherman.", getReply(npc));
		en.step(player, "fisherman");
		assertEquals("A fishing license from Santiago in Ados is the sign of a good fisherman, he sets two exams. Once you have passed both parts you are a good fisherman.", getReply(npc));
		// [14:06] Admin player changed your state of the quest 'fishermans_license2' from 'null' to 'done'
		// [14:06] Changed the state of quest 'fishermans_license2' from 'null' to 'done'
		player.setQuest("fishermans_license2", "done");
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));

		// -----------------------------------------------
		final StackableItem money = (StackableItem)SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(100000);
		player.equipToInventoryOnly(money);

		en.step(player, "hi");
		assertEquals("Hello, player.", getReply(npc));
		en.step(player, "buy");
		assertEquals("The cost of a new apartment is 100000 money.  Also, you must pay a monthly tax of 1000 money. If you have an apartment in mind, please tell me the number now. I will check availability. Athor Apartments are numbered 101 to 108.", getReply(npc));

//		 add a portal to the maps so that there's something to check and sell
		Portal destination = new Portal();
		destination.setIdentifier("dest");
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(destination);
		chest = new StoredChest();
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(chest);

		for (String zone : CITY_ZONES) {
			assertNotNull(zone);
			HousePortal portal = new HousePortal("athor apartment 101");
			portals.add(portal);
			portal.setDestination(ZONE_NAME, "dest");
			portal.setIdentifier("keep rpzone happy");
			SingletonRepository.getRPWorld().getRPZone(zone).add(portal);
		}

		en.step(player, "101");
		assertEquals("Congratulations, here is your key to athor apartment 101! Make sure you change the locks if you ever lose it. Do you want to buy a spare key, at a price of 1000 money?", getReply(npc));
		en.step(player, "yes");

		assertTrue(player.isEquipped("player's house key"));

		Item item = player.getFirstEquipped("player's house key");
		assertNotNull(item);
		assertEquals("athor apartment 101;0;player", item.get("infostring"));
		assertFalse(item.isBound());

		PlayerTestHelper.equipWithMoney(player, 1000);
		assertTrue(player.isEquipped("money", 1000));
		assertEquals("Before we go on, I must warn you that anyone with a key to your house can enter it, and access the items in the chest in your house. Do you still wish to buy a spare key?", getReply(npc));
		en.step(player, "yes");
		assertFalse(player.isEquipped("money", 1000));
		assertEquals("Here you go, a spare key to your house. Please remember, only give spare keys to people you #really, #really, trust! Anyone with a spare key can access your chest, and tell anyone that you give a key to, to let you know if they lose it. If that happens, you should #change your locks.", getReply(npc));
		// [14:07] You see a key to player's property, athor apartment 101.
		PlayerTestHelper.equipWithMoney(player, 1000);
		assertTrue(player.isEquipped("money", 1000));
		en.step(player, "change");
		assertEquals("If you are at all worried about the security of your house or, don't trust anyone you gave a spare key to, it is wise to change your locks. Do you want me to change your house lock and give you a new key now?", getReply(npc));
		en.step(player, "no");
		assertEquals("OK, if you're really sure. Please let me know if I can help with anything else.", getReply(npc));
		en.step(player, "change");
		assertEquals("If you are at all worried about the security of your house or, don't trust anyone you gave a spare key to, it is wise to change your locks. Do you want me to change your house lock and give you a new key now?", getReply(npc));
		en.step(player, "yes");
		assertEquals("The locks have been changed for athor apartment 101, here is your new key. Do you want to buy a spare key, at a price of 1000 money?", getReply(npc));
		en.step(player, "no");
		assertEquals("No problem! Just so you know, if you need to #change your locks, I can do that, and you can also #resell your house to me if you want to.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hello, player. At the cost of 1000 money you can purchase a spare key for your house. Do you want to buy one now?", getReply(npc));
		en.step(player, "no");
		assertEquals("No problem! Just so you know, if you need to #change your locks, I can do that, and you can also #resell your house to me if you want to.", getReply(npc));
		en.step(player, "available");
		assertTrue(getReply(npc).startsWith("According to my records, athor apartment 101, athor apartment 101, athor apartment 101, athor apartment 101, athor apartment 101"));
		en.step(player, "purchase");
		assertEquals("As you already know, the cost of a new house is 100000 money. But you cannot own more than one house, the market is too demanding for that! You cannot own another house until you #resell the one you already own.", getReply(npc));

		en.step(player, "resell");
		assertEquals("The state will pay you 40 percent of the price you paid for your house, minus any taxes you owe. You should remember to collect any belongings from your house before you sell it. Do you really want to sell your house to the state?", getReply(npc));
		en.step(player, "no");
		assertEquals("Well, I'm glad you changed your mind.", getReply(npc));
		en.step(player, "resell");
		assertEquals("The state will pay you 40 percent of the price you paid for your house, minus any taxes you owe. You should remember to collect any belongings from your house before you sell it. Do you really want to sell your house to the state?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thanks, here is your 40000 money owed, from the house value, minus any owed taxes. Now that you don't own a house you would be free to buy another if you want to.", getReply(npc));
		// one extra athor apartment 101 available :D
		en.step(player, "available");
		assertTrue(getReply(npc).startsWith("According to my records, athor apartment 101, athor apartment 101, athor apartment 101, athor apartment 101, athor apartment 101"));
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));
	}
}
