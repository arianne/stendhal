/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.office;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.maps.MockStendlRPWorld;

public class TestStorableEntityListTest {

	private final class EntityExtension extends Entity {
		@Override
		public String getTitle() {
			return "testentity";
		}

		@Override
		public void onRemoved(final StendhalRPZone zone) {

			removecounter++;
			super.onRemoved(zone);
		}
	}

	private int removecounter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for removeByName.
	 */
	@Test
	public void testRemoveByName() {
		final StendhalRPZone zone = new StendhalRPZone("name") {
			@Override
			public void storeToDatabase() {
				// do nothing
			}
		};
		final StorableEntityList<Entity> storelist = new StorableEntityList<Entity>(zone, Entity.class) {

			@Override
			protected String getName(final Entity entity) {
				return entity.getTitle();
			}
		};
		final Entity ent = new EntityExtension();
		final Entity ent2 = new EntityExtension();
		assertTrue(storelist.getList().isEmpty());
		storelist.add(ent);
		assertFalse(storelist.getList().isEmpty());
		assertThat(storelist.getList().size(), is(1));
		storelist.add(ent2);
		assertThat(storelist.getList().size(), is(2));
		storelist.removeByName(ent.getTitle());
		assertThat(removecounter, is(2));
		assertThat(storelist.getList().size(), is(0));

		assertThat("removebyname() removes all instances", storelist.getList().size(), is(0));
	}

}
