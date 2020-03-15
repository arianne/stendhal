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

import static games.stendhal.server.core.rp.achievement.factory.KirdnehItemAchievementFactory.ID_ARCHAEOLOGIST;
import static games.stendhal.server.core.rp.achievement.factory.KirdnehItemAchievementFactory.ID_DEDICATED;
import static games.stendhal.server.core.rp.achievement.factory.KirdnehItemAchievementFactory.ID_MASTER;
import static games.stendhal.server.core.rp.achievement.factory.KirdnehItemAchievementFactory.ID_SENIOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.townhall.MayorNPC;
import games.stendhal.server.maps.kirdneh.museum.CuratorNPC;
import games.stendhal.server.maps.quests.DailyItemQuest;
import games.stendhal.server.maps.quests.WeeklyItemQuest;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class KirdnehMuseumAchievementTest extends ZonePlayerAndNPCTestImpl {

	private Player player;
	private SpeakerNPC npc;

	private static final WeeklyItemQuest questInstance = WeeklyItemQuest.getInstance();
	private final String QUEST_SLOT = questInstance.getSlotName();
	private static final StendhalQuestSystem questSystem = StendhalQuestSystem.get();

	private final List<String> idList = Arrays.asList(ID_ARCHAEOLOGIST, ID_DEDICATED, ID_SENIOR,
			ID_MASTER);


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		/* Kirdneh Musem quest depends on Daily item quest for some checks, so
		 * Mayor Chalmers & DailyItemQuest must be loaded
		 */

		final String[] npcNames = {"Hazel", "Mayor Chalmers"};

		zone = setupZone("testzone");
		addZoneConfigurator(new CuratorNPC(), "testzone");
		addZoneConfigurator(new MayorNPC(), "testzone");
		setNpcNames(npcNames);

		super.setUp();

		npc = getNPC(npcNames[0]);
		if (!questSystem.isLoaded(DailyItemQuest.getInstance())) {
			questSystem.loadQuest(DailyItemQuest.getInstance());
		}
		if (!questSystem.isLoaded(questInstance)) {
			questSystem.loadQuest(questInstance);
		}
	}

	@Test
	public void init() {
		// check NPC & quest have been loaded
		assertNotNull(npc);
		assertTrue(questSystem.isLoaded(DailyItemQuest.getInstance()));
		assertTrue(questSystem.isLoaded(questInstance));

		resetPlayer();

		doCycle(ID_ARCHAEOLOGIST, 5);
		doCycle(ID_DEDICATED, 25);
		doCycle(ID_SENIOR, 50);
		doCycle(ID_MASTER, 100);
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

		AchievementTestHelper.init(player);
		for (final String ID: idList) {
			assertFalse(achievementReached(ID));
		}
	}

	private void doCycle(final String id, final int reqCount) {
		while (getCompletedCount() < reqCount) {
			assertFalse(achievementReached(id));
			doQuest();
		}
		assertTrue(achievementReached(id));
	}

	/**
	 * Completes quest one time.
	 */
	private void doQuest() {
		final Engine en = npc.getEngine();

		final int completedCount = getCompletedCount();

		assertFalse(questIsActive());

		// make sure we can do quest again
		String questState = player.getQuest(QUEST_SLOT, 0);
		if (questState != null && questState.equals("done")) {
			player.setQuest(QUEST_SLOT, 1, "0");
		}

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(player, "quest");
		assertTrue(questIsActive());

		questState = player.getQuest(QUEST_SLOT, 0);
		assertNotNull(questState);

		final String itemName = questState.split("=")[0];
		final int quantity = Integer.parseInt(questState.split("=")[1]);

		if (quantity > 1) {
			PlayerTestHelper.equipWithStackableItem(player, itemName, quantity);
		} else {
			PlayerTestHelper.equipWithItem(player, itemName);
		}
		assertTrue(player.isEquipped(itemName, quantity));

		en.step(player, "done");
		en.step(player, "bye");

		assertFalse(questIsActive());
		assertEquals(completedCount + 1, getCompletedCount());
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
