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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.semos.city.RudolphNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class GoodiesForRudolphTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_SEMOS = "0_semos_city";

	private static final String NPC_RUDOLPH = "Rudolph";

	private static final String ITEM_APPLE = "apple";
	private static final String ITEM_CARROT = "carrot";
	private static final String ITEM_MONEY = "money";
	private static final String ITEM_REINDEER_MOSS = "reindeer moss";
	private static final String ITEM_SNOWGLOBE = "snowglobe";

	private static final String RUDOLPH_TALK_GREETING_DEFAULT = "Hi, my jolly friend. What a wonderful time of year this is!";
	private static final String RUDOLPH_TALK_GREETING_WITHOUT_GOODIES = "Oh my. I am so in anticipation of those goodies which I have asked you for. Hopefully it will not be much longer before you can bring them to me.";
	private static final String RUDOLPH_TALK_GREETING_WITH_GOODIES = "Excuse me, please! I see you have delicious goodies. Are they for me?";
	private static final String RUDOLPH_TALK_QUEST_OFFER = "I want some delicious goodies only you can help me get. Do you think you can help me?";
	private static final String RUDOLPH_TALK_QUEST_REJECT = "Well, then I guess I'll just ask someone else for them. Woe is me.";
	private static final String RUDOLPH_TALK_QUEST_ACCEPT = "I heard about the wonderful #goodies you have here in Semos. If you get 5 reindeer moss, 10 apples and 10 carrots, I'll give you a reward.";
	private static final String RUDOLPH_TALK_QUEST_GOODIES_REFUSED = "Well then, I certainly hope you find those goodies before I pass out from hunger.";
	private static final String RUDOLPH_TALK_QUEST_GOODIES_OFFERED = "Oh, I am so excited! I have wanted to eat these things for so long. Thanks so much. And to borrow a phrase, Ho Ho Ho, Merry Christmas.";
	private static final String RUDOLPH_TALK_QUEST_TOO_SOON = "Thank you very much for the goodies, but I don't have any other task for you this year. Have a wonderful holiday season.";

	private static final String HISTORY_DEFAULT = "I have met Rudolph. He is the Red-Nosed Reindeer running around in Semos.";
	private static final String HISTORY_REJECTED = "He asked me to find goodies for him but I rejected his request.";
	private static final String HISTORY_START = "I promised to find goodies for him because he is a nice reindeer.";
	private static final String HISTORY_GOT_GOODIES = "I got all the goodies and will take them to Rudolph.";
	private static final String HISTORY_COMPLETED_REPEATABLE = "It's been a year since I helped Rudolph. I should ask him if he needs help again.";
	private static final String HISTORY_COMPLETED_NOT_REPEATABLE = "I took the goodies to Rudolph. As a little thank you, he gave ME some goodies. :)";

	private SpeakerNPC npc;
	private Engine en;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_SEMOS);
	}

	public GoodiesForRudolphTest() {
		super(ZONE_SEMOS, NPC_RUDOLPH);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		final StendhalRPZone cityZone = new StendhalRPZone(ZONE_SEMOS);
		new RudolphNPC().configureZone(cityZone, null);

		quest = new GoodiesForRudolph();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testDidNotAskForQuest() {
		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_DEFAULT, responseToGreeting);

		assertNoHistory();
	}

	@Test
	public void testRefuseQuest() {
		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(RUDOLPH_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "no");
		assertEquals(RUDOLPH_TALK_QUEST_REJECT, getReply(npc));

		assertEquals("rejected", player.getQuest(questSlot));
		assertTrue(npc.isTalking());
		assertLoseKarma(5);
		assertHistory(HISTORY_DEFAULT, HISTORY_REJECTED);
	}

	@Test
	public void testAcceptQuest() {
		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(RUDOLPH_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(RUDOLPH_TALK_QUEST_ACCEPT, getReply(npc));

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	@Test
	public void testBackToRudolphWithoutGoodies() {
		player.setQuest(questSlot, "start");

		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_WITHOUT_GOODIES, responseToGreeting);

		en.step(player, "quest");
		assertEquals(RUDOLPH_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(RUDOLPH_TALK_QUEST_ACCEPT, getReply(npc));

		prepareGoodies();

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_GOODIES);
	}

	@Test
	public void testPrepareGoodies() {
		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(RUDOLPH_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(RUDOLPH_TALK_QUEST_ACCEPT, getReply(npc));

		prepareGoodies();

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_GOODIES);
	}

	@Test
	public void testBackToRudolphRefuseGoodies() {
		player.setQuest(questSlot, "start");
		prepareGoodies();

		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_WITH_GOODIES, responseToGreeting);

		en.step(player, "no");
		assertEquals(RUDOLPH_TALK_QUEST_GOODIES_REFUSED, getReply(npc));

		assertTrue(npc.isTalking());

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_GOODIES);
	}

	@Test
	public void testBackToRudolphOfferGoodies() {
		int initialXp = player.getXP();
		player.setQuest(questSlot, "start");
		prepareGoodies();

		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_WITH_GOODIES, responseToGreeting);

		en.step(player, "yes");
		assertEquals(RUDOLPH_TALK_QUEST_GOODIES_OFFERED, getReply(npc));

		assertTrue(npc.isTalking());

		assertFalse(player.isEquipped(ITEM_APPLE));
		assertFalse(player.isEquipped(ITEM_CARROT));
		assertFalse(player.isEquipped(ITEM_REINDEER_MOSS));
		assertTrue(player.isEquipped(ITEM_SNOWGLOBE));
		assertTrue(player.isEquipped(ITEM_MONEY, 50));
		assertGainKarma(60);
		assertEquals(100, player.getXP() - initialXp);
		assertEquals("done", player.getQuest(questSlot, 0));

		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_GOODIES,
				HISTORY_COMPLETED_NOT_REPEATABLE);
	}

	@Test
	public void testBackToRudolphOfferThenHideGoodies() {
		player.setQuest(questSlot, "start");
		prepareGoodies();

		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_WITH_GOODIES, responseToGreeting);

		hideGoodies();

		en.step(player, "yes");
		assertEquals(null, getReply(npc));
		assertTrue(npc.isTalking());
	}

	@Test
	public void testAskQuestAgain() {
		player.setQuest(questSlot, 0, "done");
		PlayerTestHelper.setPastTime(player, questSlot, 1,
				TimeUnit.DAYS.toSeconds(365));

		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "task");
		assertEquals(RUDOLPH_TALK_QUEST_OFFER, getReply(npc));

		assertTrue(quest.isRepeatable(player));
		assertEquals("done", player.getQuest(questSlot, 0));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_GOODIES,
				HISTORY_COMPLETED_REPEATABLE);
	}

	@Test
	public void testAskQuestAgainAndAccept() {
		player.setQuest(questSlot, 0, "done");
		PlayerTestHelper.setPastTime(player, questSlot, 1,
				TimeUnit.DAYS.toSeconds(365));

		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "task");
		assertEquals(RUDOLPH_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(RUDOLPH_TALK_QUEST_ACCEPT, getReply(npc));

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	@Test
	public void testAskQuestAgainTooSoon() {
		player.setQuest(questSlot, 0, "done");
		PlayerTestHelper.setPastTime(player, questSlot, 1,
				TimeUnit.DAYS.toSeconds(1));

		String responseToGreeting = startTalkingToNpc(NPC_RUDOLPH);
		assertEquals(RUDOLPH_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "task");
		assertThat(getReply(npc), startsWith(RUDOLPH_TALK_QUEST_TOO_SOON));

		assertFalse(quest.isRepeatable(player));
		assertEquals("done", player.getQuest(questSlot, 0));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_GOODIES,
				HISTORY_COMPLETED_NOT_REPEATABLE);
	}

	private String startTalkingToNpc(String name) {
		npc = SingletonRepository.getNPCList().get(name);
		en = npc.getEngine();

		en.step(player, "hi");
		return getReply(npc);
	}

	private void prepareGoodies() {
		PlayerTestHelper.equipWithStackableItem(player, ITEM_APPLE, 10);
		PlayerTestHelper.equipWithStackableItem(player, ITEM_CARROT, 10);
		for (int i = 0; i < 5; i++) {
			PlayerTestHelper.equipWithItem(player, ITEM_REINDEER_MOSS);
		}
	}

	private void hideGoodies() {
		player.drop(ITEM_APPLE, 10);
		player.drop(ITEM_CARROT, 10);
		for (int i = 0; i < 5; i++) {
			player.drop(ITEM_REINDEER_MOSS);
		}
	}
}
