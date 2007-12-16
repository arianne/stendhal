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
	 * Parse the given text sentence as the following construct:
	 * [subject1] verb [subject2] [amount] [object1] [preposition] [object2]
	 * 
	 * Prefixed occurencies of "please" are ignored.
	 * All sentence constituents but the verb are optional, defaults are:
	 * subject1 = "I"
	 * amount	= 1
	 *
	 * @param text
	 * @return sentence
	 */
	public static Sentence parse(final String text) {
		ConversationParser parser = new ConversationParser(text);
		Sentence sentence = new Sentence();

		String verb = null;
		String subject = null;
		String subject2 = null;

		for(;;) {
			String word = parser.peekNextWord();
			if (word == null) {
				break;
			}

			if (word.equals("please")) {
				// skip to the next word
				parser.readNextWord();
				continue;
			}

			//TODO This rule set seems a bit complex - it should be refactored into the Sentence class, if possible.
			if (Grammar.isSubject(word)) {
				if (subject == null) {
					subject = word;
				} else if (subject2 == null) {
					subject2 = word;
				} else {
					break;	// too many subjects
				}
			}
			else if (word.equals("me")) {
				if (verb != null) {
					if (subject == null) {
    					// If there is a "me" without any preceding subject, we
    					// set the first subject to "you" and the second to "i".
        				subject = "you";
        				subject2 = "i";
    				} else if (subject2 == null) {
    					// Otherwise the "me" is stored as "i" into subject2.
        				subject2 = "i";
    				} else {
    					break;	// too many subjects
    				}
				} else {
					// no verb yet
					if (subject == null) {
    					// If there is a "me" without any preceding verb and other
						// subject, we store it as "i" into the first subject.
        				subject = "i";
    				} else if (subject2 == null) {
    					// Otherwise the "me" is stored as "i" into subject2.
        				subject2 = "i";
    				} else {
    					break;	// too many subjects
    				}
				}
			} else if (verb == null) {
				verb = word;
			} else {
				break;
			}

			// continue looking for verbs and subjects
			parser.readNextWord();
		};

		if (subject != null)
			sentence.setSubject(subject);

		if (verb != null)
			sentence.setVerb(verb);

		if (subject2 != null)
			sentence.setSubject2(subject2);

		sentence.setAmount(parser.readAmount());
		String object = parser.readObjectName();

		// Optionally there may be following a preposition and a second object.
		sentence.setPreposition(parser.readNextWord());
		sentence.setObject2(parser.readObjectName());

		sentence.setError(parser.getError());
		sentence.setOriginal(parser.original.toString());

		// derive the singular from the item name if the amount is greater than
		// one
		if (sentence.getAmount() != 1) {
			object = Grammar.singular(object);
		}

		sentence.setObject(object);

		sentence.performaAliasing();

		return sentence;
	}

	/**
	 * return next word without advancing tokenizer
	 * @return next word
	 */
	private String peekNextWord() {
		if (nextWord != null)
			return nextWord.toLowerCase();
		else
			return null;
	}

	private String readNextWord() {
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
		if (nextWord != null) {
			if (nextWord.matches("^[+-]?[0-9]+")) {
				try {
					amount = Integer.parseInt(nextWord);

					if (amount < 0) {
						setError("negative amount: " + amount);
					}

					readNextWord();
				} catch (NumberFormatException e) {
					setError("illegal number format: '" + nextWord + "'");
				}
			} else {
				// handle expressions like "one", "two", ...
				Integer number = Grammar.number(nextWord);

				if (number != null) {
					amount = number.intValue();
					readNextWord();
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

			String word = readNextWord();

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
