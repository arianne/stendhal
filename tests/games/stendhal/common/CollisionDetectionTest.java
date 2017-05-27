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
package games.stendhal.common;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

/**
 * Tests for CollisionDetection
 */
public class CollisionDetectionTest {
	/**
	 * Tests getWidth and getHeight.
	 */
	@Test
	public void testGetDimensions() {
		CollisionDetection map = new CollisionDetection();
		map.init(3, 4);
		assertThat(map.getWidth(), is(3));
		assertThat(map.getHeight(), is(4));
		map = new CollisionDetection();
		map.init(42, 1);
		assertThat(map.getWidth(), is(42));
		assertThat(map.getHeight(), is(1));
	}

	/**
	 * Test simple collisions
	 */
	@Test
	public void testCollides() {
		CollisionDetection map = new CollisionDetection();
		map.init(3, 3);
		map.setCollide(0, 1);
		map.setCollide(1, 0);
		map.setCollide(1, 2);
		map.setCollide(2, 1);
		assertThat(map.collides(0, 0), is(false));
		assertThat(map.collides(1, 0), is(true));
		assertThat(map.collides(2, 0), is(false));
		assertThat(map.collides(0, 1), is(true));
		assertThat(map.collides(1, 1), is(false));
		assertThat(map.collides(2, 1), is(true));
		assertThat(map.collides(0, 2), is(false));
		assertThat(map.collides(1, 2), is(true));
		assertThat(map.collides(2, 2), is(false));
	}
	/**
	 * Check that area outside always is collision
	 */
	@Test
	public void testCollidesOutside() {
		CollisionDetection map = new CollisionDetection();
		map.init(1, 1);
		assertThat(map.collides(-1, -1), is(true));
		assertThat(map.collides(-1, 0), is(true));
		assertThat(map.collides(-1, 1), is(true));
		assertThat(map.collides(0, -1), is(true));
		assertThat(map.collides(0, 0), is(false));
		assertThat(map.collides(0, 1), is(true));
		assertThat(map.collides(1, -1), is(true));
		assertThat(map.collides(1, 0), is(true));
		assertThat(map.collides(1, 1), is(true));
	}

	/**
	 * Test collision for a rectangle. Same map as in previous test
	 */
	@Test
	public void testCollidesShape() {
		CollisionDetection map = new CollisionDetection();
		map.init(3, 3);
		map.setCollide(0, 1);
		map.setCollide(1, 0);
		map.setCollide(1, 2);
		map.setCollide(2, 1);

		Rectangle r = new Rectangle(1, 1);

		assertThat(map.collides(r), is(false));
		r.x = 1;
		assertThat(map.collides(r), is(true));
		r.x = 2;
		assertThat(map.collides(r), is(false));
		r.x = 0;
		r.y = 1;
		assertThat(map.collides(r), is(true));
		r.x = 1;
		assertThat(map.collides(r), is(false));
		r.x = 2;
		assertThat(map.collides(r), is(true));
		r.x = 0;
		r.y = 2;
		assertThat(map.collides(r), is(false));
		r.x = 1;
		assertThat(map.collides(r), is(true));
		r.x = 2;
		assertThat(map.collides(r), is(false));
	}

	/**
	 * Test collision for a rectangle. Similar to testCollidesShape(), but
	 * checks a 1x1 rectangle placed at floating point coordinates
	 */
	@Test
	public void testCollidesShapeFloat() {
		CollisionDetection map = new CollisionDetection();
		map.init(3, 3);
		map.setCollide(0, 1);
		map.setCollide(1, 0);
		map.setCollide(2, 1);

		Rectangle2D r = new Rectangle2D.Double(0, 0, 1, 1);

		assertThat(map.collides(r), is(false));
		r.setRect(0.1, 0, 1, 1);
		assertThat(map.collides(r), is(true));
		r.setRect(0.9, 0, 1, 1);
		assertThat(map.collides(r), is(true));
		r.setRect(1.1, 0, 1, 1);
		assertThat(map.collides(r), is(true));
		r.setRect(2.1, 0, 1, 1);
		assertThat(map.collides(r), is(true));
		r.setRect(2.0, 0, 1, 1);
		assertThat(map.collides(r), is(false));

		r.setRect(0, 0.1, 1, 1);
		assertThat(map.collides(r), is(true));
		r.setRect(0, 0.9, 1, 1);
		assertThat(map.collides(r), is(true));
		r.setRect(0, 0, 1, 1);
		assertThat(map.collides(r), is(false));
		r.setRect(0, 1.1, 1, 1);
		assertThat(map.collides(r), is(true));
		r.setRect(0, 1.9, 1, 1);
		assertThat(map.collides(r), is(true));
		r.setRect(0, 2, 1, 1);
		assertThat(map.collides(r), is(false));
		r.setRect(0, 2.1, 1, 1);
		assertThat(map.collides(r), is(true));

		r.setRect(0.1, 2, 1, 1);
		assertThat(map.collides(r), is(false));
		r.setRect(0.9, 2, 1, 1);
		assertThat(map.collides(r), is(false));
		r.setRect(1.1, 2, 1, 1);
		assertThat(map.collides(r), is(false));
		r.setRect(1.9, 2, 1, 1);
		assertThat(map.collides(r), is(false));
		r.setRect(2.1, 2, 1, 1);
		assertThat(map.collides(r), is(true));

		r.setRect(1.9, 2, 1, 1);
		assertThat(map.collides(r), is(false));
		r.setRect(1.9, 1.9, 1, 1);
		assertThat(map.collides(r), is(true));
	}

	/**
	 * Test collision for a rectangle.
	 */
	@Test
	public void testCollidesShapeLarge() {
		CollisionDetection map = new CollisionDetection();
		map.init(3, 3);
		map.setCollide(0, 1);
		map.setCollide(1, 0);

		Rectangle r = new Rectangle(2, 2);

		assertThat(map.collides(r), is(true));
		r.x = 1;
		assertThat(map.collides(r), is(true));
		r.x = 2;
		assertThat(map.collides(r), is(true));
		r.x = 0;
		r.y = 1;
		assertThat(map.collides(r), is(true));
		r.x = 1;
		assertThat(map.collides(r), is(false));
		r.x = 2;
		assertThat(map.collides(r), is(true));
		r.x = 0;
		r.y = 2;
		assertThat(map.collides(r), is(true));
		r.x = 1;
		assertThat(map.collides(r), is(true));
		r.x = 2;
		assertThat(map.collides(r), is(true));
	}

	/**
	 * Test that positions outside the map count for leaving, and those inside
	 * do not.
	 */
	@Test
	public void testLeavesZone() {
		CollisionDetection map = new CollisionDetection();
		map.init(3, 3);
		Rectangle r = new Rectangle(1, 1);
		for (r.x = -1; r.x <= 3; r.x++) {
			for (r.y = -1; r.y <= 3; r.y++) {
				assertThat("Leaves zone at " + r.x + "," + r.y,
						map.leavesZone(r), is(r.x < 0 || r.y < 0 || r.x > 2 || r.y > 2));
			}
		}
	}

	/**
	 * Test that positions outside the map count for leaving, and those inside
	 * do not. Large entity
	 */
	@Test
	public void testLeavesZoneLarge() {
		CollisionDetection map = new CollisionDetection();
		map.init(3, 3);
		Rectangle r = new Rectangle(2, 2);
		for (r.x = -1; r.x <= 3; r.x++) {
			for (r.y = -1; r.y <= 3; r.y++) {
				assertThat("Leaves zone at " + r.x + "," + r.y,
						map.leavesZone(r), is(r.x < 0 || r.y < 0 || r.x > 1 || r.y > 1));
			}
		}
	}
}
