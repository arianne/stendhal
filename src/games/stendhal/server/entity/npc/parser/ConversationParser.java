package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.ErrorBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Parser for conversations with a SpeakerNPC This class parses strings in English language and returns them as Sentence
 * objects. All sentence constituents are in lower case.
 *
 * @author Martin Fuchs
 */
public final class ConversationParser extends ErrorBuffer {
	 
	private static final Logger LOGGER = Logger.getLogger(ConversationParser.class);
	   /** A cache to hold pre-parsed trigger Expressions. */
    private static Map<String, Expression> triggerExpressionsCache = new HashMap<String, Expression>();

    /**
     *  A cache to hold pre-parsed matching Sentences.
     */
    private static Map<String, Sentence> matchingSentenceCache = new HashMap<String, Sentence>();

    
	final String originalText;
	
    
    private final StringTokenizer tokenizer;

  


   
    /**
     * Create a new conversation parser and initialize with the given text string.
     *
     * @param text the text to parse
     */
    public ConversationParser(final String text) {
		if (text == null) {
			originalText = "";
		} else if (text.startsWith("_")) {
			// ignore sentences starting with "_", so players can talk
			// without triggering NPCs
			originalText = "";
		} else {
			originalText = text;
		}

        // initialize a new tokenizer with the given text
		tokenizer = new StringTokenizer(originalText);
    }

    
    /**
     * @return the original parsed text.
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     *
     * @param text
     * @return the sentence in normalized form.
     */
    public static String normalize(final String text) {
        return parse(text).getNormalized();
    }

 
    /**
     * Create trigger expression to match the parsed user input in the FSM engine.
     *
     * @param text
     * @return Expression
     */
    public static Expression createTriggerExpression(final String text) {
        Expression expr = triggerExpressionsCache.get(text);
        if (expr != null) {
            return expr;
        }

        expr = createTriggerExpression(text, null);

        triggerExpressionsCache.put(text, expr);

        return expr;
    }

    /**
     * Create trigger expression to match the parsed user input in the FSM engine.
     *
     * @param text
     * @param matcher
     * @return Expression
     */
    public static Expression createTriggerExpression(final String text, final ExpressionMatcher matcher) {
        // prepare context for matching
        final ConversationContext ctx = new ConvCtxForMatcher();

        // don't ignore words with type "IGN" if specified in trigger expressions
        ctx.setIgnoreIgnorable(false);

        if (matcher != null) {
            return matcher.parseSentence(text, ctx).getTriggerExpression();
        } else {
            final Expression expr = parse(text, ctx).getTriggerExpression();

            if ((expr.getMatcher() == null) && !expr.getNormalized().equals(expr.getOriginal())) {
                final WordEntry norm = WordList.getInstance().find(expr.getNormalized());

                // If the trigger type string is not the same as that of the normalized form,
                // associate an ExpressionMatcher in typeMatching mode.
                if ((norm != null) && !expr.getTypeString().equals(norm.getTypeString())) {
                    ExpressionMatcher newMatcher = new ExpressionMatcher();
                    newMatcher.setTypeMatching(true);
                    expr.setMatcher(newMatcher);
                }
            }

            return expr;
        }
    }

    /**
     * Parse function without conversation context.
     *
     * @param text
     * @return the parsed text
     */
    public static Sentence parse(final String text) {
        return parse(text, new ConversationContext());
    }

  
    /**
     * Parse the given text sentence to be used for sentence matching.
     *
     * @param text
     * @return Sentence
     */
    public static Sentence parseForMatching(final String text) {
        Sentence s = matchingSentenceCache.get(text);
        if (s != null) {
            return s;
        }

        s = parse(text, new ConvCtxForMatcher());

        matchingSentenceCache.put(text, s);

        return s;
    }

    /**
     * Parse the given text sentence using an explicit Expression matcher.
     *
     * @param text
     * @param matcher
     * @return Sentence result
     */
    public static Sentence parse(final String text, final ExpressionMatcher matcher) {
        if (matcher != null) {
            return matcher.parseSentence(text, new ConvCtxForMatcher());
        } else {
            return parse(text, new ConversationContext());
        }
    }

    /**
     * Parse the given text sentence using an explicit Expression matcher.
     *
     * @param text
     * @param ctx
     * @param matcher
     * @return Sentence result
     */
    public static Sentence parse(final String text, final ConversationContext ctx, final ExpressionMatcher matcher) {
        if (matcher != null) {
            return matcher.parseSentence(text, ctx);
        } else {
            return parse(text, ctx);
        }
    }

    /**
     * Parse the given text sentence.
     *
     * @param text
     * @param ctx
     * @return Sentence
     */
    public static Sentence parse(String text, final ConversationContext ctx) {
        if (text != null) {
        	if ((ctx != null) && ctx.isForMatching()) {
                final ExpressionMatcher matcher = new ExpressionMatcher();

                // If the text begins with matching flags, skip normal sentence parsing and read in
                // the expressions from the given string in prepared form.
                text = matcher.readMatchingFlags(text);

                if (matcher.isAnyFlagSet()) {
                    return matcher.parseSentence(text, ctx);
                }
            }

        } else {
            text = "";
        }       
    	final String ot = text;
        final SentenceImplementation sentence = new SentenceImplementation(ctx);

        try {
            // 1.) determine sentence type from trailing punctuation
            text = getSentenceType(text.trim(), sentence);

            // 2.) feed the separated words into the sentence object
            final ConversationParser parser = new ConversationParser(text);

            sentence.parse(parser);

            // 3.) classify word types and normalize words
            sentence.classifyWords(parser);

            if ((ctx != null) && ctx.getMergeExpressions()) {
                // 4.) evaluate sentence type from word order
                sentence.evaluateSentenceType();

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
        } catch (final Exception e) {
            LOGGER.error("ConversationParser.parse(): catched Exception while parsing '" + text + '\'');
            sentence.setError(e.getMessage());
            e.printStackTrace();
        }
        sentence.originalText = ot;
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
     * Evaluates and sets sentence type by looking at the trailing punctuation characters.
     * 
     *
     * @param text	the text to evaluate
     * @param sentence where the type is to be set
     * @return text without trailing or leading punctuation
     */
    public static String getSentenceType(final String text, final Sentence sentence) {
        final PunctuationParser punct = new PunctuationParser(text);

        final String trailing = punct.getTrailingPunctuation();
        
        
        if (trailing.contains("?")) {
            sentence.setType(Sentence.SentenceType.QUESTION);
            return punct.getText();
        } else if (trailing.contains("!")) {
            sentence.setType(Sentence.SentenceType.IMPERATIVE);
            return punct.getText();
        } else if (trailing.contains(".")) {
            sentence.setType(Sentence.SentenceType.STATEMENT);
            return punct.getText();
        }

        return text;
    }

}
