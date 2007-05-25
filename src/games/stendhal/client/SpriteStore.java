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
package games.stendhal.client;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * A resource manager for sprites in the game. Its often quite important how and
 * where you get your game resources from. In most cases it makes sense to have
 * a central resource loader that goes away, gets your resources and caches them
 * for future use.
 * <p>
 * [singleton]
 * <p>
 *
 * @author Kevin Glass
 */
public class SpriteStore {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(SpriteStore.class);

	/** The single instance of this class */
	private static SpriteStore single = new SpriteStore();

	/**
	 * Screen graphics configuration.
	 */
	protected GraphicsConfiguration	gc;

	private static boolean doOldBootstrapClassloaderWorkaroundFirst = true;

	protected SpriteStore() {
		gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	/**
	 * Get the single instance of this class
	 *
	 * @return The single instance of this class
	 */
	public static SpriteStore get() {
		return single;
	}

	/** The cached sprite map, from reference to sprite instance */
	private HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

	private class CachedSprite {
		Sprite sprite;
		int row;
		
		public CachedSprite(Sprite sprite, int row) {
			this.sprite=sprite;
			this.row=row;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof CachedSprite) {
				CachedSprite ca=(CachedSprite)obj;
				return sprite.equals(ca.sprite) && row==ca.row;				
			}
			
			return false;
		}
		
		@Override
		public int hashCode() {
			return sprite.hashCode()*(row+1);
		}
	}
	
	/** The cached sprite map, from reference to sprite instance */
	private HashMap<CachedSprite, Sprite[]> animatedSprites = new HashMap<CachedSprite, Sprite[]>();


	/**
	 * Create an animated sprite from a tile resource.
	 *
	 * @param	ref		The image resource name.
	 * @param	row		The vertical position of these
	 *				frames inside the image.
	 * @param	frameCount	The number of frames in this animation.
	 * @param	width		The width of one sprite frame,
	 *				in tiles.
	 * @param	height		The height of one sprite frame,
	 *				in tiles.
	 * @param	delay		The minimum delay between frames.
	 * @param	animating	The animating state.
	 *
	 * @return	An animated sprite.
	 */
	public AnimatedSprite getAnimatedSprite(String ref, int row, int frameCount, double width, double height, long delay, boolean animating) {
		return getAnimatedSprite(getSprite(ref), row, frameCount, width, height, delay, animating);
	}


	/**
	 * Create an animated sprite from a tile sprite.
	 *
	 * @param	sprite		The image which contains the different
	 *				frames.
	 * @param	row		The vertical position of these
	 *				frames inside the image.
	 * @param	frameCount	The number of frames in this animation.
	 * @param	width		The width of one sprite frame,
	 *				in tiles.
	 * @param	height		The height of one sprite frame,
	 *				in tiles.
	 * @param	delay		The minimum delay between frames.
	 * @param	animating	The animating state.
	 *
	 * @return	An animated sprite.
	 */
	public AnimatedSprite getAnimatedSprite(Sprite sprite, int row, int frameCount, double width, double height, long delay, boolean animating) {
		return new AnimatedSprite(getSprites(sprite, row, frameCount, width, height), delay, animating);
	}


	/**
	 * Retrieve a collection of sprites from the store.
	 *
	 * @param ref
	 *            the sprite name
	 * @param row
	 *            The row position of the frames starting at 0.
	 * @param width
	 *            of the frame
	 * @param height
	 *            of the frame
	 *
	 * @return	An array of sprites.
	 */
	public Sprite[] getSprites(String ref, int row, int frames, double width, double height) {
		return getSprites(getSprite(ref), row, frames, width, height);
	}

	/**
	 * @param animImage The image which contains the different frames
	 * @param row The vertical position of the frames inside the image
	 * @param frameCount The number of frames in this row
	 * @param width The width of each sprite, in tiles
	 * @param height The height of each sprite, in tiles
	 * @return array of sprites
	 */
	public Sprite[] getSprites(Sprite animImage, int row, int frameCount, double width, double height) {
		CachedSprite entry=new CachedSprite(animImage, row);
		if (animatedSprites.containsKey(entry)) {
			return animatedSprites.get(entry);
		}
		
		logger.debug("Cache miss: "+animImage);
		
		// calculate width and height in pixels from width and height
		// in tiles
		int pixelWidth = (int) (width * GameScreen.SIZE_UNIT_PIXELS);
		int pixelHeight = (int) (height * GameScreen.SIZE_UNIT_PIXELS);

		Sprite[] animatedSprite = new Sprite[frameCount];

		for (int i = 0; i < frameCount; i++) {
			animatedSprite[i] = getTile(animImage, i * pixelWidth, row * pixelHeight, pixelWidth, pixelHeight);
		}
		
		animatedSprites.put(new CachedSprite(animImage,row), animatedSprite);

		return animatedSprite;
	}

	public void free(String ref) {
		sprites.put(ref, null);
		sprites.remove(ref);
	}

	/**
	 * Get the failsafe sprite.
	 *
	 * @return	The failsafe sprite.
	 */
	public Sprite getFailsafe() {
		/*
		 * TODO: Create in-line sprite, incase missing all png's is
		 * why we need a failsafe. Otherwise infinite loop will occur.
		 */
		return getSprite("data/sprites/failsafe.png");
	}

	/**
	 * Retrieve a sprite from the store
	 *
	 * @param ref
	 *            The reference to the image to use for the sprite
	 * @return A sprite instance containing an accelerate image of the request
	 *         reference
	 */
	public Sprite getSprite(String ref) {
		return getSprite(ref, false);
	}

	public Sprite getSprite(String ref, boolean loadAlpha) {
		// if we've already got the sprite in the cache
		// then just return the existing version
		if (sprites.get(ref) != null) {
			return sprites.get(ref);
		}

		// otherwise, go away and grab the sprite from the resource
		// loader
		BufferedImage sourceImage = null;

		try {
			URL url = getResourceURL(ref);
			
			if (url == null) {
				logger.error("Can't find ref: " + ref);
				return getFailsafe();
			}

			// use ImageIO to read the image in
			sourceImage = ImageIO.read(url);
		} catch (IOException e) {
			logger.error("Failed to load: " + ref,e);
			return null;
		}

		// create an accelerated image of the right size to store our sprite in
		int mode = Transparency.BITMASK;

		// ALPHA channel makes it runs 30% slower.
		// if(loadAlpha)
		// {
		// mode=Transparency.TRANSLUCENT;
		// }

		Image image = gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), mode);

		// draw our source image into the accelerated image
		image.getGraphics().drawImage(sourceImage, 0, 0, null);

		// create a sprite, add it the cache then return it
		Sprite sprite = new ImageSprite(image);
		sprites.put(ref, sprite);

		return sprite;
	}


	/**
	 * Create a sprite tile (sub-region).
	 *
	 *
	 */
	public Sprite getTile(Sprite sprite, int x, int y, int width, int height) {
		if(false) {
			//
			// NEW (SEMI-TESTED) CODE
			// Saves ~3.5M, but will break flip()
			//
			return new TileSprite(sprite, x, y, width, height, null);
		} else {
			Image image = gc.createCompatibleImage(width, height, Transparency.BITMASK);

			sprite.draw(image.getGraphics(), 0, 0, x, y, width, height);

			return new ImageSprite(image);
		}
	}


	/**
	 * gets a resource URL. Use this method instead of classLoader.getResouce()
	 * because there are still clients around with a broken classloader
	 * prefering old resources. This method ensures we get the sprite
	 * from the appropriate place, this helps with deploying the game
	 * with things like webstart and updates.
	 *
	 * @param ref name of resource
	 * @return URL to this resouce
	 */
	public URL getResourceURL(String ref) {
		return doOldBootstrapClassloaderWorkaround(ref);
	}

	/**
	 * Warning, ugly workaround for a bug in Bootstrap.java prior (including) version 0.57.
	 *
	 * @param ref resource name
	 * @return URL to that resource or null in case it was not found
	 */
	private URL doOldBootstrapClassloaderWorkaround(String ref) {
		URL url = null;
		try {
			ClassLoader classloader = this.getClass().getClassLoader();
			Method method = ClassLoader.class.getDeclaredMethod("findResource", String.class);
			method.setAccessible(true);

			url = (URL) method.invoke(classloader, ref);
			if (url == null) {
				ClassLoader parent = classloader.getParent();
				if (parent != null) {
					url = parent.getResource(ref);
				}
			}
		} catch (Exception e) {
			if (doOldBootstrapClassloaderWorkaroundFirst) {
				logger.error(e, e);
				e.printStackTrace(System.err);
				doOldBootstrapClassloaderWorkaroundFirst = false;
			}
		}
		if (url == null) {
			url = this.getClass().getClassLoader().getResource(ref);
		}
		return url;
	}
}
