/***************************************************************************
 *                    (C) Copyright 2007-2011 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.dbcommand;

import java.io.IOException;
import java.sql.SQLException;

import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Sets the online/offline status.
 *
 * @author hendrik
 */
public class SetOnlineStatusCommand extends AbstractDBCommand {
	private String playerName;
	private boolean online;

	/**
	 * Creates a new SetOnlineStatusCommand
	 *
	 * @param playerName name of player
	 * @param online true, to mark as online; false to mark as offline
	 */
	public SetOnlineStatusCommand(String playerName, boolean online) {
		this.playerName = playerName;
		this.online = online;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException, IOException {
		DAORegister.get().get(StendhalWebsiteDAO.class).setOnlineStatus(transaction, playerName, online);
	}

}
