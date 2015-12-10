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
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.temple.HealerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class LearnAboutOrbsTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new HealerNPC().configureZone(zone, null);


		AbstractQuest quest = new LearnAboutOrbs();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuestAppropriateLevel() {
		int before = player.getXP();
		player.setLevel(11);
		npc = SingletonRepository.getNPCList().get("Ilisa");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "quest");
		assertEquals("Some orbs have special properties. I can teach you how to #use an orb, like the one on this table.", getReply(npc));
		en.step(player, "no");
		en.step(player, "use");
		assertEquals("Just right click on the orb and select Use. Did you get any message?", getReply(npc));
		en.step(player, "no");
		assertEquals("Well, you would need to stand next to it. Move closer, do you get a message now?", getReply(npc));
		en.step(player, "no");
		assertEquals("Well, you would need to stand next to it. Move closer, do you get a message now?", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "quest");
		assertEquals("Some orbs have special properties. I can teach you how to #use an orb, like the one on this table.", getReply(npc));
		en.step(player, "use");
		assertEquals("Just right click on the orb and select Use. Did you get any message?", getReply(npc));
		en.step(player, "yes");
		assertEquals("You're a natural! Now that you have learned to use that orb, it will teleport you to a place of magical significance. So don't use it unless you will be able to find your way back!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "task");
		assertEquals("I can remind you how to #use orbs.", getReply(npc));
		en.step(player, "use");
		assertEquals("Just right click on part of the orb, and select Use.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		assertEquals(player.getQuest(new LearnAboutOrbs().getSlotName()),"done");
		int xpAfterQuest = before + 50;
		assertEquals(player.getXP(), xpAfterQuest);
	}

	@Test
	public void testQuestTooLowLevel() {
		player.setLevel(1);
		npc = SingletonRepository.getNPCList().get("Ilisa");
		en = npc.getEngine();
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "quest");
		assertEquals("Some orbs have special properties. I can teach you how to #use an orb, like the one on this table.", getReply(npc));
		en.step(player, "no");
		en.step(player, "use");
		assertEquals("Oh oh, I just noticed you are still new here. Perhaps you better come back when you have more experience. Until then if you need any #help just ask!", getReply(npc));
        en.step(player, "bye");
        assertEquals("Bye.", getReply(npc));
	}
}
