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

/**
 * An outfit store.
 */
public class OutfitStore {
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
	public OutfitStore(SpriteStore store) {
		this.store = store;
	}

	//
	// OutfitStore
	//

	/**
	 * Build an outfit sprite.
	 *
	 * The outfit is described by an "outfit code". It is an 8-digit integer of
	 * the form RRHHDDBB where RR is the number of the hair graphics, HH for the
	 * head, DD for the dress, and BB for the base.
	 *
	 * @param code
	 *            The outfit code.
	 *
	 * @return An walking state tileset.
	 */
	protected Sprite buildOutfit(int code) {
		int idx;

		/*
		 * Base (body) layer
		 */
		idx = code % 100;
	

		Sprite layer = getBaseSprite(idx);

		if (layer == null) {
			throw new IllegalArgumentException(
					"No base image found for outfit: " + code);
		}
		code /= 100;
		ImageSprite sprite = new ImageSprite(layer);
		Graphics g = sprite.getGraphics();

		/*
		 * Dress layer
		 */
		idx = code % 100;
		code /= 100;

		layer = getDressSprite(idx);
		layer.draw(g, 0, 0);

		/*
		 * Head layer
		 */
		idx = code % 100;
		code /= 100;

		layer = getHeadSprite(idx);
		layer.draw(g, 0, 0);

		/*
		 * Hair layer
		 */
		idx = code % 100;

		layer = getHairSprite(idx);
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
	public Sprite getBaseSprite(int index) {
		String ref = "data/sprites/outfit/player_base_" + index + ".png";

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
	public Sprite getDressSprite(int index) {
		if (index == 0) {
			return getEmptySprite();
		}

		String ref = "data/sprites/outfit/dress_" + index + ".png";
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
		// TODO: Need a failsafe that depends on minimal resources
		return getOutfit(0);
	}

	/**
	 * Get the hair sprite tileset.
	 *
	 * @param index
	 *            The resource index.
	 *
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getHairSprite(int index) {
		if (index == 0) {
			return getEmptySprite();
		}

		String ref = "data/sprites/outfit/hair_" + index + ".png";
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
	public Sprite getHeadSprite(int index) {
		String ref = "data/sprites/outfit/head_" + index + ".png";
		if (!store.existsSprite(ref)) {
			return null;
		}

		return store.getSprite(ref);
	}

	/**
	 * Get an outfit sprite.
	 *
	 * The outfit is described by an "outfit code". It is an 8-digit integer of
	 * the form RRHHDDBB where RR is the number of the hair graphics, HH for the
	 * head, DD for the dress, and BB for the base.
	 *
	 * @param code
	 *            The outfit code.
	 *
	 * @return An walking state tileset.
	 */
	public Sprite getOutfit(int code) {
		SpriteCache cache = SpriteCache.get();

		OutfitRef reference = new OutfitRef(code);

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
		public OutfitRef(int code) {
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
		public boolean equals(Object obj) {
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
