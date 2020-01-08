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
package games.stendhal.common.grammar;


import java.util.ArrayList;
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
	 * Entry for registering singular/plural prefixes for a keyword.
	 */
	private static class PrefixEntry {
		public final String keyword;
		public final String prefixPlural;
		public final String prefixSingular;

		public PrefixEntry(final String keyword, final String prefixSingular, final String prefixPlural) {
			this.keyword = keyword;
			this.prefixSingular = prefixSingular;
			this.prefixPlural = prefixPlural;
		}
	}


	public static PrefixManager s_instance = new PrefixManager();

	private Collection<String> pluralPrefixes = new HashSet<String>();

	private Collection<PrefixEntry> prefixEndList = new ArrayList<PrefixEntry>();


	private Map<String, PrefixEntry> prefixMap = new HashMap<String, PrefixEntry>();

	private Collection<String> singularPrefixes = new HashSet<String>();

	/**
	 * Initialise the map of nouns and prefix expressions.
	 */
	public PrefixManager() {
		/**
		 * NOTE
		 * register, registerEnd, registerPrefix
		 */
		register("piece of ", "pieces of ", "meat");
		register("piece of ", "pieces of ", "ham");
		register("piece of ", "pieces of ", "cheese");
		register("piece of ", "pieces of ", "wood");
		register("piece of ", "pieces of ", "paper");
		register("piece of ", "pieces of ", "iron");
		register("piece of ", "pieces of ", "chicken");
		register("piece of ", "pieces of ", "coal");
		register("piece of ", "pieces of ", "beeswax");

		register("sack of ", "sacks of ", "flour");
		register("sack of ", "sacks of ", "sugar");

		register("sheaf of ", "sheaves of ", "grain");
		register("loaf of ", "loaves of ", "bread");
		register("stick of ", "sticks of ", "butter");
		register("jar of ", "jars of ", "honey");
		register("glass of ", "glasses of ", "wine");
		register("cup of ", "cups of ", "tea");

		register("sprig of ", "sprigs of ", "arandula");
		register("root of ", "roots of ", "mandragora");
		register("bulb of ", "bulbs of ", "garlic");

		register("bunch of ", "bunches of ", "daisies");
		register("bunch of ", "bunches of ", "grapes");
		register("bunch of ", "bunches of ", "pinto beans");

		register("can of ", "cans of ", "oil");

		register("bottle of ", "bottles of ", "beer");
		register("bottle of ", "bottles of ", "water");
		register("bottle of ", "bottles of ", "milk");
		register("bottle of ", "bottles of ", "holy water");
		register("bottle of ", "bottles of ", "fierywater");
		register("bottle of ", "bottles of ", "olive oil");
		register("bottle of ", "bottles of ", "vinegar");

		register("vial of ", "vials of ", "antivenom");
		register("vial of ", "vials of ", "sedative");
		register("vial of ", "vials of ", "cobra venom");

		registerEnd("bottle of ", "bottles of ", "potion");
		registerEnd("bottle of ", "bottles of ", "poison");
		registerEnd("bottle of ", "bottles of ", "antidote");

		registerEnd("nugget of ", "nuggets of ", " ore");

		registerEnd("spool of ", "spools of ", " thread");

		registerEnd("pair of ", "pairs of ", " legs");
		registerEnd("pair of ", "pairs of ", " boots");

		registerPrefix("suit of ", "suits of "); // "armor"

	}


	/**
	 * Prefix one of the registered nouns with an expression like "piece of".
	 *
	 * @param str noun to process
	 * @param lowString lowercase version of str
	 * @return noun with prefix
	 */
	public String fullForm(final String str, final String lowString) {
		String ret = lowString;

		PrefixEntry found = prefixMap.get(str);

		if (found != null) {
			ret = found.prefixSingular + ret;
		} else {
			for(PrefixEntry entry : prefixEndList) {
				if (str.endsWith(entry.keyword)) {
					ret = Grammar.addPrefixIfNotAlreadyThere(ret, entry.prefixSingular, entry.prefixPlural);
					break;
				}
			}
		}

		return ret;
	}

	/**
	 * @return collection of all registered plural prefixes.
	 */
	public Collection<String> getPluralPrefixes() {
		return pluralPrefixes;
	}
	/**
	 * @return collection of all registered singular prefixes.
	 */
	public Collection<String> getSingularPrefixes() {
		return singularPrefixes;
	}

	/**
	 * Define the singular and plural prefix strings for an item name with full match,
	 * for example "piece of paper".
	 * @param prefixSingular
	 * @param prefixPlural
	 * @param noun
	 */
	private void register(final String prefixSingular, final String prefixPlural, final String noun) {
		prefixMap.put(noun, new PrefixEntry(noun, prefixSingular, prefixPlural));

		registerPrefix(prefixSingular, prefixPlural);
	}
	/**
	 * Define the singular and plural prefix strings for an item name to be matched at the end,
	 * for example "bottle of ... potion".
	 * @param prefixSingular
	 * @param prefixPlural
	 * @param endString
	 */
	private void registerEnd(final String prefixSingular, final String prefixPlural, final String endString) {
		prefixEndList.add(new PrefixEntry(endString, prefixSingular, prefixPlural));

		registerPrefix(prefixSingular, prefixPlural);
	}


	/**
	 * Register a pair of singular and plural prefix strings to be removed
	 * when parsing item names, for example "suits of leather armor".
	 * @param prefixSingular
	 * @param prefixPlural
	 */
	private void registerPrefix(final String prefixSingular, final String prefixPlural) {
		singularPrefixes.add(prefixSingular);
		pluralPrefixes.add(prefixPlural);
	}
}
