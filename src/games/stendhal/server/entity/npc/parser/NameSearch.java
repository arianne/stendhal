package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.Grammar;

import java.util.Set;

/**
 * Return structure for Sentence.findMatchingName()
 *
 * @author Martin Fuchs
 */
public class NameSearch {
	NameSearch(Set<String> names) {
		this.names = names;
		this.name = null;
		this.amount = 1;
	}

	private Set<String> names;

	private String name;
	private int	amount;

	/**
	 * Search for item to match the given Expression. 
	 *
	 * @param item
	 * @param names
	 * @return item name, or null if no match
	 */
	public boolean search(Expression item) {
		// see if the word matches an item in our list
		String itemName = item.getNormalized();

		if (names.contains(itemName)) {
			name = itemName;
			amount = item.getAmount();
			return true;
    	}

		// see if instead the plural matches
		String plural = Grammar.plural(itemName);

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
	 * Return item name.
	 *
	 * @return
	 */
	public String getName() {
        return name;
    }

	/**
	 * Return amount of items.
	 *
	 * @return
	 */
	public int getAmount() {
        return amount;
    }
}