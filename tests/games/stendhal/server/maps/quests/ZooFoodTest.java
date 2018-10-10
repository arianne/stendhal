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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.outside.AnimalKeeperNPC;
import games.stendhal.server.maps.ados.outside.VeterinarianNPC;
import marauroa.common.game.RPObject.ID;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class ZooFoodTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public ZooFoodTest() {
		setNpcNames("Katinka", "Dr. Feelgood");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new AnimalKeeperNPC(), ZONE_NAME);
		addZoneConfigurator(new VeterinarianNPC(), ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		final ZooFood zf = new ZooFood();
		zf.addToWorld();
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final Player player = createPlayer("player");

		SpeakerNPC npc = SingletonRepository.getNPCList().get("Katinka");
		assertNotNull(npc);
		final Engine en1 = npc.getEngine();
		assertTrue("test text recognition with additional text after 'hi'",
				en1.step(player, "hi Katinka"));
		assertTrue(npc.isTalking());
		assertEquals(
				"Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?",
				getReply(npc));
		assertTrue("test text recognition with additional text after 'bye'",
				en1.step(player, "bye bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye!", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Dr. Feelgood");
		assertNotNull(npc);
		final Engine en = npc.getEngine();
		assertTrue(en.step(player, "hi"));
		assertFalse(npc.isTalking());
		assertEquals(
				"Sorry, can't stop to chat. The animals are all sick because they don't have enough food. See yourself out, won't you?",
				getReply(npc));
		assertFalse(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals(null, getReply(npc));
	}

	/**
	 * Tests for doQuest.
	 */
	@Test
	public void testDoQuest() {
		final Player player = createPlayer("player");

		final SpeakerNPC katinkaNpc = SingletonRepository.getNPCList().get("Katinka");
		assertNotNull(katinkaNpc);
		final Engine enKatinka = katinkaNpc.getEngine();
		final SpeakerNPC feelgoodNpc = SingletonRepository.getNPCList().get("Dr. Feelgood");
		assertNotNull(feelgoodNpc);
		final Engine enFeelgood = feelgoodNpc.getEngine();
		assertTrue("test saying 'Hallo' instead of 'hi'", enKatinka.step(
				player, "Hallo"));
		assertEquals(
				"Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?",
				getReply(katinkaNpc));

		assertTrue(enKatinka.step(player, "task"));
		assertEquals(
				"Our animals are hungry. We need more food to feed them. Can you help us?",
				getReply(katinkaNpc));

		assertTrue(enKatinka.step(player, "yes"));
		assertTrue(player.hasQuest("zoo_food"));
		assertTrue(getReply(katinkaNpc).startsWith("Oh, thank you! Please help us by bringing"));
		// player asks for quest again while quest is active
		assertTrue(enKatinka.step(player, "task"));
		assertTrue(getReply(katinkaNpc).startsWith("You are already on a quest to fetch"));

		assertTrue(enKatinka.step(player, "bye"));
		assertEquals("Goodbye!", getReply(katinkaNpc));
		assertTrue(player.hasQuest("zoo_food"));
		assertTrue(player.getQuest("zoo_food").startsWith("start;"));
		// feelgood is still in sorrow
		assertTrue(enFeelgood.step(player, "hi"));
		assertFalse(feelgoodNpc.isTalking());
		assertEquals(
				"Sorry, can't stop to chat. The animals are all sick because they don't have enough food. See yourself out, won't you?",
				getReply(feelgoodNpc));
		assertFalse(enFeelgood.step(player, "bye"));
		assertFalse(feelgoodNpc.isTalking());
		assertEquals(null, getReply(feelgoodNpc));

		// test compatibility with old
		player.setQuest("zoo_food","start");
		// bother katinka again
		assertTrue(enKatinka.step(player, "hi"));
		assertEquals("Welcome back! Have you brought the 10 pieces of ham?",
				getReply(katinkaNpc));
		assertTrue("lie", enKatinka.step(player, "yes"));
		assertEquals(
				"*sigh* I SPECIFICALLY said that we need 10 pieces of ham!",
				getReply(katinkaNpc));
		assertTrue(enKatinka.step(player, "bye"));
		assertEquals("Goodbye!", getReply(katinkaNpc));
		// equip player with to less needed stuff
		final StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setQuantity(5);
		ham.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(5, player.getNumberOfEquipped("ham"));

		// bother katinka again
		assertTrue(enKatinka.step(player, "hi"));
		assertEquals("Welcome back! Have you brought the 10 pieces of ham?",
				getReply(katinkaNpc));
		assertTrue("lie", enKatinka.step(player, "yes"));
		assertEquals(
				"*sigh* I SPECIFICALLY said that we need 10 pieces of ham!",
				getReply(katinkaNpc));
		assertTrue(enKatinka.step(player, "bye"));
		assertEquals("Goodbye!", getReply(katinkaNpc));
		// equip player with to needed stuff
		final StackableItem ham2 = new StackableItem("ham", "", "", null);
		ham2.setQuantity(5);
		ham2.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham2);
		assertEquals(10, player.getNumberOfEquipped("ham"));
		// bring stuff to katinka
		assertTrue(enKatinka.step(player, "hi"));
		assertEquals("Welcome back! Have you brought the 10 pieces of ham?",
				getReply(katinkaNpc));
		assertTrue(enKatinka.step(player, "yes"));
		assertEquals("Thank you! You have rescued our rare animals.",
				getReply(katinkaNpc));
		assertTrue(enKatinka.step(player, "bye"));
		assertEquals("Goodbye!", getReply(katinkaNpc));

		assertTrue(player.getQuest("zoo_food").startsWith("done;"));

		// feelgood is reacting
		assertTrue(enFeelgood.step(player, "hi"));
		assertTrue(feelgoodNpc.isTalking());
		assertEquals(
				"Hello! Now that the animals have enough food, they don't get sick that easily, and I have time for other things. How can I help you?",
				getReply(feelgoodNpc));
		assertTrue(enFeelgood.step(player, "offers"));

		assertEquals(
				"I sell antidote, minor potion, potion, and greater potion.",
				getReply(feelgoodNpc));
		assertTrue(enFeelgood.step(player, "bye"));
		assertEquals("Bye!", getReply(feelgoodNpc));
	}
}
