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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.constants.Events;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.animal_sanctuary.ZoologistNPC;
import games.stendhal.server.maps.ados.church.HealerNPC;
import games.stendhal.server.maps.ados.magician_house.WizardNPC;
import games.stendhal.server.maps.ados.snake_pit.PurpleCrystalNPC;
import games.stendhal.server.maps.ados.wall.GreeterSoldierNPC;
import games.stendhal.server.maps.athor.ship.CargoWorkerNPC;
import games.stendhal.server.maps.fado.hut.BlueCrystalNPC;
import games.stendhal.server.maps.fado.weaponshop.RingSmithNPC;
import games.stendhal.server.maps.kirdneh.river.RetiredTeacherNPC;
import games.stendhal.server.maps.nalwor.forest.RedCrystalNPC;
import games.stendhal.server.maps.nalwor.river.PinkCrystalNPC;
import games.stendhal.server.maps.quests.antivenom_ring.AntivenomRing;
import games.stendhal.server.maps.quests.antivenom_ring.ApothecaryStage;
import games.stendhal.server.maps.quests.marriage.MakeRings;
import games.stendhal.server.maps.quests.marriage.MarriageQuestInfo;
import games.stendhal.server.maps.semos.apothecary_lab.ApothecaryNPC;
import games.stendhal.server.maps.semos.bakery.ChefNPC;
import games.stendhal.server.maps.semos.mountain.YellowCrystalNPC;
import marauroa.common.game.RPEvent;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.ItemTestHelper;

public class AntivenomRingTest extends ZonePlayerAndNPCTestImpl {
	private static final String ZONE_NAME = "testzone";

	private SpeakerNPC apothecary;
	private SpeakerNPC zoologist;
	private SpeakerNPC ringmaker;
	private Item note;
	private final String infostring = "note to apothecary";

	private final String questName = "antivenom_ring";
	private final String subquestName = questName + "_extract";
	private final String questTrapsKlaas = "traps_for_klaas";

	private final String items_string = "a #'vial of cobra venom', 20 #'fairy cakes', a #kokuda, and 2 #'roots of mandragora'";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setZoneForPlayer(ZONE_NAME);

		/* NPCs that are directly associated with Antivenom Ring quest */
		final Map<String, ZoneConfigurator> directNPCs = new HashMap<String, ZoneConfigurator>() {{
			put("Jameson",  new ApothecaryNPC());
			put("Zoey", new ZoologistNPC());
			put("Ognir", new RingSmithNPC());
			put("Klaas", new CargoWorkerNPC());
			put("Julius", new GreeterSoldierNPC());
			put("Valo", new HealerNPC());
			put("Haizen", new WizardNPC());
			put("Ortiv Milquetoast", new RetiredTeacherNPC());
		}};

		/* these NPCs are not associated with Antivenom Ring quest, but with quests that could potentially
		 * conflict.
		 */
		final Map<String, ZoneConfigurator> indirectNPCs =  new HashMap<String, ZoneConfigurator>() {{
				// Emotion Crystals quest (Julius)
				put("Red Crystal", new RedCrystalNPC());
				put("Purple Crystal", new PurpleCrystalNPC());
				put("Yellow Crystal", new YellowCrystalNPC());
				put("Pink Crystal", new PinkCrystalNPC());
				put("Blue Crystal", new BlueCrystalNPC());
				// Pizza Delivery quest (Haizen)
				put("Leander", new ChefNPC());
		}};

		List<String> allNPCs = new ArrayList<>();
		for (final String name: directNPCs.keySet()) {
			allNPCs.add(name);
		}
		for (final String name: indirectNPCs.keySet()) {
			allNPCs.add(name);
		}

		setNpcNames(allNPCs.toArray(new String[allNPCs.size()]));

		for (final ZoneConfigurator configurator: directNPCs.values()) {
			addZoneConfigurator(configurator, ZONE_NAME);
		}
		for (final ZoneConfigurator configurator: indirectNPCs.values()) {
			addZoneConfigurator(configurator, ZONE_NAME);
		}

		super.setUp();

		apothecary = getNPC("Jameson");
		zoologist = getNPC("Zoey");
		ringmaker = getNPC("Ognir");
		note = ItemTestHelper.createItem("note");

		// initialize other quests related to NPCs to help detect potential conflicts
		// Ognir
		new MakeRings(new MarriageQuestInfo()).addToWorld(); // marriage quest
		new RingMaker().addToWorld();
		// Klaas
		new TrapsForKlaas().addToWorld();
		// Julius
		new EmotionCrystals().addToWorld();
		// Haizen
		new Maze().addToWorld();
		new PizzaDelivery().addToWorld();
		// Ortiv Milquetoast
		new MixtureForOrtiv().addToWorld();

		// initialize Antivenom Ring quest
		new AntivenomRing().addToWorld();
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
		assertNotNull(ringmaker);
		assertNotNull(note);
		assertNull(note.getInfoString());
	}

	private void testHintNPCs() {
		SpeakerNPC npc = getNPC("Klaas");
		assertNotNull(npc);
		Engine en = npc.getEngine();

		// Traps for Klaas quest needs to be completed before Klaas will give hints to the apothecary
		player.setQuest(questTrapsKlaas, 0, "done");
		// don't say "hi" here because will give us another copy of the note
		npc.setAttending(player);
		en.setCurrentState(ConversationStates.ATTENDING);
		assertTrue(npc.getAttending().equals(player));
		en.step(player, "apothecary");
		assertEquals(
			"I used to know an old apothecary, but don't know where he has settled down. Perhaps someone in Ados would know."
			+ " There are guards that patrol the city. They see many happenings around the area. Ask around about an"
			+ " #apothecary.",
			getReply(npc));
		player.setQuest(questTrapsKlaas, null);

		npc = getNPC("Julius");
		assertNotNull(npc);
		en = npc.getEngine();

		en.step(player, "hi");
		en.step(player, "apothecary");
		assertEquals("I had witnessed #Valo meeting with the old apothecary on many occasions.", getReply(npc));
		en.step(player, "Valo");
		assertEquals("Valo is a healer who researched healing potions with the #apothecary. He is usually in the #Church.", getReply(npc));
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
			"Hmmm, yes, I knew a man long ago who was studying medicines and antipoisons. The last I heard he was"
			+ " #retreating into the mountains.",
			getReply(npc));
		en.step(player, "retreating");
		assertEquals("He's probably #hiding. Keep an eye out for #hidden entrances.", getReply(npc));
		en.step(player, "hiding");
		assertEquals("I'm sorry, I don't have any more information. Perhaps #Haizen would know more.", getReply(npc));
		en.step(player, "Haizen");
		assertEquals("Haizen is the Wizard that lives to the west of Ados City.", getReply(npc));

		npc = getNPC("Haizen");
		assertNotNull(npc);
		en = npc.getEngine();

		en.step(player, "hi");
		en.step(player, "apothecary");
		assertEquals(
			"Yes, there was once an estudious man in Kalavan. But, due to complications with leadership"
			+ " there he was forced to leave. I heard that he was #hiding somewhere in the Semos region.",
			getReply(npc));
		en.step(player, "hiding");
		assertEquals("If I were hiding, I would surely do it in a #'secret room' with a hidden entrance.", getReply(npc));
		en.step(player, "secret room");
		assertEquals("I'm sorry, I don't have any more information. Perhaps #'Ortiv Milquetoast' would know more.", getReply(npc));
		en.step(player, "Ortiv Milquetoast");
		assertEquals("Ortiv Milquetoast is a former instructor in alchemy, retired and living in Kirdneh City.", getReply(npc));

		npc = getNPC("Ortiv Milquetoast");
		assertNotNull(npc);
		en = npc.getEngine();

		en.step(player, "hi");
		en.step(player, "apothecary");
		assertEquals(
			"You must be speaking of my colleague, Jameson. He was forced to #hide out because of problems in Kalavan."
			+ " He hasn't told me where, but he does bring the most delicious #pears when he visits.",
			getReply(npc));
		en.step(player, "hide");

		final List<String> replies = new ArrayList<>();
		for (final RPEvent event: npc.events()) {
			if (event.getName().equals(Events.PUBLIC_TEXT)) {
				replies.add(event.get("text"));
			}
		}

		assertTrue(Integer.valueOf(2) == replies.size());
		assertEquals(
			"He hinted at a secret laboratory that he had built. Something about a hidden doorway."
			+ " Did I mention how delicious the #pears are that he brings?",
			replies.get(0));
		assertEquals("!me licks his lips", replies.get(1));
		en.step(player, "pears");
		assertEquals(
				"My friends tell me that pears can be found in Semos's mountains. If you travel there, please"
				+ " be sure to bring some back for me.",
				getReply(npc));
	}

	private void testQuestNotActive() {
		assertNull(player.getQuest(questName));
		assertNull(player.getQuest(subquestName));
		assertFalse(player.isEquippedWithInfostring("note", infostring));

		// Zoey
		Engine en = zoologist.getEngine();

		// ignores players when quest is not active
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("!me yawns", getReply(zoologist));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		// Ognir
		en = ringmaker.getEngine();

		// should not reply to requests to make antivenom ring if quest is not active
		en.step(player, "hi");
		en.step(player, apothecary.getName());
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hi! Can I #help you?", getReply(ringmaker));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		// Jameson
		en = apothecary.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
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
		assertEquals("That is not a #yes or #no answer. I said, Is that note you are carrying for me?", getReply(apothecary));

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

		karma = player.getKarma();

		// player accepts quest
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(karma + 5.0, player.getKarma(), 0);
		assertEquals(ApothecaryStage.getMixItems(), player.getQuest(questName));
		assertEquals("Klaas has asked me to assist you. I can mix an antivenom that can be infused into a ring to increase its resistance to poison."
				+ " I need you to bring me " + items_string + ".  Do you have any of those with you?",
				getReply(apothecary));
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Okay. I still need " + items_string, getReply(apothecary));

		// make sure note was dropped
		assertFalse(player.isEquippedWithInfostring("note", infostring));
	}

	private void testQuestActive() {
		assertEquals(ApothecaryStage.getMixItems(), player.getQuest(questName));

		testGeneralResponses();

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
		en.step(player, "vial");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hmmm... I don't have any here. But maybe you could find one in a laboratory somewhere.", getReply(zoologist));
		en.step(player, "venom gland");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I would need the gland of a snake large enough to extract a decent amount.", getReply(zoologist));

		// player asks about venom
		en.step(player, "extract");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals("What's that, you need some venom to create an antivemon? I can extract the venom from a "
				+ "cobra's #'venom gland', but I will need a #vial to hold it in. Do you have those items?",
				getReply(zoologist));

		en.step(player, "vial");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
				"Hmmm... I don't have any here. But maybe you could find one in a laboratory somewhere."
				+ " So, do you have the items?",
				getReply(zoologist));
		en.step(player, "venom gland");
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
				"I would need the gland of a snake large enough to extract a decent amount."
				+ " So, do you have the items?",
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

		PlayerTestHelper.equipWithStackableItem(player, "mandragora", 2);
		PlayerTestHelper.equipWithStackableItem(player, "kokuda", 1);
		PlayerTestHelper.equipWithStackableItem(player, "fairy cake", 20);

		assertTrue(player.isEquipped("cobra venom"));
		assertTrue(player.isEquipped("mandragora", 2));
		assertTrue(player.isEquipped("kokuda"));
		assertTrue(player.isEquipped("fairy cake", 20));

		// says has the items
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.QUESTION_2, en.getCurrentState());
		assertEquals("What did you bring?", getReply(apothecary));

		en.step(player, "mandragora");
		assertEquals("Excellent! Do you have anything else with you?", getReply(apothecary));
		assertFalse(player.isEquipped("mandragora"));
		en.step(player, "kokuda");
		assertEquals("Excellent! Do you have anything else with you?", getReply(apothecary));
		assertFalse(player.isEquipped("kokuda"));
		en.step(player, "fairy cake");
		assertEquals("Excellent! Do you have anything else with you?", getReply(apothecary));
		assertFalse(player.isEquipped("fairy cake"));
		en.step(player, "cobra venom");
		assertFalse(player.isEquipped("cobra venom"));

		assertEquals("Thank you. I'll get to work on mixing the antivenom right after I enjoy a few of these fairy cakes. Please come back in 30 minutes.",
				getReply(apothecary));
		assertTrue(player.getQuest(questName).startsWith("mixing"));

		// zoologist back to ignoring player
		en = zoologist.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("!me yawns", getReply(zoologist));

		en = apothecary.getEngine();

		// player returns before ring is ready
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertTrue(getReply(apothecary).startsWith("I have not finished mixing the antivenom. Please check back in"));

		player.setQuest(questName, 1, "0");
		int xp = player.getXP();
		double karma = player.getKarma();

		// player returns after ring is ready
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("I have finished mixing the antivenom. Ognir is a skilled ring smith. He can infuse the antivenom into rings."
				+ " Ask him about an #'antivenom ring'. Now I'll finish the rest of my fairy cakes if you dont mind.",
				getReply(apothecary));

		assertEquals(xp + 1000, player.getXP());
		assertEquals(karma + 50.0, player.getKarma(), 0);
		assertTrue(player.isEquipped("antivenom"));
		assertEquals("ringmaker", player.getQuest(questName));
		assertNull(player.getQuest(subquestName));

		// player has not yet been to see ring maker
		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Have you not been to see the ring maker Ognir?", getReply(apothecary));

		// player lost antivenom
		player.drop("antivenom", player.getNumberOfEquipped("antivenom"));
		en.step(player, "hi");
		assertEquals(ConversationStates.QUEST_OFFERED, en.getCurrentState());
		assertEquals(
			"What's this? You have lost the antivenom? I can mix another batch, but I will need you to gather the ingredients again. Do you want me to mix another antivenom?",
			getReply(apothecary));
		en.step(player, "no");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Oh, well, come back to me if you can't find your antivenom.", getReply(apothecary));
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Okay, I need you to bring me " + items_string + ". Do you have any of those with you?", getReply(apothecary));
		assertEquals(ApothecaryStage.getMixItems(), player.getQuest(questName));
		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		// set quest state to progress with ring maker stage
		player.setQuest(questName, "ringmaker");

		// Ognir
		en = ringmaker.getEngine();

		// make sure not carrying items
		assertFalse(player.isEquipped("antivenom"));
		assertFalse(player.isEquipped("medicinal ring"));
		assertFalse(player.isEquipped("money", 1000));

		final Item antivenom = ItemTestHelper.createItem("antivenom");
		final Item ring = ItemTestHelper.createItem("medicinal ring");
		final Item money = ItemTestHelper.createItem("money", 1000);

		final String negativeReply = "I can make a stronger ring for you that resists poison, but I need you to bring me antivenom and a medicinal ring."
				+ " I also require a fee of 1000 money.";

		en.step(player, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hi! Can I #help you?", getReply(ringmaker));
		en.step(player, apothecary.getName());
		assertEquals(negativeReply, getReply(ringmaker));

		// player is carrying antivenom only
		player.equip("bag", antivenom);
		en.step(player, apothecary.getName());
		assertEquals(negativeReply, getReply(ringmaker));
		player.drop(antivenom);

		// player is carrying medicinal ring only
		player.equip("bag", ring);
		en.step(player, apothecary.getName());
		assertEquals(negativeReply, getReply(ringmaker));
		player.drop(ring);

		// player is carrying money only
		player.equip("bag", money);
		en.step(player, apothecary.getName());
		assertEquals(negativeReply, getReply(ringmaker));

		// player is carrying antivenom, medicinal ring, & enough money
		player.equip("bag", antivenom);
		player.equip("bag", ring);
		en.step(player, apothecary.getName());
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		assertEquals(
			"I can make your medicinal ring stronger, but I need a vial of antivenom."
			+ " I also require a fee of 1000 money. Do you want to pay that price?",
			getReply(ringmaker));
		en.step(player, "no");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Okay. Let me know if you change your mind.", getReply(ringmaker));
		en.step(player, apothecary.getName());
		assertEquals(ConversationStates.QUESTION_1, en.getCurrentState());
		en.step(player, "yes");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(
			"I will get to work immediately infusing your ring with the antivenom. Please come back in 3"
			+ " days. And be sure to ask for your #'antivenom ring'.",
			getReply(ringmaker));

		assertEquals("fusing", player.getQuest(questName, 0));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertFalse(player.isEquipped("antivenom"));
		assertFalse(player.isEquipped("medicinal ring"));
		assertFalse(player.isEquipped("money", 1000));

		// player returns before ring is done
		en.step(player, "hi");
		en.step(player, apothecary.getName());
		assertTrue(getReply(ringmaker).startsWith("Your antivenom ring is not ready. Please come back in"));

		player.setQuest(questName, 1, "0");
		xp = player.getXP();
		karma = player.getKarma();

		// player returns after ring is done
		en.step(player, apothecary.getName());
		assertEquals("Your antivenom ring is ready.", getReply(ringmaker));

		assertEquals(xp + 2000, player.getXP());
		assertEquals(karma + 150.0, player.getKarma(), 0);
		assertTrue(player.isEquipped("antivenom ring"));

		final Item new_ring = player.getFirstEquipped("antivenom ring");
		assertEquals(player.getName(), new_ring.getBoundTo());

		assertEquals("done", player.getQuest(questName));
	}

	private void testQuestDone() {
		Engine en = zoologist.getEngine();

		en.step(player, "hi");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("!me yawns", getReply(zoologist));

		en = ringmaker.getEngine();

		en.step(player, "hi");
		en.step(player, apothecary.getName());
		assertEquals("Hi! Can I #help you?", getReply(ringmaker));

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
	}

	private void testGeneralResponses() {
		// general responses from apothecary
		Engine en = apothecary.getEngine();

		en.step(player, "hi");

		en.step(player, "Klaas");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh yes, my good old friend. I used to travel to #Athor quite often to gather the very rare "
			+ "#kokuda herb. I got to know Klaas very well as a result.",
			getReply(apothecary));
		en.step(player, "athor");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"You mean you haven't visited Athor? It is a beautiful island. A great place to get away. But "
			+ "stay away from the cannibals' territory. If they invite you for dinner, you might never make it home.",
			getReply(apothecary));
		en.step(player, "ring");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("There are many types of rings.", getReply(apothecary));
		en.step(player, "medicinal ring");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Some poisonous creatures carry them.", getReply(apothecary));
		en.step(player, "antivenom ring");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("If you bring me what I need I may be able to strengthen a #medicinal #ring.", getReply(apothecary));
		/* this item is not available
		en.step(player, "antitoxin ring");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Heh! This is the ultimate protection against poisoning. Good luck getting one!", getReply(apothecary));
		*/
		en.step(player, "venom gland");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Some #snakes have a gland in which their venom is stored.", getReply(apothecary));
		en.step(player, "cobra");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"I've heard rumor of a newly discovered pit full of snakes somewhere in Ados. But I've never "
			+ "searched for it myself. That kind of work is better left to adventurers.",
			getReply(apothecary));
		en.step(player, "cobra venom");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Someone who specializes with animals might know how to obtain some. I suggest visiting the "
			+ "sanctuary in Ados.",
			getReply(apothecary));

		final String mandragoraResponse = "This is my favorite of all the herbs and one of the most rare. There are only a few places "
				+ "in Faimouni where it grows. Keep a vigilant eye, or you will pass them right up.";

		en.step(player, "mandragora");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(mandragoraResponse, getReply(apothecary));
		en.step(player, "root of mandragora");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(mandragoraResponse, getReply(apothecary));
		en.step(player, "kokuda"); // kokuda is not a required item for quest, but is mentioned in general dialog
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("The kokuda is an herb that can only be found inside #Athor Island's labyrinth.", getReply(apothecary));
		en.step(player, "fairy cake");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
			"Oh, fairy cakes are the best treat I have ever tasted. Only the most heavenly creatures "
			+ "could make such angelic food.",
			getReply(apothecary));

		en.step(player, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}
}
