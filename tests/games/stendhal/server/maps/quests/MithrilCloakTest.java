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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.abandonedkeep.DwarfBuyerGuyNPC;
import games.stendhal.server.maps.ados.goldsmith.MithrilForgerNPC;
import games.stendhal.server.maps.fado.house.WomanNPC;
import games.stendhal.server.maps.kalavan.castle.MadScientist1NPC;
import games.stendhal.server.maps.kalavan.castle.MadScientist2NPC;
import games.stendhal.server.maps.orril.dwarfmine.BlacksmithNPC;
import games.stendhal.server.maps.semos.caves.BabyDragonSellerNPC;
import games.stendhal.server.maps.semos.pad.DealerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.BabyDragonTestHelper;
import utilities.RPClass.ItemTestHelper;

public class MithrilCloakTest {

	private static String questSlot = "mithril_cloak";
	private static String shieldQuestSlot = "mithrilshield_quest";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		SingletonRepository.getNPCList().clear();
		QuestHelper.setUpBeforeClass();

		BabyDragonTestHelper.generateRPClasses();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("int_ados_sewing_room"));
		new games.stendhal.server.maps.ados.sewingroom.SeamstressNPC().configureZone(zone, null);
		new games.stendhal.server.maps.ados.twilightzone.SeamstressNPC().configureZone(zone, null);
		new BlacksmithNPC().configureZone(zone, null);
		new BabyDragonSellerNPC().configureZone(zone, null);
		new games.stendhal.server.maps.kirdneh.museum.WizardNPC().configureZone(zone, null);
		new games.stendhal.server.maps.magic.house2.WizardNPC().configureZone(zone, null);
		new MadScientist1NPC().configureZone(zone, null);
		new MadScientist2NPC().configureZone(zone, null);
		new MithrilForgerNPC().configureZone(zone, null);
		new WomanNPC().configureZone(zone, null);
		new DealerNPC().configureZone(zone, null);
		new DwarfBuyerGuyNPC().configureZone(zone, null);

		final AbstractQuest quest = new MithrilCloak();
		// Pdiddi's hi response is defined in RainbowBeans
		final AbstractQuest quest2 = new RainbowBeans();
		// player may be mid through cloak collecting quests for josephine
		final AbstractQuest quest3 = new CloakCollector();
		final AbstractQuest quest4 = new CloakCollector2();
		quest.addToWorld();
		quest2.addToWorld();
		quest3.addToWorld();
		quest4.addToWorld();
	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		player.setQuest(shieldQuestSlot, "done");
		// in this test we choose a player who has finished the first cloak collector
		// quest for josephine and is mid way through the second
		// i.e. chose the most complex text case we could think of
		player.setQuest("cloaks_collector", "done");
		player.setQuest("cloaks_collector_2", ";red cloak;elvish cloak;");
	}

	/**
	 * Tests for initialSteps.
	 */
	@Test
	public void testInitialSteps() {
		// start with the quest slot clean
		player.removeQuest(questSlot);
		// -----------------------------------------------
		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "help");
		assertEquals("If you want to go to the island Athor on the ferry, just go south once you've departed from Ados, and look for the pier.", getReply(npc));
		en.step(player, "quest");
		assertEquals("My sewing machine is broken, will you help me fix it?", getReply(npc));
		assertEquals(en.getCurrentState(), ConversationStates.QUEST_OFFERED);
		en.step(player, "yes");
		assertThat(getReply(npc) , is(oneOf("Thank you! To fix it, it needs a #'can of oil'. I'm ever so grateful for your help.",
				"Thank you! To fix it, it needs a #bobbin. I'm ever so grateful for your help.",
				"Thank you! To fix it, it needs a #'suit of leather armor'. I'm ever so grateful for your help.")));

		player.setQuest(questSlot, "machine;bobbin=1");
		assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		en.step(player, "bobbin");
		assertEquals("Only dwarf smiths make bobbins, no-one else has nimble enough fingers. Try #Alrak.", getReply(npc));
		en.step(player, "ok");
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));

		final Item bobbin = ItemTestHelper.createItem("bobbin");
		bobbin.setEquipableSlots(Arrays.asList("bag"));
		player.equipToInventoryOnly(bobbin);
		assertTrue(player.isEquipped("bobbin"));

		final Item oil = ItemTestHelper.createItem("oil");
		oil.setEquipableSlots(Arrays.asList("bag"));
		player.equipToInventoryOnly(oil);
		assertTrue(player.isEquipped("oil"));

		final Item armor = ItemTestHelper.createItem("leather armor");
		armor.setEquipableSlots(Arrays.asList("bag"));
		player.equipToInventoryOnly(armor);
		assertTrue(player.isEquipped("leather armor"));

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "bobbin");
		assertEquals("Only dwarf smiths make bobbins, no-one else has nimble enough fingers. Try #Alrak.", getReply(npc));
		en.step(player, "alrak");
		assertEquals("I thought you kids all knew Alrak, the only dwarf that kobolds have ever liked. Or maybe he's the only dwarf to ever like kobolds, I've never been sure which ...", getReply(npc));
		en.step(player, "quest");
		assertEquals("My sewing machine is still broken, did you bring anything to fix it?", getReply(npc));
		assertEquals(en.getCurrentState(), ConversationStates.QUEST_ITEM_QUESTION);
		en.step(player, "yes");
		// say it's true that player has done mithril shield quest here.
		assertEquals("done", player.getQuest(shieldQuestSlot));
		assertEquals("Thank you so much! Listen, I must repay the favour, and I have a wonderful idea. Do you want to hear more?", getReply(npc));
		// [22:05] jammyjam earns 100 experience points.
		player.setQuest(questSlot, "fixed_machine");
		en.step(player, "yes");
		assertEquals("I will make you the most amazing cloak of mithril. You just need to get me the fabric and any tools I need! First please bring me a couple yards of mithril fabric. The expert on fabrics is the wizard #Kampusch.", getReply(npc));
		player.setQuest(questSlot, "need_fabric");
		en.step(player, "kampusch");
		assertEquals("He is obsessed with antiques so look for him in an antiques shop or a museum.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));
	}

	/**
	 * Tests for makingFabric.
	 */
	@Test
	public void testMakingFabric() {
		player.setQuest(questSlot, "need_fabric");
		npc = SingletonRepository.getNPCList().get("Kampusch");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Greetings. What an interesting place this is.", getReply(npc));
		en.step(player, "help");
		assertEquals("Sorry, I am not the curator of this museum, I am only looking around here like you.", getReply(npc));
		en.step(player, "offer");
		assertEquals("I will teach you about #thread, and #fabric, and how wizards can fuse #mithril onto textiles.", getReply(npc));
		en.step(player, "fuse");
		assertEquals("I can only create mithril thread when you have got some silk #thread. And remember, I will know if you really need the magic performed or not.", getReply(npc));
		en.step(player, "mithril");
		assertEquals("Should you need it, I can #fuse mithril nuggets and silk thread together. But I don't perform this magic for just anyone... Once you have the mithril thread, it can be woven into fabric by #Whiggins.", getReply(npc));
		en.step(player, "threads");
		assertEquals("The best thread of all is light and strong, it is called #silk and it comes from the silk glands of spiders. Making the thread from the glands is a job which is messy. Wizards will not stoop so low. #Scientists are most likely to make thread if you need it.", getReply(npc));
		en.step(player, "whiggins");
		assertEquals("Find the wizard Whiggins inside his house in the magic city.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Vincento Price");
		en = npc.getEngine();

		Item item = ItemTestHelper.createItem("silk gland", 40);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("mithril nugget", 7);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("silk thread", 40);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Ha ha he he woo hoo ... ha ... Sorry, I get carried away sometimes. What do you want?", getReply(npc));
		en.step(player, "help");
		assertEquals("Ha ha ha ha!", getReply(npc));
		en.step(player, "make");
		assertEquals("Do you really want so few? I'm not wasting my time with that! Any decent sized pieces of fabric needs at least 40 spools of thread! You should at least #make #40.", getReply(npc));
		en.step(player, "make 40");
		assertEquals("I need you to fetch me 40 #'silk glands' for this job, which will take about 6 and a half hours. Do you have what I need?", getReply(npc));
		assertEquals(en.getCurrentState(), ConversationStates.PRODUCTION_OFFERED);
		en.step(player, "yes");
		assertEquals("It's unorthodox, but I will make 40 silk thread for you. Please be discreet and come back in about 6 and a half hours.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Ta ta!", getReply(npc));

		en.step(player, "hi");
		assertTrue(getReply(npc).startsWith("Shhhh, I'm still working on your request to make silk thread for you. I'll be done in about"));
		en.step(player, "bye");
		assertEquals("Ta ta!", getReply(npc));

		player.setQuest(questSlot, "makingthread;40;silk thread;0");
		assertEquals("makingthread;40;silk thread;0", player.getQuest(questSlot));
		en.step(player, "hi");
		assertEquals("Oh, I gave your 40 spools of silk thread to my research student Boris Karlova. Go collect them from him.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Ta ta!", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Boris Karlova");
		en = npc.getEngine();
		player.setQuest(questSlot, "makingthread;40;silk thread;0");
		en.step(player, "hi");
		assertEquals("The boss gave me these 40 spools of silk thread. Price gets his students to do his dirty work for him.", getReply(npc));
		player.setQuest(questSlot, "got_thread");


		npc = SingletonRepository.getNPCList().get("Kampusch");
		en = npc.getEngine();

		en.step(player, "hello");
		assertEquals("Greetings, can I #offer you anything?", getReply(npc));
		// say fuse without all the items
		en.step(player, "fuse");
		assertEquals("For 40 spools of mithril thread to make your cloak, I need 40 spools of #silk #thread, 7 #mithril #nuggets and a #balloon.", getReply(npc));
		// add the item
		item = ItemTestHelper.createItem("balloon", 1);
		player.getSlot("bag").add(item);
		en.step(player, "fuse");
		assertEquals("I will fuse 40 mithril thread for you. Please come back in 4 hours.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Welcome. I'm still working on your request to fuse mithril thread for you. Come back in 4 hours.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell.", getReply(npc));

		player.setQuest(questSlot, "fusingthread;1");

		en.step(player, "hi");
		assertEquals("Hello again. The magic is completed. Here you have your 40 spools of mithril thread. Now, you must go to #Whiggins to get the #fabric made.", getReply(npc));
		player.setQuest(questSlot, "got_mithril_thread");
		// [22:08] jammyjam earns 100 experience points.
		en.step(player, "whiggins");
		assertEquals("Find the wizard Whiggins inside his house in the magic city.", getReply(npc));
		en.step(player, "fabric");
		assertEquals("Cloth has different standards, which I'm sure you'll notice in your own cloaks. #Mithril fabric is the very finest and strongest of all. But then, I would say that, being from Mithrilbourgh... So, you need to find plenty of silk glands, then take them to a #scientist to make the thread. Once you have silk thread bring it to me to #fuse mithril into it. Finally, you will need to take the mithril thread to #Whiggins to get the fabric woven.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Farewell.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Whiggins");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Welcome, warmly", getReply(npc));
		en.step(player, "fabric");
		assertEquals("I would love to weave you some fabric but I'm afraid my mind is full of other things. I have offended a fellow wizard. I was up all night writing him an apology letter, but I have no-one to deliver it to him. Unless ... that is ... would YOU deliver this letter for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Wonderful! I'm so relieved! Please take this note to Pedinghaus, you will find him in Ados goldsmiths. Tell him you have a #letter for him.", getReply(npc));
		en.step(player, "letter");
		assertEquals("Please don't forget to take that letter to Pedinghaus. It means a lot to me.", getReply(npc));
		player.setQuest(questSlot, "taking_letter");
		en.step(player, "bye");
		assertEquals("Till next time.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Pedinghaus");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Greetings. I sense you may be interested in mithril. If you desire me to #cast you a #'mithril bar', just say the word.", getReply(npc));
		en.step(player, "letter");
		assertEquals("*reads* ... *reads* ... Well, I must say, that is a weight off my mind. Thank you ever so much. Please convey my warmest regards to Whiggins. All is forgiven.", getReply(npc));
		player.setQuest(questSlot, "took_letter");
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));


		npc = SingletonRepository.getNPCList().get("Whiggins");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Welcome, warmly", getReply(npc));
		en.step(player, "task");
		assertEquals("Thank you so much for taking that letter! Now, do you have the 40 spools of mithril thread so that I may weave you a couple yards of fabric?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Lovely. In 2 hours your fabric will be ready.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Till next time.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Welcome, warmly", getReply(npc));
		en.step(player, "fabric");
		assertEquals("I'm sorry, you're too early. Come back in 2 hours.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Till next time.", getReply(npc));

		// -----------------------------------------------

		player.setQuest(questSlot, "weavingfabric;1");
		en.step(player, "hi");
		assertEquals("Welcome, warmly", getReply(npc));
		en.step(player, "fabric");
		assertEquals("Here your fabric is ready! Isn't it gorgeous?", getReply(npc));
		player.setQuest(questSlot, "got_fabric");
		// [22:14] jammyjam earns 100 experience points.
		en.step(player, "bye");
		assertEquals("Till next time.", getReply(npc));


		// -----------------------------------------------



		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "fabric");
		assertEquals("Wow you got the mithril fabric , that took longer than I expected! Now, to cut it I need magical #scissors, if you would go get them from #Hogart. I will be waiting for you to return.", getReply(npc));
		en.step(player, "hogart");
		assertEquals("He's that grumpy old dwarf in the Or'ril mines. I already sent him a message saying I wanted some new scissors but he didn't respond. Well, what he lacks in people skills he makes up for in his metal work.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));
	}
	/**
	 * Tests for gettingTools.
	 */
	@Test
	public void testGettingTools() {
		npc = SingletonRepository.getNPCList().get("Hogart");
		en = npc.getEngine();
		player.setQuest(questSlot, "need_scissors");

		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "scissors");
		// Hogart asks for random number of eggshells (2-4? 1-3?) so only test starts with
		assertTrue(getReply(npc).startsWith("Ah yes, Ida sent me a message about some magical scissors. I need one each of an iron bar and a mithril bar, and also "));
		en.step(player, "eggshells");
		assertEquals("They must be from dragon eggs. I guess you better find someone who dares to hatch dragons!", getReply(npc));
		en.step(player, "bye");
		assertEquals("So long. I bet you won't sleep so well tonight.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Terry");
		en = npc.getEngine();
		player.setQuest(questSlot, "need_eggshells;4");
		Item item = ItemTestHelper.createItem("mithril bar", 1);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("iron", 1);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Hi. I don't get so many visitors, down here.", getReply(npc));
		en.step(player, "eggshells");
		assertEquals("Sure, I sell eggshells. They're not worth much to me. I'll swap you one eggshell for every 6 disease poisons you bring me. I need it to kill the rats you see. Anyway, how many eggshells was you wanting?", getReply(npc));
		en.step(player, "4");
		assertEquals("Ok, ask me again when you have 24 disease poisons with you.", getReply(npc));
		item = ItemTestHelper.createItem("disease poison", 24);
		player.getSlot("bag").add(item);
		en.step(player, "eggshells");
		assertEquals("Sure, I sell eggshells. They're not worth much to me. I'll swap you one eggshell for every 6 disease poisons you bring me. I need it to kill the rats you see. Anyway, how many eggshells was you wanting?", getReply(npc));
		en.step(player, "4");
		assertEquals("Ok, here's your 4 eggshells. Enjoy!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Watch out for the giants on your way out!", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Hogart");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "scissors");
		assertEquals("So, did you bring the items I need for the magical scissors?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Good. It will take me some time to make these, come back in 10 minutes to get your scissors.", getReply(npc));
		// [22:17] jammyjam earns 100 experience points.
		en.step(player, "bye");
		assertEquals("So long. I bet you won't sleep so well tonight.", getReply(npc));
		// [22:19] Changed the state of quest 'mithril_cloak' from 'makingscissors;1217974651141' to 'makingscissors;10000'
		player.setQuest(questSlot, "makingscissors;1");
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "scissors");
		assertEquals("Ah, thanks for reminding me. Here, Ida's scissors are ready. You better take them to her next as I don't know what she wanted them for.", getReply(npc));
		player.setQuest(questSlot, "got_scissors");
		en.step(player, "bye");
		assertEquals("So long. I bet you won't sleep so well tonight.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "scissors");
		assertEquals("You brought those magical scissors! Excellent! Now that I can cut the fabric I need a magical needle. You can buy one from a trader in the abandoned keep of Ados mountains, #Ritati Dragon something or other. Just go to him and ask for his 'specials'.", getReply(npc));
		// [22:19] jammyjam earns 100 experience points.
		en.step(player, "ritati");
		assertEquals("He's somewhere in the abandoned keep in the mountains north east from here.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Ritati Dragontracker");
		en = npc.getEngine();
		// commented out following section as the chat was done before the behaviour changed.

//		en.step(player, "hi");
	//	assertEquals("What do you want?", getReply(npc));
	//	en.step(player, "specials");
	//	assertEquals("I have some magical needles but they cost a pretty penny, 1500 pieces of money to be precise. Do you want to buy one?", getReply(npc));
	//	en.step(player, "yes");
	//	assertEquals("What the ... you don't have enough money! Get outta here!", getReply(npc));
	//	en.step(player, "specials");
	//	assertEquals("I have some magical needles but they cost a pretty penny, 1500 pieces of money to be precise. Do you want to buy one?", getReply(npc));
	//	en.step(player, "yes");
	//	assertEquals("Ok, here you are. Be careful with them, they break easy.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();
		player.setQuest(questSlot, "told_joke;1");
		item = ItemTestHelper.createItem("magical needle", 1);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "needle");
		assertEquals("Looks like you found Ritatty then, good. I'll start on the cloak now! A seamstress needs to take her time, so return in 24 hours.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));

		player.setQuest(questSlot, "sewing;1;2;1");
		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "needle");
		en.step(player, "task");
		assertEquals("These magical needles are so fragile, I'm sorry but you're going to have to get me another, the last one broke. Hopefully Ritati still has plenty.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Ritati Dragontracker");
		en = npc.getEngine();
		// commented out following section as the chat was done before the behaviour changed.
//		en.step(player, "hi");
	//	assertEquals("What do you want?", getReply(npc));
	//	en.step(player, "specials");
	//	assertEquals("I have some magical needles but they cost a pretty penny, 1500 pieces of money to be precise. Do you want to buy one?", getReply(npc));
	//	en.step(player, "yes");
	//	assertEquals("Ok, here you are. Be careful with them, they break easy.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();

		item = ItemTestHelper.createItem("magical needle", 1);
		player.getSlot("bag").add(item);
		player.setQuest(questSlot, "told_joke;1;1");

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm really sorry about the previous needle breaking. I'll start work again on your cloak, please return in another 24 hours.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertThat(getReply(npc), is(oneOf("I'm still sewing your cloak, come back in 24 hours - and don't rush me, or I'm more likely to break the needle.", "I'm still sewing your cloak, come back in 1 day - and don't rush me, or I'm more likely to break the needle.")));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));

		player.setQuest(questSlot, "sewing;1;1;1");
		// [22:21] Changed the state of quest 'mithril_cloak' from 'sewing;1217974895737;1' to 'sewing;10008;1'
		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertEquals("Ouch! I pricked my finger on that needle! I feel woozy ...", getReply(npc));
		en.step(player, "help");
		assertEquals("You can ask to #restart the quest if you are stuck getting the items for the mithril cloak.", getReply(npc));
		en.step(player, "quest");
		assertEquals("What's happening to me? I'm feverish .. I see twilight .. you can't understand unless you visit me here ... you must ask #Pdiddi how to get to the #twilight.", getReply(npc));
		en.step(player, "pdiddi");
		assertEquals("Oh, I'm too confused... I can't tell you anything about him...", getReply(npc));
		en.step(player, "twilight");
		assertEquals("What's happening to me? I'm feverish .. I see twilight .. you can't understand unless you visit me here ... you must ask #Pdiddi how to get to the #twilight.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));
	}
	/**
	 * Tests for twilightZone.
	 */
	@Test
	public void testTwilightZone() {
		npc = SingletonRepository.getNPCList().get("Pdiddi");
		en = npc.getEngine();
		player.setQuest(questSlot, "twilight_zone");

		Item item = ItemTestHelper.createItem("money", 3000);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("SHHH! Don't want all n' sundry knowin' wot I #deal in.", getReply(npc));
		en.step(player, "twilight");
		assertEquals("Keep it quiet will you! Yeah, I got moss, it's 3000 money each. How many do you want?", getReply(npc));
		en.step(player, "1");
		assertEquals("Ok, here's your 1 pieces of twilight moss. Don't take too much at once.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("twilight_ida"); // this is the version of Ida that is in the twilight zone
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("I'm sick .. so sick .. only some powerful medicine will fix me.", getReply(npc));
		// [22:22] twilight slime has been killed by jammyjam
		// [22:22] jammyjam earns 14580 experience points.
		// [22:22] You see a bottle of medicinal elixir. It is a special quest reward for jammyjam, and cannot be used by others.

		item = ItemTestHelper.createItem("twilight elixir", 1);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Is that elixir for me? If #yes I will take it immediately. You must return to see me again in my normal state.", getReply(npc));
		en.step(player, "no");
		assertEquals("I'm getting sicker ...", getReply(npc));
		en.step(player, "hi");
		assertEquals("Is that elixir for me? If #yes I will take it immediately. You must return to see me again in my normal state.", getReply(npc));
		en.step(player, "yes");

		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertEquals("When I was sick I got behind on my other jobs. I promised #Josephine I'd make her a stripey cloak but I have no time. So please, I'm relying on you to buy one and take it to her. They sell blue striped cloaks in Ados abandoned keep. Thank you!", getReply(npc));
		en.step(player, "josephine");
		assertEquals("Surely you know Josephine? That flirty flighty girl from Fado, bless her heart.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));
		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertEquals("When I was sick I got behind on my other jobs. I promised #Josephine I'd make her a stripey cloak but I have no time. So please, I'm relying on you to buy one and take it to her. They sell blue striped cloaks in Ados abandoned keep. Thank you!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));
	}
	/**
	 * Tests for cloakForJosephine.
	 */
	@Test
	public void testCloakForJosephine() {
		player.setQuest(questSlot, "taking_striped_cloak");
		npc = SingletonRepository.getNPCList().get("Josephine");
		en = npc.getEngine();

		Item item = ItemTestHelper.createItem("blue striped cloak", 1);
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Welcome back! Have you brought any #cloaks with you?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Woo! What #cloaks did you bring?", getReply(npc));
		en.step(player, "blue striped cloak");
		assertEquals("Oh, wait, that's from Ida isn't it?! Oh yay! Thank you! Please tell her thanks from me!!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye bye now!", getReply(npc));


		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();


		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertEquals("Aw, Josephine is so sweet. I'm glad she liked her blue striped cloak. Now, YOUR cloak is nearly ready, it just needs a clasp to fasten it! My friend #Pedinghaus will make it for you, if you go and ask him.", getReply(npc));
		en.step(player, "pedinghaus");
		assertEquals("I mean the wizard who works with Joshua in the Ados smithy.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));

	}
	/**
	 * Tests for makingClasp.
	 */
	@Test
	public void testMakingClasp() {
		player.setQuest(questSlot, "need_clasp");
		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertEquals("You haven't got the clasp from #Pedinghaus yet. As soon as I have that your cloak will be finished!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Pedinghaus");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Greetings. I sense you may be interested in mithril. If you desire me to #cast you a #'mithril bar', just say the word.", getReply(npc));
		en.step(player, "clasp");
		assertEquals("A clasp? Whatever you say! I am still so happy from that letter you brought me, it would be my pleasure to make something for you. I only need one mithril bar. Do you have it?", getReply(npc));
		en.step(player, "no");
		assertEquals("Well, if you should like me to cast any mithril bars just say.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Greetings. I sense you may be interested in mithril. If you desire me to #cast you a #'mithril bar', just say the word.", getReply(npc));
		en.step(player, "clasp");
		assertEquals("A clasp? Whatever you say! I am still so happy from that letter you brought me, it would be my pleasure to make something for you. I only need one mithril bar. Do you have it?", getReply(npc));
		en.step(player, "yes");
		assertEquals("You can't fool an old wizard, and I'd know mithril when I see it. Come back when you have at least one bar.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		Item item = ItemTestHelper.createItem("mithril bar", 1);
		player.getSlot("bag").add(item);

		assertEquals("Greetings. I sense you may be interested in mithril. If you desire me to #cast you a #'mithril bar', just say the word.", getReply(npc));
		en.step(player, "clasp");
		assertEquals("A clasp? Whatever you say! I am still so happy from that letter you brought me, it would be my pleasure to make something for you. I only need one mithril bar. Do you have it?", getReply(npc));
		en.step(player, "yes");
		assertEquals("What a lovely piece of mithril that is, even if I do say so myself ... Good, please come back in 60 minutes and hopefully your clasp will be ready!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		player.setQuest(questSlot, "forgingclasp;0");
		en.step(player, "hi");
		assertEquals("Greetings. I sense you may be interested in mithril. If you desire me to #cast you a #'mithril bar', just say the word.", getReply(npc));
		en.step(player, "clasp");
		assertEquals("Here, your clasp is ready!", getReply(npc));
		// [22:27] jammyjam earns 100 experience points.
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Ida");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "clasp");
		assertEquals("Wow, Pedinghaus really outdid himself this time. It looks wonderful on your new cloak! Wear it with pride.", getReply(npc));
		// [22:27] jammyjam earns 1000 experience points.
		en.step(player, "help");
		assertEquals("If you want to go to the island Athor on the ferry, just go south once you've departed from Ados, and look for the pier.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye, thanks for stepping in.", getReply(npc));
	}
}
