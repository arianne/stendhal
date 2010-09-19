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
package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.ErrorDrain;
import games.stendhal.common.Grammar;

import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * SentenceImplementation contains the implementation details of building Sentence objects.
 *
 * @author Martin Fuchs
 */
public final class SentenceImplementation extends Sentence {

    private static final Logger logger = Logger.getLogger(SentenceImplementation.class);

    /**
     * Create a SentenceImplementation object.
     *
     * @param ctx
     */
    SentenceImplementation(final ConversationContext ctx) {
        super(ctx);
    }

    /**
     * Create a SentenceImplementation object for testing purposes.
     *
     * @param exprs
     */
    public SentenceImplementation(final Expression... exprs) {
        super(new ConversationContext());

        for (final Expression e : exprs) {
            expressions.add(e);
        }
    }

    /**
     * Build sentence by using the given parser object.
     *
     * @param parser
     */
    void parse(final ConversationParser parser) {
        originalText = parser.getOriginalText();

        Expression prevWord = null;

        for (String ws; (ws = parser.readNextWord()) != null;) {
            // replace "and" by enumerations separated by break flags
            if (ws.equals("and")) {
                if (prevWord != null) {
                    prevWord.setBreakFlag();
                }
            } else {
                final PunctuationParser punct = new PunctuationParser(ws);

                final String precedingPunct = punct.getPrecedingPunctuation();
                String text = punct.getText();

                // avoid to trim leading decimal points from numbers
                if ((precedingPunct.length() > 0) && text.matches("[0-9.,]+")) {
                    text = ws;
                }

                // handle preceding comma characters
                if (precedingPunct.contains(",")) {
                    if (prevWord != null) {
                        // break sentence after the previous word
                        prevWord.setBreakFlag();
                    }
                }

                final Expression word = new Expression(text);
                expressions.add(word);

                // handle trailing comma characters
                if (punct.getTrailingPunctuation().contains(",")) {
                    // break sentence after the current word
                    word.setBreakFlag();
                }

                prevWord = word;
            }
        }
    }

    /**
     * Classify word types and normalize words.
     *
     * @param errors
     */
    void classifyWords(final ErrorDrain errors) {
        final WordList wl = WordList.getInstance();

        for (final Expression w : expressions) {
            final String original = w.getOriginal();
            WordEntry entry = null;

            // If the parsed Sentence will be used for matching, look for ExpressionType specifiers.
            if (context.isForMatching()) {
                if (ExpressionType.isTypeString(original)) {
                    w.setType(new ExpressionType(original));
                    w.setNormalized(Expression.JOKER);
                }
            }

            if (w.getType() == null) {
                entry = wl.find(original);
            }

            if ((entry != null) && (entry.getType() != null)) {
                w.setType(entry.getType());

                if (entry.getType().isNumeral()) {
                    // evaluate numeric expressions
                    w.setAmount(entry.getValue());
                    w.setNormalized(Integer.toString(w.getAmount()));
                } else if (entry.getType().isPlural()) {
                    // normalize to the singular form
                    // If getPlurSing() is null, there is no unique singular form, so use the original string.
                    if (entry.getPlurSing() != null) {
                        w.setNormalized(entry.getPlurSing());
                    } else {
                        w.setNormalized(original);
                    }
                } else {
                    w.setNormalized(entry.getNormalized());
                }
            } else {
                // handle numeric expressions
                if (original.matches("^[+-]?[0-9.,]+")) {
                    w.parseAmount(original, errors);
                    final int amount = w.getAmount();

                    if (amount < 0) {
                        errors.setError("negative amount: " + amount);
                    }
                }
            }

            // handle unknown words
            if (w.getType() == null) {
                // recognize declined verbs, e.g. "swimming"
                final WordEntry verb = wl.normalizeVerb(original);

                if (verb != null) {
                    if (Grammar.isGerund(original)) {
                        w.setType(new ExpressionType(verb.getTypeString() + ExpressionType.SUFFIX_GERUND));
                    } else {
                        w.setType(verb.getType());
                    }

                    w.setNormalized(verb.getNormalized());
                } else {
                    // recognize derived adjectives, e.g. "magical" or "nomadic"
                    final WordEntry adjective = wl.normalizeAdjective(original);

                    if (adjective != null) {
                        if (Grammar.isDerivedAdjective(original)) {
                            w.setType(new ExpressionType(ExpressionType.ADJECTIVE));
                        } else {
                            // If normalizeAdjective() changed the word, it should be a derived adjective.
                            logger.error("SentenceImplementation.classifyWords(): unexpected normalized adjective '"
                                    + adjective + "' of original '" + original + "'");
                            w.setType(adjective.getType());
                        }

                        w.setNormalized(adjective.getNormalized());
                    } else {
                        w.setType(new ExpressionType(""));
                        w.setNormalized(original.toLowerCase());

                        if (entry == null) {
                            // Don't persist expressions used for joker matching.
                            final boolean persist = context.getPersistNewWords()
                                    && (!context.isForMatching() || !original.contains(Expression.JOKER));

                            // Add the unknown word to the word list.
                            wl.addNewWord(original, persist);
                        }
                    }
                }
            }
        }
    }

    /**
     * Standardize sentence type.
     */
    void standardizeSentenceType() {
        // Look for a "me" without any preceding other subject.
        Expression prevVerb = null;

        for (final Expression w : expressions) {
            if (w.getBreakFlag()) {
                break;
            }

            if (w.getType() != null) {
                if (w.getType().isVerb()) {
                    if (prevVerb == null) {
                        prevVerb = w;
                    } else {
                        break;
                    }
                } else if (w.getType().isSubject()) {
                    if (w.getOriginal().equalsIgnoreCase("me")) {
                        // If we already found a verb, we prepend "you" as
                        // first subject and mark the sentence as imperative.
                        if (prevVerb != null) {
                        	//TODO The following line as an ugly hack to let Gordon recognize statements like "rent Me ...".
                        	// It should be replaced by using sentence matching "[you] rent" in SignLessorNPC.
                        	if (!prevVerb.getNormalized().equals("rent")) {
	                            final Expression you = new Expression("you", ExpressionType.SUBJECT);
	                            expressions.add(0, you);
	                            sentenceType = SentenceType.IMPERATIVE;
                        	}
                        }
                    }

                    break;
                }
            }
        }
    }

    /**
     * replace grammatical constructs with simpler ones with the same meaning, so that they can be understood by the FSM
     * rules TODO mf - This grammatical aliasing is only a first step to more flexible NPC conversation. It should be
     * integrated with the FSM engine so that quest writers can specify the conversation syntax on their own.
     */
    void performaAliasing() {
        final Expression verb1 = getVerb(0);
        final Expression verb2 = getVerb(1);
        final Expression subject1 = getSubject(0);
        final Expression subject2 = getSubject(1);

        // Does the Sentence start with a "will/would SUBJECT VERB" construct?
        if (matchesNormalizedStart("will SUB VER")) {
            // merge the second verb with "will/would", removing the first one
            verb2.mergeLeft(verb1, false);
            expressions.remove(verb1);
            sentenceType = SentenceType.QUESTION;
        } else if (matchesNormalized("you have OBJ for me")) {
        	// the Sentence matches "do you have OBJ for me?"?
            // remove "you"
            expressions.remove(subject1);
            // remove "for"
            expressions.remove(getPreposition(0));
            // remove "me"
            expressions.remove(subject2);
            // replace "have" by "buy"
            verb1.setNormalized("buy");
            sentenceType = SentenceType.IMPERATIVE;
        } else if (isYouGiveMe(subject1, verb1, subject2)) {
        	// the sentence matches "[you] give me(i)" -> "[I] buy"
        	
            // remove the subjects and replace the verb with "buy" as first word
            // remove "you"
            expressions.remove(subject1);
            // remove "me"
            expressions.remove(subject2);
            // replace "give" by "buy"
            verb1.setNormalized("buy");
            sentenceType = SentenceType.IMPERATIVE;
        }

        // "[SUBJECT] (would like to have)" -> "[SUBJECT] buy"
        if (isLikeToHave()) {
            // replace the verb with "buy"
            getVerb().setNormalized("buy");
            sentenceType = SentenceType.IMPERATIVE;
        }
    }

    /**
     * Is the sentence in the form "[you] give me(i)" ?
     *
     * @param subject1
     * @param verb
     * @param subject2
     * @return true for match
     */
    private static boolean isYouGiveMe(final Expression subject1, final Expression verb, final Expression subject2) {
        if ((verb != null) && (subject1 != null) && (subject2 != null)) {
            // Note: The second subject "me" is replaced by "i" in the WordList normalization.
            if (subject1.getNormalized().equals("you") && verb.getNormalized().equals("give")
                    && subject2.getNormalized().equals("i")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Is the sentence in the form "[SUBJECT] (would like to have)"?
     *
     * @return true for match
     */
    private boolean isLikeToHave() {
        final Expression verb = getVerb();

        if (verb != null) {
            if (verb.getNormalized().equals("have") && verb.getOriginal().contains("like")) {
                final Expression subject1 = getSubject(0);
                final Expression firstExpression = expressions.get(0);
                final Expression secondExpression = expressions.get(1);

                // "(would like to have)" ?
                if ((subject1 == null) && (verb == firstExpression)) {
                    return true;
                }

                // "SUBJECT (would like to have)" ?
                if ((subject1 == firstExpression) && (verb == secondExpression)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Evaluate the sentence type from word order.
     *
     * @return SentenceType
     */
    SentenceType evaluateSentenceType() {
        final Iterator<Expression> it = expressions.iterator();
        SentenceType type = SentenceType.UNDEFINED;

        // As words are not yet merged together at this stage, we have to use Expression.nextValid()
        // in this function to jump over words to ignore.
        Expression first = nextValid(it);

        if (first != null) {
            while ((first != null) && first.isQuestion() && it.hasNext()) {
                if (type == SentenceType.UNDEFINED) {
                    type = SentenceType.QUESTION;
                }

                first = nextValid(it);
            }

            Expression second = null;
            Expression third = null;

            second = nextValid(it);
            third = nextValid(it);

            if (second != null) {
                // questions beginning with "is"/"are"
                if (first.getNormalized().equals("is")) {
                    if (type == SentenceType.UNDEFINED) {
                        type = SentenceType.QUESTION;
                    }
                } else if (first.getNormalized().equals("do")
                        && (!second.getOriginal().equalsIgnoreCase("me"))) {
                    // question begins with "do", but no "do me" sentence
                    if (type == SentenceType.UNDEFINED) {
                        type = SentenceType.QUESTION;
                    }

                    expressions.remove(first);
                } else if (first.getNormalized().equals("it") && second.getNormalized().equals("is")
                        && ((third != null) && (third.getType() != null) && third.getType().isGerund())) {
                    // statement begins with "it is <VER-GER>"
                    if (type == SentenceType.UNDEFINED) {
                        type = SentenceType.STATEMENT;
                    }

                    expressions.remove(first);
                    expressions.remove(second);
                }
            }
        }

        if ((type != SentenceType.UNDEFINED) && (sentenceType == SentenceType.UNDEFINED)) {
            sentenceType = type;
        }

        return type;
    }

    /**
     * Merge words to form a simpler sentence structure.
     */
    void mergeWords() {

        // TODO mf - use WordList.compoundNames to merge compound names

        // first merge three word expressions of the form "... of ..."
        mergeThreeWordExpressions();

        // now merge two word expressions from left to right
        if (mergeTwoWordExpressions() > 0) {
	        // retry finding three word expressions
	        mergeThreeWordExpressions();
        }
    }

    /**
     * Merge two word expressions into single expressions.
     * @return number of changes
     */
    private int mergeTwoWordExpressions() {

        /*
         * There are two possibilities for word merges: Left-merging means to
         * prepend the left word before the following one, removing the first
         * one. Right-merging means to append the right word to the preceding
         * one, removing the second from the word list.
         */

        boolean changed;
        int changes = 0;

        // loop until no more simplification can be made
        do {
            changed = false;

            final Iterator<Expression> it = expressions.iterator();

            boolean prevConditional = false;
            boolean precedingVerb = false;

            if (it.hasNext()) {
                Expression next = it.next();

                // loop over all words of the sentence starting from left
                while (it.hasNext()) {
                    // Now look at two consecutive words.
                    final Expression curr = next;
                    next = it.next();

                    // don't merge if the break flag is set
                    if (curr.getBreakFlag()) {
                        continue;
                    }

                    // don't merge if there are joker expressions
                    if (context.isForMatching()) {
                        if (curr.getNormalized().contains(Expression.JOKER)
                                || next.getNormalized().contains(Expression.JOKER)) {
                            continue;
                        }
                    }

                    final ExpressionType curType = curr.getType();
                    final ExpressionType nextType = next.getType();

                    if ((curType != null) && curType.isConditional()) {
                        prevConditional = true;
                    }

                    if ((curType != null) && (nextType != null)) {
                        // left-merge composite nouns and nouns with preceding adjectives or verbs
                        if (isCompoundNoun(curType, nextType, precedingVerb)) {
                            // special case for "ice cream" -> "ice"
                            if (curr.getNormalized().equals("ice") && next.getNormalized().equals("cream")) {
                                curr.mergeRight(next, true);
                                expressions.remove(next);
                            } else {
                                next.mergeLeft(curr, true);
                                expressions.remove(curr);
                            }
                            changed = true;
                            break;
                        }
                        // left-merge nouns with preceding amounts, dropping the numerals from the
                        // merged normalized expression
                        else if (curType.isNumeral() && (nextType.isObject() || nextType.isSubject())) {
                            next.mergeLeft(curr, false);
                            expressions.remove(curr);
                            changed = true;
                            break;
                        }
                        // left-merge "would like", preserving only the main verb
                        else if (curType.isVerb() && nextType.isVerb()) {
                            if (prevConditional) {
                                next.mergeLeft(curr, false);
                                expressions.remove(curr);
                                changed = true;
                                break;
                            }
                        }
                        // right-merge consecutive words of all other same main types,
                        // while merging the normalized expressions
                        else if (curType.getMainType().equals(nextType.getMainType())) {
                            curr.mergeRight(next, true);
                            expressions.remove(next);
                            changed = true;
                            break;
                        }
                        // left-merge question words with following verbs and adjectives,
                        // dropping question words from normalized form
                        else if (curType.isQuestion() && (nextType.isVerb() || nextType.isAdjective())) {
                            next.mergeLeft(curr, false);
                            expressions.remove(curr);
                            changed = true;
                            break;
                        }
                    }

                    // left-merge words to ignore
                    if (context.getIgnoreIgnorable()) {
                        if ((curType != null) && isIgnorable(curr)) {
                            next.mergeLeft(curr, false);
                            expressions.remove(curr);
                            changed = true;
                            break;
                        }
                    }

                    // manage precedingVerb flag to detect compound verb/noun constructs
                    if (curr.getBreakFlag()) {
                        precedingVerb = false;
                    } else if (curType != null) {
                        if (curType.isVerb()) {
                            precedingVerb = true;
                        } else if (curType.isSubject()) {
                            precedingVerb = false;
                        }
                    }
                }
            }

            if (changed) {
            	++changes;
            }
        } while (changed);

        return changes;
    }

    /**
     * Decide if the given two expressions form a compound noun.
     *
     * @param curType
     * @param nextType
     * @param precedingVerb
     * @return true if so
     */
    private static boolean isCompoundNoun(final ExpressionType curType, final ExpressionType nextType,
            final boolean precedingVerb) {
        // check the next expression type for concrete subject or object names (no pronouns)
        final boolean nextIsName = nextType.isObject() || (nextType.isSubject() && !nextType.isPronoun());

        // left-merge composite nouns and nouns with preceding adjectives or verbs
        if (nextIsName) {
            // check the current expression type for concrete subject or object names (no pronouns)
            final boolean currIsName = curType.isObject() || (curType.isSubject() && !curType.isPronoun());

            // handle compound words like "fire sword"
            if (currIsName) {
                return true;
            }

            // handle compound words like "golden sword"
            if (curType.isAdjective()) {
                return true;
            }

            // handle compound words like "summon scroll"
            if (curType.isVerb() && precedingVerb) {
                return true;
            }
        }

        return false;
    }

    /**
     * Merge three word expressions of the form "... of ..." into single expressions.
     * @return number of changes
     */
    private int mergeThreeWordExpressions() {
        boolean changed;
        int changes = 0;

        // loop until no more simplification can be made
        do {
            final Iterator<Expression> it = expressions.iterator();

            changed = false;

            if (it.hasNext()) {
                Expression third = it.next();

                if (it.hasNext()) {
                    Expression first = null;
                    Expression second = third;
                    third = it.next();

                    // loop over all words of the sentence starting from left
                    while (it.hasNext()) {
                        // Now look at three consecutive words.
                        first = second;
                        second = third;
                        third = it.next();

                        // don't merge if the break flag is set
                        if (first.getBreakFlag() || second.getBreakFlag()) {
                            continue;
                        }

                        // don't merge if there are joker expressions
                        if (context.isForMatching()) {
                            if (first.getNormalized().contains(Expression.JOKER)
                                    || second.getNormalized().contains(Expression.JOKER)
                                    || third.getNormalized().contains(Expression.JOKER)) {
                                continue;
                            }
                        }

                        // merge "... of ..." expressions into one expression, preserving
                        // only the main word as merged normalized expression
                        if (first.isObject() && second.getNormalized().equals("of") && third.isObject()) {
                            final String expr = first.getNormalized() + " of " + third.getNormalized();
                            final String normalizedExpr = Grammar.extractNoun(expr);

                            // see if the expression has been normalized
                            if (normalizedExpr != expr) {
                                first.mergeRight(second, false);
                                expressions.remove(second);
                                third.mergeLeft(first, false);
                                expressions.remove(first);
                                changed = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (changed) {
            	++changes;
            }
        } while (changed);

        return changes;
    }

}
