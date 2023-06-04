/***************************************************************************
 *                    (C) Copyright 2003-2023 - Stendhal                   *
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import marauroa.server.db.DBTransaction;

/**
 * database access for the creature dump used on the website
 *
 * @author hendrik
 */
public class StendhalCreatureDAO {
	private static Logger logger = Logger.getLogger(StendhalCreatureDAO.class);


	/**
	 * Dumps the properties of the specified object into the prepared statement as an operation in
	 * a batch.
	 *
	 * @param stmt
	 *   PreparedStatement in batch mode.
	 * @param creature
	 *   DefaultCreature
	 * @throws
	 *   SQLException in case a database error is thrown.
	 */
	private void dump(PreparedStatement stmt, DefaultCreature creature) throws SQLException {
		stmt.setInt(1, 1);
		stmt.setString(2, creature.getCreatureName());
		stmt.setString(3, creature.getTileId());

		stmt.setString(4, creature.getCreatureClass());
		stmt.setString(5, creature.getCreatureSubclass());
		stmt.setString(6, creature.getShadowStyle());
		stmt.setInt(7, (int) creature.getWidth());
		stmt.setInt(8, (int) creature.getHeight());
		stmt.setString(9, creature.getDescription());

		stmt.setString(10, creature.getBloodClass());
		stmt.setString(11, creature.getCorpseName());
		stmt.setString(12, creature.getHarmlessCorpseName());
		stmt.setInt(13, creature.getCorpseWidth());
		stmt.setInt(14, creature.getCorpseHeight());

		stmt.setInt(15, creature.getHP());
		stmt.setInt(16, creature.getAtk());
		stmt.setInt(17, creature.getRatk());
		stmt.setInt(18, creature.getDef());
		stmt.setInt(19, creature.getXP());
		stmt.setInt(20, creature.getLevel());
		stmt.setInt(21, creature.getRespawnTime());
		stmt.setDouble(22, creature.getSpeed());

		stmt.setString(23, creature.getStatusAttack());
		stmt.setDouble(24, creature.getStatusAttackProbability());
		stmt.setString(25, toStringOrNull(creature.getDamageType()));
		stmt.setString(26, toStringOrNull(creature.getRangedDamageType()));

		/*
		List<EquipItem> getEquipedItems() {
		List<DropItem> getDropsItems() {
		List<EquipItem> getEquipsItems() {
		List<String> getSounds() {
		String getDeathSound() {
		String getMovementSound() {
		Map<String, String> getAiProfiles() {
		Map<Nature, Double> getSusceptibilities() {
		*/

		stmt.addBatch();
	}

	private String toStringOrNull(Enum<?> enumValue) {
		if (enumValue == null) {
			return null;
		}
		return enumValue.toString();
	}

	/**
	 * dumps all creatures
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dump(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();

		// update existing
		transaction.execute("UPDATE creatureinfo SET active=0", null);
		PreparedStatement stmt = transaction.prepareStatement("UPDATE creatureinfo SET "
			+ "active=?, name=?, tile_id=?, class=?, subclass=?, shadow_style=?, width=?, height=?, description=?, "
			+ "blood_class=?, corpse_name=?, harmless_corpse_name=?, corpse_width=?, corpse_height=?, "
			+ "hp=?, atk=?, ratk=?, def=?, xp=?, level=?, respawn_time=?, speed=?, "
			+ "status_attack=?, status_attack_probability=?, damage_type=?, ranged_damage_type=? "
			+ "WHERE name=?", null);

		EntityManager entityManager = SingletonRepository.getEntityManager();
		Collection<DefaultCreature> defaultCreatures = entityManager.getDefaultCreatures();
		Map<String, DefaultCreature> unknown = new HashMap<>();
		for (DefaultCreature creature : defaultCreatures) {
			unknown.put(creature.getCreatureName().trim(), creature);
			stmt.setString(27, creature.getCreatureName());
			dump(stmt, creature);
		}
		stmt.executeBatch();


		// add new
		ResultSet resultSet = transaction.query("SELECT name FROM creatureinfo", null);
		while (resultSet.next()) {
			unknown.remove(resultSet.getString(1));
		}

		stmt = transaction.prepareStatement("INSERT INTO creatureinfo "
				+ "(active, name, tile_id, class, subclass, shadow_style, width, height, description, "
				+ "blood_class, corpse_name, harmless_corpse_name, corpse_width, corpse_height, "
				+ "hp, atk, ratk, def, xp, level, respawn_time, speed, "
				+ "status_attack, status_attack_probability, damage_type, ranged_damage_type) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", null);
		for (DefaultCreature creature : unknown.values()) {
			dump(stmt, creature);
		}
		stmt.executeBatch();

		logger.debug("Completed dumping of creatures in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

}
