/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.engine.db.PendingAchievementDAO;
import games.stendhal.server.core.engine.db.PostmanDAO;
import games.stendhal.server.core.engine.db.StendhalBuddyDAO;
import games.stendhal.server.core.engine.db.StendhalCharacterDAO;
import games.stendhal.server.core.engine.db.StendhalGroupQuestDAO;
import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import games.stendhal.server.core.engine.db.StendhalItemDAO;
import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.core.engine.db.StendhalNPCDAO;
import games.stendhal.server.core.engine.db.StendhalRPZoneDAO;
import games.stendhal.server.core.engine.db.StendhalSearchIndexDAO;
import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;
import games.stendhal.server.entity.Outfit;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.JDBCSQLHelper;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

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
			+ "'age.year.one', 'quest.special.dm.025', 'quest.special.susi', 'item.produce.flour',"
			+ "'quest.special.santa', 'quest.special.bunny')", null);
		transaction.execute("UPDATE achievement SET identifier='xp.level.010' WHERE identifier='xp.level.10'", null);
		transaction.execute("UPDATE achievement SET identifier='xp.level.050' WHERE identifier='xp.level.50'", null);

		// 0.93: inactive achievements
		if (!transaction.doesColumnExist("achievement", "active")) {
			transaction.execute("ALTER TABLE achievement ADD COLUMN (active INTEGER);", null);
			transaction.execute("UPDATE achievement SET active = 1 WHERE active IS NULL;", null);
		}

		// 0.97: outfit_colors
		if (!transaction.doesColumnExist("character_stats", "outfit_colors")) {
			transaction.execute("ALTER TABLE character_stats ADD COLUMN (outfit_colors VARCHAR(100));", null);
			transaction.execute("UPDATE character_stats SET outfit_colors = '' WHERE outfit_colors IS NULL;", null);
		}

		// 0.97: convert itemid-table to item-table
		if (transaction.doesTableExist("itemid")) {
			int id = transaction.querySingleCellInt("SELECT last_id FROM itemid", null);
			logger.warn("Migrating from itemid-table to item-table. last_id: " + id);
			String sql = "INSERT INTO item (id, name, timedate) "
					+ " SELECT itemid, param1, timedate FROM itemlog WHERE event='register' AND timedate>='2011-10-01' ORDER BY timedate";
			transaction.execute(sql, null);

			// If there have been no recent log entries, (e. g. the server was offline for some time)
			// fake one to get the correct id
			int count = transaction.querySingleCellInt("SELECT count(id) FROM item", null);
			if (count == 0) {
				transaction.execute("INSERT INTO item (id) VALUES (" + id + ")", null);
			} else {
				// Make sure that the id from the last register row is at least as high as itemid.last_id.
				int itemid = transaction.querySingleCellInt("SELECT id FROM item ORDER BY id DESC LIMIT 1", null);
				if (itemid < id) {
					// Something went wrong, make sure not to reuse ids.
					transaction.execute("INSERT INTO item (id) VALUES (" + id + ")", null);
				}
			}
			transaction.execute("DROP TABLE itemid", null);
		}

		// 1.07: add zone column to character_stats
		if (!transaction.doesColumnExist("character_stats", "zone")) {
			transaction.execute("ALTER TABLE character_stats ADD COLUMN (zone VARCHAR(50));", null);
		}

		// 1.13: added relationtype to buddy
		if (!transaction.doesColumnExist("buddy", "relationtype")) {
			transaction.execute("ALTER TABLE buddy ADD COLUMN (relationtype VARCHAR(7));", null);
			transaction.execute("UPDATE buddy SET relationtype = 'buddy' WHERE relationtype IS NULL", null);
		}

		// 1.32: add outfit layers
		if (!transaction.doesColumnExist("npcs", "outfit_layers")) {
			transaction.execute("ALTER TABLE npcs ADD COLUMN (outfit_layers VARCHAR(255));", null);
		}
		if (!transaction.doesColumnExist("character_stats", "outfit_layers")) {
			transaction.execute("ALTER TABLE character_stats ADD COLUMN (outfit_layers VARCHAR(255));", null);
			updateCharacterStatsOutfitToOutfitLayer(transaction);
		}

		// 1.34: renamed kill_blordroughs achievements
		transaction.execute("UPDATE achievement SET identifier='quest.special.kill_blordroughs.0005' WHERE identifier='quest.special.kill_blordroughs.5'", null);
		transaction.execute("UPDATE achievement SET identifier='quest.special.kill_blordroughs.0025' WHERE identifier='quest.special.kill_blordroughs.25'", null);

		// 1.35: for performance reasons, keep track of number if awarded achievement
		if (!transaction.doesColumnExist("achievement", "reached")) {
			transaction.execute("ALTER TABLE achievement ADD COLUMN (reached INTEGER);", null);
			transaction.execute("UPDATE achievement SET reached = 0 WHERE reached IS NULL;", null);
		}

		// 1.36: increase size of halloffame.fametype
		if (transaction.getColumnLength("halloffame", "fametype") == 1) {
			transaction.execute("ALTER TABLE halloffame                  MODIFY COLUMN fametype char(10) NOT NULL", null);
			transaction.execute("ALTER TABLE halloffame_archive_alltimes MODIFY COLUMN fametype char(10) NOT NULL", null);
			transaction.execute("ALTER TABLE halloffame_archive_recent   MODIFY COLUMN fametype char(10) NOT NULL", null);
		}

		// 1.38: entity is cloned from another NPC
		if (!transaction.doesColumnExist("npcs", "cloned")) {
			transaction.execute("ALTER TABLE npcs ADD COLUMN (cloned VARCHAR(64));", null);
		}
	}


	private void updateCharacterStatsOutfitToOutfitLayer(DBTransaction transaction) throws SQLException {
		PreparedStatement prepareStatement = transaction.prepareStatement("UPDATE character_stats SET outfit_layers=? WHERE name=?", null);
		while (true) {
			ResultSet set = transaction.query("SELECT name, outfit, outfit_colors FROM character_stats WHERE outfit_layers IS NULL LIMIT 10000", null);
			if (!set.next()) {
				break;
			}
			do {
				String code = set.getString("outfit");
				String outfitColors = set.getString("outfit_colors");
				Map<String, String> colors = null;
				Outfit outfit = new Outfit(code);
				if (outfitColors != null && !outfitColors.equals("")) {
					String[] split = outfitColors.split("_");
					colors = new HashMap<>();
					colors.put("detail", split[0]);
					colors.put("hair", split[1]);
					colors.put("head", split[2]);
					colors.put("dress", split[3]);
					colors.put("skin", split[4]);
				}
				prepareStatement.setString(1, outfit.getData(colors));
				prepareStatement.setString(2, set.getString("name"));
				prepareStatement.addBatch();
			} while (set.next());
			prepareStatement.executeBatch();
		}
	}


	/**
	 * registers the database access objects for Stendhal.
	 */
	private void registerStendhalDAOs() {

		// define own version in replacement of marauroa's CharacterDAO
		DAORegister.get().register(CharacterDAO.class, new StendhalCharacterDAO());

		// define additional DAOs
		DAORegister.get().register(PostmanDAO.class, new PostmanDAO());
		DAORegister.get().register(StendhalBuddyDAO.class, new StendhalBuddyDAO());
		DAORegister.get().register(StendhalGroupQuestDAO.class, new StendhalGroupQuestDAO());
		DAORegister.get().register(StendhalHallOfFameDAO.class, new StendhalHallOfFameDAO());
		DAORegister.get().register(StendhalKillLogDAO.class, new StendhalKillLogDAO ());
		DAORegister.get().register(StendhalNPCDAO.class, new StendhalNPCDAO());
		DAORegister.get().register(StendhalWebsiteDAO.class, new StendhalWebsiteDAO());
		DAORegister.get().register(AchievementDAO.class, new AchievementDAO());
		DAORegister.get().register(PendingAchievementDAO.class, new PendingAchievementDAO());
		DAORegister.get().register(StendhalItemDAO.class, new StendhalItemDAO());
		DAORegister.get().register(StendhalRPZoneDAO.class, new StendhalRPZoneDAO());
		DAORegister.get().register(StendhalSearchIndexDAO.class, new StendhalSearchIndexDAO());
	}
}
