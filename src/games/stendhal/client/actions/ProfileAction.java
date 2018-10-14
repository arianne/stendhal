/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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
import games.stendhal.client.gui.BareBonesBrowserLaunch;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * opens a profile
 *
 * @author hendrik
 */
class ProfileAction implements SlashAction{

	/**
	 * Opens an URL with the browser
	 *
	 * @param params ignored
	 * @param remainder ignored
	 * @return <code>true</code>
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		String url = "https://stendhalgame.org/character/";
		String name = null;
		if ((params.length > 0) && (params[0] != null)) {
			name = params[0];
		} else {
			name = User.getCharacterName();
			if (name == null) {
				return true;
			}
		}
		url = url + name + ".html";
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
				"Trying to open #" + url + " in your browser.",
		NotificationType.CLIENT));

		BareBonesBrowserLaunch.openURL(url);

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
		return 0;
	}
}
