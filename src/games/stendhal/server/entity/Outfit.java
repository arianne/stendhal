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
package games.stendhal.server.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Outfits;
import games.stendhal.common.Rand;
/**
 * A data structure that represents the outfit of an RPEntity. This RPEntity can
 * either be an NPC which uses the outfit sprite system, or of a player.
 *
 * You can use this data structure so that you don't have to deal with the way
 * outfits are stored internally.
 *
 * An outfit can contain up to five parts: detail, hair, head, dress, and body.
 *
 * Note, however, that you can create outfit objects that consist of less than
 * five parts by setting the other parts to <code>null</code>. For example,
 * you can create a dress outfit that you can combine with the player's current
 * so that the player gets the dress, but keeps his hair, head, and body.
 *
 * Not all outfits can be chosen by players.
 *
 * @author daniel
 *
 */
public class Outfit {

	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(Outfit.class);

	private final Map<String, Integer> layers = new HashMap<>();

	// number of supported layers
	private final int layerCount = 9;

	/**
	 * Creates a new outfit. Set some of the parameters to null if you want an
	 * entity that put on this outfit to keep on the corresponding parts of its
	 * current outfit.
	 *
	 * @param layers
	 * 		Integer indexes of each outfit layer or null.
	 */
	public Outfit(final Integer... layers) {
		// TODO: re-order layer args on draw priority

		final List<Integer> layer_list = new ArrayList<>();
	    for (int idx = 0; idx < layerCount; idx++) {
			if (idx >= layers.length) {
				layer_list.add(null);
			} else {
				layer_list.add(layers[idx]);
			}
	    }

	    // set values of layers not specified to 0
		if (layers.length > layerCount) {
			int missing = layerCount - layers.length;
			for (int x = 0; x < missing; x++) {
				layer_list.add(null);
			}
		}

		if (layers.length > 5) {
			this.layers.put("body", layer_list.get(8));
			this.layers.put("dress", layer_list.get(7));
			this.layers.put("head", layer_list.get(6));
			this.layers.put("mouth", layer_list.get(3));
			this.layers.put("eyes", layer_list.get(2));
			this.layers.put("mask", layer_list.get(1));
			this.layers.put("hair", layer_list.get(5));
			this.layers.put("hat", layer_list.get(0));
			this.layers.put("detail", layer_list.get(4));
		} else {
			// using the outfit "code" format
			Integer code = layer_list.get(0);
			// code should never by null
			if (code == null) {
				code = 0;
			}

			this.layers.put("body", code % 100);
			this.layers.put("dress", code / 100 % 100);
			this.layers.put("head", (int) (code / Math.pow(100, 2) % 100));
			this.layers.put("hair", (int) (code / Math.pow(100, 3) % 100));
			this.layers.put("detail", (int) (code / Math.pow(100, 4) % 100));

			// extended layers
			this.layers.put("mouth", layer_list.get(1));
			this.layers.put("eyes", layer_list.get(2));
			this.layers.put("mask", layer_list.get(3));
			this.layers.put("hat", layer_list.get(4));
		}
	}

	/**
	 * This method is added for backwards compatibility. Anything using this should be updated
	 * for new method.
	 *
	 * Creates a new outfit. Set some of the parameters to null if you want an
	 * entity that put on this outfit to keep on the corresponding parts of its
	 * current outfit.
	 *
	 * @param detail
	 *            The index of the detail style, or null
	 * @param hair
	 *            The index of the hair style, or null
	 * @param head
	 *            The index of the head style, or null
	 * @param dress
	 *            The index of the dress style, or null
	 * @param body
	 *            The index of the body style, or null
	 */
	@Deprecated
	public Outfit(final Integer detail, final Integer hair, final Integer head,
			final Integer dress, final Integer body) {
		layers.put("body", body);
		layers.put("dress", dress);
		layers.put("head", head);
		layers.put("hair", hair);
		layers.put("detail", detail);

		// extended layers
		layers.put("mouth", 0);
		layers.put("eyes", 0);
		layers.put("mask", 0);
		layers.put("hat", 0);
	}

	public Integer getLayer(final String layerName) {
		return layers.get(layerName);
	}

	/**
	 * Represents this outfit in a numeric code.
	 *
	 * @return A 10-digit decimal number where the first pair of digits stand for
	 *         detail, the second pair for hair, the third pair for head, the
	 *         fourth pair for dress, and the fifth pair for body
	 */
	public int getCode() {
		Integer detail = layers.get("detail");
		Integer hair = layers.get("hair");
		Integer head = layers.get("head");
		Integer dress = layers.get("dress");
		Integer body = layers.get("body");

		int de = 0;
		int ha = 0;
		int he = 0;
		int dr = 0;
		int bo = 0;

		if (detail != null) {
			de = detail.intValue();
		}
		if (hair != null) {
			ha = hair.intValue();
		}
		if (head != null) {
			he = head.intValue();
		}
		if (dress != null) {
			dr = dress.intValue();
		}
		if (body != null) {
			bo = body.intValue();
		}

		return (de * 100000000) + (ha * 1000000) + (he * 10000) + (dr * 100)
				+ bo;
	}

	/**
	 * Gets the result that you get when you wear this outfit over another
	 * outfit. Note that this new outfit can contain parts that are marked as
	 * NONE; in this case, the parts from the other outfit will be used.
	 *
	 * @param other
	 *            the outfit that should be worn 'under' the current one
	 * @return the combined outfit
	 */
	public Outfit putOver(final Outfit other) {
		Integer newHat = layers.get("hat");
		Integer newMask = layers.get("mask");
		Integer newEyes = layers.get("eyes");
		Integer newMouth = layers.get("mouth");
		Integer newDetail = layers.get("detail");
		Integer newHair = layers.get("hair");
		Integer newHead = layers.get("head");
		Integer newDress = layers.get("dress");
		Integer newBody = layers.get("body");

		// wear the this outfit 'over' the other outfit;
		// use the other outfit for parts that are not defined for this outfit.
		if (newHat == null) {
			newHat = other.getLayer("hat");
		}
		if (newMask == null) {
			newMask = other.getLayer("mask");
		}
		if (newEyes == null) {
			newEyes = other.getLayer("eyes");
		}
		if (newMouth == null) {
			newMouth = other.getLayer("mouth");
		}
		if (newDetail == null) {
			newDetail = other.getLayer("detail");
		}
		if (newHair == null) {
			newHair = other.getLayer("hair");
		}
		if (newHead == null) {
			newHead = other.getLayer("head");
		}
		if (newDress == null) {
			newDress = other.getLayer("dress");
		}
		if (newBody == null) {
			newBody = other.getLayer("body");
		}

		return new Outfit(newHat, newMask, newEyes, newMouth, newDetail, newHair, newHead, newDress, newBody);
	}

	/**
	 * Gets the result that you get when you remove (parts of) an outfit.
	 * Removes the parts in the parameter, from the current outfit.
	 * NOTE: If a part does not match, the current outfit part will remain the same.
	 *
	 * @param other
	 *            the outfit that should be removed from the current one
	 * @return the new outfit, with the parameter-outfit removed
	 */
	public Outfit removeOutfit(final Outfit other) {
		Integer newHat = layers.get("hat");
		Integer newMask = layers.get("mask");
		Integer newEyes = layers.get("eyes");
		Integer newMouth = layers.get("mouth");
		Integer newDetail = layers.get("detail");
		Integer newHair = layers.get("hair");
		Integer newHead = layers.get("head");
		Integer newDress = layers.get("dress");
		Integer newBody = layers.get("body");

		// wear the this outfit 'over' the other outfit;
		// use the other outfit for parts that are not defined for this outfit.
		if ((newHat == null) || newHat.equals(other.getLayer("hat"))) {
			newHat = 0;
		}
		if ((newMask == null) || newMask.equals(other.getLayer("mask"))) {
			newMask = 0;
		}
		if ((newEyes == null) || newEyes.equals(other.getLayer("eyes"))) {
			newEyes = 0;
		}
		if ((newMouth == null) || newMouth.equals(other.getLayer("mouth"))) {
			newMouth = 0;
		}
		if ((newDetail == null) || newDetail.equals(other.getLayer("detail"))) {
			newDetail = 0;
		}
		if ((newHair == null) || newHair.equals(other.getLayer("hair"))) {
			newHair = 0;
		}
		if ((newHead == null) || newHead.equals(other.getLayer("head"))) {
			newHead = 0;
		}
		if ((newDress == null) || newDress.equals(other.getLayer("dress"))) {
			newDress = 0;
		}
		if ((newBody == null) || newBody.equals(other.getLayer("body"))) {
			newBody = 0;
		}

		return new Outfit(newHat, newMask, newEyes, newMouth, newDetail, newHair, newHead, newDress, newBody);
	}

	/**
	 * removes the details
	 */
	public void removeDetail() {
		// XXX: would it be better to use put("detail", 0)???
		layers.remove("detail");
	}

	/**
	 * Checks whether this outfit is equal to or part of another outfit.
	 *
	 * @param other
	 *            Another outfit.
	 * @return true iff this outfit is part of the given outfit.
	 */
	public boolean isPartOf(final Outfit other) {
		boolean partOf;

		Integer hat = layers.get("hat");
		Integer mask = layers.get("mask");
		Integer eyes = layers.get("eyes");
		Integer mouth = layers.get("mouth");
		Integer detail = layers.get("detail");
		Integer hair = layers.get("hair");
		Integer head = layers.get("head");
		Integer dress = layers.get("dress");
		Integer body = layers.get("body");

		partOf = ((hat == null) || hat.equals(other.getLayer("hat")))
				&& ((mask == null) || mask.equals(other.getLayer("mask")))
				&& ((eyes == null) || eyes.equals(other.getLayer("eyes")))
				&& ((mouth == null) || mouth.equals(other.getLayer("mouth")))
				&& ((detail == null) || detail.equals(other.getLayer("detail")))
				&& ((hair == null) || hair.equals(other.getLayer("hair")))
				&& ((head == null) || head.equals(other.getLayer("head")))
				&& ((dress == null) || dress.equals(other.getLayer("dress")))
				&& ((body == null) || body.equals(other.getLayer("body")));

		return partOf;
	}

	/**
	 * Checks whether this outfit may be selected by a normal player as normal
	 * outfit. It returns false for special event and GM outfits.
	 *
	 * @return true if it is a normal outfit
	 */
	public boolean isChoosableByPlayers() {
		boolean choosable;

		Integer hat = layers.get("hat");
		Integer mask = layers.get("mask");
		Integer eyes = layers.get("eyes");
		Integer mouth = layers.get("mouth");
		Integer detail = layers.get("detail");
		Integer hair = layers.get("hair");
		Integer head = layers.get("head");
		Integer dress = layers.get("dress");
		Integer body = layers.get("body");

		choosable = (hat < Outfits.HAT_OUTFITS) && (hat >= 0)
			&& (mask < Outfits.MASK_OUTFITS) && (mask >= 0)
			&& (eyes < Outfits.EYES_OUTFITS) && (eyes >= 0)
			&& (mouth < Outfits.MOUTH_OUTFITS) && (mouth >= 0)
			&& (detail == null || detail == 0)
			&& (hair < Outfits.HAIR_OUTFITS) && (hair >= 0)
		    && (head < Outfits.HEAD_OUTFITS) && (head >= 0)
			&& (dress < Outfits.CLOTHES_OUTFITS) && (dress >= 0)
			&& (body < Outfits.BODY_OUTFITS) && (body >= 0);

		return choosable;
	}

	/**
	 * Is outfit missing a dress?
	 *
	 * @return true if naked, false if dressed
	 */
	public boolean isNaked() {
		Integer dress = layers.get("dress");

		if (isCompatibleWithClothes()) {
			return (dress == null) || dress.equals(0);
		} else {
			return false;
		}
	}

	/**
	 * Create a random unisex outfit, with a 'normal' face and unisex body
	 *
	 * <ul>
	 * <li>hair number (1 to 26) selection of hairs which look ok with both goblin
	 *     face and cute face (later hairs only look right with cute face)</li>
	 * <li>head numbers (1 to 15) to avoid the cut eye, pink eyes, weird green eyeshadow etc</li>
	 * <li>dress numbers (1 to 16) from the early outfits before lady player base got introduced i.e. they are all unisex</li>
	 * <li>base numbers ( 1 to 15), these are the early bodies which were unisex</li>
	 * </ul>
	 * @return the new random outfit
	 */
	public static Outfit getRandomOutfit() {
		final int newEyes = Rand.randUniform(0, Outfits.EYES_OUTFITS - 1);
		final int newMouth = Rand.randUniform(0, Outfits.MOUTH_OUTFITS - 1);
		final int newHair = Rand.randUniform(0, Outfits.HAIR_OUTFITS - 1);
		final int newHead = Rand.randUniform(0, Outfits.HEAD_OUTFITS - 1);
		final int newDress = Rand.randUniform(0, Outfits.CLOTHES_OUTFITS - 1);
		final int newBody = Rand.randUniform(0, Outfits.BODY_OUTFITS - 1);

		LOGGER.debug("chose random outfit: "
				+ " " + newEyes + " " + newMouth + " " + newHair
				+ " " + newHead + " " + newDress + " " + newBody);
		return new Outfit(0, 0, newEyes, newMouth, 0, newHair, newHead, newDress, newBody);
	}

	/**
	 * Can this outfit be worn with normal clothes
	 *
	 * @return true if the outfit is compatible with clothes, false otherwise
	 */
	public boolean isCompatibleWithClothes() {
		final Integer body = layers.get("body");
		return !(body > 80 && body < 99);
	}

	@Override
	public boolean equals(Object other) {
		boolean ret = false;

		if (!(other instanceof Outfit)) {
			return ret;
		}
		else {
			Outfit outfit = (Outfit)other;
			return this.getCode() == outfit.getCode();
		}
	}

	@Override
	public int hashCode() {
		return this.getCode();
	}
}
