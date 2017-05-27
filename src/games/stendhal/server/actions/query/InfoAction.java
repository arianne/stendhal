/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.query;

import java.text.SimpleDateFormat;
import java.util.Date;

import games.stendhal.common.Debug;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Answers with the server time and if this is the test server with the version information.
 *
 * @author hendrik
 */
public class InfoAction implements ActionListener {

	private static final String DATE_FORMAT_NOW = "dd-MMMM-yyyy HH:mm:ss";

	@Override
	public void onAction(final Player player, final RPAction action) {
		player.sendPrivateText("The server time is " + getGametime());
		if (Debug.PRE_RELEASE_VERSION != null) {
			player.sendPrivateText("Testserver version " + Debug.VERSION + " - " + Debug.PRE_RELEASE_VERSION);
		}
	}

	private String getGametime() {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(new Date(System.currentTimeMillis()));
	}

}
