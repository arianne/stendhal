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
package games.stendhal.client.sprite;


import java.awt.Graphics;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * This is a sprite that transparently animates itself when drawn.
 */
public class AnimatedSprite implements Sprite {
	private static Logger logger = Logger.getLogger(AnimatedSprite.class);

	/**
	 * The identifier reference.
	 */
	private Object reference;

	/**
	 * Whether the sprite is currently animating.
	 */
	private boolean animating;

	/**
	 * The amount of time passed in the cycle.
	 */
	private int cycleTime;

	/**
	 * The [minimum] frame durations.
	 */
	protected int[] delays;

	/**
	 * The total duration of a cycle.
	 */
	private int duration;

	/**
	 * The current frame index.
	 */
	private int index;

	/**
	 * The frame sprites.
	 */
	protected Sprite[] frames;

	/**
	 * The sprite height.
	 */
	private int height;

	/**
	 * The time of the last update.
	 */
	protected long lastUpdate;

	/**
	 * Whether to loop after last frame.
	 */
	protected boolean loop;

	/**
	 * The current sprite.
	 */
	protected Sprite sprite;

	/**
	 * The sprite width.
	 */
	private int width;

	/**
	 * Create an animated sprite from a set of frame sprites.
	 *
	 * <strong>NOTE: The array of frames passed is not copied, and must not be
	 * modified while this instance exists (unless you are sure you know what
	 * you are doing).</strong>
	 *
	 * @param frames
	 *            The frames to animate.
	 * @param delay
	 *            The minimum delay between frames (in ms).
	 *
	 * @throws IllegalArgumentException
	 *             If less than one frame is given, or the delay is < 0.
	 */
	AnimatedSprite(final Sprite[] frames, final int delay) {
		this(frames, delay, true);
	}

	/**
	 * Create an animated sprite from a set of frame sprites.
	 *
	 * <strong>NOTE: The array of frames passed is not copied, and must not be
	 * modified while this instance exists (unless you are sure you know what
	 * you are doing).</strong>
	 *
	 * @param frames
	 *            The frames to animate.
	 * @param delay
	 *            The minimum delay between frames (in ms).
	 * @param animating
	 *            If animation is enabled.
	 *
	 * @throws IllegalArgumentException
	 *             If less than one frame is given, or the delay is < 0.
	 */
	public AnimatedSprite(final Sprite[] frames, final int delay,
			final boolean animating) {
		this(frames, delay, animating, null);
	}

	/**
	 * Create an animated sprite from a set of frame sprites.
	 *
	 * <strong>NOTE: The array of frames passed is not copied, and must not be
	 * modified while this instance exists (unless you are sure you know what
	 * you are doing).</strong>
	 *
	 * @param frames
	 *            The frames to animate.
	 * @param delay
	 *            The minimum delay between frames (in ms).
	 * @param animating
	 *            If animation is enabled.
	 * @param reference
	 *            The sprite identifier reference.
	 *
	 * @throws IllegalArgumentException
	 *             If less than one frame is given, or the delay is < 0.
	 */
	private AnimatedSprite(final Sprite[] frames, final int delay,
			final boolean animating, final Object reference) {
		this(frames, createDelays(delay, frames.length), animating, reference);
	}

	/**
	 * Create an animated sprite from a set of frame sprites.
	 *
	 * <strong>NOTE: The array of frames/delays passed is not copied, and must
	 * not be modified while this instance exists (unless you are sure you know
	 * what you are doing).</strong>
	 *
	 * @param frames
	 *            The frames to animate.
	 * @param delays
	 *            The minimum duration for each frame (in ms).
	 * @param animating
	 *            If animation is enabled.
	 * @param reference
	 *            The sprite identifier reference.
	 *
	 * @throws IllegalArgumentException
	 *             If less than one frame is given, or the delay is < 0.
	 */
	AnimatedSprite(final Sprite[] frames, final int[] delays,
			final boolean animating, final Object reference) {
		if (frames.length == 0) {
			logger.warn("AnimatedSprite needs at least one frame");
		}

		if (delays.length != frames.length) {
			throw new IllegalArgumentException(
					"Mismatch between number of frame sprites and delays");
		}

		/*
		 * Validate delay values. Calculate total cycle duration.
		 */
		duration = 0;

		for (int i = 0; i < delays.length; i++) {
			if (delays[i] < 0) {
				throw new IllegalArgumentException("Delay < 0");
			}

			duration += delays[i];
		}

		this.frames = frames;
		this.delays = delays;
		this.animating = animating;
		this.reference = reference;

		loop = true;

		height = 0;
		width = 0;

		for (final Sprite frame : frames) {
			height = Math.max(height, frame.getHeight());
			width = Math.max(width, frame.getWidth());
		}

		index = 0;
		if (frames.length > 0) {
			sprite = frames[0];
		} else {
			sprite = null;
		}

		cycleTime = 0;

		long now = System.currentTimeMillis();
		if (loop) {
			/*
			 * Make all looped animations look like they were started at time 0.
			 * Keeps the map sprites nicely in sync.
			 */
			lastUpdate = now - (now % duration);
		} else {
			lastUpdate = now;
		}
	}

	//
	// AnimatedSprite
	//

	/**
	 * Utility method to convert a single delay to an array of delays having
	 * that value.
	 *
	 * @param delay
	 *            The delay value.
	 * @param count
	 *            The size of the array to create.
	 *
	 * @return An array of delays.
	 */
	private static int[] createDelays(final int delay, final int count) {
		final int[] delays = new int[count];
		Arrays.fill(delays, delay);

		return delays;
	}

	/**
	 * Reset the animation back to specified frame, and reset the next frame
	 * time.
	 *
	 * @param index the default index
	 */
	public void reset(int index) {
		setIndex(index);
	}

	/**
	 * Set the frame index to a specific value.
	 *
	 * @param index
	 *            The index to use.
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the index is less than 0 or greater than or equal to the
	 *             number of frames.
	 */
	private void setIndex(final int index) {
		if ((index < 0) || (index >= frames.length)) {
			throw new ArrayIndexOutOfBoundsException("Invalid index: " + index);
		}

		this.index = index;
		sprite = frames[index];

		/*
		 * Calculate the time into this frame
		 */
		cycleTime = 0;

		for (int i = 0; i < index; i++) {
			cycleTime += delays[i];
		}

		lastUpdate = System.currentTimeMillis();
	}

	/**
	 * Start the sprite animating.
	 *
	 * @see #stop()
	 */
	public void start() {
		animating = true;
	}

	/**
	 * Stop the sprite animating. This does not change the current frame.
	 *
	 * @see #start()
	 */
	public void stop() {
		animating = false;
	}

	/**
	 * Update the current frame sprite.
	 */
	private void update() {
		final long now = System.currentTimeMillis();
		update((int) (now - lastUpdate));
		lastUpdate = now;
	}

	//
	// Sprite
	//
	/**
	 * Create a sub-region of this sprite. <strong>NOTE: This does not use
	 * caching.</strong>
	 *
	 * @param x
	 *            The starting X coordinate.
	 * @param y
	 *            The starting Y coordinate.
	 * @param width
	 *            The region width.
	 * @param height
	 *            The region height.
	 * @param ref
	 *            The sprite reference.
	 *
	 * @return A new sprite.
	 */
	@Override
	public Sprite createRegion(final int x, final int y, final int width,
			final int height, final Object ref) {
		return new TileSprite(this, x, y, width, height, ref);
	}

	/**
	 * Draw the sprite onto the graphics context provided.
	 *
	 * @param g
	 *            The graphics context on which to draw the sprite
	 * @param x
	 *            The x location at which to draw the sprite
	 * @param y
	 *            The y location at which to draw the sprite
	 */
	@Override
	public void draw(final Graphics g, final int x, final int y) {
		update();

		if (sprite != null) {
			sprite.draw(g, x, y);
		}
	}

	/**
	 * Draws the image.
	 *
	 * @param g
	 *            the graphics context where to draw to
	 * @param destx
	 *            destination x
	 * @param desty
	 *            destination y
	 * @param x
	 *            the source x
	 * @param y
	 *            the source y
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 */
	@Override
	public void draw(final Graphics g, final int destx, final int desty,
			final int x, final int y, final int w, final int h) {
		update();

		if (sprite != null) {
			sprite.draw(g, destx, desty, x, y, w, h);
		}
	}

	/**
	 * Get the height of the drawn sprite.
	 *
	 * @return The height in pixels of this sprite.
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Get the sprite reference. This identifier is an externally opaque object
	 * that implements equals() and hashCode() to uniquely/repeatably reference
	 * a keyed sprite.
	 *
	 * @return The reference identifier, or <code>null</code> if not
	 *         referencable.
	 */
	@Override
	public Object getReference() {
		return reference;
	}

	/**
	 * Get the width of the drawn sprite.
	 *
	 * @return The width in pixels of this sprite.
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * Update the current frame sprite.
	 * <em>Idealy this would be called from a central time manager,
	 * instead of draw() like now.</em>
	 *
	 * @param delta
	 *            The time since last update (in ms).
	 */
	private void update(final int delta) {
		if (animating) {
			cycleTime += delta;
			if (loop) {
				// let the non-looping sprites overflow, so that they get
				// properly stopped
				cycleTime %= duration;
			}

			while (cycleTime >= delays[index]) {
				cycleTime -= delays[index];

				if (++index == frames.length) {
					index = 0;

					if (!loop) {
						sprite = null;
						animating = false;
						return;
					}
				}
			}

			sprite = frames[index];
		}
	}
}
