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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.PlayerTestHelper.createPlayerWithOutFit;
import static utilities.PlayerTestHelper.equipWithStackableItem;
import static utilities.ZoneAndPlayerTestImpl.setupZone;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.actions.RemoveDetailAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.PlayerIsWearingOutfitCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.shop.OutfitShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopsList;
import games.stendhal.server.entity.player.Player;
import utilities.AchievementTestHelper;
import utilities.SpeakerNPCTestHelper;


public class DealerNPCTest extends SpeakerNPCTestHelper {

	private static final OutfitShopsList oshops = SingletonRepository.getOutfitShopsList();


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AchievementTestHelper.setUpBeforeClass();
		setupZone("testzone", new DealerNPC());
	}

	private boolean isWearingOutfit(final Player player, final Outfit outfit) {
		return new PlayerIsWearingOutfitCondition(outfit).fire(player, null, null);
	}

	private void removeDetail(final Player player) {
		new RemoveDetailAction().onAction(player, null);
	}

	@Test
	public void init() {
		final Player player = createPlayerWithOutFit("player");
		assertNotNull(player);
		final SpeakerNPC gwen = getSpeakerNPC("Gwen");
		assertNotNull(gwen);

		// configure Gwen's shop
		oshops.configureNPC(gwen, "deniran_accessories", "buy", false, false);

		testOffer(player, gwen);
		testInsufficientFunds(player, gwen);
		testInventory(player, gwen);
	}

	private void testOffer(final Player player, final SpeakerNPC gwen) {
		final Engine en = gwen.getEngine();
		en.step(player, "hi");
		en.step(player, "offer");
		assertEquals(
			//~ "You can #buy axe1, axe2, black balloon, bone, bow, kite shield, knife, shield, spear, sword,"
					//~ + " umbrella, and white balloon.",
			"Please see the catalog on the chair for a list of accessories that I offer.",
			getReply(gwen));
		en.step(player, "bye");
	}

	private void testInsufficientFunds(final Player player, final SpeakerNPC gwen) {
		final Outfit outfit = new Outfit("detail=2");
		final Engine en = gwen.getEngine();

		assertEquals(0, player.getNumberOfEquipped("money"));
		en.step(player, "hi");
		en.step(player, "buy white balloon");
		assertEquals(
			"To buy a white balloon will cost 50000. Do you want to buy it?",
			getReply(gwen));
		en.step(player, "yes");
		assertEquals(
			"Sorry, you don't have enough money!",
			getReply(gwen));
		assertFalse(isWearingOutfit(player, outfit));
		en.step(player, "bye");
	}

	private void testInventory(final Player player, final SpeakerNPC gwen) {
		final OutfitShopInventory inventory = oshops.get("deniran_accessories");
		final Engine en = gwen.getEngine();
		en.step(player, "hi");

		for (final String itemname: inventory.keySet()) {
			final Outfit outfit = inventory.getOutfit(itemname);
			final int price = inventory.getPrice(itemname);

			assertEquals(0, player.getNumberOfEquipped("money"));
			assertFalse(isWearingOutfit(player, outfit));
			equipWithStackableItem(player, "money", price);
			assertEquals(price, player.getNumberOfEquipped("money"));
			en.step(player, "buy " + itemname);
			en.step(player, "yes");
			assertTrue(isWearingOutfit(player, outfit));
			assertEquals(0, player.getNumberOfEquipped("money"));
			removeDetail(player);
			assertFalse(isWearingOutfit(player, outfit));
		}

		en.step(player, "bye");
	}
}
