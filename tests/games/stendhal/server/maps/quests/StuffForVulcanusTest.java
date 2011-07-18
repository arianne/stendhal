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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.kotoch.SmithNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class StuffForVulcanusTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final ZoneConfigurator zoneConf = new SmithNPC();
		zoneConf.configureZone(new StendhalRPZone("admin_test"), null);
		npc = SingletonRepository.getNPCList().get("Vulcanus");

		final AbstractQuest quest = new StuffForVulcanus();
		quest.addToWorld();
		en = npc.getEngine();

		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for hiAndByeQuest.
	 */
	@Test
	public void testHiAndByeQuest() {
		assertTrue(en.step(player, "hi"));
		assertEquals("Chairetismata! I am Vulcanus the smither.", getReply(npc));
		assertTrue(en.step(player, "help"));
		assertEquals("I may help you to get a very #special item for only a few others...", getReply(npc));
		assertTrue(en.step(player, "special"));
		assertEquals("Who told you that!?! *cough* Anyway, yes, I can forge a very special item for you. But you will need to complete a #quest", getReply(npc));
		assertFalse(en.step(player, "yes"));
		assertTrue(en.step(player, "quest"));
		assertEquals("I once forged the most powerful of swords. I can do it again for you. Are you interested?", getReply(npc));
		assertTrue(en.step(player, "no"));
		assertEquals("Oh, well forget it then, if you don't want an immortal sword...", getReply(npc));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("rejected", player.getQuest("immortalsword_quest"));
		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Chairetismata! I am Vulcanus the smither.", getReply(npc));
		en.step(player, "task");
		assertEquals("I once forged the most powerful of swords. I can do it again for you. Are you interested?", getReply(npc));
		en.step(player, "yes");
		assertEquals("I will need several things: 15 iron, 26 wood logs, 12 gold bars and 6 giant hearts. Come back when you have them in the same #exact order!", getReply(npc));
		en.step(player, "exact");
		assertEquals("This archaic magic requires that the ingredients are added on an exact order.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell", getReply(npc));

		// -----------------------------------------------

		// superkym gets 10 iron
		Item item = ItemTestHelper.createItem("iron", 10);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("I cannot #forge it without the missing 5 pieces of iron.", getReply(npc));
		en.step(player, "forge");
		assertEquals("I will need 5 #iron, 26 #wood logs, 12 #gold bars and 6 #giant hearts.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell", getReply(npc));

		// -----------------------------------------------
		// superkym gets 12 gold bar but he wants iron first
		item = ItemTestHelper.createItem("gold bar", 12);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("I cannot #forge it without the missing 5 pieces of iron.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell", getReply(npc));

		// -----------------------------------------------
		// superkym gets 10 iron, still has that gold in bag too
		item = ItemTestHelper.createItem("iron", 10);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("How do you expect me to #forge it without missing 26 wood logs for the fire?", getReply(npc));
		en.step(player, "forge");
		assertEquals("I will need 0 #iron, 26 #wood logs, 12 #gold bars and 6 #giant hearts.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell", getReply(npc));

		// -----------------------------------------------
		// superkym gets the 26 wood and he takes the 12 gold bar at same time too
		item = ItemTestHelper.createItem("wood", 26);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("It is the base element of the enchantment. I need 6 giant hearts still.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell", getReply(npc));

		// -----------------------------------------------

		// superkym gets 2 hearts
		item = ItemTestHelper.createItem("giant heart", 2);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("It is the base element of the enchantment. I need 4 giant hearts still.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell", getReply(npc));

		// -----------------------------------------------
		// superkym gets remaining 4 hearts
		item = ItemTestHelper.createItem("giant heart", 4);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Did you really get those giant hearts yourself? I don't think so! This powerful sword can only be given to those that are strong enough to kill a #giant.", getReply(npc));
		en.step(player, "giant");
		assertEquals("There are ancient stories of giants living in the mountains at the north of Semos and Ados.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell", getReply(npc));

		// -----------------------------------------------
		// superkym summons a giant and kills it, it nearly kills her first!
		// [23:14] superkym earns 14400 experience points. 
		// [23:14] superkym reaches Level 20 
		// [23:15] You see the new corpse of a giant human. You can inspect it to see its contents
		player.setSharedKill("giant");

		en.step(player, "hi");
		assertEquals("You've brought everything I need to make the immortal sword, and what is more, you are strong enough to handle it. Come back in 10 minutes and it will be ready.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell", getReply(npc));

		// -----------------------------------------------
		
		
		en.step(player, "hi");
		assertEquals("I haven't finished forging the sword. Please check back in 10 minutes.", getReply(npc));
		en.step(player, "bye");
		
		// -----------------------------------------------
		
		player.setQuest("immortalsword_quest", "forging;0");
		en.step(player, "hi");
		assertEquals("I have finished forging the mighty immortal sword. You deserve this. Now I'm going to have a long rest, so, goodbye!", getReply(npc));
		// [23:26] superkym earns 15000 experience points.
		en.step(player, "task");

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Chairetismata! I am Vulcanus the smither.", getReply(npc));
		en.step(player, "task");
		assertEquals("Oh! I am so tired. Look for me later. I need a few years of relaxing.", getReply(npc));
		
	}
}
