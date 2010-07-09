/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d;

import games.stendhal.client.sprite.Sprite;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Text {
	public static final long STANDARD_PERSISTENCE_TIME = 5000;

	private final int x;

	private final int y;

	private final Sprite sprite;

	private long removeTime;

	/**
	 * Create a new text object.
	 * 
	 * @param sprite
	 * @param x x coordinate relative to the game screen
	 * @param y y coordinate relative to the game screen
	 * @param persistTime life time of the text object in milliseconds, or
	 * 	0 for <code>STANDARD_PERSISTENCE_TIME</code>
	 */
	public Text(final Sprite sprite, final int x, final int y,
			final long persistTime) {
		this.sprite = sprite;
		this.x = x;
		this.y = y;

		if (persistTime == 0) {
			removeTime = System.currentTimeMillis() + STANDARD_PERSISTENCE_TIME;
		} else {
			removeTime = System.currentTimeMillis() + persistTime;
		}
	}

	public void draw(final Graphics g) {
		sprite.draw(g, x, y);
	}
	
	/**
	 * Check if the <code>Text</code> is old enough to be removed.
	 * 
	 * @return <code>true</code> if the text should be removed
	 */
	public boolean shouldBeRemoved() {
		return (System.currentTimeMillis() >= removeTime);
	}

	public Rectangle getArea() {
		return new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
