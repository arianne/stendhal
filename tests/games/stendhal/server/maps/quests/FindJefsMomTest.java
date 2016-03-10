/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.fado.forest.OldWomanNPC;
import games.stendhal.server.maps.kirdneh.city.GossipNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class FindJefsMomTest extends ZonePlayerAndNPCTestImpl {

	private static final String CITY_ZONE_NAME = "0_kirdneh_city";
	private static final String FOREST_ZONE_NAME = "0_fado_forest_s";

	private static final String AMBER_TALK_GIVE_FLOWER = "Oh I see :) My son Jef asked you to take a look after me. He is such a nice and gentle boy! Please give him this zantedeschia here. I love these flowers! Please give it to him and tell him that I'm #fine.";
	private static final String AMBER_TALK_LOST_FLOWER = "Oh you lost the flower? I'm afraid I don't have anymore. Speak with Jenny, by the windmill. She may be able to help you.";
	private static final String AMBER_TALK_SEND_TO_JEF = "Please give that flower to my son and let him know that I am #fine.";
	private static final String AMBER_TALK_REJECT = "I don't trust you. Your voice shivered while you told me my sons name. I bet he is fine and happy and safe.";

	private static final String JEF_TALK_QUEST_OFFER = "I miss my mother! She wanted to go to the market but didn't return so far. Can you watch out for her please?";
	private static final String JEF_TALK_QUEST_REJECT = "Oh. Ok. I can understand you... You look like a busy hero so I'll not try to convince you of helping me out.";
	private static final String JEF_TALK_QUEST_ACCEPT = "Thank you so much! I hope that my #mum is ok and will return soon! Please tell her my name, #Jef, to prove that I sent you to her. If you have found her, return to me please and I'll give you something for your efforts.";
	private static final String JEF_TALK_QUEST_REMIND = "I hope that you will find my mum soon and tell me, if she is #fine after.";
	private static final String JEF_TALK_QUEST_OFFER_AGAIN = "It is a long time ago that you watched out for my mum. May I ask you to take a look at her again and tell me if she is still fine, please?";
	private static final String JEF_TALK_QUEST_TOO_SOON = "I don't want to disturb my mum at the moment, it seems like she needs some time on herself, so you don't have to look out for her currently. You can ask me again in";

	private static final String HISTORY_DEFAULT = "I found Jef in Kirdneh city. He waits there for his mum.";
	private static final String HISTORY_REJECTED = "Finding his mum somewhere costs me too much time at the moment, that is why I rejected his request to find her.";
	private static final String HISTORY_START = "Jef asked me to take a look at his mother Amber who didn't return from the market yet. I hope she will listen to me after I told her the name of her son, Jef.";
	private static final String HISTORY_FOUND_MOM = "I found Amber, Jef's mother, while she walked around somewhere in Fado forest. She gave me a flower for her son and told me, that I have to tell him that she is fine.";
	private static final String HISTORY_COMPLETED_REPEATABLE = "Its been a while since I checked on Jef's mother and should ask Jef, if he wants me to take a look after her again.";
	private static final String HISTORY_COMPLETED_NOT_REPEATABLE = "I told Jef that his mother is fine. He wants to leave his mother alone for some time now.";

	private SpeakerNPC npc;
	private Engine en;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(CITY_ZONE_NAME);
		setupZone(FOREST_ZONE_NAME);
	}

	public FindJefsMomTest() {
		setNpcNames("Jef", "Amber");
		setZoneForPlayer(CITY_ZONE_NAME);
		addZoneConfigurator(new GossipNPC(), CITY_ZONE_NAME);
		addZoneConfigurator(new OldWomanNPC(), FOREST_ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		quest = new FindJefsMom();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testRefuseQuest() {
		startTalkingToNpc("Jef");

		en.step(player, "quest");
		assertEquals(JEF_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "no");
		assertEquals(JEF_TALK_QUEST_REJECT, getReply(npc));

		assertEquals("rejected", player.getQuest(questSlot));
		assertFalse(npc.isTalking());
		assertLoseKarma(10);
		assertHistory(HISTORY_DEFAULT, HISTORY_REJECTED);
	}

	@Test
	public void testAcceptQuest() {
		startTalkingToNpc("Jef");

		en.step(player, "quest");
		assertEquals(JEF_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(JEF_TALK_QUEST_ACCEPT, getReply(npc));

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	@Test
	public void testTalkToAmber() {
		player.setQuest(questSlot, "start");

		startTalkingToNpc("Amber");

		en.step(player, "Jef");
		assertEquals(AMBER_TALK_GIVE_FLOWER, getReply(npc));

		assertEquals("found_mom", player.getQuest(questSlot));
		assertTrue(player.isEquipped("zantedeschia"));
		assertFalse(npc.isTalking());
		assertHistory(HISTORY_DEFAULT, HISTORY_FOUND_MOM);
	}

	@Test
	public void testTalkToAmberAgain() {
		player.setQuest(questSlot, "found_mom");
		PlayerTestHelper.equipWithItem(player, "zantedeschia");

		startTalkingToNpc("Amber");

		en.step(player, "Jef");
		assertEquals(AMBER_TALK_SEND_TO_JEF, getReply(npc));

		assertEquals("found_mom", player.getQuest(questSlot));
		assertFalse(npc.isTalking());
		assertHistory(HISTORY_DEFAULT, HISTORY_FOUND_MOM);
	}

	@Test
	public void testTalkToAmberAgainLostFlower() {
		player.setQuest(questSlot, "found_mom");

		startTalkingToNpc("Amber");

		en.step(player, "Jef");
		assertEquals(AMBER_TALK_LOST_FLOWER, getReply(npc));

		assertEquals("found_mom", player.getQuest(questSlot));
		assertFalse(npc.isTalking());
		assertHistory(HISTORY_DEFAULT, HISTORY_FOUND_MOM);
	}

	@Test
	public void testTalkToAmberFirst() {
		player.setQuest(questSlot, null);

		startTalkingToNpc("Amber");

		en.step(player, "Jef");
		assertEquals(AMBER_TALK_REJECT, getReply(npc));

		assertNull(player.getQuest(questSlot));
		assertFalse(player.isEquipped("zantedeschia"));
		assertFalse(npc.isTalking());
		assertNoHistory();
	}

	@Test
	public void testBackToJef() {
		int initialXp = player.getXP();
		PlayerTestHelper.equipWithItem(player, "zantedeschia");
		player.setQuest(questSlot, "found_mom");

		startTalkingToNpc("Jef");

		en.step(player, "fine");
		assertThat(getReply(npc), startsWith("Thank you"));

		assertFalse(player.isEquipped("zantedeschia"));
		assertTrue(player.isEquipped("red lionfish"));
		assertGainKarma(15);
		assertEquals(800, player.getXP() - initialXp);
		assertEquals("done", player.getQuest(questSlot, 0));

		en.step(player, "bye");
		assertEquals("See you around.", getReply(npc));
		assertHistory(HISTORY_DEFAULT, HISTORY_COMPLETED_NOT_REPEATABLE);
	}

	@Test
	public void testBackToJefLostFlower() {
		player.setQuest(questSlot, "found_mom");

		startTalkingToNpc("Jef");

		en.step(player, "fine");
		assertNull(getReply(npc));

		en.step(player, "task");
		assertEquals(JEF_TALK_QUEST_REMIND, getReply(npc));

		assertEquals("found_mom", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_FOUND_MOM);
	}

	@Test
	public void testAskQuestAgain() {
		player.setQuest(questSlot, 0, "done");
		PlayerTestHelper.setPastTime(player, questSlot, 1,
				TimeUnit.DAYS.toSeconds(5));

		startTalkingToNpc("Jef");

		en.step(player, "task");
		assertEquals(JEF_TALK_QUEST_OFFER_AGAIN, getReply(npc));

		assertTrue(quest.isRepeatable(player));
		assertEquals("done", player.getQuest(questSlot, 0));
		assertHistory(HISTORY_DEFAULT, HISTORY_COMPLETED_REPEATABLE);
	}

	@Test
	public void testAskQuestAgainTooSoon() {
		player.setQuest(questSlot, 0, "done");
		PlayerTestHelper.setPastTime(player, questSlot, 1,
				TimeUnit.DAYS.toSeconds(1));

		startTalkingToNpc("Jef");

		en.step(player, "task");
		assertThat(getReply(npc), startsWith(JEF_TALK_QUEST_TOO_SOON));

		assertFalse(quest.isRepeatable(player));
		assertEquals("done", player.getQuest(questSlot, 0));
		assertHistory(HISTORY_DEFAULT, HISTORY_COMPLETED_NOT_REPEATABLE);
	}

	private void startTalkingToNpc(String name) {
		npc = SingletonRepository.getNPCList().get(name);
		en = npc.getEngine();

		en.step(player, "hi");
		getReply(npc);
	}
}
