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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * ExpressionMatcher is used to compare Expression in various matching modes.
 *
 * @author Martin Fuchs
 */
public class ExpressionMatcher {
    public static final String PM_SEPARATOR = "|";

    // There are some patterns that can be used as leading flags in expression strings, e.g. "|EXACT|":
    static final String PM_TYPE_MATCH = "TYPE";
    static final String PM_EXACT_MATCH = "EXACT";
    static final String PM_SIMILAR_MATCH = "SIMILAR";
    static final String PM_ICASE_MATCH = "ICASE";
    static final String PM_JOKER_MATCH = "JOKER";


    /** Flag to enable type string matching. */
    protected boolean typeMatching = false;

    /** Flag to enforce exact expression matching. */
    protected boolean exactMatching = false;

    /** Flag to use similarity matching. */
    protected boolean similarMatching = false;

    /** Flag to enable case insensitive matching. */
    protected boolean caseInsensitive = false;

    /** Flag to enable joker matching. */
    protected boolean jokerMatching = false;

    /** Reset all matching flags. */
    public void clear() {
        typeMatching = false;
        exactMatching = false;
        similarMatching = false;
        caseInsensitive = false;
        jokerMatching = false;
    }

    /**
     * @param typeMatching
     *            the typeMatching flag to set
     */
    protected void setTypeMatching(final boolean typeMatching) {
        this.typeMatching = typeMatching;
    }

    /**
     * @return the typeMatching flag
     */
    protected boolean getTypeMatching() {
        return typeMatching;
    }

    /**
     * @param exactMatching
     *            the exactMatching flag to set
     */
    public void setExactMatching(final boolean exactMatching) {
        this.exactMatching = exactMatching;
    }

    /**
     * @return the exactMatching flag
     */
    public boolean getExactMatching() {
        return exactMatching;
    }

    /**
     * @param similarMatching
     *            the similarMatching flag to set
     */
    public void setSimilarMatching(final boolean similarMatching) {
        this.similarMatching = similarMatching;
    }

    /**
     * @return the similarMatching flag
     */
    public boolean getSimilarMatching() {
        return similarMatching;
    }

    /**
     * @param caseInsensitive
     *            the caseInsensitive flag to set
     */
    public void setCaseInsensitive(final boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * @return the caseInsensitive flag
     */
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    /**
     * @return the jokerMatching flag
     */
    public boolean isJokerMatching() {
        return jokerMatching;
    }

    /**
     * @param jokerMatching
     *            the jokerMatching flag to set
     */
    public void setJokerMatching(final boolean jokerMatching) {
        this.jokerMatching = jokerMatching;
    }

    /**
     * @return true if any of the available matching flags is set.
     */
    public boolean isAnyFlagSet() {
        return typeMatching || exactMatching || similarMatching || caseInsensitive || jokerMatching;
    }

    /**
     * @return true if none of the available matching flags is set.
      */
    public boolean isEmpty() {
        return !isAnyFlagSet();
    }

    /**
     * Read leading matching flags from the given text string and return the remaining text.
     *
     * @param text
     * @return text without leading flags
     */
    public String readMatchingFlags(String text) {
        clear();

        if (text.startsWith(PM_SEPARATOR)) {
            final StringTokenizer tok = new StringTokenizer(text, PM_SEPARATOR);

            while (tok.hasMoreTokens()) {
                final String flag = tok.nextToken();

                if (flag.equals(PM_TYPE_MATCH)) {
                    typeMatching = true;
                } else if (flag.equals(PM_EXACT_MATCH)) {
                    exactMatching = true;
                } else if (flag.equals(PM_SIMILAR_MATCH)) {
                    similarMatching = true;
                } else if (flag.equals(PM_ICASE_MATCH)) {
                    caseInsensitive = true;
                } else if (flag.equals(PM_JOKER_MATCH)) {
                    jokerMatching = true;
                } else {
                    break;
                }

                text = text.substring(flag.length() + 1);
            }

            if (isAnyFlagSet()) {
                // strip the last separator character
                text = text.substring(1);
            }
        }

        return text;
    }

    /**
     * Parse the given text string and create a Sentence object using the current matching flags.
     *
     * @param text
     * @param ctx
     * @return parsed Sentence
     */
    Sentence parseSentence(final String text, final ConversationContext ctx) {
        if (isEmpty()) {
            return ConversationParser.parse(text, ctx);
        }

        // Trim white space from beginning and end.
        String txt = text.trim();

        final Sentence sentence = new SentenceImplementation(ctx, txt);

        // determine sentence type from trailing punctuation
        txt = ConversationParser.detectSentenceType(txt, sentence);

        if (typeMatching) {
            readTypeMatchExpressions(txt, sentence);
        } else if (exactMatching) {
            readSimpleExpressions(txt, sentence);
        } else if (similarMatching) {
            readSimpleExpressions(txt, sentence);
        } else if (jokerMatching) {
            readJokerExpressions(txt, sentence);
        } else if (caseInsensitive) {
            readSimpleExpressions(txt, sentence);
        }

        return sentence;
    }

    /**
     * Reads in the expressions from the given string in prepared form. The given text should be in the format:
     * "&lt;expression&gt;/&lt;TYPESTRING&gt; &lt;expression&gt;/&lt;TYPESTRING&gt; ..."
     *
     * @param text to be parsed
     * @param sentence
     */
    private void readTypeMatchExpressions(final String text, final Sentence sentence) {
        final StringTokenizer tok = new StringTokenizer(text, "/");

        while (tok.hasMoreTokens()) {
            final String str = tok.nextToken();
            String typeStr;

            try {
                // remove the leading slash from the type string
                typeStr = tok.nextToken(" \t\n\r\f").substring(1);
            } catch (final NoSuchElementException e) {
                typeStr = Expression.JOKER;
            }

            final Expression expr = new Expression(str, typeStr);
            expr.setMatcher(this);
            sentence.expressions.add(expr);
        }
    }

    /**
     * Read in the words from the given string and create the Sentence object using this unchanged expressions.
     *
     * @param text to be parsed
     * @param sentence
     */
    private void readSimpleExpressions(final String text, final Sentence sentence) {
        final StringTokenizer tok = new StringTokenizer(text);

        while (tok.hasMoreTokens()) {
            final String str = tok.nextToken();

            final Expression expr = new Expression(str);
            expr.setNormalized(str);
            expr.setMatcher(this);
            sentence.expressions.add(expr);
        }
    }

    /**
     * Read in the words from the given string and create the sentence using the same rules as in SentenceImplementation
     * with activated 'forMatching' flag.
     *
     * @param text to be parsed
     * @param sentence
     */
    private void readJokerExpressions(final String text, final Sentence sentence) {
        final StringTokenizer tok = new StringTokenizer(text);

        while (tok.hasMoreTokens()) {
            final String str = tok.nextToken();

            final Expression expr = new Expression(str);

            if (ExpressionType.isTypeString(str)) {
                expr.setType(new ExpressionType(str));
                expr.setNormalized(Expression.JOKER);
            } else {
                expr.setNormalized(str);
            }

            expr.setMatcher(this);
            sentence.expressions.add(expr);
        }
    }

    /**
     * Match two Expressions using the mode in matchingFlags.
     *
     * @param expr1
     * @param expr2
     * @return true if two expression match
     */
    public boolean match(final Expression expr1, final Expression expr2) {
        // In type matching mode, the word type has to match exactly.
        if (typeMatching) {
            if (!expr1.getTypeString().equals(expr2.getTypeString())) {
                return false;
            }
        }

        // If the original expression matches, return true.
        if (expr1.getOriginal().equals(expr2.getOriginal())) {
            return true;
        }

        if (caseInsensitive) {
            if (expr1.getOriginal().equalsIgnoreCase(expr2.getOriginal())) {
                return true;
            }
        }

        if (jokerMatching) {
            return expr1.sentenceMatchExpression(expr2);
        }

        if (similarMatching) {
            if (SimilarExprMatcher.isSimilar(expr1.getOriginal(), expr2.getOriginal(), 0.1)) {
                return true;
            }
        }

        // If no exact match is required, compare the normalized expressions.
        if (!exactMatching) {
            if (expr2.getNormalized().equals(Expression.JOKER)) {
                return true;
            }

            if (expr1.getNormalized().equals(expr2.getNormalized())) {
                return true;
            }

            if (caseInsensitive) {
                if (expr1.getNormalized().equalsIgnoreCase(expr2.getNormalized())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check for equality of two ExpressionMatcher objects.
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (other instanceof ExpressionMatcher) {
            final ExpressionMatcher o = (ExpressionMatcher) other;

            if (typeMatching != o.typeMatching) {
                return false;
            } else if (exactMatching != o.exactMatching) {
                return false;
            } else if (similarMatching != o.similarMatching) {
                return false;
            } else if (caseInsensitive != o.caseInsensitive) {
                return false;
            } else {
                return (jokerMatching == o.jokerMatching);
            }

        } else {
            return false;
        }
    }

    /**
     * Returns a hash code for this ExpressionMatcher object.
     */
    @Override
    public int hashCode() {
        int hash = 0;

        if (typeMatching) {
            hash |= 1;
        }

        if (exactMatching) {
            hash |= 2;
        }

        if (similarMatching) {
            hash |= 4;
        }

        if (caseInsensitive) {
            hash |= 8;
        }

        if (jokerMatching) {
            hash |= 0x10;
        }

        return hash;
    }

    /**
     * Return a simple string representation.
     */
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();

        if (typeMatching) {
            b.append(PM_SEPARATOR);
            b.append(PM_TYPE_MATCH);
        }

        if (exactMatching) {
            b.append(PM_SEPARATOR);
            b.append(PM_EXACT_MATCH);
        }

        if (similarMatching) {
            b.append(PM_SEPARATOR);
            b.append(PM_SIMILAR_MATCH);
        }

        if (caseInsensitive) {
            b.append(PM_SEPARATOR);
            b.append(PM_ICASE_MATCH);
        }

        if (jokerMatching) {
            b.append(PM_SEPARATOR);
            b.append(PM_JOKER_MATCH);
        }

        return b.toString();
    }

}
