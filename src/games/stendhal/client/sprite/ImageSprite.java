/*
 * @(#) src/games/stendhal/client/sprite/ImageSprite.java
 *
 * $Id$
 */

package games.stendhal.client.sprite;

//
//

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;

/**
 * A sprite to be displayed on the screen. Note that a sprite contains no state
 * information, i.e. its just the image and not the location. This allows us to
 * use a single sprite in lots of different places without having to store
 * multiple copies of the image.
 * 
 * @author Kevin Glass
 */
public class ImageSprite implements Sprite {
	/** a brighter version of the sprite. */
	protected ImageSprite brighterSprite;

	/** a darker version of the sprite. */
	protected ImageSprite darkerSprite;

	/** The image to be drawn for this sprite. */
	protected Image image;

	/**
	 * The identifier reference.
	 */
	protected Object reference;

	/**
	 * Create a new sprite based on an image.
	 * 
	 * @param image
	 *            The image that is this sprite
	 */
	public ImageSprite(final Image image) {
		this(image, null);
	}

	/**
	 * Create a new sprite based on an image.
	 * 
	 * @param image
	 *            The image that is this sprite.
	 * @param reference
	 *            The sprite reference, or null.
	 */
	public ImageSprite(final Image image, final Object reference) {
		this.image = image;
		this.reference = reference;
	}

	/**
	 * Create an image sprite from another sprite.
	 * 
	 * @param sprite
	 *            The source sprite.
	 */
	public ImageSprite(final Sprite sprite) {
		this(sprite, null);
	}

	/**
	 * Create a copy of another sprite.
	 * 
	 * @param sprite
	 *            The source sprite.
	 * @param reference
	 *            The sprite reference, or null.
	 */
	public ImageSprite(final Sprite sprite, final String reference) {
		this.reference = reference;

		image = getGC().createCompatibleImage(sprite.getWidth(),
				sprite.getHeight(), Transparency.BITMASK);

		sprite.draw(image.getGraphics(), 0, 0);
	}

	//
	// ImageSprite
	//

	/**
	 * Create a sprite with the image flipped horizontally.
	 * @param sprite 
	 * 
	 * @return A horizontally flipped sprite.
	 */
	public static ImageSprite flipped(final Sprite sprite) {
		final Image image = getGC().createCompatibleImage(sprite.getWidth(),
				sprite.getHeight(), Transparency.BITMASK);

		final int width = sprite.getWidth();

		sprite.draw(image.getGraphics(), width, 0, width, 0, -width,
				sprite.getHeight());

		return new ImageSprite(image);
	}

	protected static GraphicsConfiguration getGC() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	/**
	 * Get the graphics context of the underlying image.
	 * 
	 * @return The graphics context.
	 */
	public Graphics getGraphics() {
		return image.getGraphics();
	}

	//
	// Sprite
	//

	/**
	 * Copy the sprite.
	 * 
	 * @return A new copy of the sprite.
	 */
	public Sprite copy() {
		return new ImageSprite(this);
	}

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
	public Sprite createRegion(final int x, final int y, final int width,
			final int height, final Object ref) {
		final int iwidth = getWidth();
		final int iheight = getHeight();

		if ((x >= iwidth) || (y >= iheight)) {
			/*
			 * Outside of image (nothing to draw)
			 */
			return new EmptySprite(width, height, ref);

		}

		/*
		 * Full copy method (the memory hog)
		 */
		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

		final Image imageTemp = gc.createCompatibleImage(width, height,
				Transparency.BITMASK);

		draw(imageTemp.getGraphics(), 0, 0, x, y, width, height);

		return new ImageSprite(imageTemp, reference);
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
		g.drawImage(image, x, y, null);
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
	public void draw(final Graphics g, final int destx, final int desty, final int x, final int y, final int w,
			final int h) {
		g.drawImage(image, destx, desty, destx + w, desty + h, x, y, x + w, y
				+ h, null);
	}

	/**
	 * Get the height of the drawn sprite.
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return image.getHeight(null);
	}

	/**
	 * Get the sprite reference. This identifier is an externally opaque object
	 * that implements equals() and hashCode() to uniquely/repeatably reference
	 * a keyed sprite.
	 * 
	 * @return The reference identifier, or <code>null</code> if not
	 *         referencable.
	 */
	public Object getReference() {
		return reference;
	}

	/**
	 * Get the width of the drawn sprite.
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return image.getWidth(null);
	}
}
