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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.semos.kanmararn.CowardSoldierNPC;
import games.stendhal.server.maps.semos.kanmararn.SergeantNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * JUnit test for the KanmararnSoldiers quest.
 * @author bluelads, M. Fuchs
 */
public class KanmararnSoldiersTest extends ZonePlayerAndNPCTestImpl {

	private String questSlot;
	private static final String ZONE_NAME = "-6_kanmararn_city";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public KanmararnSoldiersTest() {
		super(ZONE_NAME, "Henry", "Sergeant James");
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		new CowardSoldierNPC().configureZone(zone, null);
		new SergeantNPC().configureZone(zone, null);

		quest = new KanmararnSoldiers();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testQuest() {
		SpeakerNPC henry = SingletonRepository.getNPCList().get("Henry");
		Engine en1 = henry.getEngine();

		SpeakerNPC james = SingletonRepository.getNPCList().get("Sergeant James");
		Engine en2 = james.getEngine();

		// talk with Henry
		en1.step(player, "hi");
		assertEquals("Ssshh! Silence or you will attract more #dwarves.", getReply(henry));
		en1.step(player, "dwarves");
		assertEquals("They are everywhere! Their #kingdom must be close.", getReply(henry));
		en1.step(player, "kingdom");
		assertEquals("Kanmararn, the legendary city of the #dwarves.", getReply(henry));
		en1.step(player, "dwarves");
		assertEquals("They are everywhere! Their #kingdom must be close.", getReply(henry));
		en1.step(player, "kingdom");
		assertEquals("Kanmararn, the legendary city of the #dwarves.", getReply(henry));
		en1.step(player, "job");
		assertEquals("I'm a soldier in the army.", getReply(henry));
		en1.step(player, "offer");
		en1.step(player, "help");
		assertEquals("I need help myself. I got separated from my #group. Now I'm all alone.", getReply(henry));
		en1.step(player, "group");
		assertEquals("The General sent five of us to explore this area in search for #treasure.", getReply(henry));
		en1.step(player, "treasure");
		assertEquals("A big treasure is rumored to be #somewhere in this dungeon.", getReply(henry));
		en1.step(player, "somewhere");
		assertEquals("If you #help me I might give you a clue.", getReply(henry));
		en1.step(player, "help");
		assertEquals("I need help myself. I got separated from my #group. Now I'm all alone.", getReply(henry));
		en1.step(player, "group");
		assertEquals("The General sent five of us to explore this area in search for #treasure.", getReply(henry));
		en1.step(player, "task");
		assertEquals("Find my #group, Peter, Tom, and Charles, prove it and I will reward you. Will you do it?", getReply(henry));
		en1.step(player, "group");
		assertEquals("The General sent five of us to explore this area in search for #treasure. So, will you help me find them?", getReply(henry));
		en1.step(player, "no");
		assertEquals("OK. I understand. I'm scared of the #dwarves myself.", getReply(henry));
		en1.step(player, "task");
		assertEquals("Find my #group, Peter, Tom, and Charles, prove it and I will reward you. Will you do it?", getReply(henry));
		en1.step(player, "group");
		assertEquals("The General sent five of us to explore this area in search for #treasure. So, will you help me find them?", getReply(henry));
		en1.step(player, "yes");
		assertEquals("Thank you! I'll be waiting for your return.", getReply(henry));
		en1.step(player, "task");
		assertEquals("I have already asked you to find my friends Peter, Tom, and Charles.", getReply(henry));
		en1.step(player, "map");
		assertEquals("If you find my friends, I will give you the map.", getReply(henry));
		en1.step(player, "treasure");
		assertEquals("A big treasure is rumored to be #somewhere in this dungeon.", getReply(henry));
		en1.step(player, "dwarves");
		assertEquals("They are everywhere! Their #kingdom must be close.", getReply(henry));
		en1.step(player, "kingodom");
		assertEquals("Kanmararn, the legendary city of the #dwarves.", getReply(henry));
		en1.step(player, "bye");
		assertEquals("Bye and be careful with all those dwarves around!", getReply(henry));

		en1.step(player, "hi");
		assertEquals("You didn't prove that you have found them all!", getReply(henry));
		en1.step(player, "bye");
		assertEquals("Bye and be careful with all those dwarves around!", getReply(henry));

		// You see the cold corpse of Peter. You can inspect it to see its contents.
		// You see a slightly rusty scale armor. It is heavily deformed by several strong hammer blows. Stats are (DEF: 7).
		// take scale armor with me
		PlayerTestHelper.equipWithItem(player, "scale armor", "peter");

		// You see the rotten corpse of Tom. You can inspect it to see its contents.
		// take leather legs with me
		// You see torn leather legs that are heavily covered with blood. Stats are (DEF: 1).
		PlayerTestHelper.equipWithItem(player, "leather legs", "tom");


		// now talk with James
		en2.step(player, "hi");
		assertEquals("Good day, adventurer!", getReply(james));
		en2.step(player, "map");
		assertEquals("The #treasure map that leads into the heart of the #dwarven #kingdom.", getReply(james));
		en2.step(player, "treasure");
		assertEquals("A big treasure is rumored to be somewhere in this dungeon.", getReply(james));
		en2.step(player, "dwarven kingdom");
		assertEquals("They are strong enemies! We're in their #kingdom.", getReply(james));
		en2.step(player, "kingdom");
		assertEquals("Kanmararn, the legendary kingdom of the #dwarves.", getReply(james));
		en2.step(player, "dwarves");
		assertEquals("They are strong enemies! We're in their #kingdom.", getReply(james));
		en2.step(player, "dwarven kingdom");
		assertEquals("They are strong enemies! We're in their #kingdom.", getReply(james));
		en2.step(player, "bye");
		assertEquals("Good luck and better watch your back with all those dwarves around!", getReply(james));

		en2.step(player, "hi");
		assertEquals("Good day, adventurer!", getReply(james));
		en2.step(player, "task");
		assertEquals("Find my fugitive soldier and bring him to me ... or at least the #map he's carrying.", getReply(james));
		en2.step(player, "bye");
		assertEquals("Good luck and better watch your back with all those dwarves around!", getReply(james));
		// You see the slightly rotten corpse of Charles. You can inspect it to see its contents.
		// You read: "IOU 250 money. (signed) McPegleg"
		// take IOU with me
		PlayerTestHelper.equipWithItem(player, "note", "charles");
		en2.step(player, "hi");

		// drop leather legs for testing
		player.drop("leather legs");

		en1.step(player, "hi");
		assertEquals("You didn't prove that you have found them all!", getReply(henry));
		en1.step(player, "bye");
		assertEquals("Bye and be careful with all those dwarves around!", getReply(henry));

		// take legs up again
		PlayerTestHelper.equipWithItem(player, "leather legs", "tom");

		assertFalse(player.isEquipped("map"));
		assertEquals(0, player.getXP());

		// back to Henry
		en1.step(player, "hi");
		assertEquals("Oh my! Peter, Tom, and Charles are all dead? *cries*. Anyway, here is your reward. And keep the IOU.", getReply(henry));
		// player earns 2500 experience points.
		assertEquals(2500, player.getXP());
		// You see a hand drawn map, but no matter how you look at it, nothing on it looks familiar.
		assertTrue(player.isEquipped("map"));
		assertEquals("map", player.getQuest(questSlot, 0));
		en1.step(player, "bye");
		assertEquals("Bye and be careful with all those dwarves around!", getReply(henry));

		// back to James
		en2.step(player, "hi");
		assertEquals("Good day, adventurer!", getReply(james));
		en2.step(player, "map");
		assertEquals("The map! Wonderful! Thank you. And here is your reward. I got these boots while on the #dreamscape.", getReply(james));
		// player earns 5000 experience points.
		assertEquals(7500, player.getXP());
		assertTrue(player.isEquipped("mainio boots"));
		en2.step(player, "bye");
		assertEquals("Good luck and better watch your back with all those dwarves around!", getReply(james));

		// back to Henry
		en1.step(player, "hi");
		assertEquals("Ssshh! Silence or you will attract more #dwarves.", getReply(henry));
		en1.step(player, "dwarves");
		assertEquals("They are everywhere! Their #kingdom must be close.", getReply(henry));
		en1.step(player, "task");
		assertEquals("I'm so sad that most of my friends are dead.", getReply(henry));

		assertEquals("done", player.getQuest(questSlot, 0));
	}
}
