/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.wt.core;

import java.awt.Point;

/**
 * A list similar to a context menu.
 * 
 * @author mtotz
 */
public class WtList extends WtPanel implements WtClickListener {

	private static final int BUTTON_HEIGHT = 20;

	/**
	 * Creates a new List.
	 * 
	 * @param name
	 *            Name of the list. This is also the title.
	 * @param items
	 *            items to show
	 * @param x
	 *            x-pos
	 * @param y
	 *            y-pos
	 * @param width
	 *            width
	 * @param maxHeight
	 *            max height (height is dynamically calculated from the items)
	 */
	public WtList(String name, String[] items, int x, int y, int width,
			int maxHeight) {
		super(name, x, y, width, 10);

		setTitleBar(true);
		setFrame(true);
		setCloseable(true);
		setMinimizeable(false);
		setMovable(false);

		this.resizeToFitClientArea(
				width,
				(items.length * BUTTON_HEIGHT < maxHeight) ? (items.length * BUTTON_HEIGHT)
						: maxHeight);

		int clientWidth = getClientWidth();

		for (int i = 0; i < items.length; i++) {
			String item = items[i];
			WtButton button = new WtButton(item, clientWidth, BUTTON_HEIGHT,
					item);
			button.moveTo(0, i * BUTTON_HEIGHT);
			button.registerClickListener(this);
			addChild(button);
		}
	}

	/** an action has been chosen. 
	 * @param name 
	 * @param point */
	public void onClick(String name, Point point) {
		// tell all listeners what happened
		notifyClickListeners(name, point);
		// close ourself
		destroy();
	}
}
