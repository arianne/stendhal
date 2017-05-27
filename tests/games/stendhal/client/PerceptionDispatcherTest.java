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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

public class PerceptionDispatcherTest {

	private static final String DISPATCHED = "dispatched";

	private static final class ObjectChangeperception implements IPerceptionListener {
		boolean cleared;
		boolean onExceptionCalled;

		private final String dispatched;

		private int byteparam;

		private ObjectChangeperception(final String dispatched) {
			this.dispatched = dispatched;
		}

		@Override
		public boolean onAdded(final RPObject newObject) {
			newObject.put(dispatched, "");
			return false;
		}

		@Override
		public boolean onClear() {
			cleared = true;
			return false;
		}

		@Override
		public boolean onDeleted(final RPObject deletedObject) {
			deletedObject.put(dispatched, "");
			return false;
		}

		@Override
		public void onException(final Exception exception,
				final MessageS2CPerception perception) {
			onExceptionCalled = true;
		}

		@Override
		public boolean onModifiedAdded(final RPObject baseObject, final RPObject changes) {
			baseObject.put(dispatched, "");
			return false;
		}

		@Override
		public boolean onModifiedDeleted(final RPObject baseObject, final RPObject changes) {
			baseObject.put(dispatched, "");
			return false;
		}

		@Override
		public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
			added.put(dispatched, "");
			return false;
		}

		@Override
		public void onPerceptionBegin(final byte type, final int timestamp) {
			this.byteparam = 5;

		}

		@Override
		public void onPerceptionEnd(final byte type, final int timestamp) {
			this.byteparam = 6;

		}

		@Override
		public void onSynced() {
			this.byteparam = 7;

		}

		@Override
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
		assertFalse(object.has(DISPATCHED));
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
		assertFalse(object.has(DISPATCHED));
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
		assertFalse(object.has(DISPATCHED));
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
		assertFalse(object.has(DISPATCHED));
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
		assertFalse(object.has(DISPATCHED));
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
