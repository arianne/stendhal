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

/**
 * SimilarExprMatcher creates an ExpressionMatcher with similarity matching.
 *
 * @author Martin Fuchs
 */
public class SimilarExprMatcher extends ExpressionMatcher {

    public SimilarExprMatcher() {
        similarMatching = true;
    }

    /**
     * Check for string similarity giving a limit quotient. Comparison is case insensitive.
     *
     * @param str1
     * @param str2
     * @param delta
     * @return true if similar
     */
    public static boolean isSimilar(final String str1, final String str2, final double delta) {
        // If any of the Strings is null, return only true if both are null.
        if ((str1 == null) || (str2 == null)) {
            return (str1 == null) &&  (str2 == null);
        }

        final int len1 = str1.length();
        final int len2 = str2.length();

        // If any of the Strings is empty, return only true if both are empty.
        if ((len1 == 0) || (len2 == 0)) {
            return len1 == len2;
        }

        final int shorterLength = Math.min(len1, len2);
        final int limit = (int) ((shorterLength * delta * shorterLength + (shorterLength - 1)) / shorterLength);

        return compareLevenshtein(str1, str2, limit);
    }

    /**
     * Calculate the number of differing characters between to non-null strings. Comparison is case insensitive.
     *
     * @param str1
     * @param str2
     * @param limit
     *            maximum allowed distance
     * @return similarity flag
     */
    private static boolean compareLevenshtein(final String str1, final String str2, final int limit) {
        // shortcut for equal Strings
        if (str1.equalsIgnoreCase(str2)) {
            return true;
        } else if (limit < 1) {
            return false;
        } else {
            final int dist = limitedLevenshtein(str1.toLowerCase(), str2.toLowerCase(), 0, 0, limit);

            return dist <= limit;
        }
    }

    /**
     * Calculate the Levenshtein distance of two strings using index arithmetic given a maximum distance value to
     * terminate calculation. This is not exactly the Levenshtein algorithm, but fits our need to quickly look for
     * similar strings.
     *
     * @param str1
     * @param str2
     * @param idx1
     *            current index
     * @param idx2
     *            current index
     * @param limit
     *            maximum allowed distance
     * @return number of differing chars
     */
    private static int limitedLevenshtein(final String str1, final String str2, int idx1, int idx2, final int limit) {
        // As long as the current characters match, we can go straight forward.
        // Stop if we reach the end of one of the strings or find a difference.
        while (true) {
            if (idx1 == str1.length()) {
                return str2.length() - idx2;
            } else if (idx2 == str2.length()) {
                return str1.length() - idx1;
            } else if (str1.charAt(idx1) != str2.charAt(idx2)) {
                // If we reach here, the distance is at least 1, so break if limit is less.
                if (limit < 1) {
                    return 1;
                } else {
                    return 1 + bestLimLev(str1, str2, idx1, idx2, limit - 1);
                }
            }

            ++idx1;
            ++idx2;
        }
    }

    /**
     * Called the limited Levenstein distance for two differing strings.
     * @param str1
     * @param str2
     * @param idx1
     * @param idx2
     * @param limit
     * @return the distance
     */
    private static int bestLimLev(final String str1, final String str2, final int idx1, final int idx2, final int limit) {
        // check for a replaced character
        final int d1 = limitedLevenshtein(str1, str2, idx1 + 1, idx2 + 1, limit);

        // Shortcut, no other result can be better than 0.
        if (d1 == 0) {
            return 0;
        }

        // check for an inserted character
        final int d2 = limitedLevenshtein(str1, str2, idx1 + 1, idx2, limit);

        // Shortcut, no result can be better than 0.
        if (d2 == 0) {
            return 0;
        }

        // check for a removed character
        final int d3 = limitedLevenshtein(str1, str2, idx1, idx2 + 1, limit);

        // Return the best of the three results.
        return min(d1, d2, d3);
    }

    private static int min(final int d1, final int d2, final int d3) {
        int d = d1;

        if (d2 < d) {
            d = d2;
        }

        if (d3 < d) {
            d = d3;
        }

        return d;
    }

}
