/***************************************************************************
 *					(C) Copyright 2013 - Faiumoni e.V					   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
 ***************************************************************************/
package games.stendhal.client.gui.styled;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;

/**
 * Pixmap style UI delegate for JTables.
 */
public class StyledTableUI extends BasicTableUI {
	/** Used style. */
	private final Style style;

	/**
	 * Required by UIManager.
	 *
	 * @param table component to create UI for
	 * @return UI delegate
	 */
	public static ComponentUI createUI(JComponent table) {
		return new StyledTableUI(StyleUtil.getStyle());
	}

	/**
	 * Create a new StyledTableUI based on a pixmap style.
	 * @param style pixmap style
	 */
	public StyledTableUI(Style style) {
		this.style = style;
	}

	@Override
	public void installUI(JComponent table) {
		super.installUI(table);
		table.setForeground(style.getForeground());
		table.setBackground(style.getPlainColor());
		table.setFont(style.getFont().deriveFont(Font.BOLD));
		this.table.setGridColor(style.getShadowColor());
	}

	@Override
	public void paint(Graphics g, JComponent table) {
		StyleUtil.fillBackground(style, g, 0, 0, table.getWidth(), table.getHeight());
		super.paint(g, table);
	}
}
