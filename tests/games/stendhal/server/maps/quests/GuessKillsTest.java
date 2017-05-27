/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
import games.stendhal.server.maps.nalwor.city.GuessKillsNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.CreatureTestHelper;

public class GuessKillsTest extends ZonePlayerAndNPCTestImpl {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	private String questSlot;
	private static final String ZONE_NAME = "0_nalwor_city";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public GuessKillsTest() {
		super(ZONE_NAME, "Crearid");
	}

	@Override
	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		new GuessKillsNPC().configureZone(zone, null);

		// Add creature
		SingletonRepository.getEntityManager().getCreature("deer");
		quest = new GuessKills();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		npc = SingletonRepository.getNPCList().get("Crearid");
		CreatureTestHelper.generateRPClasses();
		en = npc.getEngine();

		//Test default responses and if player does not meet requirement
		en.step(player, "hi");
		assertEquals("Greetings", getReply(npc));
		en.step(player, "play");
		assertEquals("I'd like some entertainment but you don't look like you're up to it just yet. Come back when you've gained a bit more experience fighting creatures.", getReply(npc));
		en.step(player, "job");
		assertEquals("I am just an old woman, I walk around and observe all around me.", getReply(npc));
		en.step(player, "help");
		assertEquals("I'm not sure how I can help you. On some days I like to #play #games.", getReply(npc));
		en.step(player, "play gamEs");
		assertEquals("I'd like some entertainment but you don't look like you're up to it just yet. Come back when you've gained a bit more experience fighting creatures.", getReply(npc));
		en.step(player, "Play");
		assertEquals("I'd like some entertainment but you don't look like you're up to it just yet. Come back when you've gained a bit more experience fighting creatures.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye deary.", getReply(npc));

		// Give player enough kills
		player.setKeyedSlot("!kills", "solo.deer", "1001");

		// Test quest offer if meets requirements
		en.step(player, "hi");
		assertEquals("Greetings", getReply(npc));
		en.step(player, "play");
		assertEquals("I'm a little bored at the moment. Would you like to play a game?", getReply(npc));
		en.step(player, "yes");
		// Deer was added in setup(), so we know that's what we get
		assertEquals("I've been counting how many creatures you have killed, now tell me, how many deer do you think you've killed? You have three guesses and I'll accept guesses that are close to the correct answer.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, come back when you want to continue.", getReply(npc));

		// Add other creature to test if NPC remembers old creature
		SingletonRepository.getEntityManager().getCreature("rat");
		player.setKeyedSlot("!kills", "solo.rat", "10");

		// Leave quest early and come back, get guess close
		en.step(player, "hi");
		assertEquals("Greetings", getReply(npc));
		en.step(player, "play");
		assertEquals("We did not finish our game last time would you like to continue?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh well. Your loss, now what can I do for you?", getReply(npc));
		en.step(player, "play");
		assertEquals("We did not finish our game last time would you like to continue?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Let me see... you have 3 guesses left... and if I recall correctly I asked you... how many deer do think you have killed?", getReply(npc));
		en.step(player, "8");
		assertEquals("Nope, that is not right. Try again.", getReply(npc));
		en.step(player, "5");
		assertEquals("Wrong again. You have one more try.", getReply(npc));
		en.step(player, "981");
		assertEquals("Wow, that was pretty close. Well done!", getReply(npc));

		// Reset quest because of timestamp
		player.setQuest(questSlot, "done;0;");

		// Test bogus answers and exact answer
		en.step(player, "play");
		assertEquals("I'm a little bored at the moment. Would you like to play a game?", getReply(npc));
		en.step(player, "yes");

		String reply = getReply(npc);
		assertEquals(reply.startsWith("I've been counting how many creatures you have killed, now tell me, how many "), true);
		assertEquals(reply.endsWith(" do you think you've killed? You have three guesses and I'll accept guesses that are close to the correct answer."), true);

		en.step(player, "sdf");
		assertEquals("How could that possibly be an answer? Give me a proper number.", getReply(npc));

		if (reply.contains("rat")) {
			en.step(player, "10");
		} else {
			en.step(player, "1001");
		}

		assertEquals("Stupendous! That is the exact number! Either you're very lucky or you really pay attention.", getReply(npc));

		// Reset quest because of timestamp
		player.setQuest(questSlot, "done;0;");

		// Test other bogus answers
		en.step(player, "play");
		assertEquals("I'm a little bored at the moment. Would you like to play a game?", getReply(npc));
		en.step(player, "yes");

		reply = getReply(npc);
		assertEquals(reply.startsWith("I've been counting how many creatures you have killed, now tell me, how many "), true);
		assertEquals(reply.endsWith(" do you think you've killed? You have three guesses and I'll accept guesses that are close to the correct answer."), true);

		en.step(player, "98");
		assertEquals("Nope, that is not right. Try again.", getReply(npc));
		en.step(player, "o");
		assertEquals("Is that even possible? Give me a valid answer.", getReply(npc));
		en.step(player, "023");
		assertEquals("Wrong again. You have one more try.", getReply(npc));
		en.step(player, "88");

		reply = getReply(npc);
		assertEquals(reply.startsWith("Unfortunately that is incorrect. The correct answer is in the region of "), true);
		assertEquals(reply.endsWith("Good effort though."), true);

		en.step(player, "play");
		assertEquals(getReply(npc).startsWith("I've had plenty of fun for now, thanks. Come back in, lets say "), true);
	}
}
