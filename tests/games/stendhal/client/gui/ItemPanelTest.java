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

import javax.swing.JPanel;

import org.junit.Test;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.factory.EntityFactory;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;
import games.stendhal.client.gui.styled.cursor.CursorRepository;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import marauroa.common.game.RPObject;
import utilities.RPClass.ItemTestHelper;

public class ItemPanelTest {
	private static final CursorRepository cursors = new CursorRepository();

	/**
	 * Test getting the cursor.
	 */
	@Test
	public void testCursors() {
		ItemPanel panel = new ItemPanel("blah", null);
		// For comparing with the default cursor
		JPanel dummy = new JPanel();

		assertEquals("Default cursor", dummy.getCursor(), panel.getCursor());

		// Check adding an item to the slot
		RPObject obj = ItemTestHelper.createItem("wedding ring");
		IEntity item = EntityFactory.createEntity(obj);
		/*
		 * Set a dummy owner for the panel to simulate something not owned by
		 * the User
		 */
		panel.setParent(item);
		panel.setEntity(item);
		/*
		 * Comparing the string representations because the cursors come from
		 * different repositories, and would compare unequal otherwise
		 */
		assertEquals("Pick up cursor",
				cursors.get(StendhalCursor.ITEM_PICK_UP_FROM_SLOT).toString(),
				panel.getCursor().toString());

		// Repeat the checks with an user owned slot
		User user = new User();
		panel.setParent(user);
		/*
		 * Comparing empty slots first because normally the parent of the slot
		 * does not change from User to a non-user or vice versa, so ItemPanel
		 * does not handle the situation.
		 */
		panel.setEntity(null);
		assertEquals("Default cursor", dummy.getCursor(), panel.getCursor());
		panel.setEntity(item);
		// Get the cursor from the view
		EntityView<?> view = EntityViewFactory.create(item);
		assertEquals("Cursor from the entity view",
				cursors.get(view.getCursor()).toString(),
				panel.getCursor().toString());
	}
}
