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

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.forest.FarmerNPC;
import games.stendhal.server.maps.semos.tavern.BowAndArrowSellerNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class BowsForOuchitTest {
	
	// better: use the one from quest and make it visible
	private static final String QUEST_SLOT = "bows_ouchit";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {

		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		
		// this is Ouchit
		new BowAndArrowSellerNPC().configureZone(zone, null);	
		// this is Karl
		new FarmerNPC().configureZone(zone, null);	
		
		AbstractQuest quest = new BowsForOuchit();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testGetWood() {
		npc = SingletonRepository.getNPCList().get("Ouchit");
		en = npc.getEngine();

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "task");
		assertEquals("Are you here to help me a bit?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Good! I sell bows and arrows. It would be great if you could bring me 10 pieces of #wood. Can you bring me the wood?", getReply(npc));
		en.step(player, "wood");
		assertEquals("Wood is a great item with many purposes. Of course you will find some pieces in a forest. Will you bring me 10 pieces?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Nice :-) Come back when you have them and say #wood.", getReply(npc));
		en.step(player, "wood");
		assertEquals("Wood is a great item with many purposes. Of course you will find some pieces in a forest. Please remember to come back when you have ten pieces for me, and say #wood.", getReply(npc));
		en.step(player, "wood");
		assertEquals("Wood is a great item with many purposes. Of course you will find some pieces in a forest. Please remember to come back when you have ten pieces for me, and say #wood.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		// check quest slot 
		assertEquals(player.getQuest(QUEST_SLOT),"wood");
		
		// test without wood
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm waiting for you to bring me 10 pieces of #wood.", getReply(npc));
		en.step(player, "wood");
		assertEquals("Wood is a great item with many purposes. Of course you will find some pieces in a forest. Please remember to come back when you have ten pieces for me, and say #wood.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		// test with wood
		PlayerTestHelper.equipWithStackableItem(player, "wood", 10);
		
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "wood");
		assertEquals("Great, now I can make new arrows. But for the bows I need bowstrings. Please go to #Karl. I know he has horses and if you tell him my name he will give you #'horse hairs' from a horsetail.", getReply(npc));
		
		// check quest slot 
		assertEquals(player.getQuest(QUEST_SLOT),"hair");
		
		en.step(player, "horse hairs");
		assertEquals("Horse hairs can be used as a bowstring. Please fetch me some from #Karl.", getReply(npc));
		en.step(player, "Karl");
		assertEquals("Karl is a farmer, east of Semos. He has many pets on his farm.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		// test without hairs or going to Karl		
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm waiting for you to bring me some #'horse hairs'.", getReply(npc));
		
		// notice a typo here done by the actual player 
		en.step(player, "hore hairs");
		assertEquals("Horse hairs can be used as a bowstring. Please fetch me some from #Karl.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		assertEquals(player.getQuest(QUEST_SLOT),"hair");
	}

	@Test
	public void testGetHairs() {
		npc = SingletonRepository.getNPCList().get("Karl");
		en = npc.getEngine();
		
		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "hair");
		
		en.step(player, "hi");
		assertEquals("Heyho! Nice to see you here at our farm.", getReply(npc));
		en.step(player, "help");
		assertEquals("You need help? I can tell you a bit about the #neighborhood.", getReply(npc));
		en.step(player, "neighborhood");

		assertEquals("In the north is a cave with bears and other creatures. If you go to the north-east you will reach after some time the great city Ados. At the east is a biiig rock. Does Balduin still live there? You want to go south-east? Well.. you can reach Ados there too, but I think the way is a bit harder.", getReply(npc));
		en.step(player, "task");
		assertEquals("I don't have time for those things, sorry. Working.. working.. working..", getReply(npc));
		
		// he doesn't seem to reply to horse hairs 
		en.step(player, "horse hairs");
		assertNull(getReply(npc));

		en.step(player, "ouchit");
		assertEquals("Hello, hello! Ouchit needs more horse hairs from my horses? No problem, here you are. Send Ouchit greetings from me.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye bye. Be careful on your way.", getReply(npc));
		
		// check quest slot and item
		assertTrue(player.isEquipped("horse hair"));
		assertEquals(player.getQuest(QUEST_SLOT),"hair");
	}

	@Test
	public void testBringHairs() {
		npc = SingletonRepository.getNPCList().get("Ouchit");
		en = npc.getEngine();
		
		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "hair");
		// nor was what the player was equipped with
		PlayerTestHelper.equipWithItem(player, "horse hair");
		
		// remember the xp and karma, did it go up?
		final int xp = player.getXP();
		final double karma = player.getKarma();
		
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm waiting for you to bring me some #'horse hairs'.", getReply(npc));
		en.step(player, "horse hairs");
		assertEquals("Yay, you got the horse hairs. Thanks a lot. Karl is really nice. Here, take this for your work. Someone left it here and I don't need those things.", getReply(npc));
		
		// [19:57] kymara earns 100 experience points.
		// check quest slot and rewards
		assertTrue(player.getQuest(QUEST_SLOT).equals("done"));
		assertTrue(player.isEquipped("scale armor"));
		assertTrue(player.isEquipped("chain legs"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(karma));
		
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		// check how he replies when the quest is finished 	
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "task");
		assertEquals("Thanks for your help. If I can #offer you anything just ask.", getReply(npc));
		en.step(player, "offer");
		assertEquals("I sell wooden bow and wooden arrow.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));	
	}
}
