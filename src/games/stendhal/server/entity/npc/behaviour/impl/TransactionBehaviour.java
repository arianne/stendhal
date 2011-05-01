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
package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import java.util.Set;

public abstract class TransactionBehaviour extends Behaviour {

	public TransactionBehaviour(final String itemName) {
	    super(itemName);
    }

	public TransactionBehaviour(final Set<String> itemNames) {
	    super(itemNames);
    }

	/**
	 * Transacts the deal that is described in BehaviourResult.
	 * 
	 * @param res
	 * 			  result of the Behaviour parsing
	 * @param seller
	 *            The NPC who sells/buys
	 * @param player
	 *            The player who buys/sells
	 * @return true if the transaction was successful.
	 */
	public abstract boolean transactAgreedDeal(ItemParserResult res, EventRaiser seller, Player player);

}
