package games.stendhal.server.entity.npc.newparser;

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
		tokenizer = new StringTokenizer(text!=null? text: "");

		// get first word
		nextWord = tokenizer.hasMoreTokens()? tokenizer.nextToken(): null;
		original = nextWord!= null? new StringBuilder(nextWord): new StringBuilder();

		// start with no errors.
		error = null;
	}

	/**
	 * backward compatible parse function without conversation context
	 * @param text
	 * @return
	 */
	public static Sentence parse(final String text) {
		return parse(text, null);
	}

	/**
	 * Parse the given text sentence
	 *
	 * @param text
	 * @param ctx
	 * @return sentence
	 */
	public static Sentence parse(String text, ConversationContext ctx) {
		// 1.) determine sentence type from trailing punctuation
		Sentence sentence = new Sentence();

	    if (text != null) {
	    	text = getSentenceType(text.trim(), sentence);

	    	//TODO get rid of underscore handling for item names
	    	text = text.replace('_', ' ');
	    }

	    // 2.) feed the separated words into the sentence object
		ConversationParser parser = new ConversationParser(text);

		sentence.parse(parser);

		// 3.) classify word types and normalise words
		sentence.classifyWords(parser);

		// 4.) evaluate sentence type from word order
		sentence.evaluateSentenceType();

		// 5.) merge words to form a simpler sentence structure
		sentence.mergeWords();

//		String verb = null;
//		String subject = null;
//		String subject2 = null;
//
//		for(;;) {
//			String word = parser.peekNextWord();
//			if (word == null) {
//				break;
//			}
//
//			if (word.equals("please")) {
//				// skip to the next word
//				parser.readNextWord();
//				continue;
//			}
//
//			//TODO This rule set seems a bit complex - it should be refactored into the Sentence class, if possible.
//			if (Grammar.isSubject(word)) {
//				if (subject == null) {
//					subject = word;
//				} else if (subject2 == null) {
//					subject2 = word;
//				} else {
//					break;	// too many subjects
//				}
//			} else if (word.equals("me")) {
//				if (verb != null) {
//					if (subject == null) {
//    					// If there is a "me" without any preceding subject, we
//    					// set the first subject to "you" and the second to "i".
//        				subject = "you";
//        				subject2 = "i";
//    				} else if (subject2 == null) {
//    					// Otherwise the "me" is stored as "i" into subject2.
//        				subject2 = "i";
//    				} else {
//    					break;	// too many subjects
//    				}
//				} else {
//					// no verb yet
//					if (subject == null) {
//    					// If there is a "me" without any preceding verb and other
//						// subject, we store it as "i" into the first subject.
//        				subject = "i";
//    				} else if (subject2 == null) {
//    					// Otherwise the "me" is stored as "i" into subject2.
//        				subject2 = "i";
//    				} else {
//    					break;	// too many subjects
//    				}
//				}
//			} else if (verb == null) {
//				verb = word;
//			} else {
//				break;
//			}
//
//			// continue looking for verbs and subjects
//			parser.readNextWord();
//		}
//
//		if (subject != null) {
//			sentence.setSubject(subject);
//		}
//
//		if (verb != null) {
//			sentence.setVerb(verb);
//		}
//
//		if (subject2 != null) {
//			sentence.setSubject2(subject2);
//		}
//
//		sentence.setAmount(parser.readAmount());
//		String object = parser.readObjectName();
//
//		// Optionally there may be following a preposition and a second object.
//		sentence.setPreposition(parser.readNextWord());
//		sentence.setObject2(parser.readObjectName());

		sentence.performaAliasing();

		sentence.setError(parser.getError());

		return sentence;
	}

	/**
	 * set error flag on parsing problems.
	 */
	public void setError(String error) {
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

	/**
	 * evaluate sentence type by looking at the trailing punctuation character
	 * @param text
	 * @param sentence
	 * @return
	 */
	public static String getSentenceType(String text, Sentence sentence) {
		if (text.length() > 0) {
			char c = text.charAt(text.length()-1);

			if (c == '.') {
				sentence.setType(Sentence.ST_STATEMENT);
				text = text.substring(0, text.length()-1);
			} else if (c == '!') {
				sentence.setType(Sentence.ST_IMPERATIVE);
				text = text.substring(0, text.length()-1);
			} else if (c == '?') {
				sentence.setType(Sentence.ST_QUESTION);
				text = text.substring(0, text.length()-1);
			}
		}

	    return text;
    }

	public String readNextWord() {
		String word = nextWord;

		if (word != null) {
			if (tokenizer.hasMoreTokens()) {
				nextWord = tokenizer.nextToken();
				original.append(' ');
				original.append(nextWord);
			} else {
				nextWord = null;
			}

			return word;
		} else {
			return null;
		}
	}

//	/**
//	 * return next word without advancing tokenizer.
//	 * @return next word
//	 */
//	public String peekNextWord() {
//		if (nextWord != null) {
//			return nextWord;
//		} else {
//			return null;
//		}
//	}

//	/**
//	 * read in a positive amount from the input text.
//	 * 
//	 * @return amount
//	 */
//	private int readAmount() {
//		int amount = 1;
//
//		// handle numeric expressions
//		if (nextWord != null) {
//			if (nextWord.matches("^[+-]?[0-9]+")) {
//				try {
//					amount = Integer.parseInt(nextWord);
//
//					if (amount < 0) {
//						setError("negative amount: " + amount);
//					}
//
//					readNextWord();
//				} catch (NumberFormatException e) {
//					setError("illegal number format: '" + nextWord + "'");
//				}
//			} else {
//				// handle expressions like "one", "two", ...
//				Integer number = Grammar.number(nextWord);
//
//				if (number != null) {
//					amount = number.intValue();
//					readNextWord();
//				}
//			}
//		}
//
//		return amount;
//	}

//	/**
//	 * read in the object of the parsed sentence (e.g. item to be bought)
//	 * 
//	 * @return object name in lower case
//	 */
//	private String readObjectName() {
//		String name = null;
//
//		// handle object names consisting of more than one word
//		for (;;) {
//			if (nextWord == null) {
//				break;
//			}
//
//			// stop if the next word is a preposition
//			if (Grammar.isPreposition(nextWord) && !nextWord.equals("of")) {
//				// TODO directly integrate Grammar.extractNoun() here
//				break;
//			}
//
//			String word = readNextWord();
//
//			// concatenate user specified item names like "baby dragon"
//			// with spaces to build the internal item names
//			if (name == null) {
//				name = word;
//			} else {
//				name += " " + word;
//			}
//		}
//
//		return Grammar.extractNoun(name);
//	}

}
