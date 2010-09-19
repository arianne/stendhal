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
package games.stendhal.bot.textclient;

import marauroa.client.ClientFramework;
import marauroa.common.game.RPAction;

/**
 * actions to execute on login
 *
 * @author hendrik
 */
public class LoginScript {
	private ClientFramework client;

	/**
	 * creates a new LoginScript
	 *
	 * @param client ClientFramework
	 */
	public LoginScript(ClientFramework client) {
		this.client = client;
	}

	/**
	 * performs some steps after an admin login
	 */
	public void adminLogin() {
		ghostmode();
		teleportToAdminLocation();
	}

	/**
	 * activates ghostmode
	 */
	private void ghostmode() {
		RPAction action = new RPAction();
		action.put("type", "ghostmode");
		action.put("mode", "true");
		client.send(action);
	}


	/**
	 * teleports to the admin house
	 */
	private void teleportToAdminLocation() {
		RPAction action = new RPAction();
		action.put("type", "teleportto");
		action.put("target", "Skye");
		client.send(action);
	}
}
