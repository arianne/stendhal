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
 * Create a ConversationContext with de-activated Expression merging and word ignoring.
 *
 * @author Martin Fuchs
 */
public final class ConvCtxForMatchingSource extends ConversationContext {

    public ConvCtxForMatchingSource() {
        mergeExpressions = false;
        ignoreIgnorable = false;
    }

}
