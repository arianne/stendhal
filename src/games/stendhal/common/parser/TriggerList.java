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

import java.util.LinkedList;
import java.util.List;

/**
 * TriggerList can be used to create a list of Expressions from Strings and search for Expressions in this list.
 */
public class TriggerList extends LinkedList<Expression> {

	private static final long serialVersionUID = 1L;

	/**
     * Create a list of normalized trigger Words from a String list.
     *
     * @param strings String list
     */
    public TriggerList(final List<String> strings) {
        for (final String item : strings) {
            add(ConversationParser.createTriggerExpression(item));
        }
    }

    /**
     * Search for the given expression in the list.
     *
     * @param expr
     * @return matching expression in the list
     */
    public final Expression find(final Expression expr) {
        final int idx = indexOf(expr);

        if (idx != -1) {
            return get(idx);
        } else {
            return null;
        }
    }

}
