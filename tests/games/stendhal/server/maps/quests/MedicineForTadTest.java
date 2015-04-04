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
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.hostel.BoyNPC;
import games.stendhal.server.maps.semos.temple.HealerNPC;
import games.stendhal.server.maps.semos.townhall.DecencyAndMannersWardenNPC;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class MedicineForTadTest extends ZonePlayerAndNPCTestImpl {

	private static final String HOSTEL_ZONE_NAME = "int_semos_hostel";
	private static final String TEMPLE_ZONE_NAME = "int_semos_temple";
	private static final String TOWNHALL_ZONE_NAME = "int_semos_townhall";

	private static final String QUEST_SLOT      = "introduce_players";
	private static final String SSSHH_COME_HERE = "Ssshh! Come here, player! I have a #task for you.";
	
	private static final String KETTEH_BYE_INTRODUCES_TAD = "Farewell. Have you met Tad, in the hostel? If you get a chance, please check in on him. I heard he was not feeling well. You can find the hostel in Semos village, close to Nishiya.";
	private static final String KETTEH_BYE_REMINDS_OF_TAD = "Goodbye. Don't forget to check on Tad. I hope he's feeling better.";
	
	private static final String TAD_TALK_QUEST_OFFER = "I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?";
	private static final String TAD_TALK_QUEST_ACCEPT = "Great! Please go as quickly as you can. *sneeze*";
	private static final String TAD_TALK_QUEST_REFUSE = "Oh, please won't you change your mind? *sneeze*";
	private static final String TAD_TALK_SEND_TO_ILISA = "Ok, you got the flask! Here take this money to cover your expense. Now, I need you to take it to #Ilisa... she'll know what to do next.";
	
	private static final String HISTORY_DEFAULT = "I have met Tad in Semos Hostel.";
	private static final String HISTORY_START = "He asked me to buy a flask from Margaret in Semos Tavern.";

	private Player player;
	private SpeakerNPC npc;
	private Engine en;

	private AbstractQuest quest;
	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(HOSTEL_ZONE_NAME);
		setupZone(TEMPLE_ZONE_NAME);
		setupZone(TOWNHALL_ZONE_NAME);
	}

	public MedicineForTadTest() {
		super(HOSTEL_ZONE_NAME, "Tad");
	}

	@Before
	public void setUp() {
		StendhalRPZone hostelZone = new StendhalRPZone(HOSTEL_ZONE_NAME);
		new BoyNPC().configureZone(hostelZone, null);
		
		StendhalRPZone templeZone = new StendhalRPZone(TEMPLE_ZONE_NAME);
		new HealerNPC().configureZone(templeZone, null);
		
		StendhalRPZone townhallZone = new StendhalRPZone(TOWNHALL_ZONE_NAME);
		new DecencyAndMannersWardenNPC().configureZone(townhallZone, null);

		quest = new MedicineForTad();
		quest.addToWorld();
		new MeetKetteh().addToWorld();
		
		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("player");
		player.setQuest("TadFirstChat", "done");
	}
	
	@After
	public void tearDown() {
		en.step(player, "bye");
	}

	private String startTalkingToNpc(String name) {
		npc = SingletonRepository.getNPCList().get(name);
		en = npc.getEngine();

		en.step(player, "hi");
		return getReply(npc);
	}
	
	@Test
	public void testKettehIntroducesTad() {
		player.setQuest(questSlot, null);

		startTalkingToNpc("Ketteh Wehoh");

		en.step(player, "bye");
		assertEquals(KETTEH_BYE_INTRODUCES_TAD, getReply(npc));
	}
	
	@Test
	public void testKettehRemindsOfTad() {
		player.setQuest(questSlot, "start");

		startTalkingToNpc("Ketteh Wehoh");
		
		en.step(player, "bye");
		assertEquals(KETTEH_BYE_REMINDS_OF_TAD, getReply(npc));
	}

	@Test
	public void testAcceptQuest() {
		player.setQuest(questSlot, null);

		String firstReply = startTalkingToNpc("Tad");
		assertEquals(SSSHH_COME_HERE, firstReply);

		en.step(player, "quest");
		assertEquals(TAD_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(TAD_TALK_QUEST_ACCEPT, getReply(npc));

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}
	
	@Test
	public void testRefuseQuest() {
		player.setQuest(questSlot, null);

		String firstReply = startTalkingToNpc("Tad");
		assertEquals(SSSHH_COME_HERE, firstReply);

		en.step(player, "quest");
		assertEquals(TAD_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "no");
		assertEquals(TAD_TALK_QUEST_REFUSE, getReply(npc));

		assertEquals(null, player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT);
	}
	
	@Test
	public void testBackToTadWithFlask() {
		player.setQuest(questSlot, "start");
		PlayerTestHelper.equipWithItem(player, "flask");

		String firstReply = startTalkingToNpc("Tad");

		assertEquals(TAD_TALK_SEND_TO_ILISA, firstReply);
		assertTrue(player.hasQuest(QUEST_SLOT));
		assertEquals("ilisa", player.getQuest(QUEST_SLOT));
	}
	
	@Test
	public void testGoToIlisaWithFlask() {
		player.setQuest(questSlot, "ilisa");
		PlayerTestHelper.equipWithItem(player, "flask");

		String firstReply = startTalkingToNpc("Ilisa");

		assertEquals("Ah, I see you have that flask. #Tad needs medicine, right? Hmm... I'll need a #herb. Can you help?",
					firstReply);
		en.step(player, "yes");
		assertEquals("North of Semos, near the tree grove, grows a herb called arandula. Here is a picture I drew so you know what to look for.",getReply(npc));
		assertEquals("corpse&herbs", player.getQuest(QUEST_SLOT));

		en.step(player, "tad");
		assertEquals("He needs a very powerful potion to heal himself. He offers a good reward to anyone who will help him.", getReply(npc));
	}
	
	@Test
	public void testBackToTadWithoutPotion() {
		player.setQuest(questSlot, "corpse&herbs");
		PlayerTestHelper.equipWithItem(player, "flask");

		String firstReply = startTalkingToNpc("Tad");

		assertEquals("*sniff* *sniff* I still feel ill, please hurry with that #favour for me.", firstReply);
	}
	
	@Test
	public void testBackToIlisaWithoutHerb() {
		player.setQuest(questSlot, "corpse&herbs");
		PlayerTestHelper.equipWithItem(player, "flask");

		String firstReply = startTalkingToNpc("Ilisa");

		assertEquals("Can you fetch those #herbs for the #medicine?", firstReply);
	}

	@Test
	public void testBackToIlisaWithHerb() {
		player.setQuest(questSlot, "corpse&herbs");
		PlayerTestHelper.equipWithItem(player, "flask");
		PlayerTestHelper.equipWithItem(player, "arandula");

		String firstReply = startTalkingToNpc("Ilisa");

		assertEquals(
				"Okay! Thank you. Now I will just mix these... a pinch of this... and a few drops... there! Can you ask #Tad to stop by and collect it? I want to see how he's doing.",
				firstReply);
		
		assertEquals("potion", player.getQuest(QUEST_SLOT));
	}

	@Test
	public void testBackToTadWithPotion() {
		player.setQuest(questSlot, "potion");

		String firstReply = startTalkingToNpc("Tad");

		assertEquals("Thanks! I will go talk with #Ilisa as soon as possible.", firstReply);
		assertEquals("done", player.getQuest(QUEST_SLOT));
	}
	
	@Test
	public void testKettehDoesNotMentionTad() {
		player.setQuest(questSlot, "done");

		startTalkingToNpc("Ketteh Wehoh");

		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	private void assertHistory(String... entries) {
		assertEquals(Arrays.asList(entries), quest.getHistory(player));
	}
}
