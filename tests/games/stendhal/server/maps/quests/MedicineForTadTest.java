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
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.hostel.BoyNPC;
import games.stendhal.server.maps.semos.temple.HealerNPC;
import games.stendhal.server.maps.semos.townhall.DecencyAndMannersWardenNPC;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.ItemTestHelper;

public class MedicineForTadTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME       = "testzone";
	private static final String QUEST_SLOT      = "introduce_players";
	private static final String SSSHH_COME_HERE = "Ssshh! Come here, player! I have a #task for you.";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		ItemTestHelper.generateRPClasses();

		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();

		setupZone(ZONE_NAME, new BoyNPC());
		setupZone(ZONE_NAME, new HealerNPC());
		setupZone(ZONE_NAME, new DecencyAndMannersWardenNPC());

		new MedicineForTad().addToWorld();
		new MeetKetteh().addToWorld();
	}

	public MedicineForTadTest() {
		super(ZONE_NAME, "Tad", "Ilisa", "Ketteh Wehoh");
	}

	/**
	 * Tests for hiAndbye.
	 */
	@Test
	public void testHiAndbye() {
		final SpeakerNPC npc = getNPC("Tad");
		final Engine en = npc.getEngine();
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());
		assertEquals(SSSHH_COME_HERE, getReply(npc));
		en.step(player, "task");
		assertTrue(npc.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				getReply(npc));
		en.step(player, "flask");
		assertTrue(npc.isTalking());
		assertEquals("You could probably get a flask from #Margaret.", getReply(npc));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for hiNoAndHiAgain.
	 */
	@Test
	public void testHiNoAndHiAgain() {
		final SpeakerNPC npc = getNPC("Tad");
		final Engine en = npc.getEngine();
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());

		en.step(player, "task");
		assertTrue(npc.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				getReply(npc));
		en.step(player, "No");
		assertTrue(npc.isTalking());
		assertEquals("Oh, please won't you change your mind? *sneeze*", getReply(npc));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));
		assertEquals("Bye.", getReply(npc));
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());
		assertEquals(MedicineForTadTest.SSSHH_COME_HERE, getReply(npc));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		final SpeakerNPC tad          = getNPC("Tad");
		final Engine     engineTad    = tad.getEngine();
		final SpeakerNPC ketteh       = getNPC("Ketteh Wehoh");
		final Engine     engineKetteh = ketteh.getEngine();

		// before quest starts, ketteh will ask if you've met TAd
		engineKetteh.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		engineKetteh.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertEquals("Farewell. Have you met Tad, in the hostel? If you get a chance, please check in on him. I heard he was not feeling well. You can find the hostel in Semos village, close to Nishiya.", 
				     getReply(ketteh));

		
		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());
		assertEquals(SSSHH_COME_HERE, getReply(tad));
		engineTad.step(player, "task");
		assertTrue(tad.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				getReply(tad));
		engineTad.step(player, "yes");
		assertTrue(player.hasQuest(QUEST_SLOT));
		engineTad.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(tad.isTalking());
		assertEquals("Bye.", getReply(tad));
		
		// don't be naked when talking to ketteh and trying to do this quest
        player.setOutfit(Outfit.getRandomOutfit());
        
		// quest started but not complete - ketteh will remind player
		engineKetteh.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		engineKetteh.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertEquals("Goodbye. Don't forget to check on Tad. I hope he's feeling better.", getReply(ketteh));
		
		
		final StackableItem flask = new StackableItem("flask", "", "", null);
		flask.setQuantity(1);
		flask.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(flask);
		assertTrue(player.isEquipped("flask"));
		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());
		assertEquals(
				"Ok, you got the flask! Here take this money to cover your expense. Now, I need you to take it to #Ilisa... she'll know what to do next.",
				getReply(tad));
		assertTrue(player.hasQuest(QUEST_SLOT));
		assertEquals("ilisa", player.getQuest(QUEST_SLOT));
		engineTad.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));

		final SpeakerNPC ilisa = getNPC("Ilisa");
		final Engine engineIlisa = ilisa.getEngine();
		engineIlisa.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		
		assertEquals("Ah, I see you have that flask. #Tad needs medicine, right? Hmm... I'll need a #herb. Can you help?",
					getReply(ilisa));
		engineIlisa.step(player, "yes");
		assertEquals("North of Semos, near the tree grove, grows a herb called arandula. Here is a picture I drew so you know what to look for.",getReply(ilisa));
		assertEquals("corpse&herbs", player.getQuest(QUEST_SLOT));
		engineIlisa.step(player, "tad");
		assertEquals("He needs a very powerful potion to heal himself. He offers a good reward to anyone who will help him.", getReply(ilisa));
		engineIlisa.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertEquals("Bye.", getReply(ilisa));

		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());

		assertEquals("Tad has already asked and the quest was accepted",
				"*sniff* *sniff* I still feel ill, please hurry with that #favour for me.", getReply(tad));
		engineTad.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(tad.isTalking());
		assertEquals("Bye.", getReply(tad));

		engineIlisa.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Can you fetch those #herbs for the #medicine?", getReply(ilisa));
		engineIlisa.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertEquals("Bye.", getReply(ilisa));
		
		// doesn't work as it causes an npe and adding it to setUp() doesn't help
		//PlayerTestHelper.equipWithItem(player, "arandula");
		final StackableItem arandula = new StackableItem("arandula", "", "", null);
		arandula.setQuantity(1);
		arandula.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(arandula);
		engineIlisa.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals(
				"Okay! Thank you. Now I will just mix these... a pinch of this... and a few drops... there! Can you ask #Tad to stop by and collect it? I want to see how he's doing.",
				getReply(ilisa));
		engineIlisa.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertEquals("Bye.", getReply(ilisa));
		
		assertEquals("potion", player.getQuest(QUEST_SLOT));
		
		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Thanks! I will go talk with #Ilisa as soon as possible.", getReply(tad));
		assertEquals("done", player.getQuest(QUEST_SLOT));
		
		// quest complete.  ketteh no longer reminds player
		engineKetteh.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		engineKetteh.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertEquals("Bye.", getReply(ketteh));
		
	}

}
