/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package scripts.region.atlantis.city.exterior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.scripting.ScriptInLua;
import games.stendhal.server.core.scripting.ScriptRunner;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;


public class ZelanNPCTest extends ZoneAndPlayerTestImpl {

	private static Player player;
	private static SpeakerNPC zelan;

	private static final String questSlot = "unicorn_horns_for_zelan";


	@BeforeClass
	public static void setUpBeforeClass() {
		setupZone("-7_deniran_atlantis");
		// initialize Lua globals
		ScriptInLua.getInstance().init();
		new ScriptRunner().perform("region/atlantis/city/exterior/ZelanNPC.lua");
		// add quest to world
		StendhalQuestSystem.get().loadCachedQuests();
	}

	@Override
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		zelan = SingletonRepository.getNPCList().get("zelan");
	}

	@Test
	public void init() {
		testEntities();
		testDialogue();
		testQuest();
	}

	private void testEntities() {
		assertNotNull(player);
		assertNotNull(zelan);
	}

	private void testDialogue() {
		final Engine en = zelan.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Greetings! How may I help you?", getReply(zelan));

		en.step(player, "help");
		assertNull(getReply(zelan));
		en.step(player, "offer");
		assertNull(getReply(zelan));
		en.step(player, "job");
		assertNull(getReply(zelan));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Bye.", getReply(zelan));
	}

	private void testQuest() {
		assertFalse(player.hasQuest(questSlot));

		final int startXP = player.getXP();
		final double startKarma = player.getKarma();

		assertFalse(player.isEquipped("money"));
		assertFalse(player.isEquipped("soup"));
		assertFalse(player.isEquipped("unicorn horn"));

		final Engine en = zelan.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals(
				"Hello! I'm in need of some unicorn horns to make some daggers."
				+ " It is really dangerous in the woods surrounding Atlantis. If you are a brave sort"
				+ " I could really use some help gathering unicorn horns. Will you help me?",
				getReply(zelan));
		en.step(player, "no");
		assertEquals("Thats ok, I will find someone else to help me.", getReply(zelan));
		assertEquals("rejected", player.getQuest(questSlot, 0));
		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
				"Great! Be careful out there lots of large monsters, and those centaurs are really nasty",
				getReply(zelan));
		assertEquals("start", player.getQuest(questSlot, 0));
		en.step(player, "quest");
		assertEquals("I have already asked you to get 10 unicorn horns. Are you #done?", getReply(zelan));
		en.step(player, "done");
		assertEquals("I asked you to bring me 10 unicorn horns.", getReply(zelan));

		PlayerTestHelper.equipWithStackableItem(player, "unicorn horn", 9);

		en.step(player, "done");
		assertEquals("I asked you to bring me 10 unicorn horns.", getReply(zelan));

		PlayerTestHelper.equipWithItem(player, "unicorn horn");

		assertTrue(player.isEquipped("unicorn horn", 10));

		en.step(player, "done");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Thanks a bunch! As a reward I will give you 3 soups and 20000 money.", getReply(zelan));

		assertEquals(startXP + 50000, player.getXP());
		assertEquals(startKarma + 5.0, player.getKarma(), 0);
		assertTrue(player.isEquipped("money", 20000));
		assertTrue(player.isEquipped("soup", 3));
		assertFalse(player.isEquipped("unicorn horn"));
		assertEquals("done", player.getQuest(questSlot, 0));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}
}
