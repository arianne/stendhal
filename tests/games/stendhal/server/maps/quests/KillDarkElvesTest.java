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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class KillDarkElvesTest {
	private static final String THING = "thing";
	private static final String DARK_ELF_CAPTAIN = "dark elf captain";
	private static final String DARK_ELF_ARCHER = "dark elf archer";

	private static SpeakerNPC npc;
	private static Engine npcEngine;
	private static final KillDarkElves quest = new KillDarkElves();
	private static final String QUEST_SLOT = quest.getSlotName();
	private final List<String> creatures=quest.creatures;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generateNPCRPClasses();
		new DatabaseFactory().initializeDatabase();
		npc = new SpeakerNPC("maerion");
		npcEngine = npc.getEngine();
		SingletonRepository.getNPCList().add(npc);
		final StendhalRPZone zone = new StendhalRPZone("int_semos_guard_house");
		final StendhalRPWorld world = MockStendlRPWorld.get();
		world.addRPZone(zone);
		quest.addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
		SingletonRepository.getNPCList().clear();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		npc.remove("text");
	}

	/**
	 * Tests for idleToAttending.
	 */
	@Test
	public void testIdleToAttending() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			assertThat(bob.hasQuest(QUEST_SLOT), is(false));
			npcEngine.setCurrentState(ConversationStates.ATTENDING);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_OFFERED));
			assertEquals(
					playerSays,
					"I have a problem with some dark elves. I used to be in league with them... now they are too strong. There is access to their lair from a #secret #room in this hall.",
					getReply(npc));
			questHistory.clear();
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for questOfferedToQuestOffered.
	 */
	@Test
	public void testQuestOfferedToQuestOffered() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : Arrays.asList("secret", "room", "secret xxxx", "secret room")) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			assertThat(bob.hasQuest(QUEST_SLOT), is(false));
			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_OFFERED));
			assertEquals(
					playerSays,
					"It's that room downstairs with a grey roof and the evil face on the door. Inside you'll find what the dark elves were making, a mutant thing. Will you help?",
					getReply(npc));
			questHistory.clear();
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for questStartedToAttending.
	 */
	@Test
	public void testQuestStartedToAttending() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : Arrays.asList("secret", "room", "secret xxxx", "secret room")) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			assertThat(bob.hasQuest(QUEST_SLOT), is(false));
			npcEngine.setCurrentState(ConversationStates.QUEST_STARTED);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					playerSays,
					"The room is below us. It has a grey roof and a evil face for a door. I need you to kill all the dark elves and bring me the amulet from the mutant thing.",
					getReply(npc));
			questHistory.clear();
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for attendingToAttending.
	 */
	@Test
	public void testAttendingToAttending() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "done");
			npcEngine.setCurrentState(ConversationStates.ATTENDING);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(playerSays, "Thanks for your help. I am relieved to have the amulet back.", getReply(npc));
			questHistory.clear();
			questHistory.add("I completed Maerion's quest and got an emerald ring of life!");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for questOfferedToAttendingNo.
	 */
	@Test
	public void testQuestOfferedToAttendingNo() {
		LinkedList<String> questHistory = new LinkedList<String>();
		final String[] triggers = { "no", "nothing" };
		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			final double oldKarma = bob.getKarma();

			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);

			npcEngine.step(bob, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(playerSays,
					"Then I fear for the safety of the Nalwor elves...",
					getReply(npc));
			assertThat(bob.getKarma(), lessThan(oldKarma));
			assertThat(bob.getQuest(QUEST_SLOT), is("rejected"));
			questHistory.clear();
			questHistory.add("I do not want to help Maerion.");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}



	// support for old-style quest testing

	/**
	 * Tests for idleToQuestStarted.
	 */
	@Test
	public void testOldQuestIdleToQuestStarted() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "start");
			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_STARTED));
			assertEquals(
					playerSays,
					"Don't you remember promising to sort out my dark elf problem? Kill every dark elf in the #secret room below - especially the snivelling dark elf captain and any evil dark elf archers you find! And bring me the amulet from the mutant thing.",
					getReply(npc));
			questHistory.clear();
			questHistory.add("I agreed to help Maerion.");
			questHistory.add("I have not yet killed the dark elf captain in the secret room.");
			questHistory.add("I have not yet killed the dark elf archer in the secret room.");
			questHistory.add("I have not yet killed the thing.");
			questHistory.add("I have no amulet with me.");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for attendingToQuestOffered.
	 */
	@Test
	public void testOldQuestAttendingToQuestOffered() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "start");
			npcEngine.setCurrentState(ConversationStates.ATTENDING);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					playerSays,
					"I already asked you to kill every dark elf in the tunnel below the secret room. And bring me the amulet from the thing.",
					getReply(npc));
			questHistory.clear();
			questHistory.add("I agreed to help Maerion.");
			questHistory.add("I have not yet killed the dark elf captain in the secret room.");
			questHistory.add("I have not yet killed the dark elf archer in the secret room.");
			questHistory.add("I have not yet killed the thing.");
			questHistory.add("I have no amulet with me.");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for attendingToAttendingAllKilledNoAmulet.
	 */
	@Test
	public void testOldQuestAttendingToAttendingAllKilledNoAmulet() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");

			bob.setSharedKill(DARK_ELF_ARCHER);
			bob.setSharedKill(DARK_ELF_CAPTAIN);
			bob.setSharedKill(THING);

			bob.setQuest(QUEST_SLOT, "start");

			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_STARTED));
			assertEquals(playerSays, "What happened to the amulet? Remember I need it back!", getReply(npc));
			questHistory.clear();
			questHistory.add("I agreed to help Maerion.");
			questHistory.add("I have killed the dark elf captain in the secret room.");
			questHistory.add("I have killed the dark elf archer in the secret room.");
			questHistory.add("I have killed the thing.");
			questHistory.add("I have killed all required creatures.");
			questHistory.add("I have no amulet with me.");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for attendingToAttendingAllKilledHaveAmulet.
	 */
	@Test
	public void testOldQuestAttendingToAttendingAllKilledHaveAmulet() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");

			bob.setSharedKill(DARK_ELF_ARCHER);
			bob.setSharedKill(DARK_ELF_CAPTAIN);
			bob.setSharedKill(THING);

			bob.setQuest(QUEST_SLOT, "start");

			PlayerTestHelper.equipWithItem(bob, "amulet");

			assertTrue(bob.hasKilled(DARK_ELF_ARCHER));
			assertTrue(bob.hasKilled(DARK_ELF_CAPTAIN));
			assertTrue(bob.hasKilled(THING));
			assertTrue(bob.isEquipped("amulet"));

			final double karma = bob.getKarma();
			final int xp = bob.getXP();

			questHistory.clear();
			questHistory.add("I agreed to help Maerion.");
			questHistory.add("I have killed the dark elf captain in the secret room.");
			questHistory.add("I have killed the dark elf archer in the secret room.");
			questHistory.add("I have killed the thing.");
			questHistory.add("I have killed all required creatures.");
			questHistory.add("I have the amulet with me.");
			questHistory.add("It's time to go back to Maerion for a reward.");
			assertEquals(questHistory, quest.getHistory(bob));

			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					playerSays,
					"Many, many thanks. I am relieved to have that back. Here, take this ring. It can revive the powers of the dead.",
					getReply(npc));
			assertFalse(bob.isEquipped("amulet"));
			assertTrue(bob.isEquipped("emerald ring"));
			assertThat(bob.getKarma(), greaterThan(karma));
			assertThat(bob.getXP(), greaterThan(xp));
			assertTrue(bob.isQuestCompleted(QUEST_SLOT));
			questHistory.clear();
			questHistory.add("I completed Maerion's quest and got an emerald ring of life!");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}


	// support for new-style quest

	/**
	 * Tests for questOfferedToAttendingYes.
	 */
	@Test
	public void testQuestOfferedToAttendingYes() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.YES_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(playerSays,
					"Good. Please kill every dark elf down there and get the amulet from the mutant thing.",
					getReply(npc));
			assertThat(bob.getQuest(QUEST_SLOT), is("started"));
			questHistory.clear();
			questHistory.add("I agreed to help Maerion.");
			questHistory.add("I have not yet killed the "+creatures.get(0)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(1)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(2)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(3)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(4)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(5)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(6)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(7)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(8)+" in the secret room.");
			questHistory.add("I have no amulet with me.");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for testAttendingToQuestOffered.
	 */
	@Test
	public void testAttendingToQuestOffered() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "started");
			npcEngine.setCurrentState(ConversationStates.ATTENDING);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					playerSays,
					"I already asked you to kill every dark elf in the tunnel below the secret room. And bring me the amulet from the thing.",
					getReply(npc));
			questHistory.clear();
			questHistory.add("I agreed to help Maerion.");
			questHistory.add("I have not yet killed the "+creatures.get(0)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(1)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(2)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(3)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(4)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(5)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(6)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(7)+" in the secret room.");
			questHistory.add("I have not yet killed the "+creatures.get(8)+" in the secret room.");
			questHistory.add("I have no amulet with me.");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for attendingToAttendingAllKilledNoAmulet.
	 */
	@Test
	public void testAttendingToAttendingAllKilledNoAmulet() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");

			StringBuilder sb=new StringBuilder("started");
			for(int i=0;i<creatures.size();i++) {
				sb.append(";"+creatures.get(i));
			}
			bob.setQuest(QUEST_SLOT, sb.toString());

			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_STARTED));
			assertEquals(playerSays, "What happened to the amulet? Remember I need it back!", getReply(npc));
			questHistory.clear();
			questHistory.add("I agreed to help Maerion.");
			questHistory.add("I have killed the "+creatures.get(0)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(1)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(2)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(3)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(4)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(5)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(6)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(7)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(8)+" in the secret room.");
			questHistory.add("I have killed all required creatures.");
			questHistory.add("I have no amulet with me.");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

	/**
	 * Tests for attendingToAttendingAllKilledHaveAmulet.
	 */
	@Test
	public void testAttendingToAttendingAllKilledHaveAmulet() {
		LinkedList<String> questHistory = new LinkedList<String>();
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");

			StringBuilder sb=new StringBuilder("started");
			for(int i=0;i<creatures.size();i++) {
				sb.append(";"+creatures.get(i));
			}
			bob.setQuest(QUEST_SLOT, sb.toString());

			PlayerTestHelper.equipWithItem(bob, "amulet");

			assertTrue(bob.isEquipped("amulet"));

			final double karma = bob.getKarma();
			final int xp = bob.getXP();

			questHistory.clear();
			questHistory.add("I agreed to help Maerion.");
			questHistory.add("I have killed the "+creatures.get(0)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(1)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(2)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(3)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(4)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(5)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(6)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(7)+" in the secret room.");
			questHistory.add("I have killed the "+creatures.get(8)+" in the secret room.");
			questHistory.add("I have killed all required creatures.");
			questHistory.add("I have the amulet with me.");
			questHistory.add("It's time to go back to Maerion for a reward.");
			assertEquals(questHistory, quest.getHistory(bob));

			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					playerSays,
					"Many, many thanks. I am relieved to have that back. Here, take this ring. It can revive the powers of the dead.",
					getReply(npc));
			assertFalse(bob.isEquipped("amulet"));
			assertTrue(bob.isEquipped("emerald ring"));
			assertThat(bob.getKarma(), greaterThan(karma));
			assertThat(bob.getXP(), greaterThan(xp));
			assertTrue(bob.isQuestCompleted(QUEST_SLOT));
			questHistory.clear();
			questHistory.add("I completed Maerion's quest and got an emerald ring of life!");
			assertEquals(questHistory, quest.getHistory(bob));
		}
	}

}
