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

import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Writes the points to the hall of fame.
 *
 * @author hendrik
 */
public class WriteHallOfFamePointsCommand extends AbstractDBCommand {
	private String playername;
	private String fametype;
	private int points;
	private boolean add;

	/**
	 * creates a new WriteHallOfFamePointsCommand
	 *
	 * @param playername name of player
	 * @param fametype type of fame
	 * @param points number of points
	 * @param add <code>true</code> to add points to the existing ones,
	 *          <code>false</code> to set the specified number of points.
	 */
	public WriteHallOfFamePointsCommand(String playername, String fametype, int points, boolean add) {
		this.playername = playername;
		this.fametype = fametype;
		this.points = points;
		this.add = add;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		StendhalHallOfFameDAO dao = DAORegister.get().get(StendhalHallOfFameDAO.class);
		int base = 0;
		if (add) {
			base = dao.getHallOfFamePoints(transaction, playername, fametype);
		}
		dao.setHallOfFamePoints(transaction, playername, fametype, base + points);
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "WriteHallOfFamePointsCommand [playername=" + playername
				+ ", fametype=" + fametype + ", points=" + points + ", add="
				+ add + "]";
	}
}
