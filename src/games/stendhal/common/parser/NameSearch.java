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
import games.stendhal.tools.statistics.AdHocCoverage;

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
		AdHocCoverage ahc = new AdHocCoverage("search", 19);
		// see if the word matches an item in our list
		boolean found = false;

		final String itemName = item.getNormalized();

		for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
			if (e.getValue().matchesNormalized(itemName)) { // ID: 0
				// COVERAGE
				ahc.branchReached(0);
				name = e.getKey();
				found = true;
				break;
			}
		}

		if (!found) { // ID: 1
			// COVERAGE
			ahc.branchReached(1);
			// see if instead the end matches, this is deliberately done afterwards because of bug #3285554
			found = searchEndMatch(itemName);
		}

		// see if instead the plural matches
		if (!found) { // ID: 2
			// COVERAGE
			ahc.branchReached(2);
			final String pluralName = Grammar.plural(itemName);
			if (!pluralName.equals(itemName)) { // ID: 3
				// COVERAGE
				ahc.branchReached(3);
				for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
					if (e.getValue().matchesStartNormalized(pluralName)) { // ID: 4
						// COVERAGE
						ahc.branchReached(4);
						name = e.getKey();
						found = true;
						break;
					}
				}
			}

			// now check for end matches with the plural
			if (!found && !pluralName.equals(itemName)) { // ID: 5,6
				// COVERAGE
				ahc.branchReached(5);
				found = searchEndMatch(pluralName);
			}
			// EXTRA_COVERAGE
			else {
				ahc.branchReached(6);
			}
		}

		if (!found) { // ID: 7
			// COVERAGE
			ahc.branchReached(7);
			// see if instead the singular matches
			final String singularName = Grammar.singular(itemName);
			if (!singularName.equals(itemName)) { // ID: 8
				// COVERAGE
				ahc.branchReached(8);
				for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
					if (e.getValue().matchesStartNormalized(singularName)) { // ID: 9
						// COVERAGE
						ahc.branchReached(9);
						name = e.getKey();
						found = true;
						break;
					}
				}

				// now check for end matches with the singular
				if (!found && !singularName.equals(itemName)) { // ID: 10, 11
					// COVERAGE
					ahc.branchReached(10);
					found = searchEndMatch(singularName);
				}
				// EXTRA_COVERAGE
				else {
					ahc.branchReached(11);
				}
			}

			if (!found) { // ID: 12
				// COVERAGE
				ahc.branchReached(12);
				// special case to handle misspelled "double" plurals
				final String singular2 = Grammar.singular(singularName);
				if (!singular2.equals(singularName)) { // ID: 13
					// COVERAGE
					ahc.branchReached(13);
					for(Map.Entry<String, Sentence> e : parsedNames.entrySet()) {
						if (e.getValue().matchesStartNormalized(singular2)) { // ID: 14
							// COVERAGE
							ahc.branchReached(14);
							name = e.getKey();
							found = true;
							break;
						}
					}
				}

				// now check for end matches with the "double singular"
				if (!found && !singular2.equals(itemName)) { // ID: 15, 16
					// COVERAGE
					ahc.branchReached(15);

					found = searchEndMatch(singular2);
				}
				// EXTRA_COVERAGE
				else {
					ahc.branchReached(16);
				}
			}
		}

		if (found) { // ID: 17
			// COVERAGE
			ahc.branchReached(17);
			amount = item.getAmount();
			return true;
		} else { // ID: 18
			// COVERAGE
			ahc.branchReached(18);
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
