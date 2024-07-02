/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.oneOf;
import static org.hamcrest.Matchers.startsWith;
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
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.townhall.MayorNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class DailyItemQuestTest {


	private static String questSlot = "daily_item";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new MayorNPC().configureZone(zone, null);

		final AbstractQuest quest = new DailyItemQuest();
		quest.addToWorld();
	}

	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		assertFalse(player.hasQuest(questSlot));
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Mayor Chalmers");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "task");
		assertThat(getReply(npc), startsWith("Ados is in need of supplies. Go fetch "));
		en.step(player, "complete");
		assertThat(getReply(npc), startsWith("You didn't fetch "));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		player.setQuest(questSlot, "pina colada;100");
		Item item = ItemTestHelper.createItem("pina colada");
		player.getSlot("bag").add(item);
		final int xp = player.getXP();

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "complete");
		assertFalse(player.isEquipped("pina colada"));
		assertThat(player.getXP(), greaterThan(xp));
		assertTrue(player.isQuestCompleted(questSlot));
		// [10:50] kymara earns 455960 experience points.
		assertEquals("Good work! Let me thank you on behalf of the people of Ados!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "task");
		assertThat(getReply(npc),
				is(oneOf("I can only give you a new quest once a day. Please check back in 24 hours.",
						"I can only give you a new quest once a day. Please check back in 1 day.")));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		// -----------------------------------------------
		player.setQuest(questSlot, "done;0");
		// [10:51] Changed the state of quest 'daily_item' from 'done;1219834233092;1' to 'done;0'
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "task");
		assertThat(getReply(npc), startsWith("Ados is in need of supplies. Go fetch "));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		// -----------------------------------------------

		// [10:53] Changed the state of quest 'daily_item' from 'dwarf cloak;1219834342834;0' to 'dwarf cloak;0'
		player.setQuest(questSlot, "dwarf cloak;0");
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "task");
		assertEquals("You're already on a quest to fetch a dwarf cloak. Say #complete if you brought it! Perhaps there are no supplies of that left at all! You could fetch #another item if you like, or return with what I first asked you.", getReply(npc));
		en.step(player, "another");
		assertThat(getReply(npc), startsWith("Ados is in need of supplies. Go fetch "));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

	}

	@Test
	public void testExperimentalSandwich() {
		StackableItem sandwich = (StackableItem) ItemTestHelper.createItem("sandwich", 5);
		StackableItem experimentalSandwich = (StackableItem) ItemTestHelper.createItem("sandwich", 5);
		experimentalSandwich.setDescription("You see an experimental sandwich made by Chef Stefan.");
		experimentalSandwich.put("amount", player.getBaseHP() / 2);
		experimentalSandwich.put("frequency", 10);
		experimentalSandwich.put("regen", 50);
		experimentalSandwich.put("persistent", 1);

		npc = SingletonRepository.getNPCList().get("Mayor Chalmers");
		assertThat(npc, notNullValue());
		en = npc.getEngine();

		player.setQuest(questSlot, "sandwich=5");
		assertThat(player.getNumberOfEquipped("sandwich"), is(0));

		// not carrying any sandwiches
		en.step(player, "hi");
		en.step(player, "done");
		assertThat(getReply(npc), is("You didn't fetch 5 sandwiches yet. Go and get it and say"
				+ " #complete only once you're done."));

		// carrying experimental sandwiches only
		player.equip("bag", experimentalSandwich);
		assertThat(player.getNumberOfEquipped("sandwich"), is(5));
		assertThat(player.getNumberOfSubmittableEquipped("sandwich"), is(0));
		assertThat(player.getFirstEquipped("sandwich").isSubmittable(), is(false));
		en.step(player, "done");
		assertThat(getReply(npc), is("There is something strange about those sandwiches. Come back when"
				+ " you have some that are unmodified."));
		assertThat(player.getNumberOfEquipped("sandwich"), is(5));
		assertThat(player.getNumberOfSubmittableEquipped("sandwich"), is(0));

		// carrying experimental sandwiches before unmodified ones
		player.equip("bag", sandwich);
		assertThat(player.getNumberOfEquipped("sandwich"), is(10));
		assertThat(player.getNumberOfSubmittableEquipped("sandwich"), is(5));
		assertThat(player.getFirstEquipped("sandwich").isSubmittable(), is(false));
		en.step(player, "done");
		assertThat(getReply(npc), is("Good work! Let me thank you on behalf of the people of Ados!"));
		assertThat(player.getQuest(questSlot, 0), is("done"));
		assertThat(player.getNumberOfEquipped("sandwich"), is(5));
		assertThat(player.getNumberOfSubmittableEquipped("sandwich"), is(0));

		player.setQuest(questSlot, "sandwich=5");
		player.drop("sandwich", 5);
		assertThat(player.getNumberOfEquipped("sandwhich"), is(0));

		// carrying experimental sandwiches after unmodified ones
		player.equip("bag", sandwich);
		player.equip("bag", experimentalSandwich);
		assertThat(player.getNumberOfEquipped("sandwich"), is(10));
		assertThat(player.getNumberOfSubmittableEquipped("sandwich"), is(5));
		assertThat(player.getFirstEquipped("sandwich").isSubmittable(), is(true));
		en.step(player, "done");
		assertThat(getReply(npc), is("Good work! Let me thank you on behalf of the people of Ados!"));
		assertThat(player.getQuest(questSlot, 0), is("done"));
		assertThat(player.getNumberOfEquipped("sandwich"), is(5));
		assertThat(player.getNumberOfSubmittableEquipped("sandwich"), is(0));

		en.step(player, "bye");
		player.setQuest(questSlot, null);
	}
}
