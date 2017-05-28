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
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.amazon.hut.PrincessNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class AmazonPrincessTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final ZoneConfigurator zoneConf = new PrincessNPC();
		zoneConf.configureZone(new StendhalRPZone("admin_test"), null);
		npc = SingletonRepository.getNPCList().get("Princess Esclara");
		en = npc.getEngine();

		final AbstractQuest quest = new AmazonPrincess();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("player");
	}
	@After
	public void tearDown() {
		SingletonRepository.getNPCList().clear();
	}

	/**
	 * Tests for getSlotname.
	 */
	@Test
	public void testGetSlotname() {
		assertEquals("amazon_princess", new AmazonPrincess().getSlotName());
	}

	/**
	 * Tests for hasRecovered.
	 */
	@Test
	public void testhasRecovered() {
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setQuest(new AmazonPrincess().getSlotName(), "drinking;0");
		en.step(player, "task");
		assertEquals("The last cocktail you brought me was so lovely. Will you bring me another?", getReply(npc));
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		en.step(player, "yes");
		assertEquals("start", player.getQuest(new AmazonPrincess().getSlotName()));

	}


	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", getReply(npc));
		en.step(player, "help");
		assertEquals("Beware of my sisters on the island, they do not like strangers.", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm looking for a drink, should be an exotic one. Can you bring me one?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you! If you have found some, say #drink to me so I know you have it. I'll be sure to give you a nice reward.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", getReply(npc));
		en.step(player, "task");
		assertEquals("I like these exotic drinks, I forget the name of my favourite one.", getReply(npc));
		en.step(player, "help");
		assertEquals("Beware of my sisters on the island, they do not like strangers.", getReply(npc));
		en.step(player, "pinacolada");
		assertEquals("You don't have any drink I like yet. Go, and you better get an exotic one!", getReply(npc));
		en.step(player, "exotic drink");
		en.step(player, "pinacolad");
		assertEquals("You don't have any drink I like yet. Go, and you better get an exotic one!", getReply(npc));
		en.step(player, "help");
		assertEquals("Beware of my sisters on the island, they do not like strangers.", getReply(npc));
		en.step(player, "favor");
		assertEquals("I like these exotic drinks, I forget the name of my favourite one.", getReply(npc));
		en.step(player, "pinacolada");
		assertEquals("You don't have any drink I like yet. Go, and you better get an exotic one!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", getReply(npc));
		en.step(player, "help");
		assertEquals("Beware of my sisters on the island, they do not like strangers.", getReply(npc));
		en.step(player, "quest");
		assertEquals("I like these exotic drinks, I forget the name of my favourite one.", getReply(npc));
		en.step(player, "done");
		en.step(player, "drink");
		assertEquals("You don't have any drink I like yet. Go, and you better get an exotic one!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", getReply(npc));

		// -----------------------------------------------
		final Item item = ItemTestHelper.createItem("pina colada");
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", getReply(npc));
		en.step(player, "pina colada");
		assertTrue(getReply(npc).startsWith("Thank you!! Take th"));
		assertTrue(player.isEquipped("fish pie"));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", getReply(npc));
		en.step(player, "task");
		assertTrue(getReply(npc).startsWith("I'm sure I'll be too drunk to have another for at least"));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", getReply(npc));
		en.step(player, "pina colada");
		assertEquals("Sometime you could do me a #favour ...", getReply(npc));
		en.step(player, "favour");
		assertTrue(getReply(npc).startsWith("I'm sure I'll be too drunk to have another for at least "));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", getReply(npc));
	}
}
