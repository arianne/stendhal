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

	protected static final String[] RECOLORABLE_OUTFIT_PARTS = { "detail",
			"dress", "hair", "body", "head", "eyes" };

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
	 * Gets this entity's outfit.
	 *
	 * Note: some entities (e.g. sheep, many NPC's, all monsters) don't use
	 * the outfit system.
	 *
	 * @return The outfit, or null if this RPEntity is represented as a single
	 *         sprite rather than an outfit combination.
	 */
	public Outfit getOutfit() {
		if (has("outfit")) {
			int mouth = 0;
			int eyes = 0;
			int mask = 0;
			int hat = 0;
			if (has("outfit_mouth")) mouth = getInt("outfit_mouth");
			if (has("outfit_eyes")) eyes = getInt("outfit_eyes");
			if (has("outfit_mask")) mask = getInt("outfit_mask");
			if (has("outfit_hat")) hat = getInt("outfit_hat");
			return new Outfit(getInt("outfit"), mouth, eyes, mask, hat);
		}
		return null;
	}

	public Outfit getOriginalOutfit() {
		if (has("outfit_org")) {
			int mouth = 0;
			int eyes = 0;
			int mask = 0;
			int hat = 0;
			if (has("outfit_mouth_org")) mouth = getInt("outfit_mouth_org");
			if (has("outfit_eyes_org")) eyes = getInt("outfit_eyes_org");
			if (has("outfit_mask_org")) mask = getInt("outfit_mask_org");
			if (has("outfit_hat_org")) hat = getInt("outfit_hat_org");
			return new Outfit(getInt("outfit_org"), mouth, eyes, mask, hat);
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
		if (temporary && !has("outfit_org")) {
			put("outfit_org", get("outfit"));
			put("outfit_mouth_org", get("outfit_mouth"));
			put("outfit_eyes_org", get("outfit_eyes"));
			put("outfit_mask_org", get("outfit_mask"));
			put("outfit_hat_org", get("outfit_hat"));

			// remember the old color selections.
			for (String part : RECOLORABLE_OUTFIT_PARTS) {
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

		// if the new outfit is not temporary, remove the backup
		if (!temporary && has("outfit_org")) {
			remove("outfit_org");
			remove("outfit_mouth_org");
			remove("outfit_eyes_org");
			remove("outfit_mask_org");
			remove("outfit_hat_org");

			// clear colors
			for (String part : RECOLORABLE_OUTFIT_PARTS) {
				if (has("outfit_colors", part)) {
					remove("outfit_colors", part);
				}
			}
		}

		// combine the old outfit with the new one, as the new one might
		// contain null parts.
		final Outfit newOutfit = outfit.putOver(getOutfit());
		put("outfit", newOutfit.getCode());
		put("outfit_mouth", newOutfit.getLayer("mouth"));
		put("outfit_eyes", newOutfit.getLayer("eyes"));
		put("outfit_mask", newOutfit.getLayer("mask"));
		put("outfit_hat", newOutfit.getLayer("hat"));
		notifyWorldAboutChanges();
	}

	// Hack to preserve detail layer
	public void setOutfitWithDetail(final Outfit outfit) {
		setOutfitWithDetail(outfit, false);
	}

	// Hack to preserve detail layer
	public void setOutfitWithDetail(final Outfit outfit, final boolean temporary) {
		// preserve detail layer
		final int detailCode = getOutfit().getCode() / 100000000;

		// set the new outfit
		setOutfit(outfit, temporary);

		if (detailCode > 0) {
			// get current outfit code
			final int outfitCode = outfit.getCode() + (detailCode * 100000000);

			// re-add detail
			put("outfit", outfitCode);
			put("outfit_mouth", outfit.getLayer("mouth"));
			put("outfit_eyes", outfit.getLayer("eyes"));
			put("outfit_mask", outfit.getLayer("mask"));
			put("outfit_hat", outfit.getLayer("hat"));
			notifyWorldAboutChanges();
		}
	}

	@Override
	protected abstract void dropItemsOn(Corpse corpse);

	@Override
	public abstract void logic();
}
