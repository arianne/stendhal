/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.engine.db.CidDAO;
import games.stendhal.server.core.engine.db.PostmanDAO;
import games.stendhal.server.core.engine.db.StendhalBuddyDAO;
import games.stendhal.server.core.engine.db.StendhalCharacterDAO;
import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.core.engine.db.StendhalNPCDAO;
import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;

import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.JDBCSQLHelper;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

import org.apache.log4j.Logger;

/**
 * initializes the database by setting up or updating the database structure and defining
 * the database access objects (DAOs).
 *
 * @author hendrik
 */
public class StendhalPlayerDatabase {

	private static final Logger logger = Logger.getLogger(StendhalPlayerDatabase.class);

	/**
	 * initializes the database by setting up or updating the database structure and defining
	 * the database access objects (DAOs).
	 */
	public void initialize() {
		final DBTransaction transaction = TransactionPool.get().beginWork();
		try {

			createTablesUnlessTheyAlreadyExist(transaction);
			updateExistingTables(transaction);

			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			logger.error(e, e);
			TransactionPool.get().rollback(transaction);
		}

		registerStendhalDAOs();
	}


	/**
	 * creates the Stendhal database tables unless they already exist
	 *
	 * @param transaction   DBTransaction
	 * @throws SQLException in case of an unexpected database error
	 */
	private void createTablesUnlessTheyAlreadyExist(final DBTransaction transaction) {
		new JDBCSQLHelper(transaction).runDBScript("games/stendhal/server/stendhal_init.sql");
	}


	/**
	 * updates existing tables as required
	 *
	 * @param transaction   DBTransaction
	 * @throws SQLException in case of an unexpected database error
	 */
	private void updateExistingTables(final DBTransaction transaction) throws SQLException {

		// 0.81: add new day column in table kills
		if (!transaction.doesColumnExist("kills", "day")) {
			transaction.execute("ALTER TABLE kills ADD COLUMN (day DATE);", null);
		}

		// 0.84: add an alternative image for the website
		if (!transaction.doesColumnExist("npcs", "image")) {
			transaction.execute("ALTER TABLE npcs ADD COLUMN (image VARCHAR(255));", null);
		}

		// 0.85: add lastseen table to character stats
		if (!transaction.doesColumnExist("character_stats", "lastseen")) {
			transaction.execute("ALTER TABLE character_stats ADD COLUMN (lastseen TIMESTAMP);", null);
		}

		// 0.86: when marking postman messages delivered, don't delete them, just update the delivered flag
		if (!transaction.doesColumnExist("postman", "messagetype")) {
			transaction.execute("ALTER TABLE postman ADD COLUMN (messagetype CHAR(1));", null);
		}

		// 0.86: add base_score to achievement table
		if (!transaction.doesColumnExist("achievement", "base_score")) {
			transaction.execute("ALTER TABLE achievement ADD COLUMN (base_score INTEGER);", null);
		}

		// 0.87: fix length of description column (using a temp table and drop/create for better cross database support)
		if (transaction.doesColumnExist("achievement", "description")) {
			if (transaction.getColumnLength("achievement", "description") < 254) {
				transaction.execute("CREATE TABLE tmp_achievement_description (id INTEGER, description VARCHAR(254));", null);
				transaction.execute("INSERT INTO tmp_achievement_description (id, description) SELECT id, description FROM achievement;", null);
				transaction.execute("ALTER TABLE achievement DROP description", null);
				transaction.execute("ALTER TABLE achievement ADD description VARCHAR(254);", null);
				transaction.execute("UPDATE achievement SET description=(SELECT tmp_achievement_description.description FROM tmp_achievement_description WHERE achievement.id=tmp_achievement_description.id);", null);
				transaction.execute("DROP TABLE tmp_achievement_description;", null);
			}
		}

		// 0.87: added new column finger to character_stats
		if (!transaction.doesColumnExist("character_stats", "finger")) {
			transaction.execute("ALTER TABLE character_stats ADD COLUMN (finger VARCHAR(32));", null);
		}

		// 0.90: added column deleted to table postman to support one sided deletions
		if (!transaction.doesColumnExist("postman", "deleted")) {
			transaction.execute("ALTER TABLE postman ADD COLUMN (deleted CHAR(1) DEFAULT 'N');", null);
		}

		// 0.92: deleted unwanted achievements
		transaction.execute("DELETE FROM achievement WHERE identifier in ('age.day.one', "
			+ "'age.week.one', 'age.month.one', 'age.month.two', 'age.month.three', "
			+ "'age.month.four', 'age.month.five', 'age.month.six', 'age.month.seven', "
			+ "'age.month.eight', 'age.month.nine', 'age.month.ten', 'age.month.eleven', "
			+ "'age.year.one', 'quest.special.dm.025', 'quest.special.susi', 'item.produce.flour')", null);
		transaction.execute("UPDATE achievement SET identifier='xp.level.010' WHERE identifier='xp.level.10'", null);
		transaction.execute("UPDATE achievement SET identifier='xp.level.050' WHERE identifier='xp.level.50'", null);
	}


	/**
	 * registers the database access objects for Stendhal.
	 */
	private void registerStendhalDAOs() {

		// define own version in replacement of marauroa's CharacterDAO
		DAORegister.get().register(CharacterDAO.class, new StendhalCharacterDAO());

		// define additional DAOs
		DAORegister.get().register(CidDAO.class, new CidDAO());
		DAORegister.get().register(PostmanDAO.class, new PostmanDAO());
		DAORegister.get().register(StendhalBuddyDAO.class, new StendhalBuddyDAO());
		DAORegister.get().register(StendhalHallOfFameDAO.class, new StendhalHallOfFameDAO());
		DAORegister.get().register(StendhalKillLogDAO.class, new StendhalKillLogDAO ());
		DAORegister.get().register(StendhalNPCDAO.class, new StendhalNPCDAO());
		DAORegister.get().register(StendhalWebsiteDAO.class, new StendhalWebsiteDAO());
		DAORegister.get().register(AchievementDAO.class, new AchievementDAO());
	}
}
