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

import static org.junit.Assert.*;

import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour.OutwearClothes;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class OutfitChangerBehaviourTest {
@BeforeClass
public static void setupBeforeClass() {
	
	MockStendlRPWorld.get();
}

@AfterClass
public static void teardownAfterClass() throws Exception {
	
	MockStendlRPWorld.reset();
}
	/**
	 * Tests for onWornOff.
	 */
	@Test
	public void testOnWornOff() {
		Map<String, Integer> pricelist = new HashMap<String, Integer>();
		pricelist.put("trunks", Integer.valueOf(50));
		OutfitChangerBehaviour beh = new OutfitChangerBehaviour(pricelist);
		Player player = PlayerTestHelper.createPlayer("bob");
		OutwearClothes cloth = beh.new OutwearClothes(player);
		OutwearClothes cloth2 = beh.new OutwearClothes(player);
		assertTrue(cloth.equals(cloth2));
		assertTrue(cloth2.equals(cloth));
		
		OutwearClothes cloth3 = beh.new OutwearClothes(PlayerTestHelper.createPlayer("bob"));
		
		assertTrue(cloth.equals(cloth3));
		assertTrue(cloth3.equals(cloth));
		
	}

}
