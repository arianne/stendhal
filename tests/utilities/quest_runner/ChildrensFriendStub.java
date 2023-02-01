/***************************************************************************
 *                    Copyright © 2020-2023 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities.quest_runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.PlayerTestHelper.equipWithItem;
import static utilities.PlayerTestHelper.equipWithStackableItem;
import static utilities.SpeakerNPCTestHelper.getReply;
import static utilities.SpeakerNPCTestHelper.getSpeakerNPC;

import java.util.Arrays;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;


public class ChildrensFriendStub {

	public static void doQuestSusi(final Player player) {
		final String questSlot = "susi";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC susi = getSpeakerNPC("Susi");
		assertNotNull(susi);

		final Engine en = susi.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "friend");
		en.step(player, "A circle is round,");
		en.step(player, "it has no end.");
		en.step(player, "That's how long,");
		en.step(player, "I will be your friend.");
		en.step(player, "bye");

		// note: quest slot is set to year of completion
		assertTrue(player.hasQuest(questSlot));
	}

	public static void doQuestTad(final Player player) {
		final String questSlot = "introduce_players";
		final SpeakerNPC tad = getSpeakerNPC("Tad");
		Engine en = tad.getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		equipWithItem(player, "flask");
		en.step(player, "flask");
		en.step(player, "bye");
		en = getSpeakerNPC("Ilisa").getEngine();
		en.step(player, "hi");
		en.step(player, "bye");
		equipWithItem(player, "arandula");
		en.step(player, "hi");
		en.step(player, "bye");
		en = tad.getEngine();
		en.step(player, "hi");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestPlink(final Player player) {
		final String questSlot = "plinks_toy";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC plink = getSpeakerNPC("Plink");
		assertNotNull(plink);

		final Engine en = plink.getEngine();

		en.step(player, "hi");
		en.step(player, "yes");

		// don't need to say "bye"
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		equipWithItem(player, "teddy");

		en.step(player, "hi");
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestAnna(final Player player) {
		final String questSlot = "toys_collector";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC anna = getSpeakerNPC("Anna");
		assertNotNull(anna);

		final Engine en = anna.getEngine();

		en.step(player, "hi");
		en.step(player, "toys");
		en.step(player, "yes");

		// don't need to say "bye"
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		en.step(player, "hi");
		en.step(player, "yes");

		for (final String toy: Arrays.asList("teddy", "dice", "dress")) {
			equipWithItem(player, toy);
			en.step(player, toy);
		}

		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestSally(final Player player) {
		final String questSlot = "campfire";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC sally = getSpeakerNPC("Sally");
		assertNotNull(sally);

		final Engine en = sally.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		equipWithStackableItem(player, "wood", 10);

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertNotNull(player.getQuest(questSlot));
		assertNotEquals("start", player.getQuest(questSlot, 0));
	}

	public static void doQuestAnnie(final Player player) {
		final String questSlot = "icecream_for_annie";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC annie = getSpeakerNPC("Annie Jones");
		final SpeakerNPC mrsjones = getSpeakerNPC("Mrs Jones");
		assertNotNull(annie);
		assertNotNull(mrsjones);

		Engine en = annie.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		en = mrsjones.getEngine();

		en.step(player, "hi");
		en.step(player, "bye");

		en = annie.getEngine();

		equipWithItem(player, "icecream");

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertEquals("eating", player.getQuest(questSlot, 0));
	}

	public static void doQuestElisabeth(final Player player) {
		final String questSlot = "chocolate_for_elisabeth";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC elisabeth = getSpeakerNPC("Elisabeth");
		final SpeakerNPC carey = getSpeakerNPC("Carey");
		assertNotNull(elisabeth);
		assertNotNull(carey);

		Engine en = elisabeth.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		en = carey.getEngine();

		en.step(player, "hi");
		en.step(player, "bye");

		equipWithItem(player, "chocolate bar");

		en = elisabeth.getEngine();

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertEquals("eating", player.getQuest(questSlot, 0));
	}

	public static void doQuestJef(final Player player) {
		final String questSlot = "find_jefs_mom";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC jef = getSpeakerNPC("Jef");
		final SpeakerNPC amber = getSpeakerNPC("Amber");
		assertNotNull(jef);
		assertNotNull(amber);

		Engine en = jef.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		en = amber.getEngine();

		en.step(player, "hi");
		en.step(player, "Jef");

		// don't need to say "bye"
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		en = jef.getEngine();

		en.step(player, "hi");
		en.step(player, "fine");
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestHughie(final Player player) {
		final String questSlot = "fishsoup_for_hughie";
		assertNull(player.getQuest(questSlot));

		//final SpeakerNPC hughie = getSpeakerNPC("Hughie");
		final SpeakerNPC anastasia = getSpeakerNPC("Anastasia");
		//assertNotNull(hughie);
		assertNotNull(anastasia);

		final Engine en = anastasia.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		equipWithItem(player, "fish soup");

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertNotNull(player.getQuest(questSlot));
		assertNotEquals("start", player.getQuest(questSlot, 0));
	}

	public static void doQuestFinn(final Player player) {
		final String questSlot = "coded_message";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC finn = getSpeakerNPC("Finn Farmer");
		final SpeakerNPC george = getSpeakerNPC("George");
		assertNotNull(finn);
		assertNotNull(george);

		Engine en = finn.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");

		String message = getReply(finn);

		en.step(player, "bye");

		en = george.getEngine();

		en.step(player, "hi");
		en.step(player, message);

		message = getReply(george);

		// don't need to say "bye"
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		en = finn.getEngine();

		en.step(player, "hi");
		en.step(player, message);
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestMarianne(final Player player) {
		final String questSlot = "eggs_for_marianne";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC marianne = getSpeakerNPC("Marianne");
		assertNotNull(marianne);

		final Engine en = marianne.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		equipWithStackableItem(player, "egg", 12);

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertNotNull(player.getQuest(questSlot));
		assertNotEquals("start", player.getQuest(questSlot, 0));
	}
}
