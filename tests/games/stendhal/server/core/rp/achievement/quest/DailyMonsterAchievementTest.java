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
package games.stendhal.server.core.rp.achievement.quest;

import static games.stendhal.server.core.rp.achievement.factory.SemosMonsterQuestAchievementFactory.ID_CHAMPION;
import static games.stendhal.server.core.rp.achievement.factory.SemosMonsterQuestAchievementFactory.ID_GUARDIAN;
import static games.stendhal.server.core.rp.achievement.factory.SemosMonsterQuestAchievementFactory.ID_HERO;
import static games.stendhal.server.core.rp.achievement.factory.SemosMonsterQuestAchievementFactory.ID_PROTECTOR;
import static games.stendhal.server.core.rp.achievement.factory.SemosMonsterQuestAchievementFactory.ID_VANQUISHER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.DailyMonsterQuest;
import games.stendhal.server.maps.semos.townhall.MayorNPC;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.CreatureTestHelper;


public class DailyMonsterAchievementTest extends ZonePlayerAndNPCTestImpl {

	private Player player;
	private SpeakerNPC npc;

	private static final DailyMonsterQuest questInstance = DailyMonsterQuest.getInstance();
	private final String QUEST_SLOT = questInstance.getSlotName();
	private static final StendhalQuestSystem questSystem = StendhalQuestSystem.get();

	private final List<String> idList = Arrays.asList(ID_PROTECTOR, ID_GUARDIAN, ID_HERO,
			ID_CHAMPION, ID_VANQUISHER);

	private static EntityManager em;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
		em = SingletonRepository.getEntityManager();
		em.populateCreatureList();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		final String npcName = "Mayor Sakhs";

		zone = setupZone("testzone");
		addZoneConfigurator(new MayorNPC(), "testzone");
		setNpcNames(npcName);

		super.setUp();

		npc = getNPC(npcName);
		CreatureTestHelper.generateRPClasses();
		questSystem.loadQuest(questInstance);
	}

	@Test
	public void init() {
		// check creatures have been loaded
		assertNotEquals(0, em.getCreatures().size());

		// check NPC & quest have been loaded
		assertNotNull(npc);
		assertTrue(questSystem.isLoaded(questInstance));

		// solo kills
		resetPlayer();

		doCycle(ID_PROTECTOR, 10, false);
		doCycle(ID_GUARDIAN, 50, false);
		doCycle(ID_HERO, 100, false);
		doCycle(ID_CHAMPION, 250, false);
		doCycle(ID_VANQUISHER, 500, false);

		// shared kills
		resetPlayer();

		doCycle(ID_PROTECTOR, 10, true);
		doCycle(ID_GUARDIAN, 50, true);
		doCycle(ID_HERO, 100, true);
		doCycle(ID_CHAMPION, 250, true);
		doCycle(ID_VANQUISHER, 500, true);
	}

	/**
	 * Resets player achievements.
	 */
	private void resetPlayer() {
		player = null;
		assertNull(player);
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

		player.setLevel(10);
		assertEquals(10, player.getLevel());

		assertNull(player.getQuest(QUEST_SLOT));

		AchievementTestHelper.init(player);
		for (final String ID: idList) {
			assertFalse(achievementReached(ID));
		}
	}

	private void doCycle(final String id, final int reqCount, final boolean shared) {
		while (getCompletedCount() < reqCount) {
			assertFalse(achievementReached(id));
			doQuest(shared);
		}
		assertTrue(achievementReached(id));
	}

	/**
	 * Completes quest one time.
	 */
	private void doQuest(final boolean shared) {
		final Engine en = npc.getEngine();

		final int completedCount = getCompletedCount();

		assertFalse(questIsActive());

		// make sure we can do quest again
		final String questState = player.getQuest(QUEST_SLOT, 0);
		if (questState != null && questState.equals("done")) {
			player.setQuest(QUEST_SLOT, 1, "0");
		}

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(player, "quest");

		assertTrue(questIsActive());

		final String enemyName = player.getQuest(QUEST_SLOT, 0).split(",")[0];
		assertNotNull(enemyName);

		onKill(enemyName, shared);

		en.step(player, "done");
		en.step(player, "bye");

		assertFalse(questIsActive());
		assertEquals(completedCount + 1, getCompletedCount());
	}

	private void onKill(final String enemy, final boolean shared) {
		if (shared) {
			player.setSharedKillCount(enemy, player.getSharedKill(enemy) + 1);
		} else {
			player.setSoloKillCount(enemy, player.getSoloKill(enemy) + 1);
		}
		AchievementNotifier.get().onKill(player);

		assertTrue(player.hasKilled(enemy));
	}

	private int getCompletedCount() {
		final String count = player.getQuest(QUEST_SLOT, 2);
		if (count == null) {
			return 0;
		}

		return Integer.parseInt(count);
	}

	/**
	 * Checks if the quest is active.
	 */
	private boolean questIsActive() {
		return new QuestActiveCondition(QUEST_SLOT).fire(player, null, npc);
	}

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	private boolean achievementReached(final String ID) {
		return AchievementTestHelper.achievementReached(player, ID);
	}
}
