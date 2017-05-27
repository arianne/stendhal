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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.RPClass.GrowingPassiveEntityRespawnPointTestHelper;

public class PassiveEntityRespawnPointFactoryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		GrowingPassiveEntityRespawnPointTestHelper.generateRPClasses();
	}

	/**
	 * Tests for createHerb.
	 */
	@Test
	public final void testCreateHerb() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "blaherbbla";
		PassiveEntityRespawnPoint herb_0 = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(herb_0);
		assertTrue(herb_0 instanceof VegetableGrower);
		assertEquals("arandula", ((VegetableGrower) herb_0).getVegetableName());

		PassiveEntityRespawnPoint herb_1 = PassiveEntityRespawnPointFactory
				.create(clazz, 1, null, 0, 0);
		assertNotNull(herb_1);
		assertTrue(herb_1 instanceof VegetableGrower);
		assertEquals("kekik", ((VegetableGrower) herb_1).getVegetableName());

		PassiveEntityRespawnPoint herb_2 = PassiveEntityRespawnPointFactory
				.create(clazz, 2, null, 0, 0);
		assertNotNull(herb_2);
		assertTrue(herb_2 instanceof VegetableGrower);
		assertEquals("sclaria", ((VegetableGrower) herb_2).getVegetableName());

		PassiveEntityRespawnPoint herb_3 = PassiveEntityRespawnPointFactory
				.create(clazz, 3, null, 0, 0);
		assertNotNull(herb_3);
		assertTrue(herb_3 instanceof VegetableGrower);
		assertEquals("mandragora", ((VegetableGrower) herb_3).getVegetableName());

		PassiveEntityRespawnPoint herb_4 = PassiveEntityRespawnPointFactory
				.create(clazz, 4, null, 0, 0);
		assertNotNull(herb_4);
		assertEquals("You see a place where reindeer moss grows.",
				herb_4.getDescription());

	}

	/**
	 * Tests for createCorn.
	 */
	@Test
	public final void testCreateCorn() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "blacornbla";
		PassiveEntityRespawnPoint grain = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(grain);
		assertTrue(grain instanceof GrainField);
	}

	/**
	 * Tests for createMushroom.
	 */
	@Test
	public final void testCreateMushroom() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "blamushroombla";
		PassiveEntityRespawnPoint value_0 = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(value_0);
		assertTrue(value_0 instanceof VegetableGrower);
		assertEquals("button mushroom", ((VegetableGrower) value_0)
				.getVegetableName());

		PassiveEntityRespawnPoint value_1 = PassiveEntityRespawnPointFactory
				.create(clazz, 1, null, 0, 0);
		assertNotNull(value_1);
		assertTrue(value_1 instanceof VegetableGrower);
		assertEquals("porcini", ((VegetableGrower) value_1).getVegetableName());

		PassiveEntityRespawnPoint value_2 = PassiveEntityRespawnPointFactory
				.create(clazz, 2, null, 0, 0);
		assertNotNull(value_2);
		assertTrue(value_2 instanceof VegetableGrower);
		assertEquals("toadstool", ((VegetableGrower) value_2)
				.getVegetableName());

		PassiveEntityRespawnPoint value_3 = PassiveEntityRespawnPointFactory
				.create(clazz, 3, null, 0, 0);
		assertNull(value_3);

	}

	/**
	 * Tests for createResource.
	 */
	@Test
	public final void testCreateResource() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "blaresourcesbla";
		PassiveEntityRespawnPoint value_0 = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(value_0);
		assertTrue(value_0 instanceof VegetableGrower);
		assertEquals("wood", ((VegetableGrower) value_0).getVegetableName());

		PassiveEntityRespawnPoint value_1 = PassiveEntityRespawnPointFactory
				.create(clazz, 1, null, 0, 0);
		assertNotNull(value_1);
		assertEquals("You see a small vein of iron ore.",
				value_1.getDescription());

		PassiveEntityRespawnPoint value_2 = PassiveEntityRespawnPointFactory
				.create(clazz, 2, null, 0, 0);
		assertNotNull(value_2);
		assertEquals("You see a trace of a gold shimmer.",
				value_2.getDescription());

		PassiveEntityRespawnPoint value_3 = PassiveEntityRespawnPointFactory
				.create(clazz, 3, null, 0, 0);
		assertNotNull(value_3);
		assertEquals("You see a trace of a silvery shimmer.",
				value_3.getDescription());

		PassiveEntityRespawnPoint value_4 = PassiveEntityRespawnPointFactory
				.create(clazz, 4, null, 0, 0);
		assertNotNull(value_4);
		assertEquals("You see tiny gold shards.",
				value_4.getDescription());

		PassiveEntityRespawnPoint value_5 = PassiveEntityRespawnPointFactory
				.create(clazz, 5, null, 0, 0);
		assertNotNull(value_5);
		assertEquals("You see tiny pieces of mithril ore.",
				value_5.getDescription());

		PassiveEntityRespawnPoint value_6 = PassiveEntityRespawnPointFactory
				.create(clazz, 6, null, 0, 0);
		assertNull(value_6);

	}

	/**
	 * Tests for sheepfoodCorn.
	 */
	@Test
	public final void testsheepfoodCorn() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "blasheepfoodbla";
		PassiveEntityRespawnPoint sheepfood = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(sheepfood);
		assertTrue(sheepfood instanceof SheepFood);
	}

	/**
	 * Tests for createVegetable.
	 */
	@Test
	public final void testCreateVegetable() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "xxvegetablexxx";
		PassiveEntityRespawnPoint value_0 = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(value_0);
		assertEquals("You see a place where an apple looks likely to fall.",
				value_0.getDescription());

		PassiveEntityRespawnPoint value_1 = PassiveEntityRespawnPointFactory
				.create(clazz, 1, null, 0, 0);
		assertNotNull(value_1);
		assertTrue(value_1 instanceof VegetableGrower);
		assertEquals("carrot", ((VegetableGrower) value_1).getVegetableName());

		PassiveEntityRespawnPoint value_2 = PassiveEntityRespawnPointFactory
				.create(clazz, 2, null, 0, 0);
		assertNotNull(value_2);
		assertTrue(value_2 instanceof VegetableGrower);
		assertEquals("salad", ((VegetableGrower) value_2).getVegetableName());

		PassiveEntityRespawnPoint value_3 = PassiveEntityRespawnPointFactory
				.create(clazz, 3, null, 0, 0);
		assertNotNull(value_3);
		assertTrue(value_3 instanceof VegetableGrower);
		assertEquals("broccoli", ((VegetableGrower) value_3).getVegetableName());

		PassiveEntityRespawnPoint value_4 = PassiveEntityRespawnPointFactory
				.create(clazz, 4, null, 0, 0);
		assertNotNull(value_4);
		assertTrue(value_4 instanceof VegetableGrower);
		assertEquals("cauliflower", ((VegetableGrower) value_4)
				.getVegetableName());

		PassiveEntityRespawnPoint value_5 = PassiveEntityRespawnPointFactory
				.create(clazz, 5, null, 0, 0);
		assertNotNull(value_5);
		assertTrue(value_5 instanceof VegetableGrower);
		assertEquals("chinese cabbage", ((VegetableGrower) value_5)
				.getVegetableName());

		PassiveEntityRespawnPoint value_6 = PassiveEntityRespawnPointFactory
				.create(clazz, 6, null, 0, 0);
		assertNotNull(value_6);
		assertTrue(value_6 instanceof VegetableGrower);
		assertEquals("leek", ((VegetableGrower) value_6).getVegetableName());

		PassiveEntityRespawnPoint value_7 = PassiveEntityRespawnPointFactory
				.create(clazz, 7, null, 0, 0);
		assertNotNull(value_7);
		assertTrue(value_7 instanceof VegetableGrower);
		assertEquals("onion", ((VegetableGrower) value_7).getVegetableName());

		PassiveEntityRespawnPoint value_8 = PassiveEntityRespawnPointFactory
				.create(clazz, 8, null, 0, 0);
		assertNotNull(value_8);
		assertTrue(value_8 instanceof VegetableGrower);
		assertEquals("courgette", ((VegetableGrower) value_8)
				.getVegetableName());

		PassiveEntityRespawnPoint value_9 = PassiveEntityRespawnPointFactory
				.create(clazz, 9, null, 0, 0);
		assertNotNull(value_9);
		assertTrue(value_9 instanceof VegetableGrower);
		assertEquals("spinach", ((VegetableGrower) value_9).getVegetableName());

		PassiveEntityRespawnPoint value_10 = PassiveEntityRespawnPointFactory
				.create(clazz, 10, null, 0, 0);
		assertNotNull(value_10);
		assertTrue(value_10 instanceof VegetableGrower);
		assertEquals("collard", ((VegetableGrower) value_10).getVegetableName());

		PassiveEntityRespawnPoint value_11 = PassiveEntityRespawnPointFactory
				.create(clazz, 11, null, 0, 0);
		assertNotNull(value_11);
		assertTrue(value_11 instanceof VegetableGrower);
		assertEquals("garlic", ((VegetableGrower) value_11).getVegetableName());

		PassiveEntityRespawnPoint value_12 = PassiveEntityRespawnPointFactory
				.create(clazz, 12, null, 0, 0);
		assertNotNull(value_12);
		assertTrue(value_12 instanceof VegetableGrower);
		assertEquals("artichoke", ((VegetableGrower) value_12).getVegetableName());

		PassiveEntityRespawnPoint value_13 = PassiveEntityRespawnPointFactory
		.create(clazz, 13, null, 0, 0);
		assertNull(value_13);

	}

	/**
	 * Tests for createJewelry.
	 */
	@Test
	public final void testCreateJewelry() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "xxxjewelryxxx";
		PassiveEntityRespawnPoint value_0 = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(value_0);
		assertEquals("You see trace elements of some red crystal.",
				value_0.getDescription());

		PassiveEntityRespawnPoint value_1 = PassiveEntityRespawnPointFactory
				.create(clazz, 1, null, 0, 0);
		assertNotNull(value_1);
		assertEquals(
				"You see evidence of a sapphire stone being here recently.",
				value_1.getDescription());

		PassiveEntityRespawnPoint value_2 = PassiveEntityRespawnPointFactory
				.create(clazz, 2, null, 0, 0);
		assertNotNull(value_2);
		assertEquals("You see trace elements of the precious gem emerald.",
				value_2.getDescription());

		PassiveEntityRespawnPoint value_3 = PassiveEntityRespawnPointFactory
				.create(clazz, 3, null, 0, 0);
		assertNull(value_3);

	}

	/**
	 * Tests for createFruit.
	 */
	@Test
	public final void testCreateFruit() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "xxfruitsxxx";
		PassiveEntityRespawnPoint value_0 = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(value_0);
		assertEquals("You see a place where a coconut looks likely to fall.",
				value_0.getDescription());

		PassiveEntityRespawnPoint value_1 = PassiveEntityRespawnPointFactory
				.create(clazz, 1, null, 0, 0);
		assertNotNull(value_1);
		assertTrue(value_1 instanceof VegetableGrower);
		assertEquals("tomato", ((VegetableGrower) value_1).getVegetableName());

		PassiveEntityRespawnPoint value_2 = PassiveEntityRespawnPointFactory
				.create(clazz, 2, null, 0, 0);
		assertNotNull(value_2);
		assertEquals("It looks like there's a pineapple sprout growing here.",
				value_2.getDescription());

		PassiveEntityRespawnPoint value_9 = PassiveEntityRespawnPointFactory
				.create(clazz, 9, null, 0, 0);
		assertNotNull(value_9);
		assertEquals("You see a place where a cherry looks likely to fall.",
				value_9.getDescription());

		PassiveEntityRespawnPoint value_10 = PassiveEntityRespawnPointFactory
				.create(clazz, 10, null, 0, 0);
		assertNull(value_10);
	}

	/**
	 * Tests for createMeatAndFish.
	 */
	@Test
	public final void testCreateMeatAndFish() {
		assertNull(PassiveEntityRespawnPointFactory.create("", 0, null, 0, 0));
		final String clazz = "xxmeat_and_fishxxx";
		PassiveEntityRespawnPoint value_0 = PassiveEntityRespawnPointFactory
				.create(clazz, 0, null, 0, 0);
		assertNotNull(value_0);
		assertEquals(
				"You see some leftovers of animal food. Might be meat.",
				value_0.getDescription());

		PassiveEntityRespawnPoint value_1 = PassiveEntityRespawnPointFactory
				.create(clazz, 1, null, 0, 0);
		assertNotNull(value_1);
		assertEquals(
				"You see some leftovers of animal food. Looks like ham.",
				value_1.getDescription());

		PassiveEntityRespawnPoint value_2 = PassiveEntityRespawnPointFactory
				.create(clazz, 2, null, 0, 0);
		assertNotNull(value_2);
		assertEquals(
				"You see some leftovers of animal food. Maybe chicken?",
				value_2.getDescription());

		PassiveEntityRespawnPoint value_3 = PassiveEntityRespawnPointFactory
				.create(clazz, 3, null, 0, 0);
		assertNotNull(value_3);
		assertEquals("You see some shimmering fish scales like from a roach.",
				value_3.getDescription());

		PassiveEntityRespawnPoint value_4 = PassiveEntityRespawnPointFactory
				.create(clazz, 4, null, 0, 0);
		assertNotNull(value_4);
		assertEquals("You see some red fish scales like from a char.",
				value_4.getDescription());

		PassiveEntityRespawnPoint value_5 = PassiveEntityRespawnPointFactory
				.create(clazz, 5, null, 0, 0);
		assertNull(value_5);
	}

}
