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
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.fado.tavern.MaidNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class SoupTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		ItemTestHelper.generateRPClasses();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("int_fado_tavern");
		MockStendlRPWorld.get().addRPZone(zone);
		new MaidNPC().configureZone(zone, null);


		AbstractQuest quest = new Soup();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Old Mother Helena");
		en = npc.getEngine();
		player.setXP(100);
		en.step(player, "hi");
		assertEquals("Hello, stranger. You look weary from your travels. I know what would #revive you.", getReply(npc));
		en.step(player, "revive");
		assertEquals("My special soup has a magic touch. I need you to bring me the #ingredients.", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("I need 9 ingredients before I make the soup: #carrot, #spinach, #courgette, #collard, #salad, #onion, #cauliflower, #broccoli, and #leek. Will you collect them?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh, never mind. It's your loss.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, all you customers do work me hard ...", getReply(npc));
		en.step(player, "hi");
		assertEquals("Hello, stranger. You look weary from your travels. I know what would #revive you.", getReply(npc));
		en.step(player, "revive");
		assertEquals("My special soup has a magic touch. I need you to bring me the #ingredients.", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("I need 9 ingredients before I make the soup: #carrot, #spinach, #courgette, #collard, #salad, #onion, #cauliflower, #broccoli, and #leek. Will you collect them?", getReply(npc));
		en.step(player, "yes");
		assertEquals("You made a wise choice. Do you have anything I need already?", getReply(npc));
		en.step(player, "yes");
		assertEquals("What did you bring?", getReply(npc));
		en.step(player, "leek");
		assertEquals("Don't take me for a fool, traveller. You don't have a leek with you.", getReply(npc));
		en.step(player, "spinach");
		assertEquals("Don't take me for a fool, traveller. You don't have a spinach with you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		// [16:51] Removed contained home scroll item with ID 6 from bag
		// [16:52] You see spinach. It is enriched with vitamins. Stats are (HP: 30).
		// summon all except salad and onion in player's bag
		PlayerTestHelper.equipWithItem(player, "carrot");
		PlayerTestHelper.equipWithItem(player, "spinach");
		PlayerTestHelper.equipWithItem(player, "courgette");
		PlayerTestHelper.equipWithItem(player, "collard");
		PlayerTestHelper.equipWithItem(player, "cauliflower");
		PlayerTestHelper.equipWithItem(player, "broccoli");
		PlayerTestHelper.equipWithItem(player, "leek");
		en.step(player, "hi");
		assertEquals("Welcome back! I hope you collected some #ingredients for the soup, or #everything.", getReply(npc));
		en.step(player, "everything");
		assertEquals("You didn't have all the ingredients I need. I still need 2 ingredients: #salad and #onion. You'll get bad karma if you keep making mistakes like that!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		en.step(player, "hi");
		assertEquals("Welcome back! I hope you collected some #ingredients for the soup, or #everything.", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("I still need 2 ingredients: #salad and #onion. Did you bring anything I need?", getReply(npc));
		PlayerTestHelper.equipWithItem(player, "salad");
		PlayerTestHelper.equipWithItem(player, "onion");
		en.step(player, "salad");
		assertEquals("Thank you very much! What else did you bring?", getReply(npc));
		en.step(player, "onion");
		// [16:52] madmetzger earns 20 experience points.
		assertEquals("The soup's on the table for you. It will heal you. My magical method in making the soup has given you a little karma too.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, all you customers do work me hard ...", getReply(npc));
		assertEquals(player.getXP(), 120);
		en.step(player, "hi");
		assertEquals("I hope you don't want more soup, because I haven't finished washing the dishes. Please check back in 10 minutes.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, all you customers do work me hard ...", getReply(npc));
	}
}
