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

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Rectangle2D;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

public class GateTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!RPClass.hasRPClass("test_rpclass")) {
			new RPClass("test_rpclass");
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for getArea.
	 */
	@Test
	public void testGetArea() {
		Gate g = new Gate();

		assertEquals(new Rectangle2D.Double(0, 0, 0, 0).toString(), g.getArea().toString());
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertEquals(new Rectangle2D.Double(1, 2, 3, 4), g.getArea());

	}

	/**
	 * Tests for getAudibleArea.
	 */
	@Test
	public void testGetAudibleArea() {
		Gate g = new Gate();
		Rectangle2D expected = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);
		assertEquals(expected, g.getAudibleArea());
	}

	/**
	 * Tests for getEntityClass.
	 */
	@Test
	public void testGetEntityClass() {
		Gate g = new Gate();
		assertThat(g.getEntityClass(), is(""));
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertThat(g.getEntityClass(), nullValue());
		object.put("class", "class");
		assertThat(g.getEntityClass(), is("class"));
	}

	/**
	 * Tests for getEntitySubclass.
	 */
	@Test
	public void testGetEntitySubclass() {
		Gate g = new Gate();
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertThat(g.getEntitySubclass(), nullValue());
		object.put("subclass", "subclass");
		assertThat(g.getEntitySubclass(), is("subclass"));
	}

	/**
	 * Tests for getHeight.
	 */
	@Test
	public void testGetHeight() {
		Gate g = new Gate();
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertThat(g.getHeight(), is(4.0));
	}

	/**
	 * Tests for getID.
	 */
	@Test
	public void testGetID() {
		Gate g = new Gate();
		assertThat(g.getID(), nullValue());
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		object.setID(RPObject.INVALID_ID);
		g.initialize(object);
		assertThat(g.getID(), is(object.getID()));

	}

	/**
	 * Tests for getName.
	 */
	@Test
	public void testGetName() {
		Gate g = new Gate();
		assertThat(g.getName(), is(""));
	}

	/**
	 * Tests for getRPObject.
	 */
	@Test
	public void testGetRPObject() {
		Gate g = new Gate();
		assertThat(g.getRPObject(), nullValue());
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertThat(g.getRPObject(), sameInstance(object));

	}

	/**
	 * Tests for getResistance.
	 */
	@Test
	public void testGetResistance() {
		Gate g = new Gate();
		assertThat(g.getResistance(), is(0));
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		object.put("resistance", 100);
		g.initialize(object);
		assertThat(g.getResistance(), is(100));
	}

	/**
	 * Tests for getResistanceIEntity.
	 */
	@Test
	public void testGetResistanceIEntity() {
		Gate g = new Gate();
		assertThat(g.getResistance(new Entity()), is(0));
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		object.put("resistance", 100);
		g.initialize(object);
		assertThat(g.getResistance(new Entity()), is(100));

	}

	/**
	 * Tests for getSlot.
	 */
	@Test
	public void testGetSlot() {
		Gate g = new Gate();
		assertThat(g.getSlot(null), nullValue());	}

	/**
	 * Tests for getTitle.
	 */
	@Test
	public void testGetTitle() {
		Gate g = new Gate();
		assertThat(g.getTitle(), nullValue());
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		object.put("title", "title");
		g.initialize(object);
		assertThat(g.getTitle(), is("title"));

	}

	/**
	 * Tests for getType.
	 */
	@Test
	public void testGetType() {
		Gate g = new Gate();
		assertThat(g.getType(), nullValue());
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		object.setRPClass("test_rpclass");
		g.initialize(object);
		assertThat(g.getType(), is("test_rpclass"));
	}

	/**
	 * Tests for getVisibility.
	 */
	@Test
	public void testGetVisibility() {
		Gate g = new Gate();
		assertThat(g.getVisibility(), is(100));

	}

	/**
	 * Tests for getWidth.
	 */
	@Test
	public void testGetWidth() {
		Gate g = new Gate();
		assertThat(g.getEntityClass(), is(""));
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertThat(g.getWidth(), is(3.0));

	}

	/**
	 * Tests for getX.
	 */
	@Test
	public void testGetX() {
		Gate g = new Gate();
		assertThat(g.getEntityClass(), is(""));
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertThat(g.getX(), is(1.0));

	}

	/**
	 * Tests for getY.
	 */
	@Test
	public void testGetY() {
		Gate g = new Gate();
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertThat(g.getY(), is(2.0));

	}

	/**
	 * Tests for initialize.
	 */
	@Test
	public void testInitialize() {
		Gate g = new Gate();
		RPObject object = new RPObject();
		object.put("x", 1);
		object.put("y", 2);
		object.put("width", 3);
		object.put("height", 4);
		g.initialize(object);
		assertThat(g.getX(), is(1.0));
		assertThat(g.getY(), is(2.0));
		assertThat(g.getWidth(), is(3.0));
		assertThat(g.getHeight(), is(4.0));
		assertSame(object, g.getRPObject());
	}

	/**
	 * Tests for isOnGround.
	 */
	@Test
	public void testIsOnGround() {
		Gate g = new Gate();
		assertTrue(g.isOnGround());
	}


	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() {
		Gate g = new Gate();
		assertThat(g.toString(), startsWith("games.stendhal.client.entity.Gate"));
	}

}
