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
package games.stendhal.server.actions.equip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class SourceObjectTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for isNotValid.
	 */
	@Test
	public void testIsNotValid() {
		SourceObject so = SourceObject.createSourceObject(null, null);
		assertFalse("null null does not make a valid source", so.isValid());

		so = SourceObject.createSourceObject(new RPAction(), PlayerTestHelper.createPlayer("bob"));
		assertFalse("empty action does not make a valid source", so.isValid());

		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, 1);
		so = SourceObject.createSourceObject(action, PlayerTestHelper.createPlayer("bob"));
		assertFalse("Player is not in zone", so.isValid());
	}

	/**
	 * Tests for isValidNonContained.
	 */
	@Test
	public void testIsValidNonContained() {
		MockStendlRPWorld.get();
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("dropzone");
		final Item dropitem = ItemTestHelper.createItem();
		zone.add(dropitem);
		zone.add(bob);
		assertNotNull(dropitem.getID().getObjectID());
		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, dropitem.getID().getObjectID());
		MockStendlRPWorld.get().addRPZone(zone);
		assertNotNull(bob.getZone());

		final SourceObject so = SourceObject.createSourceObject(action, bob);
		assertTrue(so.isValid());
	}

	/**
	 * Test a source under another player.
	 */
	@Test
	public void testIsValidNonContainedBelowPlayer() {
		MockStendlRPWorld.get();
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final Player alice = PlayerTestHelper.createPlayer("alice");
		alice.setPosition(0, 1);
		final StendhalRPZone zone = new StendhalRPZone("dropzone");
		final Item dropitem = ItemTestHelper.createItem();
		zone.add(dropitem);
		zone.add(bob);
		zone.add(alice);
		assertNotNull(dropitem.getID().getObjectID());
		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, dropitem.getID().getObjectID());
		MockStendlRPWorld.get().addRPZone(zone);
		assertNotNull(bob.getZone());

		SourceObject so = SourceObject.createSourceObject(action, alice);
		assertFalse("Picking an item below another player", so.isValid());

		// Bind it to alice. She should be able to pick it up
		dropitem.setBoundTo("alice");
		so = SourceObject.createSourceObject(action, alice);
		assertTrue("Picking an item belonging to player herself", so.isValid());

		// Sanity check. Should be still valid to bob
		so = SourceObject.createSourceObject(action, bob);
		assertTrue("Accessing bound object below oneself", so.isValid());
		// ... unless it's under alice
		dropitem.setPosition(0, 1);
		so = SourceObject.createSourceObject(action, bob);
		assertFalse("Picking an item below another player", so.isValid());
	}

	/**
	 * Tests for isValidContainedNoSlot.
	 */
	@Test
	public void testIsValidContainedNoSlot() {
		MockStendlRPWorld.get();
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("dropzone");
		final Item dropitem = SingletonRepository.getEntityManager().getItem("money");
		assertNotNull(dropitem);
		zone.add(bob);
		assertTrue(bob.equipToInventoryOnly(dropitem));
		assertNotNull(dropitem.getID().getObjectID());
		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, dropitem.getID().getObjectID());

		action.put(EquipActionConsts.BASE_OBJECT , bob.getID().getObjectID());

		MockStendlRPWorld.get().addRPZone(zone);
		assertNotNull(bob.getZone());

		final SourceObject so = SourceObject.createSourceObject(action, bob);
		assertFalse("no slot defined", so.isValid());
	}

	/**
	 * Tests for isValidContained.
	 */
	@Test
	public void testIsValidContained() {
		MockStendlRPWorld.get();
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("dropzone");
		final Item dropitem = SingletonRepository.getEntityManager().getItem("money");
		assertNotNull(dropitem);
		zone.add(bob);
		assertTrue(bob.equipToInventoryOnly(dropitem));
		assertNotNull(dropitem.getID().getObjectID());
		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, dropitem.getID().getObjectID());

		action.put(EquipActionConsts.BASE_OBJECT , bob.getID().getObjectID());
		action.put(EquipActionConsts.BASE_SLOT, "bag");
		MockStendlRPWorld.get().addRPZone(zone);
		assertNotNull(bob.getZone());

		final SourceObject so = SourceObject.createSourceObject(action, bob);
		assertTrue("Unreachable slot", so.isValid());
	}

	/**
	 * Tests for isValidContainedNotInslot.
	 */
	@Test
	public void testIsValidContainedNotInslot() {
		MockStendlRPWorld.get();
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("dropzone");
		final Item dropitem = SingletonRepository.getEntityManager().getItem("money");
		assertNotNull(dropitem);
		zone.add(bob);
		dropitem.setID(new RPObject.ID(999, "blabla"));
		//assertTrue(bob.equip(dropitem));
		assertNotNull(dropitem.getID().getObjectID());
		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, dropitem.getID().getObjectID());

		action.put(EquipActionConsts.BASE_OBJECT , bob.getID().getObjectID());
		action.put(EquipActionConsts.BASE_SLOT, "bag");
		MockStendlRPWorld.get().addRPZone(zone);
		assertNotNull(bob.getZone());

		final SourceObject so = SourceObject.createSourceObject(action, bob);
		assertFalse("Itemnot in bag", so.isValid());
	}

	/**
	 * Tests for isValidStackable.
	 */
	@Test
	public void testIsValidStackable() {
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("dropzone");
		final Item dropitem = ItemTestHelper.createItem("drops", 5);
		zone.add(dropitem);
		zone.add(bob);
		assertNotNull(dropitem.getID().getObjectID());
		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, dropitem.getID().getObjectID());
		MockStendlRPWorld.get().addRPZone(zone);
		assertNotNull(bob.getZone());

		final SourceObject so = SourceObject.createSourceObject(action, bob);
		assertTrue(so.isValid());
		assertEquals("stackable returns full quantity", dropitem.getQuantity(), so.getQuantity());
	}

	/**
	 * Tests for isValidStackableDropAFew.
	 */
	@Test
	public void testIsValidStackableDropAFew() {
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("dropzone");
		final Item dropitem = ItemTestHelper.createItem("drops", 5);
		zone.add(dropitem);
		zone.add(bob);
		MockStendlRPWorld.get().addRPZone(zone);

		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, dropitem.getID().getObjectID());
		final int amounttodrop = 3;
		action.put(EquipActionConsts.QUANTITY, amounttodrop);
		final SourceObject so = SourceObject.createSourceObject(action, bob);
		assertTrue(so.isValid());
		assertEquals("return the amount to be dropped", amounttodrop, so.getQuantity());
	}

	/**
	 * Tests for isValidStackableDropTooMany.
	 */
	@Test
	public void testIsValidStackableDropTooMany() {
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("dropzone");
		final Item dropitem = ItemTestHelper.createItem("drops", 5);
		zone.add(dropitem);
		zone.add(bob);
		MockStendlRPWorld.get().addRPZone(zone);

		final RPAction action = new RPAction();
		action.put(EquipActionConsts.BASE_ITEM, dropitem.getID().getObjectID());
		action.put(EquipActionConsts.QUANTITY, dropitem.getQuantity() + 3);
		final SourceObject so = SourceObject.createSourceObject(action, bob);
		assertTrue(so.isValid());
		assertEquals("too many are reduced to all", dropitem.getQuantity(), so.getQuantity());
	}



}
