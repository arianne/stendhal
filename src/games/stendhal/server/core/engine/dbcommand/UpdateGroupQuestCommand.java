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

import games.stendhal.server.core.engine.db.StendhalGroupQuestDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Reads the status of a group quest
 *
 * @author hendrik
 */
public class UpdateGroupQuestCommand extends AbstractDBCommand {
	private final String questname;
	private final String charname;
	private final String itemname;
	private final Integer quantity;

	/**
	 * creates a new ReadGroupQuestCommand
	 *
	 * @param questname
	 */
	public UpdateGroupQuestCommand(String questname, String itemname, String charname, Integer quantity) {
		this.questname = questname;
		this.charname = charname;
		this.itemname = itemname;
		this.quantity = quantity;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		StendhalGroupQuestDAO dao = DAORegister.get().get(StendhalGroupQuestDAO.class);
		dao.update(transaction, questname, charname, itemname, quantity, getEnqueueTime());
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "UpdateGroupQuestCommand [questname=" + questname
			+ ", charname=" + charname + ", itemname=" + itemname
			+ ", quantity=" + quantity + "]";
	}
}
