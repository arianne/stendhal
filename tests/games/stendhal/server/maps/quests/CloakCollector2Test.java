/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class CloakCollector2Test {
	private static final String NPC = "Josephine";
	private static final String QUEST_NAME = "cloaks_collector_2";
	private static final String OLD_QUEST = "cloaks_collector";
	private static final List<String> CLOAKS = Arrays.asList("red cloak",
		"shadow cloak", "xeno cloak",  "elvish cloak", "chaos cloak",
		"mainio cloak", "golden cloak", "black dragon cloak");

	private static List<String> missingCloaks(final Player player) {
		String done = player.getQuest(QUEST_NAME);
		final List<String> needed = new LinkedList<String>(CLOAKS);
		final List<String> colored = new LinkedList<String>();

		if (done == null) {
			done = "";
		}

		needed.removeAll(Arrays.asList(done.split(";")));
		for (final String cloak : needed) {
			colored.add("#" + cloak);
		}

		return colored;
	}

	private static String initiallyWantedMessage(final Player player) {
		final List<String> needed = missingCloaks(player);

		return "It's missing "
			+ Grammar.quantityplnoun(needed.size(), "cloak", "one")
			+ ". That's " + Grammar.enumerateCollection(needed)
			+ ". Will you find them?";
	}

	private static String stillWantedMessage(final Player player) {
		final List<String> needed = missingCloaks(player);

		return ("I want " + Grammar.quantityplnoun(needed.size(), "cloak", "a")
			+ ". That's " + Grammar.enumerateCollection(needed)
			+ ". Did you bring any?");
	}

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().remove(NPC);
	}

	@Test
	public final void missingPreviousQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC(NPC));
		final CloakCollector2 cc = new CloakCollector2();
		cc.addToWorld();
		final SpeakerNPC npc = cc.npcs.get(NPC);
		final Engine en = npc.getEngine();
		final Player player = PlayerTestHelper.createPlayer("player");

		/*
		 * Josephine should have nothing to say to us, unless we have completed
		 * cloaks_collector quest. Those people would be getting the answer from
		 *  that quest.
		 */
		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Josephines answer to non cloak1 people", null, getReply(npc));
	}

	@Test
	public final void rejectQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC(NPC));
		final CloakCollector2 cc = new CloakCollector2();
		cc.addToWorld();
		final SpeakerNPC npc = cc.npcs.get(NPC);
		final Engine en = npc.getEngine();
		final Player player = PlayerTestHelper.createPlayer("player");
		final double karma = player.getKarma();

		// CloakCollector needs to be done to start this quest
		player.setQuest(OLD_QUEST, "done");
		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Josephines first greeting",  "Hi again! I hear there's some new cloaks out, and I'm regretting not asking you about the ones I didn't like before. It feels like my #collection isn't complete...", getReply(npc));

		en.stepTest(player, "no");
		assertEquals("Answer to refusal", "Oh ... you're not very friendly. Please say yes?", getReply(npc));
		assertEquals("Karma penalty at refusal", karma - 5.0, player.getKarma(), 0.01);
	}

	@Test
	public final void doQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC(NPC));
		final CloakCollector2 cc = new CloakCollector2();
		cc.addToWorld();
		final SpeakerNPC npc = cc.npcs.get(NPC);
		final Engine en = npc.getEngine();
		final Player player = PlayerTestHelper.createPlayer("player");
		final double karma = player.getKarma();

		player.setQuest(OLD_QUEST, "done");

		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Hi again! I hear there's some new cloaks out, and I'm regretting not asking you about the ones I didn't like before. It feels like my #collection isn't complete...", getReply(npc));

		en.stepTest(player, "collection");
		assertEquals("Answer to 'collection'",
				initiallyWantedMessage(player), getReply(npc));

		for (final String item : CLOAKS) {
			en.stepTest(player, item);
			final String expected = "You haven't seen one before? Well, it's a "
				+ item
				+ ". Sorry if that's not much help, it's all I know! So, will you find them all?";
			assertEquals(expected, getReply(npc));
		}

		// does not exist
		en.stepTest(player, "pink cloak");
		assertEquals("Sorry, I don't know about that. Please name me another cloak.", getReply(npc));

		en.stepTest(player, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals("Brilliant! I'm all excited again! Bye!", getReply(npc));

		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Welcome back! Have you brought any #cloaks with you?", getReply(npc));

		en.stepTest(player, "cloaks");
		assertEquals(stillWantedMessage(player), getReply(npc));

		en.stepTest(player, "no");
		assertEquals("Okay then. Come back later.", getReply(npc));

		// This is weird, but it's how the quest works at the moment
		en.stepTest(player, "no");
		assertEquals("Ok. If you want help, just say.", getReply(npc));

		/* Josephine does not know what to do with "bye" without CloakCollector,
		   so do it manually. Jump over the greeting as it was already tested above */
		en.setCurrentState(ConversationStates.QUESTION_2);

		en.stepTest(player, "yes");
		assertEquals("Woo! What #cloaks did you bring?", getReply(npc));

		// Give her all but the last - Thrice to test the possible answers
		for (final String itemName : CLOAKS.subList(1, CLOAKS.size())) {
			en.stepTest(player, itemName);
			assertEquals("Oh, I'm disappointed. You don't really have "
					+ Grammar.a_noun(itemName) + " with you.", getReply(npc));

			final Item cloak = new Item(itemName, "", "", null);
			player.getSlot("bag").add(cloak);
			en.stepTest(player, itemName);
			assertEquals("Wow, thank you! What else did you bring?", getReply(npc));

			en.stepTest(player, itemName);
			assertEquals("You're terribly forgetful, you already brought that one to me.", getReply(npc));
		}

		// check the message again now that it has changed
		en.stepTest(player, "cloaks");
		assertEquals(stillWantedMessage(player), getReply(npc));

		// Give the last one too. Try lying first again just to be sure
		final String lastCloak = CLOAKS.get(0);
		en.stepTest(player, lastCloak);
		assertEquals("Oh, I'm disappointed. You don't really have "
				+ Grammar.a_noun(lastCloak) + " with you.", getReply(npc));
		final Item cloak = new Item(lastCloak, "", "", null);
		player.getSlot("bag").add(cloak);
		en.stepTest(player, lastCloak);
		assertEquals("Answer to last brought cloak", "Oh, yay! You're so kind, I bet you'll have great Karma now! Here, take these killer boots. I think they're gorgeous but they don't fit me!", getReply(npc));

		// check the rewards
		assertEquals(karma + 100.0, player.getKarma(), 0.01);
		assertEquals(100000, player.getXP());
		assertEquals("done;rewarded", player.getQuest(QUEST_NAME));
		assertEquals(true, player.isEquipped("killer boots"));

		final Item boots = player.getFirstEquipped("killer boots");
		assertEquals("player", boots.getBoundTo());
	}

	@Test
	public final void compatibility() {
		SingletonRepository.getNPCList().add(new SpeakerNPC(NPC));
		final CloakCollector2 cc = new CloakCollector2();
		cc.addToWorld();
		final SpeakerNPC npc = cc.npcs.get(NPC);
		final Engine en = npc.getEngine();
		final Player player = PlayerTestHelper.createPlayer("player");

		player.setQuest(OLD_QUEST, "done");
		player.setQuest(QUEST_NAME, "done");

		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Message for the compatibility hack",  "Oh! I didn't reward you for helping me again! Here, take these boots. I think they're gorgeous but they don't fit me :(", getReply(npc));
		assertEquals("done;rewarded", player.getQuest(QUEST_NAME));
		assertTrue("The player got the boots", player.isEquipped("killer boots"));

		final Item boots = player.getFirstEquipped("killer boots");
		assertEquals("player", boots.getBoundTo());
	}
}
