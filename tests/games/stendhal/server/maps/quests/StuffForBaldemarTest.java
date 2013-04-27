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
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.StuffForBaldemar.ItemData;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class StuffForBaldemarTest {

	private static StuffForBaldemar sfb;
	private static SpeakerNPC baldemar;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		QuestHelper.setUpBeforeClass();
		baldemar = new SpeakerNPC("Baldemar");
		NPCList.get().add(baldemar);
		sfb = new StuffForBaldemar();
		sfb.addToWorld();
	}

	
	
	/**
	 * Tests for getSlotName.
	 */
	@Test
	public void testGetSlotName() {
		assertEquals("mithrilshield_quest", new StuffForBaldemar().getSlotName());
	}

	/**
	 * Tests for step1.
	 * 
	 * @throws Exception if setup failed 
	 */
	@Test
	public void teststep1() throws Exception {
		QuestHelper.setUpBeforeClass();
		
		Engine en = baldemar.getEngine();
		Player bob = PlayerTestHelper.createPlayer("bob");

		en.setCurrentState(ConversationStates.QUEST_OFFERED);
		en.step(bob, "yes");
		assertEquals("I will need many, many things: 20 mithril bars, 1 obsidian, 1 diamond, 5 emeralds,10 carbuncles, 10 sapphires, 1 black shield, 1 magic plate shield, 10 gold bars, 20 iron bars, 10 black pearls, 20 shuriken, 15 marbles and 1 snowglobe. Come back when you have them in the same #exact order!", getReply(baldemar));
		
		en.step(bob, "exact");
		assertEquals("As I have listed them here, you must provide them in that order.", getReply(baldemar));
		assertEquals("start;0;0;0;0;0;0;0;0;0;0;0;0;0;0", bob.getQuest(sfb.getSlotName()));
		
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;5;0;0;0;0;0;0;0;0;0;0;0;0;0");
		en.step(bob, "hi");
		
		assertEquals("I cannot #forge it without the missing 15 mithril bars. After all, this IS a mithril shield.", getReply(baldemar));
		
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;0;0;0;0;0;0;0;0;0;0;0;0;0");
		en.step(bob, "hi");
		
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need an obsidian still.", getReply(baldemar));
	
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;0;0;0;0;0;0;0;0;0;0;0;0");
		en.step(bob, "hi");
		
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need a diamond still.", getReply(baldemar));
	
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;1;0;0;0;0;0;0;0;0;0;0");
		en.step(bob, "hi");
		
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need 4 emeralds still.", getReply(baldemar));
	
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;1;0;0;0;0;0;0;0;0;0");
		en.step(bob, "hi");
		
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need 9 carbuncles still.", getReply(baldemar));
	
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;1;0;0;0;0;0;0;0;0");
		en.step(bob, "hi");
		
		assertEquals("I need several gems to grind into dust to mix with the mithril. I need 9 sapphires still.", getReply(baldemar));
	
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;0;0;0;0;0;0;0;0");
		en.step(bob, "hi");
		
		assertEquals("I need a black shield to form the framework for your new shield.", getReply(baldemar));
	
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;1;0;0;0;0;0;0;0");
		en.step(bob, "hi");
		assertEquals("I need a magic plate shield for the pieces and parts for your new shield.", getReply(baldemar));
	
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;1;1;1;0;0;0;0;0");
		en.step(bob, "hi");
		assertEquals("I need 9 gold bars to melt down with the mithril and iron.", getReply(baldemar));
	
		
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;1;1;10;1;0;0;0;0");
		en.step(bob, "hi");
		assertEquals("I need 19 pieces of iron to melt down with the mithril and gold.", getReply(baldemar));
		
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;1;1;10;20;1;0;0;0");
		en.step(bob, "hi");
		assertEquals("I need 9 black pearls to crush into fine powder to sprinkle onto shield to give it a nice sheen.", getReply(baldemar));
		
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;1;1;10;20;10;1;0;0");
		en.step(bob, "hi");
		assertEquals("I need 19 shurikens to melt down with the mithril, gold and iron. It is a 'secret' ingredient that only you and I know about. ;)", getReply(baldemar));
		
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;1;1;10;20;10;20;2;0"); 
		en.step(bob, "hi");
		assertEquals("My son wants some new toys. I need 13 marbles still.", getReply(baldemar));
		
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;1;1;10;20;10;20;15;0"); 
		en.step(bob, "hi");
		assertEquals("I just LOVE those trinkets from athor. I need a snowglobe still.", getReply(baldemar));
		
		en.setCurrentState(ConversationStates.IDLE);
		bob.setQuest(sfb.getSlotName(), "start;20;1;1;5;10;10;1;1;10;20;10;20;15;1"); 
		en.step(bob, "hi");
		assertEquals("This shield can only be given to those who have killed a black giant, and without the help of others.", getReply(baldemar));
		
		
		
	}


	/**
	 * Tests for items.
	 */
@Test
	public void testItems() {
		Engine en = baldemar.getEngine();
		en.setCurrentState(ConversationStates.IDLE);
		Player jim = PlayerTestHelper.createPlayer("jim");
		
		jim.setQuest(sfb.getSlotName(), "start;0;0;0;0;0;0;0;0;0;0;0;0;0;0"); 
		
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "mithril bar", 20));
		assertTrue(PlayerTestHelper.equipWithItem(jim, "obsidian"));
		assertTrue(PlayerTestHelper.equipWithItem(jim, "diamond"));
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "emerald", 5));
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "carbuncle", 10));
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "sapphire", 10));
		assertTrue(PlayerTestHelper.equipWithItem(jim, "black shield"));
		
		assertTrue(en.step(jim, "hi"));
		assertEquals("I need a magic plate shield for the pieces and parts for your new shield.", getReply(baldemar));
		assertTrue(PlayerTestHelper.equipWithItem(jim, "magic plate shield"));
		assertTrue(jim.isEquipped("magic plate shield"));
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "gold bar", 10));
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "iron", 20));
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "black pearl", 10));
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "shuriken", 20));
		assertTrue(PlayerTestHelper.equipWithStackableItem(jim, "marbles", 15));
		assertTrue(PlayerTestHelper.equipWithItem(jim, "snowglobe"));
		
		en.setCurrentState(ConversationStates.IDLE);
		en.step(jim, "hi");
		
		assertEquals("This shield can only be given to those who have killed a black giant, and without the help of others.", getReply(baldemar));
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
}
