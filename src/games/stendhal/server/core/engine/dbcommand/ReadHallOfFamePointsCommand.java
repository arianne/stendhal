/***************************************************************************
 *                    (C) Copyright 2007-2020 - Stendhal                   *
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
 * Reads the points from the hall of fame.
 *
 * @author hendrik
 */
public class ReadHallOfFamePointsCommand extends AbstractDBCommand {
	private String playername;
	private String fametype;
	private int points;

	/**
	 * creates a new ReadHallOfFamePointsCommand
	 *
	 * @param playername name of player
	 * @param fametype type of fame
	 */
	public ReadHallOfFamePointsCommand(String playername, String fametype) {
		this.playername = playername;
		this.fametype = fametype;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		StendhalHallOfFameDAO dao = DAORegister.get().get(StendhalHallOfFameDAO.class);
		points = dao.getHallOfFamePoints(transaction, playername, fametype);
	}

	/**
	 * gets the number of points.
	 *
	 * @return points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "ReadHallOfFamePointsCommand [playername=" + playername
				+ ", fametype=" + fametype + ", points=" + points + "]";
	}
}
