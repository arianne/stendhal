/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.logic;

import java.util.LinkedList;
import java.util.List;

/**
 * Holds a list of items to collect, together with the required quantities and
 * messages to describe them ({@link ItemCollectorData}). Uses
 * {@link ItemCollectorSetters} to build the list in a fluent way.
 *
 * Example usage:
 *
 * <pre>
 * new ItemCollector().require().item("wood").pieces(2).bySaying("Bring me %s, please.");
 * </pre>
 */
public class ItemCollector {

	private final List<ItemCollectorData> requiredItems = new LinkedList<>();

	/**
	 * Starts adding a new required item. Returns an
	 * {@link ItemCollectorSetters} to allow describing the required item in a
	 * fluent way. It may add a single item. To build a list of different items,
	 * call the method multiple times. See the class documentation for example
	 * usage.
	 *
	 * @return the collector setters
	 */
	public ItemCollectorSetters require() {
		ItemCollectorData itemData = new ItemCollectorData();
		requiredItems.add(itemData);
		return itemData;
	}

	/**
	 * Gets the list of required items, in the order that they were added by
	 * subsequent calls to {@link #require()}.
	 *
	 * @return a list of items to collect
	 */
	public List<ItemCollectorData> requiredItems() {
		return requiredItems;
	}
}
