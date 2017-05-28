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

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.blacksmith.BlacksmithAssistantNPC;
import games.stendhal.server.maps.semos.tavern.TraderNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class NewsFromHackimTest {

	private Player player = null;
	private SpeakerNPC npcHackim = null;
	private Engine enHackim = null;
	private SpeakerNPC npcXin = null;
	private Engine enXin = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new BlacksmithAssistantNPC().configureZone(zone, null);
		npcHackim = SingletonRepository.getNPCList().get("Hackim Easso");

		enHackim = npcHackim.getEngine();

		final ZoneConfigurator zoneConf = new TraderNPC();
		zoneConf.configureZone(new StendhalRPZone("int_semos_tavern"), null);
		npcXin = SingletonRepository.getNPCList().get("Xin Blanca");
		enXin = npcXin.getEngine();

		final AbstractQuest quest = new NewsFromHackim();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		enHackim.step(player, "hi");
		assertEquals("Hi stranger, I'm Hackim Easso, the blacksmith's assistant. Have you come here to buy weapons?", getReply(npcHackim));
		enHackim.step(player, "bye");
		assertEquals("Bye.", getReply(npcHackim));

		// -----------------------------------------------

		enHackim.step(player, "hi");
		assertEquals("Hi again, player. How can I #help you this time?", getReply(npcHackim));
		enHackim.step(player, "task");
		assertEquals("Pssst! C'mere... do me a favour and tell #Xin #Blanca that the new supply of weapons is ready, will you?", getReply(npcHackim));
		enHackim.step(player, "Xin");
		assertEquals("You don't know who Xin is? Everybody at the tavern knows Xin. He's the guy who owes beer money to most of the people in Semos! So, will you do it?", getReply(npcHackim));
		enHackim.step(player, "yes");
		assertEquals("Thanks! I'm sure that #Xin will reward you generously. Let me know if you need anything else.", getReply(npcHackim));
		enHackim.step(player, "bye");
		assertEquals("Bye.", getReply(npcHackim));

		// -----------------------------------------------

		enXin.step(player, "hi");
		assertEquals("Ah, it's ready at last! That is very good news indeed! Here, let me give you a little something for your help... Take this set of brand new leather leg armor! Let me know if you want anything else.", getReply(npcXin));
		// [22:38] rosie earns 10 experience points.
		enXin.step(player, "task");
		assertEquals("Talk to Hackim Easso in the smithy, he might want you.", getReply(npcXin));

		// -----------------------------------------------

		enHackim.step(player, "hi");
		assertEquals("Hi again, player. How can I #help you this time?", getReply(npcHackim));
		enHackim.step(player, "task");
		assertEquals("Thanks, but I don't have any messages to pass on to #Xin. I can't smuggle so often now... I think Xoderos is beginning to suspect something. Anyway, let me know if there's anything else I can do.", getReply(npcHackim));
		enHackim.step(player, "bye");
		assertEquals("Bye.", getReply(npcHackim));
	}
}
