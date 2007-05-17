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
	/**
	 * Whether the sprite is currently animating.
	 */
	protected boolean	animating;

	/**
	 * The minimum delay between frames.
	 */
	protected long		delay;

	/**
	 * The current frame index.
	 */
	protected int		frameidx;

	/**
	 * The frame sprites.
	 */
	protected Sprite []	frames;

	/**
	 * The sprite height.
	 */
	protected int		height;

	/**
	 * The time of the next frame change.
	 */
	protected long		nextFrame;

	/**
	 * The sprite width.
	 */
	protected int		width;


	/**
	 * Create an animated sprite from a set of frame sprites.
	 *
	 * <strong>NOTE: The array of frames passed is not copied, and must
	 * not be modified while this instance exists (unless you are sure
	 * you know what you are doing).</strong>
	 *
	 * @param	frames		The frames to animate.
	 * @param	delay		The minimum delay between frames (in ms).
	 *
	 * @throws	IllegalArgumentException
	 *				If less than one frame is given, or
	 *				the delay is < 0.
	 */
	public AnimatedSprite(final Sprite [] frames, final long delay) throws IllegalArgumentException {
		this(frames, delay, true);
	}


	/**
	 * Create an animated sprite from a set of frame sprites.
	 *
	 * <strong>NOTE: The array of frames passed is not copied, and must
	 * not be modified while this instance exists (unless you are sure
	 * you know what you are doing).</strong>
	 *
	 * @param	frames		The frames to animate.
	 * @param	delay		The minimum delay between frames (in ms).
	 * @param	animating	If animation is enabled.
	 *
	 * @throws	IllegalArgumentException
	 *				If less than one frame is given, or
	 *				the delay is < 0.
	 */
	public AnimatedSprite(final Sprite [] frames, final long delay, boolean animating) throws IllegalArgumentException {
		if(frames.length == 0) {
			throw new IllegalArgumentException("Must have at least one frame");
		}

		if(delay < 0L) {
			throw new IllegalArgumentException("Delay < 0");
		}

		this.frames = frames;
		this.delay = delay;
		this.animating = animating;

		height = 0;
		width = 0;

		for(Sprite frame : frames) {
			height = Math.max(height, frame.getHeight());
			width = Math.max(width, frame.getWidth());
		}

		frameidx = 0;
		nextFrame = System.currentTimeMillis() + delay;
	}


	//
	// AnimatedSprite
	//

	/**
	 * Get the minimum delay between frames.
	 *
	 * @return	The delay between frames (in ms).
	 */
	public long getDelay() {
		return delay;
	}


	/**
	 * Get the current frame sprite.
	 *
	 * @return	The current frame.
	 */
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


	/**
	 * Get the frames that make up this animation.
	 *
	 * <strong>NOTE: The array of frames returned is not copied, and must
	 * not be modified.</strong>
	 *
	 * @return	The frames.
	 */
	public Sprite [] getFrames() {
		return frames;
	}


	/**
	 * Determine if the sprite is currently animated, or paused.
	 *
	 * @return	<code>true</code> if animating.
	 *
	 * @see-also	#start()
	 * @see-also	#stop()
	 * @see-also	#setAnimating(boolean)
	 */
	public boolean isAnimating() {
		return animating;
	}


	/**
	 * Reset the animation back to it's initial frame, and reset the
	 * next frame time.
	 */
	public void reset() {
		frameidx = 0;
		nextFrame = System.currentTimeMillis() + delay;
	}


	/**
	 * Set the sprite animating state.
	 *
	 * @param	animating	<code>true</code> if animating.
	 */
	public void setAnimating(boolean animating) {
		this.animating = animating;
	}


	/**
	 * Start the sprite animating.
	 *
	 * @see-also	#stop()
	 */
	public void start() {
		animating = true;
	}


	/**
	 * Stop the sprite animating. This does not change the current frame.
	 *
	 * @see-also	#start()
	 */
	public void stop() {
		animating = false;
	}


	//
	// Sprite
	//

	/**
	 * Copy the sprite. This does not do a deep copy, so the frames
	 * it is made of are shared.
	 *
	 * @return	A new copy of the sprite.
	 */
	public Sprite copy() {
		return new AnimatedSprite(getFrames(), getDelay());
	}
	
	/**
	 * Flip each of the sprites of the animated sprite horizontally
	 * @return a horizontally flipped animated sprite.
	 */
	public AnimatedSprite flip() {
		AnimatedSprite copy=(AnimatedSprite)copy();
		
		for(int i=0;i<frames.length;i++) {
			copy.frames[i]=((ImageSprite)frames[i]).flip();
		}
		
		return copy;
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

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AnimatedSprite) {
			AnimatedSprite img=(AnimatedSprite)obj;
			return frames.equals(img.frames);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return frames.hashCode();
	}}
