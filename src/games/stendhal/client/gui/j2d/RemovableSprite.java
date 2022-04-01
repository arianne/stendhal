/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

import java.awt.Graphics;
import java.awt.Rectangle;

import games.stendhal.client.GameScreenSpriteHelper;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.sprite.Sprite;


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

	private int x;

	private int y;

	// entity that sprite will follow
	private Entity owner;

	private final Sprite sprite;
	/** Time after which the sprite should be removed. */
	private long removeTime;
	/** Importance of the message to keep it above others. */
	private int priority;

	private static GameScreenSpriteHelper gsHelper;


	/**
	 * Creates a new text object.
	 *
	 * @param sprite
	 * @param x
	 *     X coordinate relative to the game screen.
	 * @param y
	 *     Y coordinate relative to the game screen.
	 * @param persistTime
	 *     Lifetime of the text object in milliseconds, or 0 for
	 *     <code>STANDARD_PERSISTENCE_TIME</code>.
	 */
	public RemovableSprite(final Sprite sprite, final int x, final int y,
			final long persistTime) {
		gsHelper = GameScreenSpriteHelper.get();

		this.sprite = sprite;
		this.x = x;
		this.y = y;

		setPersistTime(persistTime);
	}

	/**
	 * Creates a new text object.
	 *
	 * @param sprite
	 * @param entity
	 *     Entity that sprite will follow on screen.
	 * @param persistTime
	 *     Lifetime of the text object in milliseconds, or 0 for
	 *     <code>STANDARD_PERSISTENCE_TIME</code>.
	 */
	public RemovableSprite(final Sprite sprite, final Entity entity,
			final long persistTime) {
		gsHelper = GameScreenSpriteHelper.get();

		this.sprite = sprite;
		this.owner = entity;

		setPersistTime(persistTime);
	}

	/**
	 * Sets the time the entity will remain on screen.
	 *
	 * @param persistTime
	 *     Lifetime of the text object in milliseconds, or 0 for
	 *     <code>STANDARD_PERSISTENCE_TIME</code>.
	 */
	private void setPersistTime(final long persistTime) {
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
		if (owner != null) {
			drawAttached(g);
			return;
		}

		sprite.draw(g, x, y);
	}

	/**
	 * Draw the contained sprite following entity.
	 *
	 * @param g
	 *     Graphics.
	 */
	private void drawAttached(final Graphics g) {
		int sx = gsHelper.convertWorldXToScaledScreen(owner.getX() + owner.getWidth());
		int sy = gsHelper.convertWorldYToScaledScreen(owner.getY());

		// Point alignment: left, bottom
		sy -= sprite.getHeight();

		sx = gsHelper.keepSpriteOnMapX(sprite, sx);
		sy = gsHelper.keepSpriteOnMapY(sprite, sy);
		// FIXME: not working to place a new bubble below previous one
		sy = gsHelper.findFreeTextBoxPosition(sprite, sx, sy);

		sprite.draw(g, sx, sy);
	}

	/**
	 * Get the area the sprite covers.
	 *
	 * @return sprite area
	 */
	public Rectangle getArea() {
		if (owner != null) {
			return new Rectangle((int) owner.getX(), (int) owner.getY(), sprite.getWidth(),
				sprite.getHeight());
		}

		return new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
	}

	/**
	 * Get the position of the left side of the sprite.
	 *
	 * @return x coordinate
	 */
	public int getX() {
		if (owner != null) {
			owner.getX();
		}

		return x;
	}

	/**
	 * Get the position of the top of the sprite.
	 *
	 * @return y coordinate
	 */
	public int getY() {
		if (owner != null) {
			owner.getY();
		}

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
