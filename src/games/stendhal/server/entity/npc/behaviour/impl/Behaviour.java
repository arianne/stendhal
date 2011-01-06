/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.EventRaiser;
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

	/** The partly matching item names. */
	protected Set<String> mayBeItems;

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

	/**
	 * Search for a matching item name in the available item names.
	 *
	 * @param sentence
	 * @return true if found match
	 */
	public boolean parseRequest(final Sentence sentence) {
		final NameSearch search = sentence.findMatchingName(itemNames);

		boolean found = search.found();

		// Store found item.
		chosenItemName = search.getName();
		amount = search.getAmount();
		mayBeItems = new HashSet<String>();

		if (!found) {
			if ((sentence.getNumeralCount() == 1)
					&& (sentence.getUnknownTypeCount() == 0)
					&& (sentence.getObjectCount() == 0)) {
				final Expression number = sentence.getNumeral();

    			// If there is given only a number, return this as amount.
        		amount = number.getAmount();
    		} else {
    			// If there was no match, return the given object name instead
    			// and set amount to 1.
        		chosenItemName = sentence.getExpressionStringAfterVerb();
        		amount = 1;
    		}

			if (chosenItemName == null && itemNames.size() == 1) {
    			// The NPC only offers one type of ware, so
    			// it's clear what the player wants.
				chosenItemName = itemNames.iterator().next();
				found = true;
			} else if (chosenItemName != null) {
    			// search for items to sell with compound names, ending with the given expression
    			for(String itemName : itemNames) {
    				if (itemName.endsWith(" "+chosenItemName)) {
    					mayBeItems.add(itemName);
    				}
    			}
    		}
		}

		return found;
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

	/**
	 * Answer with an error message in case the request could not be fulfilled.
	 * @param userAction
	 * @param npcAction
	 * @param raiser
	 */
	public void sayError(final String userAction, final String npcAction, final EventRaiser raiser) {
		if (chosenItemName == null) {
			raiser.say("Please tell me what you want to " + userAction + ".");
		} else if (mayBeItems.size() > 1) {
			raiser.say("There is more than one " + chosenItemName + ". " +
					"Please specify which sort of "
					+ chosenItemName + " you want to " + userAction + ".");
		} else if (!mayBeItems.isEmpty()) {
			raiser.say("Please specify which sort of "
					+ chosenItemName + " you want to " + userAction + ".");
		} else {
			raiser.say("Sorry, I don't " + npcAction + " "
					+ Grammar.plural(chosenItemName) + ".");
		}
	}
}
