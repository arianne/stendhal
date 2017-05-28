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
package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.geom.Rectangle2D;

import org.junit.BeforeClass;
import org.junit.Test;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

public class EntityTest {
	@BeforeClass
	public static void init() {
		if (!RPClass.hasRPClass("test_rpclass")) {
			new RPClass("test_rpclass");
		}
	}

	/**
	 * Tests for entity.
	 */
	@Test
	public final void testEntity() {
		final IEntity en = new MockEntity();

		assertEquals(0.0, en.getX(), 0.001);
		assertEquals(0.0, en.getY(), 0.001);

	}

	/**
	 * Tests for initialize.
	 */
	@Test
	public final void testInitialize() {
		MockEntity en;
		RPObject rpo;
		rpo = new RPObject();

		en = new MockEntity();
		assertEquals(0, en.count);
		en.initialize(rpo);
		assertEquals("onPosition should only be called once ", 1, en.count);
	}

	/**
	 * Tests for entityRPObject.
	 */
	@Test
	public final void testEntityRPObject() {
		final RPObject rpo = new RPObject();
		rpo.setRPClass("test_rpclass");
		rpo.put("name", "bob");

		final IEntity en = new MockEntity();
		en.initialize(rpo);
		assertEquals("test_rpclass", en.getType());
		assertEquals("bob", en.getName());
	}

	/**
	 * Tests for getID.
	 */
	@Test
	public final void testGetID() {
		final RPObject rpo = new RPObject();
		rpo.put("type", "hugo");
		rpo.setID(new ID(1, "woohoo"));
		final IEntity en = new MockEntity();
		en.initialize(rpo);
		assertNotNull("id must not be null", en.getID());
		assertEquals(1, en.getID().getObjectID());
		assertEquals("woohoo", en.getID().getZoneID());
	}

	/**
	 * Tests for getNamegetType.
	 */
	@Test
	public final void testGetNamegetType() {
		IEntity en;
		RPObject rpo;
		rpo = new RPObject();
		rpo.setRPClass("test_rpclass");
		en = new MockEntity();
		en.initialize(rpo);
		assertEquals("test_rpclass", en.getType());

		rpo = new RPObject();
		rpo.setRPClass("test_rpclass");
		rpo.put("name", "ragnarok");
		en = new MockEntity();
		en.initialize(rpo);
		assertEquals("test_rpclass", en.getType());
		assertEquals("ragnarok", en.getName());
	}

	/**
	 * Tests for getXGetY.
	 */
	@Test
	public final void testGetXGetY() {
		IEntity en;
		en = new MockEntity();

		assertEquals(0.0, en.getX(), 0.001);
		assertEquals(0.0, en.getY(), 0.001);
	}

	/**
	 * Tests for distance.
	 */
	@Test
	public final void testDistance() {
		final Entity en = new MockEntity();
		User.setNull();
		User to = null;
		assertEquals(Double.POSITIVE_INFINITY, User.squaredDistanceTo(en.x, en.y), 0.001);
		to = new User();

		en.x = 3;
		en.y = 4;
		assertEquals(3.0, en.getX(), 0.001);
		assertEquals(4.0, en.getY(), 0.001);
		assertEquals(25.0, User.squaredDistanceTo(en.x, en.y), 0.001);
		assertEquals(0.0, User.squaredDistanceTo(to.x, to.y), 0.001);

	}

	/**
	 * Tests for getSlot.
	 */
	@Test
	public final void testGetSlot() {
		final IEntity en = new MockEntity();
		assertEquals(null, en.getSlot(""));

	}

	private static class MockEntity extends Entity {
		private int count;

		public MockEntity() {
			rpObject = new RPObject();
			rpObject.put("type", "entity");
		}

		@Override
		public Rectangle2D getArea() {
			return null;
		}

		@Override
		protected void onPosition(final double x, final double y) {
			count++;
			super.onPosition(x, y);

		}

	}
}
