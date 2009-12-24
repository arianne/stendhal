package games.stendhal.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PerceptionDispatcherTest {

	private final String DISPATCHED = "dispatched";;

	private final class ObjectChangeperception implements IPerceptionListener {
		boolean cleared;
		boolean onExceptionCalled;
		
		private final String dispatched;
	
		private int byteparam;

		private ObjectChangeperception(final String dispatched) {
			this.dispatched = dispatched;
		}

		public boolean onAdded(final RPObject newObject) {
			newObject.put(dispatched, "");
			return false;
		}

		public boolean onClear() {
			cleared = true;
			return false;
		}

		public boolean onDeleted(final RPObject deletedObject) {
			deletedObject.put(dispatched, "");
			return false;
		}

		public void onException(final Exception exception,
				final MessageS2CPerception perception) {
			onExceptionCalled = true;
		}

		public boolean onModifiedAdded(final RPObject baseObject, final RPObject changes) {
			baseObject.put(dispatched, "");
			return false;
		}

		public boolean onModifiedDeleted(final RPObject baseObject, final RPObject changes) {
			baseObject.put(dispatched, "");
			return false;
		}

		public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
			added.put(dispatched, "");
			return false;
		}

		public void onPerceptionBegin(final byte type, final int timestamp) {
			this.byteparam = 5;

		}

		public void onPerceptionEnd(final byte type, final int timestamp) {
			this.byteparam = 6;

		}

		public void onSynced() {
			this.byteparam = 7;

		}

		public void onUnsynced() {
			this.byteparam = 8;

		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for onAdded.
	 */
	@Test
	public void testOnAdded() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final IPerceptionListener changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		final RPObject object = new RPObject();
		assert (object.has(DISPATCHED));
		dispatch.onAdded(object);
		assertTrue(object.has(DISPATCHED));
	}

	/**
	 * Tests for onClear.
	 */
	@Test
	public void testOnClear() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final ObjectChangeperception changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		assertFalse(changeListener.cleared);
		dispatch.onClear();
		assertTrue(changeListener.cleared);
	}

	/**
	 * Tests for onDeleted.
	 */
	@Test
	public void testOnDeleted() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final IPerceptionListener changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		final RPObject object = new RPObject();
		assert (object.has(DISPATCHED));
		dispatch.onDeleted(object);
		assertTrue(object.has(DISPATCHED));
	}

	/**
	 * Tests for onException.
	 */
	@Test
	public void testOnException() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final ObjectChangeperception changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		assertFalse(changeListener.onExceptionCalled);
		dispatch.onException(null, null);
		assertTrue(changeListener.onExceptionCalled);
	}

	/**
	 * Tests for onModifiedAdded.
	 */
	@Test
	public void testOnModifiedAdded() {

		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final IPerceptionListener changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		final RPObject object = new RPObject();
		assert (object.has(DISPATCHED));
		dispatch.onModifiedAdded(object, null);
		assertTrue(object.has(DISPATCHED));

	}

	/**
	 * Tests for onModifiedDeleted.
	 */
	@Test
	public void testOnModifiedDeleted() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final IPerceptionListener changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		final RPObject object = new RPObject();
		assert (object.has(DISPATCHED));
		dispatch.onModifiedDeleted(object, null);
		assertTrue(object.has(DISPATCHED));
	}

	/**
	 * Tests for onMyRPObject.
	 */
	@Test
	public void testOnMyRPObject() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final IPerceptionListener changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		final RPObject object = new RPObject();
		assert (object.has(DISPATCHED));
		dispatch.onMyRPObject(object, null);
		assertTrue(object.has(DISPATCHED));

	}

	/**
	 * Tests for onPerceptionBegin.
	 */
	@Test
	public void testOnPerceptionBegin() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final ObjectChangeperception changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		assertEquals(0, changeListener.byteparam);
		dispatch.onPerceptionBegin((byte) 0, 0);
		assertEquals(5, changeListener.byteparam);

	}

	/**
	 * Tests for onPerceptionEnd.
	 */
	@Test
	public void testOnPerceptionEnd() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final ObjectChangeperception changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		assertEquals(0, changeListener.byteparam);
		dispatch.onPerceptionEnd((byte) 0, 0);
		assertEquals(6, changeListener.byteparam);

	}

	/**
	 * Tests for onSynced.
	 */
	@Test
	public void testOnSynced() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final ObjectChangeperception changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		assertEquals(0, changeListener.byteparam);
		dispatch.onSynced();
		assertEquals(7, changeListener.byteparam);

	}

	/**
	 * Tests for onUnsynced.
	 */
	@Test
	public void testOnUnsynced() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final ObjectChangeperception changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		assertEquals(0, changeListener.byteparam);
		dispatch.onUnsynced();
		assertEquals(8, changeListener.byteparam);

	}

	/**
	 * Tests for register.
	 */
	@Test
	public void testRegister() {
		final PerceptionDispatcher dispatch = new PerceptionDispatcher();
		final ObjectChangeperception changeListener = new ObjectChangeperception(
				DISPATCHED);
		dispatch.register(changeListener);

		assertEquals(0, changeListener.byteparam);
		dispatch.onUnsynced();
		assertEquals(8, changeListener.byteparam);

		changeListener.byteparam = 0;
		dispatch.unregister(changeListener);

		assertEquals(0, changeListener.byteparam);
		dispatch.onUnsynced();
		assertEquals(0, changeListener.byteparam);

	}

}
