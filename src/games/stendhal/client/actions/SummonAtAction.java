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
package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParser;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import marauroa.common.game.RPAction;

/**
 * Summon an item (presumably) into an entity's slot.
 */
class SummonAtAction implements SlashAction {

	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(final String[] params, final String remainder) {
		final RPAction summon = new RPAction();

		summon.put("type", "summonat");
		summon.put("target", params[0]);
		summon.put("slot", params[1]);

		// parse item name and amount
		ItemParser parser = new ItemParser();
		Sentence sentence = ConversationParser.parse((params[2] + " " + remainder).trim());
		ItemParserResult res = parser.parse(sentence);

		final int amount = res.getAmount();
		final String itemName = res.getChosenItemName();

		final String singularName = Grammar.singular(itemName);

		summon.put("amount", amount);
		summon.put("item", singularName);

		ClientSingletonRepository.getClientFramework().send(summon);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 3;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 3;
	}
}
