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
 * Default implementation of the Script interface.
 *
 * @author hendrik
 */
public class ScriptImpl implements Script {

	/** all modifications must be done using this object to be undoable on unload. */
	protected ScriptingSandbox sandbox;

	@Override
	public void execute(final Player admin, final List<String> args) {
		// do nothing
	}

	@Override
	public void load(final Player admin, final List<String> args, final ScriptingSandbox sandbox) {
		this.sandbox = sandbox;
	}

	@Override
	public void unload(final Player admin, final List<String> args) {
		// do nothing
	}

}
