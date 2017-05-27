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
package games.stendhal.server.core.scripting;

import java.util.List;

import games.stendhal.server.entity.player.Player;

/**
 * A script, which can be reloaded at runtime.
 *
 * @author hendrik
 */
public interface Script {

	/**
	 * Initial load of this script.
	 *
	 * @param admin
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 * @param sandbox
	 *            all modifications to the game must be done using this object
	 *            in order for the script to be unloadable
	 */
	void load(Player admin, List<String> args, ScriptingSandbox sandbox);

	/**
	 * Unloads this script.
	 *
	 * @param admin
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 */
	void unload(Player admin, List<String> args);

	/**
	 * Executes this script.
	 *
	 * @param admin
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 */
	void execute(Player admin, List<String> args);
}
