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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.rp.achievement.factory.CommerceAchievementFactory;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.semos.tavern.BarMaidNPC;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class HappyHourTest extends ZonePlayerAndNPCTestImpl {

	private static final String npcName = "Margaret";

	// items used in achievement
	private final List<String> itemList = Arrays.asList(CommerceAchievementFactory.ITEMS_HAPPY_HOUR);

	// ID used for achievement
	private final String achievementId = CommerceAchievementFactory.ID_HAPPY_HOUR;

	// required number of apples to harves or loot
	private final int ITEM_COUNT = CommerceAchievementFactory.COUNT_HAPPY_HOUR;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setNpcNames(npcName);
		setupZone("testzone", new BarMaidNPC());
		setZoneForPlayer("testzone");
		super.setUp();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Test
	public void init() {
		initAchievements();
		testAchievement();
	}

	private void testAchievement() {
		final SpeakerNPC npc = getNPC(npcName);
		final Engine en = npc.getEngine();

		final int priceBeer = 10;
		final int priceWine = 15;
		final int fullPrice = (priceBeer * ITEM_COUNT) + (priceWine * ITEM_COUNT);

		for (final String item: itemList) {
			assertFalse(player.isEquipped(item));
			assertEquals(0, player.getQuantityOfBoughtItems(item));
		}

		assertFalse(player.isEquipped("money"));

		PlayerTestHelper.equipWithMoney(player, fullPrice);
		assertTrue(player.isEquipped("money", fullPrice));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(player, "buy 100 beer");
		en.step(player, "yes");
		assertTrue(player.isEquipped("beer", ITEM_COUNT));
		assertFalse(achievementReached());
		en.step(player, "buy 100 wine");
		en.step(player, "yes");
		assertTrue(player.isEquipped("wine", ITEM_COUNT));

		for (final String item: itemList) {
			assertEquals(ITEM_COUNT, player.getQuantityOfBoughtItems(item));
		}

		assertTrue(achievementReached());
		en.step(player, "bye");
	}


	/**
	 * Initializes achievements engine.
	 */
	private void initAchievements() {
		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	/**
	 * Checks if the player has reached the achievement.
	 *
	 * @return
	 * 		<code>true</player> if the player has the achievement.
	 */
	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, achievementId);
	}
}
