package games.stendhal.server.entity.npc.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Parser for conversations with a SpeakerNPC This class parses strings in
 * English language and returns them as Sentence objects. All sentence
 * constituents are in lower case.
 * 
 * @author Martin Fuchs
 */
public class ConversationParser {

	private StringTokenizer tokenizer;
	private String errorBuffer;

	/**
	 * Create a new conversation parser and initialize with the given text
	 * string.
	 */
	public ConversationParser(final String text) {
		// initialize a new tokenizer with the given text
		tokenizer = new StringTokenizer(text != null ? text : "");

		// start with no errors
		errorBuffer = null;
	}

	/**
	 * backward compatible parse function without conversation context
	 *
	 * @param text
	 * @return
	 */
	public static Sentence parse(final String text) {
		return parse(text, null);
	}

	/**
	 * Return the sentence in normalized form.
	 * 
	 * @param trigger
	 * @return
	 */
	public static String normalize(String text) {
		return parse(text, null).getNormalized();
	}

	/**
	 * Normalize trigger expressions for the FSM engine to match
	 * the parsed user input.
	 * 
	 * @param trigger
	 * @return expression
	 */
	public static Expression normalizeTrigger(String trigger) {
		return parse(trigger, null).getTriggerExpression();
	}

	/**
	 * Parse the given text sentence.
	 * 
	 * @param text
	 * @param ctx
	 * @return sentence
	 */
	public static Sentence parse(String text, ConversationContext ctx) {

		//TODO use context information

		// 1.) determine sentence type from trailing punctuation
		Sentence sentence = new Sentence();

		if (text != null) {
			text = getSentenceType(text.trim(), sentence);

//			// TODO get rid of underscore handling for item names
//			text = text.replace('_', ' ');
		}

		// 2.) feed the separated words into the sentence object
		ConversationParser parser = new ConversationParser(text);

		sentence.parse(parser);

		// 3.) classify word types and normalize words
		sentence.classifyWords(parser);

		// 4.) evaluate sentence type from word order
		sentence.evaluateSentenceType();

		// 5.) merge words to form a simpler sentence structure
		sentence.mergeWords();

		// 6.) standardize sentence type
		sentence.standardizeSentenceType();

		// 7.) replace grammatical constructs with simpler ones
		sentence.performaAliasing();

		sentence.setError(parser.getError());

		return sentence;
	}

	/**
	 * Read the next word from the parsed sentence.
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
	 * Set error flag on parsing problems.
	 */
	public void setError(final String error) {
		if (errorBuffer == null) {
			errorBuffer = error;
		} else {
			errorBuffer += '\n';
			errorBuffer += error;
		}
	}

	/**
	 * Return accumulated description string for errors occurred while
	 * parsing the input text.
	 * 
	 * @return error string
	 */
	public String getError() {
		return errorBuffer;
	}

	/**
	 * Evaluate sentence type by looking at the trailing punctuation characters.
	 *
	 * @param text
	 * @param sentence
	 * @return
	 */
	public static String getSentenceType(String text, Sentence sentence) {
		PunctuationParser punct = new PunctuationParser(text);

		String trailing = punct.getTrailingPunctuation();

		if (trailing.contains("?")) {
			sentence.setType(Sentence.ST_QUESTION);
			text = punct.getText();
		} else if (trailing.contains("!")) {
			sentence.setType(Sentence.ST_IMPERATIVE);
			text = punct.getText();
		} else if (trailing.contains(".")) {
			sentence.setType(Sentence.ST_STATEMENT);
			text = punct.getText();
		}

		return text;
	}

	/**
	 * Create a list of normalized trigger Words from a String list.
	 *
	 * @param string list
	 * @return word list
	 */
	public static List<Expression> normalizeTriggerList(List<String> strings) {
		List<Expression> words = new LinkedList<Expression>();

		for(String item : strings) {
			words.add(normalizeTrigger(item));
		}

		return words;
    }

}
