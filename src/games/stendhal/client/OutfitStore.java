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
package games.stendhal.client;


import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;

import org.apache.log4j.Logger;

import games.stendhal.client.gui.OutfitColor;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteCache;
import games.stendhal.client.sprite.SpriteStore;

/**
 * An outfit store.
 */
public class OutfitStore {
	private Logger logger = Logger.getLogger(OutfitStore.class);

	/** outfit directory */
	private static final String OUTFITS = "data/sprites/outfit";
	/** body directory */
	private static final String BODIES = OUTFITS + "/body";
	/** dress directory */
	private static final String DRESSES = OUTFITS + "/dress";
	/** head directory */
	private static final String HEADS = OUTFITS + "/head";
	/** mouth directory */
	private static final String MOUTHS = OUTFITS + "/mouth";
	/** eyes directory */
	private static final String EYES = OUTFITS + "/eyes";
	/** hair directory */
	private static final String HAIRS = OUTFITS + "/hair";
	/** detail directory */
	private static final String DETAILS = OUTFITS + "/detail";

	/**
	 * The singleton.
	 */
	private static final OutfitStore sharedInstance = new OutfitStore(
			SpriteStore.get());

	/**
	 * The sprite store.
	 */
	private SpriteStore store;

	/**
	 * Create an outfit store.
	 *
	 * @param store
	 *            The sprite store to use.
	 */
	private OutfitStore(final SpriteStore store) {
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
	 * head, DD for the dress, and BB for the body.
	 *
	 * @param code
	 *            The outfit code.
	 * @param color coloring data
	 *
	 * @return A walking state tileset.
	 */
	private Sprite buildOutfit(int code, final OutfitColor color) {
		final int bodycode = (code % 100);
		code /= 100;

		final int dresscode = (code % 100);
		code /= 100;

		final int headcode = (code % 100);
		code /= 100;

		final int haircode = (code % 100);
		code /= 100;

		final int detailcode = (code % 100);

		// Body layer
		Sprite layer = getBodySprite(bodycode, color);
		if (layer == null) {
			throw new IllegalArgumentException(
					"No body image found for outfit: " + bodycode);
		}

		final ImageSprite sprite = new ImageSprite(layer);
		final Graphics g = sprite.getGraphics();

		// Dress layer
		layer = getDressSprite(dresscode, color);
		layer.draw(g, 0, 0);

		// Head layer
		layer = getHeadSprite(headcode, color);
		layer.draw(g, 0, 0);

		// Hair layer
		layer = getHairSprite(haircode, color);
		layer.draw(g, 0, 0);

		// Item layer
		layer = getDetailSprite(detailcode, color);
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
	 * Get a string value of convention xxx from "index"
	 *
	 * @param index
	 * 		The sprite index number
	 * @return
	 * 		A string value of the format xxx
	 *
	 * FIXME:	Probably not necessary since there are no more than 100
	 * 			sprites for each group.
	 */
	public String getSpriteSuffix(final int index) {
		String suffix;

		/** Get the value of the index using xxx naming convention */
		if (index < 10) {
			suffix = "00" + Integer.toString(index);
		} else if (index < 100) {
			suffix = "0" + Integer.toString(index);
		} else {
			suffix = Integer.toString(index);
		}

		return suffix;
	}

	/**
	 * Get the body sprite tileset.
	 *
	 * @param index
	 *            The resource index.
	 * @param color Skin color
	 *
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getBodySprite(final int index, OutfitColor color) {
		final String suffix = getSpriteSuffix(index);

		final String ref = BODIES + "/body_" + suffix + ".png";

		if (!store.existsSprite(ref)) {
			return null;
		}

		return store.getColoredSprite(ref, color.getColor(OutfitColor.SKIN));
	}

	/**
	 * Get the dress sprite tileset.
	 *
	 * @param index
	 *            The resource index.
	 * @param color coloring data
	 *
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getDressSprite(final int index, OutfitColor color) {
		if (index == 0) {
			return getEmptySprite();
		}

		final String suffix = getSpriteSuffix(index);

		final String ref = DRESSES + "/dress_" + suffix + ".png";

		return store.getColoredSprite(ref, color.getColor(OutfitColor.DRESS));
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
			return getOutfit(0, OutfitColor.PLAIN);
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
	 * @param color coloring data
	 *
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getHairSprite(final int index, OutfitColor color) {
		if (index == 0) {
			return getEmptySprite();
		}

		final String suffix = getSpriteSuffix(index);

		final String ref = HAIRS + "/hair_" + suffix + ".png";

		return store.getColoredSprite(ref, color.getColor(OutfitColor.HAIR));
	}

	/**
	 * Get the eyes sprite tileset.
	 *
	 * @param index
	 *            The resource index.
	 * @param color Eye color
	 * @return The sprite, or <code>null</code>
	 */
	public Sprite getEyesSprite(final int index, OutfitColor color) {
		final String suffix = getSpriteSuffix(index);

		final String ref = EYES + "/eyes_" + suffix + ".png";

		if (!store.existsSprite(ref)) {
			return null;
		}

		return store.getColoredSprite(ref, color.getColor(OutfitColor.EYES));
	}

	/**
	 * Get the mouth sprite tileset.
	 *
	 * @param index
	 *            The resource index.
	 * @return The sprite, or <code>null</code>
	 */
	public Sprite getMouthSprite(final int index) {
		final String suffix = getSpriteSuffix(index);

		final String ref = MOUTHS + "/mouth_" + suffix + ".png";

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
	 * @param color Skin color
	 *
	 * @return The sprite, or <code>null</code>.
	 */
	public Sprite getHeadSprite(final int index, OutfitColor color) {
		final String suffix = getSpriteSuffix(index);

		final String ref = HEADS + "/head_" + suffix + ".png";

		if (!store.existsSprite(ref)) {
			return null;
		}

		return store.getColoredSprite(ref, color.getColor(OutfitColor.SKIN));
	}

	/**
	 * Get the item sprite tileset.
	 *
	 * @param index
	 *            The resource index.
	 * @param color coloring data
	 *
	 * @return The sprite, or <code>null</code>.
	 */
	private Sprite getDetailSprite(final int index, OutfitColor color) {
		if (index == 0) {
			return getEmptySprite();
		}

		final String suffix = getSpriteSuffix(index);

		final String ref = DETAILS + "/detail_" + suffix + ".png";

		return store.getColoredSprite(ref, color.getColor(OutfitColor.DETAIL));
	}

	/**
	 * Get an outfit sprite.
	 *
	 * The outfit is described by an "outfit code". It is an 10-digit integer of
	 * the form TTRRHHDDBB where where TT is the number of the detail graphics (optional)
	 * RR is the number of the hair graphics, HH for the
	 * head, DD for the dress, and BB for the body.
	 *
	 * @param code
	 *            The outfit code.
	 * @param color Colors for coloring some outfit parts
	 *
	 * @return An walking state tileset.
	 */
	private Sprite getOutfit(final int code, final OutfitColor color) {
		// Use the normalized string for the reference
		final String reference = buildReference(code, color.toString());
		return getOutfit(code, color, reference);
	}

	/**
	 * Get outfit for a known outfit reference.
	 *
	 * @param code outfit code
	 * @param color Color information for outfit parts
	 * @param reference outfit reference
	 * @return outfit
	 */
	private Sprite getOutfit(final int code, final OutfitColor color,
			final String reference) {
		final SpriteCache cache = SpriteCache.get();
		Sprite sprite = cache.get(reference);

		if (sprite == null) {
			sprite = buildOutfit(code, color);
			cache.add(reference, sprite);
		}

		return sprite;
	}

	/**
	 * Get an outfit with color adjustment, such as a player in colored light.
	 *
	 * @param code outfit code
	 * @param color Color information for outfit parts
	 * @param adjColor adjustment color for the entire outfit
	 * @param blend blend mode for applying the adjustment color
	 * @return color adjusted outfit
	 */
	public Sprite getAdjustedOutfit(final int code, OutfitColor color,
			Color adjColor, Composite blend) {
		if ((adjColor == null) || (blend == null)) {
			return getOutfit(code, color);
		} else {
			final SpriteCache cache = SpriteCache.get();
			// Use the normalized string for the reference
			final String reference = buildReference(code, color.toString());
			String fullRef = reference + ":" + adjColor.getRGB() + blend.toString();
			Sprite sprite = cache.get(fullRef);
			if (sprite == null) {
				Sprite plain = getOutfit(code, color);
				sprite = store.modifySprite(plain, adjColor, blend, fullRef);

			}
			return sprite;
		}
	}

	/**
	 * Create an unique reference for an outfit.
	 *
	 * @param code outfit code
	 * @param colorCode color information for outfit parts
	 * @return outfit reference
	 */
	private String buildReference(final int code, final String colorCode) {
		return "OUTFIT:" + code + "@" + colorCode;
	}
}
