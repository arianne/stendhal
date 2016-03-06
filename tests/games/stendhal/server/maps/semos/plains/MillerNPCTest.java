/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.plains;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test for MillerNPC: mill grain to flour.
 *
 * @author Martin Fuchs
 */
public class MillerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_semos_plains_ne";

	private static final String QUEST_SLOT = "jenny_mill_flour";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public MillerNPCTest() {
		setNpcNames("Jenny");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new MillerNPC(), ZONE_NAME);
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Jenny");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Jenny"));
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		final SpeakerNPC npc = getNPC("Jenny");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I run this windmill, where I can #mill people's #grain into flour for them. I also supply the bakery in Semos.", getReply(npc));

		assertTrue(en.step(player, "grain"));
		assertEquals("There's a farm nearby; they usually let people harvest there. You'll need a scythe, of course.", getReply(npc));

		assertTrue(en.step(player, "help"));
		assertEquals("Do you know the bakery in Semos? I'm proud to say they use my flour. But the wolves ate my delivery boy again recently... they're probably running out.", getReply(npc));

		assertTrue(en.step(player, "mill"));
		assertEquals("I can only mill a sack of flour if you bring me 5 #'sheaves of grain'.", getReply(npc));

		assertTrue(en.step(player, "mill flour"));
		assertEquals("I can only mill a sack of flour if you bring me 5 #'sheaves of grain'.", getReply(npc));

		assertTrue(en.step(player, "mill two sacks of flour"));
		assertEquals("I can only mill 2 sacks of flour if you bring me 10 #'sheaves of grain'.", getReply(npc));

		assertTrue(en.step(player, "mill grain"));
		assertEquals("Sorry, I don't produce sheaves of grain.", getReply(npc));

		assertTrue(equipWithStackableItem(player, "grain", 10));

		assertTrue(en.step(player, "mill two sacks of flour"));
		assertEquals("I need you to fetch me 10 #'sheaves of grain' for this job, which will take 4 minutes. Do you have what I need?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("OK, I will mill 2 sacks of flour for you, but that will take some time. Please come back in 4 minutes.", getReply(npc));

		assertFalse(player.isEquipped("flour"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));

		// wait one minute
		setPastTime(player, QUEST_SLOT, 2, 1*60);

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome back! I'm still busy with your order to mill 2 sacks of flour for you. Come back in 3 minutes to get it.", getReply(npc));

		assertFalse(player.isEquipped("flour"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));

		// wait four minutes
		setPastTime(player, QUEST_SLOT, 2, 4*60);

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome back! I'm done with your order. Here you have 2 sacks of flour.", getReply(npc));

		assertTrue(player.isEquipped("flour", 2));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests buying pansy seed from Jenny.
	 */
	@Test
	public void testSellingPansySeed() {
		final SpeakerNPC npc = getNPC("Jenny");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));

		equipWithMoney(player, 30);
		assertTrue(en.step(player, "buy 3 pansy seed"));
		assertEquals("3 pansy seeds will cost 30. Do you want to buy them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here are your pansy seeds!", getReply(npc));
		assertTrue(player.isEquipped("seed", 3));
	}

	/**
	 * Tests buying daisies seed from Jenny.
	 */
	@Test
	public void testSellingDaisiesSeed() {
		final SpeakerNPC npc = getNPC("Jenny");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));

		equipWithMoney(player, 20);
		assertTrue(en.step(player, "buy daisies seed"));
		assertEquals("A daisies seed will cost 20. Do you want to buy it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here is your daisies seed!", getReply(npc));
		assertTrue(player.isEquipped("seed"));
		Item seed = player.getFirstEquipped("seed");
		assertEquals("daisies", seed.getInfoString());

		equipWithMoney(player, 100);
		assertTrue(en.step(player, "buy five daisies seeds"));
		assertEquals("5 daisies seeds will cost 100. Do you want to buy them?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("Congratulations! Here are your daisies seeds!", getReply(npc));
	}
}
