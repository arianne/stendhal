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
package games.stendhal.server.entity.creature;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.Arrays;
import java.util.List;

import marauroa.common.game.RPObject;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.BabyDragonTestHelper;

public class BabyDragonTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BabyDragonTestHelper.generateRPClasses();
		MockStendlRPWorld.get();
	}

	List<String> foods = Arrays.asList("ham", "pizza", "meat");

	/**
	 * Tests for babyDragon.
	 */
	@Test
	public void testBabyDragon() {
		final BabyDragon drako = new BabyDragon();
		assertThat(drako.getFoodNames(), is(foods));
	}

	/**
	 * Tests for babyDragonPlayer.
	 */
	@Test
	public void testBabyDragonPlayer() {

		final StendhalRPZone zone = new StendhalRPZone("zone");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		zone.add(bob);
		final BabyDragon drako = new BabyDragon(bob);

		assertThat(drako.getFoodNames(), is(foods));
	}

	/**
	 * Tests for babyDragonRPObjectPlayer.
	 */
	@Test
	public void testBabyDragonRPObjectPlayer() {
		final BabyDragon drako = new BabyDragon(new RPObject(), PlayerTestHelper.createPlayer("bob"));
		assertThat(drako.getFoodNames(), is(foods));
	}

}
