/*
 * @(#) src/games/stendhal/client/ImageSprite.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 * A sprite to be displayed on the screen. Note that a sprite contains no state
 * information, i.e. its just the image and not the location. This allows us to
 * use a single sprite in lots of different places without having to store
 * multiple copies of the image.
 * 
 * @author Kevin Glass
 */
public class ImageSprite implements Sprite {
	/** a brighter version of the sprite */
	protected ImageSprite brighterSprite;

	/** a darker version of the sprite */
	protected ImageSprite darkerSprite;

	/** The image to be drawn for this sprite */
	protected Image image;


	/**
	 * Create a new sprite based on an image
	 * 
	 * @param image
	 *            The image that is this sprite
	 */
	public ImageSprite(Image image) {
		this.image = image;
	}


	public ImageSprite(Sprite sprite) {
		image = getGC().createCompatibleImage(sprite.getWidth(), sprite.getHeight(), Transparency.BITMASK);

		sprite.draw(image.getGraphics(), 0, 0);
	}


	//
	// ImageSprite
	//

	protected static GraphicsConfiguration getGC() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	public Graphics getGraphics() {
		return image.getGraphics();
	}
	
	/**
	 * Flip the image horizontally.
	 * @return an horizontal flipped sprite.
	 */
	public ImageSprite flip() {
		Image empty = getGC().createCompatibleImage(getWidth(), getHeight(), Transparency.BITMASK);
		ImageSprite spr=new ImageSprite(empty);
		
		spr.getGraphics().drawImage(image, getWidth(), 0, 0, getHeight(), 0, 0, getWidth(), getHeight(), null);
		
		return spr;
	}

	/** overlays the image with the given color and returns a new image. */
	private Image getModifiedImage(Color color, float alpha) {
		BufferedImage i = getGC().createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
		draw(i.getGraphics(), 0, 0);
		Graphics2D g = i.createGraphics();
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
		g.fillRect(0, 0, i.getWidth(), i.getHeight());

		return i;
	}

	/** returns a brighter version of the sprite */
	public synchronized ImageSprite brighter() {
		if (brighterSprite == null) {
			brighterSprite = new ImageSprite(getModifiedImage(Color.WHITE, 0.3f));
			brighterSprite.darkerSprite = this;
		}
		return brighterSprite;
	}

	/** returns a darker version of the sprite */
	public synchronized ImageSprite darker() {
		if (darkerSprite == null) {
			darkerSprite = new ImageSprite(getModifiedImage(Color.BLACK, 0.3f));
			darkerSprite.brighterSprite = this;
		}
		return darkerSprite;
	}


	//
	// Sprite
	//

	/**
	 * Copy the sprite.
	 *
	 * @return	A new copy of the sprite.
	 */
	public Sprite copy() {
		return new ImageSprite(this);
	}

	/**
	 * Draw the sprite onto the graphics context provided
	 * 
	 * @param g
	 *            The graphics context on which to draw the sprite
	 * @param x
	 *            The x location at which to draw the sprite
	 * @param y
	 *            The y location at which to draw the sprite
	 */
	public void draw(Graphics g, int x, int y) {
		g.drawImage(image, x, y, null);
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
	// Bugfix: to use image.getWidth()/getHeight() is not correct for images
	// coming from the Tilestore, as those are used to draw more than 1 Sprite
	// from the same image. The relevant image size is that of the image
	// that we're painting in, but the Graphics context doesn't say anything
	// about the size of the image it belongs to so I had to add parameters
	// for width and height. This bug was responsible for the drawing problems
	// on Mac OS X.
	// What I don't understand now though is why it worked well on Windows ;)
	// This bugfix also affects Sprite.draw in TileStore.java and
	// SpriteStore.java
	// intensifly @ gmx.com, April 20th, 2006
	// public void draw(Graphics g, int destx, int desty, int x,int y) {
	public void draw(Graphics g, int destx, int desty, int x, int y, int w, int h) {

		// g.drawImage(image,destx,desty,image.getWidth(null),image.getHeight(null),x,y,x+image.getWidth(null),y+image.getHeight(null),null);
		g.drawImage(image, destx, desty, destx + w, desty + h, x, y, x + w, y + h, null);
	}

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return image.getHeight(null);
	}

	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return image.getWidth(null);
	}
}
