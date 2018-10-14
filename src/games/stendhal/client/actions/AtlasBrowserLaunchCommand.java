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
import games.stendhal.client.gui.BareBonesBrowserLaunch;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * opens the atlas at the current position in the browser.
 *
 * @author hendrik
 */
class AtlasBrowserLaunchCommand implements SlashAction{

	/**
	 * Opens the atlas URL at the current position
	 *
	 * @param params ignored
	 * @param remainder ignored
	 * @return <code>true</code>
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		StringBuilder url = new StringBuilder();
		User user = User.get();
		url.append("https://stendhalgame.org/world/atlas.html");
		if (user != null) {
			url.append("?me=");
			url.append(user.getZoneName());
			url.append(".");
			url.append(Math.round(user.getX()));
			url.append(".");
			url.append(Math.round(user.getY()));
		}

		String urlString = url.toString();
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
				"Trying to open #" + urlString + " in your browser.",
		NotificationType.CLIENT));
		BareBonesBrowserLaunch.openURL(urlString);
		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
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
