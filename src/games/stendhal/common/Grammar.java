/***************************************************************************
 *                      (C) Copyright 2006 - Arianne                       *
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

import org.apache.log4j.Logger;

/**
 * Helper functions for producing grammatically-correct sentences.
 */
public class Grammar {

	private static final Logger logger = Logger.getLogger(Grammar.class);

	/**
	 * "it" or "them", depending on the quantity
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "it" or "them" as appropriate
	 */
	public static String itthem(int quantity) {
		return (quantity == 1 ? "it" : "them");
	}

	/**
	 * "It" or "Them", depending on the quantity
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "It" or "Them" as appropriate
	 */
	public static String ItThem(int quantity) {
		String s = itthem(quantity);
		s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
		return s;
	}

	/**
	 * "it" or "they", depending on the quantity
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "it" or "they" as appropriate
	 */
	public static String itthey(int quantity) {
		return (quantity == 1 ? "it" : "they");
	}

	/**
	 * "It" or "They", depending on the quantity
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "It" or "They" as appropriate
	 */
	public static String ItThey(int quantity) {
		String s = itthey(quantity);
		s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
		return s;
	}

	/**
	 * "is" or "are", depending on the quantity
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "is" or "are" as appropriate
	 */
	public static String isare(int quantity) {
		return (quantity == 1 ? "is" : "are");
	}

	/**
	 * "Is" or "Are", depending on the quantity
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "Is" or "Are" as appropriate
	 */
	public static String IsAre(int quantity) {
		String s = isare(quantity);
		s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
		return s;
	}

	/**
	 * Prefixes a noun with an article.
	 *
	 * @param noun
	 *            nount
	 * @param definite
	 *            true for "the", false for a/an
	 * @return noun with article
	 */
	public static String article_noun(String noun, boolean definite) {
		if (definite) {
			return "the " + noun;
		} else {
			return a_noun(noun);
		}
	}

	/**
	 * "a [noun]" or "an [noun]", depending on the first syllable
	 *
	 * @param noun
	 *            The noun to examine
	 * @return Either "a [noun]" or "an [noun]" as appropriate
	 */
	public static String a_noun(String noun) {
		if (noun == null) {
			return null;
		}
		String enoun = fullform(noun);
		char initial = noun.length() > 0 ? Character.toLowerCase(enoun.charAt(0))
				: ' ';
		char second = noun.length() > 1 ? Character.toLowerCase(enoun.charAt(1))
				: ' ';
		if ((initial == 'e') && (second == 'u')) {
			return "a " + enoun;
		}
		if (vowel_p(initial)) {
			return "an " + enoun;
		}
		if ((initial == 'y') && consonant_p(second)) {
			return "an " + enoun;
		}
		return "a " + enoun;
	}

	/**
	 * adds a prefix unless it was already added
	 *
	 * @param noun
	 *            the noun (which may already start with the specified prefix
	 * @param prefix
	 *            prefix to add
	 * @return noun starting with prefix
	 */
	private static String addPrefixIfNotAlreadyThere(String prefix, String noun) {
		if (noun.startsWith(prefix)) {
			return noun;
		} else {
			return prefix + noun;
		}
	}

	public static String fullform(String noun) {
		String result;
		String lowString = noun.toLowerCase();
		result = lowString.replace("#", "");
		if (result.equals("meat") || result.equals("ham")
				|| result.equals("cheese") || result.equals("wood")
				|| result.equals("paper") || result.equals("iron")
				|| result.equals("chicken")) {
			result = addPrefixIfNotAlreadyThere("piece of ", lowString);
		} else if (result.endsWith(" ore") || result.endsWith("_ore")) {
			result = addPrefixIfNotAlreadyThere("nugget of ", lowString);
		} else if (result.equals("flour")) {
			result = addPrefixIfNotAlreadyThere("sack of ", lowString);
		} else if (result.equals("grain")) {
			result = addPrefixIfNotAlreadyThere("sheaf of ", lowString);
		} else if (result.equals("bread")) {
			result = addPrefixIfNotAlreadyThere("loaf of ", lowString);
		} else if (result.equals("beer") || result.equals("wine")
				|| result.endsWith("poison") || result.equals("antidote")) {
			result = addPrefixIfNotAlreadyThere("bottle of ", lowString);
		} else if (result.equals("money")) {
			// TODO: fix this (going back to money as workaround because /drop 1
			// coin does not work
			// enoun = "coin";
		} else if (result.startsWith("book_") || result.startsWith("book ")) {
			result = result.substring(5) + " book";
		} else if (result.equals("arandula")) {
			result = addPrefixIfNotAlreadyThere("sprig of ", lowString);
		} else if ((result.indexOf("_armor") > -1)
				|| (result.indexOf(" armor") > -1)) {
			result = addPrefixIfNotAlreadyThere("suit of ", lowString);
		} else if (result.endsWith("_legs") || result.endsWith(" legs")
				|| result.endsWith("_boots") || result.endsWith(" boots")) {
			result = addPrefixIfNotAlreadyThere("pair of ", lowString);
		} else {
			result = lowString;
		}

		return result;
	}

	/**
	 * "A [noun]" or "An [noun]", depending on the first syllable
	 *
	 * @param noun
	 *            The noun to examine
	 * @return Either "A [noun]" or "An [noun]" as appropriate
	 */
	public static String A_noun(String noun) {
		String s = a_noun(noun);
		s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
		return s;
	}

	/**
	 * "[noun]'s" or "[noun]'", depending on the last character
	 *
	 * @param noun
	 *            The noun to examine
	 * @return Either "[noun]'s" or "[noun]'" as appropriate
	 */
	public static String suffix_s(String noun) {
		char last = Character.toLowerCase(noun.charAt(noun.length() - 1));
		if (last == 's') {
			return noun + "'";
		}
		return noun + "'s";
	}

	/**
	 * Returns the plural form of the given noun
	 *
	 * @param noun
	 *            The noun to examine
	 * @return An appropriate plural form
	 */
	public static String plural(String noun) {
		String enoun = fullform(noun);
		String postfix = "";
		int position = enoun.indexOf('+');
		if (position != -1) {
			if (enoun.charAt(position-1) == ' ') {
				postfix = enoun.substring(position - 1);
				enoun = enoun.substring(0, position - 1);
			} else {
				postfix = enoun.substring(position);
				enoun = enoun.substring(0, position);
			}
		}
		try {
			// in "of"-phrases pluralize only the first part
			if (enoun.indexOf(" of ") > -1) {
				return plural(enoun.substring(0, enoun.indexOf(" of ")))
						+ enoun.substring(enoun.indexOf(" of ")) + postfix;

				// first of all handle words which do not change
			} else if (enoun.endsWith("sheep") || enoun.endsWith("money")
					|| enoun.endsWith("dice") || enoun.equals("deer")) {
				return enoun + postfix;

				// ok and now all the special cases
			} else if (enoun.endsWith("staff") || enoun.endsWith("chief")) {
				return enoun + "s" + postfix;
			} else if (enoun.endsWith("f")
					&& enoun.length() > 1
					&& ("aeiourl".indexOf(enoun.charAt(enoun.length() - 2)) > -1)) {
				return enoun.substring(0, enoun.length() - 1) + "ves" + postfix;
			} else if (enoun.endsWith("fe")) {
				return enoun.substring(0, enoun.length() - 2) + "ves" + postfix;
			} else if (enoun.endsWith("ouse")
					&& ("mMlL".indexOf(enoun.charAt(enoun.length() - 5)) > -1)) {
				return enoun.substring(0, enoun.length() - 4) + "ice" + postfix;
			} else if (enoun.endsWith("oose") && !(enoun.endsWith("caboose"))) {
				return enoun.substring(0, enoun.length() - 4) + "eese"
						+ postfix;
			} else if (enoun.endsWith("ooth")) {
				return enoun.substring(0, enoun.length() - 4) + "eeth"
						+ postfix;
			} else if (enoun.endsWith("foot")) {
				return enoun.substring(0, enoun.length() - 4) + "feet"
						+ postfix;
			} else if (enoun.endsWith("child")) {
				return enoun + "ren" + postfix;
			} else if (enoun.endsWith("eau")) {
				return enoun + "x" + postfix;
			} else if (enoun.endsWith("ato")) {
				return enoun + "es" + postfix;
			} else if (enoun.endsWith("ium")) {
				return enoun.substring(0, enoun.length() - 3) + "a" + postfix;
			} else if (enoun.endsWith("alga") || enoun.endsWith("hypha")
					|| enoun.endsWith("larva")) {
				return enoun + "e" + postfix;
			} else if ((enoun.length() > 3) && enoun.endsWith("us")
					&& !(enoun.endsWith("lotus") || enoun.endsWith("wumpus"))) {
				return enoun.substring(0, enoun.length() - 2) + "i" + postfix;
			} else if (enoun.endsWith("man")
					&& !(enoun.endsWith("shaman") || enoun.endsWith("human"))) {
				return enoun.substring(0, enoun.length() - 3) + "men" + postfix;
			} else if (enoun.endsWith("rtex")) {
				return enoun.substring(0, enoun.length() - 2) + "ices"
						+ postfix;
			} else if (enoun.endsWith("trix")) {
				return enoun.substring(0, enoun.length() - 1) + "ces" + postfix;
			} else if (enoun.endsWith("sis")) {
				return enoun.substring(0, enoun.length() - 2) + "es" + postfix;
				/*
				 * } else if (enoun.endsWith("erinys") ||
				 * enoun.endsWith("cyclops")) { return enoun.substring(0,
				 * enoun.length() - 2) + "es" + postfix;
				 */
			} else if (enoun.endsWith("mumak")) {
				return enoun + "il" + postfix;
			} else if (enoun.endsWith("djinni") || enoun.endsWith("efreeti")) {
				return enoun.substring(0, enoun.length() - 1) + postfix;
			} else if (enoun.endsWith("ch") || enoun.endsWith("sh")
					|| ("zxs".indexOf(enoun.charAt(enoun.length() - 1)) > -1)) {
				return enoun + "es" + postfix;
			} else if (enoun.endsWith("y") && enoun.length() > 1
					&& consonant_p(enoun.charAt(enoun.length() - 2))) {
				return enoun.substring(0, enoun.length() - 1) + "ies" + postfix;
			} else if (enoun.endsWith("porcini") || (enoun.endsWith("porcino"))) {
				return enoun.substring(0, enoun.length() - 1) + "i" + postfix;
			} else {
				// no special case matched, so use the boring default plural
				// rule
				return enoun + "s" + postfix;
			}
		} catch (StringIndexOutOfBoundsException e) {
			logger.warn("Cannot pluralize noun " + enoun, e);
			return enoun + postfix;
		}
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending
	 * on the quantity
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @return Either "[noun]" or plural("[noun]") as appropriate
	 */
	public static String plnoun(int quantity, String noun) {
		String enoun = fullform(noun);
		return (quantity == 1 ? enoun : plural(noun));
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending
	 * on the quantity; also prefixes the quantity
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @return Either "[quantity] [noun]" or "[quantity]" + plural("[noun]") as
	 *         appropriate
	 */
	public static String quantityplnoun(int quantity, String noun) {
		return "" + quantity + " " + plnoun(quantity, noun);
	}

	/**
	 * Is the character a vowel?
	 *
	 * @param c
	 *            The character to examine
	 * @return true if c is a vowel, false otherwise
	 */
	protected static boolean vowel_p(char c) {
		char l = Character.toLowerCase(c);
		return ((l == 'a') || (l == 'e') || (l == 'i') || (l == 'o') || (l == 'u'));
	}

	/**
	 * Is the character a consonant?
	 *
	 * @param c
	 *            The character to examine
	 * @return true if c is a consonant, false otherwise
	 */
	protected static boolean consonant_p(char c) {
		return !vowel_p(c);
	}

	/**
	 * first, second, third, ...
	 *
	 * @param n
	 *            a number
	 * @return first, second, third, ...
	 */
	public static String ordered(int n) {
		switch (n) {
		case 1:
			return "first";
		case 2:
			return "second";
		case 3:
			return "third";
		default:
			logger.error("Grammar.ordered not implemented for: " + n);
			return Integer.toString(n);

		}
	}

	/**
	 * Helper function to nicely formulate an enumeration of a collection. For
	 * example, for a collection containing the 3 elements x, y, z, returns the
	 * string "x, y, and z".
	 *
	 * @param collection
	 *            The collection whose elements should be enumerated
	 * @return A nice String representation of the collection
	 */
	public static String enumerateCollection(Collection<String> collection) {
		if (collection == null) {
			return "";
		}
		String[] elements = collection.toArray(new String[collection.size()]);
		if (elements.length == 0) {
			return "";
		} else if (elements.length == 1) {
			return elements[0];
		} else if (elements.length == 2) {
			return elements[0] + " and " + elements[1];
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < elements.length - 1; i++) {
				sb.append(elements[i] + ", ");
			}
			sb.append("and " + elements[elements.length - 1]);
			return sb.toString();
		}
	}
}
