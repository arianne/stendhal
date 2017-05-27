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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.RingOfLife;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.fado.weaponshop.RingSmithNPC;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;

public class RingMakerTest {

	private static final String QUEST_SLOT = "fix_emerald_ring";

	private static SpeakerNPC npc;
	private static Engine en;
	private Player player;

	@BeforeClass
	public static void setUpBeforeClass() {
		Log4J.init();
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get();
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		final RingSmithNPC ognir = new RingSmithNPC();
		ognir.configureZone(zone, null);

		final AbstractQuest quest = new RingMaker();
		quest.addToWorld();
		npc = SingletonRepository.getNPCList().get("Ognir");
		en = npc.getEngine();
	}

	@AfterClass
	public static void tearDownAftereClass() {
		MockStendlRPWorld.reset();

		 npc = null;
		 en = null;
	}

	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		en.setCurrentState(ConversationStates.IDLE);
	}

	/**
	 * Tests for hiandBye.
	 */
	@Test
	public void testHiandBye() {
		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		assertTrue(en.step(player, "bye"));
		assertEquals("Bye, my friend.", getReply(npc));

	}


	/**
	 * Tests for orderEmeraldRingWithoutEnoughmoney.
	 */
	@Test
	public void testOrderEmeraldRingWithoutEnoughmoney() {

		// **at ringsmith**
		npc = SingletonRepository.getNPCList().get("Ognir");
		en = npc.getEngine();
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", getReply(npc));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		assertTrue(en.step(player, "emerald"));
		assertEquals("It is difficult to get the ring of life. Do a favour for a powerful elf in Nal'wor and you may receive one as a reward.", getReply(npc));
		assertTrue(en.step(player, "bye"));
		// -----------------------------------------------
		final RingOfLife ring = (RingOfLife) SingletonRepository.getEntityManager().getItem("emerald ring");
		player.getSlot("bag").add(ring);
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", getReply(npc));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		assertTrue(en.step(player, "emerald"));
		assertEquals("I see you already have an emerald ring. If it gets broken, you can come to me to fix it.", getReply(npc));

		// break the ring don't give them money and make them lie that they have it
		ring.damage();
		assertTrue(en.step(player, "emerald"));
		assertEquals("What a pity, your emerald ring is broken. I can fix it, for a #price.", getReply(npc));
		assertTrue(en.step(player, "price"));
		assertEquals("The charge for my service is 80000 money, and I need 2 gold bars and 1 emerald to fix the ring. Do you want to pay now?", getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals("Come back when you have the money, the gem and the gold. Goodbye.", getReply(npc));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}

	/**
	 * Tests for orderEmeraldRingDeny.
	 */
	@Test
	public void testOrderEmeraldRingDeny() {

		// -----------------------------------------------
		// this time say no they don't want to pay yet
		npc = SingletonRepository.getNPCList().get("Ognir");
		en = npc.getEngine();
		final RingOfLife ring = (RingOfLife) SingletonRepository.getEntityManager().getItem("emerald ring");
		ring.damage();
		player.getSlot("bag").add(ring);
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", getReply(npc));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		assertTrue(en.step(player, "emerald"));
		assertEquals("What a pity, your emerald ring is broken. I can fix it, for a #price.", getReply(npc));
		assertTrue(en.step(player, "price"));
		assertEquals("The charge for my service is 80000 money, and I need 2 gold bars and 1 emerald to fix the ring. Do you want to pay now?", getReply(npc));
		assertTrue(en.step(player, "no"));
		assertEquals("No problem, just come back when you have the money, the emerald, and the gold.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, my friend.", getReply(npc));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}

	/**
	 * Tests for orderEmeraldRing.
	 */
	@Test
	public void testOrderEmeraldRing() {

		final RingOfLife ring = (RingOfLife) SingletonRepository.getEntityManager().getItem("emerald ring");
		ring.damage();
		player.equipToInventoryOnly(ring);

		PlayerTestHelper.equipWithMoney(player, 80000);
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 2);
		PlayerTestHelper.equipWithStackableItem(player, "emerald", 1);


		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", getReply(npc));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		assertTrue(en.step(player, "emerald"));
		assertEquals("What a pity, your emerald ring is broken. I can fix it, for a #price.", getReply(npc));
		assertTrue(en.step(player, "price"));
		assertEquals("The charge for my service is 80000 money, and I need 2 gold bars and 1 emerald to fix the ring. Do you want to pay now?", getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals("Okay, that's all I need to fix the ring. Come back in 10 minutes and it will be ready. Bye for now.", getReply(npc));

		assertTrue(player.getQuest(QUEST_SLOT).startsWith("forging"));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}

	/**
	 * Tests for fetchOrderedEmeraldRing.
	 */
	@Test
	public void testFetchOrderedEmeraldRing() {

		player.setQuest("fix_emerald_ring", "forging;" + Long.MAX_VALUE);

		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(player, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		en.step(player, "emerald");
		assertTrue(getReply(npc).startsWith("I haven't finished fixing your ring of life. Please check back"));
		en.step(player, "bye");

		// Jump relatively forward in time (by pushing the past events to the beginning of time

		assertTrue(player.getQuest("fix_emerald_ring").startsWith("forging;"));
		player.setQuest("fix_emerald_ring", "forging;1");

		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(player, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		final int oldXP = player.getXP();
		en.step(player, "emerald");
		assertEquals("I'm pleased to say, your ring of life is fixed! It's good as new now.", getReply(npc));
		assertEquals("player earns 500 experience points.", oldXP + 500, player.getXP());

		final Item ring = player.getFirstEquipped("emerald ring");
		assertTrue(ring.isBound());
		assertTrue(player.isBoundTo(ring));
		assertEquals("You see an emerald ring, known as ring of life. Wear it, and you risk less from death.", ring.getDescription());
		assertEquals("You see an §'emerald ring', known as the ring of life. Wear it, and you risk less from death. It is a special quest reward for player, and cannot be used by others.", ring.describe());
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));
		assertTrue(player.isQuestCompleted(QUEST_SLOT));
		en.step(player, "bye");
		assertEquals("Bye, my friend.", getReply(npc));
	}

	/**
	 * Tests for ringIsboundAfterfix.
	 */
	@Test
	public void testRingIsboundAfterfix() {
		RingMaker rm = new RingMaker();
		SpeakerNPC testNpc = new SpeakerNPC("jack");
		rm.fixRingStep(testNpc);
		Engine engine = testNpc.getEngine();
		engine.setCurrentState(ConversationStates.ATTENDING);
		Player bob = PlayerTestHelper.createPlayer("bob");
		bob.setQuest(QUEST_SLOT, "forging;0");
		assertTrue(engine.step(bob, "life"));
		assertTrue(bob.isEquipped("emerald ring"));
		assertTrue(bob.getFirstEquipped("emerald ring").isBound());
	}

	/**
	 * Tests for ringIsUnboundAfterfix.
	 */
	@Test
	public void testRingIsUnboundAfterfix() {
		RingMaker rm = new RingMaker();
		SpeakerNPC testNpc = new SpeakerNPC("jack");
		rm.fixRingStep(testNpc);
		Engine engine = testNpc.getEngine();
		engine.setCurrentState(ConversationStates.ATTENDING);
		Player bob = PlayerTestHelper.createPlayer("bob");
		bob.setQuest(QUEST_SLOT, "forgingunbound;0");
		assertTrue(engine.step(bob, "life"));
		assertTrue(bob.isEquipped("emerald ring"));
		assertFalse(bob.getFirstEquipped("emerald ring").isBound());
	}

	/**
	 * Tests for deliverBoundRinghasnoRing.
	 */
	@Test
	public void testdeliverBoundRinghasnoRing() {
		RingMaker rm = new RingMaker();
		SpeakerNPC testNpc = new SpeakerNPC("jack");
		Player hasnoRingPlayer = PlayerTestHelper.createPlayer("hasnoRingPlayer");

		rm.fixRingStep(testNpc);
		Engine engine = testNpc.getEngine();
		engine.setCurrentState(ConversationStates.ATTENDING);
			//queststate may not start with forging
		hasnoRingPlayer.setQuest(QUEST_SLOT, "doesnotstartwithforging");
		assertTrue(engine.step(hasnoRingPlayer, "life"));
		assertThat(getReply(testNpc), is("It is difficult to get the ring of life. Do a favour for a powerful elf in Nal'wor and you may receive one as a reward."));
		assertThat(engine.getCurrentState(), is(ConversationStates.ATTENDING));

		assertFalse(hasnoRingPlayer.isEquipped("emerald ring"));
	}


	/**
	 * Tests for giveBoundRingGetBoundRing.
	 */
	@Test
	public void testgiveBoundRingGetBoundRing() {
		final RingOfLife ring = (RingOfLife) SingletonRepository.getEntityManager().getItem("emerald ring");
		ring.damage();
		player.equipToInventoryOnly(ring);
		ring.setBoundTo(player.getName());
		orderfixandfetchordered(player);
		final Item ringafter = player.getFirstEquipped("emerald ring");
		assertTrue(ringafter.isBound());
		assertTrue(player.isBoundTo(ringafter));
		assertEquals("You see an emerald ring, known as ring of life. Wear it, and you risk less from death.", ringafter.getDescription());
		assertEquals("You see an §'emerald ring', known as the ring of life. Wear it, and you risk less from death. It is a special quest reward for player, and cannot be used by others.", ringafter.describe());

		assertEquals("Bye, my friend.", getReply(npc));
	}

	private void orderfixandfetchordered(final Player testplayer) {
		PlayerTestHelper.equipWithMoney(testplayer, 80000);
		PlayerTestHelper.equipWithStackableItem(testplayer, "gold bar", 2);
		PlayerTestHelper.equipWithStackableItem(testplayer, "emerald", 1);

		assertTrue(en.step(testplayer, "hi"));
		assertEquals("Hi! Can I #help you?", getReply(npc));
		assertTrue(en.step(testplayer, "help"));
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		assertTrue(en.step(testplayer, "emerald"));
		assertEquals("What a pity, your emerald ring is broken. I can fix it, for a #price.", getReply(npc));
		assertTrue(en.step(testplayer, "price"));
		assertEquals("The charge for my service is 80000 money, and I need 2 gold bars and 1 emerald to fix the ring. Do you want to pay now?", getReply(npc));
		assertTrue(en.step(testplayer, "yes"));
		assertEquals("Okay, that's all I need to fix the ring. Come back in 10 minutes and it will be ready. Bye for now.", getReply(npc));

		assertTrue(testplayer.getQuest(QUEST_SLOT).startsWith("forging"));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());



		en.step(testplayer, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(testplayer, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		en.step(testplayer, "emerald");
		assertTrue(getReply(npc).startsWith("I haven't finished fixing your ring of life. Please check back"));
		en.step(testplayer, "bye");

		// Jump relatively forward in time (by pushing the past events to the beginning of time

		assertTrue(testplayer.getQuest("fix_emerald_ring").startsWith("forging"));
		String[] tokens = testplayer.getQuest(QUEST_SLOT).split(";");
		testplayer.setQuest(QUEST_SLOT, tokens[0] + ";1");

		en.step(testplayer, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(testplayer, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		final int oldXP = testplayer.getXP();
		en.step(testplayer, "emerald");
		assertEquals("I'm pleased to say, your ring of life is fixed! It's good as new now.", getReply(npc));
		assertEquals("player earns 500 experience points.", oldXP + 500, testplayer.getXP());
		en.step(testplayer, "bye");
	}


	/**
	 * Tests for giveUnboundRingGetUnboundRing.
	 */
	@Test
	public void testgiveUnboundRingGetUnboundRing() {
		final RingOfLife ring = (RingOfLife) SingletonRepository.getEntityManager().getItem("emerald ring");
		ring.damage();
		player.equipToInventoryOnly(ring);

		assertFalse(ring.isBound());
		orderfixandfetchordered(player);
		final Item ringafter = player.getFirstEquipped("emerald ring");
		assertFalse(ringafter.isBound());

	}

	/**
	 * Tests for name.
	 */
	@Test
	public void testname() {
		try (Scanner sc = new Scanner("forging;123")) {
			sc.useDelimiter(";");
			assertFalse(sc.hasNextInt());
			assertThat(sc.next(), is("forging"));
			assertTrue(sc.hasNextInt());
			assertThat(sc.next(), is("123"));
		}
	}
}
