package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.ErrorBuffer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Parser for conversations with a SpeakerNPC This class parses strings in
 * English language and returns them as Sentence objects. All sentence
 * constituents are in lower case.
 * 
 * @author Martin Fuchs
 */
public final class ConversationParser extends ErrorBuffer {

	private static final Logger logger = Logger.getLogger(ConversationParser.class);

	private StringTokenizer tokenizer;

	final String originalText;

	/**
	 * Return the original parsed text.
	 *
	 * @return
	 */
	public String getOriginalText() {
	    return originalText;
    }

	/**
	 * Create a new conversation parser and initialize with the given text
	 * string.
	 *
	 * @param text 
	 */
	public ConversationParser(final String text) {
		// initialize a new tokenizer with the given text
		tokenizer = new StringTokenizer(text != null ? text : "");
		originalText = text;
	}

	/**
	 * Return the sentence in normalized form.
	 * 
	 * @param text
	 * @return
	 */
	public static String normalize(String text) {
		return parse(text).getNormalized();
	}

	/**
	 * Create trigger expression to match the parsed user input in the FSM engine.
	 * 
	 * @param text
	 * @return expression
	 */
	public static Expression createTriggerExpression(String text) {
		ConversationContext ctx = new ConversationContext();

		// don't ignore words with type "IGN" if specified in trigger expressions
		ctx.setIgnoreIgnorable(false);

		return parse(text, ctx).getTriggerExpression();
	}

	/**
	 * Parse function without conversation context.
	 *
	 * @param text
	 * @return
	 */
	public static Sentence parse(final String text) {
		return parse(text, new ConversationContext());
	}

	/** A cache to hold pre-parsed matching Sentences. */
	private static Map<String, Sentence> matchingSentenceCache = new HashMap<String, Sentence>();

	/**
	 * Parse the given text sentence to be used for sentence matching.
	 *
	 * @param text
	 * @return
	 */
	public static Sentence parseForMatching(String text) {
		Sentence ret = matchingSentenceCache.get(text);

		if (ret == null) {
			ConversationContext ctx = new ConversationContext();
			ctx.setForMatching(true);

			ret = parse(text, ctx);

			matchingSentenceCache.put(text, ret);
		}

		return ret;
	}

	/**
	 * Parse the given text sentence.
	 * 
	 * @param text
	 * @param ctx
	 * @return Sentence
	 */
	public static Sentence parse(String text, ConversationContext ctx) {
		if (text != null) {
			ExpressionMatcher matcher = new ExpressionMatcher();

			// If the text begins with matching flags, skip normal sentence parsing and read in the
			// expressions from the given string in prepared form.
			text = matcher.readMatchingFlags(text);

			if (matcher.isAnyFlagSet()) {
				return matcher.parseSentence(text, ctx);
			} else {
				text = text.trim();
			}
		} else {
			text = "";
		}

		SentenceImplementation sentence = new SentenceImplementation(ctx);

		try {
			// 1.) determine sentence type from trailing punctuation
			text = getSentenceType(text, sentence);

    		// 2.) feed the separated words into the sentence object
    		ConversationParser parser = new ConversationParser(text);

    		sentence.parse(parser);

    		// 3.) classify word types and normalize words
    		sentence.classifyWords(parser);

    		// 4.) evaluate sentence type from word order
    		sentence.evaluateSentenceType();

    		if (ctx != null && ctx.getMergeExpressions()) {
        		// 5.) merge words to form a simpler sentence structure
        		sentence.mergeWords();

        		if (!ctx.isForMatching()) {
            		// 6.) standardize sentence type
        			sentence.standardizeSentenceType();

            		// 7.) replace grammatical constructs with simpler ones
            		sentence.performaAliasing();
        		}
    		}

    		sentence.setError(parser.getErrorString());
		} catch(Exception e) {
			logger.error("ConversationParser.parse(): catched Exception while parsing '" + text + '\'');
			sentence.setError(e.getMessage());
			e.printStackTrace();
		}

		return sentence;
	}

	/**
	 * Read the next word from the parsed sentence.
	 *
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
			sentence.setType(Sentence.SentenceType.QUESTION);
			text = punct.getText();
		} else if (trailing.contains("!")) {
			sentence.setType(Sentence.SentenceType.IMPERATIVE);
			text = punct.getText();
		} else if (trailing.contains(".")) {
			sentence.setType(Sentence.SentenceType.STATEMENT);
			text = punct.getText();
		}

		return text;
	}

	/**
	 * Create a list of normalized trigger Words from a String list.
	 *
	 * @param strings list
	 * @return word list
	 */
	public static List<Expression> createTriggerList(List<String> strings) {
		List<Expression> words = new LinkedList<Expression>();

		for (String item : strings) {
			words.add(createTriggerExpression(item));
		}

		return words;
    }

}
