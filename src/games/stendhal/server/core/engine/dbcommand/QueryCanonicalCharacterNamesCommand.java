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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import games.stendhal.server.entity.player.Player;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Retrieves the canonical name of a number of characters.
 *
 * @author kymara / M. Fuchs
 */
public class QueryCanonicalCharacterNamesCommand extends AbstractDBCommand {
	private final Player player;
	private final Collection<String> namesToCheck;
	private Collection<String> validNames;

	/**
	 * Creates a new QueryCanonicalCharacterNamesCommand.
	 *
	 * @param player the player the check originated from
	 * @param namesToCheck the character names to check
	 */
	public QueryCanonicalCharacterNamesCommand(Player player, Collection<String> namesToCheck) {
		this.player = player;
		this.namesToCheck = namesToCheck;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		CharacterDAO dao = DAORegister.get().get(CharacterDAO.class);

		validNames = new HashSet<String>();

		for(String name : namesToCheck) {
			// check for existing accounts (may be merged with the following call into only one DB query)
			if (dao.getAccountName(transaction, name) != null) {
				// get the real character name independent from the client character case
				String canonicalName = dao.getCanonicalName(transaction, name);

				if (canonicalName != null) {
					validNames.add(canonicalName);
				}
			}
		}
	}

	/**
	 * To access the player sending the query
	 *
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}

	/**.
	 * Return the character name we queried
	 *
	 * @return character names to be checked
	 */
	public Collection<String> getQueriedNames() {
		return namesToCheck;
	}

	/**
	 * Return the character name we found to be valid.
	 *
	 * @return the unique character names we found to be valid
	 */
	public Collection<String> getValidNames() {
		return validNames;
	}

	/**
	 * Returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "QueryCanonicalCharacterNamesCommand [player=" + player + ", who="
				+ namesToCheck + ", validNames=" + validNames + "]";
	}
}
