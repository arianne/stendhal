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
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.deniran.cityinterior.brelandhouse.OldManNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;


public class AnOldMansWishTest extends QuestHelper {

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
		assertEquals("Hello young one.", getReply(elias));

		// quest not added to world
		en.step(player, "quest");
		assertEquals(
			"There is something that weighs heavy on me. But I am not ready"
				+ " for help. Perhaps you could come back later.",
			getReply(elias));

		// add quest to world
		new AnOldMansWish().addToWorld();

		// level too low to start quest
		en.step(player, "quest");
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
		assertEquals("Alas! What has become of my grandson!?", getReply(elias));
		assertEquals("rejected", player.getQuest(QUEST_SLOT));

		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals("Thank you so much! I await your return.", getReply(elias));
		assertEquals("start", player.getQuest(QUEST_SLOT));

		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(elias));
	}
}
