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
package games.stendhal.server.entity.npc.behaviour.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Seed;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class SeedSellerBehaviourTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.generateNPCRPClasses();
	}

	/**
	 * Tests for transactAgreedDeal.
	 */
	@Test
	public void testTransactAgreedDeal() {
		final Map<String, Integer> pricelist = new HashMap<String, Integer>();
		SellerBehaviour sb = new SeedSellerBehaviour(pricelist);
		pricelist.put("lilia seed", 10);
		pricelist.put("daisies seed", 20);
		final SpeakerNPC speakerNPC = new SpeakerNPC("hugo");

		sb = new SeedSellerBehaviour(pricelist);
		ItemParserResult res = new ItemParserResult(true, "lilia seed",1, null);
		final Player bob = PlayerTestHelper.createPlayer("bob");
		PlayerTestHelper.equipWithMoney(bob, 100);
		sb.transactAgreedDeal(res, new EventRaiser(speakerNPC), bob);
		final Item seed = bob.getFirstEquipped("seed");
		assertNotNull(seed);
		assertEquals("lilia", seed.getInfoString());
		assertEquals(90, ((Stackable<?>) bob.getFirstEquipped("money")).getQuantity());
	}

	/**
	 * Tests for getAskedItem.
	 */
	@Test
	public void testGetAskedItem() {
		final Map<String, Integer> pricelist = new HashMap<String, Integer>();
		final SeedSellerBehaviour sb = new SeedSellerBehaviour(pricelist);
		pricelist.put("lilia seed", 10);
		pricelist.put("daisies seed", 20);
		Item item = sb.getAskedItem("lilia seed");
		assertTrue(item instanceof Seed);
		assertEquals("lilia", item.getInfoString());

		item = sb.getAskedItem("daisies seed");
		assertTrue(item instanceof Seed);
		assertEquals("daisies", item.getInfoString());
	}

}
