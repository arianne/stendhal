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
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.blacksmith.BlacksmithAssistantNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class MeetHackimTest {

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
		new BlacksmithAssistantNPC().configureZone(zone, null);
		npc = SingletonRepository.getNPCList().get("Hackim Easso");

		en = npc.getEngine();

		final AbstractQuest quest = new MeetHackim();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Hi stranger, I'm Hackim Easso, the blacksmith's assistant. Have you come here to buy weapons?", getReply(npc));
		en.step(player, "yes");
		assertEquals("We aren't allowed to sell weapons to adventurers nowadays; we're working flat-out to produce equipment for the glorious Imperial Deniran Army as they fight against Blordrough's dark legions in the south. (Sssh... can you come here so I can whisper?)", getReply(npc));
		en.step(player, "no");
		assertEquals("Remember, all the weapons are counted; best to leave them alone.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hi again, player. How can I #help you this time?", getReply(npc));
		en.step(player, "help");
		assertEquals("I'm the blacksmith's assistant. Tell me... Have you come here to buy weapons?", getReply(npc));
		en.step(player, "yes");
		assertEquals("We aren't allowed to sell weapons to adventurers nowadays; we're working flat-out to produce equipment for the glorious Imperial Deniran Army as they fight against Blordrough's dark legions in the south. (Sssh... can you come here so I can whisper?)", getReply(npc));
		en.step(player, "yes");
		assertEquals("*whisper* Go to the tavern and talk to a man called #Xin #Blanca... he buys and sells equipment that might interest you. Do you want to hear more?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Ask him what he has to #offer, and look at what he will let you #buy and #sell. For instance, if you had a studded shield which you didn't want, you could #'sell studded shield'.", getReply(npc));
		en.step(player, "sell");
		assertEquals("Guessed who supplies Xin Blanca with the weapons he sells? Well, it's me! I have to avoid raising suspicion, though, " +
				"so I can only smuggle him small weapons. If you want something more powerful, you'll have to venture into the dungeons and kill some of the creatures there for items.\n" +
				"If anybody asks, you don't know me!", getReply(npc));
		assertEquals(en.getCurrentState(), ConversationStates.IDLE);
	}
}
