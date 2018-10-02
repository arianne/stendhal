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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.kalavan.castle.SadScientistNPC;
import games.stendhal.server.maps.semos.townhall.MayorNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class SadScientistTest {
	private static final String QUEST_SLOT = "sad_scientist";
	// better: use the one from quest and make it visible
	private static final String NEEDED_ITEMS = "emerald=1;obsidian=1;sapphire=1;carbuncle=2;gold bar=20;mithril bar=1;shadow legs=1";
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

		new SadScientistNPC().configureZone(zone, null);
		new MayorNPC().configureZone(zone, null);

		AbstractQuest quest = new SadScientist();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Vasi Elos");
		en = npc.getEngine();

		// -----------------------------------------------


		// -----------------------------------------------

		// [23:00] Admin kymara changed your state of the quest 'sad_scientist' from 'done' to 'null'
		// [23:00] Changed the state of quest 'sad_scientist' from 'done' to 'null'
		// [23:00] Script "AlterQuest.class" was successfully executed.
		en.step(player, "hi");
		assertEquals("Go away!", getReply(npc));
		en.step(player, "task");
		assertEquals("So...looks like you want to help me?", getReply(npc));
		en.step(player, "no");
		assertEquals("If you change your mind please ask me again...", getReply(npc));
		en.step(player, "bye");
		assertEquals("Go away!", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Go away!", getReply(npc));
		en.step(player, "task");
		assertEquals("So...looks like you want to help me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("My wife is living in Semos City. She loves gems. Can you bring me some #gems that I need to make a pair of precious #legs?", getReply(npc));
		en.step(player, "no");
		assertEquals("Go away before I kill you!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Go away!", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Go away!", getReply(npc));
		en.step(player, "task");
		assertEquals("So...looks like you want to help me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("My wife is living in Semos City. She loves gems. Can you bring me some #gems that I need to make a pair of precious #legs?", getReply(npc));
		en.step(player, "gems");
		assertEquals("I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars and one mithril bar. Can you do that for my wife?", getReply(npc));
		en.step(player, "legs");
		assertEquals("Jewelled legs. I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars and one mithril bar. Can you do that for my wife? Can you bring what I need?", getReply(npc));
		en.step(player, "yes");
		assertEquals("I am waiting, Semos man.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hello. Do you have any #items I need for the jewelled legs?", getReply(npc));

		// summon all the items needed:
		// but not all the gold bar
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 10);
		PlayerTestHelper.equipWithItem(player, "mithril bar");
		PlayerTestHelper.equipWithItem(player, "emerald");
		PlayerTestHelper.equipWithItem(player, "obsidian");
		PlayerTestHelper.equipWithItem(player, "sapphire");
		PlayerTestHelper.equipWithStackableItem(player, "carbuncle", 2);

		assertFalse(player.isEquipped("gold bar", 20));

		final String[] triggers = { "obsidian", "gold bar", "carbuncle", "sapphire", "emerald", "mithril bar" };

		for (final String playerSays : triggers) {

			assertThat(player.isQuestCompleted(QUEST_SLOT), is(false));

			en.setCurrentState(ConversationStates.QUESTION_1);

			en.step(player, playerSays);
			assertThat(playerSays, en.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Good, do you have anything else?", getReply(npc));
			assertThat(player.getQuest(QUEST_SLOT), not((is(NEEDED_ITEMS))));
		}
		// now bring the remaining gold bar
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 10);

		// -----------------------------------------------
		en.step(player, "gold bar");
		assertEquals("I am a stupid fool too much in love with my wife Vera to remember, of course these legs also need a base to add " +
		"the jewels to. Please return with a pair of shadow legs. Bye.", getReply(npc));

		assertEquals(en.getCurrentState(), ConversationStates.IDLE);

		// return with no legs
		en.step(player, "hi");
		assertEquals("Hello again. Please return when you have the shadow legs, a base for me to add jewels to make jewelled legs for Vera.", getReply(npc));

		PlayerTestHelper.equipWithItem(player, "shadow legs");

		en.step(player, "hi");
		assertEquals("The shadow legs! Wonderful! I will start my work. I can do this in very little time with the help of technology! " +
		"Please come back in 20 minutes.", getReply(npc));

		assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		en.step(player, "hi");
		assertTrue(getReply(npc).startsWith("Do you think I can work that fast? Go away. Come back in"));

		// -----------------------------------------------

		// -----------------------------------------------

		// [23:03] Admin kymara changed your state of the quest 'sad_scientist' from 'making;1269298965037' to 'making;1'
		// [23:03] Changed the state of quest 'sad_scientist' from 'making;1269298965037' to 'making;1'

		// -----------------------------------------------
		assertTrue(player.getQuest(QUEST_SLOT).startsWith("making;"));
		player.setQuest(QUEST_SLOT, "making;1");

		en.step(player, "hi");
		assertEquals("I finished the legs. But I cannot trust you. Before I give the jewelled legs to you, I need a message from my darling. Ask Mayor Sakhs for Vera. Can you do that for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Oh, thank you. I am waiting.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Please ask Mayor Sakhs about my wife Vera.", getReply(npc));

		// -----------------------------------------------

		npc = SingletonRepository.getNPCList().get("Mayor Sakhs");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Welcome citizen! Do you need #help?", getReply(npc));
		en.step(player, "vera");
		assertEquals("What? How do you know her? Well it is a sad story. She was picking arandula for Ilisa (they were friends) and she saw the catacombs entrance. 3 months later a young hero saw her, and she was a vampirette. I kept this for her husband. A letter. I think he is in Kalavan.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Have a good day and enjoy your stay!", getReply(npc));

		assertTrue(player.isEquipped("note"));
		// -----------------------------------------------

		npc = SingletonRepository.getNPCList().get("Vasi Elos");
		en = npc.getEngine();

		// [23:03] You put a valuable item on the ground. Please note that it will expire in 10 minutes, as all items do. But in this case there is no way to restore it.
		// I did test removing the note also but I am too lazy to add it in right now
		// en.step(player, "hi");
		// assertEquals("Please ask Mayor Sakhs about my wife Vera.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hello! Do you have anything for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Oh no! I feel the pain. I do not need to create those beautiful jewelled legs now. I want to transform them. I want to make them a symbol of pain. You! Go kill my brother, the Imperial Scientist Sergej Elos. Give me his blood.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Do it!", getReply(npc));
		// i don't understand this. it doesn't always do it. but no error. weird.
		en.step(player, "bye");
		assertEquals("Go away!", getReply(npc));

		// -----------------------------------------------
		assertFalse(player.isEquipped("note"));
		en.step(player, "hi");
		assertEquals("I am only in pain. Kill my brother and bring me his blood. It's all I want now.", getReply(npc));

		// -----------------------------------------------

		// [23:04] Sergej Elos has been killed by kymara
		// [23:04] kymara earns 1750 experience points.

		// -----------------------------------------------

		PlayerTestHelper.equipWithItem(player, "goblet", QUEST_SLOT);
		player.setSharedKill("Sergej Elos");

		en.step(player, "hi");
		assertEquals("Ha, ha, ha! I will cover those jewelled legs with this blood and they will transform into a #symbol of pain.", getReply(npc));
		en.step(player, "symbol");
		assertEquals("I am going to create a pair of black legs. Come back in 5 minutes.", getReply(npc));

		assertFalse(player.isEquipped("goblet"));

		assertTrue(player.getQuest("sad_scientist").startsWith("decorating;"));
		// -----------------------------------------------

		// -----------------------------------------------

		en.step(player, "hi");
		assertTrue(getReply(npc).startsWith("I did not finish decorating the legs. Please check back"));

		// -----------------------------------------------

		// [23:05] Admin kymara changed your state of the quest 'sad_scientist' from 'decorating;1269299078702' to 'decorating;1'
		// [23:05] Changed the state of quest 'sad_scientist' from 'decorating;1269299078702' to 'decorating;1'

		// -----------------------------------------------
		player.setQuest(QUEST_SLOT, "decorating;1");

		final int xp = player.getXP();
		final double karma = player.getKarma();

		en.step(player, "hi");
		assertEquals("Here are the black legs. Now I beg you to wear them. The symbol of my pain is done. Fare thee well.", getReply(npc));
		// [23:05] kymara earns 10000 experience points.

		// -----------------------------------------------
		assertTrue(player.getQuest(QUEST_SLOT).equals("done"));
		assertTrue(player.isEquipped("black legs"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(karma));

		en.step(player, "hi");
		assertEquals("Go away!", getReply(npc));
	}
}
