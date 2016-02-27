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

/**
 * Allows setting attributes for an {@link ItemCollector}.
 */
public interface ItemCollectorSetters {

	/**
	 * Sets the name of an item to collect.
	 *
	 * @param itemName
	 *            an item name
	 * @return this object to allow method call chaining
	 */
	ItemCollectorSetters item(String itemName);

	/**
	 * Sets the number of required instances for an item to collect.
	 *
	 * @param count
	 *            the number of items of the same kind
	 * @return this object to allow method call chaining
	 */
	ItemCollectorSetters pieces(int count);

	/**
	 * Sets the message to describe an item to collect. Should contain a "%s"
	 * where the count and name of the item are to be replaced. For example, the
	 * message "I still need %s." may appear as "I still need 6 pieces of iron."
	 *
	 * @param message
	 *            a message to be said by the NPC when talking about the item to
	 *            collect
	 * @return this object to allow method call chaining
	 */
	ItemCollectorSetters bySaying(String message);
}
