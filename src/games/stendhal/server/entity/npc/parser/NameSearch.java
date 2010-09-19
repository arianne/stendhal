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

import games.stendhal.common.Grammar;

import java.util.Set;

/**
 * Returns structure for Sentence.findMatchingName().
 *
 * @author Martin Fuchs
 */
public final class NameSearch {
    NameSearch(final Set<String> names) {
        this.names = names;
        this.name = null;
        this.amount = 1;
    }

    private final Set<String> names;

    private String name;
    private int amount;

    /**
     * Searches for item to match the given Expression.
     *
     * @param item
     * @return item name, or null if no match
     */
    public boolean search(final Expression item) {
        // see if the word matches an item in our list
        final String itemName = item.getNormalized();

        if (names.contains(itemName)) {
            name = itemName;
            amount = item.getAmount();
            return true;
        }

        // see if instead the plural matches
        final String plural = Grammar.plural(itemName);

        if (names.contains(plural)) {
            name = plural;
            amount = item.getAmount();
            return true;
        }

        return false;
    }

    /**
     * Return true if matching name found.
     *
     * @return boolean find flag
     */
    public boolean found() {
        return name != null;
    }

    /**
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return amount of items
     */
    public int getAmount() {
        return amount;
    }
}
