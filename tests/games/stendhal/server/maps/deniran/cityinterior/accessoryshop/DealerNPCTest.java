/***************************************************************************
 *                      (C) Copyright 2023 - Stendhal                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.accessoryshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static utilities.ZoneAndPlayerTestImpl.setupZone;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;


public class DealerNPCTest extends SpeakerNPCTestHelper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AchievementTestHelper.setUpBeforeClass();
		setupZone("testzone", new DealerNPC());
	}

	@Test
	public void init() {
		final Player player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);
		final SpeakerNPC gwen = getSpeakerNPC("Gwen");
		assertNotNull(gwen);

		// configure Gwen's shop
		SingletonRepository.getOutfitShopsList().configureNPC(gwen, "deniran_accessories", "buy", true,
				false);

		final Engine en = gwen.getEngine();
		en.step(player, "hi");
		en.step(player, "offer");
		assertEquals(
			//~ "I'm sorry but my shop is not quite set up yet. Come back in the future and I"
				//~ + " will have some accessories for you.",
			"You can #buy axe1, axe2, black balloon, bone, bow, kite shield, knife, shield, spear, sword,"
					+ " umbrella, and white balloon.",
			getReply(gwen));
		en.step(player, "bye");
	}
}
