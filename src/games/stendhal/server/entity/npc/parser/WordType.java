package games.stendhal.server.entity.npc.parser;

public class WordType {

	private String typeString = "";

	// word type string constants
	public static final String VERB = "VER"; // verb
	public static final String GERUND = "-GER"; // gerund form
	public static final String CONDITIONAL = "CON"; // conditional form

	public static final String OBJECT = "OBJ"; // object
	public static final String AMOUNT = "AMO"; // amount
	public static final String FLUID = "FLU"; // fluid
	public static final String FOOD = "FOO"; // food
	public static final String OBSESSIONAL = "OBS"; // obsessional word

	public static final String SUBJECT = "SUB"; // subject
	public static final String ANIMAL = "ANI"; // animal
	public static final String NAME = "NAM"; // person name

	public static final String ADJECTIVE = "ADJ"; // adjective/adverb
	public static final String COLOR = "COL"; // color expression

	public static final String NUMERAL = "NUM"; // numeral
	public static final String PREPOSITION = "PRE"; // preposition

	public static final String QUESTION = "QUE"; // question word

	public static final String IGNORE = "IGN"; // word to ignore

	public static final String SUFFIX = "-";

	public static final String PLURAL = "PLU"; // plural form

	// derived constants
	public static final String SUFFIX_COLOR = SUFFIX + COLOR;
	public static final String SUFFIX_CONDITIONAL = SUFFIX + CONDITIONAL;
	public static final String SUFFIX_FOOD = SUFFIX + FOOD;
	public static final String SUFFIX_FLUID = SUFFIX + FLUID;
	public static final String SUFFIX_ANIMAL = SUFFIX + ANIMAL;
	public static final String SUFFIX_NAME = SUFFIX + NAME;
	public static final String SUBJECT_NAME = SUBJECT + SUFFIX_NAME;
	public static final String SUFFIX_PLURAL = SUFFIX + PLURAL;
	public static final String SUFFIX_QUESTION = SUFFIX + QUESTION;
	public static final String VERB_GERUND = VERB + GERUND;

	public WordType(String s) {
		typeString = s;
	}

	/**
	 * return type string Note: There is no setTypeString to make WordType
	 * objects unmutable.
	 * 
	 * @return
	 */
	public String getTypeString() {
		return typeString;
	}

	/**
	 * return main word type string (first 3 letters).
	 * 
	 * @return
	 */
	public String getMainType() {
		if (typeString.length() >= 3) {
			return typeString.substring(0, 3);
		} else {
			return typeString;
		}
	}

	/**
	 * determine if the word is a verb.
	 * 
	 * @return
	 */
	public boolean isVerb() {
		return typeString.startsWith(VERB);
	}

	/**
	 * determine if the word is a verb in gerund form.
	 * 
	 * @return
	 */
	public boolean isGerund() {
		return typeString.startsWith(VERB_GERUND);
	}

	/**
	 * determine if the word is an object. (a thing, not a person)
	 * 
	 * @return
	 */
	public boolean isObject() {
		return typeString.startsWith(OBJECT);
	}

	/**
	 * determine if the word is a person.
	 * 
	 * @return
	 */
	public boolean isSubject() {
		return typeString.startsWith(SUBJECT);
	}

	/**
	 * determine if the word is a numeral.
	 * 
	 * @return
	 */
	public boolean isNumeral() {
		return typeString.startsWith(NUMERAL);
	}

	/**
	 * determine if the word is an adjective or adverb.
	 * 
	 * @return
	 */
	public boolean isAdjective() {
		return typeString.startsWith(ADJECTIVE);
	}

	/**
	 * determine if the word is a preposition.
	 * 
	 * @return
	 */
	public boolean isPreposition() {
		return typeString.startsWith(PREPOSITION);
	}

	/**
	 * determine words to ignore.
	 * 
	 * @return
	 */
	public boolean isIgnore() {
		return typeString.startsWith(IGNORE);
	}

	/**
	 * determine if the word is in plural form.
	 * 
	 * @return
	 */
	public boolean isPlural() {
		return typeString.contains(SUFFIX_PLURAL);
	}

	/**
	 * determine if the word is a creature name.
	 * 
	 * @return
	 */
	public boolean isName() {
		return typeString.contains(SUFFIX_NAME);
	}

	/**
	 * determine if the word is a question word.
	 * 
	 * @return
	 */
	public boolean isQuestion() {
		return typeString.startsWith(QUESTION);
	}

	/**
	 * determine if the word is an or is merged with a question word.
	 * 
	 * @return
	 */
	public boolean hasQuestion() {
		return typeString.contains(QUESTION);
	}

	/**
	 * determine if the word specifies a color.
	 * 
	 * @return
	 */
	public boolean hasColor() {
		return typeString.contains(COLOR);
	}

	/**
	 * determine if the word is in conditional form.
	 * 
	 * @return
	 */
	public boolean isConditional() {
		return typeString.contains(SUFFIX_CONDITIONAL);
	}

	/**
	 * merge with another WordType.
	 * 
	 * @param other
	 * @return new WordType object or this
	 */
	public WordType merge(final WordType other) {
		String newTypeString = typeString;

		if (other.hasColor() && !hasColor()) {
			newTypeString += SUFFIX_COLOR;
		}

		if (other.hasQuestion() && !hasQuestion()) {
			newTypeString += SUFFIX_QUESTION;
		}

		if (other.isConditional() && !isConditional()) {
			newTypeString += SUFFIX_CONDITIONAL;
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
