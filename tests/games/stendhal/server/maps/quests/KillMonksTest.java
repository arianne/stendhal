/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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
import games.stendhal.server.maps.ados.city.ManWithHatNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class KillMonksTest extends ZonePlayerAndNPCTestImpl {

	private static final String CITY_ZONE_NAME = "0_ados_city_s";

	private static final String NPC_TALK_QUEST_OFFER = "My lovely wife was killed when she went to Wo'fol to order some freshmade pizza by Kroip. Some monks stepped into her way and she had no chance. Now I want revenge! May you help me?";
	private static final String NPC_TALK_QUEST_REJECT = "That is a pity... Maybe you'll change your mind soon and help a sad man then.";
	private static final String NPC_TALK_QUEST_ACCEPT = "Thank you! Please kill 25 monks and 25 darkmonks in the name of my beloved wife.";
	private static final String NPC_TALK_QUEST_REMIND = "Please help me with reaching my goal of taking revenge!";
	private static final String NPC_TALK_QUEST_OFFER_AGAIN = "Those monks are cruel and I still didn't get my revenge. May you help me again please?";
	private static final String NPC_TALK_QUEST_TOO_SOON = "These monks learned their lesson for now but I could need your help again in";

	private static final String NPC_TALK_BYE = "Goodbye, thank you for talking with me.";

	private static final String HISTORY_DEFAULT = "I met Andy in Ados city. He asked me to get revenge for his wife.";
	private static final String HISTORY_REJECTED = "I rejected his request.";
	private static final String HISTORY_START = "I promised to kill 25 monks and 25 darkmonks to get revenge for Andy's wife.";
	private static final String HISTORY_STATUS = "I have killed ";
	private static final String HISTORY_COMPLETED_REPEATABLE = "Now, after more than two weeks, I should check on Andy again. Maybe he needs my help!";
	private static final String HISTORY_COMPLETED_NOT_REPEATABLE = "I've killed some monks and Andy finally can sleep a bit better!";
	private static final String HISTORY_COMPLETED_ONCE = "I have taken revenge for Andy 1 time now.";

	private static final String QUEST_STATE_JUST_STARTED = "start;darkmonk,0,25,0,0,monk,0,25,0,0";

	private SpeakerNPC npc;
	private Engine en;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(CITY_ZONE_NAME);
	}

	public KillMonksTest() {
		setNpcNames("Andy");
		setZoneForPlayer(CITY_ZONE_NAME);
		addZoneConfigurator(new ManWithHatNPC(), CITY_ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		quest = new KillMonks();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testRefuseQuest() {
		startTalkingToNpc("Andy");

		en.step(player, "quest");
		assertEquals(NPC_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "no");
		assertEquals(NPC_TALK_QUEST_REJECT, getReply(npc));

		assertEquals("rejected", player.getQuest(questSlot));
		assertTrue(npc.isTalking());
		assertLoseKarma(5);
		assertHistory(HISTORY_DEFAULT, HISTORY_REJECTED);
	}

	@Test
	public void testAcceptQuest() {
		startTalkingToNpc("Andy");

		en.step(player, "quest");
		assertEquals(NPC_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(NPC_TALK_QUEST_ACCEPT, getReply(npc));

		assertEquals("start", player.getQuest(questSlot, 0));
		assertGainKarma(5);
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_STATUS + "0 monks and 0 darkmonks.");
	}

	@Test
	public void testBackToNpc() {
		int initialXp = player.getXP();

		player.setQuest(questSlot, QUEST_STATE_JUST_STARTED);
		killCreatureShared("monk", 25);
		killCreatureShared("darkmonk", 25);

		startTalkingToNpc("Andy");

		en.step(player, "task");
		assertThat(getReply(npc), startsWith("Thank you"));

		assertTrue(player.isEquipped("soup"));
		assertGainKarma(0);
		assertEquals(15000, player.getXP() - initialXp);
		assertEquals("killed", player.getQuest(questSlot, 0));

		en.step(player, "bye");
		assertEquals(NPC_TALK_BYE, getReply(npc));
		assertHistory(HISTORY_DEFAULT, HISTORY_COMPLETED_NOT_REPEATABLE, HISTORY_COMPLETED_ONCE);
	}

	@Test
	public void testBackToNpcTooSoon() {
		player.setQuest(questSlot, QUEST_STATE_JUST_STARTED);
		killCreatureShared("monk", 1);
		killCreatureShared("darkmonk", 2);

		startTalkingToNpc("Andy");

		en.step(player, "task");
		assertEquals(NPC_TALK_QUEST_REMIND, getReply(npc));

		assertEquals(QUEST_STATE_JUST_STARTED, player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_STATUS + "1 monk and 2 darkmonks.");
	}

	@Test
	public void testAskQuestAgain() {
		player.setQuest(questSlot, 0, "killed");
		PlayerTestHelper.setPastTime(player, questSlot, 1, TimeUnit.DAYS.toSeconds(14));

		startTalkingToNpc("Andy");

		en.step(player, "task");
		assertEquals(NPC_TALK_QUEST_OFFER_AGAIN, getReply(npc));

		assertTrue(quest.isRepeatable(player));
		assertEquals("killed", player.getQuest(questSlot, 0));
		assertHistory(HISTORY_DEFAULT, HISTORY_COMPLETED_REPEATABLE);
	}

	@Test
	public void testAskQuestAgainTooSoon() {
		player.setQuest(questSlot, 0, "killed");
		PlayerTestHelper.setPastTime(player, questSlot, 1, TimeUnit.DAYS.toSeconds(1));

		startTalkingToNpc("Andy");

		en.step(player, "task");
		assertThat(getReply(npc), startsWith(NPC_TALK_QUEST_TOO_SOON));

		assertFalse(quest.isRepeatable(player));
		assertEquals("killed", player.getQuest(questSlot, 0));
		assertHistory(HISTORY_DEFAULT, HISTORY_COMPLETED_NOT_REPEATABLE);
	}

	private void startTalkingToNpc(String name) {
		npc = SingletonRepository.getNPCList().get(name);
		en = npc.getEngine();

		en.step(player, "hi");
		getReply(npc);
	}

	private void killCreatureShared(String creatureName, int count) {
		for (int i = 0; i < count; i++) {
			player.setSharedKill(creatureName);
		}
	}
}
