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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.barracks.BuyerNPC;
import games.stendhal.server.maps.semos.mines.DwarfGuardianNPC;
import games.stendhal.server.maps.wofol.house4.TraderNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class SuppliesForPhalkTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;
	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new DwarfGuardianNPC().configureZone(zone, null);
		new BuyerNPC().configureZone(zone, null);
		new TraderNPC().configureZone(zone, null);

		AbstractQuest quest = new  SuppliesForPhalk();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
		//to get the right reply from phalk for this player
		player.setLevel(100);
		// have to set the xp too as otherwise when some gets added later it goes back to low level
		player.setXP(9753800);
		questSlot = new SuppliesForPhalk().getSlotName();
	}

	@After
	public void tearDown() {
		PlayerTestHelper.removeNPC("Mrotho");
		PlayerTestHelper.removeNPC("Phalk");
		PlayerTestHelper.removeNPC("Wrvil");
	}

	/**
	 * Tests for getSlotName.
	 */
	@Test
	public void testGetSlotName() {
		assertEquals("supplies_for_phalk", questSlot);
	}

	@Test
	public void testStartQuest() {

		npc = SingletonRepository.getNPCList().get("Phalk");
		en = npc.getEngine();


		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("There is something huge there! Everyone is very nervous. Be careful! For entering the dark parts of the mines, push the #stones away which are laying infront of the entrance...", getReply(npc));
		en.step(player, "task");
		assertEquals("I've been here a long time, and I can not leave this place. Could you bring me some food?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Oh, great! I am really hungry and thirsty. 3 #sandwiches, 3 bottles of #beer and 3 glasses of #wine should be enough. Please bring it to me and say #food!", getReply(npc));
		en.step(player, "sandwiches");
		assertEquals("Come on, ask in a bakery!", getReply(npc));
		en.step(player, "beer");
		assertEquals("In an INN of course!", getReply(npc));
		en.step(player, "wine");
		assertEquals("In an INN of course!", getReply(npc));
		en.step(player, "food");
		assertEquals("Do you have 3 sandwiches, 3 bottles of beer and 3 glasses of wine?", getReply(npc));

        // say no
		en.step(player, "no");
		assertEquals("Pff! Then go away! But be sure, you will not get a reward if you don't bring me the items!", getReply(npc));
		en.step(player, "food");
		assertEquals("Do you have 3 sandwiches, 3 bottles of beer and 3 glasses of wine?", getReply(npc));

		// lie
		en.step(player, "yes");
		assertEquals("I've been around a long time and what's more I am really hungry. You can't trick me.", getReply(npc));

		// get the food
		PlayerTestHelper.equipWithStackableItem(player, "sandwich", 3);
		PlayerTestHelper.equipWithStackableItem(player, "beer", 3);
		PlayerTestHelper.equipWithStackableItem(player, "wine", 3);

		// remember xp
		final int xp = player.getXP();
		en.step(player, "food");
		assertEquals("Do you have 3 sandwiches, 3 bottles of beer and 3 glasses of wine?", getReply(npc));
		en.step(player, "yes");
		// [16:26] redlads earns 600 experience points.

		assertFalse(player.isEquipped("sandwich"));
		assertFalse(player.isEquipped("beer"));
		assertFalse(player.isEquipped("wine"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getQuest(questSlot), is("clothes;none;none"));

		assertEquals("Yay, thank you!!! There is another thing you could do for me: my clothes are old and dirty and I need a new #cloak and a new #armor. Please bring them to me and say #clothes.", getReply(npc));
		en.step(player, "cloak");
		assertEquals("I know Wrvil (he lives in Wofol) has a new cloak for me. Just tell him my name.", getReply(npc));
		en.step(player, "armor");
		assertEquals("Mrotho (he lives in Ados) told me he will look for a golden armor for me. Just tell him my name.", getReply(npc));
		en.step(player, "clothes");
		assertEquals("Hm, I want the special golden #armor from Mrotho and the dwarf #cloak from Wrvil. Tell them my name and they will give you what they made me.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("There is something huge there! Everyone is very nervous. Be careful! For entering the dark parts of the mines, push the #stones away which are laying infront of the entrance...", getReply(npc));
		en.step(player, "task");
		assertEquals("I am waiting for you to bring me new #clothes from Wrvil and Mrotho.", getReply(npc));
		en.step(player, "clothes");
		assertEquals("Hm, I want the special golden #armor from Mrotho and the dwarf #cloak from Wrvil. Tell them my name and they will give you what they made me.", getReply(npc));
		en.step(player, "task");
		assertEquals("I am waiting for you to bring me new #clothes from Wrvil and Mrotho.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// try just getting any old golden armor and dwarf cloak, will it do?
		PlayerTestHelper.equipWithItem(player, "golden armor");
		PlayerTestHelper.equipWithItem(player, "dwarf cloak");

		en.step(player, "hi");
		assertEquals("There is something huge there! Everyone is very nervous. Be careful! For entering the dark parts of the mines, push the #stones away which are laying infront of the entrance...", getReply(npc));
		en.step(player, "task");
		assertEquals("I am waiting for you to bring me new #clothes from Wrvil and Mrotho.", getReply(npc));
		en.step(player, "clothes");
		assertEquals("Hm, I want the special golden #armor from Mrotho and the dwarf #cloak from Wrvil. Tell them my name and they will give you what they made me.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

	}

    @Test
	public void testMrotho() {

	    npc = SingletonRepository.getNPCList().get("Mrotho");
		en = npc.getEngine();

		player.setQuest(questSlot,"clothes;none;none");

		en.step(player, "hi");
		assertEquals("Greetings. Have you come to enlist as a soldier?", getReply(npc));
		en.step(player, "phalk");
		assertEquals("Ooops, his armor...wait.. where is it.. aah here it is. Did he give you the #payment for me too?", getReply(npc));
		en.step(player, "payment");
		assertEquals("Well.. the armor will cost 20 gold bars. Do you have them?", getReply(npc));

		// say no
		en.step(player, "no");
		assertEquals("Bah! I will not give you the armor without payment!", getReply(npc));
		en.step(player, "payment");
		assertEquals("The armor will cost 20 gold bars. Do you have them?", getReply(npc));

		// lie
		en.step(player, "yes");
		assertEquals("Army disciplinary actions are pretty serious, so don't lie to me.", getReply(npc));

		// get the gold bars
		PlayerTestHelper.equipWithStackableItem(player, "gold bar", 20);
		final int xp = player.getXP();

		en.step(player, "payment");
		assertEquals("The armor will cost 20 gold bars. Do you have them?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Ok, here you are.", getReply(npc));
		// [16:27] redlads earns 200 experience points.
		assertFalse(player.isEquipped("gold bar"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getQuest(questSlot), is("clothes;none;armor"));
		assertTrue(player.isEquipped("golden armor"));
		// could also check the infostring and description. maybe just fire the infostring checking method?

		en.step(player, "bye");
		assertEquals("Goodbye, comrade.", getReply(npc));

		// test for subsequent reply to 'Phalk'
		en.step(player, "hi");
		assertEquals("Greetings. Have you come to enlist as a soldier?", getReply(npc));
		npc.remove("text");
		en.step(player, "phalk");
		assertEquals("Take the armor I gave you to Phalk. If you lost it the replacement price is 10000 money. Do you want to pay for a replacement for Phalk?", getReply(npc));
		en.step(player, "no");
		assertEquals("Okay, but Phalk will only accept golden armor from me, with his name on it.", getReply(npc));
		en.step(player, "phalk");
		assertEquals("Take the armor I gave you to Phalk. If you lost it the replacement price is 10000 money. Do you want to pay for a replacement for Phalk?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Sorry, you don't have enough money.", getReply(npc));

		PlayerTestHelper.equipWithStackableItem(player, "money", 10000);
		en.step(player, "phalk");
		assertEquals("Take the armor I gave you to Phalk. If you lost it the replacement price is 10000 money. Do you want to pay for a replacement for Phalk?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Ok, here you are.", getReply(npc));

		en.step(player, "task");
		assertEquals("Oh, thanks but no thanks. I don't need anything.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, comrade.", getReply(npc));

    }

    @Test
	public void testWrvil() {

	    npc = SingletonRepository.getNPCList().get("Wrvil");
		en = npc.getEngine();

		player.setQuest(questSlot,"clothes;none;armor");

		en.step(player, "hi");
		assertEquals("Welcome to the Kobold City of Wofol. I hope you come in peace.", getReply(npc));
		en.step(player, "phalk");
		assertEquals("Aaah, his cloak... yes, it is ready. But I am still waiting for the #payment!", getReply(npc));
		en.step(player, "payment");
		assertEquals("Oh yes! it costs 20 steel arrows. Our victims don't bring them back ;) Do you have them?", getReply(npc));

		// say no
		en.step(player, "no");
		assertEquals("So I can not give you the cloak! First the payment!", getReply(npc));
		en.step(player, "payment");
		assertEquals("Oh yes! it costs 20 steel arrows. Our victims don't bring them back ;) Do you have them?", getReply(npc));

		// lie
		en.step(player, "yes");
		assertEquals("Your type are all liars, aren't they? Come back when you have the payment.", getReply(npc));
		en.step(player, "payment");
		assertEquals("Oh yes! it costs 20 steel arrows. Our victims don't bring them back ;) Do you have them?", getReply(npc));

		// get the arrows
		PlayerTestHelper.equipWithStackableItem(player, "steel arrow", 20);
		final int xp = player.getXP();

		en.step(player, "yes");
		assertEquals("Ok, here you are.", getReply(npc));
		// [16:28] redlads earns 200 experience points.
		assertFalse(player.isEquipped("steel arrow"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getQuest(questSlot), is("clothes;cloak;armor"));
		assertTrue(player.isEquipped("dwarf cloak"));
		// could also check the infostring and description. maybe just fire the infostring checking method?

		en.step(player, "bye");
		assertEquals("Bye, and please don't attack too many of my friends.", getReply(npc));

		// test for subsequent reply to 'Phalk'
	    en.step(player, "hi");
		assertEquals("Welcome to the Kobold City of Wofol. I hope you come in peace.", getReply(npc));
		npc.remove("text");

		en.step(player, "phalk");
		assertEquals("Take the cloak I gave you to Phalk. If you lost it the replacement price is 250 money. Do you want to pay for a replacement for Phalk?", getReply(npc));
		en.step(player, "no");
		assertEquals("Okay, but Phalk will only accept a dwarf cloak from me, with his name sewn in.", getReply(npc));
		en.step(player, "phalk");
		assertEquals("Take the cloak I gave you to Phalk. If you lost it the replacement price is 250 money. Do you want to pay for a replacement for Phalk?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Sorry, you don't have enough money.", getReply(npc));

		PlayerTestHelper.equipWithStackableItem(player, "money", 250);
		en.step(player, "phalk");
		assertEquals("Take the cloak I gave you to Phalk. If you lost it the replacement price is 250 money. Do you want to pay for a replacement for Phalk?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Ok, here you are.", getReply(npc));


		en.step(player, "bye");
		assertEquals("Bye, and please don't attack too many of my friends.", getReply(npc));
    }

    @Test
	public void testEndQuest() {

		npc = SingletonRepository.getNPCList().get("Phalk");
		en = npc.getEngine();

		player.setQuest(questSlot,"clothes;cloak;armor");

		en.step(player, "hi");
		assertEquals("There is something huge there! Everyone is very nervous. Be careful! For entering the dark parts of the mines, push the #stones away which are laying infront of the entrance...", getReply(npc));
		en.step(player, "task");
		assertEquals("I am waiting for you to bring me new #clothes from Wrvil and Mrotho.", getReply(npc));

		// yes he still replies to these old ones but it does not seem bad
		en.step(player, "wine");
		assertEquals("In an INN of course!", getReply(npc));
		en.step(player, "beer");
		assertEquals("In an INN of course!", getReply(npc));
		en.step(player, "sandwich");
		assertEquals("Come on, ask in a bakery!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// the armor and cloak must be the special ones, we tested ones without infostring already.
		Item armor = ItemTestHelper.createItem("golden armor", 1);
		armor.setInfoString("Phalk");
		player.getSlot("bag").add(armor);

		Item cloak = ItemTestHelper.createItem("dwarf cloak", 1);
		cloak.setInfoString("Phalk");
		player.getSlot("bag").add(cloak);

		final int xp = player.getXP();

		en.step(player, "hi");
		assertEquals("There is something huge there! Everyone is very nervous. Be careful! For entering the dark parts of the mines, push the #stones away which are laying infront of the entrance...", getReply(npc));
		en.step(player, "task");
		assertEquals("I am waiting for you to bring me new #clothes from Wrvil and Mrotho.", getReply(npc));
		en.step(player, "clothes");
		// [16:30] redlads earns 4000 experience points.
		assertEquals("Oh yeah! Thank you so much! Payment?? Erm... *cough* I will give you my old armor as a reward.", getReply(npc));

		assertFalse(player.isEquipped("golden armor"));
		assertFalse(player.isEquipped("dwarf cloak"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getQuest(questSlot), is("done"));
		assertTrue(player.isEquipped("dwarvish armor"));

		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// test reply to quest message when task is completed.
		en.step(player, "hi");
		assertEquals("There is something huge there! Everyone is very nervous. Be careful! For entering the dark parts of the mines, push the #stones away which are laying infront of the entrance...", getReply(npc));
		en.step(player, "task");
		assertEquals("Thanks for getting me the food and clothes. I think I can stand here warning people for some months longer now.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

	}
}
