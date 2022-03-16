/***************************************************************************
 *                   (C) Copyright 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package scripts.quest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
//import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.MetalDetector;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.LuaTestHelper;


public class LostEngagementRingTest extends LuaTestHelper {

	private static final EntityManager entityMan = SingletonRepository.getEntityManager();

	private SpeakerNPC ari;
	private SpeakerNPC emma;
	private SpeakerNPC sawyer;
	private StendhalRPZone fado;
	private StendhalRPZone athor;
	private StendhalRPZone pawnShop;

	private final String slot = "lost_engagement_ring";


	@Before
	public void setUp() {
		setUpZone("0_fado_city");

		world.addRPZone("dummy", new StendhalRPZone("0_athor_island"));
		world.addRPZone("dummy", new StendhalRPZone("int_deniran_pawn_shop"));

		load("data/script/region/fado/city/EngagedCouple.lua");
		load("data/script/quest/LostEngagementRing.lua");
		load("data/script/region/deniran/city/interior/pawn_shop/BrokerNPC.lua");
		load("data/script/region/deniran/city/interior/pawn_shop/ItemsOnShelf.lua");
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		pawnShop.remove(sawyer);

		// fado is handled in super method
		world.removeZone(athor);
		world.removeZone(pawnShop);
	}

	@Test
	public void init() {
		initZones();
		initEntities();

		testWithoutQuestLoaded();
		testWithQuestLoaded();
	}

	private void initZones() {
		fado = world.getZone("0_fado_city");
		athor = world.getZone("0_athor_island");
		pawnShop = world.getZone("int_deniran_pawn_shop");

		assertNotNull(fado);
		assertNotNull(athor);
		assertNotNull(pawnShop);
	}

	private void initEntities() {
		final NPCList npcs = SingletonRepository.getNPCList();
		ari = npcs.get("Ari");
		emma = npcs.get("Emma");
		sawyer = npcs.get("Sawyer");

		assertNotNull(ari);
		assertEquals("0_fado_city", ari.getZone().getName());
		assertEquals(74, ari.getX());
		assertEquals(56, ari.getY());

		assertNotNull(emma);
		assertEquals("0_fado_city", emma.getZone().getName());
		assertEquals(75, emma.getX());
		assertEquals(56, emma.getY());

		assertNotNull(sawyer);
		assertEquals("int_deniran_pawn_shop", sawyer.getZone().getName());
		assertEquals(18, sawyer.getX());
		assertEquals(5, sawyer.getY());

		addPlayerToWorld();
		assertNotNull(player);
		assertEquals("0_fado_city", player.getZone().getName());
		assertFalse(player.hasQuest(slot));
	}

	private void leaveCurrentZone() {
		final StendhalRPZone currentZone = player.getZone();
		if (currentZone != null) {
			currentZone.remove(player);
		}
		// reset player's position
		player.setPosition(0, 0);
	}

	private void gotoFado() {
		leaveCurrentZone();
		fado.add(player);
		assertEquals("0_fado_city", player.getZone().getName());
	}

	private void gotoPawnShop() {
		leaveCurrentZone();
		pawnShop.add(player);
		assertEquals("int_deniran_pawn_shop", player.getZone().getName());
	}

	private void gotoAthor() {
		leaveCurrentZone();
		athor.add(player);
		assertEquals("0_athor_island", player.getZone().getName());
	}

	private void testWithoutQuestLoaded() {
		Engine en;
		for (final SpeakerNPC npc: Arrays.asList(ari, emma)) {
			assertEquals("love", npc.getIdea());

			en = npc.getEngine();
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
			en.step(player, "hi");
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
		}

		en = sawyer.getEngine();
		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Welcome to the Deniran Pawn Shop.", getReply(sawyer));
		en.step(player, "offer");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Please check the blackboard for a list of items that I buy.",
			getReply(sawyer));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Bye.", getReply(sawyer));
	}

	private void testWithQuestLoaded() {
		loadCachedQuests();

		// make sure Sawyer & metal detector are working
		getMetalDetector();

		final Engine en = ari.getEngine();

		assertEquals(0, player.getLevel());
		assertFalse(player.hasFeature("keyring"));
		//assertFalse(player.hasVisitedZone("0_athor_island"));
		assertFalse(player.hasVisitedZone(athor));

		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hi there!", getReply(ari));
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I don't think you have the experience to help me. Come back when"
				+ " you are stronger.",
			getReply(ari));

		player.setLevel(50);
		assertEquals(50, player.getLevel());

		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I don't think you have the experience to help me. Maybe if you knew"
				+ " more about keyrings.",
			getReply(ari));

		player.setFeature("keyring", "2 4");
		assertTrue(player.hasFeature("keyring"));
		assertEquals("2 4", player.getFeature("keyring"));

		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I don't think you have the experience to help me. Maybe if you were"
				+ " more familiar with Athor island.",
			getReply(ari));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Bye.", getReply(ari));

		gotoAthor();
		gotoFado();
		assertTrue(player.hasVisitedZone("0_athor_island"));

		int karma = (int) player.getKarma();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Hey! You look like an experienced adventurer. Perhaps you could help me with a #task.",
			getReply(ari));
		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals(
			"I have lost my engagement ring, and I am too embarrassed to tell "
				+ emma.getName() + ". Would you help me?",
			getReply(ari));
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"I don't want to leave " + emma.getName()
				+ "'s side. I hope I can find someone to help me.",
			getReply(ari));
		assertEquals(karma - 15, (int) player.getKarma());

		karma = (int) player.getKarma();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"Thank you so much! I lost my ring while visiting Athor Island."
				+ " Please let me know when you find it. And don't say anything to "
				+ emma.getName() + ".",
			getReply(ari));
		assertTrue(player.hasQuest(slot));
		assertEquals(karma + 15, (int) player.getKarma());

		karma = (int) player.getKarma();

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
			"Did you find my ring? If there is something I can do to #help, please let me know.",
			getReply(ari));
		// player does not have ring
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Please keep looking.", getReply(ari));
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("You don't have my ring. Please, keep looking.", getReply(ari));
		en.step(player, "hi");
		en.step(player, "help");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"I may have dropped it while walking along the beach. If that is the case,"
				+ " you may need something to dig in the sand or something that can"
				+ " detect metal. Maybe a local pawn shop has a tool you could use.",
			getReply(ari));

		// try to give Ari a fake ring
		final Item fakeRing = entityMan.getItem("engagement ring");
		player.equip("bag", fakeRing);
		assertEquals(1, player.getAllEquipped("engagement ring").size());
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals("You don't have my ring. Please, keep looking.", getReply(ari));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		player.drop("engagement ring");
		assertFalse(player.isEquipped("engagement ring"));

		// ATTENDING state does not occur in game while quest is active
		en.step(player, "hi");
		en.setCurrentState(ConversationStates.ATTENDING);
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("You are already helping me find my engagement ring.", getReply(ari));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		final String[] coords = player.getQuest(slot).split(";");

		// get ring
		searchForRing();

		// have ring but tell Ari is lost
		en.step(player, "hi");
		en.step(player, "lost");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"You lost my lost engagement ring? That's kind of ironic."
				+ " I bet you're teasing me.",
			getReply(ari));
		assertEquals("found_ring", player.getQuest(slot, 0));

		// truly lost ring
		player.dropWithInfostring("engagement ring", "Ari's ring");
		assertFalse(player.isEquipped("engagement ring"));
		en.step(player, "hi");
		en.step(player, "lost");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"You lost my lost engagement ring? That's kind of ironic."
				+ " I bet you dropped it near where you found it."
				+ " Check there again.",
			getReply(ari));

		// quest slot should have been reset to same position
		assertEquals(coords[0], player.getQuest(slot, 0));
		assertEquals(coords[1], player.getQuest(slot, 1));

		// get ring again
		searchForRing();

		// finish quest
		en.step(player, "hi");
		en.step(player, "yes");
		//en.step(player, "done");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"Thank you so much! As a reward, I will give you this keyring. It is larger than the one you have.",
			getReply(ari));
		assertEquals("done", player.getQuest(slot, 0));
		assertFalse(player.isEquipped("engagement ring"));
		assertEquals(karma + 50, (int) player.getKarma());
		assertEquals("3 4", player.getFeature("keyring"));
	}

	private void searchForRing() {
		gotoAthor();

		assertNotEquals("found_ring", player.getQuest(slot, 0));

		final MetalDetector detector = (MetalDetector) player.getFirstEquipped("metal detector");
		assertNotNull(detector);

		assertEquals(0, player.getX());
		assertEquals(0, player.getY());

		// did not find ring
		detector.onUsed(player);
		assertFalse(player.isEquipped("engagement ring"));
		assertNotEquals("found_ring", player.getQuest(slot, 0));

		// go to coordinates where ring can be found
		player.setPosition(
			Integer.parseInt(player.getQuest(slot, 0)),
			Integer.parseInt(player.getQuest(slot, 1)));

		// found ring
		detector.onUsed(player);
		assertTrue(player.isEquipped("engagement ring"));
		assertEquals("found_ring", player.getQuest(slot, 0));

		final Item ring = player.getFirstEquipped("engagement ring");
		assertEquals(player.getName(), ring.getBoundTo());
		assertEquals("Ari's ring", ring.getInfoString());

		// put player back in Fado City
		gotoFado();
	}

	private void checkMetalDetectorNotOnShelf() {
		assertNull(pawnShop.getEntityAt(19, 5));
	}

	private void checkMetalDetectorOnShelf() {
		final Entity itemOnShelf = pawnShop.getEntityAt(19, 5);
		assertNotNull(itemOnShelf);
		assertTrue(itemOnShelf instanceof MetalDetector);
		assertEquals(sawyer.getName(), ((MetalDetector) itemOnShelf).getBoundTo());
	}

	private void removeMetalDetectorFromShelf() {
		final Entity itemOnShelf = pawnShop.getEntityAt(19, 5);
		if (itemOnShelf != null) {
			pawnShop.remove(itemOnShelf);
		}

		checkMetalDetectorNotOnShelf();
	}

	private void getMetalDetector() {
		gotoPawnShop();

		final Engine en = sawyer.getEngine();
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		final String pawnSlot = "sawyer_metal_detector";

		assertFalse(player.hasQuest(pawnSlot));
		assertFalse(player.isEquipped("metal detector"));
		assertFalse(player.isEquipped("mithril legs"));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Welcome to the Deniran Pawn Shop.", getReply(sawyer));
		en.step(player, "help");
		assertEquals(
			"If you want to pawn something, check my blackboard for a list of what I buy.",
			getReply(sawyer));
		en.step(player, "offer");
		assertEquals(
			"Please check the blackboard for a list of items that I buy."
				+ " I also have some items that I don't mind #loaning out.",
			getReply(sawyer));
		en.step(player, "return");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Return what? I haven't loaned anything to you.", getReply(sawyer));
		en.step(player, "borrow");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
			"So, you want to borrow my metal detector? Well, I don't lend things out"
				+ " without some form of collateral. What would you like to leave"
				+ " behind?",
			getReply(sawyer));
		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Okay then. What else can I help you with?", getReply(sawyer));
		en.step(player, "borrow");
		en.step(player, "cheese");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals("Hmmm, I'm not interested in that. What else you got?", getReply(sawyer));
		en.step(player, "mithril legs");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
			"You're not even carrying one of those. Come on, what do you have?",
			getReply(sawyer));

		// test borrowing when detector is on shelf
		borrowFromShelf(en, pawnSlot);
		// test borrowing when detector not on shelf
		borrowExtra(en, pawnSlot);

		// put player back in Fado City
		gotoFado();
	}

	/**
	 * Tests borrowing metal detector from Sawyer when item
	 * is still on shelf.
	 */
	private void borrowFromShelf(final Engine en, final String pawnSlot) {
		assertFalse(player.hasQuest(pawnSlot));
		assertFalse(player.isEquipped("metal detector"));

		// check that metal detector is on shelf
		checkMetalDetectorOnShelf();

		final String legsInfo = "foo bar";
		final Item legs = entityMan.getItem("mithril legs");
		legs.setBoundTo(player.getName());
		legs.setInfoString(legsInfo);

		player.equip("bag", legs);
		assertEquals(1, player.getAllEquipped("mithril legs").size());

		en.step(player, "mithril legs");
		// player walked away
		assertNull(sawyer.getAttending());
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"Where did you go? Oh well, guess I'll just put this back on the shelf.",
			getReply(sawyer));

		en.step(player, "hi");
		sawyer.setAttending(player);
		assertEquals(player, sawyer.getAttending());
		en.step(player, "borrow");
		en.step(player, "mithril legs");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Okay, here you go. My metal detector for your mithril legs"
				+ ". Be careful with it. If it gets lost, you won't be able to"
				+ " #return it and I will keep your mithril legs. Anything"
				+ " else I can help you with?",
			getReply(sawyer));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		sawyer.setAttending(null);
		assertNull(sawyer.getAttending());

		assertFalse(player.isEquipped("mithril legs"));
		assertEquals(1, player.getAllEquipped("metal detector").size());
		// make sure bound & infostring data are stored in quest slot
		assertEquals("mithril legs", player.getQuest(pawnSlot, 0));
		assertEquals(player.getName(), player.getQuest(pawnSlot, 1));
		assertEquals(legsInfo, player.getQuest(pawnSlot, 2));

		// check that detector was removed from shelf
		checkMetalDetectorNotOnShelf();

		final MetalDetector detector = (MetalDetector) player.getFirstEquipped("metal detector");
		assertNotNull(detector);
		assertEquals(player.getName(), detector.getBoundTo());
		// make sure bound & infostring data are stored in item infostring
		final String[] dinfo = detector.getInfoString().split(";");
		assertEquals(sawyer.getName(), dinfo[0]);
		assertEquals("mithril legs", dinfo[1]);
		assertEquals(player.getName(), dinfo[2]);
		assertEquals(legsInfo, dinfo[3]);

		// returning metal detector

		en.step(player, "hi");
		en.step(player, "return");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals("You want to return my metal detector?", getReply(sawyer));
		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Okay. What else can I help you with?", getReply(sawyer));

		// try to return metal detector when not equipped
		player.drop(detector);
		assertFalse(player.isEquipped("metal detector"));
		en.step(player, "return");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("You aren't even carryng a metal detector.", getReply(sawyer));

		// try to return a metal detector that doesn't belong to Sawyer
		final Item fakeDetector = entityMan.getItem("metal detector");
		player.equip("bag", fakeDetector);
		assertEquals(1, player.getAllEquipped("metal detector").size());
		en.step(player, "return");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("This isn't mine. Take it back. I want MY metal detector.", getReply(sawyer));
		player.drop(fakeDetector);
		assertFalse(player.isEquipped("metal detector"));

		// return the real metal detector
		player.equip("bag", detector);
		en.step(player, "return");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Okay. Here is your mithril legs. Good as new. Is there"
				+ " anything else I can help you with?",
			getReply(sawyer));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertFalse(player.isEquipped("metal detector"));
		assertFalse(player.hasQuest(pawnSlot));

		// check that metal detector was returned to shelf
		checkMetalDetectorOnShelf();

		// make sure returned item retains bound & infostring information
		final Item returnedLegs = player.getFirstEquipped("mithril legs");
		assertNotNull(returnedLegs);
		assertEquals(player.getName(), returnedLegs.getBoundTo());
		assertEquals(legsInfo, returnedLegs.getInfoString());
	}

	/**
	 * Tests borrowing metal detector from Sawyer when item
	 * has been previously borrowed by another player.
	 */
	private void borrowExtra(final Engine en, final String pawnSlot) {
		assertFalse(player.hasQuest(pawnSlot));
		assertFalse(player.isEquipped("metal detector"));

		// make sure metal detector was removed from shelf
		removeMetalDetectorFromShelf();

		player.equip("head", entityMan.getItem("black helmet"));
		assertEquals(1, player.getAllEquipped("black helmet").size());

		en.step(player, "hi");
		en.step(player, "borrow");
		// clear replies cache
		getReply(sawyer);

		// FIXME: how do we simulate waiting for Sawyer to finish thinking?
		// equip with metal detector so rest of test doesn't fail
		player.equip("bag", entityMan.getItem("metal detector"));

		/*
		en.step(player, "black helmet");
		assertEquals(ConversationStates.BUSY, en.getCurrentState());

		List<String> replies = getOrderedReplies(sawyer);
		assertEquals(2, replies.size());
		assertEquals("Hmmmm. I guess I forgot that I already loaned it out...", replies.get(0));
		assertEquals("!me is thinking.", replies.get(1));

		SingletonRepository.getTurnNotifier().notifyInTurns(0, new TurnListener() {
			public void onTurnReached(final int currentTurn) {
				en.step(player, "black helmet");
				assertEquals(ConversationStates.BUSY, en.getCurrentState());

				final List<String> replies = getOrderedReplies(sawyer);
				assertEquals(2, replies.size());
				assertEquals("Hmmmm. I guess I forgot that I already loaned it out...", replies.get(0));
				assertEquals("!me is thinking.", replies.get(1));
			}
		});

		final long startTime = System.currentTimeMillis();

		int reports = 0;
		int elapsed = 0;
		System.out.println("Sawyer is thinking ...");
		while (elapsed < 16) {
			// wait for Sawyer to finish thinking

			if (elapsed >= 5 && reports == 0) {
				System.out.println("Sawyer is still thinking ...");
				reports++;
			} else if (elapsed >= 10 && reports == 1) {
				System.out.println("Sawyer is almost done thinking ...");
				reports++;
			}

			elapsed = (int) ((System.currentTimeMillis() - startTime) / 1000);
		}

		final List<String> replies = getOrderedReplies(sawyer);
		assertEquals(2, replies.size());
		assertEquals("Oh! That's right, I keep a spare just in case.", replies.get(0));
		assertEquals(
			"Okay, here you go. My metal detector for your black helmet."
				+ " Be careful with it. If it gets lost, you won't be able to"
				+ " #return it and I will keep your black helmet. Anything"
				+ " else I can help you with?",
			replies.get(1));
		*/

		// TODO: player dropped item while Sawyer was thinking
	}
}
