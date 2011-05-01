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
 * ExactExprMatcher creates an ExpressionMatcher with exact matching.
 *
 * @author Martin Fuchs
 */
public class ExactExprMatcher extends ExpressionMatcher {

    public ExactExprMatcher() {
        exactMatching = true;
    }

}
