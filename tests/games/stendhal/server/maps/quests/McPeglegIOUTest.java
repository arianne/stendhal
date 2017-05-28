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
import games.stendhal.server.maps.semos.tavern.RareWeaponsSellerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * JUnit test for the McPeglegIOU quest.
 * @author bluelads, M. Fuchs
 */
public class McPeglegIOUTest extends ZonePlayerAndNPCTestImpl {

	private SpeakerNPC npc = null;
	private Engine en = null;

	private String questSlot;
	private static final String ZONE_NAME = "int_semos_tavern_1";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public McPeglegIOUTest() {
		super(ZONE_NAME, "McPegleg");
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		new RareWeaponsSellerNPC().configureZone(zone, null);

		quest = new McPeglegIOU();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testQuest() {
		npc = SingletonRepository.getNPCList().get("McPegleg");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Yo matey! You look like you need #help.", getReply(npc));
		en.step(player, "IOU");
		assertEquals("I can't see that you got a valid IOU with my signature!", getReply(npc));
		en.step(player, "task");
		assertEquals("Perhaps if you find some #rare #armor or #weapon ...", getReply(npc));
		en.step(player, "rare armor");
		en.step(player, "weapon");
		assertEquals("Ssshh! I'm occasionally buying rare weapons and armor. Got any? Ask for my #offer", getReply(npc));
		en.step(player, "rare armor");
		en.step(player, "McPegleg doesn't react although rare armor is blue...");
		en.step(player, "offer");
		assertEquals("Have a look at the blackboard on the wall to see my offers.", getReply(npc));
		en.step(player, "IOU");
		assertEquals("I can't see that you got a valid IOU with my signature!", getReply(npc));
		en.step(player, "bye");
		assertEquals("I see you!", getReply(npc));

		// equip with IOU "IOU 250 money. (signed) McPegleg"
		PlayerTestHelper.equipWithItem(player, "note", "charles");

		en.step(player, "hi");
		assertEquals("Yo matey! You look like you need #help.", getReply(npc));
		en.step(player, "IOU");
		assertEquals("Where did you get that from? Anyways, here is the money *sighs*", getReply(npc));
		en.step(player, "got 100 money into my bag");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot));

		assertEquals("I see you!", getReply(npc));
		en.step(player, "hi");
		assertEquals("Yo matey! You look like you need #help.", getReply(npc));
		en.step(player, "IOU");
		assertEquals("You already got cash for that damned IOU!", getReply(npc));
		en.step(player, "bye");
		assertEquals("I see you!", getReply(npc));
	}
}
