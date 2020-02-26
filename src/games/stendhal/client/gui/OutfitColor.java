/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import marauroa.common.game.RPObject;

/**
 * A helper object for accessing players' outfit color data.
 */
public class OutfitColor {
	/** Identifier for hat color. */
	public static final String HAT = "hat";
	/** Identifier for hair color. */
	public static final String HAIR = "hair";
	/** Identifier for mask color. */
	public static final String MASK = "mask";
	/** Identifier for eyes color. */
	public static final String EYES = "eyes";
	/** Identifier for dress color. */
	public static final String DRESS = "dress";
	/** Identifier for skin color. */
	public static final String SKIN = "skin";
	/** Identifier for detail color. */
	public static final String DETAIL = "detail";

	private static final String[] parts = { DRESS, SKIN, HAIR, DETAIL, EYES, MASK, HAT };
	public static final OutfitColor PLAIN = new OutfitColor();

	private Map<String, Color> map = new TreeMap<String, Color>();

	/**
	 * Create a new OutfitColor for no colors.
	 */
	private OutfitColor() {
	}

	/**
	 * Create a new OutfitColor for an RPObject. Usually you should use get()
	 * instead.
	 *
	 * @param obj
	 */
	OutfitColor(RPObject obj) {
		for (String key : parts) {
			String val = obj.get("outfit_colors", key);
			if (val != null) {
				try {
					int color = Integer.parseInt(val);
					map.put(key, new Color(color));
				} catch (NumberFormatException e) {
					Logger.getLogger(OutfitColor.class).warn("Invalid color : " + key + "=" + val);
				}
			}
		}
	}

	/**
	 * Get an OutfitColor for an RPObject. If the object has no colors
	 * specified, PLAIN is returned.
	 *
	 * @param obj
	 * @return outfit color
	 */
	public static OutfitColor get(RPObject obj) {
		Map<String, String> map = obj.getMap("outfit_colors");
		if (map != null && !map.isEmpty()) {
			return new OutfitColor(obj);
		}
		// Don't needlessly create colors for players who don't have them
		return PLAIN;
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OutfitColor) {
			return map.equals(((OutfitColor) obj).map);
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Color> entry : map.entrySet()) {
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue().getRGB());
			sb.append(";");
		}
		return sb.toString();
	}

	/**
	 * Get the color of a specific outfit part.
	 *
	 * @param key outfit part identifier
	 * @return color, or <code>null</code> if the part does not have a specified
	 * 	color
	 */
	public Color getColor(String key) {
		return map.get(key);
	}

	/**
	 * Set the color of a specific outfit part.
	 *
	 * @param key outfit part identifier
	 * @param value color, or <code>null</code> if the default colors should be
	 * 	used
	 */
	void setColor(String key, Color value) {
		if (value != null) {
			map.put(key, value);
		} else {
			map.remove(key);
		}
	}
}
