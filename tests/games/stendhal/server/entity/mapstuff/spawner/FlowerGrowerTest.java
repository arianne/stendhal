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
package games.stendhal.server.entity.mapstuff.spawner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.area.Allotment;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPClass;
import utilities.RPClass.GrowingPassiveEntityRespawnPointTestHelper;

public class FlowerGrowerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		if (!RPClass.hasRPClass("area")) {
			AreaEntity.generateRPClass();
		}
		GrowingPassiveEntityRespawnPointTestHelper.generateRPClasses();
	}

	/**
	 * Tests for onFruitPicked.
	 */
	@Test
	public void testOnFruitPicked() {
		final FlowerGrower fl = new FlowerGrower();
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(fl);
		assertFalse(zone.getPlantGrowers().isEmpty());
		fl.onFruitPicked(null);
		assertTrue(zone.getPlantGrowers().isEmpty());
	}

	/**
	 * Tests for flowerGrower.
	 */
	@Test
	public void testFlowerGrower() {
		final FlowerGrower fl = new FlowerGrower();
		assertThat(fl.getMaxRipeness(), is(4));

	}

	/**
	 * Tests for getDescription.
	 */
	@Test
	public void testGetDescription() {
		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		assertThat(fl.describe(),
				is("You see something which has just been planted."));
		fl.setRipeness(1);
		assertThat(fl.describe(), is("Something is sprouting from the ground."));
		fl.setRipeness(2);
		assertThat(fl.describe(),
				is("A plant is growing here, and you can already see foliage."));
		fl.setRipeness(3);
		assertThat(
				fl.describe(),
				is("You see a plant growing a lilia, it is nearly at full maturity."));
		fl.setRipeness(4);
		assertThat(
				fl.describe(),
				is("You see a fully grown lilia, ready to pull from the ground."));
		fl.setRipeness(5);
		assertThat(fl.describe(), is("You see an unripe lilia."));
	}

	/**
	 * Tests for getDescriptionAnyitem.
	 */
	@Test
	public void testGetDescriptionAnyitem() {
		final FlowerGrower fl = new FlowerGrower("someotherItem");
		fl.setRipeness(0);
		assertThat(fl.describe(),
				is("You see something which has just been planted."));
		fl.setRipeness(1);
		assertThat(fl.describe(), is("Something is sprouting from the ground."));
		fl.setRipeness(2);
		assertThat(fl.describe(),
				is("A plant is growing here, and you can already see foliage."));
		fl.setRipeness(3);
		assertThat(
				fl.describe(),
				is("You see a plant growing a someotheritem, it is nearly at full maturity."));
		fl.setRipeness(4);
		assertThat(
				fl.describe(),
				is("You see a fully grown someotheritem, ready to pull from the ground."));
		fl.setRipeness(5);
		assertThat(fl.describe(), is("You see an unripe someotheritem."));
	}

	/**
	 * Tests for growOnFertileGround.
	 */
	@Test
	public void testGrowOnFertileGround() {
		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		final Entity entity = new Allotment();
		zone.add(entity);
		zone.add(fl);

		assertTrue(fl.isOnFreeFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(), is(1));
		assertTrue(zone.getPlantGrowers().contains(fl));
	}

	/**
	 * Tests for growOnFertileGround2.
	 */
	@Test
	public void testGrowOnFertileGround2() {
		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		final Entity entity = new Allotment();

		zone.add(fl);
		zone.add(entity);
		assertTrue(fl.isOnFreeFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(), is(1));
		assertTrue(zone.getPlantGrowers().contains(fl));
	}

	/**
	 * Tests for growFertileGroundElsewhere.
	 */
	@Test
	public void testGrowFertileGroundElsewhere() {
		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		final Entity entity = new Allotment();
		entity.setPosition(10, 10);
		zone.add(fl);
		zone.add(entity);
		assertFalse(fl.isOnFreeFertileGround());

		// check it withers when grown
		fl.growNewFruit();
		assertThat(fl.getRipeness(), is(0));
		assertFalse(zone.getPlantGrowers().contains(fl));
	}

	/**
	 * Tests for growOnInFertileGround.
	 */
	@Test
	public void testGrowOnInFertileGround() {

		final FlowerGrower fl = new FlowerGrower();
		fl.setRipeness(0);

		assertFalse(fl.isOnFreeFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(), is(0));
	}

	/**
	 * Check that growing on top of another FlowerGrower fails
	 */
	@Test
	public void testGrowOnFreeFertileGroundReserved() {
		final FlowerGrower fl = new FlowerGrower();
		final FlowerGrower fl2 = new FlowerGrower();
		fl.setRipeness(0);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		final Entity entity = new Allotment();

		zone.add(fl);
		zone.add(fl2);
		zone.add(entity);
		assertFalse(fl.isOnFreeFertileGround());
		fl.growNewFruit();
		assertThat(fl.getRipeness(), is(0));

		// check that the right one got removed
		assertTrue(zone.getPlantGrowers().contains(fl2));
		assertFalse(zone.getPlantGrowers().contains(fl));
	}
}
