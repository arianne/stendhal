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
package games.stendhal.server.maps.semos.tavern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.slot.PlayerSlot;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.maps.semos.tavern.market.MarketManagerNPC;
import games.stendhal.server.maps.semos.tavern.market.TradeCenterZoneConfigurator;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test the trade center npc
 *
 * @author madmetzger
 */
public class TradeMangerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_semos_tavern_0";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public TradeMangerNPCTest() {
		setNpcNames("Harold");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new TradeCenterZoneConfigurator(), ZONE_NAME);
	}

	/**
	 * Wipe out the market so that the test player does not run in to offer
	 * limits.
	 */
	@After
	public void wipeMarket() {
		Market m = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		m.getSlot(Market.OFFERS_SLOT_NAME).clear();
		final SpeakerNPC npc = getNPC("Harold");
		if (npc instanceof MarketManagerNPC) {
			((MarketManagerNPC) npc).getOfferMap().clear();
		}
	}

	/**
	 * Check that creating offers without a price fails and offers for items not owned by the player
	 */
	@Test
	public void testCreateInvalidOffer() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		PlayerTestHelper.equipWithItem(player, "axe");
		PlayerTestHelper.equipWithStackableItem(player, "money", 42);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		assertTrue(en.step(player, "sell axe"));
		assertEquals("I did not understand you. Please say \"sell item price\".", getReply(npc));

		assertTrue(en.step(player, "sell vampire cloak 100"));
		assertEquals("Sorry, but I don't think you have any vampire cloaks.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals(
				"Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));
	}


	/**
	 * Tests for successful placement of an offer.
	 */
	@Test
	public void testSuccessfullOfferPlacement() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem playersMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(1500);
		playersMoney.setQuantity(price);
		player.equipToInventoryOnly(item);
		player.equipToInventoryOnly(playersMoney);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		assertTrue(en.step(player, "sell axe 150000"));
		assertEquals("Do you want to sell an axe for 150000 money? It would cost you 1500 money.", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("I added your offer to the trading center and took the fee of 1500.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals(
				"Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));
	}

	/**
	 * Check that creating offers for zero price cost.
	 * (Harold needs his provision; we need to charge for those to
	 * prevent cheating the trade score)
	 */
	@Test
	public void testCreateOfferForFree() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		PlayerTestHelper.equipWithItem(player, "axe");
		PlayerTestHelper.equipWithStackableItem(player, "money", 42);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		assertTrue(en.step(player, "sell axe 0"));
		assertEquals("Do you want to sell an axe for 0 money? It would cost you 1 money.", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("I added your offer to the trading center and took the fee of 1.", getReply(npc));

		assertEquals("Making a free offer should cost", 41, ((StackableItem) player.getFirstEquipped("money")).getQuantity());

		assertTrue(en.step(player, "bye"));
		assertEquals(
				"Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));
	}

	/**
	 * Tests for successful placement of an offer with more than one item.
	 */
	@Test
	public void testSellPlural() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		PlayerTestHelper.equipWithStackableItem(player, "coal", 10);
		PlayerTestHelper.equipWithMoney(player, 100);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		assertTrue(en.step(player, "sell 2 coals for 1000"));
		assertEquals("Do you want to sell 2 pieces of coal for total 1000 money? It would cost you 10 money.", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("I added your offer to the trading center and took the fee of 10.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));
	}

	/**
	 * Tests for trying to put multiple non stackable items in one offer.
	 */
	@Test
	public void testSellPluralNonStackable() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		PlayerTestHelper.equipWithItem(player, "dagger");
		PlayerTestHelper.equipWithMoney(player, 100);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		assertTrue(en.step(player, "sell 2 daggers for 1000"));
		assertEquals("Sorry, you can only put those for sale as individual items.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));
	}

	/**
	 * Test selling a container item.
	 */
	@Test
	public void testSellContainer() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		Item keyring = SingletonRepository.getEntityManager().getItem("keyring");
		StackableItem playersMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		final int expectedTradingFee = 1500;
        Integer price = Integer.valueOf(expectedTradingFee);
		playersMoney.setQuantity(price);

		//player needs belt slot to equip keyring
		if(!player.hasSlot("belt")) {
		    player.addSlot(new PlayerSlot("belt"));
		}
		assertThat(player.hasSlot("belt"), is(Boolean.TRUE));

		assertTrue("Equipping player with keyring in belt should be successfull.", player.equip("belt", keyring));
		assertTrue("Equipping player with money in bag should be successfull.", player.equipToInventoryOnly(playersMoney));
		assertTrue("Player is not equipped with money for trading fee.", player.isEquipped("money", expectedTradingFee));
		assertTrue("Player is not equipped with keyring.", player.isEquipped("keyring"));

		Item key = SingletonRepository.getEntityManager().getItem("dungeon silver key");
		keyring.getSlot("content").add(key);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		// Try first selling it when it's not empty

		assertTrue(en.step(player, "sell keyring 150000"));
		assertEquals("Please empty your keyring first.", getReply(npc));
		assertTrue(player.isEquipped("keyring"));
		assertTrue(player.isEquipped("dungeon silver key"));

		// Then after emptying it
		keyring.getSlot("content").clear();
		assertTrue(en.step(player, "sell keyring 150000"));
		assertEquals("Do you want to sell a keyring for 150000 money? It would cost you 1500 money.", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("I added your offer to the trading center and took the fee of 1500.", getReply(npc));
		assertFalse(player.isEquipped("keyring"));
		assertFalse(player.isEquipped("dungeon silver key"));

		assertTrue(en.step(player, "bye"));
		assertEquals(
				"Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));

	}


	/**
	 * Tests for successful placement of an offer of daisies.
	 */
	@Test
	public void testSellDaisies() {
		final SpeakerNPC npc = getNPC("Harold");
		final Engine en = npc.getEngine();
		player.addXP(1700);

		PlayerTestHelper.equipWithStackableItem(player, "daisies", 5);
		PlayerTestHelper.equipWithMoney(player, 100);

		assertTrue(en.step(player, "hello"));
		assertEquals("Welcome to Semos trading center. How can I #help you?", getReply(npc));

		assertTrue(en.step(player, "sell 5 daisies for 700"));
		assertEquals("Do you want to sell 5 bunches of daisies for total 700 money? It would cost you 7 money.", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("I added your offer to the trading center and took the fee of 7.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Visit me again to see available offers, make a new offer or fetch your earnings!", getReply(npc));
	}
}
