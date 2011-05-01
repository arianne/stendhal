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
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.common.Constants;
import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.grammar.ItemParser;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.ConversationParser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Drop a player item.
 */
class DropAction implements SlashAction {

	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if command was handled.
	 */
	public boolean execute(final String[] params, final String remainder) {
		// assemble available item names in all slots
		User user = User.get();
		Set<String> itemNames = new HashSet<String>();
		for(final String slotName : Constants.CARRYING_SLOTS) {
			for(Iterator<RPObject> it=user.getSlot(slotName).iterator(); it.hasNext(); )
				itemNames.add(it.next().get("name"));
		}

		// parse item name and amount
		ItemParser parser = new ItemParser(itemNames);
		ItemParserResult res = parser.parse(ConversationParser.parse((params[0] + " " + remainder).trim()));
		String errorMsg;

		if (res.wasFound()) {
			String itemName = res.getChosenItemName();

			for(final String slotName : Constants.CARRYING_SLOTS) {
				int itemID = User.get().findItem(slotName, itemName);

				if (itemID != -1) {
					final RPAction drop = new RPAction();

					drop.put(EquipActionConsts.TYPE, "drop");
					drop.put(EquipActionConsts.BASE_OBJECT, User.get().getObjectID());
					drop.put(EquipActionConsts.BASE_SLOT, slotName);
					drop.put(EquipActionConsts.GROUND_X, (int) User.get().getX());
					drop.put(EquipActionConsts.GROUND_Y, (int) User.get().getY());
					drop.put(EquipActionConsts.QUANTITY, res.getAmount());
					drop.put(EquipActionConsts.BASE_ITEM, itemID);

					ClientSingletonRepository.getClientFramework().send(drop);
					return true;
				}
			}

			// should never be reached, as matching item names is already handled by ItemParser
			errorMsg = "You don't have any " + itemName;
		} else {
			errorMsg = parser.getErrormessage(res, "drop", null);

			if (errorMsg == null) {
				errorMsg = "You don't have any " + res.getChosenItemName();
			}
		}

		ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine(errorMsg));
		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 1;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 1;
	}
}
