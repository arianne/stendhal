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
import games.stendhal.server.maps.semos.guardhouse.RetiredAdventurerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class MeetHayunnTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new RetiredAdventurerNPC().configureZone(zone, null);
		npc = SingletonRepository.getNPCList().get("Hayunn Naratha");
		en = npc.getEngine();

		AbstractQuest quest = new MeetHayunn();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Hayunn Naratha");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hi. I bet you've been sent here to learn about adventuring from me. First, lets see what you're made of. Go and kill a rat outside, you should be able to find one easily. Do you want to learn how to attack it, before you go?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well, back when I was a young adventurer, I clicked on my enemies to attack them. I'm sure that will work for you, too. Good luck, and come back once you are done.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));


		en.step(player, "hi");
		assertEquals("I see you haven't managed to kill a rat yet. Do you need me to tell you how to fight them?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well, back when I was a young adventurer, I clicked on my enemies to attack them. I'm sure that will work for you, too. Good luck, and come back once you are done.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// [15:13] rat has been killed by omerob
		player.setSoloKill("rat");

		en.step(player, "hi");
		// [15:14] omerob earns 10 experience points.
		assertEquals("You killed the rat! Now, I guess you want to explore. Do you want to know the way to Semos?", getReply(npc));
		en.step(player, "yes");
		// [15:14] omerob earns 10 experience points.
		assertEquals("Follow the path through this village to the east, and you can't miss Semos. If you go and speak to Monogenes, the old man in this picture, he will give you a map. Here's 5 money to get you started. Bye bye!", getReply(npc));
		en.step(player, "bye");
		en.step(player, "hi");
		assertEquals("Hello again. Have you come to learn more from me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Perhaps you have found Semos dungeons by now. The corridors are pretty narrow down there, so there's a trick to moving quickly and accurately, if you'd like to hear it. #Yes?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Simple, really; just click the place you want to move to. There's a lot more information than I can relate just off the top of my head... do you want to know where to read more?", getReply(npc));
		en.step(player, "yes");
		// [15:14] omerob earns 20 experience points.
		assertEquals("You can find answers to frequently asked questions by typing #/faq \n" +
				"You can read about some of the currently most powerful and successful warriors at #https://stendhalgame.org\n"
			+ " Well, good luck in the dungeons! This shield should help you. Here's hoping you find fame and glory, and keep watch for monsters!", getReply(npc));

		// -----------------------------------------------
		assertEquals("done", player.getQuest("meet_hayunn"));

	}
}
