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
package games.stendhal.common;


import java.util.HashMap;
import java.util.Iterator;

/**
 * A list of [enabled] features.
 */
public class FeatureList implements Iterable<String> {
	private HashMap<String, String> list;

	/**
	 * Create a list of [enabled] features.
	 */
	public FeatureList() {
		list = new HashMap<String, String>();
	}

	//
	// FeatureList
	//

	/**
	 * Clear the list of features.
	 */
	public void clear() {
		list.clear();
	}

	/**
	 * Read an encoded features list.
	 *
	 * Encoded features are in the form of:<br>
	 * <em>name</em>[<code>=</code><em>value</em>][<code>:</code><em>name</em>[<code>=</code><em>value</em>]...]
	 * @param encoded encoded string to decode
	 */
	public void decode(final String encoded) {
		int len;
		int pos;
		int epos;
		int cpos;
		String name;
		String value;

		list.clear();

		len = encoded.length();
		pos = 0;

		while (pos < len) {
			cpos = encoded.indexOf(':', pos);
			if (cpos == -1) {
				cpos = len;
			}
			epos = encoded.indexOf('=', pos);
			if ((epos == -1) || (epos > cpos)) {
				epos = cpos;
			}

			name = encoded.substring(pos, epos);

			if (epos < cpos) {
				value = encoded.substring(epos + 1, cpos);
			} else {
				value = "";
			}

			list.put(name, value);

			pos = cpos + 1;
		}
	}



	/**
	 * Get a feature value.
	 *
	 * @param name key
	 * @return A feature value, or <code>null</code> if not-enabled.
	 */
	public String get(final String name) {
		return list.get(name);
	}


	//
	// Iterable
	//
	@Override
	public Iterator<String> iterator() {
		return list.keySet().iterator();
	}
}
