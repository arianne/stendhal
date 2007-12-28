package games.stendhal.server.entity.npc.newparser;

public class WordType {

	private String typeString = "";

	public WordType(String s) {
	    typeString = s;
    }

	/**
	 * return type string
	 * Note: There is no setTypeString to make WordType objects unmutable.
	 * @return
	 */
	public String getTypeString() {
	    return typeString;
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
	 * determine if the word is a verb in gerund form
	 * @return
	 */
	public boolean isGerund() {
	    return typeString.startsWith("VER-GER");
    }

	/**
	 * determine if the word is an object
	 * (a thing, not a person)
	 * @return
	 */
	public boolean isObject() {
	    return typeString.startsWith("OBJ");
    }

	/**
	 * determine if the word is a person
	 * @return
	 */
	public boolean isSubject() {
	    return typeString.startsWith("SUB");
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
	 * determine if the word is in conditional form
	 * @return
	 */
	public boolean isConditional() {
	    return typeString.contains("-CON");
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

		if (other.isConditional() && !isConditional()) {
			newTypeString += "-CON";
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
