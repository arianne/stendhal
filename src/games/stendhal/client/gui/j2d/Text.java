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

import games.stendhal.client.IGameScreen;
import games.stendhal.client.sprite.Sprite;

import java.awt.Rectangle;

public class Text {
	public static final long STANDARD_PERSISTENCE_TIME = 5000;

	private int x;

	private int y;

	private Sprite sprite;

	private long removeTime;

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

	public void draw(final IGameScreen screen) {
		screen.drawInScreen(sprite, x - screen.getScreenViewX(), y - screen.getScreenViewY());

		if (System.currentTimeMillis() >= removeTime) {
			screen.removeText(this);
		}
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
