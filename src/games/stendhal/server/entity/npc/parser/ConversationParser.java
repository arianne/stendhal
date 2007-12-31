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
	private String error;

	/**
	 * Create a new conversation parser and initialize with the given text
	 * string.
	 */
	public ConversationParser(final String text) {
		// initialize a new tokenizer with the given text
		tokenizer = new StringTokenizer(text != null ? text : "");

		// start with no errors.
		error = null;
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
	 * Parse the given text sentence.
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

			// TODO get rid of underscore handling for item names
			text = text.replace('_', ' ');
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
	public void setError(String error) {
		if (this.error == null) {
			this.error = error;
		} else {
			this.error += "\n" + error;
		}
	}

	/**
	 * Return whether some error occurred while parsing the input text.
	 * 
	 * @return error flag
	 */
	public String getError() {
		return error;
	}

	/**
	 * Evaluate sentence type by looking at the trailing punctuation character.
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

	/**
	 * Return the sentence in normalized form.
	 * 
	 * @param trigger
	 * @return
	 */
	public static String normalize(String text) {
		Sentence sentence = parse(text, null);

		return sentence.getNormalized();
	}

	/**
	 * Normalize trigger expressions for the GSM engine to match
	 * the parsed user input.
	 * 
	 * @param trigger
	 * @return
	 */
	public static Expression normalizeTrigger(String trigger) {
		Sentence sentence = parse(trigger, null);

		return sentence.getTriggerWord();
	}

	/**
	 * Create a list of normalized trigger Words from a String list.
	 *
	 * @param string list
	 * @return word lsit
	 */
	public static List<Expression> normalizeTriggerList(List<String> strings) {
		List<Expression> words = new LinkedList<Expression>();

		for(String item : strings) {
			words.add(normalizeTrigger(item));
		}

		return words;
    }

}
