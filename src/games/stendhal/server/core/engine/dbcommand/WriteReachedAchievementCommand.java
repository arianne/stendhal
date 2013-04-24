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
package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.rp.achievement.Category;

import java.io.IOException;
import java.sql.SQLException;

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

	/**
	 * Create a new command.
	 * 
	 * @param id database id of the achievement
	 * @param title achievement title 
	 * @param category achievement category
	 * @param playerName name of player who has reached it
	 */
	public WriteReachedAchievementCommand(Integer id, String title, Category category, String playerName) {
		this.id = id;
		this.playerName = playerName;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		AchievementDAO dao = DAORegister.get().get(AchievementDAO.class);
		dao.saveReachedAchievement(transaction, id, playerName);
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
