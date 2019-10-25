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

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.city.MakeupArtistNPC;
import games.stendhal.server.maps.ados.coast.FerryConveyerNPC;
import games.stendhal.server.maps.ados.magician_house.WizardNPC;
import games.stendhal.server.maps.ados.outside.AnimalKeeperNPC;
import games.stendhal.server.maps.ados.outside.VeterinarianNPC;
import games.stendhal.server.maps.ados.rock.WeaponsCollectorNPC;
import games.stendhal.server.maps.ados.wall.HolidayingManNPC;
import games.stendhal.server.maps.orril.magician_house.WitchNPC;
import games.stendhal.server.maps.quests.houses.HouseBuyingMain;
import games.stendhal.server.maps.semos.bakery.ChefNPC;
import games.stendhal.server.maps.semos.dungeon.SheepBuyerNPC;
import games.stendhal.server.maps.semos.jail.GuardNPC;
import games.stendhal.server.maps.semos.plains.MillerNPC;
import games.stendhal.server.maps.semos.tavern.BowAndArrowSellerNPC;
import games.stendhal.server.maps.semos.village.SheepSellerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class PizzaDeliveryTest {


	private static String questSlot = "pizza_delivery";

	private Player player = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		// this is convoluted .. we need this for Blackjack quest
		// which must be loaded because ramon is defined there!
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("-1_athor_ship_w2"));
		// a lot of NPCs to load for this quest!

		new WizardNPC().configureZone(zone, null);
		new MakeupArtistNPC().configureZone(zone, null);
		new AnimalKeeperNPC().configureZone(zone, null);
		// Dr feelgood isn't part of pizza quest but katinka is and we have to load zoofood
		// so that we have her correct 'hi' and he's in that.
		new VeterinarianNPC().configureZone(zone, null);
		new WeaponsCollectorNPC().configureZone(zone, null);
		new SheepSellerNPC().configureZone(zone, null);
		new BowAndArrowSellerNPC().configureZone(zone, null);
		new WitchNPC().configureZone(zone, null);
		new HolidayingManNPC().configureZone(zone, null);
		new SheepBuyerNPC().configureZone(zone, null);
		new MillerNPC().configureZone(zone, null);
		new ChefNPC().configureZone(zone, null);
		new GuardNPC().configureZone(zone, null);
		new FerryConveyerNPC().configureZone(zone, null);

		//	ramon is added in this quest - so we have to load this before we load pizza one.
		final AbstractQuest blackjackquest = new Blackjack();
		blackjackquest.addToWorld();

		// Taxman is required by HouseBuying
		new HouseBuyingMain().createAthorNPC(zone);

		final AbstractQuest quest = new PizzaDelivery();
		quest.addToWorld();


		// katinka's hi response is defined here
		final AbstractQuest zooquest = new ZooFood();
		zooquest.addToWorld();
	}


	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayerWithOutFit("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		// first the basics, get a task, and deliver the pizza

		final SpeakerNPC leander = SingletonRepository.getNPCList().get("Leander");
		en = leander.getEngine();

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(leander));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(leander));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(leander));
		en.step(player, "yes");
		assertTrue(getReply(leander).startsWith("You must bring this Pizza "));
		en.step(player, "eliza");
		assertTrue(player.isEquipped("pizza"));
		assertEquals("Eliza works for the Athor Island ferry service. You'll find her at the docks south of the Ados swamps.", getReply(leander));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(leander));
		assertTrue(player.hasQuest(questSlot));

		// we choose to make it so that he had asked us to take a pizza to a specific npc so we have to remove the
		// old pizza and add the correct new flavour of pizza

		player.drop("pizza");
		Item item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza del Mare");
		player.getSlot("bag").add(item);

		final SpeakerNPC eliza = SingletonRepository.getNPCList().get("Eliza");
		en = eliza.getEngine();
		int xp = player.getXP();
		player.setQuest(questSlot, "Eliza;" + System.currentTimeMillis());
		en.step(player, "hi");
		assertEquals("Welcome to the #ferry service to #Athor #island! How can I #help you?", getReply(eliza));
		en.step(player, "pizza");
		// [16:55] kymara earns 30 experience points.
		assertEquals("Incredible! It's still hot! Here, buy something nice from these 170 pieces of gold!", getReply(eliza));
		assertFalse(player.isEquipped("pizza"));
		assertTrue(player.isEquipped("money"));
		assertThat(player.getXP(), greaterThan(xp));
		en.step(player, "bye");
		assertEquals("Goodbye!", getReply(eliza));

		// try saying pizza when you don't have one nor a quest for one
		en.step(player, "hi");
		assertEquals("Welcome to the #ferry service to #Athor #island! How can I #help you?", getReply(eliza));
		en.step(player, "pizza");
		assertEquals("A pizza? Where?", getReply(eliza));
		en.step(player, "bye");
		assertEquals("Goodbye!", getReply(eliza));

		SpeakerNPC npc1 = SingletonRepository.getNPCList().get("Leander");
		en = npc1.getEngine();
		// test rejecting quest
		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(npc1));
		en.step(player, "no");
		assertEquals("Too bad. I hope my daughter #Sally will soon come back from her camp to help me with the deliveries.", getReply(npc1));
		en.step(player, "sally");
		assertEquals("My daughter Sally might be able to help you get ham. She's a scout, you see; I think she's currently camped out south of Or'ril Castle.", getReply(npc1));
		en.step(player, "bye.");
		assertEquals("Bye.", getReply(npc1));

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(npc1));
		en.step(player, "yes");
		assertTrue(getReply(npc1).startsWith("You must bring this Pizza "));
		en.step(player, "jenny");
		assertEquals("Jenny owns a mill in the plains north and a little east of Semos.", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));
		assertTrue(player.hasQuest(questSlot));

		npc1 = SingletonRepository.getNPCList().get("Jenny");
		en = npc1.getEngine();
		xp = player.getXP();
		// testing taking too long to bring the pizza
		player.setQuest(questSlot, "Jenny;0");
		// we choose to make it so that he had asked us to take a pizza to a specific npc so we have to remove the
		// old pizza and add the correct new flavour of pizza
		player.drop("pizza");
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Margherita");
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc1));
		en.step(player, "pizza");
		assertFalse(player.isEquipped("pizza"));
		assertTrue(player.isQuestCompleted(questSlot));
		assertThat(player.getXP(), greaterThan(xp));
		// [16:58] kymara earns 5 experience points.
		assertEquals("It's a shame. Your pizza service can't deliver a hot pizza although the bakery is just around the corner.", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));

		// test trying to get a pizza order with a slime outfit
		npc1 = SingletonRepository.getNPCList().get("Leander");
		en = npc1.getEngine();

		final Outfit SLIME = new Outfit(91, 0, 98);
		player.setOutfit(SLIME);

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("Sorry, you can't wear our pizza delivery uniform looking like that. If you get changed, you can ask about the #task again.", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));


		// ok, we've already tested getting pizza orders but now we're trying different npcs
		npc1 = SingletonRepository.getNPCList().get("Leander");
		en = npc1.getEngine();

		final Outfit NOTSLIME = new Outfit(Integer.valueOf(00), Integer.valueOf(00), Integer.valueOf(00), Integer.valueOf(00), null, Integer.valueOf(01), Integer.valueOf(01), Integer.valueOf(01), Integer.valueOf(01));
		player.setOutfit(NOTSLIME);

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(npc1));
		en.step(player, "yes");
		assertTrue(getReply(npc1).startsWith("You must bring this Pizza "));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));

		npc1 = SingletonRepository.getNPCList().get("Katinka");
		en = npc1.getEngine();
		player.setQuest(questSlot, "Katinka;" + System.currentTimeMillis());
		// we choose to make it so that he had asked us to take a pizza to a DIFFERENT npc than Katinka
		// so we have to remove the
		// old pizza and add the WRONG new flavour of pizza
		player.drop("pizza");
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Margherita");
		player.getSlot("bag").add(item);

		// on time
		en.step(player, "hi");
		assertEquals("Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("No, thanks. I like Pizza Vegetale better.", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Goodbye!", getReply(npc1));

		// player find correct pizza
		player.drop("pizza");
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Vegetale");
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("Yay! My Pizza Vegetale! Here, you can have 100 pieces of gold as a tip!", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Goodbye!", getReply(npc1));

		npc1 = SingletonRepository.getNPCList().get("Leander");
		en = npc1.getEngine();

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(npc1));
		en.step(player, "yes");
		assertTrue(getReply(npc1).startsWith("You must bring this Pizza "));
		en.step(player, "fidorea");
		assertEquals("Fidorea lives in Ados city. She is a makeup artist. You'll need to walk east from here.", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));
		player.drop("pizza");
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Napoli");
		player.getSlot("bag").add(item);

		npc1 = SingletonRepository.getNPCList().get("Fidorea");
		en = npc1.getEngine();
		player.setQuest(questSlot, "Fidorea;" + System.currentTimeMillis());
		// on time
		en.step(player, "hi");
		assertEquals("Hi, there. Do you need #help with anything?", getReply(npc1));
		en.step(player, "pizza");
		// [16:59] kymara earns 20 experience points.
		assertEquals("Thanks a lot! You're a born pizza deliverer. You can have these 150 pieces of gold as a tip!", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye, come back soon.", getReply(npc1));
		assertFalse(player.isEquipped("pizza"));

		// try taking any pizza to fidorea when we didn't have a quest slot activated
		item = ItemTestHelper.createItem("pizza");
		player.getSlot("bag").add(item);
		player.removeQuest(questSlot);
		assertFalse(player.hasQuest(questSlot));

		en.step(player, "hi");
		assertEquals("Hi, there. Do you need #help with anything?", getReply(npc1));
		en.step(player, "pizza");
		// The flavor won't match
		assertEquals("No, thanks. I like Pizza Napoli better.", getReply(npc1));
		assertTrue(player.isEquipped("pizza"));
		en.step(player, "bye");
		assertEquals("Bye, come back soon.", getReply(npc1));
		// put the extra one on the ground now, we don't want it
		player.drop("pizza");

		npc1 = SingletonRepository.getNPCList().get("Leander");
		en = npc1.getEngine();

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(npc1));
		en.step(player, "yes");
		assertTrue(getReply(npc1).startsWith("You must bring this Pizza "));
		en.step(player, "jenny");
		assertEquals("Jenny owns a mill in the plains north and a little east of Semos.", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));

		// we choose to make it so that he had asked us to take a pizza to a specific npc so we have to remove the
		// old pizza and add the correct new flavour of pizza

		player.drop("pizza");
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Margherita");
		player.getSlot("bag").add(item);
		npc1 = SingletonRepository.getNPCList().get("Jenny");
		en = npc1.getEngine();
		player.setQuest(questSlot, "Jenny;" + System.currentTimeMillis());
		// on time
		en.step(player, "hi");
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc1));
		en.step(player, "pizza");
		// [17:00] kymara earns 10 experience points.
		assertEquals("Ah, you brought my Pizza Margherita! Very nice of you! Here, take 20 coins as a tip!", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));

		npc1 = SingletonRepository.getNPCList().get("Leander");
		en = npc1.getEngine();

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(npc1));
		en.step(player, "yes");
		assertTrue(getReply(npc1).startsWith("You must bring this Pizza "));
		en.step(player, "katinka");
		assertEquals("Katinka takes care of the animals at the Ados Wildlife Refuge. That's north east of here, on the way to Ados city.", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));

		npc1 = SingletonRepository.getNPCList().get("Katinka");
		en = npc1.getEngine();

		player.drop("pizza");
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Vegetale");
		player.getSlot("bag").add(item);
		// be late
		player.setQuest(questSlot, "Katinka;0");

		en.step(player, "hi");
		assertEquals("Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("Eek. I hate cold pizza. I think I'll feed it to the animals.", getReply(npc1));
		// [17:10] kymara earns 10 experience points.
		en.step(player, "bye");
		assertEquals("Goodbye!", getReply(npc1));

		npc1 = SingletonRepository.getNPCList().get("Leander");
		en = npc1.getEngine();

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(npc1));
		en.step(player, "yes");
		assertTrue(getReply(npc1).startsWith("You must bring this Pizza "));
		en.step(player, "cyk");
		assertEquals("Cyk is currently on holiday on Athor Island. You'll easily recognize him by his blue hair. Go South East to find Athor ferry.", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));
		player.setQuest(questSlot, "Cyk;" + System.currentTimeMillis());
		player.drop("pizza");
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Hawaii");
		player.getSlot("bag").add(item);
		// test ask leander for task again before completing last
		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "pizza");
		assertEquals("I need someone who helps me delivering pizza. Maybe you could do that #task.", getReply(npc1));
		en.step(player, "task");
		assertEquals("You still have to deliver a pizza to Cyk, and hurry!", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));

		npc1 = SingletonRepository.getNPCList().get("Cyk");
		en = npc1.getEngine();
		// on time
		en.step(player, "hi");
		assertEquals("Hello, player.", getReply(npc1));
		en.step(player, "pizza");
		// [17:10] kymara earns 50 experience points.
		assertEquals("Wow, I never believed you would really deliver this half over the world! Here, take these Pizza Hawaii bucks!", getReply(npc1));

		npc1 = SingletonRepository.getNPCList().get("Leander");
		en = npc1.getEngine();

		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(npc1));
		en.step(player, "task");
		assertEquals("I need you to quickly deliver a hot pizza. If you're fast enough, you might get quite a nice tip. So, will you do it?", getReply(npc1));
		en.step(player, "yes");
		assertTrue(getReply(npc1).startsWith("You must bring this Pizza "));
		en.step(player, "haizen");
		assertEquals("Haizen is a magician who lives in a hut near the road to Ados. You'll need to walk east and north from here.", getReply(npc1));

		player.drop("pizza");
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Diavolo");
		player.getSlot("bag").add(item);

		npc1 = SingletonRepository.getNPCList().get("Haizen");
		en = npc1.getEngine();
		player.setQuest(questSlot, "Haizen;" + System.currentTimeMillis());
		// on time
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc1));
		en.step(player, "pizza");
		// [17:11] kymara earns 15 experience points.
		assertEquals("Ah, my Pizza Diavolo! And it's fresh out of the oven! Take these 80 coins as a tip!", getReply(npc1));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc1));

		// Check coming back to leander after a failure
		// Add a test pizza, one that leander should not steal
		item = ItemTestHelper.createItem("pizza");
		player.getSlot("bag").add(item);
		// and then the pizza that belongs to Haizen
		item = ItemTestHelper.createItem("pizza");
		item.setInfoString("Pizza Diavolo");
		player.getSlot("bag").add(item);
		// Haizen allows 4 min delay. Set the time stamp 5min  to the past
		player.setQuest(questSlot, "Haizen;" + (System.currentTimeMillis() - 1000 * 60 * 5));
		en = leander.getEngine();
		en.setCurrentState(ConversationStates.IDLE);
		en.step(player, "hi");
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", getReply(leander));
		en.step(player, "task");
		assertEquals("I see you failed to deliver the pizza to Haizen in time. Are you sure you will be more reliable this time?", getReply(leander));
		// Leander will take the pizza
		List<Item> pizzas = player.getAllEquipped("pizza");
		assertEquals(pizzas.size(), 1);
		assertEquals(pizzas.get(0).getInfoString(), null);
		// after this we should be like any other player wanting to deliver a pizza, phew
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		en.step(player, "bye");
		assertEquals("Bye.", getReply(leander));
	}
}
