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
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;

/**
 * sets a client configuration parameter
 *
 * @author hendrik
 */
class ConfigAction implements SlashAction {

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
	@Override
	public boolean execute(String[] params, String remainder) {
		String oldValue = WtWindowManager.getInstance().getProperty(params[0], "{undefined}");
		if ((remainder == null) || remainder.equals("")) {
			ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine(
					params[0] + "=" + oldValue));
			return true;
		}

		WtWindowManager.getInstance().setProperty(params[0], remainder);
		ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine(
				"Changed configuration property " + params[0] + " from \"" + oldValue + "\" to \"" + remainder + "\"."));
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
