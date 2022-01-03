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

import static games.stendhal.server.core.rp.achievement.factory.FriendAchievementFactory.ID_STILL_BELIEVING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.MeetBunny;
import games.stendhal.server.maps.quests.MeetSanta;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class StillBelievingAchievementTest extends ZonePlayerAndNPCTestImpl {

	private Player player;

	private static final StendhalRPWorld world = MockStendlRPWorld.get();
	private static final StendhalQuestSystem questSystem = StendhalQuestSystem.get();

	private static final NPCList npcs = SingletonRepository.getNPCList();

	private final String year = new SimpleDateFormat("yy").format(Calendar.getInstance().getTime());


	@BeforeClass
	public static void setUpBeforeClass() {
		new DatabaseFactory().initializeDatabase();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		final String zoneName = "testzone";

		// zone required by quests
		world.addRPZone("none", new StendhalRPZone("int_admin_playground"));

		setNpcNames("Easter Bunny", "Santa");
		zone = setupZone(zoneName);
		setZoneForPlayer(zoneName);

		super.setUp();

		// load quests
		System.setProperty("stendhal.easterbunny", "");
		System.setProperty("stendhal.santa", "");
		questSystem.loadQuest(new MeetBunny());
		questSystem.loadQuest(new MeetSanta());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Test
	public void init() {
		resetPlayer();

		doQuestBunny();
		assertFalse(achievementReached());
		doQuestSanta();

		assertTrue(achievementReached());
	}

	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_STILL_BELIEVING);
	}

	private void resetPlayer() {
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

		// make sure player is dressed so Santa gives present
		player.setOutfit("dress=1");

		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	private void doQuestBunny() {
		final String questSlot = "meet_bunny_" + year;
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC bunny = npcs.get("Easter Bunny");
		assertNotNull(bunny);

		final Engine en = bunny.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestSanta() {

		final SpeakerNPC santa = npcs.get("Santa");
		assertNotNull(santa);

		final Engine en = santa.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}
}
