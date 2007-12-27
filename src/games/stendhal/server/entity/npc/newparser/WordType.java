package games.stendhal.server.entity.npc.newparser;

public class WordType {

	String typeString = "";

	public WordType(String s) {
	    typeString = s;
    }

	/**
	 * return main word type string (first 3 letters)
	 *
	 * @return
	 */
	public String getMainType() {
		if (typeString.length() >= 3)
			return typeString.substring(0, 3);
		else
			return typeString;
	}

	/**
	 * determine if the word is a verb
	 * @return
	 */
	public boolean isVerb() {
	    return typeString.startsWith("VER");
    } 

	/**
	 * determine if the word is a noun
	 * @return
	 */
	public boolean isNoun() {
	    return typeString.startsWith("NOU");
    } 

	/**
	 * determine if the word is a person
	 * @return
	 */
	public boolean isPerson() {
	    return typeString.startsWith("NOU-PER");
    }

	/**
	 * determine if the word is a numeral
	 * @return
	 */
	public boolean isNumeral() {
	    return typeString.startsWith("NUM");
    } 

	/**
	 * determine if the word is an adjective or adverb
	 * @return
	 */
	public boolean isAdjective() {
	    return typeString.startsWith("ADJ");
    }

	/**
	 * determine if the word is a preposition
	 * @return
	 */
	public boolean isPreposition() {
	    return typeString.startsWith("PRE");
    } 

	/**
	 * determine words to ignore
	 * @return
	 */
	public boolean isIgnore() {
	    return typeString.startsWith("IGN");
    }

	/**
	 * determine if the word is in plural form
	 * @return
	 */
	public boolean isPlural() {
	    return typeString.endsWith("-PLU");
    }

	/**
	 * determine if the word is a creature name
	 * @return
	 */
	public boolean isName() {
	    return typeString.endsWith("NAM");
    }

	/**
	 * determine if the word is a question word
	 * @return
	 */
	public boolean isQuestion() {
	    return typeString.startsWith("QUE");
    }

	/**
	 * determine if the word is an or is
	 * merged with a question word
	 * @return
	 */
	public boolean hasQuestion() {
	    return typeString.contains("QUE");
    }

	/**
	 * determine if the word specifies a colour
	 * @return
	 */
	public boolean hasColor() {
	    return typeString.contains("COL");
    }

	/**
	 * merge with another WordType
	 * @param other
	 * @return new WordType object or this
	 */
	public WordType merge(final WordType other) {
		String newTypeString = typeString;

		if (other.hasColor() && !hasColor()) {
			newTypeString += "-COL";
		}

		if (other.hasQuestion() && !hasQuestion()) {
			newTypeString += "-QUE";
		}

		if (newTypeString != typeString) {
			return new WordType(newTypeString);
		} else {
			return this;
		}
    }

	@Override
	public String toString() {
		return typeString;
	}
}
