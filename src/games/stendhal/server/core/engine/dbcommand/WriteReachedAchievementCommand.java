/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
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

import games.stendhal.server.core.engine.db.AchievementDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * command to log a reached achievement to the database
 *
 * @author madmetzger
 */
public class WriteReachedAchievementCommand extends AbstractDBCommand {

	private final Integer id;
	private final String playerName;
	private final boolean incReachedCount;

	/**
	 * Create a new command.
	 *
	 * @param id database id of the achievement
	 * @param title achievement title
	 * @param category achievement category
	 * @param playerName name of player who has reached it
	 * @param incReachedCount shall the reached counter of this achievement be incremented
	 */
	public WriteReachedAchievementCommand(Integer id, String playerName, boolean incReachedCount) {
		this.id = id;
		this.playerName = playerName;
		this.incReachedCount = incReachedCount;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		AchievementDAO dao = DAORegister.get().get(AchievementDAO.class);
		dao.saveReachedAchievement(transaction, id, playerName, incReachedCount, getEnqueueTime());
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "WriteReachedAchievementCommand [id=" + id + ", playerName="
				+ playerName + "]";
	}
}
