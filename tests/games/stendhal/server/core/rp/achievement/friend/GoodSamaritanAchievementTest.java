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
package games.stendhal.server.core.rp.achievement.friend;

import static games.stendhal.server.core.rp.achievement.factory.FriendAchievementFactory.ID_GOOD_SAMARITAN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.townhall.MayorNPC;
import games.stendhal.server.maps.quests.DailyItemQuest;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class GoodSamaritanAchievementTest extends ZonePlayerAndNPCTestImpl {

	private static final AchievementNotifier an = AchievementNotifier.get();

	private static final NPCList npcs = SingletonRepository.getNPCList();


	@BeforeClass
	public static void setUpBeforeClass() {
		new DatabaseFactory().initializeDatabase();
		// initialize world
		MockStendlRPWorld.get();
	}

	@Override
	public void setUp() throws Exception {
		final String zoneName = "testzone";
		zone = setupZone(zoneName);

		setNpcNames("Mayor Chalmers");
		addZoneConfigurator(new MayorNPC(), zoneName);

		setZoneForPlayer(zoneName);

		super.setUp();

		StendhalQuestSystem.get().loadQuest(new DailyItemQuest());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Test
	public void init() {
		AchievementTestHelper.init(player);
		assertFalse(achievementReached());

		final int reqKarma = 251;

		while (player.getKarma() < reqKarma) {
			assertFalse(achievementReached());
			doQuest();
		}

		assertTrue(achievementReached());
	}

	private void doQuest() {
		final String questSlot = "daily_item";

		final SpeakerNPC mayor = npcs.get("Mayor Chalmers");
		assertNotNull(mayor);

		final Engine en = mayor.getEngine();

		String questState = player.getQuest(questSlot, 0);
		if (questState != null && questState.equals("done")) {
			player.setQuest(questSlot, 1, "0");
		}

		en.step(player, "hi");
		en.step(player, "quest");

		questState = player.getQuest(questSlot, 0);
		final String itemName = questState.split("=")[0];
		final int quantity = Integer.parseInt(questState.split("=")[1]);

		if (quantity > 1) {
			PlayerTestHelper.equipWithStackableItem(player, itemName, quantity);
		} else {
			PlayerTestHelper.equipWithItem(player, itemName);
		}

		en.step(player, "hi");
		en.step(player, "done");
		en.step(player, "bye");

		an.onFinishQuest(player);

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_GOOD_SAMARITAN);
	}
}
