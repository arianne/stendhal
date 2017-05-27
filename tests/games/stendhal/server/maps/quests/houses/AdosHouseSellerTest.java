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
package games.stendhal.server.maps.quests.houses;

import static games.stendhal.server.entity.npc.ConversationStates.ATTENDING;
import static games.stendhal.server.entity.npc.ConversationStates.IDLE;
import static games.stendhal.server.entity.npc.ConversationStates.QUEST_OFFERED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class AdosHouseSellerTest {

	private HousePortal housePortal;
	private StoredChest chest;
	private AdosHouseSeller seller;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generateNPCRPClasses();
		Chest.generateRPClass();
		Portal.generateRPClass();
		HousePortal.generateRPClass();
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		SingletonRepository.getNPCList().clear();
		MockStendhalRPRuleProcessor.get().clearPlayers();
		HouseUtilities.clearCache();
	}

	@Before
	public void setUp() {
		SingletonRepository.getNPCList().add(new SpeakerNPC("Mr Taxman"));
		seller = new AdosHouseSeller("bob", "nirvana", new HouseTax());
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

		PlayerTestHelper.removeNPC("Mr Taxman");
	}

	/**
	 * Tests for getCost.
	 */
	@Test
	public void testGetCost() {
		assertEquals(120000, seller.getCost());
	}

	/**
	 * Tests for getLowestHouseNumber.
	 */
	@Test
	public void testGetLowestHouseNumber() {
		assertEquals(50, seller.getLowestHouseNumber());
	}

	/**
	 * Tests for getHighestHouseNumber.
	 */
	@Test
	public void testGetHighestHouseNumber() {
		assertEquals(77, seller.getHighestHouseNumber());
		assertThat(seller.getLowestHouseNumber(), is(lessThan(seller.getHighestHouseNumber())));
	}

	/**
	 * Tests for adosHouseSellerTooYoungNoQuests.
	 */
	@Test
	public void testAdosHouseSellerTooYoungNoQuests() {
		Engine en = seller.getEngine();
		assertThat(en.getCurrentState(), is(IDLE));

		Player george = PlayerTestHelper.createPlayer("george");

		en.step(george, "hi");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertThat(getReply(seller), is("Hello, george."));

		en.step(george, "job");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertThat(getReply(seller), containsString("Ados"));

		en.step(george, "cost");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertThat("player is too young", getReply(seller), containsString("you have spent at least"));

		george.setAge(300 * MathHelper.MINUTES_IN_ONE_HOUR + 1);
		en.step(george, "cost");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertThat("player is old enough but has no quests done", getReply(seller), containsString("you must first prove yourself a worthy"));

	}

	/**
	 * Tests for adosHouseSellerNoZones.
	 */
	@Test
	public void testAdosHouseSellerNoZones() {
		HouseUtilities.clearCache();
		Engine en = seller.getEngine();
		en.setCurrentState(QUEST_OFFERED);

		Player george = PlayerTestHelper.createPlayer("george");

		en.step(george, "51");
		assertThat("no zones loaded", getReply(seller), is("Sorry I did not understand you, could you try saying the house number you want again please?"));
	}

	/**
	 * Tests for adosHouseSeller.
	 */
	@Test
	public void testAdosHouseSeller() {
		String zoneName = "0_ados_city_n";
		StendhalRPZone ados = new StendhalRPZone(zoneName);
		MockStendlRPWorld.get().addRPZone(ados);
		housePortal = new HousePortal("schnick bla 51");
		housePortal.setDestination(zoneName, "schnick bla 51");
		housePortal.setIdentifier("keep rpzone happy");
		ados.add(housePortal);
		chest = new StoredChest();
		ados.add(chest);
		HouseUtilities.clearCache();

		Engine en = seller.getEngine();
		en.setCurrentState(QUEST_OFFERED);


		Player george = PlayerTestHelper.createPlayer("george");

		en.step(george, "51");
		assertThat("no zones loaded", getReply(seller), is("You do not have enough money to buy a house!"));
		assertThat(en.getCurrentState(), is(ATTENDING));

		en.setCurrentState(QUEST_OFFERED);

		StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(120000);
		george.equipToInventoryOnly(money);
		assertFalse(george.isEquipped("house key"));
		assertTrue(george.isEquipped("money", 120000));
		en.step(george, "51");
		assertThat(getReply(seller), containsString("Congratulations"));
		assertFalse(george.isEquipped("money", 120000));
		assertTrue(george.isEquipped("george's house key"));

	}

}
