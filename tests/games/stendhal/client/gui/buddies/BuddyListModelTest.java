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
package games.stendhal.client.gui.buddies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the BuddyListModel
 */
public class BuddyListModelTest implements ListDataListener {
	private boolean intervalAddedFlag;
	private boolean intervalRemovedFlag;
	private boolean contentsChangedFlag;
	private int index0, index1;

	/**
	 * Reset the state of the test class.
	 */
	@Before
	public void resetState() {
		intervalAddedFlag = false;
		intervalRemovedFlag = false;
		contentsChangedFlag = false;
		// -1 should fail any tests
		index0 = -1;
		index1 = -1;
	}

	/**
	 * Test adding new buddies to the list.
	 */
	@Test
	public void testAddBuddies() {
		BuddyListModel model = new BuddyListModel();
		assertEquals("Starts empty", 0, model.getSize());
		model.addListDataListener(this);

		// Adding null should not work
		model.setOnline(null, true);
		assertEquals("Adding null buddy should fail", 0, model.getSize());

		// Add a buddy
		model.setOnline("dumdiduu", true);
		assertEquals("Number of buddies", 1, model.getSize());
		assertTrue("intervalAdded() should be called", intervalAddedFlag);
		assertEquals(0, index0);
		assertEquals(0, index1);
		resetState();
		// Add another; should sort before the first
		model.setOnline("daibaduu", true);
		assertEquals("Number of buddies", 2, model.getSize());
		assertTrue("intervalAdded() should be called", intervalAddedFlag);
		assertEquals(0, index0);
		assertEquals(0, index1);
		resetState();

		// Add third; should sort last
		model.setOnline("hubbaduu", true);
		assertEquals("Number of buddies", 3, model.getSize());
		assertTrue("intervalAdded() should be called", intervalAddedFlag);
		assertEquals(2, index0);
		assertEquals(2, index1);
		resetState();

		// Add third; should sort last due to being offline
		model.setOnline("alibaba", false);
		assertEquals("Number of buddies", 4, model.getSize());
		assertTrue("intervalAdded() should be called", intervalAddedFlag);
		assertEquals(3, index0);
		assertEquals(3, index1);
		resetState();

		// Check that there were no spurious calls
		assertFalse("contentsChanged() should not be called", contentsChangedFlag);
		assertFalse("intervalRemoved() should not be called", intervalRemovedFlag);

		// Check the final order
		assertEquals("daibaduu", getBuddy(model, 0).getName());
		assertEquals("dumdiduu", getBuddy(model, 1).getName());
		assertEquals("hubbaduu", getBuddy(model, 2).getName());
		assertEquals("alibaba", getBuddy(model, 3).getName());
	}

	/**
	 * Test changing status of already added buddies.
	 */
	@Test
	public void testChangeState() {
		BuddyListModel model = new BuddyListModel();

		model.addListDataListener(this);
		// add some buddies
		model.setOnline("dumdiduu", true);
		model.setOnline("daibaduu", true);
		model.setOnline("hubbaduu", true);
		model.setOnline("alibaba", false);
		assertEquals("Number of buddies", 4, model.getSize());
		resetState();

		// A dummy change doing nothing should not invoke anything
		model.setOnline("hubbaduu", true);
		assertFalse("intervalAdded() should not be called", intervalAddedFlag);
		assertFalse("intervalRemoved() should not be called", intervalRemovedFlag);
		assertFalse("contentsChanged() should not be called", contentsChangedFlag);

		// dumdiduu should become last if set offline
		model.setOnline("dumdiduu", false);
		assertEquals("Number of buddies should not change", 4, model.getSize());
		assertTrue("contentsChanged() should be called", contentsChangedFlag);
		assertFalse("intervalAdded() should not be called", intervalAddedFlag);
		assertFalse("intervalRemoved() should not be called", intervalRemovedFlag);
		// interval; dumdiduu moved from second to the last
		assertEquals(1, Math.min(index0, index1));
		assertEquals(3, Math.max(index0, index1));
		resetState();

		// alibaba should become first if set online
		model.setOnline("alibaba", true);
		assertEquals("Number of buddies should not change", 4, model.getSize());
		assertTrue("contentsChanged() should be called", contentsChangedFlag);
		assertFalse("intervalAdded() should not be called", intervalAddedFlag);
		assertFalse("intervalRemoved() should not be called", intervalRemovedFlag);
		// interval; alibaba moved from second last to first
		assertEquals(0, Math.min(index0, index1));
		assertEquals(2, Math.max(index0, index1));

		// Check the final order
		assertEquals("alibaba", getBuddy(model, 0).getName());
		assertEquals("daibaduu", getBuddy(model, 1).getName());
		assertEquals("hubbaduu", getBuddy(model, 2).getName());
		assertEquals("dumdiduu", getBuddy(model, 3).getName());
	}

	/**
	 * Test removing buddies that do not exist on the list.
	 */
	@Test
	public void testRemoveNonExistingBuddy() {
		BuddyListModel model = new BuddyListModel();

		model.addListDataListener(this);
		// Try removing nothing from an empty list
		model.removeBuddy(null);
		assertFalse("intervalAdded() should not be called", intervalAddedFlag);
		assertFalse("intervalRemoved() should not be called", intervalRemovedFlag);
		assertFalse("contentsChanged() should not be called", contentsChangedFlag);

		// Try removing dummy from an empty list
		model.removeBuddy("kenny");
		assertFalse("intervalAdded() should not be called", intervalAddedFlag);
		assertFalse("intervalRemoved() should not be called", intervalRemovedFlag);
		assertFalse("contentsChanged() should not be called", contentsChangedFlag);

		// add some buddies
		model.setOnline("dumdiduu", true);
		model.setOnline("daibaduu", true);
		model.setOnline("hubbaduu", true);
		model.setOnline("alibaba", false);
		assertEquals("Number of buddies", 4, model.getSize());
		resetState();

		// killing kenny should still fail
		model.removeBuddy("kenny");
		assertFalse("intervalAdded() should not be called", intervalAddedFlag);
		assertFalse("intervalRemoved() should not be called", intervalRemovedFlag);
		assertFalse("contentsChanged() should not be called", contentsChangedFlag);

		// Check the final order
		assertEquals("daibaduu", getBuddy(model, 0).getName());
		assertEquals("dumdiduu", getBuddy(model, 1).getName());
		assertEquals("hubbaduu", getBuddy(model, 2).getName());
		assertEquals("alibaba", getBuddy(model, 3).getName());
	}

	/**
	 * Test removing buddies that actually are on the list.
	 */
	@Test
	public void testRemoveBuddy() {
		BuddyListModel model = new BuddyListModel();
		model.addListDataListener(this);

		// add some buddies
		model.setOnline("dumdiduu", true);
		model.setOnline("daibaduu", true);
		model.setOnline("hubbaduu", true);
		model.setOnline("alibaba", false);
		resetState();

		// test removing an online buddy
		model.removeBuddy("dumdiduu");
		assertFalse("intervalAdded() should not be called", intervalAddedFlag);
		assertTrue("intervalRemoved() should be called", intervalRemovedFlag);
		assertFalse("contentsChanged() should not be called", contentsChangedFlag);
		assertEquals(1, index0);
		assertEquals(1, index1);
		assertEquals("Number of buddies", 3, model.getSize());
		resetState();

		// test removing an offline buddy
		model.removeBuddy("alibaba");
		assertEquals("intervalAdded() should not be called", false, intervalAddedFlag);
		assertEquals("intervalRemoved() should be called", true, intervalRemovedFlag);
		assertEquals("contentsChanged() should not be called", false, contentsChangedFlag);
		assertEquals(2, index0);
		assertEquals(2, index1);
		assertEquals("Number of buddies", 2, model.getSize());

		// Check the final order
		assertEquals("daibaduu", getBuddy(model, 0).getName());
		assertEquals("hubbaduu", getBuddy(model, 1).getName());
	}

	/**
	 * Get a buddy. The model returns an object. not a Buddy, so this is
	 * just a convenience method doing the casting and type checking.
	 *
	 * @param model BuddyModel
	 * @param index index of the element to be fetched
	 * @return Buddy at index
	 */
	private Buddy getBuddy(BuddyListModel model, int index) {
		Buddy buddy = null;
		Object obj = model.getElementAt(index);
		if (obj instanceof Buddy) {
			buddy = (Buddy) obj;
		} else {
			fail();
		}

		return buddy;
	}

	/*
	 * **** ListDataListener. Update the test class state at model changes ****
	 */
	@Override
	public void contentsChanged(ListDataEvent e) {
		contentsChangedFlag = true;
		index0 = e.getIndex0();
		index1 = e.getIndex1();
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		intervalAddedFlag = true;
		index0 = e.getIndex0();
		index1 = e.getIndex1();
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		intervalRemovedFlag = true;
		index0 = e.getIndex0();
		index1 = e.getIndex1();
	}
}
