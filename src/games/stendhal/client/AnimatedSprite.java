/*
 * @(#) src/games/stendhal/client/AnimatedSprite.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import java.awt.Graphics;

/**
 * This is a sprite that transparently animates itself when drawn.
 */
public class AnimatedSprite implements Sprite {
	protected boolean	animating;
	protected long		delay;
	protected long		nextFrame;
	protected int		frameidx;
	protected Sprite []	frames;
	protected int		height;
	protected int		width;


	public AnimatedSprite(final Sprite [] frames, final long delay) throws IllegalArgumentException {
		if(frames.length == 0) {
			throw new IllegalArgumentException("Must have at least one frame");
		}

		if(delay <= 0L) {
			throw new IllegalArgumentException("Delay must be greater than 0");
		}

		this.frames = frames;
		this.delay = delay;

		height = 0;
		width = 0;

		for(Sprite frame : frames) {
			height = Math.max(height, frame.getHeight());
			width = Math.max(width, frame.getWidth());
		}

		animating = true;
		frameidx = 0;
		nextFrame = System.currentTimeMillis() + delay;
	}


	//
	// AnimatedSprite
	//

	public long getDelay() {
		return delay;
	}


	protected Sprite getFrame() {
		if(animating) {
			long now = System.currentTimeMillis();

			if(nextFrame <= now) {
				if(++frameidx >= frames.length) {
					frameidx = 0;
				}

				nextFrame = now + delay;
			}
		}

		return frames[frameidx];
	}


	public Sprite [] getFrames() {
		return frames;
	}


	public boolean isAnimating() {
		return animating;
	}


	public void reset() {
		frameidx = 0;
		nextFrame = System.currentTimeMillis() + delay;
	}


	public void start() {
		animating = true;
	}


	public void stop() {
		animating = true;
	}


	//
	// Sprite
	//

	public Sprite copy() {
		return new AnimatedSprite(getFrames(), getDelay());
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
	public void draw(final Graphics g, final int x, final int y) {
		getFrame().draw(g, x, y);
	}

	/**
	 * Draws the image
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
	public void draw(final Graphics g, final int destx, final int desty, final int x, final int y, final int w, final int h) {
		getFrame().draw(g, destx, desty, x, y, w, h);
	}

	/**
	 * Get the height of the drawn sprite.
	 * 
	 * @return The height in pixels of this sprite.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the width of the drawn sprite.
	 * 
	 * @return The width in pixels of this sprite.
	 */
	public int getWidth() {
		return width;
	}
}
