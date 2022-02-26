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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Outfits;
import games.stendhal.common.Rand;

/**
 * A data structure that represents the outfit of an RPEntity. This RPEntity can
 * either be an NPC which uses the outfit sprite system, or of a player.
 *
 * You can use this data structure so that you don't have to deal with the way
 * outfits are stored internally.
 *
 * An outfit can contain up to nine parts: body, dress, head, mouth, eyes, mask,
 * hair, hat, & detail.
 *
 * Note, however, that you can create outfit objects that consist of less than
 * nine parts by setting the other parts to <code>null</code>. For example,
 * you can create a dress outfit that you can combine with the player's current
 * so that the player gets the dress, but keeps his hair, head, and body. To
 * denote that a layer should not be drawn, its value can be set to -1.
 *
 * Not all outfits can be chosen by players.
 *
 * @author daniel
 *
 */
public class Outfit {

	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(Outfit.class);

	private static final Map<String, String> EMPTY_MAP = new HashMap<>();

	private final Map<String, Integer> layers = new HashMap<>();

	// for mapping old player outfits to new ones
	private final Map<String, Map<Integer, List<Integer>>> old_outfit_mapping = new HashMap<String, Map<Integer, List<Integer>>>() {{
		put("body", new HashMap<Integer, List<Integer>>() {{
			put(0, Arrays.asList(0, 1, 2, 3, 4, 5, 12, 14)); // male bodies
			put(1, Arrays.asList(6, 7, 8, 9, 10, 11)); // female bodies 1
			put(2, Arrays.asList(13)); // female bodies 2
		}});
		put("dress", new HashMap<Integer, List<Integer>>() {{
			put(5, Arrays.asList(3)); // casual shirt & pants
			put(6, Arrays.asList(2, 5)); // denim
			put(11, Arrays.asList(38, 50)); // soldier uniform
			put(22, Arrays.asList(18, 23, 35)); // robe
			put(23, Arrays.asList(62)); // soldier uniform/armor
			put(27, Arrays.asList(29)); // sleeveless dress
			put(29, Arrays.asList(46)); // robe
			put(50, Arrays.asList(43)); // soldier uniform with cape
			put(52, Arrays.asList(24)); // denim wings
		}});
		put("head", new HashMap<Integer, List<Integer>>() {{
			put(0, Arrays.asList(5, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21)); // small chin, small ears
			put(1, Arrays.asList(0, 1, 2, 3, 4)); // small chin, large ears
			put(2, Arrays.asList(8)); // large chin, small ears
		}});
		// eyes are set from old heads
		put("eyes", new HashMap<Integer, List<Integer>>() {{
			put(0, Arrays.asList(5, 6, 10)); // plain 1 (dark blue)
			put(1, Arrays.asList(0, 1, 2, 3, 4, 7, 14, 21)); // plain 2 (red)
			put(2, Arrays.asList(19)); // plain 3 (brown)
			put(13, Arrays.asList(15)); // blinking
			put(14, Arrays.asList(16)); // thick eyebrows
			put(15, Arrays.asList(20)); // scar
			put(18, Arrays.asList(8)); // small 1
			put(19, Arrays.asList(9, 11)); // plain 1 (green)
			put(20, Arrays.asList(17)); // plain 1 (pink)
			put(21, Arrays.asList(13, 18)); // bright blue
			put(23, Arrays.asList(12)); // small 2
		}});
		// mask is set from old heads
		put("mask", new HashMap<Integer, List<Integer>> () {{
			put(1, Arrays.asList(9)); // glasses
			put(4, Arrays.asList(21)); // eyepatch
			put(6, Arrays.asList(79, 86, 99)); // sunglasses
		}});
		put("hair", new HashMap<Integer, List<Integer>> () {{
			put(23, Arrays.asList(3, 15)); // shoulder length curly
			put(26, Arrays.asList(13)); // long ponytail
			put(7, Arrays.asList(33, 34)); // short
			put(20, Arrays.asList(38)); // shoulder length
			put(0, Arrays.asList(37, 39, 99)); // bald
		}});
		// hat is set from old hair
		put("hat", new HashMap<Integer, List<Integer>> () {{
			put(1, Arrays.asList(33, 34)); // baseball cap
			put(2, Arrays.asList(39)); // reverse baseball cap
			put(13, Arrays.asList(37)); // robe hood
			put(995, Arrays.asList(99)); // jester hat
			put(999, IntStream.rangeClosed(50, 96).boxed().collect(Collectors.toList())); // santa hat
		}});
	}};

	/**
	 * If entity is wearing an index from this layer list, should not be considered naked.
	 */
	private final static Map<String, List<Integer>> bodyCoveringIndexes = new HashMap<String, List<Integer>>() {{
		put("body", Arrays.asList(978, 979, 988, 989, 990, 991, 992, 993, 994, 995, 996, 997));
		put("hat", Arrays.asList(993, 994));
	}};


	/**
	 * Creates a new outfit. Set some of the parameters to null if you want an
	 * entity that put on this outfit to keep on the corresponding parts of its
	 * current outfit.
	 *
	 * Currently supported layers should be in this order:
	 * 		body, dress, head, mouth, eyes, mask, hair, hat, detail
	 *
	 * @param layers
	 * 		Integer indexes of each outfit layer or null.
	 */
	public Outfit(final Integer... layers) {
		int idx = 0;
		for (final String lname: Outfits.LAYER_NAMES) {
			if (idx >= layers.length) {
				break;
			}

			this.layers.put(lname, layers[idx]);
			idx++;
		}
	}

	/**
	 * Construct an outfit using a string.
	 *
	 * @param strcode
	 * 		Can be a comma separated key=value list or a 10-digit integer
	 * 		for backward compatibility.
	 */
	public Outfit(final String strcode) {
		if (strcode.contains("=")) {
			final String[] layers;
			if (strcode.contains(",")) {
				layers = strcode.split(",");
			} else {
				layers = new String[] {strcode};
			}

			for (int idx = 0; idx < layers.length; idx++) {
				final String layer = layers[idx];
				if (layer.contains("=")) {
					final String[] key = layer.split("=");
					if (Outfits.LAYER_NAMES.contains(key[0])) {
						this.layers.put(key[0], Integer.parseInt(key[1]));
					}
				}
			}
		} else {
			try {
				final int code = Integer.parseInt(strcode);

				// store old indexes so we can retrieve correct mappings for new outfit parts
				final int old_body = code % 100;
				final int old_head = (int) (code / Math.pow(100, 2) % 100);
				final int old_hair =  (int) (code / Math.pow(100, 3) % 100);

				// compatibility for special outfits from old outfit system
				int body = old_body;
				if (body >= 78) { // old special bodies started at index 78
					body += 900;
				}
				int dress = code / 100 % 100;
				if (dress >= 72) { // old special dresses started at index 72
					dress += 900;
				}
				int head = old_head;
				if (head >= 78) { // old special heads started at index 78
					head += 900;
				}
				int hair = old_hair;

				// mapping old bodies to new system
				final Map<Integer, List<Integer>> bodies_map = old_outfit_mapping.get("body");
				for (final Integer idx: bodies_map.keySet()) {
					if (bodies_map.get(idx).contains(old_body)) {
						body = idx;
						break;
					}
				}
				// mapping old heads to new system
				final Map<Integer, List<Integer>> heads_map = old_outfit_mapping.get("head");
				for (final Integer idx: heads_map.keySet()) {
					if (heads_map.get(idx).contains(old_head)) {
						head = idx;
						break;
					}
				}
				// re-map some old dresses so they are as close to old outfit as possible
				final Map<Integer, List<Integer>> dress_map = old_outfit_mapping.get("dress");
				for (final Integer idx: dress_map.keySet()) {
					if (dress_map.get(idx).contains(dress)) {
						dress = idx;
						break;
					}
				}
				// mapping eyes from old heads
				int eyes = 0;
				final Map<Integer, List<Integer>> eyes_map = old_outfit_mapping.get("eyes");
				for (final Integer idx: eyes_map.keySet()) {
					if (eyes_map.get(idx).contains(old_head)) {
						eyes = idx;
						break;
					}
				}
				// mapping mouth from old heads
				int mouth = 0;
				if (old_head == 15) {
					mouth = 1;
				} else if (old_head == 18) {
					mouth = 2;
				}
				// mapping mask/glasses from old heads
				int mask = 0;
				final Map<Integer, List<Integer>> mask_map = old_outfit_mapping.get("mask");
				for (final Integer idx: mask_map.keySet()) {
					if (mask_map.get(idx).contains(old_head)) {
						mask = idx;
						break;
					}
				}
				// re-map some old hairs so they are as close to old outfit as possible
				final Map<Integer, List<Integer>> hair_map = old_outfit_mapping.get("hair");
				for (final Integer idx: hair_map.keySet()) {
					if (hair_map.get(idx).contains(old_hair)) {
						hair = idx;
						break;
					}
				}
				// mapping hat from old hair
				int hat = 0;
				final Map<Integer, List<Integer>> hat_map = old_outfit_mapping.get("hat");
				for (final Integer idx: hat_map.keySet()) {
					if (hat_map.get(idx).contains(old_hair)) {
						hat = idx;
						break;
					}
				}

				this.layers.put("body", body);
				this.layers.put("dress", dress);
				this.layers.put("head", head);
				this.layers.put("mouth", mouth);
				this.layers.put("eyes", eyes);
				this.layers.put("mask", mask);
				this.layers.put("hair", hair);
				this.layers.put("hat", hat);
				this.layers.put("detail", (int) (code / Math.pow(100, 4) % 100));
			} catch (NumberFormatException e) {
				LOGGER.warn("Can't parse outfit code, setting failsafe outfit.");
			}
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
	 * @deprecated
	 *     Use {@link #Outfit(String)} or {@link #Outfit(Integer...)}.
	 */
	@Deprecated
	public Outfit(final Integer detail, Integer hair, Integer head,
			Integer dress, Integer body) {
		// compatibility for special outfits from old outfit system
		if (body >= 78) { // old special bodies started at index 78
			body += 900;
		}
		if (dress >= 72) { // old special dresses started at index 72
			dress += 900;
		}
		if (head >= 78) { // old special heads started at index 78
			head += 900;
		}
		if (hair == 99) { // jester hat
			hair = 999;
		}

		layers.put("body", body);
		layers.put("dress", dress);
		layers.put("head", head);
		layers.put("hair", hair);
		layers.put("detail", detail);
	}

	public Integer getLayer(final String layerName) {
		Integer layer = layers.get(layerName);
		if (layer == null) {
			layer = 0;
		}

		return layer;
	}

	public void setLayer(final String layerName, final Integer code) {
		layers.put(layerName, code);
	}

	/**
	 * Represents this outfit in a numeric code.
	 *
	 * This is for backward-compatibility with old outfit system.
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

		// compatibility for special outfits from old outfit system
		if (ha == 999) {
			ha = 99;
		}
		if (he >= 978) {
			he -= 900;
		}
		if (dr >= 972) {
			dr -= 900;
		}
		if (bo >= 978) {
			bo -= 900;
		}

		return (de * 100000000) + (ha * 1000000) + (he * 10000) + (dr * 100)
				+ bo;
	}

	/**
	 * Gets the result that you get when you wear this outfit over another
	 * outfit. Note that this new outfit can contain parts that are marked as
	 * NONE; in this case, the parts from the other outfit will be used.
	 *
	 * FIXME: the Java client cannot render outfit correctly when this is called
	 *        using an outfit created with Outfit(String) constructor. Not sure
	 *        if problem is in server or client. (AntumDeluge)
	 *
	 * @param other
	 *            the outfit that should be worn 'under' the current one
	 * @return the combined outfit
	 */
	public Outfit putOver(Outfit old) {
		// make sure old outfit is not null
		if (old == null) {
			old = new Outfit();
		}

		Integer newBody = layers.get("body");
		Integer newDress = layers.get("dress");
		Integer newHead = layers.get("head");
		Integer newMouth = layers.get("mouth");
		Integer newEyes = layers.get("eyes");
		Integer newMask = layers.get("mask");
		Integer newHair = layers.get("hair");
		Integer newHat = layers.get("hat");
		Integer newDetail = layers.get("detail");

		// wear the this outfit 'over' the other outfit;
		// use the other outfit for parts that are not defined for this outfit.
		if (newBody == null) {
			newBody = old.getLayer("body");
		}
		if (newDress == null) {
			newDress = old.getLayer("dress");
		}
		if (newHead == null) {
			newHead = old.getLayer("head");
		}
		if (newMouth == null) {
			newMouth = old.getLayer("mouth");
		}
		if (newEyes == null) {
			newEyes = old.getLayer("eyes");
		}
		if (newMask == null) {
			newMask = old.getLayer("mask");
		}
		if (newHair == null) {
			newHair = old.getLayer("hair");
		}
		if (newHat == null) {
			newHat = old.getLayer("hat");
		}
		if (newDetail == null) {
			newDetail = old.getLayer("detail");
		}

		return new Outfit(newBody, newDress, newHead, newMouth, newEyes, newMask, newHair, newHat, newDetail);
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
		Integer newBody = layers.get("body");
		Integer newDress = layers.get("dress");
		Integer newHead = layers.get("head");
		Integer newMouth = layers.get("mouth");
		Integer newEyes = layers.get("eyes");
		Integer newMask = layers.get("mask");
		Integer newHair = layers.get("hair");
		Integer newHat = layers.get("hat");
		Integer newDetail = layers.get("detail");

		// wear the this outfit 'over' the other outfit;
		// use the other outfit for parts that are not defined for this outfit.
		if (newBody == null || newBody.equals(other.getLayer("body"))) {
			newBody = 0;
		}
		if ((newDress == null) || newDress.equals(other.getLayer("dress"))) {
			newDress = 0;
		}
		if ((newHead == null) || newHead.equals(other.getLayer("head"))) {
			newHead = 0;
		}
		if ((newMouth == null) || newMouth.equals(other.getLayer("mouth"))) {
			newMouth = 0;
		}
		if ((newEyes == null) || newEyes.equals(other.getLayer("eyes"))) {
			newEyes = 0;
		}
		if ((newMask == null) || newMask.equals(other.getLayer("mask"))) {
			newMask = 0;
		}
		if ((newHair == null) || newHair.equals(other.getLayer("hair"))) {
			newHair = 0;
		}
		if ((newHat == null) || newHat.equals(other.getLayer("hat"))) {
			newHat = 0;
		}
		if ((newDetail == null) || newDetail.equals(other.getLayer("detail"))) {
			newDetail = 0;
		}

		return new Outfit(newBody, newDress, newHead, newMouth, newEyes, newMask, newHair, newHat, newDetail);
	}

	/**
	 * Gets the result that you get when you remove (parts of) an outfit.
	 * Removes the parts in the parameter, from the current outfit.
	 * NOTE: If a part does not match, the current outfit part will remain the same.
	 *
	 * Currently supported layers should be in this order:
	 * 		body, dress, head, mouth, eyes, mask, hair, hat, detail
	 *
	 * @param layers
	 * 		Integer indexes of each outfit layer that should be removed.
	 * @return the new outfit, with the layers removed.
	 */
	public Outfit removeOutfit(final Integer... layers) {
		return removeOutfit(new Outfit(layers));
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
		Integer hat = layers.get("hat");
		Integer mask = layers.get("mask");
		Integer eyes = layers.get("eyes");
		Integer mouth = layers.get("mouth");
		Integer detail = layers.get("detail");
		Integer hair = layers.get("hair");
		Integer head = layers.get("head");
		Integer dress = layers.get("dress");
		Integer body = layers.get("body");

		return ((hat == null) || hat.equals(other.getLayer("hat")))
				&& ((mask == null) || mask.equals(other.getLayer("mask")))
				&& ((eyes == null) || eyes.equals(other.getLayer("eyes")))
				&& ((mouth == null) || mouth.equals(other.getLayer("mouth")))
				&& ((detail == null) || detail.equals(other.getLayer("detail")))
				&& ((hair == null) || hair.equals(other.getLayer("hair")))
				&& ((head == null) || head.equals(other.getLayer("head")))
				&& ((dress == null) || dress.equals(other.getLayer("dress")))
				&& ((body == null) || body.equals(other.getLayer("body")));
	}

	/**
	 * Checks a single layer of the outfit.
	 *
	 * @param layer
	 * @param index
	 * @return
	 */
	public boolean isPartOf(final String layer, final Integer index) {
		return index.equals(layers.get(layer));
	}

	/**
	 * Checks whether this outfit may be selected by a normal player as normal
	 * outfit. It returns false for special event and GM outfits.
	 *
	 * @return true if it is a normal outfit
	 */
	public boolean isChoosableByPlayers() {
		Integer hat = layers.get("hat");
		Integer mask = layers.get("mask");
		Integer eyes = layers.get("eyes");
		Integer mouth = layers.get("mouth");
		Integer detail = layers.get("detail");
		Integer hair = layers.get("hair");
		Integer head = layers.get("head");
		Integer dress = layers.get("dress");
		Integer body = layers.get("body");

		return (hat == null || (hat < Outfits.HAT_OUTFITS) && (hat >= 0))
			&& (mask == null || (mask < Outfits.MASK_OUTFITS) && (mask >= 0))
			&& (eyes == null || (eyes < Outfits.EYES_OUTFITS) && (eyes >= 0))
			&& (mouth == null || (mouth < Outfits.MOUTH_OUTFITS) && (mouth >= 0))
			&& (detail == null || detail == 0)
			&& (hair == null || (hair < Outfits.HAIR_OUTFITS) && (hair >= 0))
			&& (head == null || (head < Outfits.HEAD_OUTFITS) && (head >= 0))
			&& (dress == null || (dress < Outfits.CLOTHES_OUTFITS) && (dress >= 0))
			&& (body == null || (body < Outfits.BODY_OUTFITS) && (body >= 0));
	}

	/**
	 * Is outfit missing a dress?
	 *
	 * @return true if naked, false if dressed
	 */
	public boolean isNaked() {
		for (final String layerName: bodyCoveringIndexes.keySet()) {
			if (bodyCoveringIndexes.get(layerName).contains(layers.get(layerName))) {
				return false;
			}
		}

		final Integer dress = layers.get("dress");

		if (isCompatibleWithClothes()) {
			return (dress == null) || dress.equals(0);
		}

		return false;
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
		final int newDress = Rand.randUniform(1, Outfits.CLOTHES_OUTFITS - 1);
		final int newBody = Rand.randUniform(0, Outfits.BODY_OUTFITS - 1);

		LOGGER.debug("chose random outfit: "
				+ " " + newEyes + " " + newMouth + " " + newHair
				+ " " + newHead + " " + newDress + " " + newBody);
		return new Outfit(newBody, newDress, newHead, newMouth, newEyes, 0, newHair, 0, 0);
	}

	/**
	 * Can this outfit be worn with normal clothes
	 *
	 * @return true if the outfit is compatible with clothes, false otherwise
	 */
	public boolean isCompatibleWithClothes() {
		return Outfits.isDressCompatibleBody(layers.get("body"));
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

	public String getData(Map<String, String> colors) {
		if (colors == null) {
			colors = EMPTY_MAP;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("body-" + getLayer("body") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("skin"), 0)));
		sb.append("_dress-" + getLayer("dress") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("dress"), 0)));
		sb.append("_head-" + getLayer("head") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("skin"), 0)));
		sb.append("_mouth-" + getLayer("mouth") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("mouth"), 0)));
		sb.append("_eyes-" + getLayer("eyes") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("eyes"), 0)));
		sb.append("_mask-" + getLayer("mask") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("mask"), 0)));
		sb.append("_hair-" + getLayer("hair") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("hair"), 0)));
		sb.append("_hat-" + getLayer("hat") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("hat"), 0)));
		sb.append("_detail-" + getLayer("detail") + "-");
		sb.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("detail"), 0)));
		return sb.toString();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		int idx = 0;
		for (final String layer: Outfits.LAYER_NAMES) {
			final Integer value = layers.get(layer);
			if (value != null) {
				if (idx > 0) {
					sb.append(",");
				}
				sb.append(layer + "=" + layers.get(layer));
				idx++;
			}
		}

		return sb.toString();
	}
}
