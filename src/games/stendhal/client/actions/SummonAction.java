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
import games.stendhal.common.NameBuilder;
import marauroa.common.game.RPAction;

/**
 * Summon an entity.
 */
class SummonAction implements SlashAction {

	/**
	 * Execute a chat command.
	 *
	 * We accept the following command syntaxes, coordinates are recognized from numeric parameters:
	 * 		/summon entity
	 * 		/summon x y entity
	 * 		/summon entity x y
	 * 		/summon quantity entity
	 * 		/summon entity quantity
	 *		/summon x y quantity entity
	 *		/summon entity x y quantity
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if was handled.
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		final RPAction summon = new RPAction();

		final NameBuilder nameBuilder = new NameBuilder();
		Integer x = null;
		Integer y = null;
		Integer quantity = null;

		for (int i = 0; i < params.length; ++i) {
			final String str = params[i];

			if (str != null) {
				if (str.matches("[0-9].*")) {
        			try {
        				final Integer num = Integer.valueOf(str);

        				if (x == null) {
        					x = num;
        				} else if (y == null) {
        					y = num;
        				} else if (quantity == null) {
        					quantity = num;
        				} else {
        					nameBuilder.append(str);
        				}
        			} catch (final NumberFormatException e) {
        				ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("Invalid number: " + str));
        				return true;
        			}
    			} else {
    				nameBuilder.append(str);
    			}
			}
		}

		// use x value as quantity if y was not specified
		if (quantity == null && y == null && x != null) {
			quantity = x;
			x = null;
		}

		summon.put("type", "summon");
		summon.put("creature", nameBuilder.toString());
		if (quantity != null) {
			summon.put("quantity", quantity);
		}

		if (x != null) {
			if (y != null) {
    			summon.put("x", x);
    			summon.put("y", y);
    		} else {
    			return false;
			}
		} else {
			summon.put("x", (int) User.get().getX());
			summon.put("y", (int) User.get().getY());
		}

		ClientSingletonRepository.getClientFramework().send(summon);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 9;
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
