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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsIn.isOneOf;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.rock.WeaponsCollectorNPC;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import static utilities.SpeakerNPCTestHelper.getReply;

public class UltimateCollectorTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new WeaponsCollectorNPC().configureZone(zone, null);	
		

		AbstractQuest quest = new UltimateCollector();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().remove("Balduin");
	}

	@Test
	public void testQuest() {
		

		
		npc = SingletonRepository.getNPCList().get("Balduin");
		en = npc.getEngine();
		// -----------------------------------------------

		// [22:23] Admin kymara changed your state of the quest 'weapons_collector' from '' to 'done'
		// [22:23] Changed the state of quest 'weapons_collector' from '' to 'done'
		
		player.setQuest("weapons_collector", "done");
		
		// [22:23] Admin kymara changed your state of the quest 'weapons_collector2' from 'null' to 'done'
		// [22:23] Changed the state of quest 'weapons_collector2' from 'null' to 'done'
		
		player.setQuest("weapons_collector2", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertThat(getReply(npc) , isOneOf("You are missing a special mithril item which you can win if you help the right person, you cannot be an ultimate collector without it.",
				"There is still a quest in the Kotoch area which you have not completed. Explore thoroughly and you will be on your way to becoming the ultimate collector!",
				"You've collected so many special items, but you have never helped those down in Kanmararn city. You should complete a task there.",
				"A special item will be yours if you collect many cloaks, whether to fulfil another's vanity or keep them warm, it's a task you must complete.",
				"Another collector of items still needs your help. You'd find him in Fado Forest, and until you have completed that favour for him, you cannot be the ultimate collector.",
				"There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this."));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		// [22:23] Admin kymara changed your state of the quest 'mithril_cloak' from 'null' to 'done'
		// [22:23] Changed the state of quest 'mithril_cloak' from 'null' to 'done'
		
		player.setQuest("mithril_cloak", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertThat(getReply(npc) , isOneOf("You are missing a special mithril item which you can win if you help the right person, you cannot be an ultimate collector without it.",
				"There is still a quest in the Kotoch area which you have not completed. Explore thoroughly and you will be on your way to becoming the ultimate collector!",
				"You've collected so many special items, but you have never helped those down in Kanmararn city. You should complete a task there.",
				"A special item will be yours if you collect many cloaks, whether to fulfil another's vanity or keep them warm, it's a task you must complete.",
				"Another collector of items still needs your help. You'd find him in Fado Forest, and until you have completed that favour for him, you cannot be the ultimate collector.",
				"There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this."));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		// [22:26] Admin kymara changed your state of the quest 'mithrilshield_quest' from 'null' to 'done'
		// [22:26] Changed the state of quest 'mithrilshield_quest' from 'null' to 'done'
		
		player.setQuest("mithrilshield_quest", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertThat(getReply(npc) , isOneOf("There is still a quest in the Kotoch area which you have not completed. Explore thoroughly and you will be on your way to becoming the ultimate collector!",
				"You've collected so many special items, but you have never helped those down in Kanmararn city. You should complete a task there.",
				"A special item will be yours if you collect many cloaks, whether to fulfil another's vanity or keep them warm, it's a task you must complete.",
				"Another collector of items still needs your help. You'd find him in Fado Forest, and until you have completed that favour for him, you cannot be the ultimate collector.",
				"There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this."));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		
		// [22:25] Admin kymara changed your state of the quest 'immortalsword_quest' from 'null' to 'done'
		// [22:25] Changed the state of quest 'immortalsword_quest' from 'null' to 'done'
		// [22:27] Admin kymara changed your state of the quest 'club_thorns' from 'null' to 'done'
		// [22:27] Changed the state of quest 'club_thorns' from 'null' to 'done'
		player.setQuest("immortalsword_quest", "done");
		player.setQuest("club_thorns", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertThat(getReply(npc) , isOneOf("You've collected so many special items, but you have never helped those down in Kanmararn city. You should complete a task there.",
				"A special item will be yours if you collect many cloaks, whether to fulfil another's vanity or keep them warm, it's a task you must complete.",
				"Another collector of items still needs your help. You'd find him in Fado Forest, and until you have completed that favour for him, you cannot be the ultimate collector.",
				"There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this."));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		
		// [22:25] Admin kymara changed your state of the quest 'soldier_henry' from 'null' to 'done'
		// [22:25] Changed the state of quest 'soldier_henry' from 'null' to 'done'
		
		player.setQuest("soldier_henry", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertThat(getReply(npc) , isOneOf("A special item will be yours if you collect many cloaks, whether to fulfil another's vanity or keep them warm, it's a task you must complete.",
				"Another collector of items still needs your help. You'd find him in Fado Forest, and until you have completed that favour for him, you cannot be the ultimate collector.",
				"There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this."));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		// [22:25] Admin kymara changed your state of the quest 'cloakscollector2' from 'null' to 'done'
		// [22:25] Changed the state of quest 'cloakscollector2' from 'null' to 'done'
		
		// [22:26] Admin kymara changed your state of the quest 'cloaks_for_bario' from 'null' to 'done'
		// [22:26] Changed the state of quest 'cloaks_for_bario' from 'null' to 'done'
		
		player.setQuest("cloaks_collector_2", "done");
		player.setQuest("cloaks_for_bario", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertThat(getReply(npc) , isOneOf("Another collector of items still needs your help. You'd find him in Fado Forest, and until you have completed that favour for him, you cannot be the ultimate collector.",
				"There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this."));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		// [22:26] Admin kymara changed your state of the quest 'elvish_armor' from 'null' to 'done'
		// [22:26] Changed the state of quest 'elvish_armor' from 'null' to 'done'
		
		player.setQuest("elvish_armor", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertEquals(getReply(npc) , "There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this.");
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		// [22:27] Admin kymara changed your state of the quest 'obsidian_knife' from 'null' to 'done'
		// [22:27] Changed the state of quest 'obsidian_knife' from 'null' to 'done'
		
		player.setQuest("obsidian_knife", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertEquals(getReply(npc) , "There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this.");
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		// [22:27] Admin kymara changed your state of the quest 'vs_quest' from 'null' to 'done'
		// [22:27] Changed the state of quest 'vs_quest' from 'null' to 'done'
		
		player.setQuest("vs_quest", "done");
		
		en.step(player, "hi");
		assertEquals("Greetings old friend. I have another collecting #challenge for you.", getReply(npc));
		en.step(player, "challenge");
		assertTrue(getReply(npc).startsWith("Well, you've certainly proved to the residents of Faiumoni that you could be the ultimate collector, but I have one more task for you. Please bring me "));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		player.setQuest("ultimate_collector", "vulcano hammer=1");

		en.step(player, "hi");
		assertEquals("Did you bring me that very rare item I asked you for?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Hm, no, you don't have a vulcano hammer, don't try to fool me!", getReply(npc));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		en.step(player, "hi");
		assertEquals("Did you bring me that very rare item I asked you for?", getReply(npc));
		en.step(player, "no");
		assertEquals("Very well, come back when you have the vulcano hammer with you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));
		
		
		final int xp = player.getXP();
		PlayerTestHelper.equipWithItem(player, "vulcano hammer");
		en.step(player, "hi");
		assertEquals("Did you bring me that very rare item I asked you for?", getReply(npc));
		en.step(player, "yes");
		// [22:42] kymara earns 100000 experience points.
		assertEquals("Wow, it's incredible to see this close up! Many thanks. Now, perhaps we can #deal together.", getReply(npc));
		assertThat(player.getXP(), greaterThan(xp));
		assertTrue(player.isQuestCompleted("ultimate_collector"));
		
		en.step(player, "deal");
		assertEquals("I buy black items, but I can only afford to pay you modest prices.", getReply(npc));
		
		PlayerTestHelper.equipWithItem(player, "black armor");
		en.step(player, "sell black armor");
		assertEquals("A suit of black armor is worth 60000. Do you want to sell it?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thanks! Here is your money.", getReply(npc));
		en.step(player, "bye");
		assertEquals("It was nice to meet you.", getReply(npc));

		// -----------------------------------------------

	}
}
