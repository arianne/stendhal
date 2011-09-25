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
package games.stendhal.server.entity.npc.behaviour.impl;

import static org.junit.Assert.*;

import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour.ExpireOutfit;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * tests for OutfitChangerBehaviour
 */
public class OutfitChangerBehaviourTest {

	/**
	 * prepare tests
	 */
	@BeforeClass
	public static void setupBeforeClass() {
		
		MockStendlRPWorld.get();
	}

	/**
	 * cleanup after tests
	 */
	@AfterClass
	public static void teardownAfterClass() {
		MockStendlRPWorld.reset();
	}

	/**
	 * Tests for onWornOff.
	 */
	@Test
	public void testOnWornOff() {
		Map<String, Integer> pricelist = new HashMap<String, Integer>();
		pricelist.put("trunks", Integer.valueOf(50));
		Player player = PlayerTestHelper.createPlayer("bob");
		ExpireOutfit cloth = new ExpireOutfit(player.getName());
		ExpireOutfit cloth2 = new ExpireOutfit(player.getName());
		assertTrue(cloth.equals(cloth2));
		assertTrue(cloth2.equals(cloth));
		
		ExpireOutfit cloth3 = new ExpireOutfit(PlayerTestHelper.createPlayer("bob").getName());
		
		assertTrue(cloth.equals(cloth3));
		assertTrue(cloth3.equals(cloth));
		
	}

}
