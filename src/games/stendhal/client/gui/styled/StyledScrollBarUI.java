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

import java.awt.Adjustable;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class StyledScrollBarUI extends BasicScrollBarUI {
	private final Style style;

	// Required by UIManager
	public static ComponentUI createUI(JComponent pane) {
		// BasicScrollBarUI instances can not be shared
		return new StyledScrollBarUI(StyleUtil.getStyle());
	}

	public StyledScrollBarUI(Style style) {
		this.style = style;
	}

	/**
	 * Paints the background flat area of the scroll bar.
	 *
	 * @param g graphics
	 * @param bar the scroll bar to be painted
	 * @param trackBounds bounds of the painted area
	 */
	@Override
	protected void paintTrack(Graphics g, JComponent bar, Rectangle trackBounds) {
		g.setColor(style.getHighLightColor());
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

		g.setColor(style.getShadowColor());
		// Don't draw shadow against the light side of an arrow button, it
		// looks ugly.
		int width = trackBounds.width;
		int height = trackBounds.height;
		if (scrollbar.getOrientation() == Adjustable.HORIZONTAL) {
			height--;
		} else {
			width--;
		}
		g.drawRect(trackBounds.x, trackBounds.y, width, height);
	}

	/**
	 * Draws the handle of the scroll bar.
	 *
	 * @param g graphics
	 * @param bar the scroll bar to be painted
	 * @param bounds bounds of the scroll bar handle
	 */
	@Override
	protected void paintThumb(Graphics g, JComponent bar, Rectangle bounds) {
		StyleUtil.fillBackground(style, g, bounds.x, bounds.y, bounds.width, bounds.height);
		style.getBorder().paintBorder(bar, g, bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		return new StyledArrowButton(orientation, style);
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		return new StyledArrowButton(orientation, style);
	}

	@Override
	public void installUI(JComponent scrollBar) {
		super.installUI(scrollBar);
		scrollBar.setForeground(style.getForeground());
	}
}
