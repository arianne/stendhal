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

import java.sql.SQLException;
import java.util.Date;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Get the date the character was registered
 *
 * @author kymara
 */
public class GetCharacterCreationDateCommand extends AbstractDBCommand {
	private final String charname;
	private Date date = null;

	/**
	 * creates a new GetCharacterCreationDateCommand
	 *
	 * @param charname the name of the character to get the date for
	 */
	public GetCharacterCreationDateCommand(String charname) {
		this.charname = charname;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		CharacterDAO dao = DAORegister.get().get(CharacterDAO.class);
		date = dao.getCreationDate(transaction, charname);
	}

	/**
	 * To access the character creation date we retrieved
	 *
	 * @return date of character creation
	 */
	public Date getCreationDate() {
		if (date == null) {
			return null;
		} else {
			return (Date) date.clone();
		}
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "GetCharacterCreationDateCommand [character=" + charname
				+ "]";
	}
}
