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
package games.stendhal.client.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.util.UserInterfaceTestHelper;

public class InternalWindowTest {
	@BeforeClass
	public static void init() {
		UserInterfaceTestHelper.initUserInterface();
	}

	/**
	 * Test that setCloseable works properly, and that the window defaults to
	 * being closeable.
	 */
	@Test
	public void testCloseable() {
		InternalWindow window = new InternalWindow("Test window");

		assertTrue("Closeable by default", window.closeButton.isVisible());
		// Check disabling
		window.setCloseable(false);
		assertFalse("Should not be closeable", window.closeButton.isVisible());
		// Check restoring
		window.setCloseable(true);
		assertTrue("Should be closeable", window.closeButton.isVisible());
	}

	/**
	 * Test that setHideOnClose works correctly
	 */
	@Test
	public void testSetHideOnClose() {
		// A container where we can test the appearing and disappearing of the
		// window
		JComponent container = new JPanel();
		InternalWindow window = new InternalWindow("Test window");

		assertEquals("Sanity check", container.getComponentCount(), 0);
		// Check default behavior
		container.add(window);
		assertEquals("Added the window", container.getComponentCount(), 1);
		window.closeButton.doClick();
		assertEquals("Window removed", container.getComponentCount(), 0);

		// check preserving behavior
		container.add(window);
		assertEquals("Added the window", container.getComponentCount(), 1);
		window.setHideOnClose(true);
		window.closeButton.doClick();
		assertEquals("Window preserved", container.getComponentCount(), 1);
		assertFalse("Window is hidden", window.isVisible());

		// Check restoring the default behavior
		window.setHideOnClose(false);
		window.closeButton.doClick();
		assertEquals("Window removed", container.getComponentCount(), 0);
	}

	/**
	 * Test that setMinimizable works properly, and that the window defaults to
	 * being minimizable.
	 */
	@Test
	public void testMinimizable() {
		InternalWindow window = new InternalWindow("Test window");

		assertTrue("Minimizable by default", window.minimizeButton.isVisible());
		// Check disabling
		window.setMinimizable(false);
		assertFalse("Should not be minimizable", window.minimizeButton.isVisible());
		// Check restoring
		window.setMinimizable(true);
		assertTrue("Should be minimizable", window.minimizeButton.isVisible());
	}

	/**
	 * Test minimizing the window using setMinimized and clicking the minimize
	 * button.
	 */
	@Test
	public void testMinimizing() {
		InternalWindow window = new InternalWindow("Test window");
		JLabel content = new JLabel("Hello");
		window.setContent(content);

		assertFalse("Default to visible", window.isMinimized());
		// check the window manager interface
		window.setMinimized(true);
		assertTrue("Should be minimized", window.isMinimized());
		window.setMinimized(false);
		assertFalse("Should be restored", window.isMinimized());

		// check the button use
		window.minimizeButton.doClick();
		assertTrue("Should be minimized", window.isMinimized());
		window.minimizeButton.doClick();
		assertFalse("Should be restored", window.isMinimized());
	}

	/**
	 * Test setting the title text.
	 */
	@Test
	public void testSetTitle() {
		InternalWindow window = new InternalWindow("Test window");
		assertEquals("Initial title text", "<html>Test&nbsp;window</html>", window.titleLabel.getText());
		window.setTitle("Changed");
		// html ellipsis workaround
		assertEquals("Changed title text", "<html>Changed</html>", window.titleLabel.getText());
		// which should not be used when there are spaces in the title
		window.setTitle("Changed again");
		assertEquals("Changed title text", "<html>Changed&nbsp;again</html>", window.titleLabel.getText());
	}
}
