/***************************************************************************
 *                    (C) Copyright 2007-2010 - Stendhal                   *
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

import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Killer;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * logs kill events
 *
 * @author hendrik
 */
public class LogKillEventCommand extends AbstractDBCommand {

	private Entity frozenKilled;
	private Killer frozenKiller;

	/**
	 * creates a new LogKillEventCommand
	 *
	 * @param killed killed entity
	 * @param killer killer entity
	 */
	public LogKillEventCommand(Entity killed, Killer killer) {
		this.frozenKilled = (Entity) killed.clone();
		this.frozenKiller = (Killer) killer.clone();
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		StendhalKillLogDAO killLog = DAORegister.get().get(StendhalKillLogDAO.class);
		killLog.logKill(transaction, frozenKilled, frozenKiller, getEnqueueTime());
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "LogKillEventCommand [frozenKilled=" + frozenKilled
				+ ", frozenKiller=" + frozenKiller + "]";
	}
}
