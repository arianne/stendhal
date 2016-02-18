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
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.magic.theater.MithrilShieldForgerNPC;
import games.stendhal.server.maps.quests.StuffForBaldemar.ItemData;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class StuffForBaldemarTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_magic_theater";
	private static final String NPC_NAME = "Baldemar";

	private Player player;
	private SpeakerNPC baldemar;
	private Engine en;

	private StuffForBaldemar quest;
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
	public void testAcceptQuest() {
		en.setCurrentState(ConversationStates.QUEST_OFFERED);
		en.step(player, "yes");
		assertEquals("I will need many, many things: 20 mithril bars, 1 obsidian, 1 diamond, 5 emeralds, 10 carbuncles, 10 sapphires, 1 black shield, 1 magic plate shield, 10 gold bars, 20 iron bars, 10 black pearls, 20 shuriken, 15 marbles and 1 snowglobe. Come back when you have them in the same #exact order!", getReply(baldemar));
		
		en.step(player, "exact");
		assertEquals("As I have listed them here, you must provide them in that order.", getReply(baldemar));
		assertEquals("start;0;0;0;0;0;0;0;0;0;0;0;0;0;0", player.getQuest(questSlot));
	}

	@Test
	public void testBroughtNotEnoughMithrilBars() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;5;0;0;0;0;0;0;0;0;0;0;0;0;0");
		en.step(player, "hi");
		assertEquals("I cannot #forge it without the missing 15 mithril bars. After all, this IS a mithril shield.", getReply(baldemar));
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
		assertEquals("I just LOVE those trinkets from athor. I need a snowglobe still.", getReply(baldemar));
	}

	@Test
	public void testBroughtSnowglobe() {
		en.setCurrentState(ConversationStates.IDLE);
		player.setQuest(questSlot, "start;20;1;1;5;10;10;1;1;10;20;10;20;15;1");
		en.step(player, "hi");
		assertEquals("This shield can only be given to those who have killed a black giant, and without the help of others.", getReply(baldemar));
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

	/**
	 * Tests for itemData.
	 */
	@Test
	public void testItemData() {
		int needed = 20;
		ItemData id = new ItemData("name", needed, "prefix ", " suffix");
		assertEquals(needed, id.getStillNeeded());

		id.setAmount(15);
		assertEquals(15, id.getStillNeeded());
		assertEquals(needed, id.getRequired());
		assertEquals("name", id.getName());
		assertEquals("prefix ", id.getPrefix());
		assertEquals(" suffix", id.getSuffix());

		id.subAmount("10");
		assertEquals(5, id.getStillNeeded());
		assertEquals(needed, id.getRequired());
		assertEquals(15, id.getAlreadyBrought());
		assertEquals("prefix 5 names suffix", id.getAnswer());
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
