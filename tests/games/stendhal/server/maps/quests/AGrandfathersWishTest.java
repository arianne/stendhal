/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReplies;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.church.PriestNPC;
import games.stendhal.server.maps.deniran.cityinterior.brelandhouse.GrandfatherNPC;
import games.stendhal.server.maps.deniran.cityinterior.brelandhouse.GrandsonNPC;
import games.stendhal.server.maps.deniran.cityoutside.LittleGirlNPC;
import games.stendhal.server.maps.quests.a_grandfathers_wish.MylingSpawner;
import games.stendhal.server.maps.quests.a_grandfathers_wish.MylingWellPortal;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;


public class AGrandfathersWishTest extends QuestHelper {

	private static final String QUEST_SLOT = AGrandfathersWish.QUEST_SLOT;

	private Player player;
	private SpeakerNPC elias;
	private SpeakerNPC niall;
	private SpeakerNPC marianne;
	private SpeakerNPC priest;

	private StendhalRPZone mainZone;
	private StendhalRPZone wellZone;
	private MylingWellPortal wellEntrance;
	private Portal wellExit;


	@Before
	public void setup() {
		final StendhalRPWorld world = SingletonRepository.getRPWorld();

		// well zone must exist in world when quest is loaded
		wellZone = new StendhalRPZone("-1_myling_well");
		world.addRPZone("dummy", wellZone);
		mainZone = new StendhalRPZone("test_zone");
		world.addRPZone("dummy", mainZone);

		assertEquals(wellZone, world.getZone("-1_myling_well"));
		assertEquals(mainZone, world.getZone("test_zone"));

		player = PlayerTestHelper.createPlayer("player");
		mainZone.add(player);

		new GrandfatherNPC().configureZone(mainZone, null);
		new GrandsonNPC().configureZone(mainZone, null);
		new LittleGirlNPC().configureZone(mainZone, null);
		new PriestNPC().configureZone(mainZone, null);
		elias = SingletonRepository.getNPCList().get("Elias Breland");
		niall = SingletonRepository.getNPCList().get("Niall Breland");
		marianne = SingletonRepository.getNPCList().get("Marianne");
		priest = SingletonRepository.getNPCList().get("Father Calenus");

		// setup well portals
		wellExit = new Portal();
		wellExit.setPosition(9, 10);
		wellExit.setIdentifier("well_exit");
		wellExit.setDestination(mainZone.getName(), "well_entrance");
		wellZone.add(wellExit);

		assertEquals(wellZone, wellExit.getZone());
		assertEquals(mainZone.getName(), wellExit.getDestinationZone());
		assertEquals("well_entrance", wellExit.getDestinationReference());

		wellEntrance = new MylingWellPortal();
		wellEntrance.setPosition(5, 5);
		wellEntrance.setIdentifier("well_entrance");
		wellEntrance.setDestination(wellZone.getName(), "well_exit");
		mainZone.add(wellEntrance);

		assertEquals(mainZone, wellEntrance.getZone());
		assertEquals(wellZone.getName(), wellEntrance.getDestinationZone());
		assertEquals("well_exit", wellEntrance.getDestinationReference());
	}

	@After
	public void tearDown() throws Exception {
		removePlayer(player);

		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		world.removeZone(mainZone);
		world.removeZone(wellZone);
	}

	@Test
	public void init() {
		checkEntities();
		checkBeforeQuest();
		checkRequestStep();
		checkMarianneStep();
		checkFindWellStep();
		checkFindPriestStep();
		checkHolyWaterStep();
		checkCureMylingStep();
		checkCompleteStep();
		checkAfterQuest();
	}

	private void checkEntities() {
		assertNotNull(player);
		assertEquals(mainZone, player.getZone());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertNotNull(elias);
		assertNotNull(niall);
		assertNotNull(marianne);
		assertNotNull(priest);

		/* FIXME: how to make portals functional?
		// check functioning portals
		SingletonRepository.getRPWorld().changeZone(wellZone.getID(), player);
		assertEquals(wellZone, player.getZone());
		wellExit.onUsed(player);
		assertEquals(mainZone, player.getZone());
		*/
	}

	private void checkBeforeQuest() {
		assertFalse(quests.isLoaded(quests.getQuestFromSlot(QUEST_SLOT)));

		// quest not added to world
		Engine en = elias.getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"There is something that weighs heavy on me. But I am not ready"
				+ " for help. Perhaps you could come back later.",
			getReply(elias));
		en.step(player, "bye");

		// Marianne should not respond before quest is started
		en = marianne.getEngine();
		en.step(player, "hi");
		marianne.clearEvents(); // clear reply to "hi"
		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertNull(getReply(marianne));
		en.step(player, "bye");

		en = priest.getEngine();
		en.step(player, "hi");
		en.step(player, "help");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"If you are in need of blessings, I can offer you"
			+ " some #'holy water'.",
			getReply(priest));
		en.step(player, "holy water");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Holy water is consecrated to help those that are afflicted and"
				+ " in need of blessings.",
			getReply(priest));
		en.step(player, "bye");

		// clear replies
		elias.clearEvents();
		marianne.clearEvents();
		priest.clearEvents();

		// check portals
		wellEntrance.onUsed(player);
		assertEquals(mainZone, player.getZone());
		/* FIXME: see: games.stendhal.server.entity.mapstuff.portal.AccessCheckingPortalTest.testSetRejectedMessage
		assertEquals(
			"There is no reason to enter this well right now.",
			PlayerTestHelper.getPrivateReply(player));
		*/
		assertEquals(
			"There is no reason to enter this well right now.",
			wellEntrance.getRejectedMessage());
	}

	private void checkRequestStep() {
		// add quest to world
		quests.loadQuest(new AGrandfathersWish());
		assertTrue(quests.isLoaded(quests.getQuestFromSlot(QUEST_SLOT)));

		double karma = player.getKarma();

		player.setLevel(99);
		assertEquals(99, player.getLevel());

		final Engine en = elias.getEngine();
		en.step(player, "hello");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hello young one.", getReply(elias));

		// level too low to start quest
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"My grandson disappeared over a year ago. But I need help from a"
				+ " more experienced adventurer.",
			getReply(elias));

		player.setLevel(100);
		assertEquals(100, player.getLevel());

		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals(
			"My grandson disappeared over a year ago. I fear the worst and"
				+ " have nearly given up all hope. What I would give to just"
				+ " know what happened to him! If you learn anything will"
				+ " you bring me the news?",
			getReply(elias));

		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Alas! What has become of my grandson!?", getReply(elias));
		assertEquals("rejected", player.getQuest(QUEST_SLOT, 0));
		assertEquals(karma - 15, player.getKarma(), 0);

		karma = player.getKarma();

		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh thank you! My grandson's name is #Niall. You could talk to"
				+ " #Marianne. They used to play together.",
			getReply(elias));
		assertEquals("investigate", player.getQuest(QUEST_SLOT, 0));
		assertEquals(karma + 15, player.getKarma(), 0);

		// quest already started
		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thank you for accepting my plea for help. Please tell me if"
				+ " you hear any news about what has become of my grandson."
				+ " He used to play with a little girl named #Marianne.",
			getReply(elias));

		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Niall is my grandson. I am so distraught over his disappearance."
				+ " Ask the girl #Marianne. They often played together.",
			getReply(elias));

		en.step(player, "Marianne");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Marianne lives here in Deniran. Ask her about #Niall.",
			getReply(elias));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Goodbye.", getReply(elias));
	}

	private void checkMarianneStep() {
		final Engine en = marianne.getEngine();

		en.step(player, "hi");
		marianne.clearEvents(); // clear reply to "hi"
		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh! My friend Niall! I haven't seen him in a long time. Every"
				+ " time I go to his grandfather's house to #play, he is not"
				+ " home.",
			getReplies(marianne).get(0));

		en.step(player, "play");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Not only was he fun to play with, but he was also very helpful."
				+ " He used to help me gather chicken eggs whenever I was too"
				+ " #afraid to do it myself.",
			getReplies(marianne).get(0));

		en.step(player, "afraid");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Know what he told me once? He said he wanted to go all the way"
				+ " to Semos mountains to see the #graveyard there. Nuh uh! No"
				+ " way! That sounds more scary than chickens.",
			getReplies(marianne).get(0));
		assertEquals("find_myling:start", player.getQuest(QUEST_SLOT, 1));

		en.step(player, "graveyard");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I hope he didn't go to that scary graveyard. Who knows what kind"
				+ " of monsters are there.",
			getReply(marianne));

		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Niall said he wanted to go all the way to Semos mountains to see"
				+ " the #graveyard there. Nuh uh! No way! That sounds more"
				+ " scary than chickens.",
				getReply(marianne));

		en.step(player, "bye");
	}

	private void checkFindWellStep() {
		assertFalse(player.isEquipped("rope"));

		wellEntrance.onUsed(player);
		assertEquals(mainZone, player.getZone());
		/* FIXME: see: games.stendhal.server.entity.mapstuff.portal.AccessCheckingPortalTest.testSetRejectedMessage
		assertEquals(
			"You need a rope to descend down this well.",
			PlayerTestHelper.getPrivateReply(player));
		*/
		assertEquals(
			"You need a rope to descend down this well.",
			wellEntrance.getRejectedMessage());
		assertEquals(
			"find_myling:well_rope",
			player.getQuest(QUEST_SLOT, 1));

		PlayerTestHelper.equipWithItem(player, "rope");
		assertEquals(1, player.getNumberOfEquipped("rope"));

		wellEntrance.onUsed(player);
		// FIXME: how to make portals functional?
		//assertEquals(wellZone, player.getZone());
		assertEquals(
			"Is that thing Niall!? Poor boy. I need to tell Elias right"
				+ " away.",
			PlayerTestHelper.getPrivateReply(player));
		assertEquals("find_myling:done", player.getQuest(QUEST_SLOT, 1));

		// FIXME: how to make portals functional?
		//wellExit.onUsed(player);
		assertEquals(mainZone, player.getZone());
	}

	private void checkFindPriestStep() {
		final Engine en = elias.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh no! My dear grandson! If only there were a way to #change"
					+ " him back.",
			getReply(elias));

		en.step(player, "change");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Wait! I have heard that #'holy water' has special properties"
					+ " when used on the undead. Perhaps a #priest would have"
					+ " some. Please, go and find a priest.",
			getReply(elias));
		assertEquals("holy_water:find_priest", player.getQuest(QUEST_SLOT, 2));

		en.step(player, "priest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Please! Find a priest. I have heard there is one in Ados that"
				+ " specializes in holy water. Maybe he can provide some to"
				+ " help my grandson.",
			getReply(elias));
		en.step(player, "bye");

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Please! Find a priest. I have heard there is one in Ados that"
				+ " specializes in holy water. Maybe he can provide some to"
				+ " help my grandson.",
			getReply(elias));
		en.step(player, "bye");
	}

	private void checkHolyWaterStep() {
		final Engine en = priest.getEngine();

		assertFalse(player.isEquipped("water"));
		assertFalse(player.isEquipped("ashen holy water"));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Hello my child. What can I #help you with?",
			getReply(priest));
		en.step(player, "holy water");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh my! A young boy has transformed into a myling? I can help,"
				+ " but this will require a special holy water. Bring me a"
				+ " flask of water and some charcoal.",
			getReply(priest));
		assertEquals("holy_water:bring_items", player.getQuest(QUEST_SLOT, 2));

		en.step(player, "holy water");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I am still waiting for you to bring me a flask of water and some"
				+ " charcoal before I can bless the holy water.",
			getReply(priest));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Go in peace.", getReply(priest));

		en.step(player, "hi");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
			"Have you brought the items I requested?",
			getReply(priest));
		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Okay, I still need a flask of water and some charcoal.",
			getReply(priest));
		en.step(player, "bye");
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Hmmm... It doesn't look like you have what I need. I requested"
				+ " a flask of water and some charcoal.",
			getReply(priest));
		en.step(player, "bye");

		PlayerTestHelper.equipWithItem(player, "water");
		assertEquals(1, player.getNumberOfEquipped("water"));

		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Hmmm... It doesn't look like you have what I need. I requested"
				+ " a flask of water and some charcoal.",
			getReply(priest));
		en.step(player, "bye");

		player.drop("water");
		PlayerTestHelper.equipWithItem(player, "charcoal");
		assertEquals(1, player.getNumberOfEquipped("charcoal"));

		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Hmmm... It doesn't look like you have what I need. I requested"
				+ " a flask of water and some charcoal.",
			getReply(priest));
		en.step(player, "bye");

		PlayerTestHelper.equipWithItem(player, "water");
		assertEquals(1, player.getNumberOfEquipped("water"));

		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"Okay. It will take about 1 hour to bless this water and"
				+ " make it holy.",
			getReply(priest));
		assertEquals("holy_water:blessing", player.getQuest(QUEST_SLOT, 2));
		assertFalse(player.isEquipped("water"));
		assertFalse(player.isEquipped("charcoal"));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		final String reply = getReply(priest);
		assertTrue(
			reply.equals("The holy water will be ready in 60 minutes.") ||
			reply.equals("The holy water will be ready in 1 hour."));
		en.step(player, "bye");

		final Long timestamp = Long.parseLong(player.getQuest(QUEST_SLOT, 4));
		assertNotNull(timestamp);

		player.setQuest(QUEST_SLOT, 4, Long.toString(
			timestamp - (55 * 60000)));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"The holy water will be ready in 5 minutes.",
			getReply(priest));
		en.step(player, "bye");

		player.setQuest(QUEST_SLOT, 4, Long.toString(
			timestamp - (60 * 60000)));

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Here is the holy water. Use it to cure the boy.",
			getReply(priest));

		assertEquals("holy_water:done", player.getQuest(QUEST_SLOT, 2));
		assertEquals("cure_myling:start", player.getQuest(QUEST_SLOT, 3));
		assertFalse(player.isEquipped("water"));
		assertFalse(player.isEquipped("charcoal"));
		assertEquals(1, player.getNumberOfEquipped("ashen holy water"));
		en.step(player, "bye");

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Hello my child. What can I #help you with?",
			getReply(priest));
		en.step(player, "bye");

		// check holy water attributes
		final Item holy_water = player.getFirstEquipped("ashen holy water");
		assertNotNull(holy_water);
		assertEquals(1, holy_water.getQuantity());
		assertEquals(player.getName(), holy_water.getBoundTo());
		assertEquals("Niall Breland", holy_water.getInfoString());
		assertEquals("A bottle of ashen holy water to cure Niall.", holy_water.getDescription());
	}

	private void checkCureMylingStep() {
		final MylingSpawner spawner = AGrandfathersWish.getMylingSpawner();
		assertNotNull(spawner);
		assertFalse(spawner.mylingIsActive());

		final Item holy_water = player.getFirstEquipped("ashen holy water");
		assertNotNull(holy_water);

		assertFalse(wellZone.equals(player.getZone()));

		holy_water.onUsed(player);
		assertEquals(
			"There is nothing here that this can be used on.",
			PlayerTestHelper.getPrivateReply(player));
		assertEquals(1, player.getNumberOfEquipped("ashen holy water"));
		assertEquals("cure_myling:start", player.getQuest(QUEST_SLOT, 3));

		wellEntrance.onUsed(player);
		assertEquals(
			"I should be able to use the holy water here.",
			PlayerTestHelper.getPrivateReply(player));
		// FIXME: how to make portals functional?
		SingletonRepository.getRPWorld()
			.changeZone(wellZone.getID(), player);
		assertEquals(wellZone, player.getZone());

		holy_water.onUsed(player);
		assertEquals(
			"There is no myling here. Maybe if I wait one will show up.",
			PlayerTestHelper.getPrivateReply(player));
		assertEquals(1, player.getNumberOfEquipped("ashen holy water"));
		assertEquals("cure_myling:start", player.getQuest(QUEST_SLOT, 3));

		// add myling to zone
		spawner.onTurnReached(0);
		assertTrue(spawner.mylingIsActive());

		holy_water.onUsed(player);
		assertEquals(
			"You sprinkle the holy water over the myling's head.",
			PlayerTestHelper.getPrivateReply(player));
		assertEquals(0, player.getNumberOfEquipped("ashen holy water"));
		assertEquals("cure_myling:done", player.getQuest(QUEST_SLOT, 3));

		final SpeakerNPC activeNiall = spawner.getActiveNiall();
		assertNotNull(activeNiall);

		/* FIXME: need to get correct TurnListener instance
		spawner.onTurnReached(10);
		assertEquals(
			player.getName() + " you cured me!",
			getReply(activeNiall));
		spawner.onTurnReached(20);
		assertEquals(
			"I have been stuck in that myling form for so long now. My"
				+ " grandpa must be worried sick about me.",
			getReply(activeNiall));
		spawner.onTurnReached(30);
		assertEquals(
			"I need to get home as soon as possible to let him know I am"
				+ " alright. Stop by my house sometime. There is something I"
				+ " want to give you.",
			getReply(activeNiall));
		*/

		// leave well
		// FIXME: how to make portals functional?
		//wellExit.onUsed(player);
		SingletonRepository.getRPWorld()
			.changeZone(mainZone.getID(), player);
		assertEquals(mainZone, player.getZone());
	}

	private void checkCompleteStep() {
		assertNull(player.getFeature("bag"));

		Engine en = elias.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"You have returned my grandson to me. I cannot thank you enough."
				+ " I don't have much to offer for your kind service, but"
				+ " please speak to Niall. He is in the basement.",
			getReply(elias));
		en.step(player, "bye");

		en = niall.getEngine();

		final int xpBefore = player.getXP();
		final double karmaBefore = player.getKarma();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thank you. Without your help, I would have never made it back"
				+ " home. This is my backpack. I want you to have it. It will"
				+ " enable you to carry more stuff.",
			getReply(niall));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("done", player.getQuest(QUEST_SLOT, 0));
		assertEquals("3 5", player.getFeature("bag"));
		assertEquals(xpBefore + 5000, player.getXP());
		assertEquals(karmaBefore + 500, player.getKarma(), 0);
	}

	private void checkAfterQuest() {
		Engine en = niall.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Hi again. I'm getting ready to go on another adventure with"
				+ " Marianne. But don't worry, we are staying away from"
				+ " graveyards.",
			getReply(niall));
		en.step(player, "bye");

		en = elias.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thank you for returning my grandson to me. He is in the basement"
				+ " if you want to speak to him.",
			getReply(elias));

		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Thank you for returning my grandson to me. I am overfilled"
				+ " with joy!",
			getReply(elias));
		en.step(player, "bye");

		en = marianne.getEngine();

		en.step(player, "hi");
		marianne.clearEvents(); // clear reply to "hi"
		en.step(player, "Niall");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I heard that Niall came home! He sure was gone for a long time."
				+ " I am glad he is home safe.",
			getReplies(marianne).get(0));
		en.step(player, "bye");

		en = priest.getEngine();

		en.step(player, "hi");
		en.step(player, "holy water");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Holy water is consecrated to help those that are afflicted and"
				+ " in need of blessings.",
			getReply(priest));
		en.step(player, "bye");
	}
}
