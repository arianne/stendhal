/*
 * @(#) src/games/stendhal/client/OutfitStore.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteCache;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics;

import org.apache.log4j.Logger;

/**
 * An outfit store.
 */
public class OutfitStore {
	private Logger logger = Logger.getLogger(OutfitStore.class);

	/**
	 * The singleton.
	 */
	private static final OutfitStore sharedInstance = new OutfitStore(
			SpriteStore.get());

	/**
	 * The sprite store.
	 */
	protected SpriteStore store;

	/**
	 * Create an outfit store.
	 * 
	 * @param store
	 *            The sprite store to use.
	 */
	public OutfitStore(final SpriteStore store) {
		this.store = store;
	}

	//
	// OutfitStore
	//

	/**
	 * Build an outfit sprite.
	 * 
	 * The outfit is described by an "outfit code". It is an 8-digit integer of
	 * the form TTRRHHDDBB where TT is the number of the detail graphics (optional)
	 * RR is the number of the hair graphics (optional), HH for the
	 * head, DD for the dress, and BB for the base.
	 * 
	 * @param code
	 *            The outfit code.
	 * 
	 * @return A walking state tileset.
	 */
	protected Sprite buildOutfit(int code) {
		int basecode = code % 100;
		code /= 100;
		
		int dresscode = code % 100;
		code /= 100;
		
		int headcode = code % 100;
		code /= 100;
		
		int haircode = code % 100;
		code /= 100;
		
		int detailcode = code % 100;
		
		// Base (body) layer
		Sprite layer = getBaseSprite(basecode);
		if (layer == null) {
			throw new IllegalArgumentException("No base image found for outfit: " + basecode);
		}

		final ImageSprite sprite = new ImageSprite(layer);
		final Graphics g = sprite.getGraphics();

		// Dress layer
		layer = getDressSprite(dresscode);
		layer.draw(g, 0, 0);

		// Head layer
		layer = getHeadSprite(headcode);
		layer.draw(g, 0, 0);

		// Hair layer
		layer = getHairSprite(haircode);
		layer.draw(g, 0, 0);
		
		// Item layer
		layer = getDetailSprite(detailcode);
		layer.draw(g, 0, 0);

		return sprite;
	}

	/**
	 * Get the shared instance.
	 * 
	 * @return The shared [singleton] instance.
	 */
	public static OutfitStore get() {
		return sharedInstance;
	}

	/**
	 * Get the base sprite tileset.
	 * 
	 * @param index
	 *            The resource index.
	 * 
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getBaseSprite(final int index) {
		final String ref = "data/sprites/outfit/player_base_" + index + ".png";

		if (!store.existsSprite(ref)) {
			return null;
		}

		return store.getSprite(ref);
	}

	/**
	 * Get the dress sprite tileset.
	 * 
	 * @param index
	 *            The resource index.
	 * 
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getDressSprite(final int index) {
		if (index == 0) {
			return getEmptySprite();
		}

		final String ref = "data/sprites/outfit/dress_" + index + ".png";
		return store.getSprite(ref);
	}

	/**
	 * Get the empty sprite tileset.
	 * 
	 * @return The sprite.
	 */
	private Sprite getEmptySprite() {
		return store.getEmptySprite();
	}

	/**
	 * Get the failsafe outfit.
	 * 
	 * @return The failsafe outfit tileset.
	 */
	public Sprite getFailsafeOutfit() {
		try {
			return getOutfit(0);
		} catch (RuntimeException e) {
			logger.warn("Cannot build failsafe outfit. Trying to use standard failsafe sprite.", e);
			return store.getFailsafe();
		}
	}

	/**
	 * Get the hair sprite tileset.
	 * 
	 * @param index
	 *            The resource index.
	 * 
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getHairSprite(final int index) {
		if (index == 0) {
			return getEmptySprite();
		}

		final String ref = "data/sprites/outfit/hair_" + index + ".png";
		if (!store.existsSprite(ref)) {
			return null;
		}

		return store.getSprite(ref);
	}

	/**
	 * Get the head sprite tileset.
	 * 
	 * @param index
	 *            The resource index.
	 * 
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getHeadSprite(final int index) {
		final String ref = "data/sprites/outfit/head_" + index + ".png";
		if (!store.existsSprite(ref)) {
			return null;
		}

		return store.getSprite(ref);
	}

	/**
	 * Get the item sprite tileset.
	 * 
	 * @param index
	 *            The resource index.
	 * 
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getDetailSprite(final int index) {
		if (index == 0) {
			return getEmptySprite();
		}

		final String ref = "data/sprites/outfit/detail_" + index + ".png";
		if (!store.existsSprite(ref)) {
			return null;
		}

		return store.getSprite(ref);
	}
	
	/**
	 * Get an outfit sprite.
	 * 
	 * The outfit is described by an "outfit code". It is an 8-digit integer of
	 * the form TTRRHHDDBB where where TT is the number of the detail graphics (optional)
	 * RR is the number of the hair graphics, HH for the
	 * head, DD for the dress, and BB for the base.
	 * 
	 * @param code
	 *            The outfit code.
	 * 
	 * @return An walking state tileset.
	 */
	public Sprite getOutfit(final int code) {
		final SpriteCache cache = SpriteCache.get();

		final OutfitRef reference = new OutfitRef(code);

		Sprite sprite = cache.get(reference);

		if (sprite == null) {
			sprite = buildOutfit(code);
			cache.add(reference, sprite);
		}

		return sprite;
	}

	//
	//

	/**
	 * Outfit sprite reference.
	 */
	protected static class OutfitRef {
		/*
		 * The outfit code.
		 */
		protected int code;

		/**
		 * Create an outfit reference.
		 * 
		 * @param code
		 *            The outfit code.
		 */
		public OutfitRef(final int code) {
			this.code = code;
		}

		//
		// OutfitRef
		//

		/**
		 * Get the outfit code.
		 * 
		 * @return The outfit code.
		 */
		public int getCode() {
			return code;
		}

		//
		// Object
		//

		/**
		 * Determine if this equals another object.
		 * 
		 * @param obj
		 *            Another object.
		 * 
		 * @return <code>true</code> if the object is an OutfitRef with the
		 *         same code.
		 */
		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof OutfitRef) {
				return (getCode() == ((OutfitRef) obj).getCode());
			}

			return false;
		}

		/**
		 * Get the hash code.
		 * 
		 * @return The hash code.
		 */
		@Override
		public int hashCode() {
			return getCode();
		}

		/**
		 * Get the string representation.
		 * 
		 * @return The string in the form of <code>outfit:</code><em>code</em>.
		 */
		@Override
		public String toString() {
			return "outfit:" + getCode();
		}
	}
}
