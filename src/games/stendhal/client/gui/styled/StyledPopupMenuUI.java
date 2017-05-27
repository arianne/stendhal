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
package games.stendhal.client.gui.styled;

import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

/**
 * PopupMenuUI implementation for drawing styled menus.
 */
public class StyledPopupMenuUI extends BasicPopupMenuUI {
	private static StyledPopupMenuUI instance;

	private final Style style;

	// Required by UIManager, not necessarily called from EDT
	public static synchronized ComponentUI createUI(JComponent popup) {
		// Menu UI can be shared
		if (instance == null) {
			instance = new StyledPopupMenuUI(StyleUtil.getStyle());
		}

		return instance;
	}

	/**
	 * Create a new StyledPopupMenuUI.
	 *
	 * @param style The pixmap style for drawing the menu
	 */
	public StyledPopupMenuUI(Style style) {
		this.style = style;
	}

	@Override
	public void paint(Graphics g, JComponent menu) {
		StyleUtil.fillBackground(style, g, 0, 0, menu.getWidth(), menu.getHeight());
	}

	@Override
	public Popup getPopup(JPopupMenu menu, int x, int y) {
		Popup popup = super.getPopup(menu, x, y);
		/*
		 * The menu should now have a parent, which is probably a JPanel, In
		 * which case its borders need to be deleted.
		 */
		Container parent = menu.getParent();
		if (parent instanceof JComponent) {
			((JComponent) parent).setBorder(null);
		}

		return popup;
	}

	@Override
	public void installUI(JComponent component) {
		super.installUI(component);
		component.setBorder(style.getBorder());
	}
}
