/***************************************************************************
 *                    (C) Copyright 2003-2009 - Stendhal                   *
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

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

import org.apache.log4j.Logger;


/**
 * database access for the redundant tables used on the website
 */
public class StendhalWebsiteDAO {
	private static Logger logger = Logger.getLogger(StendhalWebsiteDAO.class);

	public void clearOnlineStatus(DBTransaction transaction) throws SQLException {
		transaction.execute("UPDATE character_stats SET online=0", null);
	}

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

	public void clearOnlineStatus() {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			clearOnlineStatus(transaction);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			TransactionPool.get().rollback(transaction);
		}
	}

	public void setOnlineStatus(final String playerName, final boolean online) {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			setOnlineStatus(transaction, playerName, online);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			TransactionPool.get().rollback(transaction);
		}
	}

	protected int updateCharStats(final DBTransaction transaction, final Player instance) throws SQLException {
		final String query = "UPDATE character_stats SET "
			+ " admin=[admin], sentence='[sentence]', age=[age], level=[level],"
			+ " outfit=[outfit], outfit_colors='[outfit_colors]', xp=[xp], money='[money]',"
			+ " married='[married]', atk='[atk]', def='[def]', hp='[hp]', karma='[karma]',"
			+ " head='[head]', armor='[armor]', lhand='[lhand]', rhand='[rhand]',"
			+ " legs='[legs]', feet='[feet]', cloak='[cloak]', lastseen='[lastseen]',"
			+ " finger='[finger]', zone='[zone]'"
			+ " WHERE name='[name]'";

		Map<String, Object> params = getParamsFromPlayer(instance);
		logger.debug("storeCharacter is running: " + query);
		final int count = transaction.execute(query, params);
		return count;
	}

	private Map<String, Object> getParamsFromPlayer(final Player instance) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("admin", instance.getAdminLevel());
		params.put("sentence", instance.getSentence());
		params.put("age", instance.getAge());
		params.put("level", instance.getLevel());
		params.put("outfit", instance.getOutfit().getCode());
		params.put("outfit_colors", getOutfitColors(instance));
		params.put("xp", instance.getXP());
		params.put("money", instance.getTotalNumberOf("money"));
		params.put("married", extractSpouseOrNull(instance));
		params.put("atk", instance.getAtk());
		params.put("def", instance.getDef());
		params.put("hp", instance.getHP());
		params.put("karma", (int) instance.getKarma());
		params.put("head", extractName(instance.getHelmet()));
		params.put("armor", extractName(instance.getArmor()));
		params.put("lhand", extractHandName(instance, "lhand"));
		params.put("rhand", extractHandName(instance, "rhand"));
		params.put("legs", extractName(instance.getLegs()));
		params.put("feet", extractName(instance.getBoots()));
		params.put("cloak", extractName(instance.getCloak()));
		params.put("finger", extractHandName(instance, "finger"));
		params.put("name", instance.getName());
		String zoneName = "";
		StendhalRPZone zone = instance.getZone();
		if (zone != null) {
			zoneName = zone.getName();
		}
		params.put("zone", zoneName);
		params.put("lastseen", new Timestamp(new Date().getTime()));
		return params;
	}

	protected void insertIntoCharStats(final DBTransaction transaction, final Player instance) throws SQLException {
		final String query = "INSERT INTO character_stats"
			+ " (name, admin, sentence, age, level,"
			+ " outfit, outfit_colors, xp, money, married, atk, def, hp,"
			+ " karma, head, armor, lhand, rhand,"
			+ " legs, feet, cloak, finger, zone, lastseen)"
			+ " VALUES ('[name]', '[admin]', '[sentence]', '[age]', '[level]',"
			+ " '[outfit]', '[outfit_colors]', '[xp]', '[money]', '[married]',"
			+ " '[atk]', '[atk]', '[hp]', '[karma]', '[head]', '[armor]',"
			+ " '[lhand]', '[rhand]', '[legs]', '[feet]', '[cloak]', '[finger]',"
			+ " '[zone]', '[lastseen]')";
		Map<String, Object> params = getParamsFromPlayer(instance);
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
		res.append(Integer.toHexString(MathHelper.parseIntDefault(colors.get("base"), 0)));
		return res.toString();
	}
}
