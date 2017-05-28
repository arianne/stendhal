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
package games.stendhal.client.gui.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for StatusIconPanel
 */
public class StatusIconPanelTest {
	/**
	 * Check that all status icons start invisible.
	 */
	@Test
	public void testInitialStatus() {
		StatusIconPanel iconPanel = new StatusIconPanel();

		assertFalse(iconPanel.away.isVisible());
		assertFalse(iconPanel.grumpy.isVisible());
		assertFalse(iconPanel.eating.isVisible());
		assertFalse(iconPanel.choking.isVisible());
	}

	/**
	 * Check displaying and hiding the away indicator.
	 */
	@Test
	public void testAway() {
		StatusIconPanel iconPanel = new StatusIconPanel();

		iconPanel.setAway("excuse to be away");
		assertTrue(iconPanel.away.isVisible());
		assertEquals("<html>You are away with the message:<br><b>excuse to be away", iconPanel.away.getToolTipText());
		iconPanel.setAway(null);
		assertFalse(iconPanel.away.isVisible());
	}

	/**
	 * Check displaying and hiding the grumpy indicator.
	 */
	@Test
	public void testGrumpy() {
		StatusIconPanel iconPanel = new StatusIconPanel();

		iconPanel.setGrumpy("reason to be grumpy");
		assertTrue(iconPanel.grumpy.isVisible());
		assertEquals("<html>You are grumpy with the message:<br><b>reason to be grumpy", iconPanel.grumpy.getToolTipText());
		iconPanel.setGrumpy(null);
		assertFalse(iconPanel.grumpy.isVisible());
	}

	/**
	 * Check displaying and hiding the eating indicator. No interaction with
	 * choking icon.
	 */
	@Test
	public void testEatingBasic() {
		StatusIconPanel iconPanel = new StatusIconPanel();

		iconPanel.setEating(true);
		assertTrue(iconPanel.eating.isVisible());
		iconPanel.setEating(false);
		assertFalse(iconPanel.eating.isVisible());
	}

	/**
	 * Check displaying and hiding the choking indicator. No interaction with
	 * eating icon.
	 */
	@Test
	public void testChokingBasic() {
		StatusIconPanel iconPanel = new StatusIconPanel();

		iconPanel.setChoking(true);
		assertTrue(iconPanel.choking.isVisible());
		iconPanel.setChoking(false);
		assertFalse(iconPanel.choking.isVisible());
	}

	/**
	 * Test the interaction of eating and choking icons. They should not appear
	 * at the same time.
	 */
	@Test
	public void testEatingChoking() {
		StatusIconPanel iconPanel = new StatusIconPanel();

		// The player ate a bit too much
		iconPanel.setEating(true);
		iconPanel.setChoking(true);
		assertFalse("Starting choking should hide the eating icon", iconPanel.eating.isVisible());
		assertTrue("Choking should be visible if set after eating", iconPanel.choking.isVisible());

		// Simulate zone change when choking. Choking property can arrive first
		// (the other possibility is covered by the previous checks)
		iconPanel.setEating(true);
		assertFalse("Eating icon should not be made visible if the player is choking", iconPanel.eating.isVisible());
		assertTrue("Choking should be still visible", iconPanel.choking.isVisible());

		// Just a sanity check. Allow eating icon to become visible if choking
		// is removed.
		iconPanel.setChoking(false);
		assertFalse(iconPanel.choking.isVisible());
		iconPanel.setEating(true);
		assertTrue(iconPanel.eating.isVisible());
	}
}
