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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Reads the points from the hall of fame.
 *
 * @author hendrik
 */
public class ReadCharactersFromHallOfFameCommand extends AbstractDBCommand {
	private final String fametype;

	private List<String> characterNames;
	private int max = 10;
	private boolean ascending = true;

	/**
	 * creates a new ReadHallOfFamePointsCommand
	 *
	 * @param fametype type of fame
	 * @param max maximum number of returned characters
	 * @param ascending sort ascending or descending
	 */
	public ReadCharactersFromHallOfFameCommand(String fametype, int max, boolean ascending) {
		this.fametype = fametype;
		this.max = max;
		this.ascending = ascending;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		StendhalHallOfFameDAO dao = DAORegister.get().get(StendhalHallOfFameDAO.class);
		characterNames = dao.getCharactersByFametype(transaction, fametype, max, ascending);
	}

	/**
	 * gets the best characters.
	 *
	 * @return list of character names
	 */
	public List<String> getNames() {
		return new LinkedList<String>(characterNames);
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "ReadCharactersFromHallOfFameCommand [fametype=" + fametype
				+ ", characterNames=" + characterNames + ", max=" + max
				+ ", ascending=" + ascending + "]";
	}
}
