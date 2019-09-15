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

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.fado.city.GreeterNPC;
import games.stendhal.server.maps.fado.hotel.HotelChefNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Tests for WaterForXhiphin
 *
 * @author krupi, hendrik
 */
public class WaterForXhiphinTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "admin_test";
	private static final String QUEST_SLOT = "water_for_xhiphin";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public WaterForXhiphinTest() {
		setNpcNames("Xhiphin Zohos", "Stefan");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new HotelChefNPC(), ZONE_NAME);
		addZoneConfigurator(new GreeterNPC(), ZONE_NAME);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		new WaterForXhiphin().addToWorld();
	}

	@Test
	public void testQuestStartQuest() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get("Xhiphin Zohos");
		Engine en = npc.getEngine();

		// 1.START
		en.step(player, "hi");
		assertEquals("Hello! Welcome to Fado City! You can #learn about Fado from me.", getReply(npc));
		en.step(player, "offer");
		assertEquals("I sell fado city scroll and empty scroll.", getReply(npc));
		en.step(player, "help");
		assertEquals("You can head into the tavern to buy food and drinks. You can also visit the people in the houses, or visit the blacksmith or the city hotel.", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm really thirsty, could you possibly get me some fresh water please?", getReply(npc));
		en.step(player, "sure");
		assertEquals("Thank you! Natural spring water is best, the river that runs from Fado to Nal'wor might provide a source.", getReply(npc));
		assertEquals("start", player.getQuest(QUEST_SLOT));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	// 2.MAKE WATER CLEAN
	@Test
	public void testVerifyWater() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get("Stefan");
		Engine en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Welcome in the Fado hotel kitchen, stranger!", getReply(npc));
		en.step(player, "offer");
		assertEquals("The kitchen isn't open at the moment and before it can be opened... I have to think about a solution for my #problem in here... I am a really #stressed #cook now!", getReply(npc));
		en.step(player, "problem");
		assertEquals("Being the only #cook... ahem... The only #chef! All alone... In a tiny hotel restaurant kitchen... It will never going to work at all!", getReply(npc));
		en.step(player, "help");
		assertEquals("I'm really #stressed in this kitchen here... I am the only *cough* #cook *cough* #chef *cough* around here... If I only could tell you about all the ingredients that are missing in this place...That #troublesome #customer down there... Keeps ranting and... go ask him what he wants now!", getReply(npc));
		en.step(player, "stressed");
		assertEquals("It's high season at the moment! We get lots of reservations which means more #guests and more work for everyone.", getReply(npc));
		en.step(player, "guests");
		assertEquals("Most of the guests visit Fado for #getting #married. I can understand why choosing Fado for getting married. Fado is indeed a beautiful and tranquil, a very romantic city... Except for that troublesome customer down there... That #troublesome #customer keeps ranting... Please, go ask him what he wants now!", getReply(npc));
		en.step(player, "getting married");
		assertEquals("Didn't you know that Fado is the most known wedding town in whole Faiumoni? You have to visit our church, it's so lovely!", getReply(npc));
		en.step(player, "bottle of water");
		assertEquals("You can gather water from natural mountain springs or bigger springs like next to waterfalls. If you bring it to me I can check the purity for you.", getReply(npc));

		PlayerTestHelper.equipWithItem(player, "water");
		en.step(player, "bottle of water");
		assertEquals("That water looks clean to me! It must be from a pure source.", getReply(npc));
		en.step(player, "water");
		assertEquals("That water looks clean to me! It must be from a pure source.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye! Have a nice stay in Fado!", getReply(npc));
	}

	@Test
	public void testQuestWithoutWater() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get("Xhiphin Zohos");
		Engine en = npc.getEngine();
		player.setQuest(QUEST_SLOT, "start");

		en.step(player, "hi");
		assertEquals("Hello! Welcome to Fado City! You can #learn about Fado from me.", getReply(npc));
		en.step(player, "offer");
		assertEquals("I sell fado city scroll and empty scroll.", getReply(npc));
		en.step(player, "help");
		assertEquals("You can head into the tavern to buy food and drinks. You can also visit the people in the houses, or visit the blacksmith or the city hotel.", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm waiting for you to bring me some drinking water, this sun is so hot.", getReply(npc));
		en.step(player, "quest");
		assertEquals("I'm waiting for you to bring me some drinking water, this sun is so hot.", getReply(npc));
		en.step(player, "job");
		assertEquals("I greet all of the new-comers to Fado. I can #offer you a scroll if you'd like to come back here again.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}


	// 4.TRY WITH SOILED WATER
	@Test
	public void testQuestWitSoiledWater() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get("Xhiphin Zohos");
		Engine en = npc.getEngine();
		player.setQuest(QUEST_SLOT, "start");
		PlayerTestHelper.equipWithItem(player, "water");

		en.step(player, "hi");
		assertEquals("Hello! Welcome to Fado City! You can #learn about Fado from me.", getReply(npc));
		en.step(player, "water");
		assertEquals("Hmm... it's not that I don't trust you, but I'm not sure that water is okay to drink. Could you go and ask #Stefan to #check it please?", getReply(npc));
		en.step(player, "check");
		assertEquals("Sorry, I'm no expert on food or drink myself, try asking #Stefan.", getReply(npc));
		en.step(player, "stefan");
		assertEquals("Stefan is a chef over in the restaurant at Fado Hotel. I'd trust him to check if anything is safe to eat or drink, he's a professional.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

		// 5.TRY WITH CLEAN WATER
	@Test
	public void testQuestWithCleanWater() {
		SpeakerNPC npc = SingletonRepository.getNPCList().get("Xhiphin Zohos");
		Engine en = npc.getEngine();
		player.setQuest(QUEST_SLOT, "start");
		PlayerTestHelper.equipWithItem(player, "water", "clean");

		en.step(player, "HI");
		assertEquals("Hello! Welcome to Fado City! You can #learn about Fado from me.", getReply(npc));
		en.step(player, "quest");
		// [19:05] krupi earns 100 experience points.
		assertEquals("Thank you ever so much! That's just what I wanted! Here, take these potions that Sarzina gave me - I hardly have use for them here.", getReply(npc));
		en.step(player, "thanks");
		en.step(player, "task");
		assertEquals("Thank you, I don't need anything right now.", getReply(npc));
		en.step(player, "water");
		assertEquals("Thank you, I don't need anything right now.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
