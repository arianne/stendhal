/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.item.Bestiary;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;


public class CollectEnemyDataTest extends ZoneAndPlayerTestImpl {

	private SpeakerNPC rengard;
	private static final CollectEnemyData quest = new CollectEnemyData();
	private final String questSlot = quest.getSlotName();

	private static StendhalRPWorld world;

	private static final String zoneList[] = CollectEnemyData.zonesWhitelist;

	private static final int steps = 3;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		world = MockStendlRPWorld.get();
		// initialize creatures/enemies
		SingletonRepository.getEntityManager().populateCreatureList();

		for (final String zoneName: zoneList) {
			world.addRPZone(new StendhalRPZone(zoneName, 10, 10));
		}

		StendhalQuestSystem.get().loadQuest(quest);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		player = PlayerTestHelper.createPlayer("player");
		rengard = SingletonRepository.getNPCList().get("Rengard");

		registerPlayer(player, zoneList[0]);
	}

	@Test
	public void init() {
		testZones();
		testEntities();
		testQuest();
	}

	private void testZones() {
		for (final String zoneName: CollectEnemyData.zonesWhitelist) {
			assertNotNull(world.getRPZone(zoneName));
		}
	}

	private void testEntities() {
		assertNotNull(player);
		assertNotNull(rengard);
	}

	private void testQuest() {
		assertFalse(questIsStarted());
		assertFalse(questIsDone());

		final Engine en = rengard.getEngine();

		final int xp = player.getXP();
		double karma = player.getKarma();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hello fellow adventurer.", getReply(rengard));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("May you have luck on your future adventures.", getReply(rengard));

		en.step(player, "hi");
		en.step(player, "job");
		assertEquals(
				"Job? Hah! I am a free spirit. I travel the world, seeking to increase my own knowledge and experience.",
				getReply(rengard));
		en.step(player, "help");
		assertEquals(
				"If you seek to expand your knowledge as I do, I have a little #task I could use some help with.",
				getReply(rengard));
		en.step(player, "offer");
		assertEquals(
				"If you seek to expand your knowledge as I do, I have a little #task I could use some help with.",
				getReply(rengard));
		en.step(player, "buy bestiary");
		assertEquals("I need your help with a #task first.", getReply(rengard));
		en.step(player, "bestiary");
		assertNull(getReply(rengard));

		// asking for quest
		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals(
				"Would you like to help me collect data on creatures found around the world of Faimouni?",
				getReply(rengard));
		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Okay, have it your way.", getReply(rengard));
		assertNull(player.getQuest(questSlot));
		en.step(player, "quest");
		en.step(player, "yes");
		assertTrue(questIsStarted());

		karma += 35;
		assertEquals(xp, player.getXP());
		assertEquals(karma, player.getKarma(), 0);

		for (int step = 0; step < steps; step++) {
			doStep(step, en);
		}

		assertTrue(questIsDone());
		assertEquals(xp, player.getXP());
		assertEquals(karma + 200.0, player.getKarma(), 0);

		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(player, "quest");
		assertEquals("Thank you for your help compiling creature information.", getReply(rengard));
		en.step(player, "help");
		assertEquals(
				"If you own a #bestiary, you may be able to find a psychic that can give you more insight into the creatures you have encountered.",
				getReply(rengard));
		en.step(player, "offer");
		assertEquals("I can sell you a #bestiary.", getReply(rengard));
		en.step(player, "bestiary");
		assertEquals("A bestiary allows you to keep track of the enemies you have defeated.", getReply(rengard));

		assertFalse(player.isEquipped("money"));
		assertFalse(player.isEquipped("bestiary"));

		PlayerTestHelper.equipWithMoney(player, 499999);

		en.step(player, "buy bestiary");
		assertEquals(ConversationStates.BUY_PRICE_OFFERED, en.getCurrentState());
		assertEquals("A bestiary will cost 500000. Do you want to buy it?", getReply(rengard));

		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Sorry, you don't have enough money!", getReply(rengard));

		assertFalse(player.isEquipped("bestiary"));

		PlayerTestHelper.equipWithMoney(player, 1);

		assertTrue(player.isEquipped("money", 500000));

		en.step(player, "buy bestiary");
		en.step(player, "yes");
		assertEquals(
				"I have written your name down in it, just in case you lose it. Remember, the creatures you track in this"
				+ " bestiary are only for you. So it will not work for anyone else. Anyone who wants to track kills should"
				+ " buy their own.",
				getReply(rengard));

		assertEquals(1, player.getAllEquipped("bestiary").size());
		assertFalse(player.isEquipped("money"));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		// check that player is owner
		final Bestiary bestiary = (Bestiary) player.getFirstEquipped("bestiary");
		assertEquals(player.getName(), bestiary.getOwner());
	}


	private void doStep(final int step, final Engine en) {
		final int currentStep = getCurrentStep();
		assertEquals(step, currentStep);
		assertFalse(isStepDone(currentStep));

		String currentEnemy = getEnemyForStep(currentStep);
		int killCount = getRecordedKillsForStep(currentStep);
		String answer = getAnswerForStep(currentEnemy, currentStep);
		assertNotNull(currentEnemy);
		assertNotNull(answer);

		if (currentStep == 0) {
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
			assertEquals(
					"Great! I have compiled much info on creatures I have come across. But I am still missing three. First, I need some info on "
					+ Grammar.singular(currentEnemy) + ".",
					getReply(rengard));
		}

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
				"Have you brought information about the creature I requested?",
				getReply(rengard));
		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Okay. What else can I help you with?", getReply(rengard));
		en.step(player, "quest");
		assertEquals("You have already agreed to help me collect creature data.", getReply(rengard));
		en.step(player, "buy bestiary");
		assertEquals("I still need you to help me gather information on " + Grammar.a_noun(currentEnemy) + ".", getReply(rengard));

		assertEquals(0, killCount);

		en.step(player, "bye");
		en.step(player, "hi");
		assertEquals(
				"Have you brought information about the creature I requested?",
				getReply(rengard));
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Don't lie to me. You haven't even killed one since we spoke.", getReply(rengard));

		en.step(player, "bye");

		// test that it works with both solo & shared kills
		if (currentStep < 2) {
			player.setSoloKillCount(currentEnemy, player.getSoloKill(currentEnemy) + 1);
		} else {
			player.setSharedKillCount(currentEnemy, player.getSharedKill(currentEnemy) + 1);
		}

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
				"Have you brought information about the creature I requested?",
				getReply(rengard));
		en.step(player, "yes");
		assertEquals(ConversationStates.QUESTION_2, en.getCurrentState());
		questionMatches(currentStep);

		// answer incorrectly
		en.step(player, answer + "-");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Hmmm, that doesn't seem accurate. Perhaps you could double check.", getReply(rengard));

		// answer correctly
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.QUESTION_2, en.getCurrentState());
		questionMatches(currentStep);
		en.step(player, answer);

		assertTrue(isStepDone(currentStep));

		if (currentStep < 2) {
			final int nextStep = getCurrentStep();
			final String nextEnemy = getEnemyForStep(nextStep);

			assertEquals(currentStep + 1, nextStep);
			assertNotEquals(currentEnemy, nextEnemy);

			assertEquals(ConversationStates.IDLE, en.getCurrentState());
			assertEquals(
					"Thank you! I am going to write this down. Now I need information on "
					+ Grammar.singular(nextEnemy) + ".", getReply(rengard));
		} else {
			assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
			assertEquals(
					"Thanks so much for you help. Now I have all the information I need to complete my #bestiary."
					+ " If you would like one of your own, I can sell you one.", getReply(rengard));
		}
	}


	private boolean questIsStarted() {
		final String questState = player.getQuest(questSlot);
		return (questState != null && !questState.equals("done"));
	}

	private boolean questIsDone() {
		final String questState = player.getQuest(questSlot);
		return (questState != null && quest.isCompleted(player));
	}

	private boolean isStepDone(final int step) {
		return quest.isStepDone(player, step);
	}

	private int getCurrentStep() {
		return quest.getCurrentStep(player);
	}

	private String getEnemyForStep(final int step) {
		return quest.getEnemyForStep(player, step);
	}

	private int getRecordedKillsForStep(final int step) {
		Integer killCount = quest.getRecordedKillsForStep(player, step);
		if (killCount == null) {
			return 0;
		}

		return killCount;
	}

	private String getAnswerForStep(final String enemy, final int step) {
		return quest.getAnswerForStep(player, SingletonRepository.getEntityManager().getCreature(enemy), step);
	}

	private void questionMatches(final int step) {
		assertEquals(quest.getQuestionForStep(player, step), getReply(rengard));
	}
}
