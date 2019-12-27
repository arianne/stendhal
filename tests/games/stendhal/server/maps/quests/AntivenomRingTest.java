/***************************************************************************
 *                   (C) Copyright 2019 - Arianne                          *
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
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.animal_sanctuary.ZoologistNPC;
import games.stendhal.server.maps.ados.church.HealerNPC;
import games.stendhal.server.maps.ados.magician_house.WizardNPC;
import games.stendhal.server.maps.ados.wall.GreeterSoldierNPC;
import games.stendhal.server.maps.athor.ship.CargoWorkerNPC;
import games.stendhal.server.maps.kirdneh.river.RetiredTeacherNPC;
import games.stendhal.server.maps.quests.antivenom_ring.AntivenomRing;
import games.stendhal.server.maps.quests.antivenom_ring.ApothecaryStage;
import games.stendhal.server.maps.semos.apothecary_lab.ApothecaryNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.ItemTestHelper;

public class AntivenomRingTest extends ZonePlayerAndNPCTestImpl {
	private static final String ZONE_NAME = "testzone";

	private SpeakerNPC apothecary;
	private SpeakerNPC zoologist;
	private Item note;
	private final String infostring = "note to apothecary";

	private final String questName = "antivenom_ring";
	private final String subquestName = questName + "_extract";
	private final String questTrapsKlaas = "traps_for_klaas";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setZoneForPlayer(ZONE_NAME);
		setNpcNames("Jameson", "Zoey", "Klaas", "Julius", "Valo", "Haizen", "Ortiv Milquetoast");

		addZoneConfigurator(new ApothecaryNPC(), ZONE_NAME);
		addZoneConfigurator(new ZoologistNPC(), ZONE_NAME);
		addZoneConfigurator(new CargoWorkerNPC(), ZONE_NAME);
		addZoneConfigurator(new GreeterSoldierNPC(), ZONE_NAME);
		addZoneConfigurator(new HealerNPC(), ZONE_NAME);
		addZoneConfigurator(new WizardNPC(), ZONE_NAME);
		addZoneConfigurator(new RetiredTeacherNPC(), ZONE_NAME);

		super.setUp();

		apothecary = getNPC("Jameson");
		zoologist = getNPC("Zoey");
		note = ItemTestHelper.createItem("note");

		// initialize quest
		new AntivenomRing().addToWorld();

		// Traps for Klaas quest needs to be completed for some dialog to work
		player.setQuest(questTrapsKlaas, 0, "done");
	}

	@Test
	public void testQuest() {
		testEntities();
		testHintNPCs();
		testQuestNotActive();
		testQuestActive();
		testQuestDone();
	}

	private void testEntities() {
		assertNotNull(player);
		assertNotNull(apothecary);
		assertNotNull(zoologist);
		assertNotNull(note);
		assertNull(note.getInfoString());

		assertEquals("done", player.getQuest(questTrapsKlaas, 0));
	}

	private void testHintNPCs() {
		SpeakerNPC npc = getNPC("Klaas");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		en.step(player, "hi");
		en.step(player, "apothecary");
		assertEquals(
			"I used to know an old apothecary, but don't know where he has settled down. Perhaps someone in Ados would know."
			+ " There are guards that patrol the city. They see a lot of things that others do not. As around about an"
			+ " #apothecary.",
			getReply(npc));

		npc = getNPC("Julius");
		assertNotNull(npc);
		en = npc.getEngine();

		en.step(player, "hi");
		en.step(player, "apothecary");
		assertEquals("I had witnessed #Valo meeting with the old apothecary on many occasions.", getReply(npc));
		en.step(player, "Valo");
		assertEquals("Valo is a healer who researched healing potions with the apothecary. He is usually in the #Church.", getReply(npc));
		en.step(player, "Church");
		assertEquals(
			"I have a #map if you have trouble finding it.... Oh, I guess my map isn't updated with that part"
			+ " of Ados City. Well, it's south of Town Hall.",
			getReply(npc));

		npc = getNPC("Valo");
		assertNotNull(npc);
		en = npc.getEngine();

		en.step(player, "hi");
		en.step(player, "apothecary");
		assertEquals(
			"Hmmm, yes, I knew a man long ago who was studying medicines and antipoisons. The last I heard he was #retreating into the mountains.",
			getReply(npc));
		en.step(player, "retreating");
		assertEquals("He's probably hiding. Keep an eye out for hidden entrances.", getReply(npc));

		npc = getNPC("Haizen");
		assertNotNull(npc);
		en = npc.getEngine();

		en.step(player, "hi");
		en.step(player, "apothecary");
		assertEquals(
			"Yes, there was once an estudious man in Kalavan. But, due to complications with leadership there he was forced to leave. I heard that he was #hiding somewhere in the Semos region.",
			getReply(npc));
		en.step(player, "hiding");
		assertEquals("If I were hiding I would surely do it in a secret room with a hidden entrance.", getReply(npc));

		npc = getNPC("Ortiv Milquetoast");
		assertNotNull(npc);
		en = npc.getEngine();

		en.step(player, "hi");
		en.step(player, "apothecary");
		assertEquals(
			"You must be speaking of my colleague, Jameson. He was forced to #hide out because of problems in Kalavan. He hasn't told me where, but he does bring the most delicious pears when he visits.",
			getReply(npc));
		en.step(player, "hide");
		assertEquals("He hinted at a secret laboratory that he had built. Something about a hidden doorway.", getReply(npc));
		en.step(player, "pears");
		assertEquals("My friends tell me that pears can be found in Semos's mountains.", getReply(npc));
	}

	private void testQuestNotActive() {
		assertNull(player.getQuest(questName));
		assertNull(player.getQuest(subquestName));

		Engine en = zoologist.getEngine();

		// Zoey ignores players when quest is not active
		en.step(player, "hi");
		assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		assertEquals("!me yawns", getReply(zoologist));

		en = apothecary.getEngine();

		en.step(player, "hi");
		assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		assertEquals("Hello, welcome to my lab.", getReply(apothecary));

		// request quest without note from Klaas
		en.step(player, "quest");
		assertEquals("I'm sorry, but I'm much too busy right now. Perhaps you could talk to #Klaas.", getReply(apothecary));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		player.equip("bag", note);

		// player is carrying note without appropriate info string
		en.step(player, "hi");
		en.step(player, "quest");
		assertEquals("I'm sorry, but I'm much too busy right now. Perhaps you could talk to #Klaas.", getReply(apothecary));

		// add appropriate info string to note
		note.setInfoString(infostring);

		// request quest with note from Klaas
		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals("Oh, a message from Klaas. Is that for me?", getReply(apothecary));

		en.step(player, "bye");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals("That is not a \"yes\" or \"no\" answer. I said, Is that note you are carrying for me?", getReply(apothecary));

		en.setCurrentState(ConversationStates.IDLE);

		// player begins conversation with note equipped
		en.step(player, "hi");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals("Oh, a message from Klaas. Is that for me?", getReply(apothecary));

		// player drops note before accepting quest
		player.drop(note);
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Okay then, I will need you too... wait, where did that note go?", getReply(apothecary));

		player.equip("bag", note);
		en.step(player, "quest");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals("Oh, a message from Klaas. Is that for me?", getReply(apothecary));

		double karma = player.getKarma();

		// player rejects quest
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Oh, well, carry on then.", getReply(apothecary));
		// player loses some karma
		assertEquals(karma - 5.0, player.getKarma(), 0);

		en.step(player, "hi");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals("Oh, a message from Klaas. Is that for me?", getReply(apothecary));

		final String items_string = "a #'cobra venom', 20 #'fairy cakes', 2 #'roots of mandragora', and a #'medicinal ring'";

		karma = player.getKarma();

		// player accepts quest
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(karma + 5.0, player.getKarma(), 0);
		assertEquals(ApothecaryStage.getMixItems(), player.getQuest(questName));
		assertEquals("Klaas has asked me to assist you. I can make a ring that will increase your resistance to poison. I need you to bring me "
				+ items_string + ".  Do you have any of those with you?",
				getReply(apothecary));
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Okay. I still need " + items_string, getReply(apothecary));

		// make sure note was dropped
		assertFalse(player.isEquippedWithInfostring("note", infostring));
	}

	private void testQuestActive() {
		Engine en = zoologist.getEngine();

		// Zoey will now respond to player
		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Oh! You startled me. I didn't see you there. I'm very busy, so if there is something you need please tell me quickly.",
				getReply(zoologist));

		en.step(player, "quest");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("There is nothing that I need right now. But maybe you could help me #milk some #snakes ones of these days.",
				getReply(zoologist));
		en.step(player, "help");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I am a zoologist and work full-time here at the animal sanctuary. I specialize in #venomous animals.",
				getReply(zoologist));
		en.step(player, "venomous");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I can use my equipment to #extract the poisons from venomous animals.", getReply(zoologist));

		// player asks about venom
		en.step(player, "extract");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals("What's that, you need some venom to create an antivemon? I can extract the venom from a "
				+ "cobra's venom gland, but I will need a vial to hold it in. Do you have those items?",
				getReply(zoologist));

		// player does not have items
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Oh? Okay then. Come back when you do", getReply(zoologist));

		// player claims to have items but does not
		en.step(player, "hi");
		en.step(player, "extract");
		en.step(player, "yes");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Oh? Then where are they?", getReply(zoologist));

		final Item vial = ItemTestHelper.createItem("vial");
		final Item gland = ItemTestHelper.createItem("venom gland");

		assertNotNull(vial);
		assertNotNull(gland);

		// player only has vial
		player.equip("bag", vial);
		en.step(player, "hi");
		en.step(player, "extract");
		en.step(player, "yes");
		assertEquals("Oh? Then where are they?", getReply(zoologist));

		// player only have venom gland
		player.drop(vial);
		player.equip("bag", gland);
		en.step(player, "hi");
		en.step(player, "extract");
		en.step(player, "yes");
		assertEquals("Oh? Then where are they?", getReply(zoologist));

		assertNull(player.getQuest(subquestName));

		// player has required items
		player.equip("bag", vial);
		en.step(player, "hi");
		en.step(player, "extract");
		en.step(player, "yes");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Okay, I will have your venom ready in about 20 minutes.", getReply(zoologist));

		assertFalse(player.isEquipped("vial"));
		assertFalse(player.isEquipped("venom gland"));
		assertNotNull(player.getQuest(subquestName));

		// player returns before venom is ready
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertTrue(getReply(zoologist).startsWith("The venom is not ready yet. Please come back in"));

		// player returns after venom is ready
		player.setQuest(subquestName, "0");
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Your cobra venom is ready.", getReply(zoologist));
		assertTrue(player.isEquipped("cobra venom"));
		assertEquals("done", player.getQuest(subquestName));

		en = apothecary.getEngine();

		// player returns to apothecary
		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hello again! Did you bring me the #items I requested?", getReply(apothecary));

		String current_reply;

		// player asks for quest while quest is active
		en.step(player, "quest");
		current_reply = getReply(apothecary);
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertTrue(current_reply.startsWith("I am still waiting for you to bring me") && current_reply.endsWith("Do you have any of those with you?"));

		// player wants to know which items are requested
		en.step(player, "items");
		current_reply = getReply(apothecary);
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertTrue(current_reply.startsWith("I need") && current_reply.endsWith("Did you bring something?"));

		// says does not have the items
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertTrue(getReply(apothecary).startsWith("Okay. I still need"));

		PlayerTestHelper.equipWithItem(player, "medicinal ring");
		PlayerTestHelper.equipWithStackableItem(player, "mandragora", 2);
		PlayerTestHelper.equipWithStackableItem(player, "fairy cake", 20);

		assertTrue(player.isEquipped("cobra venom"));
		assertTrue(player.isEquipped("medicinal ring"));
		assertTrue(player.isEquipped("mandragora", 2));
		assertTrue(player.isEquipped("fairy cake", 20));

		// says has the items
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.QUESTION_2, en.getCurrentState());
		assertEquals("What did you bring?", getReply(apothecary));

		en.step(player, "medicinal ring");
		assertEquals("Excellent! Do you have anything else with you?", getReply(apothecary));
		assertFalse(player.isEquipped("medicinal ring"));
		en.step(player, "mandragora");
		assertEquals("Excellent! Do you have anything else with you?", getReply(apothecary));
		assertFalse(player.isEquipped("mandragora"));
		en.step(player, "fairy cake");
		assertEquals("Excellent! Do you have anything else with you?", getReply(apothecary));
		assertFalse(player.isEquipped("fairy cake"));
		en.step(player, "cobra venom");
		assertFalse(player.isEquipped("cobra venom"));

		assertEquals("Thank you. I'll get to work on infusing your ring right after I enjoy a few of these fairy cakes. Please come back in 3 days.",
				getReply(apothecary));
		assertTrue(player.getQuest(questName).startsWith("enhancing"));

		// zoologist back to ignoring player
		en = zoologist.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("!me yawns", getReply(zoologist));

		en = apothecary.getEngine();

		// player returns before ring is ready
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertTrue(getReply(apothecary).startsWith("I have not finished with the ring. Please check back in"));

		player.setQuest(questName, "enhancing;0");
		int xp = player.getXP();
		double karma = player.getKarma();

		// player returns after ring is ready
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("I have finished infusing your ring. Now I'll finish the rest of my fairy cakes if you dont mind.",
				getReply(apothecary));

		assertEquals(xp + 2000, player.getXP());
		assertEquals(karma + 25.0, player.getKarma(), 0);
		assertTrue(player.isEquipped("antivenom ring"));
		assertEquals("done", player.getQuest(questName));
		assertNull(player.getQuest(subquestName));
	}

	private void testQuestDone() {
		Engine en = zoologist.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("!me yawns", getReply(zoologist));

		en = apothecary.getEngine();

		en.step(player, "hi");
		assertEquals("Hello, welcome to my lab.", getReply(apothecary));

		en.step(player, "quest");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals("Thank you so much. It had been so long since I was able to enjoy a fairy cake. Are you enjoying your ring?",
				getReply(apothecary));
		en.step(player, "yes");
		assertEquals("Wonderful!", getReply(apothecary));
		en.step(player, "quest");
		en.step(player, "no");
		assertEquals("Oh, that's too bad.", getReply(apothecary));

		en.setCurrentState(ConversationStates.IDLE);
	}
}
