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
package games.stendhal.server.maps.deniran.cityinterior.tannery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.rp.DaylightPhase;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class TannerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";
	private static final String npcName = "Skinner Rawhide";

	private static TannerNPC configurator;
	private static SpeakerNPC tanner;
	private static String QUEST_SLOT;
	private static String FEATURE_SLOT;
	private static int requiredMoneyLoot;
	private static int serviceFee;
	private static Map<String, Integer> requiredItems;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setZoneForPlayer(ZONE_NAME);
		setNpcNames(npcName);

		configurator = TannerNPC.getInstance();
		addZoneConfigurator(configurator, ZONE_NAME);

		super.setUp();

		tanner = configurator.getNPC();
		QUEST_SLOT = configurator.getQuestSlot();
		FEATURE_SLOT = configurator.getFeatureSlot();
		requiredMoneyLoot = configurator.getRequiredMoneyLoot();
		serviceFee = configurator.getServiceFee();
		requiredItems = configurator.getRequiredItems();
	}

	private String getCurrentTimestamp() {
		return Long.toString(System.currentTimeMillis());
	}

	private int getLootCount() {
		return player.getNumberOfLootsForItem("money");
	}

	// for setting player's number of looted money
	private void setLootCount(final int count) {
		final int diff = count - player.getNumberOfLootsForItem("money");
		player.incLootForItem("money", diff);

		assertEquals(count, getLootCount());
	}

	// for resetting player money loot count to 0
	private void resetLoots() {
		setLootCount(0);
	}

	@Test
	public void initTest() {
		testEntities();
		testNightTime();
		testDayTime();

		DaylightPhase.unsetTestingPhase();
	}

	private void testEntities() {
		assertNotNull(tanner);
		assertEquals(npcName, tanner.getName());
		assertNotNull(player);
		assertNotNull(player.getID());
		assertNull(player.getQuest(QUEST_SLOT));
		assertNull(player.getFeature(FEATURE_SLOT));
	}

	private void testNightTime() {
		// player can not do quest at night

		final Engine en = tanner.getEngine();

		DaylightPhase.setTestingPhase(DaylightPhase.NIGHT);
		assertEquals(DaylightPhase.NIGHT, DaylightPhase.current());

		final String nightReply = "It's late. I need to get to bed. Please come back in the morning.";

		// XXX: is there an assert method for less/greater than comparison?
		assertTrue(player.getNumberOfLootsForItem("money") < requiredMoneyLoot);

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(nightReply, getReply(tanner));

		player.setQuest(QUEST_SLOT, "start");

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(nightReply, getReply(tanner));

		player.setQuest(QUEST_SLOT, getCurrentTimestamp());

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(nightReply, getReply(tanner));

		setLootCount(requiredMoneyLoot);
		assertTrue(player.getNumberOfLootsForItem("money") >= requiredMoneyLoot);

		player.setQuest(QUEST_SLOT, null);

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(nightReply, getReply(tanner));

		player.setQuest(QUEST_SLOT, "start");

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(nightReply, getReply(tanner));

		player.setQuest(QUEST_SLOT, getCurrentTimestamp());

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(nightReply, getReply(tanner));

		player.setQuest(QUEST_SLOT, "done");

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(nightReply, getReply(tanner));
	}

	private void testDayTime() {
		final Engine en = tanner.getEngine();

		DaylightPhase.setTestingPhase(DaylightPhase.DAY);
		assertEquals(DaylightPhase.DAY, DaylightPhase.current());

		// reset player
		player.setQuest(QUEST_SLOT, null);
		resetLoots();

		assertNull(player.getQuest(QUEST_SLOT));
		assertTrue(player.getNumberOfLootsForItem("money") < serviceFee);
		assertFalse(player.isEquipped("pelt"));
		assertFalse(player.isEquipped("money", serviceFee));

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Welcome to Deniran's tannery.", getReply(tanner));

		setLootCount(requiredMoneyLoot - 1);

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Welcome to Deniran's tannery.", getReply(tanner));

		setLootCount(requiredMoneyLoot);
		assertEquals(requiredMoneyLoot, getLootCount());

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
				"I see you are experienced at looting money. I can make a pouch for you to carry your money in."
				+ " But I will need some items. Are you interested?",
				getReply(tanner));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
				"I see you are experienced at looting money. I can make a pouch for you to carry your money in."
				+ " But I will need some items. Are you interested?",
				getReply(tanner));

		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Oh? I think it would be encouraged.", getReply(tanner));

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());

		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
				configurator.sayRequiredItems("Okay. I will need [items]. Also, my fee is " + Integer.toString(configurator.getServiceFee())
				+ " money. Please come back when you have that.", false),
				getReply(tanner));
		assertEquals("start", player.getQuest(QUEST_SLOT));

		// test keyword responses
		en.step(player, "leather needle");
		assertEquals("I'm sure I had one around here somewhere.", getReply(tanner));
		en.step(player, "leather thread");
		assertEquals("Leather thread can be made by cutting up a #pelt. You will need a #'rotary cutter'.", getReply(tanner));
		en.step(player, "pelt");
		assertEquals("Sometimes you can get pelts off of animals that drop them.", getReply(tanner));
		en.step(player, "rotary cutter");
		assertEquals(
				"I seem to have misplaced mine. Perhaps you could borrow one from somebody else. They are even used for slicing pizza"
				+ ", so ask around in places that make pizza if you can't find one anywhere else.",
				getReply(tanner));
		en.step(player, "bye");

		final String noItemsReply = configurator.sayRequiredItems("Bring me [items] and I will make a pouch to carry your money in.", true);

		// player has none of the required items
		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(noItemsReply, getReply(tanner));
		en.step(player, "bye");

		// player has money only
		PlayerTestHelper.equipWithMoney(player, serviceFee);

		for (final String itemName: requiredItems.keySet()) {
			assertFalse(player.isEquipped(itemName));
		}
		assertTrue(player.isEquipped("money", serviceFee));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(noItemsReply, getReply(tanner));
		en.step(player, "bye");

		// player has items only
		player.drop("money", serviceFee);
		for (final String itemName: requiredItems.keySet()) {
			final int quant = requiredItems.get(itemName);
			if (quant == 1) {
				PlayerTestHelper.equipWithItem(player, itemName);
			} else if (quant > 1) {
				PlayerTestHelper.equipWithStackableItem(player, itemName, quant);
			}

			assertTrue(player.isEquipped(itemName, quant));
		}
		assertFalse(player.isEquipped("money", serviceFee));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(noItemsReply, getReply(tanner));
		en.step(player, "bye");

		// player has items & money but not enough
		PlayerTestHelper.equipWithMoney(player, serviceFee - 1);
		assertEquals(serviceFee - 1, player.getNumberOfEquipped("money"));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(noItemsReply, getReply(tanner));
		en.step(player, "bye");

		// player has items & enough money
		PlayerTestHelper.equipWithItem(player, "money");
		assertEquals(serviceFee, player.getNumberOfEquipped("money"));

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals("Ah, you found the items to make the pouch. Would you like me to begin?", getReply(tanner));

		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Really? Okay then. See me again if you change your mind.", getReply(tanner));

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		en.step(player, "yes");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		String readyReply = getReply(tanner);
		assertTrue(
				readyReply.equals("Okay, I will begin making your money pouch. Please come back in 24 hours.") ||
				readyReply.equals("Okay, I will begin making your money pouch. Please come back in 1 day."));

		assertFalse(player.getQuest(QUEST_SLOT).equals("start"));
		assertFalse(player.isEquipped("pouch"));
		assertFalse(player.isEquipped("money"));

		// player returns before pouch is ready
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertTrue(getReply(tanner).startsWith("I'm sorry, your money pouch is not ready yet. Please come back in "));

		// player returns after pouch is ready
		player.setQuest(QUEST_SLOT, "0");
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
				"You came back just in time. Your money pouch is ready. Try it out. I know you will like it.",
				getReply(tanner));
		assertEquals("done", player.getQuest(QUEST_SLOT));
		assertNotNull(player.getFeature(FEATURE_SLOT));

		// player talks to tanner after receiving pouch
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("I knew you would enjoy the pouch.", getReply(tanner));

		PlayerTestHelper.resetNPC(tanner);
	}
}
