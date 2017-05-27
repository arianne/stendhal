package games.stendhal.common.grammar;

import java.util.Set;

/**
 * Result of parsing an item name and amount by ItemParser.
 */
public class ItemParserResult {

	private final boolean found;

	/** The item name of the thing requested. */
	protected String chosenItemName;

	/** The partly matching item names. */
	protected final Set<String> mayBeItems;

	/** The amount of requested items. */
	protected int amount;

	public ItemParserResult(boolean found, String chosenItemName, int amount, Set<String> mayBeItems) {
		this.found = found;
		this.chosenItemName = chosenItemName;
		this.amount = amount;
		this.mayBeItems = mayBeItems;
	}

	/**
	 * @return found flag
	 */
	public boolean wasFound() {
		return found;
	}

	/**
     * @return the chosenItemName
     */
    public String getChosenItemName() {
	    return chosenItemName;
    }

	/**
     * @param chosenItemName the chosenItemName to set
     */
    public void setChosenItemName(final String chosenItemName) {
	    this.chosenItemName = chosenItemName;
    }

	/**
     * @return the partly matching item names
     */
	public Set<String> getMayBeItems() {
		return mayBeItems;
	}

	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the amount that the player wants to transact with the NPC.
	 *
	 * @param amount
	 *            amount
	 */
	public void setAmount(final int amount) {
		if (amount < 1 || amount > 1000) {
			this.amount = 1;
		} else {
			this.amount = amount;
		}
	}

}
