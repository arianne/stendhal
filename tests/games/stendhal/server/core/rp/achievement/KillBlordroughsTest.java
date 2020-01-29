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
package games.stendhal.server.core.rp.achievement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.rp.achievement.factory.KillBlordroughsAchievementFactory;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.barracks.BuyerNPC;
import games.stendhal.server.maps.quests.KillBlordroughs;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class KillBlordroughsTest extends ZonePlayerAndNPCTestImpl {

	private Player player;
	private SpeakerNPC npc;
	private static final KillBlordroughs questInstance = KillBlordroughs.getInstance();
	private static final String QUEST_SLOT = questInstance.getSlotName();
	private static final StendhalQuestSystem questSystem = StendhalQuestSystem.get();

	// ID used for achievement
	private final String achievementId = KillBlordroughsAchievementFactory.ID_LACKEY;

	// required completions
	private final int requiredCompletions = KillBlordroughsAchievementFactory.COUNT_LACKEY;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
		AchievementNotifier.get().initialize();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		zone = setupZone("testzone");
		addZoneConfigurator(new BuyerNPC(), "testzone");
		setNpcNames("Mrotho");

		super.setUp();

		npc = getNPC("Mrotho");
		questSystem.loadQuest(questInstance);
	}

	@Test
	public void init() {
		resetPlayer();
		testNPC();
		testAchievement();
	}

	private void testNPC() {
		assertNotNull(npc);
		assertEquals("Mrotho", npc.getName());
	}

	private void testAchievement() {
		for (int idx = 0; idx < requiredCompletions; idx++) {
			completeQuest();

			if (idx >= requiredCompletions - 1) {
				assertEquals(requiredCompletions, questInstance.getCompletedCount(player));
			} else {
				assertFalse(achievementReached());
			}
		}

		assertTrue(achievementReached());
	}


	/**
	 * Resets player achievements.
	 */
	private void resetPlayer() {
		player = null;
		assertNull(player);
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

		assertNull(player.getQuest(QUEST_SLOT));

		assertFalse(player.arePlayerAchievementsLoaded());
		player.initReachedAchievements();
		assertTrue(player.arePlayerAchievementsLoaded());
		assertFalse(achievementReached());
	}

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	private boolean achievementReached() {
		return player.hasReachedAchievement(achievementId);
	}

	/**
	 * Completes quest one time.
	 */
	private void completeQuest() {
		final Engine en = npc.getEngine();

		final int completedCount = questInstance.getCompletedCount(player);

		assertFalse(questIsActive());

		// reset kill count & set quest state to repeatable
		player.setSoloKillCount("blordrough corporal", 0);
		if (player.getQuest(QUEST_SLOT) != null) {
			player.setQuest(QUEST_SLOT, 1, "0");
		}

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(player, "quest");
		assertTrue(questIsActive());
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		player.setSoloKillCount("blordrough corporal", 100);

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(player, "done");
		assertFalse(questIsActive());
		assertEquals(completedCount + 1, questInstance.getCompletedCount(player));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}

	/**
	 * Checks if the quest is active.
	 */
	private boolean questIsActive() {
		return new QuestActiveCondition(QUEST_SLOT).fire(player, null, npc);
	}
}
