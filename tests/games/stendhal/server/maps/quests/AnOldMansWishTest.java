/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.deniran.cityinterior.brelandhouse.OldManNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;


public class AnOldMansWishTest extends QuestHelper {

	private static final StendhalQuestSystem quests = StendhalQuestSystem.get();

	private static final String QUEST_SLOT = AnOldMansWish.QUEST_SLOT;

	private Player player;
	private SpeakerNPC elias;


	@Before
	public void setup() {
		final StendhalRPZone zone = new StendhalRPZone("test_zone");
		new OldManNPC().configureZone(zone, null);
		player = PlayerTestHelper.createPlayer("player");
		elias = SingletonRepository.getNPCList().get("Elias Breland");
	}

	@Test
	public void init() {
		assertNotNull(player);
		assertNotNull(elias);
		assertFalse(player.hasQuest(QUEST_SLOT));

		player.setLevel(99);
		assertEquals(99, player.getLevel());

		final Engine en = elias.getEngine();
		en.step(player, "hello");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hello young one.", getReply(elias));

		// quest not added to world
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"There is something that weighs heavy on me. But I am not ready"
				+ " for help. Perhaps you could come back later.",
			getReply(elias));

		// add quest to world
		final AnOldMansWish quest = new AnOldMansWish();
		assertFalse(quests.isLoaded(quest));
		quests.loadQuest(quest);
		assertTrue(quests.isLoaded(quest));

		// level too low to start quest
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"My grandson disappeared over a year ago. But I need help from a"
				+ " more experienced adventurer.",
			getReply(elias));

		player.setLevel(100);
		assertEquals(100, player.getLevel());

		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals(
			"My grandson disappeared over a year ago. I fear the worst and"
				+ " have nearly given up all hope. What I would give to just"
				+ " know what happened to him! If you learn anything will"
				+ " you bring me the news?",
			getReply(elias));

		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Alas! What has become of my grandson!?", getReply(elias));
		assertEquals("rejected", player.getQuest(QUEST_SLOT));

		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh thank you! My grandson's name is #Niall. You could talk to"
				+ " #Marianne. They used to play together.",
			getReply(elias));
		assertEquals("Marianne", player.getQuest(QUEST_SLOT));

		// quest already started
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thank you for accepting my plea for help. Please tell me if"
				+ " you hear any news about what has become of my grandson."
				+ " He used to play with a little girl named #Marianne.",
			getReply(elias));

		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Niall is my grandson. I am so distraught over his disappearance."
				+ " Ask the girl #Marianne. The often played together.",
			getReply(elias));

		en.step(player, "Marianne");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Marianne lives here in Deniran. Ask her about #Niall.",
			getReply(elias));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Goodbye.", getReply(elias));

		// TODO: complete quest
		player.setQuest(QUEST_SLOT, "done");

		en.step(player, "hi");
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thank you for returning my grandson to me. I am overfilled"
				+ " with joy!",
			getReply(elias));
	}
}
