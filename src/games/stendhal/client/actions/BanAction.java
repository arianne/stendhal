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
import marauroa.common.game.RPAction;

public class BanAction implements SlashAction {

	public boolean execute(final String[] params, final String remainder) {
		final RPAction action = new RPAction();

		action.put("type", "ban");
		action.put("target", params[0]);
		action.put("hours", params[1]);
		action.put("reason", remainder);

		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	public int getMaximumParameters() {
		return 2;
	}

	public int getMinimumParameters() {
		return 2;
	}

}
