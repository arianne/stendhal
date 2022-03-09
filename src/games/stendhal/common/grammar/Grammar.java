/***************************************************************************
 *                    (C) Copyright 2009-2015 - Stendhal                    *
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Expression;

/**
 * Helper functions for producing and parsing grammatically-correct sentences.
 */
public class Grammar {

	private static final Logger logger = Logger.getLogger(Grammar.class);

	// static instance
	private static Grammar instance;

	// nouns beginning with hard "u" sound
	// TODO: parse this list from an external text file
	private static final List<String> hard_u_nouns = Arrays.asList("unicorn");


	public static Grammar get() {
		if (instance == null) {
			instance = new Grammar();
		}

		return instance;
	}

	/**
	 * "it" or "them", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "it" or "them" as appropriate
	 */
	public static String itthem(final int quantity) {
		if (quantity == 1) {
			return "it";
		} else {
			return "them";
		}
	}

	/**
	 * Modify a word to upper case notation.
	 *
	 * @param word
	 * @return word with first letter in upper case
	 */
	public static String makeUpperCaseWord(final String word) {
		final StringBuilder res = new StringBuilder();
		if (word.length() > 0) {
			res.append(Character.toUpperCase(word.charAt(0)));
			if (word.length() > 1) {
				res.append(word.substring(1));
			}
		}
		return res.toString();
	}

	/**
	 * "It" or "Them", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "It" or "Them" as appropriate
	 */
	public static String ItThem(final int quantity) {
		return makeUpperCaseWord(itthem(quantity));
	}

	/**
	 * "it" or "they", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "it" or "they" as appropriate
	 */
	public static String itthey(final int quantity) {
		if (quantity == 1) {
			return "it";
		} else {
			return "they";
		}
	}

	/**
	 * "It" or "They", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "It" or "They" as appropriate
	 */
	public static String ItThey(final int quantity) {
		return makeUpperCaseWord(itthey(quantity));
	}

	/**
	 * "is" or "are", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "is" or "are" as appropriate
	 */
	public static String isare(final int quantity) {
		if (quantity == 1) {
			return "is";
		} else {
			return "are";
		}
	}

	/**
	 * "Is" or "Are", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "Is" or "Are" as appropriate
	 */
	public static String IsAre(final int quantity) {
		return makeUpperCaseWord(isare(quantity));
	}

	/**
	 * "has" or "have", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "has" or "have" as appropriate
	 */
	public static String hashave(final int quantity) {
		if (quantity == 1) {
			return "has";
		} else {
			return "have";
		}
	}

	/**
	 * "Has" or "Have", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "Has" or "Have" as appropriate
	 */
	public static String HasHave(final int quantity) {
		return makeUpperCaseWord(hashave(quantity));
	}

	/**
	 * "that" or "those", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "that" or "those" as appropriate
	 */
	public static String thatthose(final int quantity) {
		if (quantity == 1) {
			return "that";
		} else {
			return "those";
		}
	}

	/**
	 * "That or "Those", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "That" or "Those" as appropriate
	 */
	public static String ThatThose(final int quantity) {
		return makeUpperCaseWord(thatthose(quantity));
	}

	/**
	 * "this" or "these", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "this" or "these" as appropriate
	 */
	public static String thisthese(final int quantity) {
		if (quantity == 1) {
			return "this";
		} else {
			return "these";
		}
	}

	/**
	 * "This or "These", depending on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @return Either "This" or "These" as appropriate
	 */
	public static String ThisThese(final int quantity) {
		return makeUpperCaseWord(thisthese(quantity));
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
	public static String article_noun(final String noun, final boolean definite) {
		if (definite) {
			return "the " + noun;
		} else {
			return a_noun(noun);
		}
	}

	/**
	 * "a [noun]" or "an [noun]", depending on the first syllable.
	 *
	 * @param noun
	 *            The noun to examine
	 * @return Either "a [noun]" or "an [noun]" as appropriate
	 */
	public static String a_noun(final String noun) {
		if (noun == null) {
			return null;
		}
		final String enoun = fullForm(noun);
		return a_an(enoun) + enoun;
	}

	/**
	 * "a [noun]" or "an [noun]", depending on the first syllable.
	 * Method to prevent collision of items and creatures.
	 *
	 * @param noun
	 *            The noun to examine
	 * @return Either "a [noun]" or "an [noun]" as appropriate
	 */
	public static String a_nounCreature(final String noun) {
		if (noun.equals("chicken")) {
			return "a chicken";
		}

		return a_noun(noun);
	}

	/**
	 * "a " or "an ", depending on the noun
	 *
	 * @param noun the noun to be examined
	 * @return either "a " or "an " as appropriate
	 */
	private static String a_an(final String noun) {
		String lowerCaseString = noun.toLowerCase();
		final char initial;
		if (lowerCaseString.length() == 0) {
			return "a ";
		} else {
			initial = lowerCaseString.charAt(0);
		}

		if (lowerCaseString.length()==1){
			if (isVowel(initial) && initial != 'u') {
				return "an ";
			} else {
				return "a ";
			}
		}


		if ("eu".equals(lowerCaseString.substring(0, 2))
				|| (initial == 'u' && startsWithHardU(lowerCaseString))) {
			return "a ";
		}
		if (isVowel(initial)) {
			return "an ";
		}
		if ((initial == 'y') && isConsonant(lowerCaseString.charAt(1))) {
			return "an ";
		}

		return "a ";
	}

	/**
	 * Compares word against a hard-coded static list of nouns that
	 * begin with a hard "u" sound.
	 *
	 * @param lowerCaseString
	 *     String to be checked.
	 * @return
	 *     <code>true</code> if the first word is found in the list.
	 */
	private static boolean startsWithHardU(String lowerCaseString) {
		// only concerned with first word
		if (lowerCaseString.contains(" ")) {
			lowerCaseString = lowerCaseString.split(" ")[0];
		}

		return hard_u_nouns.contains(lowerCaseString);
	}

	/**
	 * Adds a prefix unless it was already added.
	 *
	 * @param noun
	 *            the noun (which may already start with the specified prefix
	 * @param prefixSingular
	 *            prefix to add
	 * @param prefixPlural
	 *            prefix, that may be present in plural form
	 * @return noun starting with prefix
	 */
	static String addPrefixIfNotAlreadyThere(final String noun,
			final String prefixSingular, final String prefixPlural) {
		if (noun.startsWith(prefixSingular)) {
			return noun;
		} else if (noun.startsWith(prefixPlural)) {
			return noun;
		} else {
			return prefixSingular + noun;
		}
	}

	/**
	 * Prefix a noun with an expression like "piece of".
	 *
	 * @param noun
	 * @return noun with prefix
	 */
	public static String fullForm(final String noun) {
		final String lowString = noun.toLowerCase(Locale.ENGLISH);
		String str = lowString.replace("#", "");

		if (str.startsWith("book ")) {
			str = str.substring(5) + " book";
		} else if (str.indexOf(" armor") > -1) {
			str = addPrefixIfNotAlreadyThere(lowString, "suit of ", "suits of ");
		} else {
			str = replaceInternalByDisplayNames(PrefixManager.s_instance.fullForm(str, lowString));
		}

		return str;
	}

	/**
	 * Replace internal item names bye their display name.
	 * @param str
	 * @return fixed string
	 */
	public static String replaceInternalByDisplayNames(final String str) {
		return str.
			replace("icecream", "ice cream");
	}

	/**
	 * Merge two expressions into a compound noun.
	 * @param word1
	 * @param word2
	 * @return resulting expression: word1 or word2
	 */

	public static Expression mergeCompoundNoun(final Expression word1, Expression word2) {
		// handle special cases:
				// "ice cream" -> "ice"
		if ((word1.getMainWord().equals("ice") && word2.getMainWord().equals("cream")) ||
				// "teddy bear" -> "teddy"
				(word1.getMainWord().equals("teddy") && word2.getMainWord().equals("bear"))) {

		    // transform "ice cream" into the item name "icecream"
		    if (word1.getMainWord().equals("ice")) {
		    	word1.setNormalized("icecream");
		    }

		    return word1;
        } else {
            word2.mergeLeft(word1, true);

            return word2;
        }
	}

	/**
	 * Extracts noun from a string, that may be prefixed with a plural expression
	 * like "piece of", ... So this function is just the counter part to fullForm().
	 *
	 * @param text
	 * @return the extracted noun
	 */
	public static String extractNoun(final String text) {
		String result;

		if (text == null) {
			result = null;
		} else {
			final PrefixExtractor extractor = new PrefixExtractor(text);
			boolean changed;

			// loop until all prefix strings are removed
			do {
				changed = false;

				if (extractor.extractNounSingular()) {
					changed = true;
				}

				if (extractor.extractNounPlural()) {
					changed = true;
				}
			} while(changed);

			result = extractor.toString();
		}

		return result;
	}

	/**
	 * Check if an expression is normalized.
	 * equivalent to: {return extractNoun(text) == text}
	 *
	 * @param text
	 * @return true if the expression is already normalized
	 */
	public static boolean isNormalized(final String text) {
		boolean ret;

		if (text == null) {
			ret = true;
		} else {
			final PrefixExtractor extractor = new PrefixExtractor(text);

			// If there is detected any prefix, the reviewed text was not normalized.
			if (extractor.extractNounSingular() || extractor.extractNounPlural()) {
				ret = false;
			} else {
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * "A [noun]" or "An [noun]", depending on the first syllable.
	 *
	 * @param noun
	 *            The noun to examine
	 * @return Either "A [noun]" or "An [noun]" as appropriate
	 */
	public static String A_noun(final String noun) {
		return makeUpperCaseWord(a_noun(noun));
	}

	/**
	 * "[noun]'s" or "[noun]'", depending on the last character.
	 *
	 * @param noun
	 *            The noun to examine
	 * @return Either "[noun]'s" or "[noun]'" as appropriate
	 */
	public static String suffix_s(final String noun) {
		final char last = Character.toLowerCase(noun.charAt(noun.length() - 1));
		if (last == 's') {
			return noun + "'";
		}
		return noun + "'s";
	}

	private static final String of = " of ";

	/**
	 * Returns the plural form of the given noun if not already given in plural
	 * form.
	 *
	 * @param noun
	 *            The noun to examine
	 * @return An appropriate plural form
	 */
	public static String plural(final String noun) {
		if (noun == null) {
			return null;
		}

		String enoun = fullForm(noun);
		String postfix = "";

		final int position = enoun.indexOf('+');
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
		if (enoun.indexOf(of) > -1) {
			return plural(enoun.substring(0, enoun.indexOf(of)))
					+ enoun.substring(enoun.indexOf(of)) + postfix;

			// first of all handle words which do not change
		} else if (enoun.endsWith("money") || enoun.endsWith("dice")
				|| enoun.endsWith("sheep") || enoun.equals("deer")
				|| enoun.equals("moose") || enoun.equals("magic")){
			return enoun + postfix;

			// ok and now all the special cases
		} else if (enoun.endsWith("staff") || enoun.endsWith("chief")) {
			return enoun + "s" + postfix;
		} else if ((enoun.length() > 2) && enoun.endsWith("f")
				&& ("aeiourl".indexOf(enoun.charAt(enoun.length() - 2)) > -1)) {
			return enoun.substring(0, enoun.length() - 1) + "ves" + postfix;
		} else if (enoun.endsWith("fe")) {
			return enoun.substring(0, enoun.length() - 2) + "ves" + postfix;
		} else if ((enoun.length() >= 4) && enoun.endsWith("ouse")
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
		} else if (enoun.equals("oni")) {
			return enoun;
		} else if (enoun.endsWith("man")
				&& !(enoun.endsWith("shaman") || enoun.endsWith("human"))) {
			return enoun.substring(0, enoun.length() - 3) + "men" + postfix;
		} else if (enoun.endsWith("rtex") || enoun.endsWith("index")) {
			return enoun.substring(0, enoun.length() - 2) + "ices" + postfix;
		} else if (enoun.endsWith("trix")) {
			return enoun.substring(0, enoun.length() - 1) + "ces" + postfix;
		} else if (enoun.endsWith("sis")) {
			return enoun.substring(0, enoun.length() - 2) + "es" + postfix;
		} else if (enoun.endsWith("erinys")) {
			return enoun.substring(0, enoun.length() - 1) + "es" + postfix;
		} else if (enoun.endsWith("mumak")) {
			return enoun + "il" + postfix;
		} else if (enoun.endsWith("djinni") || enoun.endsWith("efreeti")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if (enoun.endsWith("porcini") || enoun.endsWith("porcino")) {
			return enoun.substring(0, enoun.length() - 1) + "i" + postfix;
		} else if ((enoun.length() > 2) && enoun.endsWith("y")
				&& isConsonant(enoun.charAt(enoun.length() - 2))) {
			return enoun.substring(0, enoun.length() - 1) + "ies" + postfix;

			// If the word is already in plural form, return it unchanged.
		} else if (!singular(enoun).equals(enoun)) {
			return enoun + postfix;

			// last special case: Does the word end with "ch", "sh", "s", "x"
			// oder "z"?
		} else if (enoun.endsWith("ch")
				|| enoun.endsWith("sh")
				|| ((enoun.length() > 1) && ("sxz".indexOf(enoun.charAt(enoun.length() - 1)) > -1))) {
			return enoun + "es" + postfix;
			// German special case
		} else if (enoun.equals("glück") || enoun.equals("glücke")) {
			return "glücke";
		} else {
			// no special case matched, so use the boring default plural rule
			return enoun + "s" + postfix;
		}
	}

	/**
	 * Returns the plural form of the given noun if not already given in plural
	 * form. Method to prevent collision of items and creatures.
	 *
	 * @param noun
	 *            The noun to examine
	 * @return An appropriate plural form
	 */
	public static String pluralCreature(final String noun) {
		if (noun.equals("chicken")) {
			return "chickens";
		}

		return plural(noun);
	}

	/**
	 * Returns the singular form of the given noun if not already given in
	 * singular form.
	 *
	 * @param enoun
	 *            The noun to examine
	 * @return An appropriate singular form
	 */
	public static String singular(String enoun) {
		if (enoun == null) {
			return null;
		}

		String postfix = "";

		final int position = enoun.indexOf('+');
		if (position != -1) {
			postfix = enoun.substring(position - 1);
			enoun = enoun.substring(0, position - 1);
		}

		// in "of"-phrases build only the singular of the first part
		if (enoun.indexOf(of) > -1) {
			return singular(enoun.substring(0, enoun.indexOf(of)))
					+ enoun.substring(enoun.indexOf(of)) + postfix;

			// first of all handle words which do not change
		} else if (enoun.endsWith("money") || enoun.endsWith("dice")
				|| enoun.endsWith("sheep")
				|| enoun.endsWith("legs") || enoun.endsWith("boots")
				|| enoun.endsWith("pegasus") || enoun.endsWith("djinn")
				|| enoun.equals("deer") || enoun.equals("moose") || enoun.equals("magic")) {
			return enoun + postfix;

			// now all the special cases
		} else if (enoun.endsWith("staffs") || enoun.endsWith("chiefs")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if ((enoun.length() > 4) && enoun.endsWith("ves")
				&& ("aeiourl".indexOf(enoun.charAt(enoun.length() - 4)) > -1)
				&& !enoun.endsWith("knives")) {
			return enoun.substring(0, enoun.length() - 3) + "f" + postfix;
		} else if (enoun.endsWith("ves")) {
			return enoun.substring(0, enoun.length() - 3) + "fe" + postfix;
		} else if (enoun.endsWith("houses")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if ((enoun.length() > 3) && enoun.endsWith("ice")
				&& ("mMlL".indexOf(enoun.charAt(enoun.length() - 4)) > -1)) {
			return enoun.substring(0, enoun.length() - 3) + "ouse" + postfix;
		} else if (enoun.endsWith("eese")
				&& !enoun.endsWith("cabeese") && !enoun.endsWith("cheese")) {
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
		// don't transform "wikipedia" to "wikipedium" -> endswith("ia") is not enough
		} else if (enoun.endsWith("helia") || enoun.endsWith("sodia")) {
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
		} else if (enoun.endsWith("ices")) {
			// indices, vertices, ...
			return enoun.substring(0, enoun.length() - 4) + "ex" + postfix;
		} else if (enoun.endsWith("erinyes")) {
			return enoun.substring(0, enoun.length() - 2) + "s" + postfix;
		} else if (enoun.endsWith("erinys") || enoun.endsWith("cyclops")) {
			// singular detected
			return enoun + postfix;
		} else if (enoun.endsWith("mumakil")) {
			return enoun.substring(0, enoun.length() - 2) + postfix;
		} else if (enoun.endsWith("djin")) {
			return enoun + "ni" + postfix;
		} else if (enoun.endsWith("efreet")) {
			return enoun + "i" + postfix;
		} else if (enoun.endsWith("porcini") || enoun.endsWith("porcino")) {
			return enoun.substring(0, enoun.length() - 1) + "o" + postfix;
		} else if (enoun.endsWith("lotus") || enoun.endsWith("wumpus")
				|| enoun.endsWith("deus")) {
			return enoun + postfix;
		} else if (enoun.endsWith("cabooses")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
		} else if (enoun.endsWith("yses") || enoun.endsWith("ysis")) {
			return enoun.substring(0, enoun.length() - 2) + "is" + postfix;
		} else if ((enoun.length() > 3)
				&& enoun.endsWith("es")
				&& (("zxs".indexOf(enoun.charAt(enoun.length() - 3)) > -1) || (enoun.endsWith("ches") || enoun.endsWith("shes")))
				&& !enoun.endsWith("axes") && !enoun.endsWith("bardiches")
				&& !enoun.endsWith("nooses")) {
			return enoun.substring(0, enoun.length() - 2) + postfix;
		} else if ((enoun.length() > 4) && enoun.endsWith("ies")
				&& isConsonant(enoun.charAt(enoun.length() - 4))
				&& !enoun.endsWith("zombies")) {
			return enoun.substring(0, enoun.length() - 3) + "y" + postfix;
			// no special case matched, so look for the standard "s" plural
		} else if (enoun.endsWith("s") && !enoun.endsWith("ss")) {
			return enoun.substring(0, enoun.length() - 1) + postfix;
			// German special case
		} else if (enoun.equals("glück") || enoun.equals("glücke")) {
			return "glück";
		} else {
			return enoun + postfix;
		}
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending
	 * on the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @return Either "[noun]" or plural("[noun]") as appropriate
	 */
	public static String plnoun(final int quantity, final String noun) {
		final String enoun = fullForm(noun);
		if (quantity == 1) {
			return singular(enoun);
		} else {
			return plural(noun);
		}
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending
	 * on the quantity. Method to prevent collision of items and creatures.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @return Either "[noun]" or plural("[noun]") as appropriate
	 */
	public static String plnounCreature(final int quantity, final String noun) {
		if (noun.equals("chicken")) {
			if (quantity == 1) {
				return "chicken";
			} else {
				return "chickens";
			}
		}

		return plnoun(quantity, noun);
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending
	 * on the quantity; also prefixes the quantity.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @return Either "[quantity] [noun]" or "[quantity]" + plural("[noun]") as
	 *         appropriate
	 */
	public static String quantityplnoun(final int quantity, final String noun) {
		final String end = plnoun(quantity, noun);
		return Integer.toString(quantity) + " " + end;
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending
	 * on the quantity; also prefixes the quantity. Method to prevent
	 * collision of items and creatures
	 *
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @return Either "[quantity] [noun]" or "[quantity]" + plural("[noun]") as
	 *         appropriate
	 */
	public static String quantityplnounCreature(final int quantity, final String noun) {
		if (noun.equals("chicken")) {
			final String end = plnounCreature(quantity, noun);
			return Integer.toString(quantity) + " " + end;
		}

		return quantityplnoun(quantity, noun);
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending
	 * on the quantity; also prefixes the quantity. In case the quantity is exactly
	 * 1, the specified prefix is used. Note: There is some additional magic to convert
	 * "a" and "A" to "an" and "An" in case that is required by the noun.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @param one replacement for "1".
	 * @return Either "[quantity] [noun]" or "[quantity]" + plural("[noun]") as
	 *         appropriate
	 */
	public static String quantityplnoun(final int quantity, final String noun, final String one) {
		final String word = plnoun(quantity, noun);

		if (quantity == 1) {
			if (one.equals("a")) {
				return a_noun(word);
			} else if (one.equals("A")) {
				return A_noun(word);
			} else if (one.equals("")) {
				return word;
			} else {
				return one + " " + word;
			}
		} else {
			return Integer.toString(quantity) + " " + plural(noun);
		}
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending on
	 * the quantity; also prefixes the quantity and prints the noun with a hash prefix.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @return Either "[quantity] [noun]" or "[quantity]" + plural("[noun]") as
	 *         appropriate
	 */
	public static String quantityplnounWithHash(final int quantity, final String noun) {
		return quantityplnounWithMarker(quantity, noun, '#');
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending on
	 * the quantity; also prefixes the quantity and prints the noun with a
	 * specifier prefix.
	 *
	 * @param quantity The quantity to examine
	 * @param noun The noun to examine
	 * @param marker The character use for the markup. '#' or '§'
	 * @return Either "[quantity] [noun]" or "[quantity]" + plural("[noun]") as
	 *         appropriate
	 */
	public static String quantityplnounWithMarker(int quantity, String noun, char marker) {
		final String fullNoun = plnoun(quantity, noun);
		String prefix;
		if (quantity == 1) {
			prefix = a_an(fullNoun);
		} else {
			prefix = Integer.toString(quantity) + " ";
		}
		final StringBuilder sb = new StringBuilder(prefix);

		if (fullNoun.indexOf(' ') == -1) {
			sb.append(marker);
			sb.append(fullNoun);
		} else {
			sb.append(marker);
			sb.append("'" + fullNoun + "'");
		}

		return sb.toString();
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending
	 * on the quantity; also prefixes the quantity as number string, if appropriate.
	 *
	 * @param quantity
	 *            The quantity to examine
	 * @param noun
	 *            The noun to examine
	 * @return Either "[quantity string] [noun]" or "[quantity string]" + plural("[noun]") as
	 *         appropriate
	 */
	public static String quantityNumberStrNoun(final int quantity, final String noun) {
		StringBuilder sb = new StringBuilder();

		switch(quantity) {
			case 0:
				sb.append("0 ");
				break;

			case 1:
				// skip quantity string
				break;

			default:
				sb.append(numberString(quantity)).append(' ');
				break;
		}

		sb.append(plnoun(quantity, noun));

		return sb.toString();
	}

	/**
	 * Is the character a vowel?
	 *
	 * @param c
	 *            The character to examine
	 * @return true if c is a vowel, false otherwise
	 */
	protected static boolean isVowel(final char c) {
		final char l = Character.toLowerCase(c);
		return ((l == 'a') || (l == 'e') || (l == 'i') || (l == 'o') || (l == 'u'));
	}

	/**
	 * Is the character a consonant?
	 *
	 * @param c
	 *            The character to examine
	 * @return true if c is a consonant, false otherwise
	 */
	protected static boolean isConsonant(final char c) {
		return !isVowel(c);
	}

	/**
	 * first, second, third, ...
	 *
	 * @param n
	 *            a number
	 * @return first, second, third, ...
	 */
	public static String ordered(final int n) {
		switch (n) {
		case 1:	return "first";
		case 2:	return "second";
		case 3:	return "third";
		case 4:	return "fourth";
		case 5:	return "fifth";
		case 6:	return "sixth";
		case 7:	return "seventh";
		case 8:	return "eighth";
		case 9:	return "ninth";
		case 10:return "tenth";
		default:
			if (n > 0) {
				return n + ordinalSuffix(n);
			}
			logger.error("Grammar.ordered not implemented for: " + n);
			return Integer.toString(n);
		}
	}

	/**
	 * Get ordinal suffix string corresponding to an integer.
	 *
	 * @param n integer whose ordinal's suffix is wanted
	 * @return ordinal suffix
	 */
	private static String ordinalSuffix(int n) {
		int penultimate = (n % 100) / 10;
		if (penultimate == 1) {
			return "th";
		}
		int last = n % 10;
		if (last == 1) {
			return "st";
		} else if (last == 2) {
			return "nd";
		} else if (last == 3) {
			return "rd";
		}
		return "th";
	}

	/**
	 * Helper function to nicely formulate an enumeration of a collection.
	 * <p>
	 * For example, for a collection containing the 3 elements x, y, z, returns the
	 * string "x, y, and z".
	 *
	 * @param collection
	 *            The collection whose elements should be enumerated
	 * @return A nice String representation of the collection
	 */
	public static String enumerateCollection(final Collection<String> collection) {
		return enumerateCollection(collection, "and");
	}
		/**
		 * Helper function to nicely formulate an enumeration of a collection.
		 * <p>
		 * For example, for a collection containing the 3 elements x, y, z, returns the
		 * string "x, y, and z".
		 *
		 * @param collection
		 *            The collection whose elements should be enumerated
		 * @param conjunction "and" or "or"
		 * @return A nice String representation of the collection
		 */
	public static String enumerateCollection(final Collection<String> collection, String conjunction) {
		if (collection == null) {
			return "";
		}
		final String[] elements = collection.toArray(new String[collection.size()]);
		String ret;

		if (elements.length == 0) {
			ret = "";
		} else if (elements.length == 1) {
			ret = quoteHash(elements[0]);
		} else if (elements.length == 2) {
			ret = quoteHash(elements[0]) + " " + conjunction + " " + quoteHash(elements[1]);
		} else {
			final StringBuilder sb = new StringBuilder();

			for(int i = 0; i < elements.length - 1; i++) {
				sb.append(quoteHash(elements[i]) + ", ");
			}
			sb.append(conjunction + " " + quoteHash(elements[elements.length - 1]));

			ret = sb.toString();
		}

		return replaceInternalByDisplayNames(ret);
	}

	/**
	 * Helper function to nicely formulate an enumeration of a collection,
	 * with hashes to colour the words.
	 * <p>
	 * For example, for a collection containing the 3 elements x, y, z, returns the
	 * string "#x, #y, and #z".
	 *
	 * @param collection
	 *            The collection whose elements should be enumerated
	 * @return A nice String representation of the collection with hashes
	 */
	public static String enumerateCollectionWithHash(final Collection<String> collection) {
		if (collection == null) {
			return "";
		}

		final List<String> result = new ArrayList<String>(collection.size());
		for (String entry : collection) {
			result.add("#" + entry);
		}

		return enumerateCollection(result);
	}


	/**
	 * To let the client display compound words like "#battle axe" in blue, we put the whole item name in quotes.
	 *
	 * @param str
	 * @return the hashed word
	 */
	public static String quoteHash(final String str) {
		if (str != null) {
			final int idx = str.indexOf('#');

			if ((idx != -1) && (str.indexOf(' ', idx) != -1) && (str.charAt(idx + 1) != '\'')) {
				return str.substring(0, idx) + "#'" + str.substring(idx + 1) + '\'';
			}
		}

		return str;
	}

	/**
	 * Converts numbers into their textual representation.
	 *
	 * @param n
	 *            a number
	 * @return one, two, three, ...
	 */
	public static String numberString(final int n) {
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
			return Integer.toString(n);
		}
	}

	/**
	 * Interprets number texts.
	 *
	 * @param text
	 *            a number
	 * @return one, two, three, ...
	 */
	public static Integer number(final String text) {
		if (text.equals("no") || text.equals("zero")) {
			return 0;
		} else if (text.equals("a") || text.equals("an")) {
			return 1;
		} else if (text.equals("one")) {
			return 1;
		} else if (text.equals("two")) {
			return 2;
		} else if (text.equals("three")) {
			return 3;
		} else if (text.equals("four")) {
			return 4;
		} else if (text.equals("five")) {
			return 5;
		} else if (text.equals("six")) {
			return 6;
		} else if (text.equals("seven")) {
			return 7;
		} else if (text.equals("eight")) {
			return 8;
		} else if (text.equals("nine")) {
			return 9;
		} else if (text.equals("ten")) {
			return 10;
		} else if (text.equals("eleven")) {
			return 11;
		} else if (text.equals("twelve")) {
			return 12;
		} else {
			// also handle "a dozen", ...
			return null;
		}
	}

	/**
	 * Return type for normalizedRegularVerb().
	 */
	public static class Verb {
		public Verb(String normalized) {
			this.word = normalized;
			isGerund = false;
			isPast = false;
		}

		public Verb(Verb other) {
			word = other.word;
			isGerund = other.isGerund;
			isPast = other.isPast;
		}

		public String word;
		public boolean isGerund;
		public boolean isPast;
	}

	/**
	 * Normalize the given regular verb, or return null if not applicable.
	 * Note: Some words like "close" are returned without the trailing "e"
	 * character. This is handled in WordList.normalizeVerb().
	 *
	 * @param word
	 * @return normalized string
	 */
	public static Verb normalizeRegularVerb(final String word) {
		Verb verb = null;

		if ((word.length() > 4) && (word.endsWith("ed") || word.endsWith("es"))) {
			if (word.charAt(word.length() - 4) == word.charAt(word.length() - 3)) {
				verb = new Verb(word.substring(0, word.length() - 3));
			} else {
				verb = new Verb(word.substring(0, word.length() - 2));
			}

			if (word.endsWith("ed")) {
				verb.isPast = true;
			}
		} else if (word.length() > 3 && word.endsWith("s") &&
					!Grammar.isVowel(word.charAt(word.length()-2))) {
			verb = new Verb(word.substring(0, word.length() - 1));
		} else if (isGerund(word)) {
			verb = new Verb(word.substring(0, word.length() - 3));
			verb.isGerund = true;
		}

		return verb;
	}

	/**
	 * Check the given verb for gerund form, e.g. "doing".
	 *
	 * @param word
	 * @return true if gerund false otherwise
	 */
	public static boolean isGerund(final String word) {
		if ((word.length() > 4) && word.endsWith("ing")) {
			// Is there a vowel in the preceding characters?
			for (int i = word.length() - 3; --i >= 0;) {
				if (isVowel(word.charAt(i))) {
					return true;
				}
			}
		}

		return false;
	}


	/**
	 * Return gerund form, e.g. "making" or "casting".
	 *
	 * @param word
	 * @return gerund form
	 */
	public static String gerundForm(final String word) {
		if (word.length() > 2) {
			char last = word.charAt(word.length()-1);

			if (last == 'y' || last == 'w' || last == 'x') {
				// word finishes with a 'y', 'w', or an 'x'
				return word + "ing";
			} else if (isVowel(last)) {
				// word finishes with a vowel
				return word.substring(0, word.length() - 1) + "ing";
			} else if (isVowel(word.charAt(word.length()-2))) {
				// word finishes with a single consonant
				// duplicate the last character
				return word + word.charAt(word.length()-1) + "ing";
			}
		}

		// word is too short or finishes with more than one consonant  (e.g. "st")
		return word + "ing";
	}

	/**
	 * Check the given word for derived adjectives like "magical"
	 * or "nomadic".
	 *
	 * @param word
	 * @return true if ends with "al", "ic" or "ed"
	 */
	public static boolean isDerivedAdjective(final String word) {
		if (word.length() > 4) {
			if (word.endsWith("al") || word.endsWith("ic")) {
				return true;
			}

			if (word.endsWith("ed")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Normalize the given derived adjective, or return null if not applicable.
	 *
	 * @param word
	 * @return normalized string
	 */
	public static String normalizeDerivedAdjective(final String word) {
		if (isDerivedAdjective(word)) {
			return word.substring(0, word.length() - 2);
		} else {
			return null;
		}
    }

	/**
	 * Check for words with ambiguity between noun and verb.
	 * @param normalized word in normalized form
	 * @return ambiguity flag
	 */
	public static boolean isAmbiguousNounVerb(final String normalized) {
		return normalized.equals("mill") || normalized.equals("fish")
				|| normalized.equals("esacpe");
	}

	/**
	 * enumerates a collections using the plural forms.
	 *
	 * @param collection Collection
	 * @return enumeration using plural forms
	 */
	public static String enumerateCollectionPlural(Collection<String> collection) {
		Collection<String> pluralCollection = new ArrayList<String>(collection.size());
		for (String entry : collection) {
			pluralCollection.add(plural(entry));
		}
		return enumerateCollection(pluralCollection);
	}
}
