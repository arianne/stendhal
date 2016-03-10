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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.kirdneh.city.LittleGirlNPC;
import games.stendhal.server.maps.kirdneh.city.MummyNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class ChocolateForElisabethTest extends ZonePlayerAndNPCTestImpl {

	private static final String CAREY = "Carey";
	private static final String ELISABETH = "Elisabeth";
	private static final String ZONE_NAME = "0_kirdneh_city";

	private static final String CHOCOLATE = "chocolate bar";
	private static final String[] FLOWERS = { "daisies", "zantedeschia", "pansy" };

	private static final String LIZ_TALK_GREETING_DEFAULT = "Hello.";
	private static final String LIZ_TALK_GREETING_FIRST_TIME = "I can't remember when I smelt the good taste of #chocolate the last time...";
	private static final String LIZ_TALK_GREETING_WITHOUT_CHOCOLATE = "I hope that someone will bring me some chocolate soon...:(";
	private static final String LIZ_TALK_GREETING_WITH_CHOCOLATE_ALLOWED = "Awesome! Is that chocolate for me?";
	private static final String LIZ_TALK_GREETING_WITH_CHOCOLATE_NOT_ALLOWED = "My mum wants to know who I was asking for chocolate from now :(";

	private static final String LIZ_TALK_QUEST_OFFER = "I would really love to have some chocolate. I'd like one bar, please. A dark brown one or a sweet white one or some with flakes. Will you get me one?";
	private static final String LIZ_TALK_QUEST_OFFER_AGAIN = "I hope another chocolate bar wouldn't be greedy. Can you get me another one?";
	private static final String LIZ_TALK_QUEST_NOT_NOW = "I've had too much chocolate. I feel sick.";
	private static final String LIZ_TALK_QUEST_ALREADY_OFFERED = "Waaaaaaaa! Where is my chocolate ...";
	private static final String LIZ_TALK_QUEST_REJECT = "Ok, I'll wait till mommy finds some helpers...";
	private static final String LIZ_TALK_QUEST_ACCEPT = "Thank you!";
	private static final String LIZ_TALK_REWARD = "Thank you EVER so much! You are very kind. Here, take a fresh flower as a present.";
	private static final String LIZ_TALK_PISSED = "Waaaaaa! You're a big fat meanie.";

	private static final String MUM_TALK_GREET = "Hello, nice to meet you.";
	private static final String MUM_TALK_GREET_AND_APPROVE = "Oh you met my daughter Elisabeth already. You seem like a nice person so it would be really kind, if you can bring her a chocolate bar because I'm not #strong enough for that.";

	private static final String HISTORY_DEFAULT = "Elisabeth is a sweet little girl who lives in Kirdneh together with her family.";
	private static final String HISTORY_REJECTED = "I don't like sweet little girls.";
	private static final String HISTORY_START = "Little Elisabeth wants a chocolate bar.";
	private static final String HISTORY_GOT_CHOCOLATE = "I found a tasty chocolate bar for Elisabeth.";
	private static final String HISTORY_MUM_APPROVES = "I spoke to Carey, Elisabeth's mom and she agreed I could give a chocolate bar to her daughter.";
	private static final String HISTORY_DONE = "Elisabeth is eating the chocolate bar I gave her, and she gave me some flowers in return.";
	private static final String HISTORY_REPEATABLE = "I took some chocolate to Elisabeth, she gave me some flowers in return. Perhaps she'd like more chocolate now.";

	private SpeakerNPC npc;
	private Engine en;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public ChocolateForElisabethTest() {
		setNpcNames(ELISABETH, CAREY);
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new LittleGirlNPC(), ZONE_NAME);
		addZoneConfigurator(new MummyNPC(), ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		quest = new ChocolateForElisabeth();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testRefuseQuest() {
		startTalkingToNpc(ELISABETH);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "no");
		assertEquals(LIZ_TALK_QUEST_REJECT, getReply(npc));

		assertEquals("rejected", player.getQuest(questSlot));
		assertFalse(npc.isTalking());
		assertLoseKarma(5);
		assertHistory(HISTORY_DEFAULT, HISTORY_REJECTED);
	}

	@Test
	public void testAcceptQuest() {
		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_FIRST_TIME, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_OFFER, getReply(npc));

		en.step(player, "yes");
		assertEquals(LIZ_TALK_QUEST_ACCEPT, getReply(npc));

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	@Test
	public void testAskForQuestAlreadyAccepted() {
		player.setQuest(questSlot, "start");

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_WITHOUT_CHOCOLATE, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_ALREADY_OFFERED, getReply(npc));

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	@Test
	public void testFoundChocolate() {
		player.setQuest(questSlot, "start");

		equipWithItem(player, CHOCOLATE);

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE);
	}

	@Test
	public void testBringChocolateBeforeTalkingToMum() {
		player.setQuest(questSlot, "start");

		equipWithItem(player, CHOCOLATE);

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_WITH_CHOCOLATE_NOT_ALLOWED, responseToGreeting);

		assertTrue(player.isEquipped(CHOCOLATE));
		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE);
	}

	@Test
	public void testTalkToMumAfterQuestStart() {
		player.setQuest(questSlot, "start");

		String responseToGreeting = startTalkingToNpc(CAREY);
		assertEquals(MUM_TALK_GREET_AND_APPROVE, responseToGreeting);

		assertEquals("mummy", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_MUM_APPROVES);
	}

	@Test
	public void testTalkToMumBeforeQuestStart() {
		player.setQuest(questSlot, null);

		String responseToGreeting = startTalkingToNpc(CAREY);
		assertEquals(MUM_TALK_GREET, responseToGreeting);

		assertNull(null, player.getQuest(questSlot));
		assertNoHistory();
	}

	@Test
	public void testBringChocolateAfterTalkingToMum() {
		player.setQuest(questSlot, "mummy");

		equipWithItem(player, CHOCOLATE);

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_WITH_CHOCOLATE_ALLOWED, responseToGreeting);

		en.step(player, "yes");
		assertEquals(LIZ_TALK_REWARD, getReply(npc));

		assertFalse(player.isEquipped(CHOCOLATE));
		assertTrue(isEquippedWithFlower());
		assertGainKarma(10);
		assertTrue(player.getQuest(questSlot).startsWith("eating;"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE, HISTORY_MUM_APPROVES, HISTORY_DONE);
	}

	@Test
	public void testRefuseToGiveChocolate() {
		player.setQuest(questSlot, "mummy");

		equipWithItem(player, CHOCOLATE);

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_WITH_CHOCOLATE_ALLOWED, responseToGreeting);

		en.step(player, "no");
		assertEquals(LIZ_TALK_PISSED, getReply(npc));

		assertTrue(player.isEquipped(CHOCOLATE));
		assertFalse(isEquippedWithFlower());
		assertLoseKarma(5);
		assertEquals("mummy", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE, HISTORY_MUM_APPROVES);
	}

	@Test
	public void testAskQuestAgain() {
		player.setQuest(questSlot, "eating");
		PlayerTestHelper.setPastTime(player, questSlot, 1, TimeUnit.HOURS.toSeconds(1));

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_OFFER_AGAIN, getReply(npc));

		assertTrue(player.getQuest(questSlot).startsWith("eating"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE, HISTORY_MUM_APPROVES, HISTORY_REPEATABLE);
	}

	@Test
	public void testAskQuestAgaintTooSoon() {
		player.setQuest(questSlot, "eating");
		PlayerTestHelper.setPastTime(player, questSlot, 1, TimeUnit.MINUTES.toSeconds(30));

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_NOT_NOW, getReply(npc));

		assertTrue(player.getQuest(questSlot).startsWith("eating"));
		assertHistory(HISTORY_DEFAULT, HISTORY_START, HISTORY_GOT_CHOCOLATE, HISTORY_MUM_APPROVES, HISTORY_DONE);
	}

	@Test
	public void testAskQuestAgainAndAccept() {
		player.setQuest(questSlot, "eating");
		PlayerTestHelper.setPastTime(player, questSlot, 1, TimeUnit.HOURS.toSeconds(1));

		String responseToGreeting = startTalkingToNpc(ELISABETH);
		assertEquals(LIZ_TALK_GREETING_DEFAULT, responseToGreeting);

		en.step(player, "quest");
		assertEquals(LIZ_TALK_QUEST_OFFER_AGAIN, getReply(npc));

		en.step(player, "yes");

		assertEquals("start", player.getQuest(questSlot));
		assertHistory(HISTORY_DEFAULT, HISTORY_START);
	}

	private String startTalkingToNpc(String name) {
		npc = SingletonRepository.getNPCList().get(name);
		en = npc.getEngine();

		en.step(player, "hi");
		return getReply(npc);
	}

	private boolean isEquippedWithFlower() {
		for (String flower : FLOWERS) {
			if (player.isEquipped(flower)) {
				return true;
			}
		}
		return false;
	}
}
