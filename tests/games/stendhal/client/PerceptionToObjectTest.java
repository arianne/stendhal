package games.stendhal.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PerceptionToObjectTest {

	private class ObjectChangeListenerAdapter implements
			ObjectChangeListener {
		

		private ObjectChangeListenerAdapter() {
			
		}

		public void deleted() {
			
		}

		public void modifiedAdded(final RPObject changes) {
			
		}

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

	@Ignore
	@Test
	public final void testOnAdded() {
		fail("Not yet implemented");
	}

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
		assertTrue(pto.map.containsKey(object.getID()));
		
		assertFalse(object.has(attribute));

		assertTrue(pto.map.containsKey(object2.getID()));
		assertFalse(object2.has(attribute));

		pto.onClear();

		assertTrue(object.has(attribute));
		assertFalse(pto.map.containsKey(object.getID()));
		assertFalse(pto.map.containsKey(object2.getID()));
	}

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
			@Override
			public void modifiedAdded(final RPObject arg0) {
			
				
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
		assertFalse(pto.map.containsKey(object.getID()));
		assertFalse(pto.map.containsKey(object2.getID()));
		assertTrue(pto.map.containsKey(object3.getID()));
		
	}

}
