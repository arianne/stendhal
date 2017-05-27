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
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.market.BBQGrillmasterNPC;
import games.stendhal.server.maps.semos.mines.MinerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * JUnit test for the CoalForHaunchy quest.
 * @author bluelads, M. Fuchs
 */
public class CoalForHaunchyTest extends ZonePlayerAndNPCTestImpl {

	private String questSlot;
	private static final String ZONE_NAME = "0_ados_city_n2";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public CoalForHaunchyTest() {
		super(ZONE_NAME, "Haunchy", "Barbarus");
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		new BBQGrillmasterNPC().configureZone(zone, null);
		new MinerNPC().configureZone(zone, null);

		quest = new CoalForHaunchy();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testQuest() {
		SpeakerNPC haunchy = SingletonRepository.getNPCList().get("Haunchy Meatoch");
		Engine haunchyEng = haunchy.getEngine();

		SpeakerNPC barbarus = SingletonRepository.getNPCList().get("Barbarus");
		Engine barbarusEng = barbarus.getEngine();

		// -----------------------------------------------
		// start with Haunchy

		haunchyEng.step(player, "hi");
		assertEquals("Hey! Nice day for a BBQ!", getReply(haunchy));
		haunchyEng.step(player, "task");
		assertEquals("I cannot use wood for this huge BBQ. To keep the heat I need some really old stone coal but there isn't much left. The problem is, that I can't fetch it myself because my steaks would burn then so I have to stay here. Can you bring me 25 pieces of #coal for my BBQ please?", getReply(haunchy));
		haunchyEng.step(player, "coal");
		assertEquals("Coal isn't easy to find. You normally can find it somewhere in the ground but perhaps you are lucky and find some in the old Semos Mine tunnels...", getReply(haunchy));
		haunchyEng.step(player, "yes");
		assertEquals("Thank you! If you have found 25 pieces, say #coal to me so I know you have it. I'll be sure to give you a nice and tasty reward.", getReply(haunchy));
		haunchyEng.step(player, "coal");
		assertEquals("You don't have the coal amount which I need yet. Go and pick some more pieces up, please.", getReply(haunchy));
		haunchyEng.step(player, "bye");
		assertEquals("A nice day to you! Always keep your fire burning!", getReply(haunchy));

		// -----------------------------------------------
		// now talk to Barbarus

		barbarusEng.step(player, "hi");
		assertEquals("Good luck!", getReply(barbarus));
		barbarusEng.step(player, "buy pick");
		assertEquals("A pick will cost 400. Do you want to buy it?", getReply(barbarus));
		barbarusEng.step(player, "yes");
		assertEquals("Sorry, you don't have enough money!", getReply(barbarus));
		barbarusEng.step(player, "bye");
		assertEquals("Nice to meet you. Good luck!", getReply(barbarus));

		barbarusEng.step(player, "hi");
		assertEquals("Good luck!", getReply(barbarus));
		barbarusEng.step(player, "buy pick");
		assertEquals("A pick will cost 400. Do you want to buy it?", getReply(barbarus));
		PlayerTestHelper.equipWithMoney(player, 400);
		barbarusEng.step(player, "yes");
		assertEquals("Congratulations! Here is your pick!", getReply(barbarus));
		// You see a pick. It is a tool which helps you to get some coal.
		assertTrue(player.isEquipped("pick"));
		barbarusEng.step(player, "bye");
		assertEquals("Nice to meet you. Good luck!", getReply(barbarus));

		// get 10 coals
		PlayerTestHelper.equipWithStackableItem(player, "coal", 10);
		haunchyEng.step(player, "hi");
		assertEquals("Hey! Nice day for a BBQ!", getReply(haunchy));
		haunchyEng.step(player, "task");
		assertEquals("You don't have the coal amount which I need yet. Go and pick some more pieces up, please.", getReply(haunchy));
		haunchyEng.step(player, "bye");
		assertEquals("A nice day to you! Always keep your fire burning!", getReply(haunchy));

		// get another 15 coals
		PlayerTestHelper.equipWithStackableItem(player, "coal", 25);
		haunchyEng.step(player, "hi");
		assertEquals("Hey! Nice day for a BBQ!", getReply(haunchy));
		haunchyEng.step(player, "task");
		// We get one or more grilled steaks a reward:
		// You see a fresh grilled steak. It smells awesome and is really juicy. It is a special quest reward for player, and cannot be used by others. Stats are (HP: 200).
		assertTrue(getReply(haunchy).matches("Thank you!! Take .* grilled steaks? from my grill!"));
		assertTrue(player.isEquipped("grilled steak"));
		assertEquals("waiting", player.getQuest(questSlot, 0));
		haunchyEng.step(player, "bye");
		assertEquals("A nice day to you! Always keep your fire burning!", getReply(haunchy));

		// -----------------------------------------------

		haunchyEng.step(player, "hi");
		assertEquals("Hey! Nice day for a BBQ!", getReply(haunchy));
		haunchyEng.step(player, "task");
		assertEquals("The coal amount behind my counter is still high enough. I will not need more for 2 days.", getReply(haunchy));
		haunchyEng.step(player, "bye");
		assertEquals("A nice day to you! Always keep your fire burning!", getReply(haunchy));

		// -----------------------------------------------

		haunchyEng.step(player, "hi");
		assertEquals("Hey! Nice day for a BBQ!", getReply(haunchy));
		haunchyEng.step(player, "coal");
		assertEquals("Sometime you could do me a #favour ...", getReply(haunchy));
		haunchyEng.step(player, "favour");
		assertEquals("The coal amount behind my counter is still high enough. I will not need more for 2 days.", getReply(haunchy));
		haunchyEng.step(player, "offer");
		assertEquals("I hope that my steaks will be ready soon. Please be a bit patient or have some other snacks first.", getReply(haunchy));
		haunchyEng.step(player, "help");
		assertEquals("Unfortunately the steaks aren't ready yet... If you are hungry and can't wait, you could check some offers in the near out like the Blacksheep offers near the fisherhuts in Ados or you can take a ferry to Athor for getting some nice snacks...", getReply(haunchy));
		haunchyEng.step(player, "task");
		assertEquals("The coal amount behind my counter is still high enough. I will not need more for 2 days.", getReply(haunchy));
		haunchyEng.step(player, "bye");
		assertEquals("A nice day to you! Always keep your fire burning!", getReply(haunchy));
	}
}
