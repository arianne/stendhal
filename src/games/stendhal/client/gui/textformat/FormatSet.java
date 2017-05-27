/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.textformat;

/**
 * Helper interface for Map like format sets.
 *
 * @param <T> Type of stored data
 * @param <K> Type of the wrapper interface itself. Used for type safe copies
 * 	and unions
 */
interface FormatSet<T, K extends FormatSet<T, K>> {
	/**
	 * Create a new FormatSet, starting with the values in the current set
	 * and adding all values in additional. If the format sets define values for
	 * the same attributes, the values in additional will be used.
	 *
	 * @param additional additional attributes
	 * @return a FormatSet with values from both the original FormatSet,
	 * 	and the additional FormatSet
	 */
	K union(K additional);
	/**
	 * Create a copy of the format set.
	 *
	 * @return a copy of the set
	 */
	K copy();
	/**
	 * Get the contents of the FormatSet
	 *
	 * @return contents
	 */
	T contents();
}
