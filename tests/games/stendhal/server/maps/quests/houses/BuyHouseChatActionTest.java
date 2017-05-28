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
package games.stendhal.server.maps.quests.houses;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class BuyHouseChatActionTest {
	private HousePortal housePortal;
	private StoredChest chest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generateNPCRPClasses();
		Chest.generateRPClass();
		Portal.generateRPClass();
		HousePortal.generateRPClass();
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		HouseUtilities.clearCache();
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Remove added stored entities.
	 * <p>
	 * stored entities can pollute the database
	 * if a server is ran on the same system as the tests.
	 */
	@After
	public void clearStored() {
		if (housePortal != null) {
			StendhalRPZone zone = housePortal.getZone();
			if (zone != null) {
				zone.remove(housePortal);
				housePortal = null;
			}
		}

		if (chest != null) {
			StendhalRPZone zone = chest.getZone();
			if (zone != null) {
				zone.remove(chest);
				chest = null;
			}
		}
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public void testFire() {
		BuyHouseChatAction action = new BuyHouseChatAction(1, HouseSellerNPCBase.QUEST_SLOT);
		String zoneName = "0_ados_city_n";
		StendhalRPZone ados = new StendhalRPZone(zoneName);
		MockStendlRPWorld.get().addRPZone(ados);
		housePortal = new HousePortal("schnick bla 51");
		housePortal.setIdentifier("keep rpzone happy");
		housePortal.setDestination(zoneName, "schnick bla 51");
		ados.add(housePortal);
		chest = new StoredChest();
		ados.add(chest);
		HouseUtilities.clearCache();

		SpeakerNPC engine = new SpeakerNPC("bob");
		EventRaiser raiser = new EventRaiser(engine);
		Player player = PlayerTestHelper.createPlayer("george");
		Sentence sentence = ConversationParser.parse("51");
		action.fire(player , sentence , raiser);
		assertThat(getReply(engine), is("You do not have enough money to buy a house!"));
		housePortal.setOwner("jim");

		action.fire(player , sentence , raiser);
		assertThat(getReply(engine), containsString("Sorry, house 51 is sold"));

		PlayerTestHelper.equipWithMoney(player, 1);

		housePortal.setOwner("");

		action.fire(player , sentence , raiser);
		assertThat(getReply(engine), containsString("Congratulation"));
		assertFalse(player.isEquipped("money"));
	}

}
