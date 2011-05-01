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
 * TypeExprMatcher creates an ExpressionMatcher with type matching.
 *
 * @author Martin Fuchs
 */
class TypeExprMatcher extends ExpressionMatcher {

    public TypeExprMatcher() {
        typeMatching = true;
    }

}
