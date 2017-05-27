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
package games.stendhal.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import marauroa.common.game.RPObject;

public class PerceptionToObjectTest {

	private class ObjectChangeListenerAdapter implements
			ObjectChangeListener {


		private ObjectChangeListenerAdapter() {

		}

		@Override
		public void deleted() {

		}

		@Override
		public void modifiedAdded(final RPObject changes) {

		}

		@Override
		public void modifiedDeleted(final RPObject changes) {

		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	 * Tests for onClear2Listeners1Object.
	 */
	@Test
	public final void testOnClear2Listeners1Object() {
		final PerceptionToObject pto = new PerceptionToObject();
		final RPObject testvalues = new RPObject();

		final ObjectChangeListener listener1 = new ObjectChangeListenerAdapter() {
			@Override
			public void deleted() {
				testvalues.put("listener1", "");

			}
		};
		final ObjectChangeListener listener2 = new ObjectChangeListenerAdapter() {
			@Override
			public void deleted() {
				testvalues.put("listener2", "");
			}
		};

		RPObject observed = new RPObject();
		observed.setID(new RPObject.ID(1, "zone"));
		assertFalse(testvalues.has("listener1"));
		assertFalse(testvalues.has("listener2"));

		pto.register(observed, listener1);
		pto.register(observed, listener2);

		assertTrue(pto.map.get(observed.getID()).contains(listener2));
		assertTrue(pto.map.get(observed.getID()).contains(listener1));

		pto.onClear();


		assertTrue(testvalues.has("listener2"));
		assertTrue(testvalues.has("listener1"));
	}

	// TODO: remove listener from pto must remove all references



	/**
	 * Tests for onClear.
	 */
	@Test
	public final void testOnClear() {
		final String attribute = "clear";
		final PerceptionToObject pto = new PerceptionToObject();
		final RPObject object = new RPObject();
		object.setID(new RPObject.ID(1, "zone"));
		final ObjectChangeListener listener = new ObjectChangeListenerAdapter() {

			@Override
			public void deleted() {
				object.put(attribute, "");
			}

		};
		final RPObject object2 = new RPObject();
		object2.setID(new RPObject.ID(2, "zone"));

		pto.register(object, listener);
		pto.register(object2, listener);
		assertTrue("object is contained in map after register of listener", pto.map.containsKey(object.getID()));

		assertFalse("deleted not yet called", object.has(attribute));

		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));

		pto.onClear();

		assertTrue(object.has(attribute));
		assertFalse(object2.has(attribute));

		assertFalse(pto.map.containsKey(object.getID()));
		assertFalse(pto.map.containsKey(object2.getID()));
	}

	/**
	 * Tests for onDeleted.
	 */
	@Test
	public final void testOnDeleted() {
		final String attribute = "deleted";
		final PerceptionToObject pto = new PerceptionToObject();
		final RPObject object = new RPObject();
		object.setID(new RPObject.ID(1, "zone"));
		final ObjectChangeListener listener = new ObjectChangeListenerAdapter() {

			@Override
			public void deleted() {
				object.put(attribute, "");
			}

		};
		final RPObject object2 = new RPObject();
		object2.setID(new RPObject.ID(2, "zone"));

		pto.register(object, listener);
		pto.register(object2, listener);
		assertTrue(pto.map.containsKey(object.getID()));

		assertFalse(object.has(attribute));

		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));

		pto.onDeleted(object);

		assertFalse(pto.map.containsKey(object.getID()));
		assertTrue(object.has(attribute));
		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));
	}

	/**
	 * Tests for onException.
	 */
	@Test
	public final void testOnException() {
		final String attribute = "clear";
		final PerceptionToObject pto = new PerceptionToObject();
		final RPObject object = new RPObject();
		object.setID(new RPObject.ID(1, "zone"));
		final ObjectChangeListener listener = new ObjectChangeListenerAdapter() {

			@Override
			public void deleted() {
				object.put(attribute, "");
			}

		};
		final RPObject object2 = new RPObject();
		object2.setID(new RPObject.ID(2, "zone"));

		pto.register(object, listener);
		pto.register(object2, listener);
		assertTrue(pto.map.containsKey(object.getID()));

		assertFalse(object.has(attribute));

		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));

		pto.onException(null, null);

		assertTrue(object.has(attribute));
		assertFalse(pto.map.containsKey(object.getID()));
		assertFalse(pto.map.containsKey(object2.getID()));
	}

	/**
	 * Tests for onModifiedAdded.
	 */
	@Test
	public final void testOnModifiedAdded() {
		final PerceptionToObject pto = new PerceptionToObject();
		final RPObject object = new RPObject();
		object.setID(new RPObject.ID(1, "zone"));
		final String attribute = "modifiedadded";
		final ObjectChangeListener listener = new ObjectChangeListenerAdapter() {

			@Override
			public void modifiedAdded(final RPObject changes) {
				object.add(attribute, 1);
			}
		};

		final RPObject changes = new RPObject();
		changes.setID(new RPObject.ID(object.getID().getObjectID(), object.getID().getZoneID()));
		final RPObject object2 = new RPObject();
		object2.setID(new RPObject.ID(2, "zone"));

		pto.register(object, listener);
		pto.register(object2, listener);
		assertTrue(pto.map.containsKey(object.getID()));
		assertFalse(object.has(attribute));

		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));

		pto.onModifiedAdded(object, changes);

		assertTrue(pto.map.containsKey(object.getID()));
		assertTrue(object.has(attribute));
		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));
	}

	/**
	 * Tests for onModifiedDeleted.
	 */
	@Test
	public final void testOnModifiedDeleted() {
		final PerceptionToObject pto = new PerceptionToObject();
		final RPObject object = new RPObject();
		object.setID(new RPObject.ID(1, "zone"));
		final String attribute = "modifieddeleted";
		final ObjectChangeListener listener = new ObjectChangeListenerAdapter() {

			@Override
			public void modifiedDeleted(final RPObject changes) {
				object.add(attribute, 1);
			}
		};

		final RPObject changes = new RPObject();
		changes.setID(new RPObject.ID(object.getID().getObjectID(), object.getID().getZoneID()));
		final RPObject object2 = new RPObject();
		object2.setID(new RPObject.ID(2, "zone"));

		pto.register(object, listener);
		pto.register(object2, listener);
		assertTrue(pto.map.containsKey(object.getID()));
		assertFalse(object.has(attribute));

		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));

		pto.onModifiedDeleted(object, changes);

		assertTrue(pto.map.containsKey(object.getID()));
		assertTrue(object.has(attribute));
		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));

	}

	/**
	 * Tests for onMyRPObject.
	 */
	@Test
	public final void testOnMyRPObject() {
		final PerceptionToObject pto = new PerceptionToObject();
		final RPObject object = new RPObject();
		object.setID(new RPObject.ID(1, "zone"));
		final String attribute = "modifieddeleted";
		final ObjectChangeListener listener = new ObjectChangeListenerAdapter() {

			@Override
			public void modifiedDeleted(final RPObject changes) {
				object.add(attribute, 1);
			}
		};

		final RPObject changes = new RPObject();
		changes.setID(new RPObject.ID(object.getID().getObjectID(), object.getID().getZoneID()));
		final RPObject object2 = new RPObject();
		object2.setID(new RPObject.ID(2, "zone"));

		pto.register(object, listener);
		pto.register(object2, listener);
		assertTrue(pto.map.containsKey(object.getID()));
		assertFalse(object.has(attribute));

		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));

		pto.onModifiedDeleted(object, changes);

		assertTrue(pto.map.containsKey(object.getID()));
		assertTrue(object.has(attribute));
		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));
	}


	/**
	 * Tests for registerUnregister.
	 */
	@Test
	public final void testRegisterUnregister() {
		final PerceptionToObject pto = new PerceptionToObject();
		final RPObject object = new RPObject();
		object.setID(new RPObject.ID(1, "zone"));
		final ObjectChangeListener listener = new ObjectChangeListenerAdapter();

		final RPObject changes = new RPObject();
		changes.setID(new RPObject.ID(object.getID().getObjectID(), object.getID().getZoneID()));
		final RPObject object2 = new RPObject();
		object2.setID(new RPObject.ID(2, "zone"));
		final RPObject object3 = new RPObject();
		object3.setID(new RPObject.ID(3, "zone"));

		pto.register(object, listener);
		pto.register(object2, listener);
		pto.register(object3, new ObjectChangeListenerAdapter());
		assertTrue(pto.map.containsKey(object.getID()));
		assertTrue(pto.map.containsKey(object2.getID()));
		assertTrue(pto.map.containsKey(object3.getID()));


		pto.unregister(listener);
		assertTrue(pto.map.get(object.getID()).isEmpty());
		assertTrue(pto.map.get(object2.getID()).isEmpty());
		assertFalse(pto.map.get(object3.getID()).isEmpty());

	}

	/**
	 * Tests for absentObject.
	 */
	@Test
	public final void testabsentObject() {
		PerceptionToObject pto = new PerceptionToObject();
		assertTrue(pto.map.isEmpty());
		RPObject object = new RPObject();
		object.setID(new RPObject.ID(1, "ZONE"));

		pto.onClear();
		pto.onDeleted(object);
		pto.onException(null, null);
		pto.onModifiedAdded(object, null);
		pto.onModifiedDeleted(object, null);
		pto.onMyRPObject(object, null);
		pto.onMyRPObject(null, object);


	}

}
