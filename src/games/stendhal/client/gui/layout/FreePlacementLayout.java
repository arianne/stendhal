/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import games.stendhal.common.MathHelper;

/**
 * A layout manager that does nothing else but keep the child components inside
 * the parent's borders.
 */
public class FreePlacementLayout implements LayoutManager {
	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension();
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension();
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		int maxX = parent.getWidth() - insets.right;
		int maxY = parent.getHeight() - insets.bottom;
		for (Component c : parent.getComponents()) {
			int x = MathHelper.clamp(c.getX(), insets.top, maxX - c.getWidth());
			int y = MathHelper.clamp(c.getY(), insets.left, maxY - c.getHeight());
			c.setLocation(x, y);
		}
	}
}
