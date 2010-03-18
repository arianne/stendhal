/***************************************************************************
 *                    (C) Copyright 2008-2010 - Stendhal                   *
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

import java.sql.SQLException;

import games.stendhal.server.core.engine.db.CidDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Logs a cid
 */
public class LogCidCommand extends AbstractDBCommand {

	private String cid;
	private String playerName;
	private String address;

	/**
	 * creates a new LogCidCommand
	 *
	 * @param playerName name of player
	 * @param address ip-address
	 * @param cid cid
	 */
	public LogCidCommand(String playerName, String address, String cid) {
		this.playerName = playerName;
		this.address = address;
		this.cid = cid;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		DAORegister.get().get(CidDAO.class).log(transaction, playerName, address, cid);
	}

}
