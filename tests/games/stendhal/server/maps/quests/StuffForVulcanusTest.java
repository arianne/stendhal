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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.kotoch.SmithNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class StuffForVulcanusTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "-1_kotoch_entrance_n";
	private static final String NPC_NAME = "Vulcanus";

	private static final String HISTORY_DEFAULT = "I met Vulcanus in Kotoch.";
	private static final String HISTORY_REJECTED = "I don't want an immortal sword.";
	private static final String HISTORY_START = "To forge the immortal sword I must bring several things to Vulcanus.";
	private static final String HISTORY_NEED_ITEMS_PREFIX = "I still need to bring ";
	private static final String HISTORY_NEED_ITEMS_SUFFIX = ", in this order.";
	private static final String HISTORY_NEED_ONE_ITEM_SUFFIX = ".";
	private static final String HISTORY_BROUGHT_ALL_ITEMS = "I took all the special items to Vulcanus.";
	private static final String HISTORY_NEED_KILL_GIANT = "I must prove my worth and kill a giant, before I am worthy of this prize.";
	private static final String HISTORY_REWARD_PENDING = "Vulcanus, son of gods himself, now forges my immortal sword.";
	private static final String HISTORY_COMPLETED = "Gold bars and giant hearts together with the forging from a god's son made me a sword of which I can be proud.";

	private SpeakerNPC vulcanus;
	private Engine en;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public StuffForVulcanusTest() {
		super(ZONE_NAME, NPC_NAME);
	}

	@Override
	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		new SmithNPC().configureZone(zone, null);

		quest = new StuffForVulcanus();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("bob");

		vulcanus = SingletonRepository.getNPCList().get(NPC_NAME);
		en = vulcanus.getEngine();
	}

	@Override
	@After
	public void tearDown() {
		PlayerTestHelper.removeNPC(NPC_NAME);
	}

	@Test
	public void testTalkGreeting() {
		assertTrue(en.step(player, "hi"));
		assertEquals("Chairetismata! I am Vulcanus the smither.", getReply(vulcanus));
	}

	@Test
	public void testTalkHelp() {
		en.step(player, "hi");

		assertTrue(en.step(player, "help"));
		assertEquals("I may help you to get a very #special item for only a few others...", getReply(vulcanus));

		assertTrue(en.step(player, "special"));
		assertEquals("Who told you that!?! *cough* Anyway, yes, I can forge a very special item for you. But you will need to complete a #quest", getReply(vulcanus));
		assertFalse(en.step(player, "yes"));
	}

	@Test
	public void testRejectQuest() {
		en.setCurrentState(ConversationStates.QUEST_OFFERED);
		en.step(player, "no");

		assertEquals("Oh, well forget it then, if you don't want an immortal sword...", getReply(vulcanus));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("rejected", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_REJECTED);
	}

	@Test
	public void testAcceptQuest() {
		String neededItems = "15 pieces of iron, 26 pieces of wood, 12 gold bars and 6 giant hearts";

		en.setCurrentState(ConversationStates.QUEST_OFFERED);
		en.step(player, "yes");
		assertEquals("I will need several things: " + neededItems + ". Come back when you have them in the same #exact order!", getReply(vulcanus));

		en.step(player, "exact");
		assertEquals("This archaic magic requires that the ingredients are added on an exact order.", getReply(vulcanus));
		assertEquals("start;0;0;0;0", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_NEED_ITEMS_PREFIX + neededItems + HISTORY_NEED_ITEMS_SUFFIX);
	}

	@Test
	public void testBroughtNotEnoughIronBars() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;10;0;0;0");

		en.step(player, "hi");
		assertEquals("I cannot #forge it without the missing 5 pieces of iron.", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need 5 #'pieces of iron', 26 #'pieces of wood', 12 #'gold bars' and 6 #'giant hearts'.", getReply(vulcanus));

		String neededItems = "5 pieces of iron, 26 pieces of wood, 12 gold bars and 6 giant hearts";
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_NEED_ITEMS_PREFIX + neededItems + HISTORY_NEED_ITEMS_SUFFIX);
	}

	@Test
	public void testNeedTwoMoreIronBars() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;13;0;0;0");

		en.step(player, "hi");
		assertEquals("I cannot #forge it without the missing 2 pieces of iron.", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need 2 #'pieces of iron', 26 #'pieces of wood', 12 #'gold bars' and 6 #'giant hearts'.", getReply(vulcanus));
	}

	@Test
	public void testNeedOneMoreIronBar() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;14;0;0;0");

		en.step(player, "hi");
		assertEquals("I cannot #forge it without the missing a piece of iron.", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need a #'piece of iron', 26 #'pieces of wood', 12 #'gold bars' and 6 #'giant hearts'.", getReply(vulcanus));
	}

	@Test
	public void testBroughtGoldButNotEnoughIronBars() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;10;0;0;0");
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 12);

		en.step(player, "hi");
		assertEquals("I cannot #forge it without the missing 5 pieces of iron.", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need 5 #'pieces of iron', 26 #'pieces of wood', 12 #'gold bars' and 6 #'giant hearts'.", getReply(vulcanus));

		assertTrue(player.isEquipped("gold bar", 12));
	}

	@Test
	public void testBroughtRemainingIronAndGold() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;10;0;0;0");
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 12);
		PlayerTestHelper.equipWithStackableItem(player, "iron", 10);

		en.step(player, "hi");
		assertEquals("How do you expect me to #forge it without missing 26 pieces of wood for the fire?", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need 26 #'pieces of wood', 12 #'gold bars' and 6 #'giant hearts'.", getReply(vulcanus));

		assertTrue(player.isEquipped("gold bar", 12));
	}

	@Test
	public void testBroughtWoodAndGold() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;0;0;0");
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 12);
		PlayerTestHelper.equipWithStackableItem(player, "wood", 26);

		en.step(player, "hi");
		assertEquals("It is the base element of the enchantment. I need 6 giant hearts still.", getReply(vulcanus));

		assertFalse(player.isEquipped("gold bar"));
	}

	@Test
	public void testNeedTwoMoreWood() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;24;0;0");

		en.step(player, "hi");
		assertEquals("How do you expect me to #forge it without missing 2 pieces of wood for the fire?", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need 2 #'pieces of wood', 12 #'gold bars' and 6 #'giant hearts'.", getReply(vulcanus));
	}

	@Test
	public void testNeedOneMoreWood() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;25;0;0");

		en.step(player, "hi");
		assertEquals("How do you expect me to #forge it without missing a piece of wood for the fire?", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need a #'piece of wood', 12 #'gold bars' and 6 #'giant hearts'.", getReply(vulcanus));
	}

	@Test
	public void testNeedTwoMoreGoldBars() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;26;10;0");

		en.step(player, "hi");
		assertEquals("I must pay a bill to spirits in order to cast the enchantment over the sword. I need 2 gold bars more.", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need 2 #'gold bars' and 6 #'giant hearts'.", getReply(vulcanus));
	}

	@Test
	public void testNeedOneMoreGoldBar() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;26;11;0");

		en.step(player, "hi");
		assertEquals("I must pay a bill to spirits in order to cast the enchantment over the sword. I need a gold bar more.", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need a #'gold bar' and 6 #'giant hearts'.", getReply(vulcanus));
	}

	@Test
	public void testBrought2GiantHearts() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;26;12;0");
		PlayerTestHelper.equipWithStackableItem(player, "giant heart", 2);

		en.step(player, "hi");
		assertEquals("It is the base element of the enchantment. I need 4 giant hearts still.", getReply(vulcanus));
	}

	@Test
	public void testNeedOneMoreGiantHeart() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;26;12;5");

		en.step(player, "hi");
		assertEquals("It is the base element of the enchantment. I need a giant heart still.", getReply(vulcanus));

		en.step(player, "forge");
		assertEquals("I will need a #'giant heart'.", getReply(vulcanus));

		String neededItems = "a giant heart";
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_NEED_ITEMS_PREFIX + neededItems + HISTORY_NEED_ONE_ITEM_SUFFIX);
	}

	@Test
	public void testBroughtRemainingGiantHearts() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;26;12;2");
		PlayerTestHelper.equipWithStackableItem(player, "giant heart", 4);

		en.step(player, "hi");
		assertEquals("Did you really get those giant hearts yourself? I don't think so! This powerful sword can only be given to those that are strong enough to kill a #giant.", getReply(vulcanus));
		en.step(player, "giant");
		assertEquals("There are ancient stories of giants living in the mountains at the north of Semos and Ados.", getReply(vulcanus));

		assertFalse(player.isEquipped("giant heart"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_BROUGHT_ALL_ITEMS, HISTORY_NEED_KILL_GIANT);
	}

	@Test
	public void testBroughtEverythingAndKilledGiant() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;15;26;12;6");
		player.setSharedKill("giant");

		en.step(player, "hi");

		assertEquals("You've brought everything I need to make the immortal sword, and what is more, you are strong enough to handle it. Come back in 10 minutes and it will be ready.", getReply(vulcanus));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_BROUGHT_ALL_ITEMS, HISTORY_REWARD_PENDING);
	}

	@Test
	public void testForgingNotComplete() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "forging;" + (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)));

		en.step(player, "hi");

		assertTrue(getReply(vulcanus).startsWith("I haven't finished forging the sword. Please check back in"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_BROUGHT_ALL_ITEMS, HISTORY_REWARD_PENDING);
	}

	@Test
	public void testForgingComplete() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "forging;" + (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(20)));

		en.step(player, "hi");

		assertTrue(getReply(vulcanus).startsWith("I have finished forging the mighty immortal sword. You deserve this."));
		assertEquals("done", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_BROUGHT_ALL_ITEMS, HISTORY_COMPLETED);
	}

	@Test
	public void testAttemptToRepeatTask() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "done");

		en.step(player, "hi");
		en.step(player, "task");
		assertEquals("Oh! I am so tired. Look for me later. I need a few years of relaxing.", getReply(vulcanus));
	}
}
