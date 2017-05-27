/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;

/**
 * save all players
 *
 * @author hendrik
 */
public class SaveAllPlayers extends ScriptImpl {
	private static Logger logger = Logger.getLogger(SaveAllPlayers.class);

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		for (PlayerEntry entry : PlayerEntryContainer.getContainer()) {
			try {
				entry.storeRPObject(entry.object);
			} catch (SQLException e) {
				logger.error(e, e);
			} catch (IOException e) {
				logger.error(e, e);
			}
		}
	}


}
