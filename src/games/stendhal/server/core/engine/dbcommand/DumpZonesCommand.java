/***************************************************************************
 *                    (C) Copyright 2009-2015 - Stendhal                   *
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

import games.stendhal.server.core.engine.db.StendhalRPZoneDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * dumps zone information to the database so that they can be accessed on the website
 *
 * @author hendrik
 */
public class DumpZonesCommand extends AbstractDBCommand {

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		DAORegister.get().get(StendhalRPZoneDAO.class).dumpZones(transaction);
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "DumpZonesCommand []";
	}
}
