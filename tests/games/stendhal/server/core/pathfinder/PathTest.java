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
package games.stendhal.server.core.pathfinder;

import static org.junit.Assert.assertArrayEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.maps.MockStendlRPWorld;

public class PathTest {
	static List<Node> expected;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		expected = new LinkedList<Node>();
		expected.add(new Node(0, 0));
		expected.add(new Node(1, 0));
		expected.add(new Node(2, 0));
		expected.add(new Node(3, 0));
		expected.add(new Node(4, 0));
		expected.add(new Node(5, 0));
		expected.add(new Node(6, 0));
		expected.add(new Node(6, 1));
		expected.add(new Node(6, 2));
		expected.add(new Node(6, 3));
		expected.add(new Node(6, 4));
		expected.add(new Node(6, 5));
		expected.add(new Node(6, 6));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	/**
	 * Tests for searchPathEntityIntInt.
	 */
	@Test
	public void testSearchPathEntityIntInt() {
		final Entity entity = new Entity() {
			// just to create an instance
		};
		final StendhalRPZone zone = new StendhalRPZone("test", 10, 10);
		zone.add(entity);
		assertArrayEquals(expected.toArray(), Path.searchPath(entity, 6, 6).toArray());
	}

	/**
	 * Test the entity free path finder.
	 */
	@Test
	public void testSearchPathSimpleIntInt() {
		final StendhalRPZone zone = new StendhalRPZone("test", 10, 10);

		assertArrayEquals(expected.toArray(), Path.searchPath(zone, 0, 0, 6, 6, 20).toArray());
	}
}
