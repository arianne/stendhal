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
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;

public class CrownForTheWannaBeKingTest {
	private static final String QUEST_SLOT = "crown_for_the_wannabe_king";
	private static Engine npcEngine;
	private static SpeakerNPC npc;

	@BeforeClass
	public static void setUpBeforeClass() {
		PlayerTestHelper.generateNPCRPClasses();

		npc = new SpeakerNPC("Ivan Abe");
		SingletonRepository.getNPCList().add(npc);

		final SpeakerNPC rewardnpc = new SpeakerNPC("Kendra Mattori");
		SingletonRepository.getNPCList().add(rewardnpc);

		final CrownForTheWannaBeKing cotbk = new CrownForTheWannaBeKing();
		cotbk.addToWorld();

		npcEngine = npc.getEngine();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		SingletonRepository.getNPCList().clear();
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Tests for idleToQuestion1.
	 */
	@Test
	public void testIdleToQuestion1() {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "");
			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);
			assertThat("Used greeting: " + playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Oh it's you again. Did you bring me any #items for my new crown?", getReply(npc));
		}
	}

	/**
	 * Tests for idleToIdleQuestCompleted.
	 */
	@Test
	public void testIdleToIdleQuestCompleted() {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(ConversationStates.IDLE);
			bob.setQuest(QUEST_SLOT, "done");
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(true));
			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.IDLE));
			assertEquals("My new crown will be ready soon and I will dethrone the king! Mwahahaha!", getReply(npc));
		}
	}

	/**
	 * Tests for idleToIdleQuestinStatereward.
	 */
	@Test
	public void testIdleToIdleQuestinStatereward() {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "reward");
			npcEngine.setCurrentState(ConversationStates.IDLE);

			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.IDLE));
			assertEquals("My new crown will be ready soon and I will dethrone the king! Mwahahaha!", getReply(npc));
		}
	}

	/**
	 * Tests for idleToAttending.
	 */
	@Test
	public void testIdleToAttending() {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			assertThat(bob.hasQuest(QUEST_SLOT), is(false));
			npcEngine.setCurrentState(ConversationStates.IDLE);

			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					"Greetings. Be quick with your matters, I have a lot of work to do. And next time clean your boots, you are lucky that I'm not the king...yet!",
					getReply(npc));
		}
	}

	/**
	 * Tests for attendingToQuestOffered.
	 */
	@Test
	public void testAttendingToQuestOffered() {
		final Player bob = PlayerTestHelper.createPlayer("bob");
		npcEngine.setCurrentState(ConversationStates.ATTENDING);
		assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
		npcEngine.step(bob, "crown");
		assertThat(npcEngine.getCurrentState(), is(ConversationStates.QUEST_OFFERED));
		assertEquals("Yes, I need jewels and gold for my new crown. Will you help me?", getReply(npc));
	}

	/**
	 * Tests for attendingToIdle.
	 */
	@Test
	public void testAttendingToIdle() {
		final Player bob = PlayerTestHelper.createPlayer("bob");
		npcEngine.setCurrentState(ConversationStates.ATTENDING);
		npcEngine.step(bob, "reward");
		assertThat(npcEngine.getCurrentState(), is(ConversationStates.IDLE));
		assertEquals(
				"As I said, find priestess Kendra Mattori in a temple at the city of wizards. She will give you your reward. Now go, I'm busy!",
				getReply(npc));
	}

	/**
	 * Tests for attendingToIdleQuestNotCompleted.
	 */
	@Test
	public void testAttendingToIdleQuestNotCompleted() {
		final String[] triggers = { "no", "nothing" };
		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(ConversationStates.ATTENDING);
			bob.setQuest(QUEST_SLOT, "");
			assertThat(bob.hasQuest(QUEST_SLOT), is(true));
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.IDLE));
			assertEquals("Well don't come back before you find something for me!", getReply(npc));
		}
	}

	/**
	 * Tests for questOfferedToQuestion1.
	 */
	@Test
	public void testQuestOfferedToQuestion1() {
		for (final String playerSays : ConversationPhrases.YES_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);
			assertTrue(new QuestNotStartedCondition(QUEST_SLOT).fire(bob, null, npc));

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertThat(playerSays, bob.hasQuest(QUEST_SLOT), is(true));
			assertEquals(
					"I want my crown to be beautiful and shiny. I need 2 #carbuncles, 2 #diamonds, 4 #emeralds, 2 #'gold bars', an #obsidian, and 3 #sapphires."
					+ " Do you have some of those now with you?",
					getReply(npc));
		}
	}

	/**
	 * Tests for questOfferedToIdle.
	 */
	@Test
	public void testQuestOfferedToIdle() {
		final String[] triggers = { "no", "nothing" };
		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			final double oldkarma = bob.getKarma();
			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);
			assertThat(playerSays, bob.isQuestCompleted(QUEST_SLOT), is(false));
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.IDLE));
			assertThat(playerSays, bob.getKarma(), lessThan(oldkarma));
			assertThat(playerSays, bob.hasQuest(QUEST_SLOT), is(true));
			assertThat(playerSays, bob.getQuest(QUEST_SLOT), is("rejected"));
			assertEquals("Oh you don't want to help me?! Get lost, you are wasting my precious time!", getReply(npc));
		}
	}

	/**
	 * Tests for attendingToAttending.
	 */
	@Test
	public void testAttendingToAttending() {
		final String[] triggers = { "plan", "favor", "favour", "quest", "task", "work", "job", "trade", "deal", "offer" };
		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");

			npcEngine.setCurrentState(ConversationStates.ATTENDING);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
		}
	}

	/**
	 * Tests for question1toQuestion1.
	 */
	@Test
	public void testQuestion1toQuestion1() {
		for (final String playerSays : ConversationPhrases.YES_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
		}
	}

	/**
	 * Tests for question1toQuestion1PosactionList.
	 */
	@Test
	public void testQuestion1toQuestion1PosactionList() {
		npc.remove("text");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);
		npcEngine.setCurrentState(ConversationStates.QUESTION_1);

		npcEngine.step(bob, "items");
		assertThat("items", npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
		assertThat(
				"items",
				getReply(npc),
				is("I need 2 #carbuncles, 2 #diamonds, 4 #emeralds, 2 #'gold bars', an #obsidian, and 3 #sapphires. Did you bring something?"));
	}

	/**
	 * Tests for question1ToIdle.
	 */
	@Test
	public void testQuestion1ToIdle() {
		final String[] triggers = { "no", "nothing" };
		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "");
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.IDLE));
		}
	}

	/**
	 * Tests for question1ToQuestion1Itembrought.
	 */
	@Test
	public void testQuestion1ToQuestion1Itembrought() {
		final String[] triggers = { "obsidian", "diamond", "carbuncle", "sapphire", "emerald", "gold bar" };

		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "");
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("You have already brought that!", getReply(npc));
		}

		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("You don't have " + Grammar.a_noun(playerSays) + " with you!", getReply(npc));
		}

		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);

			PlayerTestHelper.equipWithItem(bob, playerSays);
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Good, do you have anything else?", getReply(npc));
			assertThat(bob.getQuest(QUEST_SLOT), not((is(CrownForTheWannaBeKing.NEEDED_ITEMS))));
		}

		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);

			PlayerTestHelper.equipWithItem(bob, playerSays);
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Good, do you have anything else?", getReply(npc));
			assertThat(bob.getQuest(QUEST_SLOT), not((is(CrownForTheWannaBeKing.NEEDED_ITEMS))));
		}
	}

	/**
	 * Tests for bringItems.
	 */
	@Test
	public void testBringItems() {
		final Player bob = PlayerTestHelper.createPlayer("bob");
		bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);

		// is("I need 2 #gold bar, 4 #emerald, 3 #sapphire, 2 #carbuncle,
		// 2 #diamond, and 1 #obsidian. Did you bring something?"));
		final String[] triggers = { "obsidian", "diamond", "diamond", "carbuncle", "carbuncle", "sapphire", "sapphire",
				"sapphire", "emerald", "emerald", "emerald", "emerald", "gold bar" };
		npcEngine.setCurrentState(ConversationStates.QUESTION_1);
		for (final String playerSays : triggers) {
			PlayerTestHelper.equipWithItem(bob, playerSays);
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Good, do you have anything else?", getReply(npc));
			assertThat(bob.getQuest(QUEST_SLOT), not((is(CrownForTheWannaBeKing.NEEDED_ITEMS))));
		}

		PlayerTestHelper.equipWithItem(bob, "gold bar");
		assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
		assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

		npcEngine.step(bob, "gold bar");
		assertEquals(
				"You have served me well, my crown will be the mightiest of them all! Go to see Kendra Mattori in the Wizard City to get your #reward.",
				getReply(npc));
        assertThat("last thing brought", npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
        assertEquals("reward", bob.getQuest(QUEST_SLOT));

		final double oldkarma = bob.getKarma();
		final int oldatk = bob.getAtkXP();

		final SpeakerNPC rewardnpc = SingletonRepository.getNPCList().get("Kendra Mattori");
		final Engine rewardEngine = rewardnpc.getEngine();
		rewardEngine.setCurrentState(ConversationStates.ATTENDING);
		rewardEngine.step(bob, "reward");

		assertThat(bob.isQuestCompleted(QUEST_SLOT), is(true));
		assertThat(bob.getKarma(), greaterThan(oldkarma));
		assertThat(bob.getAtkXP(), greaterThan(oldatk));
	}

}
