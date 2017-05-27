/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Rectangle2D;
import java.util.BitSet;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.entity.Player;
import games.stendhal.client.util.UserInterfaceTestHelper;
import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class CollisionMapTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		UserInterfaceTestHelper.initUserInterface();
	}

	/**
	 * Tests for collissionMap.
	 */
	@Test
	public void testCollissionMap() {
		final CollisionMap map = new CollisionMap(1, 1);
		assertThat(map.getWidth(), is(1));
		assertThat(map.getHeight(), is(1));
		final CollisionMap map2x2 = new CollisionMap(2, 2);
		assertThat(map2x2.getWidth(), is(2));
		assertThat(map2x2.getHeight(), is(2));
	}

	/**
	 * Tests for collides.
	 */
	@Test
	public void testcollides() {
		final CollisionMap map = new CollisionMap(2, 2);
		assertFalse(map.get(0, 0));
		assertFalse(map.get(1, 0));
		assertFalse(map.get(0, 1));
		assertFalse(map.get(1, 1));
		map.set(1, 0);
		assertFalse(map.get(0, 0));
		assertTrue(map.get(1, 0));
		assertFalse(map.get(0, 1));
		assertFalse(map.get(1, 1));

		assertTrue(map.collides(0, 0, 2, 2));
	}

	/**
	 * Tests for collides2.
	 */
	@Test
	public void testcollides2() {
		final CollisionMap map = new CollisionMap(4, 4);
		map.set(0, 0);
		map.set(3, 0);
		map.set(0, 3);
		map.set(3, 3);
		assertTrue(map.collides(0, 0, 4, 4));

		assertTrue(map.collides(0, 0, 2, 2));
		assertFalse(map.collides(0, 1, 2, 2));
		assertTrue(map.collides(0, 2, 2, 2));
		assertTrue("edge", map.collides(0, 3, 2, 2));

		assertFalse(map.collides(1, 0, 2, 2));
		assertFalse(map.collides(1, 1, 2, 2));
		assertFalse(map.collides(1, 2, 2, 2));
		assertTrue("edge", map.collides(1, 3, 2, 2));

		assertTrue(map.collides(2, 0, 2, 2));
		assertFalse(map.collides(2, 1, 2, 2));
		assertTrue(map.collides(2, 2, 2, 2));
		assertTrue("edge", map.collides(2, 3, 2, 2));

		assertTrue("edge", map.collides(3, 0, 2, 2));
		assertTrue("edge", map.collides(3, 1, 2, 2));
		assertTrue("edge", map.collides(3, 2, 2, 2));
		assertTrue("edge", map.collides(3, 3, 2, 2));
	}

	/**
	 * Tests for collidesEdges.
	 */
	@Test
	public void testcollidesEdges() {
		final CollisionMap map = new CollisionMap(4, 4);
		assertFalse(map.collides(0, 0, 2, 2));
		assertFalse(map.collides(0, 1, 2, 2));
		assertFalse(map.collides(0, 2, 2, 2));
		assertTrue("edge", map.collides(0, 3, 2, 2));

		assertFalse(map.collides(1, 0, 2, 2));
		assertFalse(map.collides(1, 1, 2, 2));
		assertFalse(map.collides(1, 2, 2, 2));
		assertTrue("edge", map.collides(1, 3, 2, 2));

		assertFalse(map.collides(2, 0, 2, 2));
		assertFalse(map.collides(2, 1, 2, 2));
		assertFalse(map.collides(2, 2, 2, 2));
		assertTrue("edge", map.collides(2, 3, 2, 2));

		assertTrue("edge", map.collides(3, 0, 2, 2));
		assertTrue("edge", map.collides(3, 1, 2, 2));
		assertTrue("edge", map.collides(3, 2, 2, 2));
		assertTrue("edge", map.collides(3, 3, 2, 2));
	}

	/**
	 * Tests for bitsetlogic.
	 */
	@Test
	public void testbitsetlogic() {
		final BitSet setallfalse = new BitSet(7);
		final BitSet invert = (BitSet) setallfalse.clone();
		assertThat(invert.length(), is(0));

		invert.flip(0, 6);
		assertThat(invert.length(), is(6));
		final BitSet set = new BitSet();
		set.set(3);
		set.set(4);
		set.set(5);
		set.set(7);
		set.set(8);

		final BitSet subset = set.get(2, 6);

		assertFalse(subset.get(0));
		assertTrue(subset.get(1));
		assertTrue(subset.get(2));
		assertTrue(subset.get(3));
		assertFalse(subset.get(4));
		assertFalse(subset.get(5));

		final BitSet subset2 = set.get(2, 8);

		assertFalse(subset2.get(0));
		assertTrue(subset2.get(1));
		assertTrue(subset2.get(2));
		assertTrue(subset2.get(3));
		assertFalse(subset2.get(4));
		assertTrue(subset2.get(5));
		assertFalse(subset2.get(6));
	}

	/**
	 * Tests for clear.
	 */
	@Test
	public void testClear() {
		final CollisionMap map = new CollisionMap(4, 4);
		map.set(0, 0);
		map.set(3, 0);
		map.set(0, 3);
		map.set(3, 3);
		assertTrue(map.collides(0, 0, 4, 4));
		map.clear();
		assertFalse(map.collides(0, 0, 4, 4));
	}

	/**
	 * Tests for setCollideRectangle2D2x1.
	 */
	@Test
	public void testsetCollideRectangle2D2x1() {
		final CollisionMap map = new CollisionMap(4, 4);
		Rectangle2D shape = new Rectangle2D.Double(1.0, 2.0, 2.0, 1.0);
		map.set(shape);
		assertFalse(map.get(0, 0));
		assertFalse(map.get(0, 1));
		assertFalse(map.get(0, 2));
		assertFalse(map.get(0, 3));

		assertFalse(map.get(1, 0));
		assertFalse(map.get(1, 1));
		assertTrue(map.get(1, 2));
		assertFalse(map.get(1, 3));

		assertFalse(map.get(2, 0));
		assertFalse(map.get(2, 1));
		assertTrue(map.get(2, 2));
		assertFalse(map.get(2, 3));

		assertFalse(map.get(3, 0));
		assertFalse(map.get(3, 1));
		assertFalse(map.get(3, 2));
		assertFalse(map.get(3, 3));
	}

	/**
	 * Tests for setCollideRectangle2D2x3.
	 */
	@Test
	public void testsetCollideRectangle2D2x3() {
		final CollisionMap map = new CollisionMap(4, 4);
		Rectangle2D shape = new Rectangle2D.Double(1.0, 0.0, 2.0, 3.0);
		map.set(shape);
		assertFalse(map.get(0, 0));
		assertFalse(map.get(0, 1));
		assertFalse(map.get(0, 2));
		assertFalse(map.get(0, 3));

		assertTrue(map.get(1, 0));
		assertTrue(map.get(1, 1));
		assertTrue(map.get(1, 2));
		assertFalse(map.get(1, 3));

		assertTrue(map.get(2, 0));
		assertTrue(map.get(2, 1));
		assertTrue(map.get(2, 2));
		assertFalse(map.get(2, 3));

		assertFalse(map.get(3, 0));
		assertFalse(map.get(3, 1));
		assertFalse(map.get(3, 2));
		assertFalse(map.get(3, 3));
	}


	/**
	 * Tests for createLayerDefintion.
	 */
	@Test
	public void testCreateLayerDefintion() {
		LayerDefinition layer;
		final int width = 3;
		final int height = 4;
		layer = new LayerDefinition(width, height);
		layer.build();
		layer.set(1, 2, 1);
		layer.set(2, 3, 1);

		final CollisionMap map = new CollisionMap(layer);
		assertThat(map.getWidth(), is(3));
		assertThat(map.getHeight(), is(4));
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				final boolean isSet = layer.getTileAt(x, y) != 0;
				assertThat(x + ";" + y, map.get(x, y), is(isSet));
			}
		}

	}

	/**
	 * Tests for collidesEntity.
	 */
	@Test
	public void testCollidesEntity() {
		final CollisionMap map = new CollisionMap(4, 4);
		map.set(1, 1);
		Player bob = new Player();
		PlayerTestHelper.generatePlayerRPClasses();
		StendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
		games.stendhal.server.entity.player.Player serverbob = games.stendhal.server.entity.player.Player
				.createZeroLevelPlayer("bob", null);
		serverbob.setPosition(0, 0);
		bob.initialize(serverbob);

		assertThat(bob.getWidth(), is(1.0));
		assertThat(bob.getWidth(), is(1.0));
		assertThat(bob.getX(), is(0.0));
		assertThat(bob.getY(), is(0.0));

		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(0, 1);
		bob.initialize(serverbob);
		assertThat(bob.getX(), is(0.0));
		assertThat(bob.getY(), is(1.0));

		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(0, 2);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(0, 3);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(1, 0);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(1, 1);
		bob.initialize(serverbob);
		assertTrue(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(1, 2);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(1, 3);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(2, 0);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(2, 1);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(2, 2);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(2, 3);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(3, 0);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(3, 1);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(3, 2);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
		serverbob.setPosition(3, 3);
		bob.initialize(serverbob);
		assertFalse(map.collides((int) bob.getX(), (int) bob.getY(), (int) bob
				.getWidth(), (int) bob.getHeight()));
	}

}
