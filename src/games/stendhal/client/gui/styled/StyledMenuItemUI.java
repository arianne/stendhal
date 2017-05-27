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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * MenuItemUI for drawing menu items with style.
 */
public class StyledMenuItemUI extends BasicMenuItemUI {
	private final Style style;

	// Required by UIManager
	public static ComponentUI createUI(JComponent menuItem) {
		// BasicMenuItemUI can not be shared
		return new StyledMenuItemUI(StyleUtil.getStyle());
	}

	/**
	 * Create a new StyledMenuItemUI.
	 *
	 * @param style pixmap style
	 */
	public StyledMenuItemUI(Style style) {
		this.style = style;
		selectionBackground = style.getHighLightColor();
		selectionForeground = style.getShadowColor();
	}

	@Override
	public void installUI(JComponent component) {
		super.installUI(component);

		component.setBorder(style.getBorder());
		component.setOpaque(false);
		component.setFont(style.getFont());
		component.setForeground(style.getForeground());
	}
}
