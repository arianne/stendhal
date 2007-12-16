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
 * Helper functions for producing and parsing grammatically-correct sentences.
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
	 * Modify a word to upper case notation
	 * 
	 * @param word
	 * @return word with first letter in upper case
	 */
	public static String makeUpperCaseWord(String word) {
		StringBuilder res = new StringBuilder();
		if (word.length() > 0) {
			res.append(Character.toUpperCase(word.charAt(0)));
			if (word.length() > 1) {
				res.append(word.substring(1));
			}
		}
		return res.toString();
	}

	/**
	 * "It" or "Them", depending on the quantity
	 * 
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "It" or "Them" as appropriate
	 */
	public static String ItThem(int quantity) {
		return makeUpperCaseWord(itthem(quantity));
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
		return makeUpperCaseWord(itthey(quantity));
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
		return makeUpperCaseWord(isare(quantity));
	}

	/**
	 * Prefixes a noun with an article.
	 * 
	 * @param noun
	 *            noun
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
		String enoun = fullForm(noun);
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
	 * @param prefixSingular
	 *            prefix to add
	 * @param prefixPlural
	 *            prefix, that may be present in plural form
	 * @return noun starting with prefix
	 */
	private static String addPrefixIfNotAlreadyThere(String noun,
			String prefixSingular, String prefixPlural) {
		if (noun.startsWith(prefixSingular)) {
			return noun;
		} else if (noun.startsWith(prefixPlural)) {
			return noun;
		} else {
			return prefixSingular + noun;
		}
	}

	/**
	 * prefix a noun with an expression like "piece of"
	 * 
	 * @param noun
	 * @return
	 */
	public static String fullForm(String noun) {
		String result;
		String lowString = noun.toLowerCase();
		result = lowString.replace("#", "");
		if (result.equals("meat") || result.equals("ham")
				|| result.equals("cheese") || result.equals("wood")
				|| result.equals("paper") || result.equals("iron")
				|| result.equals("chicken")) {
			result = addPrefixIfNotAlreadyThere(lowString, "piece of ",
					"pieces of ");
		} else if (result.endsWith(" ore") || result.endsWith("_ore")) {
			result = addPrefixIfNotAlreadyThere(lowString, "nugget of ",
					"nuggets of ");
		} else if (result.equals("flour")) {
			result = addPrefixIfNotAlreadyThere(lowString, "sack of ",
					"sacks of ");
		} else if (result.equals("grain")) {
			result = addPrefixIfNotAlreadyThere(lowString, "sheaf of ",
					"sheaves of ");
		} else if (result.equals("bread")) {
			result = addPrefixIfNotAlreadyThere(lowString, "loaf of ",
					"loaves of ");
		} else if (result.equals("beer") || result.equals("wine")
				|| result.endsWith("poison") || result.equals("antidote")) {
			result = addPrefixIfNotAlreadyThere(lowString, "bottle of ",
					"bottles of ");
		} else if (result.equals("money")) {
			// TODO: fix this (going back to money as workaround because /drop 1
			// coin does not work
			// enoun = "coin";
		} else if (result.startsWith("book_") || result.startsWith("book ")) {
			result = result.substring(5) + " book";
		} else if (result.equals("arandula")) {
			result = addPrefixIfNotAlreadyThere(lowString, "sprig of ",
					"sprigs of ");
		} else if ((result.indexOf("_armor") > -1)
				|| (result.indexOf(" armor") > -1)) {
			result = addPrefixIfNotAlreadyThere(lowString, "suit of ",
					"suits of ");
		} else if (result.endsWith("_legs") || result.endsWith(" legs")
				|| result.endsWith("_boots") || result.endsWith(" boots")) {
			result = addPrefixIfNotAlreadyThere(lowString, "pair of ",
					"pairs of ");
		} else {
			result = lowString;
		}

		return result;
	}

	/**
	 * remove a prefix if present
	 * 
	 * @param prefix
	 * @param noun
	 * @return object name without prefix
	 */
	private static String removePrefix(String noun, String prefix) {
		if (noun.startsWith(prefix)) {
			return noun.substring(prefix.length());
		} else {
			return noun;
		}
	}

	/**
	 * extract noun from a string, that may be prefixed with a singular
	 * expression like "piece of", ...
	 * 
	 * @param noun
	 * @return
	 */
	private static String extractNounSingular(String expr) {
		String result;

		result = removePrefix(expr, "piece of ");
		result = removePrefix(result, "nugget of ");
		result = removePrefix(result, "sack of ");
		result = removePrefix(result, "sheaf of ");
		result = removePrefix(result, "loaf of ");
		result = removePrefix(result, "bottle of ");
		result = removePrefix(result, "sprig of ");
		result = removePrefix(result, "suit of ");
		result = removePrefix(result, "pair of ");

		return result;
	}

	/**
	 * extract noun from a string, that may be prefixed with a plural expression
	 * like "piece of", ...
	 * 
	 * @param expr
	 * @return
	 */
	private static String extractNounPlural(String expr) {
		String result;

		result = removePrefix(expr, "pieces of ");
		result = removePrefix(result, "nuggets of ");
		result = removePrefix(result, "sacks of ");
		result = removePrefix(result, "sheaves of ");
		result = removePrefix(result, "loaves of ");
		result = removePrefix(result, "bottles of ");
		result = removePrefix(result, "sprigs of ");
		result = removePrefix(result, "suits of ");
		result = removePrefix(result, "pairs of ");

		return result;
	}

	/**
	 * extract noun from a string, that may be prefixed with a plural expression
	 * like "piece of", ... So this function is just the recursive counter part
	 * to fullForm().
	 * 
	 * @param expr
	 * @return
	 */
	public static String extractNoun(String expr) {
		if (expr == null) {
			return expr;
		}

		String lastExpr, result = expr;

		// loop until all prefix strings are removed
		do {
			lastExpr = result;
			result = extractNounSingular(result);
			result = extractNounPlural(result);
		} while (result != lastExpr);

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
		return makeUpperCaseWord(a_noun(noun));
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
	 * Returns the plural form of the given noun if not already given in plural
	 * form
	 * 
	 * @param noun
	 *            The noun to examine
	 * @return An appropriate plural form
	 */
	public static String plural(String noun) {
		String enoun = fullForm(noun);
		String postfix = "";
		int position = enoun.indexOf('+');
		if (position != -1) {
			if (enoun.charAt(position - 1) == ' ') {
				postfix = enoun.substring(position - 1);
				enoun = enoun.substring(0, position - 1);
			} else {
				postfix = enoun.substring(position);
				enoun = enoun.substring(0, position);
			}
		}

		// in "of"-phrases pluralize only the first part
		if (enoun.indexOf(" of ") > -1) {
			return plural(enoun.substring(0, enoun.indexOf(" of ")))
					+ enoun.substring(enoun.indexOf(" of ")) + postfix;

			// first of all handle words which do not change
		} else if (enoun.endsWith("money") || enoun.endsWith("dice")
				|| enoun.endsWith("sheep") || enoun.equals("deer")
				|| enoun.equals("moose")) {
			return enoun + postfix;

			// ok and now all the special cases
		} else if (enoun.endsWith("staff") || enoun.endsWith("chief")) {
			return enoun + "s" + postfix;
		} else if (enoun.length() > 2 && enoun.endsWith("f")
				&& ("aeiourl".indexOf(enoun.charAt(enoun.length() - 2)) > -1)) {
			return enoun.substring(0, enoun.length() - 1) + "ves" + postfix;
		} else if (enoun.endsWith("fe")) {
			return enoun.substring(0, enoun.length() - 2) + "ves" + postfix;
		} else if (enoun.length() >= 4 && enoun.endsWith("ouse")
				&& ("mMlL".indexOf(enoun.charAt(enoun.length() - 5)) > -1)) {
			return enoun.substring(0, enoun.length() - 4) + "ice" + postfix;
		} else if (enoun.endsWith("oose") && !enoun.endsWith("caboose")
				&& !enoun.endsWith("noose")) {
			return enoun.substring(0, enoun.length() - 4) + "eese" + postfix;
		} else if (enoun.endsWith("ooth")) {
			return enoun.substring(0, enoun.length() - 4) + "eeth" + postfix;
		} else if (enoun.endsWith("foot")) {
			return enoun.substring(0, enoun.length() - 4) + "feet" + postfix;
		} else if (enoun.endsWith("child")) {
			return enoun + "ren" + postfix;
		} else if (enoun.endsWith("eau")) {
			return enoun + "x" + postfix;
		} else if (enoun.endsWith("ato")) {
			return enoun + "es" + postfix;
		} else if (enoun.endsWith("ium")) {
			return enoun.substring(0, enoun.length() - 2) + "a" + postfix;
		} else if (enoun.endsWith("alga") || enoun.endsWith("hypha")
				|| enoun.endsWith("larva")) {
			return enoun + "e" + postfix;
		} else if ((enoun.length() > 3) && enoun.endsWith("us")
				&& !(enoun.endsWith("lotus") || enoun.endsWith("wumpus"))) {
			return enoun.substring(0, enoun.length() - 2) + "i" + postfix;
		} else if (enoun.endsWith("man")
				&& !(enoun.endsWith("shaman") || enoun.endsWith("human"))) {
			return enoun.substring(0, enoun.length() - 3) + "men" + postfix;
		} else if (enoun.endsWith("rtex") || enoun.endsWith("index")) {
			return enoun.substring(0, enoun.length() - 2) + "ices" + postfix;
		} else if (enoun.endsWith("trix")) {
			return enoun.substring(0, enoun.length() - 1) + "ces" + postfix;
		} else if (enoun.endsWith("sis")) {
			return enoun.substring(0, enoun.length() - 2) + "es" + postfix;
		} else if (enoun.endsWith("erinys")) { // || enoun.endsWith("cyclops")
			return enoun.substring(0, enoun.length() - 1) + "es" + postfix;
		} else if (enoun.endsWith("mumak")) {
			return enoun + "il" + postfix;
		} else if (enoun.endsWith("djinni") || enoun.endsWith("efreeti")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if (enoun.endsWith("porcini") || (enoun.endsWith("porcino"))) {
			return enoun.substring(0, enoun.length() - 1) + "i" + postfix;
		} else if (enoun.length() > 2 && enoun.endsWith("y")
				&& consonant_p(enoun.charAt(enoun.length() - 2))) {
			return enoun.substring(0, enoun.length() - 1) + "ies" + postfix;

			// If the word is already in plural form, return it unchanged.
		} else if (!singular(enoun).equals(enoun)) {
			return enoun + postfix;

			// last special case: Does the word end with "ch", "sh", "s", "x"
			// oder "z"?
		} else if (enoun.endsWith("ch")
				|| enoun.endsWith("sh")
				|| (enoun.length() > 1 && "sxz".indexOf(enoun.charAt(enoun.length() - 1)) > -1)) {
			return enoun + "es" + postfix;
		} else {
			// no special case matched, so use the boring default plural rule
			return enoun + "s" + postfix;
		}
	}

	/**
	 * Returns the singular form of the given noun if not already given in
	 * singular form
	 * 
	 * @param enoun
	 *            The noun to examine
	 * @return An appropriate singular form
	 */
	public static String singular(String enoun) {
		String postfix = "";

		int position = enoun.indexOf('+');
		if (position != -1) {
			postfix = enoun.substring(position - 1);
			enoun = enoun.substring(0, position - 1);
		}

		// in "of"-phrases build only the singular of the first part
		if (enoun.indexOf(" of ") > -1) {
			return singular(enoun.substring(0, enoun.indexOf(" of ")))
					+ enoun.substring(enoun.indexOf(" of ")) + postfix;

			// first of all handle words which do not change
		} else if (enoun.endsWith("money") || enoun.endsWith("dice")
				|| enoun.endsWith("porcini") || enoun.endsWith("sheep")
				|| enoun.equals("deer") || enoun.equals("moose")) {
			return enoun + postfix;

			// now all the special cases
		} else if (enoun.endsWith("staffs") || enoun.endsWith("chiefs")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if (enoun.length() > 4 && enoun.endsWith("ves")
				&& ("aeiourl".indexOf(enoun.charAt(enoun.length() - 4)) > -1)
				&& !enoun.endsWith("knives")) {
			return enoun.substring(0, enoun.length() - 3) + "f" + postfix;
		} else if (enoun.endsWith("ves")) {
			return enoun.substring(0, enoun.length() - 3) + "fe" + postfix;
		} else if (enoun.endsWith("houses")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if (enoun.length() > 3 && enoun.endsWith("ice")
				&& ("mMlL".indexOf(enoun.charAt(enoun.length() - 4)) > -1)) {
			return enoun.substring(0, enoun.length() - 3) + "ouse" + postfix;
		} else if (enoun.endsWith("eese") && !enoun.endsWith("cabeese")) {
			return enoun.substring(0, enoun.length() - 4) + "oose" + postfix;
		} else if (enoun.endsWith("eeth")) {
			return enoun.substring(0, enoun.length() - 4) + "ooth" + postfix;
		} else if (enoun.endsWith("feet")) {
			return enoun.substring(0, enoun.length() - 4) + "foot" + postfix;
		} else if (enoun.endsWith("children")) {
			return enoun.substring(0, enoun.length() - 3) + postfix;
		} else if (enoun.endsWith("eaux")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if (enoun.endsWith("atoes")) {
			return enoun.substring(0, enoun.length() - 2) + postfix;
		} else if (enoun.endsWith("ia") || enoun.endsWith("ia")) { // enoun.endsWith("helia")
			// ||
			// enoun.endsWith("sodia")
			return enoun.substring(0, enoun.length() - 1) + "um" + postfix;
		} else if (enoun.endsWith("algae") || enoun.endsWith("hyphae")
				|| enoun.endsWith("larvae")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if ((enoun.length() > 2) && enoun.endsWith("ei")) {
			return enoun.substring(0, enoun.length() - 1) + "us" + postfix;
		} else if (enoun.endsWith("men")) {
			return enoun.substring(0, enoun.length() - 3) + "man" + postfix;
		} else if (enoun.endsWith("matrices")) {
			return enoun.substring(0, enoun.length() - 4) + "ix" + postfix;
		} else if (enoun.endsWith("ices")) { // indices, vertices, ...
			return enoun.substring(0, enoun.length() - 4) + "ex" + postfix;
		} else if (enoun.endsWith("erinyes")) { // || enoun.endsWith("cyclopes")
			return enoun.substring(0, enoun.length() - 2) + "s" + postfix;
		} else if (enoun.endsWith("erinys") || enoun.endsWith("cyclops")) {
			return enoun + postfix; // singular detected
		} else if (enoun.endsWith("mumakil")) {
			return enoun.substring(0, enoun.length() - 2) + postfix;
		} else if (enoun.endsWith("djin")) {
			return enoun + "ni" + postfix;
		} else if (enoun.endsWith("djinn") || enoun.endsWith("efreet")) {
			return enoun + "i" + postfix;
		} else if (enoun.endsWith("lotus") || enoun.endsWith("wumpus")
				|| enoun.endsWith("deus")) {
			return enoun + postfix;
		} else if (enoun.endsWith("cabooses")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if (enoun.endsWith("yses") || enoun.endsWith("ysis")) {
			return enoun.substring(0, enoun.length() - 2) + "is" + postfix;
		} else if (enoun.length() > 3
				&& enoun.endsWith("es")
				&& (("zxs".indexOf(enoun.charAt(enoun.length() - 3)) > -1) || (enoun.endsWith("ches") || enoun.endsWith("shes")))
				&& !enoun.endsWith("axes") && !enoun.endsWith("bardiches")
				&& !enoun.endsWith("nooses")) {
			return enoun.substring(0, enoun.length() - 2) + postfix;
		} else if (enoun.length() > 4 && enoun.endsWith("ies")
				&& consonant_p(enoun.charAt(enoun.length() - 4))
				&& !enoun.endsWith("zombies")) {
			return enoun.substring(0, enoun.length() - 3) + "y" + postfix;
			// no special case matched, so look for the standard "s" plural
		} else if (enoun.endsWith("s") && !enoun.endsWith("ss")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else {
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
		String enoun = fullForm(noun);
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

	/**
	 * convert numbers into their textual representation
	 * 
	 * @param n
	 *            a number
	 * @return one, two, three, ...
	 */
	public static String numberString(int n) {
		switch (n) {
		case 0:
			return "no";
		case 1:
			return "one";
		case 2:
			return "two";
		case 3:
			return "three";
		case 4:
			return "four";
		case 5:
			return "five";
		case 6:
			return "six";
		case 7:
			return "seven";
		case 8:
			return "eight";
		case 9:
			return "nine";
		case 10:
			return "ten";
		case 11:
			return "eleven";
		case 12:
			return "twelve";
		default:
			logger.error("Grammar.ordered not implemented for: " + n);
			return Integer.toString(n);
		}
	}

	/**
	 * interpret number texts
	 * 
	 * @param n
	 *            a number
	 * @return one, two, three, ...
	 */
	public static Integer number(String text) {
		if (text.equals("no") || text.equals("zero")) {
			return 0;
		}
		if (text.equals("a") || text.equals("an")) {
			return 1;
		}
		if (text.equals("one")) {
			return 1;
		}
		if (text.equals("two")) {
			return 2;
		}
		if (text.equals("three")) {
			return 3;
		}
		if (text.equals("four")) {
			return 4;
		}
		if (text.equals("five")) {
			return 5;
		}
		if (text.equals("six")) {
			return 6;
		}
		if (text.equals("seven")) {
			return 7;
		}
		if (text.equals("eight")) {
			return 8;
		}
		if (text.equals("nine")) {
			return 9;
		}
		if (text.equals("ten")) {
			return 10;
		}
		if (text.equals("eleven")) {
			return 11;
		}
		if (text.equals("twelve")) {
			return 12;
		} else {
			// also handle "a dozen", ...
			return null;
		}
	}

	/**
	 * decide if the given word is a preposition
	 * 
	 * @param word
	 * @return true if word is a preposition
	 */
	public static boolean isPreposition(String word) {
		// TODO handle more prepositions
		return word.equals("on") || word.equals("of") || word.equals("under")
				|| word.equals("with");
	}

	/**
	 * decide if the given word is a subject
	 * 
	 * TODO May be we should also handle the remaining subjective pronouns
	 * "what", "who" and "they".
	 * 
	 * @param word
	 * @return true if word is a subject
	 */
	public static boolean isSubject(String word) {
		return word.equals("i") || word.equals("we") || word.equals("you")
				|| word.equals("he") || word.equals("she") || word.equals("it");
	}
}
