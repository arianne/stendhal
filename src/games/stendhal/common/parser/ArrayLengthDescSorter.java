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
 * sorts an array list
 * 
 * @param <T> ArrayList
 */
public class ArrayLengthDescSorter<T extends ArrayList<?>> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		return o2.size() - o1.size();
	}

}
