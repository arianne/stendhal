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
	private String error;

	/**
	 * create a new conversation parser and initialise with the given text
	 * string
	 */
	public ConversationParser(final String text) {
		// initialise a new tokenizer with the given text
		tokenizer = new StringTokenizer(text!=null? text: "");

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
//		for(String word = parser.peekNextWord(); ; parser.readNextWord()) {
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
//		}

		sentence.performaAliasing();

		sentence.setError(parser.getError());

		return sentence;
	}

	/**
	 * read the next word from the parsed sentence
	 * @return word string
	 */
	public String readNextWord() {
		if (tokenizer.hasMoreTokens()) {
			return tokenizer.nextToken();
		} else {
			return null;
		}
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
	 *
	 * @param text
	 * @param sentence
	 * @return
	 */
	public static String getSentenceType(String text, Sentence sentence) {
		PunctuationParser punct = new PunctuationParser(text);

		String trailing = punct.getTrailingPunctuation();

		if (trailing.contains("!")) {
			sentence.setType(Sentence.ST_IMPERATIVE);
			text = punct.getText();
		} else if (trailing.contains("?")) {
			sentence.setType(Sentence.ST_QUESTION);
			text = punct.getText();
		} else if (trailing.contains(".")) {
			sentence.setType(Sentence.ST_STATEMENT);
			text = punct.getText();
		}

	    return text;
    }

}
