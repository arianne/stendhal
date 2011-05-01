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

/**
 * This utility class is used to create string representations of sentences by separating words by space characters.
 *
 * @author Martin Fuchs
 */
public final class SentenceBuilder {
    private final StringBuilder builder = new StringBuilder();
    private boolean first = true;
    private char space = ' ';

    public SentenceBuilder() {
        space = ' ';
    }

    public SentenceBuilder(final char separator) {
        space = separator;
    }

    /**
     * Append string separated by space.
     *
     * @param s
     */
    public void append(final String s) {
        if (first) {
            first = false;
        } else {
            builder.append(space);
        }

        builder.append(s);
    }

    /**
     * Directly append the given character.
     *
     * @param c
     */
    public void append(final char c) {
        builder.append(c);
    }

    /**
     * Append a sequence of Expressions until we find a break flag or there is no more Expression.
     *
     * @param it
     *            Expression iterator
     * @return amount of appended tokens
     */
    public int appendUntilBreak(final Iterator<Expression> it) {
        int count = 0;

        while (it.hasNext()) {
            final Expression expr = it.next();

            append(expr.getNormalized());
            ++count;

            // break on next sentence part
            if (expr.getBreakFlag()) {
                break;
            }
        }

        return count;
    }

    /**
     * Check for empty buffer content.
     *
     * @return true, if the builder is empty
     */
    public boolean isEmpty() {
        return builder.toString().length() == 0;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
