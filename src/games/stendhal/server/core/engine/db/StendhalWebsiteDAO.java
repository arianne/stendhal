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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;


/**
 * database access for the redundant tables used on the website
 */
public class StendhalWebsiteDAO {
	private static Logger logger = Logger.getLogger(StendhalWebsiteDAO.class);

	/**
	 * clears the online status of all players (used on server startup)
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void clearOnlineStatus(DBTransaction transaction) throws SQLException {
		transaction.execute("UPDATE character_stats SET online=0", null);
	}

	/**
	 * sets the online status of a particular player
	 *
	 * @param transaction DBTransaction
	 * @param playerName name of player
	 * @param online true, if the player is online; false otherwise
	 * @throws SQLException in case of an database error
	 */
	public void setOnlineStatus(final DBTransaction transaction, final String playerName, final boolean online) throws SQLException {
		String onlinestate;
		if (online) {
			onlinestate = "1";
		} else {
			onlinestate = "0";
		}
		// first try an update
		final String query = "UPDATE character_stats SET online='[onlinestate]'"
			+ " WHERE name='[name]'";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("onlinestate", onlinestate);
		params.put("name", playerName);
		logger.debug("setOnlineStatus is running: " + query);

		transaction.execute(query, params);
	}

	/**
	 * clears the online status of all players (used on server startup)
	 */
	public void clearOnlineStatus() {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			clearOnlineStatus(transaction);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			TransactionPool.get().rollback(transaction);
		}
	}

	/**
	 * logs a trade event
	 *
	 * @param transaction DBTransaction
	 * @param charname name of character
	 * @param itemname name of item
	 * @param itemid   id of item
	 * @param quantity quantity
	 * @param price    price
	 * @param stats    description of item
	 * @param timestamp timestamp
	 * @throws SQLException in case of an database error
	 */
	public void logTradeEvent(final DBTransaction transaction, String charname, String itemname, int itemid,
			int quantity, int price, String stats, Timestamp timestamp) throws SQLException {
		String sql = "INSERT INTO trade(charname, itemname, itemid, quantity, price, stats, timedate) "
				+ " VALUES ('[charname]', '[itemname]', [itemid], [quantity], [price], '[stats]', '[timedate]')";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("charname", charname);
		params.put("itemname", itemname);
		params.put("itemid", itemid);
		params.put("quantity", quantity);
		params.put("price", price);
		params.put("stats", stats);
		params.put("timedate", timestamp);
		transaction.execute(sql, params);
	}

	/**
	 * updates the statistics information about a player
	 *
	 * @param transaction DBTransaction
	 * @param player Player
	 * @param timestamp timestamp
	 * @return number of updates rows
	 * @throws SQLException in case of an database error
	 */
	protected int updateCharStats(final DBTransaction transaction, final Player player, Timestamp timestamp) throws SQLException {
		final String query = "UPDATE character_stats SET "
			+ " admin=[admin], sentence='[sentence]', age=[age], level=[level],"
			+ " outfit=[outfit], outfit_colors='[outfit_colors]', outfit_layers='[outfit_layers]', xp=[xp], money='[money]',"
			+ " married='[married]', atk='[atk]', def='[def]', hp='[hp]', karma='[karma]',"
			+ " head='[head]', armor='[armor]', lhand='[lhand]', rhand='[rhand]',"
			+ " legs='[legs]', feet='[feet]', cloak='[cloak]', lastseen='[lastseen]',"
			+ " finger='[finger]', zone='[zone]'"
			+ " WHERE name='[name]'";

		Map<String, Object> params = getParamsFromPlayer(player);
		params.put("lastseen", timestamp);
		logger.debug("storeCharacter is running: " + query);
		final int count = transaction.execute(query, params);
		return count;
	}

	/**
	 * gets the attributes from a player object.
	 *
	 * @param player Player
	 * @return Map with key value pairs
	 */
	private Map<String, Object> getParamsFromPlayer(final Player player) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("admin", player.getAdminLevel());
		params.put("sentence", player.getSentence());
		params.put("age", player.getAge());
		params.put("level", player.getLevel());
		params.put("outfit", player.getOutfit().getCode());
		params.put("outfit_colors", getOutfitColors(player));
		params.put("outfit_layers", player.getOutfit().getData(player.getOutfitColors()));
		params.put("xp", player.getXP());
		params.put("money", player.getTotalNumberOf("money"));
		params.put("married", extractSpouseOrNull(player));
		params.put("atk", player.getAtk());
		params.put("def", player.getDef());
		params.put("hp", player.getHP());
		params.put("karma", (int) player.getKarma());
		params.put("head", extractName(player.getHelmet()));
		params.put("armor", extractName(player.getArmor()));
		params.put("lhand", extractHandName(player, "lhand"));
		params.put("rhand", extractHandName(player, "rhand"));
		params.put("legs", extractName(player.getLegs()));
		params.put("feet", extractName(player.getBoots()));
		params.put("cloak", extractName(player.getCloak()));
		params.put("finger", extractHandName(player, "finger"));
		params.put("name", player.getName());
		String zoneName = "";
		StendhalRPZone zone = player.getZone();
		if (zone != null) {
			zoneName = zone.getName();
		}
		params.put("zone", zoneName);
		return params;
	}

	/**
	 * Insert statistics information about a new player
	 *
	 *
	 * @param transaction DBTransaction
	 * @param player Player
	 * @throws SQLException in case of an database error
	 */
	protected void insertIntoCharStats(final DBTransaction transaction, final Player player, Timestamp timestamp) throws SQLException {
		final String query = "INSERT INTO character_stats"
			+ " (name, admin, sentence, age, level,"
			+ " outfit, outfit_colors, outfit_layers, xp, money, married, atk, def, hp,"
			+ " karma, head, armor, lhand, rhand,"
			+ " legs, feet, cloak, finger, zone, lastseen)"
			+ " VALUES ('[name]', '[admin]', '[sentence]', '[age]', '[level]',"
			+ " '[outfit]', '[outfit_colors]', '[outfit_layers]', '[xp]', '[money]', '[married]',"
			+ " '[atk]', '[atk]', '[hp]', '[karma]', '[head]', '[armor]',"
			+ " '[lhand]', '[rhand]', '[legs]', '[feet]', '[cloak]', '[finger]',"
			+ " '[zone]', '[lastseen]')";
		Map<String, Object> params = getParamsFromPlayer(player);
		params.put("lastseen", timestamp);
		logger.debug("storeCharacter is running: " + query);
		transaction.execute(query, params);
	}

	/**
	 * Used to get the items in the hands container, as they can be different to weapons or shields...
	 * Could also be done using getEquippedItemClass and using all posibble classes for
	 * the objects that can be used in hands.
	 *
	 * @param instance player
	 * @param handSlot hand slot name
	 * @return item name
	 */
	private String extractHandName(final Player instance, final String handSlot) {
		if (instance != null && handSlot != null) {
			if (instance.hasSlot(handSlot)) {
				final RPSlot rpslot = instance.getSlot(handSlot);
					// traverse all slot items
					for (final RPObject object : rpslot) {
						// is it the right type
						if (object instanceof Item) {
							final Item item = (Item) object;
							return item.getName();
						}
					}
					return null;
			}
			return null;
		}
		return null;
	}

	private String extractName(final Item item) {
		if (item != null) {
			return item.getName();
		}
		return null;
	}

	private String extractSpouseOrNull(final Player instance) {
		if (!instance.hasSlot("!quests")) {
			// first login, Player object has not been fully constructed (fixes https://sourceforge.net/tracker/index.php?func=detail&aid=2854092&group_id=1111&atid=101111 )
			return null;
		}
		if (instance.hasQuest("spouse")) {
			return instance.getQuest("spouse");
		} else {
			return null;
		}
	}

	private String getOutfitColors(final Player player) {
		Map<String, String> colors = player.getOutfitColors();
		if (colors == null) {
			return "";
		}
		StringBuilder res = new StringBuilder();
		res.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("detail"), 0)));
		res.append("_");
		res.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("hair"), 0)));
		res.append("_");
		res.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("head"), 0)));
		res.append("_");
		res.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("dress"), 0)));
		res.append("_");
		res.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("skin"), 0)));
		return res.toString();
	}
}
