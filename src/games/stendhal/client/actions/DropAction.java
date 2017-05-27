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
import games.stendhal.common.grammar.Grammar;
import marauroa.common.game.RPAction;

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
	@Override
	public boolean execute(final String[] params, final String remainder) {
		int quantity;
		String itemName;

		// Is there a numeric expression as first parameter?
		if (params[0].matches("[0-9].*")) {
			try {
				quantity = Integer.parseInt(params[0]);
			} catch (final NumberFormatException ex) {
				ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("Invalid quantity: " + params[0]));
				return true;
			}

			itemName = remainder;
		} else {
			quantity = 1;
			itemName = (params[0] + " " + remainder).trim();
		}

		final String singularItemName = Grammar.singular(itemName);

		for (final String slotName : Constants.CARRYING_SLOTS) {
			int itemID = User.get().findItem(slotName, itemName);

			// search again using the singular, in case it was a plural item name
			if ((itemID == -1) && !itemName.equals(singularItemName)) {
				itemID = User.get().findItem(slotName, singularItemName);
			}

			if (itemID != -1) {
				final RPAction drop = new RPAction();

				drop.put(EquipActionConsts.TYPE, "drop");
				drop.put(EquipActionConsts.BASE_OBJECT, User.get().getObjectID());
				drop.put(EquipActionConsts.BASE_SLOT, slotName);
				drop.put(EquipActionConsts.GROUND_X, (int) User.get().getX());
				drop.put(EquipActionConsts.GROUND_Y, (int) User.get().getY());
				drop.put(EquipActionConsts.QUANTITY, quantity);
				drop.put(EquipActionConsts.BASE_ITEM, itemID);

				ClientSingletonRepository.getClientFramework().send(drop);
				return true;
			}
		}
		ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("You don't have any " + singularItemName));
		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 1;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 1;
	}
}
