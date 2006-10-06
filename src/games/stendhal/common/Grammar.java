/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

/**
 * Helper functions for producing grammatically-correct sentences.
 */
public class Grammar {

	/**
	 * "it" or "them", depending on the quantity
	 * @param quantity The quantity to examine
	 * @return Either "it" or "them" as appropriate
	 */
	public static String itthem(int quantity) { return (quantity==1 ? "it" : "them"); }

	/**
	 * "It" or "Them", depending on the quantity
	 * @param quantity The quantity to examine
	 * @return Either "It" or "Them" as appropriate
	 */
	public static String ItThem(int quantity) { String s = itthem(quantity); s = Character.toUpperCase(s.charAt(0))+s.substring(1); return s; }
	
	/**
	 * "it" or "they", depending on the quantity
	 * @param quantity The quantity to examine
	 * @return Either "it" or "they" as appropriate
	 */
	public static String itthey(int quantity) { return (quantity==1 ? "it" : "they"); }
	
	/**
	 * "It" or "They", depending on the quantity
	 * @param quantity The quantity to examine
	 * @return Either "It" or "They" as appropriate
	 */
	public static String ItThey(int quantity) { String s = itthey(quantity); s = Character.toUpperCase(s.charAt(0))+s.substring(1); return s; }
	
	/**
	 * "is" or "are", depending on the quantity
	 * @param quantity The quantity to examine
	 * @return Either "is" or "are" as appropriate
	 */
	public static String isare(int quantity) { return (quantity==1 ? "is" : "are"); }
	
	/**
	 * "Is" or "Are", depending on the quantity
	 * @param quantity The quantity to examine
	 * @return Either "Is" or "Are" as appropriate
	 */
	public static String IsAre(int quantity) { String s = isare(quantity); s = Character.toUpperCase(s.charAt(0))+s.substring(1); return s; }
	
	/**
	 * "a [noun]" or "an [noun]", depending on the first syllable
	 * @param noun The noun to examine
	 * @return Either "a [noun]" or "an [noun]" as appropriate
	 */
	public static String a_noun(String noun) {
	    String enoun = fullform(noun);
	    char initial = Character.toLowerCase(enoun.charAt(0));
	    char second  = Character.toLowerCase(enoun.charAt(1));
	    if(initial=='e' && second=='u') return "a " + enoun;
	    if(vowel_p(initial)) return "an " + enoun;
	    if(initial=='y' && consonant_p(second)) return "an " + enoun;
	    return "a " + enoun;
	}

	public static String fullform(String noun) {
	    String enoun = noun.toLowerCase();
	    if(enoun=="meat" || enoun=="ham" || enoun=="cheese" || enoun=="wood" || enoun=="paper"
			|| enoun=="iron" || enoun.endsWith(" ore") || enoun.endsWith("_ore"))
		enoun = "piece of " + enoun;
	    else if(enoun=="flour")
		enoun = "sack of " + enoun;
	    else if(enoun=="grain")
		enoun = "sheaf of " + enoun;
	    else if(enoun=="bread")
		enoun = "loaf of " + enoun;
	    else if(enoun=="beer" || enoun=="wine" || enoun.endsWith("poison") || enoun=="antidote")
		enoun = "bottle of " + enoun;
	    else if(enoun=="money")
		enoun = "coin";
	    else if(enoun.startsWith("book_") || enoun.startsWith("book "))
		enoun = enoun.substring(5) + " book";
	    else if(enoun=="arandula")
		enoun = "sprigs of " + enoun;
	    else if(enoun.indexOf("_armor")>-1 || enoun.indexOf(" armor")>-1)
		enoun = "suit of " + enoun;
	    else if(enoun.endsWith("_legs") || enoun.endsWith(" legs") || enoun.endsWith("_boots") || enoun.endsWith(" boots"))
		enoun = "pair of " + enoun;
	    else
		enoun = enoun;
	    return enoun;
	}

	/**
	 * "A [noun]" or "An [noun]", depending on the first syllable
	 * @param noun The noun to examine
	 * @return Either "A [noun]" or "An [noun]" as appropriate
	 */
	public static String A_noun(String noun) { String s = a_noun(noun); s = Character.toUpperCase(s.charAt(0))+s.substring(1); return s; }

	/**
	 * "[noun]'s" or "[noun]", depending on the last character
	 * @param noun The noun to examine
	 * @return Either "[noun]'s" or "[noun]'" as appropriate
	 */
	public static String suffix_s(String noun) {
	    char last = Character.toLowerCase(noun.charAt(noun.length() - 1));
	    if(last=='s') return noun + "'";
	    return noun + "'s";
	}

	/**
	 * Returns the plural form of the given noun
	 * @param noun The noun to examine
	 * @return An appropriate plural form
	 */
	public static String plural(String noun) {
	    String enoun = fullform(noun);
	    char c;
	    if(enoun.endsWith("sheep"))
		return enoun;
	    else if(enoun.indexOf(" of ")>-1)
		return plural(enoun.substring(0, enoun.indexOf(" of ")-1)) + enoun.substring(enoun.indexOf(" of "));
	    else if(enoun.endsWith("staff"))
		return enoun.substring(0, enoun.length()-3) + "ves";
	    else if(enoun.endsWith("f") && "aeiourl".indexOf(enoun.charAt(enoun.length()-2))>-1)
		return enoun.substring(0, enoun.length()-2) + "ves";
	    else if(enoun.endsWith("fe"))
		return enoun.substring(0, enoun.length()-3) + "ves";
	    else if(enoun.endsWith("house"))
		return enoun + "es";
	    else if(enoun.endsWith("ouse") && "mMlL".indexOf(enoun.charAt(enoun.length()-5))>-1)
		return enoun.substring(0, enoun.length()-5) + "ice";
	    else if(enoun.endsWith("oose") && !(enoun.endsWith("caboose")))
		return enoun.substring(0, enoun.length()-5) + "eese";
	    else if(enoun.endsWith("ooth"))
		return enoun.substring(0, enoun.length()-5) + "eeth";
	    else if(enoun.endsWith("foot"))
		return enoun.substring(0, enoun.length()-5) + "feet";
	    else if(enoun.endsWith("child"))
		return enoun + "ren";
	    else if(enoun.endsWith("eau"))
		return enoun + "x";
	    else if(enoun.endsWith("ato"))
		return enoun + "es";
	    else if(enoun.endsWith("ium"))
		return enoun.substring(0, enoun.length()-3) + "a";
	    else if(enoun.endsWith("alga") || enoun.endsWith("hypha") || enoun.endsWith("larva"))
		return enoun + "e";
	    else if(enoun.length()>3 && enoun.endsWith("us") && !(enoun.endsWith("lotus") || enoun.endsWith("wumpus")))
		return enoun.substring(0, enoun.length()-3) + "i";
	    else if(enoun.endsWith("man") && !(enoun.endsWith("shaman") || enoun.endsWith("human")))
		return enoun.substring(0, enoun.length()-4) + "men";
	    else if(enoun.endsWith("rtex"))
		return enoun.substring(0, enoun.length()-3) + "ices";
	    else if(enoun.endsWith("trix"))
		return enoun.substring(0, enoun.length()-2) + "ces";
	    else if(enoun.endsWith("sis"))
		return enoun.substring(0, enoun.length()-2) + "es";
	    else if(enoun.endsWith("erinys") || enoun.endsWith("cyclops"))
		return enoun.substring(0, enoun.length()-2) + "es";
	    else if(enoun.endsWith("mumak"))
		return enoun + "il";
	    else if(enoun.endsWith("djinni") || enoun.endsWith("efreeti"))
		return enoun.substring(0, enoun.length()-2);
	    else if(enoun.endsWith("ch") || enoun.endsWith("sh") || "zxs".indexOf(enoun.charAt(enoun.length()-1))>-1)
		return enoun + "es";
	    else if(enoun.endsWith("y") && consonant_p(enoun.charAt(enoun.length()-2)))
		return enoun.substring(0, enoun.length()-2) + "ies";
	    else
		return enoun + "s";
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending on the quantity
	 * @param quantity The quantity to examine
	 * @param noun The noun to examine
	 * @return Either "[noun]" or plural("[noun]") as appropriate
	 */
	public static String plnoun(int quantity, String noun) {
	    String enoun = fullform(noun);
	    return (quantity==1 ? enoun : plural(noun));
	}

	/**
	 * Returns either the plural or singular form of the given noun, depending on the quantity; also prefixes the quantity
	 * @param quantity The quantity to examine
	 * @param noun The noun to examine
	 * @return Either "[quantity] [noun]" or "[quantity]" + plural("[noun]") as appropriate
	 */
	public static String quantityplnoun(int quantity, String noun) {
	    return "" + quantity + " " + plnoun(quantity, noun);
	}

	/**
	 * Is the character a vowel?
	 * @param c The character to examine
	 * @return true if c is a vowel, false otherwise
	 */
	public static boolean vowel_p(char c) { char l = Character.toLowerCase(c); return (l=='a' || l=='e' || l=='i' || l=='o' || l=='u'); }

	/**
	 * Is the character a consonant?
	 * @param c The character to examine
	 * @return true if c is a consonant, false otherwise
	 */
        public static boolean consonant_p(char c) { return !vowel_p(c); }
	
}
