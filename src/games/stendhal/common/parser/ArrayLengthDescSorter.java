/***************************************************************************
 *                   (C) Copyright 2017 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.parser;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * sorts a array lists based on the length, the order of
 * list with the same length is undefined but consistent.
 *
 * @param <T> ArrayList
 */
public class ArrayLengthDescSorter<T extends ArrayList<?>> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		int res = o2.size() - o1.size();
		if (res != 0) {
			return res;
		}

		// Both arrays have equal length, so we don't care which one to put first.
		// But, we may not return 0 as that means that both arguments are within
		// the same equivalence class, thus only one object will survive in a Set.
		// Therefore we distinguish ArrayLists with the same length based
		// on other properties.
		return o1.toString().compareTo(o2.toString());
	}

}
