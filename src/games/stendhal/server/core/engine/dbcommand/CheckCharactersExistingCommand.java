/***************************************************************************
 *                    (C) Copyright 2010-2011 - Stendhal                   *
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

import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Checks if a number of characters exists
 *
 * @author M. Fuchs
 */
public class CheckCharactersExistingCommand extends AbstractDBCommand {
	private Player player;
	private Set<String> namesToCheck;
	private Set<String> validNames;

	/**
	 * Creates a new CheckCharactersExistingCommand.
	 *
	 * @param player the player the check originated from
	 * @param namesToCheck the character names to check
	 */
	public CheckCharactersExistingCommand(Player player, Set<String> namesToCheck) {
		this.player = player;
		this.namesToCheck = namesToCheck;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		CharacterDAO dao = DAORegister.get().get(CharacterDAO.class);

		validNames = new HashSet<String>();

		for(String name : namesToCheck) {
			// check for existing accounts (may be merged with the following call into only one DB query)
			if (dao.getAccountName(name) != null) {
				// get the real character name independent from the client character case
				String canonicalName = dao.getCanonicalName(name);

				if (canonicalName != null) {
					validNames.add(canonicalName);
				}
			}
		}
	}

	/**.
	 * Return the character name we queried
	 *
	 * @return set of the character names we found to be valid
	 */
	public Set<String> getQueriedNames() {
		return namesToCheck;
	}

	/**
	 * Return the character name we found to be valid.
	 *
	 * @return set of the character names we found to be valid
	 */
	public Set<String> getValidNames() {
		return validNames;
	}

	/**
	 * Returns a string suitable for debug output of this DBCommand.
	 * 
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "CheckCharactersExistingCommand [player=" + player + ", who="
				+ namesToCheck + ", validNames=" + validNames + "]";
	}
}
