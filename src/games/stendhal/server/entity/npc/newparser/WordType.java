package games.stendhal.server.entity.npc.newparser;

public class WordType {

	String typeString = "";

	public WordType(String s) {
	    typeString = s;
    }

	/**
	 * determine is the word is a verb
	 * @return
	 */
	public boolean isVerb() {
	    return typeString.startsWith("VER");
    } 

	/**
	 * determine is the word is a noun
	 * @return
	 */
	public boolean isNoun() {
	    return typeString.startsWith("NOU");
    } 

	/**
	 * determine is the word is a numeral
	 * @return
	 */
	public boolean isNumeral() {
	    return typeString.startsWith("NUM");
    } 

	/**
	 * determine is the word is an adjective or adverb
	 * @return
	 */
	public boolean isAdjective() {
	    return typeString.startsWith("ADJ");
    }

	/**
	 * determine is the word is a preposition
	 * @return
	 */
	public boolean isPreposition() {
	    return typeString.startsWith("PRE");
    } 

	/**
	 * determine is the word is in plural form
	 * @return
	 */
	public boolean isPlural() {
	    return typeString.endsWith("-PLU");
    }

	/**
	 * determine is the word is a creature name
	 * @return
	 */
	public boolean isName() {
	    return typeString.endsWith("NAM");
    }

	public String toString() {
		return typeString;
	}
}
