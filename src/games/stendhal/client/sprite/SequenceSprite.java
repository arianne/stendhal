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
package games.stendhal.client.sprite;

import java.awt.Graphics;

/**
 * An animated sprite that draw the frames once, and then signals the attached
 * listener.
 */
public class SequenceSprite extends AnimatedSprite {
	private final SequenceSpriteListener listener;
	/** A flag for book keeping so that the listener gets notified only once. */
	private boolean done;

	/**
	 * Create a new SequenceSprite with constant delay between the frames.
	 *
	 * @param listener listener that gets notified when drawing the sprite
	 * 	sequence has finished
	 * @param frames animation frames
	 * @param delay delay between frames in milliseconds
	 */
	public SequenceSprite(SequenceSpriteListener listener, Sprite[] frames, int delay) {
		super(frames, delay);
		loop = false;
		lastUpdate = System.currentTimeMillis();
		this.listener = listener;
	}

	@Override
	public void draw(final Graphics g, final int x, final int y) {
		super.draw(g, x, y);
		if ((sprite == null) && !done) {
			listener.endSequence();
			done = true;
		}
	}

	/**
	 * Interface for objects that receive notifications at the end of the
	 * sprite sequence.
	 */
	public interface SequenceSpriteListener {
		/**
		 * Called when all the image frames have been drawn.
		 */
		void endSequence();
	}
}
