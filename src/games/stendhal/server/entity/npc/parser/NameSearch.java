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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Returns structure for Sentence.findMatchingName().
 *
 * @author Martin Fuchs
 */
public final class NameSearch {
    NameSearch(final Set<String> names) {
        for(String name : names) {
        	parsedNames.put(name, ConversationParser.parse(name));
        }

        this.name = null;
        this.amount = 1;
    }

    private final Map<String, Sentence> parsedNames = new HashMap<String, Sentence>(); // map of parsed names to search for

    private String name;	// name we found as matching
    private int amount;		// item count from the matching expression

    /**
     * Searches for item to match the given Expression.
     *
     * @param item
     * @return item name, or null if no match
     */
    public boolean search(final Expression item) {
        // see if the word matches an item in our list
        final String itemName = item.getNormalized();
        final String singularName = Grammar.singular(itemName);
        final String pluralName = Grammar.plural(itemName);

        for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
        	final Sentence parsed = e.getValue();

        	if (parsed.matchesNormalized(itemName)) {
                name = e.getKey();
                amount = item.getAmount();
                return true;
        	}

        	if (!singularName.equals(itemName)) {
	        	if (parsed.matchesNormalized(singularName)) {
	                name = e.getKey();
	                amount = item.getAmount();
	                return true;
	        	}
        	}

        	// see if instead the plural matches
        	if (!pluralName.equals(itemName)) {
	        	if (parsed.matchesNormalized(pluralName)) {
	                name = e.getKey();
	                amount = item.getAmount();
	                return true;
	        	}
            }
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
