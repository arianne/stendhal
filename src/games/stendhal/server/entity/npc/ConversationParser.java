package games.stendhal.server.entity.npc;

import games.stendhal.common.Grammar;

import java.util.StringTokenizer;

/**
 * Parser for conversations with a SpeakerNPC This class parses strings in
 * english language and returns them as Sentence objects. All sentence
 * constituents are in lower case.
 * 
 * @author Martin Fuchs
 */
public class ConversationParser {
	private StringTokenizer tokenizer;
	private StringBuilder original;
	private String nextWord;
	private String error;

	/**
	 * create a new conversation parser and initialise with the given text
	 * string
	 */
	public ConversationParser(final String text) {
		// initialise a new tokenizer with the given text
		tokenizer = new StringTokenizer(text != null ? text : "");

		// get first word
		nextWord = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
		original = nextWord != null ? new StringBuilder(nextWord)
				: new StringBuilder();

		// start with no errors.
		error = null;
	}

	/**
	 * parse the given command sentence
	 * 
	 * @param text
	 * @return sentence
	 */
	public static Sentence parse(final String text) {
		ConversationParser parser = new ConversationParser(text);
		Sentence sentence = new Sentence();

		// Parse the text as "verb - amount - object" construct,
		// ignoring prefixed occurencies of "please".
		do {
			sentence.setVerb(parser.nextWord());
		} while (sentence.getVerb() != null
				&& sentence.getVerb().equals("please"));

		sentence.setAmount(parser.readAmount());
		String object = parser.readObjectName();

		// Optionally there may be following a preposition and a second object.
		sentence.setPreposition(parser.nextWord());
		sentence.setObject2(parser.readObjectName());

		sentence.setError(parser.getError());
		sentence.setOriginal(parser.original.toString());

		// derive the singular from the item name if the amount is greater than
		// one
		if (sentence.getAmount() != 1) {
			object = Grammar.singular(object);
		}

		sentence.setObject(object);

		return sentence;
	}

	private String nextWord() {
		String word = nextWord;

		if (word != null) {
			if (tokenizer.hasMoreTokens()) {
				nextWord = tokenizer.nextToken();
				original.append(' ');
				original.append(nextWord);
			} else {
				nextWord = null;
			}

			return word.toLowerCase();
		} else {
			return null;
		}
	}

	/**
	 * read in a positive amount from the input text
	 * 
	 * @return amount
	 */
	private int readAmount() {
		int amount = 1;

		// handle numeric expressions
		if (tokenizer.hasMoreTokens()) {
			if (nextWord.matches("^[+-]?[0-9]+")) {
				try {
					amount = Integer.parseInt(nextWord);

					if (amount < 0) {
						setError("negative amount: " + amount);
					}

					nextWord();
				} catch (NumberFormatException e) {
					setError("illegal number format: '" + nextWord + "'");
				}
			} else {
				// handle expressions like "one", "two", ...
				Integer number = Grammar.number(nextWord);

				if (number != null) {
					amount = number.intValue();
					nextWord();
				}
			}
		}

		return amount;
	}

	/**
	 * read in the object of the parsed sentence (e.g. item to be bought)
	 * 
	 * @return object name in lower case
	 */
	private String readObjectName() {
		String name = null;

		// handle object names consisting of more than one word
		for (;;) {
			if (nextWord == null) {
				break;
			}

			// stop if the next word is a preposition
			if (Grammar.isPreposition(nextWord) && !nextWord.equals("of")) {
				// TODO directly integrate Grammar.extractNoun() here
				break;
			}

			String word = nextWord();

			// concatenate user specified item names like "baby dragon"
			// with spaces to build the internal item names
			if (name == null) {
				name = word;
			} else {
				name += " " + word;
			}
		}

		return Grammar.extractNoun(name);
	}

	/**
	 * set error flag on parsing problems.
	 */
	private void setError(String error) {
		if (this.error == null) {
			this.error = error;
		} else {
			this.error += "\n" + error;
		}
	}

	/**
	 * return whether some error occurred while parsing the input text.
	 * 
	 * @return error flag
	 */
	public String getError() {
		return error;
	}
}
