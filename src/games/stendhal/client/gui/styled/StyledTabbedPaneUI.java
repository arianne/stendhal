/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import games.stendhal.common.MathHelper;

/**
 * UI delegate for JTabbedPanes.
 */
public class StyledTabbedPaneUI extends BasicTabbedPaneUI {
	private final Style style;

	/**
	 * Required by UIManager.
	 *
	 * @param pane the component to create the UI for
	 * @return UI delegate
	 */
	public static ComponentUI createUI(JComponent pane) {
		// BasicTabbedPaneUI can not be shared
		return new StyledTabbedPaneUI(StyleUtil.getStyle());
	}

	/**
	 * Create a new StyledTabbedPaneUI.
	 *
	 * @param style pixmap style
	 */
	StyledTabbedPaneUI(Style style) {
		this.style = style;
	}

	@Override
	protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
		// Calculate the content area
		int width = tabPane.getWidth();
		int height = tabPane.getHeight();
		Insets insets = tabPane.getInsets();

		int x = insets.left;
		int y = insets.top;
		// Adjust for tabs. Only top and bottom positions are supported for now.
		int tabHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
		switch (tabPlacement) {
		case SwingConstants.TOP:
			y += tabHeight;
			break;
		default:
			// keep at top
		}
		height -= tabHeight;

		// Drawing the background is this method's responsibility, even though
		// Thats not obvious from the name
		StyleUtil.fillBackground(style, g, x, y, width, height);
		// Then the actual border
		style.getBorder().paintBorder(tabPane, g, x, y, width, height);

		// Paint background over the area between the selected tab and the
		// content area
		int selected = tabPane.getSelectedIndex();
		Rectangle r = getTabBounds(selected, calcRect);
		r = r.intersection(new Rectangle(x, y, width, height));
		// Find out the border width
		int bwidth = style.getBorder().getBorderInsets(tabPane).left;
		StyleUtil.fillBackground(style, g, r.x + bwidth, r.y,
				r.width - 2 * bwidth, r.height);
	}

	@Override
	protected void paintFocusIndicator(Graphics g, int tabPlacement,
			Rectangle[] rects, int tabIndex, Rectangle iconRect,
			Rectangle textRect, boolean isSelected) {
		if (tabIndex == getFocusIndex() && tabPane.isFocusOwner()) {
			g.setColor(focus);
			g.drawRect(textRect.x, textRect.y, textRect.width, textRect.height);
		}
	}

	@Override
	protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
			int x, int y, int width, int height, boolean isSelected) {
		StyleUtil.fillBackground(style, g, x, y, width, height);
		Color c = tabPane.getBackgroundAt(tabIndex);
		// Check for custom set colors. Defaults are ColorUIResources
		if (c != null && !(c instanceof ColorUIResource)) {
			g.setColor(c);
			g.fillRect(x, y, width, height);
		}
	}

	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
			int x, int y, int width, int height, boolean isSelected) {
		style.getBorder().paintBorder(tabPane, g, x, y, width, height);
	}

	/**
	 * Set the empty space at the sides of the tab label.
	 *
	 * @param margin margin width in pixels
	 */
	public void setTabLabelMargins(int margin) {
		tabInsets = (Insets) tabInsets.clone();
		tabInsets.left = margin;
		tabInsets.right = margin;
	}

	@Override
	public void installUI(JComponent component) {
		super.installUI(component);
		component.setFont(style.getFont());
		component.setForeground(style.getForeground());
		focus = style.getShadowColor();
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		tabPane.addMouseWheelListener(new MouseWheelHandler());
	}

	/**
	 * Implements changing tabs using the mouse wheel.
	 */
	private class MouseWheelHandler implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int tabIndex = tabPane.getSelectedIndex();
			int newIndex = MathHelper.clamp(tabIndex + e.getWheelRotation(), 0, tabPane.getTabCount() - 1);
			if (newIndex != tabIndex) {
				tabPane.setSelectedIndex(newIndex);
			}
		}
	}
}
