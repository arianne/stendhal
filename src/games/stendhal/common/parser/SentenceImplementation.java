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

import java.util.Iterator;

import games.stendhal.common.ErrorDrain;
import games.stendhal.common.grammar.Grammar;

/**
 * SentenceImplementation contains the implementation details of building Sentence objects.
 *
 * @author Martin Fuchs
 */
public final class SentenceImplementation extends Sentence {

    /**
     * Create a SentenceImplementation object in preparation to parse a text phrase.
     *
     * @param ctx
     * @param text phrase
     */
    SentenceImplementation(final ConversationContext ctx, String text) {
        super(ctx);

        originalText = text;
    }

    /**
     * Create a SentenceImplementation object for testing purposes.
     * note: This constructor does not set originalText.
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

            // If the parsed Sentence will be used for matching, look for ExpressionType specifiers.
            if (context.isForMatching()) {
                if (ExpressionType.isTypeString(original)) {
                    w.setType(new ExpressionType(original));
                    w.setNormalized(Expression.JOKER);
                }
            }

            boolean wordFound = w.getType()!=null;
            boolean entryMissing = false;

            if (!wordFound) {
            	WordEntry entry = wl.find(original);

            	if (entry == null) {
            		entryMissing = true;
            	} else if (entry.getType() != null) {
            		ExpressionType type = entry.getType();

	                w.setType(type);
	                wordFound = true;

	                if (type.isNumeral()) {
	                    // evaluate numeric expressions
	                    w.setAmount(entry.getValue());
	                    w.setNormalized(Integer.toString(w.getAmount()));
	                } else if (type.isPlural()) {
	                    // normalise to the singular form
	                    // If getPlurSing() is null, there is no unique singular form, so use the original string.
	                    if (entry.getPlurSing() != null) {
	                        w.setNormalized(entry.getPlurSing());
	                    } else {
	                        w.setNormalized(original);
	                    }
	                } else {
	                    w.setNormalized(entry.getNormalized());
	                }
	            }
            }

            if (!wordFound) {
                // handle numeric expressions
                if (original.matches("^[+-]?[0-9.,]+")) {
                    w.parseAmount(original, errors);
                    final int amount = w.getAmount();
                    if (amount < 0) {
                        errors.setError("negative amount: " + amount);
                    }
                    wordFound = w.getType()!=null;
                }
            }

            // handle unknown words
            if (!wordFound) {
                // recognise declined verbs, e.g. "swimming"
                final WordList.Verb verb = wl.normalizeVerb(original);

                if (verb != null) {
                    if (verb.isGerund) {
                        w.setType(new ExpressionType(verb.entry.getTypeString() + ExpressionType.SUFFIX_GERUND));
                        wordFound = true;
                    } else if ((verb.entry.getType() != null) && verb.entry.getType().isVerb()) {
                        w.setType(verb.entry.getType());
                        wordFound = true;
                    } else if (!verb.isPast) { // avoid cases like "rounded"
                    	w.setType(new ExpressionType(ExpressionType.VERB));
                        wordFound = true;
                    }

                    if (wordFound) {
                    	w.setNormalized(verb.entry.getNormalized());
                    }
                }
            }

            if (!wordFound) {
                // recognise derived adjectives, e.g. "magical", "nomadic" or "rounded"
                final WordEntry adjective = wl.normalizeAdjective(original);

                if (adjective != null) {
                	w.setType(new ExpressionType(ExpressionType.ADJECTIVE));
                    w.setNormalized(adjective.getNormalized());
                    wordFound = true;
                }
            }

            if (!wordFound) {
                w.setType(new ExpressionType(""));
                w.setNormalized(original.toLowerCase());

                if (entryMissing) {
                    // Add the unknown word to the word list.
                    wl.addNewWord(original);
                }
            }

            assert w.getType()!=null;
        }
    }

    /**
     * Standardise sentence type.
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
                            final Expression you = new Expression("you", ExpressionType.SUBJECT);
                            expressions.add(0, you);
                            sentenceType = SentenceType.IMPERATIVE;
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
        	// the Sentence matches "do you have OBJ for me?"
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
            // Note: The second subject "me" is replaced by "i" in the WordList normalisation.
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
     * Evaluate the sentence type from word order and
     * remove do/don't expressions from questions.
     *
     * @return SentenceType
     */
    SentenceType evaluateSentenceType() {
        final Iterator<Expression> it = expressions.iterator();
        SentenceType type = SentenceType.UNDEFINED;
        boolean negate = false;

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

                    // Remove all verbs meaning "do" from the expression list,
                    // since they don't change the sentence meaning.
                    if (first.isNegated()) {
                    	// If the question uses a "don't" expression and there is another verb in the
                    	// sentence, negate this and drop the "don't".
                    	if (getVerbCount() > 1) {
                    		negate = true;
                    		expressions.remove(first);
                    	}
                    } else {
						expressions.remove(first);
					}
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

        if (negate) {
        	// negate the first verb if the sentence did contain a "don't" expression
        	Expression firstVerb = getVerb(0);

        	if (firstVerb != null) {
        		firstVerb.negate();
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
        // use WordList.compoundNames to merge compound names
    	mergeCompoundNames();

        // first merge three word expressions of the form "... of ..."
        mergeThreeWordExpressions();

        // now merge two word expressions from left to right
        if (mergeTwoWordExpressions() > 0) {
	        // retry finding three word expressions
	        mergeThreeWordExpressions();
        }
    }

    /**
     * Merge compound names.
     * @return number of merges performed
     */
	public int mergeCompoundNames() {
    	final WordList wl = WordList.getInstance();
        int changes = 0;

        // loop until no more simplifications can be made
    	boolean changed;
        do {
            changed = false;

            // loop over all words of the sentence starting from left
            for(int idx=0; idx<expressions.size()-1; ++idx) {
                // search for matching compound names
                CompoundName compName = wl.searchCompoundName(expressions, idx);

                if (compName != null) {
                    Expression first = expressions.get(idx);

        			int wordsMatched = compName.size();
        			for(int i=1; i<wordsMatched; ++i) {
        				Expression next = expressions.get(idx+1);

            			first.mergeName(next, compName.getType());
            	        expressions.remove(next);
        			}

        	        changed = true;
                    break;
    			}

        		if (changed) {
        			++changes;
        			break;
        		}
            }
        } while(changed);

        return changes;
    }

    /**
     * Merge two word expressions into single expressions.
     * @return number of merges performed
     */
    private int mergeTwoWordExpressions() {

        /*
         * There are two possibilities for word merges: Left-merging means to
         * prepend the left word before the following one, removing the first
         * one. Right-merging means to append the right word to the preceding
         * one, removing the second from the word list.
         */

        int changes = 0;

        // loop until no more simplifications can be made
        boolean changed;
        do {
            changed = false;

            final Iterator<Expression> it = expressions.iterator();

            boolean prevConditional = false;
            boolean precedingVerb = false;

            if (it.hasNext()) {
                Expression next = it.next();
//				Expression prev = null;

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
                        	if (Grammar.mergeCompoundNoun(curr, next) == curr) {
                        		expressions.remove(next);
                        	} else {
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
                        // check consecutive verbs
                        else if (curType.isVerb() && nextType.isVerb()) {
                            // merge "do" and "don't" expressions with the following verb
                            if (curr.getNormalized().equals("do")) {
                            	next.mergeSimple(curr);
                                expressions.remove(curr);
                                changed = true;
                                break;
                            }
                            // left-merge "would like", preserving only the main verb
                            else if (prevConditional) {
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
                        // dropping question words from the normalized form
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

                    // manage the precedingVerb flag to detect compound verb/noun constructs
                    if (curr.getBreakFlag()) {
                        precedingVerb = false;
                    } else if (curType != null) {
                        if (curType.isVerb()) {
                            precedingVerb = true;
                        } else if (curType.isSubject()) {
                            precedingVerb = false;
                        }
                    }

//                  prev = curr;
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
        // check the next expression type for concrete subject or object expressions (no pronouns)
        final boolean nextIsSubjObjName = nextType.isObject() || (nextType.isSubject() && !nextType.isPronoun());

        // left-merge composite nouns and nouns with preceding adjectives or verbs
        if (nextIsSubjObjName) {
            // check the current expression type for concrete subject or object expressions (no pronouns), excluding subject names
            final boolean currIsSubjObj = curType.isObject() || (curType.isSubject() && !curType.isPronoun() && !curType.isName());

            // handle compound words like "fire sword"
            if (currIsSubjObj) {
                return true;
            }
            // handle compound words like "golden sword"
            else if (curType.isAdjective()) {
                return true;
            }
            // handle compound words like "summon scroll"
            else if (curType.isVerb() && precedingVerb) {
                return true;
            }
        }

        return false;
    }

    /**
     * Merge three word expressions of the form "... of ..." into single expressions.
     * @return number of merges performed
     */
    private int mergeThreeWordExpressions() {
        int changes = 0;

        // loop until no more simplifications can be made
        boolean changed;
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

                            // see if the expression has been normalized
                            if (!Grammar.isNormalized(expr)) {
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
