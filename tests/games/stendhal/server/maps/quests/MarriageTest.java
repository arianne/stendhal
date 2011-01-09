/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.townhall.ClerkNPC;
import games.stendhal.server.maps.fado.church.PriestNPC;
import games.stendhal.server.maps.fado.church.VergerNPC;
import games.stendhal.server.maps.fado.city.NunNPC;
import games.stendhal.server.maps.fado.dressingrooms.BrideAssistantNPC;
import games.stendhal.server.maps.fado.dressingrooms.GroomAssistantNPC;
import games.stendhal.server.maps.fado.hotel.GreeterNPC;
import games.stendhal.server.maps.fado.weaponshop.RingSmithNPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class MarriageTest {

	private static final String QUEST_SLOT = "marriage";
	private static Player player = null;
	private static Player player2 = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("int_fado_lovers_room_2")); 
		new PriestNPC().configureZone(zone, null);
		new VergerNPC().configureZone(zone, null);
		new NunNPC().configureZone(zone, null);
		new RingSmithNPC().configureZone(zone, null);
		new BrideAssistantNPC().configureZone(zone, null);
		new GroomAssistantNPC().configureZone(zone, null);
		new GreeterNPC().configureZone(zone, null);
		new ClerkNPC().configureZone(zone, null);

		final AbstractQuest quest = new Marriage();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayerWithOutFit("player");
		player2 = PlayerTestHelper.createPlayerWithOutFit("player2");

		zone.add(player);
		player.setPosition(52, 53);
		zone.add(player2);
		player2.setPosition(53, 53);
		MockStendhalRPRuleProcessor.get().addPlayer(player2);
	}

	@AfterClass
	public static void afterClass() {
		PlayerTestHelper.removePlayer(player);
		PlayerTestHelper.removePlayer(player2);
	}

	/**
	 * Tests for gettingEngaged.
	 */
	@Test
	public void testGettingEngaged() {
		player.removeQuest(QUEST_SLOT);
		player2.removeQuest(QUEST_SLOT);

		// **in front of church**
		npc = SingletonRepository.getNPCList().get("Sister Benedicta");
		en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to this place of worship.", getReply(npc));
		assertTrue(en.step(player, "help"));
		assertEquals("I don't know what you need, dear child.", getReply(npc));
		assertTrue(en.step(player, "engage"));
		assertEquals("You have to tell me who you want to marry.", getReply(npc));
		assertTrue(en.step(player, "engage player2"));
		assertEquals("player, do you want to get engaged to player2?", getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals("player2, do you want to get engaged to player?", getReply(npc));
		assertTrue(en.step(player2, "yes"));
		assertEquals("Congratulations, player and player2, you are now engaged! Please make sure you have been to Ognir to get wedding rings made before you go to the church for the service. And here are some invitations you can give to your guests.", getReply(npc));
		assertTrue(en.step(player, "bye"));
		assertEquals("Goodbye, may peace be with you.", getReply(npc));

		assertEquals("engaged", player.getQuest(QUEST_SLOT));
		assertEquals("engaged", player2.getQuest(QUEST_SLOT));
	}

	/**
	 * Tests for orderWeddingRing.
	 */
	@Test
	public void testOrderWeddingRing() {
		player.setQuest(QUEST_SLOT, "engaged");
		player2.setQuest(QUEST_SLOT, "engaged");

		// **at ringsmith**
		npc = SingletonRepository.getNPCList().get("Ognir");
		en = npc.getEngine();
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", getReply(npc));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		assertTrue(en.step(player, "task"));
		assertEquals("Well, you could consider getting married to be a quest! Ask me about #'wedding rings' if you need one.", getReply(npc));
		assertTrue(en.step(player, "wedding"));
		assertEquals("I need 10 gold bars and a fee of 500 money, to make a wedding ring for your fiancee. Do you have it?", getReply(npc));
		assertTrue(en.step(player, "yes"));
		assertEquals("Come back when you have both the money and the gold.", getReply(npc));
		assertTrue(en.step(player, "bye"));

		// -----------------------------------------------

		Item item = ItemTestHelper.createItem("money", 5000);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("gold bar", 10);
		player.getSlot("bag").add(item);
		
		en.step(player, "hi");
		en.step(player, "task");
		assertEquals("Well, you could consider getting married to be a quest! Ask me about #'wedding rings' if you need one.", getReply(npc));
		en.step(player, "wedding");
		assertEquals("I need 10 gold bars and a fee of 500 money, to make a wedding ring for your fiancee. Do you have it?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Good, come back in 10 minutes and it will be ready. Goodbye until then.", getReply(npc));
		en.step(player, "bye");

		assertTrue(player.getQuest(QUEST_SLOT).startsWith("forging"));
		assertEquals("engaged", player2.getQuest(QUEST_SLOT));

		// -----------------------------------------------

		item = ItemTestHelper.createItem("money", 5000);
		player2.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("gold bar", 10);
		player2.getSlot("bag").add(item);

		en.step(player2, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(player2, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		en.step(player2, "wedding");
		assertEquals("I need 10 gold bars and a fee of 500 money, to make a wedding ring for your fiancee. Do you have it?", getReply(npc));
		en.step(player2, "yes");
		assertEquals("Good, come back in 10 minutes and it will be ready. Goodbye until then.", getReply(npc));
		en.step(player2, "bye");

		assertTrue(player.getQuest(QUEST_SLOT).startsWith("forging"));
		assertTrue(player2.getQuest(QUEST_SLOT).startsWith("forging"));
	}

	/**
	 * Tests for buySuitForGroom.
	 */
	@Test
	public void testBuySuitForGroom() {
		// **at hotel's dressing room**
		npc = SingletonRepository.getNPCList().get("Timothy");
		en = npc.getEngine();

		player.setQuest(QUEST_SLOT, "engaged");

		en.step(player, "hi");
		assertEquals("Good day! If you're a prospective groom I can #help you prepare for your wedding.", getReply(npc));
		en.step(player, "help");
		assertEquals("Please tell me if you want to #'wear a suit' for your wedding.", getReply(npc));
		en.step(player, "wear");
		assertEquals("To wear a suit will cost 50. Do you want to wear it?", getReply(npc));
		PlayerTestHelper.equipWithMoney(player, 50);
		en.step(player, "yes");
		assertEquals("Thanks, and please don't forget to #return it when you don't need it anymore!", getReply(npc));
		en.step(player2, "bye");
		assertEquals("Good bye, I hope everything goes well for you.", getReply(npc));
		
		// now test that once you are married you cannot get the outfit again
		player.setQuest(QUEST_SLOT, "just_married");
		en.step(player, "hi");
		assertEquals("Sorry, I can't help you, I am busy pressing suits.", getReply(npc));
	}

	/**
	 * Tests for buyGownForBride.
	 */
	@Test
	public void testBuyGownForBride() {
		// **at hotel's dressing room**

		npc = SingletonRepository.getNPCList().get("Tamara");
		en = npc.getEngine();
		
		player2.setQuest(QUEST_SLOT, "engaged");
		en.step(player2, "hi");
		assertEquals("Welcome! If you're a bride-to-be I can #help you get ready for your wedding", getReply(npc));
		en.step(player2, "help");
		assertEquals("Just tell me if you want to #'wear a gown' for your wedding.", getReply(npc));
		en.step(player2, "wear a gown");
		assertEquals("To wear a gown will cost 100. Do you want to wear it?", getReply(npc));
		en.step(player2, "yes");
		assertEquals("Thanks, and please don't forget to #return it when you don't need it anymore!", getReply(npc));
		en.step(player2, "bye");
		assertEquals("Have a lovely time!", getReply(npc));

		// now test that once you are married you cannot get the outfit again
		player2.setQuest(QUEST_SLOT, "just_married");
		en.step(player, "hi");
		assertEquals("Sorry, I can't help you, I am busy getting dresses ready for brides-to-be!", getReply(npc));
	}

	/**
	 * Tests for fetchOrderedWeddingRings.
	 */
	@Test
	public void testFetchOrderedWeddingRings() {
		npc = SingletonRepository.getNPCList().get("Ognir");
		en = npc.getEngine();

		player.setQuest("marriage", "forging;" + Long.MAX_VALUE);
		player2.setQuest("marriage", "forging;" + Long.MAX_VALUE);
		
		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(player, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		en.step(player, "wedding");
		assertTrue(getReply(npc).startsWith("I haven't finished making the wedding ring. Please check back"));
		en.step(player, "bye");

		// Jump relatively forward in time (by pushing the past events to the beginning of time
		
		assertTrue(player.getQuest("marriage").startsWith("forging;"));
		assertTrue(player2.getQuest("marriage").startsWith("forging;"));
		player.setQuest("marriage", "forging;1");
		player2.setQuest("marriage", "forging;1");
		
		
		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(player, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		en.step(player, "wedding");
		assertEquals("I'm pleased to say, the wedding ring for your fiancee is finished! Make sure one is made for you, too! *psst* just a little #hint for the wedding day ...", getReply(npc));
		// [14:25] player earns 500 experience points.
		en.step(player, "hint");
		assertEquals("When my wife and I got married we went to Fado hotel and hired special clothes. The dressing rooms are on your right when you go in, look for the wooden door. Good luck!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, my friend.", getReply(npc));

		en.step(player2, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(player2, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		en.step(player2, "wedding");
		assertEquals("I'm pleased to say, the wedding ring for your fiancee is finished! Make sure one is made for you, too! *psst* just a little #hint for the wedding day ...", getReply(npc));
		// [14:26] player2 earns 500 experience points.StendhalRPRuleProcessor.get()
		en.step(player2, "bye");
		assertEquals("Bye, my friend.", getReply(npc));

		assertEquals("engaged_with_ring", player.getQuest(QUEST_SLOT));
		assertEquals("engaged_with_ring", player2.getQuest(QUEST_SLOT));
		// -----------------------------------------------

		// **player drops ring of life**
		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", getReply(npc));
		en.step(player, "help");
		assertEquals("I am an expert on #'wedding rings' and #'emerald rings', sometimes called the ring of #life.", getReply(npc));
		en.step(player, "wedding");
		assertEquals("Looking forward to your wedding? Make sure your fiancee gets a wedding ring made for you, too! Oh and remember to get #dressed up for the big day.", getReply(npc));
		en.step(player, "dressed");
		assertEquals("When my wife and I got married we went to Fado hotel and hired special clothes. The dressing rooms are on your right when you go in, look for the wooden door. Good luck!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, my friend.", getReply(npc));

		assertEquals("engaged_with_ring", player.getQuest(QUEST_SLOT));
		assertEquals("engaged_with_ring", player2.getQuest(QUEST_SLOT));
	}

	/**
	 * Tests for preWeddingTalk.
	 */
	@Test
	public void testPreWeddingTalk() {
		// **inside church**

		player.setQuest("marriage", "engaged_with_ring");
		player2.setQuest("marriage", "engaged_with_ring");

		npc = SingletonRepository.getNPCList().get("Lukas");
		en = npc.getEngine();
		en.step(player2, "hi");
		assertEquals("Welcome to this place of worship. Are you here to be #married?", getReply(npc));
		en.step(player2, "married");
		assertEquals("If you want to be engaged, speak to Sister Benedicta. She'll make sure the priest knows about your plans.", getReply(npc));
		en.step(player2, "task");
		assertEquals("I have eveything I need. But it does bring me pleasure to see people #married.", getReply(npc));

		assertEquals("engaged_with_ring", player.getQuest(QUEST_SLOT));
		assertEquals("engaged_with_ring", player2.getQuest(QUEST_SLOT));
	}

	/**
	 * Tests for wedding.
	 */
	@Test
	public void testWedding() {
		// **inside church**

		player.setQuest("marriage", "engaged_with_ring");
		player2.setQuest("marriage", "engaged_with_ring");

		npc = SingletonRepository.getNPCList().get("Priest");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Welcome to the church!", getReply(npc));
		en.step(player, "help");
		assertEquals("I can help you #marry your loved one. But you must be engaged under the supervision of Sister Benedicta, and have a #ring to give your partner.", getReply(npc));
		en.step(player, "ring");
		assertEquals("Once you are engaged, you can go to Ognir who works here in Fado to get your wedding rings made. I believe he also sells engagement rings, but they are purely for decoration. How wanton!", getReply(npc));
		en.step(player, "marry");
		assertEquals("You have to tell me who you want to marry.", getReply(npc));
		en.step(player, "marry player2");
		assertEquals("You must step in front of the altar if you want to marry.", getReply(npc));

		player.setPosition(10, 9);
		player2.setPosition(11, 9);

		en.step(player, "marry player2");
		assertEquals("player, do you really want to marry player2?", getReply(npc));
		en.step(player, "no");
		assertEquals("What a pity! Goodbye!", getReply(npc));

		en.step(player, "hi");
		assertEquals("Welcome to the church!", getReply(npc));
		en.step(player, "marry player2");
		assertEquals("player, do you really want to marry player2?", getReply(npc));
		en.step(player, "yes");
		assertEquals("player2, do you really want to marry player?", getReply(npc));
		en.step(player2, "yes");
		assertEquals("Congratulations, player and player2, you are now married! I don't really approve of this, but if you would like a honeymoon, go ask Linda in the hotel. Just say 'honeymoon' to her and she will understand.", getReply(npc));
		en.step(player2, "bye");

		en.step(player2, "hi");
		assertEquals("Welcome to the church!", getReply(npc));
		en.step(player2, "marry");
		en.step(player2, "bye");
		assertEquals("Go well, and safely.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Welcome to the church!", getReply(npc));
		en.step(player, "help");
		assertEquals("I can help you #marry your loved one. But you must be engaged under the supervision of Sister Benedicta, and have a #ring to give your partner.", getReply(npc));
		en.step(player, "marry");
		en.step(player, "bye");

		assertEquals("just_married", player.getQuest(QUEST_SLOT));
		assertEquals("just_married", player2.getQuest(QUEST_SLOT));
	}

	/**
	 * Tests for honeymoon.
	 */
	@Test
	public void testHoneymoon() {
		// **inside hotel**

		player.setQuest("marriage", "just_married");
		player2.setQuest("marriage", "just_married");

		npc = SingletonRepository.getNPCList().get("Linda");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Hello! Welcome to the Fado City Hotel! Can I #help you?", getReply(npc));
		en.step(player, "help");
		assertEquals("When the building work on the hotel rooms is complete you will be able to #reserve one.", getReply(npc));
		en.step(player, "honeymoon");
		assertEquals("Could you get player2 to come to the reception desk, please. Then please read our catalogue here and tell me the room number that you would like.", getReply(npc));
		// [14:34] You read:
		// "0. Blue Paradise - with a flaming bed
		// 1. Windy Love - be blown away
		// 2. Literary Haven - bathe, read and relax
		// 3. Heart of Flowers - a floral masterpiece
		// 4. A Gothic Romance - the room for mystics
		// 5. Gourmet Delight - a room for food lovers
		// 6. Forest Fantasy - for natural lovers
		// 7. The Cold Love - feel the chill
		// 8. Waterfall Wonder - splash, or admire
		// 9. Wooden Delicacy - go back to your roots
		// 10. Simple Serenity - a room of calm
		// 11. Water of Love - wine flows freely
		// 12. Stone Hearted - an architect's delight
		// 13. Blue For You - azure, lapis, cobalt and cornflower
		// 14. Rhapsody in pink - romantic yet regal
		// 15. Femme Fatale - feminine and frivolous

		// -----------------------------------------------

		assertFalse(en.step(player, "x"));
		assertFalse(en.step(player, "0"));
		assertFalse(en.step(player, "999"));

		// If you're looking for a honeymoon room, just say the room number you desire
		// For example say:  11  if you want the room called Water of Love."

		assertTrue(en.step(player, "two"));

		assertEquals("Linda tells you: Use the scroll in your bag to return to the hotel, our special honeymoon suites are so private that they don't use normal entrances and exits!", PlayerTestHelper.getPrivateReply(player));
		assertEquals("Linda tells you: Use the scroll in your bag to return to the hotel, our special honeymoon suites are so private that they don't use normal entrances and exits!", PlayerTestHelper.getPrivateReply(player2));

		assertEquals("done", player.getQuest(QUEST_SLOT));
		assertEquals("done", player2.getQuest(QUEST_SLOT));
	} 


	/**
	 * Tests for divorcing.
	 */
	@Test
	public void testDivorce() {
		player.setQuest("marriage", "just_married");
		player2.setQuest("marriage", "just_married");

		// Both players should carry exactly one wedding ring with them.
		if (!(player.isEquipped("wedding ring"))) {
			PlayerTestHelper.equipWithItem(player, "wedding ring");
		}
		if (!(player2.isEquipped("wedding ring"))) {
			PlayerTestHelper.equipWithItem(player2, "wedding ring");
		}

		npc = SingletonRepository.getNPCList().get("Wilfred");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello. If I can help you, just say the word.", getReply(npc));

		en.step(player, "help");
		assertEquals("I can #divorce people, if they are unhappily married.", getReply(npc));

		en.step(player, "divorce");
		assertEquals("I see you haven't been on your honeymoon yet. Are you sure you want to divorce so soon?", getReply(npc));

		en.step(player, "no");
		assertEquals("I hope you have a happy marriage, then.", getReply(npc));

		assertTrue(player.drop("wedding ring"));
		en.step(player, "divorce");
		assertEquals("I apologise, but I need your wedding ring in order to divorce you. If you have lost yours, you can go to Ognir to make another.", getReply(npc));

		PlayerTestHelper.equipWithItem(player, "wedding ring");
		en.step(player, "divorce");
		assertEquals("I see you haven't been on your honeymoon yet. Are you sure you want to divorce so soon?", getReply(npc));

		en.step(player, "yes");
		assertEquals("What a pity...what a pity...and you two were married so happily, too...", getReply(npc));
		assertEquals("player has divorced from you.", PlayerTestHelper.getPrivateReply(player2));
		assertFalse(player.isEquipped("wedding ring"));
		assertFalse(player2.isEquipped("wedding ring"));

		en.step(player, "divorce");
		assertEquals("You're not even married. Stop wasting my time!", getReply(npc));

		en.step(player, "bye");
		assertEquals("Good day.", getReply(npc));
	}

}
