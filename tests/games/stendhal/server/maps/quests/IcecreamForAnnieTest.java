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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.kalavan.citygardens.IceCreamSellerNPC;
import games.stendhal.server.maps.kalavan.citygardens.LittleGirlNPC;
import games.stendhal.server.maps.kalavan.citygardens.MummyNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class IcecreamForAnnieTest {


	private static String questSlot = "icecream_for_annie";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new IceCreamSellerNPC().configureZone(zone, null);
		new LittleGirlNPC().configureZone(zone, null);
		new MummyNPC().configureZone(zone, null);

		final AbstractQuest quest = new IcecreamForAnnie();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		npc = SingletonRepository.getNPCList().get("Annie Jones");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello, my name is Annie. I am five years old.", getReply(npc));
		en.step(player, "help");
		assertEquals("Ask my mummy.", getReply(npc));
		en.step(player, "job");
		assertEquals("I help my mummy.", getReply(npc));
		en.step(player, "offer");
		assertEquals("I'm a little girl, I haven't anything to offer.", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm hungry! I'd like an ice cream, please. Vanilla, with a chocolate flake. Will you get me one?", getReply(npc));
		en.step(player, "ok");
		assertEquals("Thank you!", getReply(npc));
		assertThat(player.getQuest(questSlot), is("start"));
		en.step(player, "bye");
		assertEquals("Ta ta.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Hello. I'm hungry.", getReply(npc));
		en.step(player, "task");
		assertEquals("Waaaaaaaa! Where is my ice cream ....", getReply(npc));
		en.step(player, "bye");
		assertEquals("Ta ta.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Sam");
		en = npc.getEngine();

		Item item = ItemTestHelper.createItem("money", 30);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Hi. Can I #offer you an ice cream?", getReply(npc));
		en.step(player, "yes");
		assertEquals("An ice cream will cost 30. Do you want to buy it?", getReply(npc));
		en.step(player, "no");

		en.step(player, "offer");
		assertEquals("I sell ice cream.", getReply(npc));
		assertTrue(en.step(player, "buy 0 icecreams"));
		assertEquals("Sorry, how many ice creams do you want to buy?!", getReply(npc));
		en.step(player, "buy ice cream");
		assertEquals("An ice cream will cost 30. Do you want to buy it?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Congratulations! Here is your ice cream!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, enjoy your day!", getReply(npc));
		assertTrue(player.isEquipped("icecream"));

		npc = SingletonRepository.getNPCList().get("Annie Jones");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Mummy says I mustn't talk to you any more. You're a stranger.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Mrs Jones");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello, I see you've met my daughter Annie. I hope she wasn't too demanding. You seem like a nice person.", getReply(npc));
		assertThat(player.getQuest(questSlot), is("mummy"));
		en.step(player, "task");
		assertEquals("Nothing, thank you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye for now.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Annie Jones");
		en = npc.getEngine();

		final int xp = player.getXP();
		final double karma = player.getKarma();
		en.step(player, "hi");
		assertEquals("Yummy! Is that ice cream for me?", getReply(npc));
		en.step(player, "yes");
		// [15:06] kymara earns 500 experience points.
		assertFalse(player.isEquipped("icecream"));
		assertTrue(player.isEquipped("present"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(karma));
		assertTrue(player.getQuest(questSlot).startsWith("eating"));
		assertEquals("Thank you EVER so much! You are very kind. Here, take this present.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Ta ta.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Hello.", getReply(npc));
		en.step(player, "task");
		assertEquals("I've had too much ice cream. I feel sick.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Ta ta.", getReply(npc));

		// -----------------------------------------------


		// -----------------------------------------------
		final double newKarma = player.getKarma();
		// [15:07] Changed the state of quest 'icecream_for_annie' from 'eating;1219676807283' to 'eating;0'
		player.setQuest(questSlot, "eating;0");
		en.step(player, "hi");
		assertEquals("Hello.", getReply(npc));
		en.step(player, "task");
		assertEquals("I hope another ice cream wouldn't be greedy. Can you get me one?", getReply(npc));
		en.step(player, "no");
		assertThat(player.getQuest(questSlot), is("rejected"));
		assertThat(player.getKarma(), lessThan(newKarma));
		assertEquals("Ok, I'll ask my mummy instead.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hello.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Ta ta.", getReply(npc));
	}
}
