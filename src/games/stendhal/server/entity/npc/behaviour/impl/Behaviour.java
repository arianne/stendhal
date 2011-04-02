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

import games.stendhal.common.grammar.Grammar;
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
	 * Search for a matching item name in the available item names.
	 *
	 * @param sentence
	 * @return parsing result
	 */
	public BehaviourResult parseRequest(final Sentence sentence) {
		NameSearch search = sentence.findMatchingName(itemNames);
		
		boolean found = search.found();

		// Store found item.
		String chosenItemName = search.getName();
		int amount = search.getAmount();
		Set<String> mayBeItems = new HashSet<String>();

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

		return new BehaviourResult(found, chosenItemName, amount, mayBeItems);
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
	 *
	 * @param res
	 * @param userAction
	 * @param npcAction
	 * @param npc
	 */
	public void sayError(final BehaviourResult res, final String userAction, final String npcAction, final EventRaiser npc) {
		String chosenItemName = res.getChosenItemName();
		Set<String> mayBeItems = res.getMayBeItems();

		if (chosenItemName == null) {
			npc.say("Please tell me what you want to " + userAction + ".");
		} else if (mayBeItems.size() > 1) {
			npc.say("There is more than one " + chosenItemName + ". " +
					"Please specify which sort of "
					+ chosenItemName + " you want to " + userAction + ".");
		} else if (!mayBeItems.isEmpty()) {
			npc.say("Please specify which sort of "
					+ chosenItemName + " you want to " + userAction + ".");
		} else {
			npc.say("Sorry, I don't " + npcAction + " "
					+ Grammar.plural(chosenItemName) + ".");
		}
	}

}
