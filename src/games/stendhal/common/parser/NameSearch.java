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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import games.stendhal.common.grammar.Grammar;

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
     * @return true if we found a match
     */
    public boolean search(final Expression item) {
        // see if the word matches an item in our list
        boolean found = false;

        final String itemName = item.getNormalized();

        for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
        	if (e.getValue().matchesNormalized(itemName)) {
                name = e.getKey();
        		found = true;
        		break;
        	}
        }

        if (!found) {
	    	// see if instead the end matches, this is deliberately done afterwards because of bug #3285554
        	found = searchEndMatch(itemName);
        }

    	// see if instead the plural matches
        if (!found) {
	        final String pluralName = Grammar.plural(itemName);
	    	if (!pluralName.equals(itemName)) {
	            for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
	            	if (e.getValue().matchesStartNormalized(pluralName)) {
		                name = e.getKey();
		        		found = true;
		        		break;
		        	}
	            }
            }

	    	// now check for end matches with the plural
	    	if (!found && !pluralName.equals(itemName)) {
	        	found = searchEndMatch(pluralName);
            }
        }

        if (!found) {
        	// see if instead the singular matches
	        final String singularName = Grammar.singular(itemName);
	    	if (!singularName.equals(itemName)) {
	            for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
	            	if (e.getValue().matchesStartNormalized(singularName)) {
		                name = e.getKey();
		        		found = true;
		        		break;
		        	}
	        	}

		    	// now check for end matches with the singular
		    	if (!found && !singularName.equals(itemName)) {
			        found = searchEndMatch(singularName);
		    	}
	    	}

	        if (!found) {
	        	// special case to handle misspelled "double" plurals
		        final String singular2 = Grammar.singular(singularName);
		    	if (!singular2.equals(singularName)) {
		            for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
		            	if (e.getValue().matchesStartNormalized(singular2)) {
			                name = e.getKey();
			        		found = true;
			        		break;
			        	}
		        	}
		    	}

		    	// now check for end matches with the "double singular"
		    	if (!found && !singular2.equals(itemName)) {
		        	found = searchEndMatch(singular2);
		    	}
	        }
    	}

    	if (found) {
            amount = item.getAmount();
            return true;
    	} else {
			return false;
		}
    }

    /**
     * Check for end matches while searching for item names.
     * @param itemName
     * @return <code>true</true> if a match was found
     */
	private boolean searchEndMatch(final String itemName) {
		for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
			Sentence parsed = e.getValue();

			if (itemName.endsWith(parsed.getOriginalText()) ||
					itemName.endsWith(parsed.getNormalized())) {
		        name = e.getKey();
				return true;
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
