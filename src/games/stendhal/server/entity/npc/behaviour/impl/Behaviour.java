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

import java.util.Set;

import games.stendhal.common.grammar.ItemParser;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.AlwaysTrueCondition;

/**
 * Behaviour is the base class for all quest behaviours parsing a sentence to
 * get an amount and an item name.
 *
 * @author Martin Fuchs
 */
public class Behaviour extends ItemParser {

	public Behaviour() {
    }

	public Behaviour(final Set<String> itemNames) {
	    super(itemNames);
    }

	public Behaviour(final String itemName) {
		super(itemName);
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
