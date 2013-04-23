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
package games.stendhal.client.gui.j2d;

import games.stendhal.client.sprite.Sprite;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Container sprite for texboxes etc. Keeps track of the time when the sprite
 * should be removed. Also implements Comparable in a way that more important
 * sprites can be placed above less important ones.
 * 
 * <br>
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class RemovableSprite implements Comparable<RemovableSprite> {
	public static final long STANDARD_PERSISTENCE_TIME = 5000;
	
	private final int x;

	private final int y;

	private final Sprite sprite;
	/** Time after which the sprite should be removed. */
	private long removeTime;
	/** Importance of the message to keep it above others. */
	private int priority;

	/**
	 * Create a new text object.
	 * 
	 * @param sprite
	 * @param x x coordinate relative to the game screen
	 * @param y y coordinate relative to the game screen
	 * @param persistTime life time of the text object in milliseconds, or
	 * 	0 for <code>STANDARD_PERSISTENCE_TIME</code>
	 */
	public RemovableSprite(final Sprite sprite, final int x, final int y,
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

	/**
	 * Draw the contained sprite.
	 * 
	 * @param g graphics
	 */
	public void draw(final Graphics g) {
		sprite.draw(g, x, y);
	}
	
	/**
	 * Get the area the sprite covers.
	 * 
	 * @return sprite area
	 */
	public Rectangle getArea() {
		return new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
	}

	/**
	 * Get the position of the left side of the sprite.
	 * 
	 * @return x coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the position of the top of the sprite.
	 * 
	 * @return y coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Set the priority of the message. Higher priority messages are kept above
	 * others; sprites of the same priority are ordered newest on top.
	 * 
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Check if the <code>Text</code> is old enough to be removed.
	 * 
	 * @return <code>true</code> if the text should be removed
	 */
	public boolean shouldBeRemoved() {
		return (System.currentTimeMillis() >= removeTime);
	}

	@Override
	public int compareTo(RemovableSprite other) {
		return priority - other.priority;
	}
}
