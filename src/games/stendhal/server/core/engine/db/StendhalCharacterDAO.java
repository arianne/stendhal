/***************************************************************************
 *                    (C) Copyright 2003-2020 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.db;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Stendhal specific extensions to the normal CharacterDAO which will update
 * the redundant tables for the web application.
 */
public class StendhalCharacterDAO extends CharacterDAO {
	private static Logger logger = Logger.getLogger(StendhalCharacterDAO.class);

	@Override
	public void addCharacter(final DBTransaction transaction, final String username,
			final String character, final RPObject player, Timestamp timestamp) throws SQLException, IOException {

		super.addCharacter(transaction, username, character, player, timestamp);

		// Here goes the Stendhal specific code.
		try {
			if (player instanceof Player) {
				final Player instance = (Player) player;
				DAORegister.get().get(StendhalHallOfFameDAO.class).setHallOfFamePoints(transaction, instance.getName(), "T", instance.getTradescore());
				DAORegister.get().get(StendhalWebsiteDAO.class).insertIntoCharStats(transaction, instance, timestamp);
				DAORegister.get().get(StendhalBuddyDAO.class).saveRelations(transaction, character, instance);
			} else {
				logger.error("player no instance of Player but: " + player, new Throwable());
			}
		} catch (final SQLException sqle) {
			logger.warn("error storing character", sqle);
			throw sqle;
		}
	}

	@Override
	public void storeCharacter(final DBTransaction transaction, final String username,
			final String character, final RPObject player, Timestamp timestamp) throws SQLException, IOException {

		super.storeCharacter(transaction, username, character, player, timestamp);

		// Here goes the Stendhal specific code.
		if (player instanceof Player) {
			try {
				final Player instance = (Player) player;
				final int count = DAORegister.get().get(StendhalWebsiteDAO.class).updateCharStats(transaction, instance, timestamp);
				if (count == 0) {
					DAORegister.get().get(StendhalWebsiteDAO.class).insertIntoCharStats(transaction, instance, timestamp);
				}
				DAORegister.get().get(StendhalBuddyDAO.class).saveRelations(transaction, character, instance);
			} catch (final SQLException sqle) {
				logger.warn("error storing character", sqle);
				throw sqle;
			}
		} else {
			logger.error("player no instance of Player but: " + player, new Throwable());
		}
	}

}
