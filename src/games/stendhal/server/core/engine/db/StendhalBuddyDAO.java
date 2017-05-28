/***************************************************************************
 *                    (C) Copyright 2003-2010 - Stendhal                   *
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import games.stendhal.server.entity.player.Player;
import marauroa.server.db.DBTransaction;

/**
 * database access for the redundant buddy table used on the website
 */
public class StendhalBuddyDAO {

	/**
	 * loads the relationship lists for the specified charname
	 *
	 * @param transaction DBTransaction
	 * @param charname name of char
	 * @return buddy list
	 * @throws SQLException in case of an database error
	 */
	public Multimap<String, String> loadRelations(DBTransaction transaction, String charname) throws SQLException {
		HashMultimap<String, String> map = HashMultimap.create();
		String query = "SELECT relationtype, buddy FROM buddy WHERE charname='[charname]'";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("charname", charname);
		ResultSet resultSet = transaction.query(query, params);
		while (resultSet.next()) {
			map.put(resultSet.getString(1), resultSet.getString(2));
		}
		return map;
	}


	/**
	 * checks whether a palyer is ignored by another player
	 *
	 * @param transaction DBTransaction
	 *
	 * @param character character whose ignore list is checked
	 * @param candidate candidate who might have been ignored
	 * @return true, if the player is ginored, false otherwise
	 * @throws SQLException SQLException
	 */
	public boolean isIgnored(DBTransaction transaction, String character, String candidate) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("charname", character);
		params.put("candidate", candidate);
		int count = transaction.querySingleCellInt("SELECT count(*) FROM buddy WHERE charname='[charname]' AND buddy='[candidate]' AND relationtype='ignore'", params);
		return count > 0;
	}


	/**
	 * saves the buddy list for the specified charname
	 *
	 * @param transaction transaction
	 * @param charname name of char
	 * @param player player
	 * @throws SQLException in case of an database error
	 */
	public void saveRelations(DBTransaction transaction, String charname, Player player) throws SQLException {
		Multimap<String, String> oldList = loadRelations(transaction, charname);

		Set<String> buddies = player.getBuddies();
		buddies.add(charname);
		syncBuddyListToDB(transaction, charname, "buddy", oldList.get("buddy"), buddies);
		syncBuddyListToDB(transaction, charname, "ignore", oldList.get("ignore"), player.getIgnores());
	}


	/**
	 * writes the current buddy list to the database, minimizing the write operations.
	 *
	 * @param transaction DBTransaction
	 * @param charname name of character
	 * @param relationtype type of the relationship
	 * @param oldList  old buddy list from db
	 * @param newList  current buddy list
	 * @throws SQLException
	 * @throws SQLException in case of an database error
	 */
	private void syncBuddyListToDB(DBTransaction transaction, String charname, String relationtype, Collection<String> oldList, Collection<String> newList) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("charname", charname);
		params.put("relationtype", relationtype);

		// add
		Set<String> toAdd = new TreeSet<String>(newList);
		toAdd.removeAll(oldList);
		String query = "INSERT INTO buddy (charname, relationtype, buddy) VALUES ('[charname]', '[relationtype]', '[buddy]')";
		for (String buddy : toAdd) {
			params.put("buddy", buddy);
			transaction.execute(query, params);
		}

		// delete
		Set<String> toDel = new TreeSet<String>(oldList);
		toDel.removeAll(newList);
		query = "DELETE FROM buddy WHERE charname='[charname]' AND buddy='[buddy]' AND relationtype='[relationtype]'";
		for (String buddy : toDel) {
			params.put("buddy", buddy);
			transaction.execute(query, params);
		}
	}


}
