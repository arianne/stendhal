package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.AlwaysTrueCondition;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.NameSearch;
import games.stendhal.server.entity.npc.parser.Sentence;

import java.util.HashSet;
import java.util.Set;

/**
 * Behaviour is the base class for all quest behaviours parsing a sentence to
 * get an amount and an item name.
 * 
 * @author Martin Fuchs
 */
public class Behaviour {

	/** ItemNames contains all valid item names. */
	protected Set<String> itemNames;

	/** The item name of the thing requested. */
	protected String chosenItemName;

	/** The amount of requested items. */
	protected int amount;

	public Behaviour() {
	    this.itemNames = new HashSet<String>();
    }

	public Behaviour(final Set<String> itemNames) {
	    this.itemNames = itemNames;
    }

	public Behaviour(final String itemName) {
	    itemNames = new HashSet<String>();
	    itemNames.add(itemName);
    }

	/**
	 * @return the recognized item names
	 */
	public Set<String> getItemNames() {
		return itemNames;
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

	/**
	 * Search for a matching item name in the available item names.
	 *
	 * @param sentence
	 * @return true if found match
	 */
	public boolean parseRequest(final Sentence sentence) {
		final NameSearch search = sentence.findMatchingName(itemNames);

		if (search.found()) {
			// Store found item.
    		chosenItemName = search.getName();
    		amount = search.getAmount();

    		return true;
		} else {
			if ((sentence.getNumeralCount() == 1)
					&& (sentence.getUnknownTypeCount() == 0)
					&& (sentence.getObjectCount() == 0)) {
				final Expression number = sentence.getNumeral();

    			// If there is given only a number, return this as amount.
        		chosenItemName = null;
        		amount = number.getAmount();
    		} else {
    			// If there was no match, return the given object name instead
    			// and set amount to 1.
        		chosenItemName = sentence.getExpressionStringAfterVerb();
        		amount = 1;
    		}

			return false;
		}
    }

	/**
	 * a chat condition that checks whether transactions are possible at all
	 * 
	 * @return ChatCondition
	 */
	public ChatCondition getTransactionCondition() {
		return new AlwaysTrueCondition();
	}

	/**
	 * a chat action that is executed in case transaction are not possible
	 *
	 * @return ChatAction
	 */
	public ChatAction getRejectedTransactionAction() {
		return null;
	}

}
