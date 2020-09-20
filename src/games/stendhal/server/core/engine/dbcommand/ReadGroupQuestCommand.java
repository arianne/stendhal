/***************************************************************************
 *                      (C) Copyright 2020 - Stendhal                      *
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
import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.engine.db.StendhalGroupQuestDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Reads the status of a group quest
 *
 * @author hendrik
 */
public class ReadGroupQuestCommand extends AbstractDBCommand {
	private final String questname;
	private Map<String, Integer> res;

	/**
	 * creates a new ReadGroupQuestCommand
	 *
	 * @param questname
	 */
	public ReadGroupQuestCommand(String questname) {
		this.questname = questname;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		StendhalGroupQuestDAO dao = DAORegister.get().get(StendhalGroupQuestDAO.class);
		res = dao.load(transaction, questname);
	}

	/**
	 * gets the best characters.
	 *
	 * @return list of character names
	 */
	public Map<String, Integer> getProgress() {
		if (res == null) {
			return null;
		}
		return new HashMap<String, Integer>(res);
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "ReadGroupQuestCommand [questname=" + questname + "]";
	}
}
