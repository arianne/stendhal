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

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

/**
 * PanelUI implementation for drawing in pixmap styles.
 */
public class StyledPanelUI extends PanelUI {
	private static StyledPanelUI instance;

	private final Style style;

	// Required by UIManager, not necessarily called from EDT
	public static synchronized ComponentUI createUI(JComponent panel) {
		// Panel UI instances can be shared
		if (instance == null) {
			instance = new StyledPanelUI(StyleUtil.getStyle());
		}

		return instance;
	}

	/**
	 * Create a new pixmap style.
	 *
	 * @param style {@link Style} to be used for drawing the panel
	 */
	public StyledPanelUI(Style style) {
		this.style = style;
	}

	@Override
	public void paint(Graphics graphics, JComponent panel) {
		StyleUtil.fillBackground(style, graphics, 0, 0, panel.getWidth(), panel.getHeight());
	}

	@Override
	public void installUI(JComponent panel) {
		super.installUI(panel);
		panel.setForeground(style.getForeground());
		panel.setBorder(style.getBorder());
	}
}
