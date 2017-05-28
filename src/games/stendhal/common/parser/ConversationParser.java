/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import games.stendhal.common.ErrorBuffer;

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

    /** A cache to hold pre-parsed matching Sentences. */
    private static Map<String, Sentence> matchingSentenceCache = new HashMap<String, Sentence>();

    /** Tokenizer to split a sentence into words */
    private final transient StringTokenizer tokenizer;


    /**
     * Create a new conversation parser and initialise with the given sentence.
     *
     * @param sentence
     */
    protected ConversationParser(final SentenceImplementation sentence) {
    	String text = sentence.getOriginalText();

		if (text == null) {
			text = "";
		} else if (text.startsWith("_")) {
			// ignore sentences starting with "_", so players can talk
			// without triggering NPCs
			text = "";
		}

		final String textWithoutPunctation = detectSentenceType(text, sentence);

        // initialise a new tokenizer with the given text
		tokenizer = new StringTokenizer(textWithoutPunctation);
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

        if (expr == null) {
        	expr = createTriggerExpression(text, null);

        	triggerExpressionsCache.put(text, expr);
        }

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

        Expression expr;

        if (matcher == null) {
            expr = parse(text, ctx).getTriggerExpression();

            if ((expr.getMatcher() == null) && !expr.getNormalized().equals(expr.getOriginal())) {
                final WordEntry norm = WordList.getInstance().find(expr.getNormalized());

                // If the trigger type string is not the same as that of the normalized form,
                // associate an ExpressionMatcher in typeMatching mode.
                if ((norm != null) && !expr.getTypeString().equals(norm.getTypeString())) {
                     expr.setMatcher(new TypeExprMatcher());
                }
            }
        } else {
            expr = matcher.parseSentence(text, ctx).getTriggerExpression();
        }

        return expr;
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
     * Parse the given text sentence to be used as matching source.
     *
     * @param text
     * @return the parsed text
     */
    public static Sentence parseAsMatchingSource(final String text) {
    	Sentence sentence = parse(text, new ConvCtxForMatchingSource());

    	return sentence;
    }

    /**
     * Parse the given text sentence to be used for sentence matching.
     *
     * @param text
     * @return the parsed text
     */
    public static Sentence parseAsMatcher(final String text) {
        Sentence sentence = matchingSentenceCache.get(text);

        if (sentence == null) {
        	sentence = parse(text, new ConvCtxForMatcher());

        	matchingSentenceCache.put(text, sentence);
        }

        return sentence;
    }

    /**
     * Parse the given text sentence using an explicit Expression matcher.
     *
     * @param text
     * @param matcher
     * @return the parsed text
     */
    public static Sentence parse(final String text, final ExpressionMatcher matcher) {
    	Sentence sentence;

        if (matcher == null) {
            sentence = parse(text, new ConversationContext());
        } else {
            sentence = matcher.parseSentence(text, new ConvCtxForMatcher());
        }

        return sentence;
    }

    /**
     * Parse the given text sentence using an ConversationContext and an
     * explicit Expression matcher.
     *
     * @param text
     * @param ctx
     * @param matcher
     * @return the parsed text
     */
    public static Sentence parse(final String text, final ConversationContext ctx, final ExpressionMatcher matcher) {
    	Sentence sentence;

        if (matcher == null) {
        	sentence = parse(text, ctx);
        } else {
            sentence = matcher.parseSentence(text, ctx);
        }

        return sentence;
    }

    /**
     * Parse the given text sentence.
     *
     * @param text
     * @param ctx
     * @return the parsed text
     */
    public static Sentence parse(final String text, final ConversationContext ctx) {
    	String txt;

        if (text == null) {
        	txt = "";
        } else {
        	if ((ctx != null) && ctx.isForMatching()) {
                final ExpressionMatcher matcher = new ExpressionMatcher();

                // If the text begins with matching flags, skip normal sentence parsing and read in
                // the expressions from the given string in prepared form.
                txt = matcher.readMatchingFlags(text);

                if (matcher.isAnyFlagSet()) {
                    return matcher.parseSentence(txt, ctx);
                }
            } else {
            	txt = text;
            }
        }

        // Trim white space from beginning and end.
        txt = txt.trim();

        // Create a Sentence object and initialise its originalText.
        final SentenceImplementation sentence = new SentenceImplementation(ctx, txt);

        try {
            // 1.) create ConversationParser
            // This determines the sentence type from trailing punctuation
        	// and feeds the separated words into the sentence object.
            final ConversationParser parser = new ConversationParser(sentence);

            sentence.parse(parser);

            // 2.) classify word types and normalise words
            sentence.classifyWords(parser);

            if ((ctx != null) && ctx.getMergeExpressions()) {
                // 3.) evaluate sentence type from word order
                sentence.evaluateSentenceType();

                // 4.) merge words to form a simpler sentence structure
                sentence.mergeWords();

                if (!ctx.isForMatching()) {
                    // 5.) standardise sentence type
                    sentence.standardizeSentenceType();

                    // 6.) replace grammatical constructs with simpler ones
                    sentence.performaAliasing();
                }
            }

            sentence.setError(parser.getErrorString());
        } catch (final Exception e) {
            LOGGER.error("ConversationParser.parse(): caught Exception while parsing '" + text + '\'');
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
     * Evaluates and sets sentence type by looking at the trailing punctuation characters.
     *
     * @param text	the text to evaluate
     * @param sentence where the type is to be set
     * @return text without trailing or leading punctuation
     */
    public static String detectSentenceType(final String text, final Sentence sentence) {
        final PunctuationParser punct = new PunctuationParser(text);
        final String trailing = punct.getTrailingPunctuation();

        String newText;

        if (trailing.contains("?")) {
            sentence.setType(Sentence.SentenceType.QUESTION);
            newText = punct.getText();
        } else if (trailing.contains("!")) {
            sentence.setType(Sentence.SentenceType.IMPERATIVE);
            newText = punct.getText();
        } else if (trailing.contains(".")) {
            sentence.setType(Sentence.SentenceType.STATEMENT);
            newText = punct.getText();
        } else {
        	newText = text;
        }

        return newText;
    }

}
