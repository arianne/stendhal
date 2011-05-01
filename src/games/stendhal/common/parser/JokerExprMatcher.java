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
 * JokerExprMatcher creates an ExpressionMatcher for joker matching. This mode uses the
 * Expression.sentenceMatchExpression() function of sentence matching, which chooses automatically
 * between word and type matching.
 *
 * @author Martin Fuchs
 */
public final class JokerExprMatcher extends ExpressionMatcher {

    public JokerExprMatcher() {
        jokerMatching = true;
    }

}
