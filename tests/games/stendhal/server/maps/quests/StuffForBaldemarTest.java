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
import games.stendhal.server.maps.magic.theater.MithrilShieldForgerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class StuffForBaldemarTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_magic_theater";
	private static final String NPC_NAME = "Baldemar";

	private static final String HISTORY_DEFAULT = "I met Baldemar in the magic theater.";
	private static final String HISTORY_REJECTED = "I'm not interested in his ideas about shields made from mithril.";
	private static final String HISTORY_START = "Baldemar asked me to bring him many things.";
	private static final String HISTORY_NEED_ITEMS_PREFIX = "I still need to bring ";
	private static final String HISTORY_NEED_ITEMS_SUFFIX = ", in this order.";
	private static final String HISTORY_NEED_ONE_ITEM_SUFFIX = ".";
	private static final String HISTORY_BROUGHT_ALL_ITEMS = "I took all the special items to Baldemar.";
	private static final String HISTORY_NEED_KILL_GIANT = "I will need to bravely face a black giant alone, before I am worthy of this shield.";
	private static final String HISTORY_REWARD_PENDING = "Baldemar is forging my mithril shield!";
	private static final String HISTORY_COMPLETED = "I brought Baldemar many items, killed a black giant solo, and he forged me a mithril shield.";

	private SpeakerNPC baldemar;
	private Engine en;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public StuffForBaldemarTest() {
		super(ZONE_NAME, NPC_NAME);
	}

	@Override
	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		new MithrilShieldForgerNPC().configureZone(zone, null);

		quest = new StuffForBaldemar();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("bob");

		baldemar = SingletonRepository.getNPCList().get(NPC_NAME);
		en = baldemar.getEngine();
	}

	@Override
	@After
	public void tearDown() {
		PlayerTestHelper.removeNPC(NPC_NAME);
	}

	@Test
	public void testRejectQuest() {
		en.setCurrentState(ConversationStates.QUEST_OFFERED);
		en.step(player, "no");
		assertEquals("I can't believe you are going to pass up this opportunity! You must be daft!!!", getReply(baldemar));

		assertEquals("rejected", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_REJECTED);
	}

	@Test
	public void testAcceptQuest() {
		String neededItems = "20 mithril bars, an obsidian, a diamond, 5 emeralds, 10 carbuncles, 10 sapphires, a black shield, a magic plate shield, 10 gold bars, 20 pieces of iron, 10 black pearls, 20 shurikens, 15 marbles and a snowglobe";

		en.setCurrentState(ConversationStates.QUEST_OFFERED);
		en.step(player, "yes");
		assertEquals("I will need many, many things: " + neededItems + ". Come back when you have them in the same #exact order!", getReply(baldemar));

		en.step(player, "exact");
		assertEquals("As I have listed them here, you must provide them in that order.", getReply(baldemar));
		assertEquals("start;0;0;0;0;0;0;0;0;0;0;0;0;0;0", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_NEED_ITEMS_PREFIX + neededItems + HISTORY_NEED_ITEMS_SUFFIX);
	}

	@Test
	public void testBroughtNotEnoughMithrilBars() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;5;0;0;0;0;0;0;0;0;0;0;0;0;0");

		en.step(player, "hi");

		assertEquals("I cannot #forge it without the missing 15 mithril bars. After all, this IS a mithril shield.", getReply(baldemar));
		String neededItems = "15 mithril bars, an obsidian, a diamond, 5 emeralds, 10 carbuncles, 10 sapphires, a black shield, a magic plate shield, 10 gold bars, 20 pieces of iron, 10 black pearls, 20 shurikens, 15 marbles and a snowglobe";
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_NEED_ITEMS_PREFIX + neededItems + HISTORY_NEED_ITEMS_SUFFIX);
	}

	@Test
	public void testBroughtEnoughMithrilBars() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;0;0;0;0;0;0;0;0;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need an obsidian still.", getReply(baldemar));
	}

	@Test
	public void testBroughtObsidian() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;0;0;0;0;0;0;0;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need a diamond still.", getReply(baldemar));
	}

	@Test
	public void testBroughtDiamondAndNotEnoughEmeralds() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;1;0;0;0;0;0;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need 4 emeralds still.", getReply(baldemar));
	}

	@Test
	public void testBroughtEmeraldsAndNotEnoughCarbuncles() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;1;0;0;0;0;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need 9 carbuncles still.", getReply(baldemar));
	}

	@Test
	public void testBroughtCarbunclesAndNotEnoughSapphires() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;1;0;0;0;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need 9 sapphires still.", getReply(baldemar));
	}

	@Test
	public void testBroughtSapphires() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;0;0;0;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need a black shield to form the framework for your new shield.", getReply(baldemar));
	}

	@Test
	public void testBroughtBlackShield() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;0;0;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need a magic plate shield for the pieces and parts for your new shield.", getReply(baldemar));
	}

	@Test
	public void testBroughtMagicPlateShield() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;1;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need 9 gold bars to melt down with the mithril and iron.", getReply(baldemar));
	}

	@Test
	public void testBroughtGoldBarsAndNotEnoughIron() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;1;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I need 19 pieces of iron to melt down with the mithril and gold.", getReply(baldemar));
	}

	@Test
	public void testBroughtIronBarsAndNotEnoughPearls() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;1;0;0;0");
		en.step(player, "hi");
		assertEquals("I need 9 black pearls to crush into fine powder to sprinkle onto shield to give it a nice sheen.", getReply(baldemar));
	}

	@Test
	public void testBroughtBlackPearlsAndNotEnoughShurikens() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;1;0;0");
		en.step(player, "hi");
		assertEquals("I need 19 shurikens to melt down with the mithril, gold and iron. It is a 'secret' ingredient that only you and I know about. ;)", getReply(baldemar));
	}

	@Test
	public void testBroughtShurikensAndNotEnoughMarbles() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;20;2;0");
		en.step(player, "hi");
		assertEquals("My son wants some new toys. I need 13 marbles still.", getReply(baldemar));
	}

	@Test
	public void testBroughtMarbles() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;20;15;0");
		en.step(player, "hi");
		assertEquals("I just LOVE those trinkets from Athor. I need a snowglobe still.", getReply(baldemar));

		String neededItems = "a snowglobe";
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_NEED_ITEMS_PREFIX + neededItems + HISTORY_NEED_ONE_ITEM_SUFFIX);
	}

	@Test
	public void testBroughtSnowglobe() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;20;15;1");
		en.step(player, "hi");
		assertEquals("This shield can only be given to those who have killed a black giant, and without the help of others.", getReply(baldemar));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_BROUGHT_ALL_ITEMS, HISTORY_NEED_KILL_GIANT);
	}

	@Test
	public void testBroughtEverythingAndKilledGiant() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;20;15;1");
		player.setSoloKill("black giant");

		en.step(player, "hi");

		assertEquals("You've brought everything I need to forge the shield. Come back in 10 minutes and it will be ready.", getReply(baldemar));
		assertTrue(player.getQuest(questSlot).startsWith("forging;"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_BROUGHT_ALL_ITEMS, HISTORY_REWARD_PENDING);
	}

	@Test
	public void testForgingNotComplete() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "forging;" + (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)));

		en.step(player, "hi");

		assertTrue(getReply(baldemar).startsWith("I haven't finished forging your shield. Please check back in"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_BROUGHT_ALL_ITEMS, HISTORY_REWARD_PENDING);
	}

	@Test
	public void testForgingComplete() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "forging;" + (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(20)));

		en.step(player, "hi");

		assertTrue(getReply(baldemar).startsWith("I have finished forging your new mithril shield. Enjoy."));
		assertEquals("done", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_BROUGHT_ALL_ITEMS, HISTORY_COMPLETED);
	}

	@Test
	public void testItemsAllUpToBlackShield() {
		player.setQuest(questSlot, "start;0;0;0;0;0;0;0;0;0;0;0;0;0;0");

		PlayerTestHelper.equipWithStackableItem(player, "mithril bar", 20);
		PlayerTestHelper.equipWithItem(player, "obsidian");
		PlayerTestHelper.equipWithItem(player, "diamond");
		PlayerTestHelper.equipWithStackableItem(player, "emerald", 5);
		PlayerTestHelper.equipWithStackableItem(player, "carbuncle", 10);
		PlayerTestHelper.equipWithStackableItem(player, "sapphire", 10);
		PlayerTestHelper.equipWithItem(player, "black shield");

		en.setCurrentState(ConversationStates.IDLE);
		en.step(player, "hi");

		assertEquals("start;20;1;1;5;10;10;1;0;0;0;0;0;0;0", player.getQuest(questSlot));
	}

	@Test
	public void testItemsAllAfterBlackShield() {
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;0;0;0;0;0;0;0");

		PlayerTestHelper.equipWithItem(player, "magic plate shield");
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 10);
		PlayerTestHelper.equipWithStackableItem(player, "iron", 20);
		PlayerTestHelper.equipWithStackableItem(player, "black pearl", 10);
		PlayerTestHelper.equipWithStackableItem(player, "shuriken", 20);
		PlayerTestHelper.equipWithStackableItem(player, "marbles", 15);
		PlayerTestHelper.equipWithItem(player, "snowglobe");

		en.setCurrentState(ConversationStates.IDLE);
		en.step(player, "hi");

		assertEquals("start;20;1;1;5;10;10;1;1;10;20;10;20;15;1", player.getQuest(questSlot));
	}

	@Test
	public void testAskToForgeWithoutAnyItem() {
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setQuest(questSlot, "start;0;0;0;0;0;0;0;0;0;0;0;0;0;0");
		en.step(player, "forge");

		assertEquals("I need 20 mithril bars, an obsidian, a diamond, 5 emeralds, 10 carbuncles, 10 sapphires, a black shield, a magic plate shield, 10 gold bars, 20 pieces of iron, 10 black pearls, 20 shurikens, 15 marbles and a snowglobe.", getReply(baldemar));
	}

	@Test
	public void testAskToForgeWithoutLastThreeItems() {
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;10;0;0");
		en.step(player, "forge");

		assertEquals("I need 10 shurikens, 15 marbles and a snowglobe.", getReply(baldemar));
	}

	@Test
	public void testAskToForgeWithoutLastTwoItems() {
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;20;12;0");
		en.step(player, "forge");

		assertEquals("I need 3 marbles and a snowglobe.", getReply(baldemar));
	}

	@Test
	public void testAskToForgeWithoutLastItem() {
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;20;15;0");
		en.step(player, "forge");

		assertEquals("I need a snowglobe.", getReply(baldemar));
	}

	@Test
	public void testAskToForgeWithAllItems() {
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;20;15;1");
		en.step(player, "forge");

		assertEquals(StuffForBaldemar.TALK_NEED_KILL_GIANT, getReply(baldemar));
	}
}
