/***************************************************************************
 *                      (C) Copyright 2011 - Stendhal                      *
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Manage the rules for building full forms from words
 * by prefixing with word phrases like "piece of".
 */
final class PrefixManager
{
	/**
	 * Initialise the map of words and prefix expressions.
	 */
	public PrefixManager() {
		register("piece of ", "pieces of ", "meat");
		register("piece of ", "pieces of ", "ham");
		register("piece of ", "pieces of ", "cheese");
		register("piece of ", "pieces of ", "wood");
		register("piece of ", "pieces of ", "paper");
		register("piece of ", "pieces of ", "iron");
		register("piece of ", "pieces of ", "chicken");
		register("piece of ", "pieces of ", "coal");

		registerEnd("nugget of ", "nuggets of ", " ore");

		register("sack of ", "sacks of ", "flour");
		register("sack of ", "sacks of ", "sugar");

		register("sheaf of ", "sheaves of ", "grain");

		register("loaf of ", "loaves of ", "bread");

		register("bottle of ", "bottles of ", "beer");
		register("bottle of ", "bottles of ", "water");
		register("bottle of ", "bottles of ", "fierywater");
		register("bottle of ", "bottles of ", "milk");
		registerEnd("bottle of ", "bottles of ", "potion");
		registerEnd("bottle of ", "bottles of ", "poison");
		registerEnd("bottle of ", "bottles of ", "antidote");

		register("stick of ", "sticks of ", "butter");

		register("bulb of ", "bulbs of ", "garlic");

		register("jar of ", "jars of ", "honey");

		register("glass of ", "glasses of ", "wine");

		register("cup of ", "cups of ", "tea");

		register("sprig of ", "sprigs of ", "arandula");

		register("root of ", "roots of ", "mandragora");

		registerPrefix("suit of ", "suits of "); // "armor"

		registerEnd("pair of ", "pairs of ", " legs");
		registerEnd("pair of ", "pairs of ", " boots");

		register("bunch of ", "bunches of ", "daisies");

		register("can of ", "cans of ", "oil");

		registerEnd("spool of ", "spools of ", " thread");
	}


	private void register(final String prefixSingular, final String prefixPlural, final String noun) {
		prefixMap.put(noun, new PrefixEntry(prefixSingular, prefixPlural, false));

		registerPrefix(prefixSingular, prefixPlural);
	}

	private void registerEnd(final String prefixSingular, final String prefixPlural, final String endString) {
		prefixMap.put(endString, new PrefixEntry(prefixSingular, prefixPlural, true));

		registerPrefix(prefixSingular, prefixPlural);
	}

	private void registerPrefix(final String prefixSingular, final String prefixPlural) {
		singularPrefixes.add(prefixSingular);
		pluralPrefixes.add(prefixPlural);
	}


	public String fullForm(final String str, final String lowString) {
		String ret = lowString;

		for(Map.Entry<String, PrefixEntry> e : prefixMap.entrySet()) {
			final PrefixEntry entry = e.getValue();

			if (entry.endsWith) {
				if (str.endsWith(e.getKey())) {
					ret = Grammar.addPrefixIfNotAlreadyThere(ret, entry.prefixSingular, entry.prefixPlural);
					break;
				}
			} else {
				if (str.equals(e.getKey())) {
					ret = Grammar.addPrefixIfNotAlreadyThere(ret, entry.prefixSingular, entry.prefixPlural);
					break;
				}
			}
		}

		return ret;
	}

	public Collection<String> getSingularPrefixes() {
		return singularPrefixes;
	}

	public Collection<String> getPluralPrefixes() {
		return pluralPrefixes;
	}


	private static class PrefixEntry {
		public final String prefixSingular;
		public final String prefixPlural;
		public final boolean endsWith;

		public PrefixEntry(final String prefixSingular, final String prefixPlural, final boolean endsWith) {
			this.prefixSingular = prefixSingular;
			this.prefixPlural = prefixPlural;
			this.endsWith = endsWith;
		}
	}

	private Map<String, PrefixEntry> prefixMap = new HashMap<String, PrefixEntry>();

	private Collection<String> singularPrefixes = new HashSet<String>();
	private Collection<String> pluralPrefixes = new HashSet<String>();


	public static PrefixManager s_instance = new PrefixManager();
}
