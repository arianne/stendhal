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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
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
import games.stendhal.server.maps.semos.city.HealerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class HerbsForCarmenTest {


	private static String questSlot = "herbs_for_carmen";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new HealerNPC().configureZone(zone, null);

		final AbstractQuest quest = new HerbsForCarmen();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		player.setLevel(10);
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Carmen");
		en = npc.getEngine();

		// say hello for the first time but don't accept or reject quest
		en.step(player, "hi");
		assertEquals("Hi, if I can #help, just say.", getReply(npc));
		en.step(player, "quest");
		assertEquals("Hm, Do you know what I do for a living?", getReply(npc));
		en.step(player, "no");
		assertEquals("I am Carmen. I can heal you for free, until your powers become too strong. Many warriors ask for my help. Now my #ingredients are running out and I need to fill up my supplies.", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("So many people are asking me to heal them. That uses many ingredients and now my inventories are near empty. Can you help me to fill them up?", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// ------------------------------------------------------------------
		// return, say you know her, and reject quest
		en.step(player, "hi");
		assertEquals("Hi, if I can #help, just say.", getReply(npc));
		en.step(player, "quest");
		assertEquals("Hm, Do you know what I do for a living?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Great, so you know my job. My supply of healing #ingredients is running low.", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("So many people are asking me to heal them. That uses many ingredients and now my inventories are near empty. Can you help me to fill them up?", getReply(npc));
		en.step(player, "no");
		assertEquals("Hargh, thats not good! But ok, its your choice. But remember, I will tell the others that I can't heal them much longer, because YOU didn't want to help me.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		assertThat(player.getQuest(questSlot), is("rejected"));

		// ------------------------------------------------------------------------
		// return and reject it again (could check karma levels each time it's rejected)
		en.step(player, "hi");
		assertEquals("Hi, if I can #help, just say.", getReply(npc));
		en.step(player, "quest");
		assertEquals("Hey, are you going to help me yet?", getReply(npc));
		en.step(player, "no");
		assertEquals("Hargh, thats not good! But ok, its your choice. But remember, I will tell the others that I can't heal them much longer, because YOU didn't want to help me.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// ------------------------------------------------------------------------
		// agree to help and check about each ingredient
		en.step(player, "hi");
		assertEquals("Hi, if I can #help, just say.", getReply(npc));
		en.step(player, "quest");
		assertEquals("Hey, are you going to help me yet?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Oh how nice. Please bring me those ingredients: 3 #apples, 5 #'sprigs of arandula', a #'button mushroom', a #porcino, and 2 #'pieces of wood'.", getReply(npc));
		en.step(player, "apples");
		assertEquals("Apples have many vitamins, I saw some apple trees on the east of semos.", getReply(npc));
		en.step(player, "sprigs of arandula");
		assertEquals("North of Semos, near the tree grove, grows a herb called arandula. Here is a picture so you know what to look for.", getReply(npc));
		en.step(player, "button mushroom");
		assertEquals("Someone told me there are many different mushrooms in the Semos forest, follow the path south from here.", getReply(npc));
		en.step(player, "porcini");
		assertEquals("Someone told me there are many different mushrooms in the Semos forest, follow the path south from here.", getReply(npc));
		en.step(player, "pieces of wood");
		assertEquals("Wood is great resource with many different purposes. Of course you can find logs in a forest.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// --------------------------------------------------------------
		// Return and check what is needed, say you don't have anything
		en.step(player, "hi");
		assertEquals("Hi again. I can #heal you, or if you brought me #ingredients I'll happily take those!", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("I need 3 #apples, 5 #'sprigs of arandula', a #'button mushroom', a #porcino, and 2 #'pieces of wood'. Did you bring something?", getReply(npc));
		en.step(player, "no");
		assertEquals("Ok, well just let me know if I can #help you with anything else.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------
		// lie about having apple
		en.step(player, "hi");
		assertEquals("Hi again. I can #heal you, or if you brought me #ingredients I'll happily take those!", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("I need 3 #apples, 5 #'sprigs of arandula', a #'button mushroom', a #porcino, and 2 #'pieces of wood'. Did you bring something?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Great, what did you bring?", getReply(npc));
		en.step(player, "apple");
		assertEquals("You don't have an apple with you!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// ---------------------------------------------------
		// don't take ingredients
		en.step(player, "hi");
		assertEquals("Hi again. I can #heal you, or if you brought me #ingredients I'll happily take those!", getReply(npc));
		en.step(player, "no");
		assertEquals("Ok, well just let me know if I can #help you with anything else.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		Item apple= ItemTestHelper.createItem("apple", 1);
		player.getSlot("bag").add(apple);
		Item arandula = ItemTestHelper.createItem("arandula", 3);
		player.getSlot("bag").add(arandula);
		Item porcini = ItemTestHelper.createItem("porcini", 1);
		player.getSlot("bag").add(porcini);

		//-----------------------------------------------------------------
		// bring some of the ingredients. try to take one twice

		en.step(player, "hi");
		assertEquals("Hi again. I can #heal you, or if you brought me #ingredients I'll happily take those!", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("I need 3 #apples, 5 #'sprigs of arandula', a #'button mushroom', a #porcino, and 2 #'pieces of wood'. Did you bring something?", getReply(npc));
		en.step(player, "wood");
		assertEquals("You don't have a piece of wood with you!", getReply(npc));
		en.step(player, "woos"); // misspelled "wood"
		assertEquals("You don't have a piece of wood with you!", getReply(npc));
		en.step(player, "arandula");
		assertEquals("Good, do you have anything else?", getReply(npc));
		en.step(player, "apple");
		assertEquals("Good, do you have anything else?", getReply(npc));
		en.step(player, "porcino");
		assertEquals("Good, do you have anything else?", getReply(npc));
		en.step(player, "porcini");
		assertEquals("You have already brought 1 porcino for me but thank you anyway.", getReply(npc));
		en.step(player, "porcinis"); // misspelled "porcini"
		assertEquals("You have already brought 1 porcino for me but thank you anyway.", getReply(npc));
		en.step(player, "no");
		assertEquals("Ok, well just let me know if I can #help you with anything else.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		final int xp = player.getXP();
		Item apples2= ItemTestHelper.createItem("apple", 2);
		player.getSlot("bag").add(apples2);
		Item arandula2 = ItemTestHelper.createItem("arandula", 2);
		player.getSlot("bag").add(arandula2);
		Item wood = ItemTestHelper.createItem("wood", 2);
		player.getSlot("bag").add(wood);
		Item mushroom = ItemTestHelper.createItem("button mushroom", 1);
		player.getSlot("bag").add(mushroom);

		// ----------------------------------------------------------
		// bring remaining ingredients and check reward
		en.step(player, "hi");
		assertEquals("Hi again. I can #heal you, or if you brought me #ingredients I'll happily take those!", getReply(npc));
		en.step(player, "ingredients");
		assertEquals("I need 2 #apples, 2 #'sprigs of arandula', a #'button mushroom', and 2 #'pieces of wood'. Did you bring something?", getReply(npc));
		en.step(player, "apple");
		assertEquals("Good, do you have anything else?", getReply(npc));
		en.step(player, "arandula");
		assertEquals("Good, do you have anything else?", getReply(npc));
		en.step(player, "wood");
		assertEquals("Good, do you have anything else?", getReply(npc));
		en.step(player, "button mushroom");
		// [08:23] kymara earns 50 experience points.
		assertEquals("Great! Now I can heal many people for free. Thanks a lot. Take this for your work.", getReply(npc));
		assertThat(player.getXP(), greaterThan(xp));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		assertTrue(player.isQuestCompleted(questSlot));

		en.step(player, "hi");
		assertEquals("Hi, if I can #help, just say.", getReply(npc));
		en.step(player, "quest");
		assertEquals("There's nothing I need right now, thank you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		assertTrue(player.isEquipped("minor potion", 5));
	}
}
