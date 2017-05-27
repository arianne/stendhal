/***************************************************************************
 *                   (C) Copyright 2010-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.grammar;

/**
 * PrefixProcessor is used to process prefix texts in a text string.
 */
public class PrefixExtractor
{
	private transient String txt;

	public PrefixExtractor(final String text) {
		txt = text;
	}

	/**
	 * Removes a prefix, if present.
	 * If the prefix was found at the beginning of the object name, it is removed.
	 * Otherwise txt remains unchanged.
	 *
	 * @param prefix
	 * @return true if a prefix was removed
	 */
	public boolean removePrefix(final String prefix) {
		boolean changed;

		if (txt.startsWith(prefix)) {
			txt = txt.substring(prefix.length());
			changed = true;
		} else {
			changed = false;
		}

		return changed;
	}

	/**
	 * Extracts noun from a string, that may be prefixed with a singular
	 * expression like "piece of", ...
	 * The result is stored in txt.
	 *
	 * @return true on any change of txt
	 */
	public boolean extractNounSingular() {
		boolean changed = false;

		for(String prefix : PrefixManager.s_instance.getSingularPrefixes()) {
			changed |= removePrefix(prefix);
		}

		return changed;
	}

	/**
	 * Extracts noun from a string, that may be prefixed with a plural expression
	 * like "piece of", ...
	 * The result is stored in txt.
	 *
	 * @return true on any change of txt
	 */
	public boolean extractNounPlural() {
		boolean changed = false;

		for(String prefix : PrefixManager.s_instance.getPluralPrefixes()) {
			changed |= removePrefix(prefix);
		}

		return changed;
	}

	@Override
	public String toString() {
		return txt;
	}
}
