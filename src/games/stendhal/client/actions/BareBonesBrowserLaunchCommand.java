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
import games.stendhal.client.gui.BareBonesBrowserLaunch;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * generalized super class to provide a uniform way to open urls in the browser
 *
 * @author madmetzger
 */
class BareBonesBrowserLaunchCommand implements SlashAction{

	private final String urlToOpen;

	/**
	 * creates a new BareBonesBrowserLaunchCommand
	 *
	 * @param url url to open
	 */
	BareBonesBrowserLaunchCommand(String url) {
		urlToOpen = url;
	}

	/**
	 * Opens an URL with the browser
	 *
	 * @param params ignored
	 * @param remainder ignored
	 * @return <code>true</code>
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
				"Trying to open #" + urlToOpen + " in your browser.",
		NotificationType.CLIENT));

		BareBonesBrowserLaunch.openURL(urlToOpen);

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
