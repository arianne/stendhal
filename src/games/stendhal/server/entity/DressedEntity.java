/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity;

import static games.stendhal.common.Outfits.RECOLORABLE_OUTFIT_PARTS;
import static games.stendhal.common.Outfits.SKIN_LAYERS;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.item.Corpse;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;

/**
 * Defines an entity whose appearance (outfit) can be changed.
 */
public abstract class DressedEntity extends RPEntity {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(DressedEntity.class);

	public DressedEntity() {
		super();
	}

	public DressedEntity(RPObject object) {
		super(object);
	}

	public static void generateRPClass() {
		try {
			DressedEntityRPClass.generateRPClass();
		} catch (final SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	/**
	 * This is simply for backwards compatibility to update a user's outfit
	 * with the "outfit" attribute.
	 */
	@Override
	public void put(final String attr, final String value) {
		if (attr.equals("outfit")) {
			final StringBuilder sb = new StringBuilder();
			final int code = Integer.parseInt(value);

			sb.append("body=" + code % 100);
			sb.append(",dress=" + code / 100 % 100);
			sb.append(",head=" + (int) (code / Math.pow(100, 2) % 100));
			sb.append(",hair=" + (int) (code / Math.pow(100, 3) % 100));
			sb.append(",detail=" + (int) (code / Math.pow(100, 4) % 100));

			// "outfit_ext" actually manages the entity's outfit
			super.put("outfit_ext", sb.toString());
		}

		super.put(attr, value);
	}

	/**
	 * Gets this entity's outfit.
	 *
	 * Note: some entities (e.g. sheep, many NPC's, all monsters) don't use
	 * the outfit system.
	 *
	 * @return The outfit, or null if this RPEntity is represented as a single
	 *         sprite rather than an outfit combination.
	 */
	public Outfit getOutfit() {
		if (has("outfit_ext")) {
			return new Outfit(get("outfit_ext"));
		} else if (has("outfit")) {
			return new Outfit(Integer.toString(getInt("outfit")));
		}
		return null;
	}

	public Outfit getOriginalOutfit() {
		if (has("outfit_ext_orig")) {
			return new Outfit(get("outfit_ext_orig"));
		} else if (has("outfit_org")) {
			return new Outfit(Integer.toString(getInt("outfit_org")));
		}

		return null;
	}

	/**
	 * gets the color map
	 *
	 * @return color map
	 */
	public Map<String, String> getOutfitColors() {
		return getMap("outfit_colors");
	}


	/**
	 * Sets this entity's outfit.
	 *
	 * Note: some entities (e.g. sheep, many NPC's, all monsters) don't use
	 * the outfit system.
	 *
	 * @param outfit
	 *            The new outfit.
	 */
	public void setOutfit(final Outfit outfit) {
		setOutfit(outfit, false);
	}

	/**
	 * Makes this player wear the given outfit. If the given outfit contains
	 * null parts, the current outfit will be kept for these parts. If the
	 * outfit change includes any colors, they should be changed <b>after</b>
	 * calling this.
	 *
	 * @param outfit
	 *            The new outfit.
	 * @param temporary
	 *            If true, the original outfit will be stored so that it can be
	 *            restored later.
	 */
	public void setOutfit(final Outfit outfit, final boolean temporary) {
		// if the new outfit is temporary and the player is not wearing
		// a temporary outfit already, store the current outfit in a
		// second slot so that we can return to it later.
		if (temporary) {
			// remember original outfit & colors
			storeOriginalOutfit();

			// backward compatibility
			if (has("outfit") && !has("outfit_org")) {
				put("outfit_org", get("outfit"));
			}

			if (has("outfit_ext") || has("outfit")) {
				// remember the old color selections.
				for (final String part : getColorableLayers()) {
					String tmp = part + "_orig";
					String color = get("outfit_colors", part);
					if (color != null) {
						put("outfit_colors", tmp, color);
						if (!"hair".equals(part)) {
							remove("outfit_colors", part);
						}
					} else if (has("outfit_colors", tmp)) {
						// old saved colors need to be cleared in any case
						remove("outfit_colors", tmp);
					}
				}
			}
		} else {
			if (has("outfit_ext_orig")) {
				remove("outfit_ext_orig");
			}
			if (has("outfit_org")) {
				remove("outfit_org");
			}

			if (has("outfit_ext_orig") || has("outfit_org")) {
				// clear colors
				for (final String part : getColorableLayers()) {
					if (has("outfit_colors", part)) {
						remove("outfit_colors", part);
					}
				}
			}
		}

		// combine the old outfit with the new one, as the new one might
		// contain null parts.
		final Outfit newOutfit = outfit.putOver(getOutfit());

		final StringBuilder sb = new StringBuilder();
		sb.append("body=" + newOutfit.getLayer("body") + ",");
		sb.append("dress=" + newOutfit.getLayer("dress") + ",");
		sb.append("head=" + newOutfit.getLayer("head") + ",");
		sb.append("mouth=" + newOutfit.getLayer("mouth") + ",");
		sb.append("eyes=" + newOutfit.getLayer("eyes") + ",");
		sb.append("mask=" + newOutfit.getLayer("mask") + ",");
		sb.append("hair=" + newOutfit.getLayer("hair") + ",");
		sb.append("hat=" + newOutfit.getLayer("hat") + ",");
		sb.append("detail=" + newOutfit.getLayer("detail"));

		put("outfit_ext", sb.toString());
		notifyWorldAboutChanges();
	}

	/**
	 * Makes this player wear the given outfit. If the given outfit contains
	 * null parts, the current outfit will be kept for these parts. If the
	 * outfit change includes any colors, they should be changed <b>after</b>
	 * calling this.
	 *
	 * Currently supported layers should be in this order:
	 * 		body, dress, head, mouth, eyes, mask, hair, hat, detail
	 *
	 * @param layers
	 *            Integer indexes of each outfit layer or null.
	 */
	public void setOutfit(final Integer... layers) {
		setOutfit(new Outfit(layers), false);
	}

	/**
	 * Makes this player wear the given outfit. If the given outfit contains
	 * null parts, the current outfit will be kept for these parts. If the
	 * outfit change includes any colors, they should be changed <b>after</b>
	 * calling this.
	 *
	 * Currently supported layers should be in this order:
	 * 		body, dress, head, mouth, eyes, mask, hair, hat, detail
	 *
	 * @param temporary
	 *            If true, the original outfit will be stored so that it can be
	 *            restored later.
	 * @param layers
	 *            Integer indexes of each outfit layer or null.
	 */
	public void setOutfit(final boolean temporary, final Integer... layers) {
		setOutfit(new Outfit(layers), temporary);
	}

	/**
	 * Sets the entity's outfit using a string code. E.g.:
	 * 		body=1,hair=5,dress=13
	 *
	 * @param strcode
	 */
	public void setOutfit(final String strcode) {
		setOutfit(new Outfit(strcode), false);
	}

	/**
	 * Sets the entity's outfit using a string code. E.g.:
	 * 		body=1,hair=5,dress=13
	 *
	 * @param strcode
	 * 		String code representing outfit.
	 * @param temporary
	 * 		If true, the original outfit will be stored so that it can be
	 * 		restored later.
	 */
	public void setOutfit(final String strcode, final boolean temporary) {
		setOutfit(new Outfit(strcode), temporary);
	}

	// Hack to preserve detail layer
	public void setOutfitWithDetail(final Outfit outfit, final boolean temporary) {
		// preserve detail layer
		int oldDetailCode = getOutfit().getLayer("detail");
		int newDetailCode = outfit.getLayer("detail");
		if (oldDetailCode > 0 && newDetailCode == 0) {
			outfit.setLayer("detail", oldDetailCode);
		}
		setOutfit(outfit, temporary);
	}

	/**
	 * Set color for single outfit layer.
	 *
	 * @param part
	 * 		Layer to be colored.
	 * @param color
	 * 		<code>Integer</code> value color to use.
	 */
	public void setOutfitColor(final String part, final int color) {
		put("outfit_colors", part, color);
	}

	/**
	 * Set color for single outfit layer.
	 *
	 * @param part
	 * 		Layer to be colored.
	 * @param color
	 * 		<code>Color</code> value color to use.
	 */
	public void setOutfitColor(final String part, final Color color) {
		setOutfitColor(part, color.getRGB());
	}

	public void setOutfitColor(final String part, final String color) {
		put("outfit_colors", part, color);
	}

	/**
	 * Set colors for the entire outfit.
	 *
	 * @param parts
	 * 		<code>Map</code> of layers & colors (<code>Integer</code>).
	 */
	public void setOutfitColors(final Map<String, Integer> parts) {
		remove("outfit_colors"); // clear old colors
		for (final String key: parts.keySet()) {
			put("outfit_colors", key, parts.get(key));
		}
	}

	/**
	 * Checks if the entity is not wearing clothes.
	 */
	public boolean isNaked() {
		return getOutfit().isNaked();
	}

	/**
	 * Unset color of a single layer.
	 *
	 * @param part
	 * 		Layer to be unset.
	 */
	public void unsetOutfitColor(final String part) {
		remove("outfit_colors", part);
	}


	private List<String> getColorableLayers() {
		final List<String> new_list = new ArrayList<>();
		for (final String part : RECOLORABLE_OUTFIT_PARTS) {
			if (!SKIN_LAYERS.contains(part)) {
				new_list.add(part);
			}
		}

		new_list.add("skin");
		return new_list;
	}

	private void storeOriginalOutfit() {
		if (has("outfit_ext") && !has("outfit_ext_orig")) {
			put("outfit_ext_orig", get("outfit_ext"));
		}

		for (final String part : getColorableLayers()) {
			final String color_orig = get("outfit_colors", part + "_orig");
			if (color_orig == null) {
				final String color = get("outfit_colors", part);
				if (color != null) {
					put("outfit_colors", part + "_orig", color);
				}
			}
		}
	}

	public void restoreOriginalOutfit() {
		if (has("outfit_ext_orig")) {
			setOutfitWithDetail(new Outfit(get("outfit_ext_orig")), false);

			for (final String part : getColorableLayers()) {
				final String color_orig = get("outfit_colors", part + "_orig");
				if (color_orig != null) {
					put("outfit_colors", part, color_orig);
					remove("outfit_colors", part + "_orig");
				}
			}
		}
	}

	@Override
	protected abstract void dropItemsOn(Corpse corpse);

	@Override
	public abstract void logic();
}
