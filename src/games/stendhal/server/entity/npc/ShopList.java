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
package games.stendhal.server.entity.npc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Singleton class that contains inventory and prices of NPC stores.
 */
public final class ShopList {

	/** The singleton instance. */
	private static ShopList instance;

	private final Map<String, Map<String, Integer>> contents;


	/**
	 * Returns the Singleton instance.
	 *
	 * @return The instance
	 */
	public static ShopList get() {
		if (instance == null) {
			instance = new ShopList();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private ShopList() {
		contents = new HashMap<String, Map<String, Integer>>();
	}

	/**
	 * gets the items offered by a shop with their prices
	 *
	 * @param name name of shop
	 * @return items and prices
	 */
	public Map<String, Integer> get(final String name) {
		return contents.get(name);
	}

	/**
	 * gets a set of all shops
	 *
	 * @return set of shops
	 */
	public Set<String> getShops() {
		return contents.keySet();
	}

	/**
	 * converts a shop into a human readable form
	 *
	 * @param name   name of shop
	 * @param header prefix
	 * @return human readable description
	 */
	public String toString(final String name, final String header) {
		final Map<String, Integer> items = contents.get(name);

		final StringBuilder sb = new StringBuilder(header + "\n");

		for (final Entry<String, Integer> entry : items.entrySet()) {
			sb.append(entry.getKey() + " \t" + entry.getValue() + "\n");
		}

		return sb.toString();
	}

	/**
	 * Add an item to a shop
	 *
	 * @param name the shop name
	 * @param item the item to add
	 * @param price the price for the item
	 */
	public void add(final String name, final String item, final int price) {
		Map<String, Integer> shop;

		if (contents.containsKey(name)) {
			shop = contents.get(name);
		} else {
			shop = new LinkedHashMap<String, Integer>();
			contents.put(name, shop);
		}

		shop.put(item, Integer.valueOf(price));
	}
}
