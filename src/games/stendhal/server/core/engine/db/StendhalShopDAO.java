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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.core.config.ShopGroupsXMLLoader.MerchantConfigurator;
import games.stendhal.server.entity.npc.shop.ItemShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopsList;
import games.stendhal.server.entity.npc.shop.ShopInventory;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.npc.shop.ShopsList;
import marauroa.server.db.DBTransaction;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Dumps shops to the database for use by the website
 */
public class StendhalShopDAO extends CharacterDAO {
	private static Logger logger = Logger.getLogger(StendhalCreatureDAO.class);

	/*
	 * Shop name type ["buy", "sell", "outfit"]
	 *
	 *
	 * Item name price
	 *
	 * Outfit name layers price
	 *
	 *
	 * Merchant flag-configure flag-noOffer
	 *
	 * outfit-flag-removeDetailColor outfit-flag-confirmTemp
	 * outfit-flag-confirmBalloon outfit-flag-resetBeforeChange
	 *
	 * note
	 */

	/**
	 * Dumps the properties of the specified object into the prepared statement as
	 * an operation in a batch.
	 *
	 * @param stmt PreparedStatement in batch mode.
	 * @param shop Shop
	 * @throws SQLException in case a database error is thrown.
	 */
	private void dumpShop(PreparedStatement stmt, ShopInventory<?, ?> shop) throws SQLException {
		stmt.setInt(1, 1);
		stmt.setString(2, shop.getName());
		stmt.setString(3, shop.getShopType().toString());
		stmt.addBatch();
	}

	/**
	 * dumps all creatures
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dumpShops(DBTransaction transaction) throws SQLException {

		// update existing
		transaction.execute("UPDATE shopinfo SET active=0", null);
		PreparedStatement stmt = transaction.prepareStatement("UPDATE shopinfo SET "
				+ "active=?, name=?, shop_type=? "
				+ "WHERE name=?", null);

		Map<String, ShopInventory<?, ?>> unknown = new HashMap<>();
		List<ShopInventory<?, ?>> shops = getShops();
		for (ShopInventory<?, ?> shop : shops) {
			unknown.put(shop.getName(), shop);
			stmt.setString(4, shop.getName());
			dumpShop(stmt, shop);
		}
		stmt.executeBatch();

		// add new
		Set<String> known = getShopIdMap(transaction).keySet();
		for (String knownEntry : known) {
			unknown.remove(knownEntry);
		}

		stmt = transaction.prepareStatement("INSERT INTO shopinfo "
				+ "(active, name, shop_type) "
				+ "VALUES (?, ?, ?);", null);
		for (ShopInventory<?, ?> shop : unknown.values()) {
			dumpShop(stmt, shop);
		}
		stmt.executeBatch();
	}

	private void dumpShopInventory(PreparedStatement stmt, ShopInventory<?, ?> shop,
			Map<String, Integer> shopIdMap,
			Map<String, Integer> itemInfoIdMap) throws SQLException {
		String shopName = shop.getName();
		Integer shopId = shopIdMap.get(shopName);

		for (String name : shop.keySet()) {
			String outfit = null;
			Integer itemId = null;
			if (shop instanceof ItemShopInventory) {
				itemId = itemInfoIdMap.get(name);
			} else if (shop instanceof OutfitShopInventory){
				outfit = ((OutfitShopInventory) shop).get(name).first();
			}
			stmt.setInt(1, 1);
			stmt.setObject(2, shopId);
			stmt.setString(3, name);
			stmt.setObject(4, shop.getPrice(name));
			stmt.setObject(5, itemId);
			stmt.setObject(6, outfit);
			stmt.addBatch();
		}
	}

	private void dumpShopIventories(DBTransaction transaction) throws SQLException {
		transaction.execute("DELETE FROM shopinventoryinfo", null);

		List<ShopInventory<?, ?>> shops = getShops();
		PreparedStatement stmt = transaction.prepareStatement("INSERT INTO shopinventoryinfo "
				+ "(active, shopinfo_id, name, price, iteminfo_id, outfit) "
				+ "VALUES (?, ?, ?, ?, ?, ?);", null);
		Map<String, Integer> shopIdMap = getShopIdMap(transaction);
		Map<String, Integer> itemInfoIdMap = DAORegister.get().get(StendhalItemDAO.class).getItemInfoIdMap(transaction);
		for (ShopInventory<?, ?> shop : shops) {
			dumpShopInventory(stmt, shop, shopIdMap, itemInfoIdMap);
		}
		stmt.executeBatch();
	}

	private void dumpShopOwner(PreparedStatement stmt, ShopInventory<?, ?> shop,
			Map<String, Integer> shopIdMap,
			Map<String, Integer> npcIdMap) throws SQLException {

		String shopName = shop.getName();
		Integer shopId = shopIdMap.get(shopName);

		for (MerchantConfigurator mc : shop.getMerchantConfigurators()) {
			stmt.setInt(1, 1);
			stmt.setObject(2, npcIdMap.get(mc.npc));
			stmt.setObject(3, shopId);
			stmt.setObject(4, mc.factor);
			stmt.addBatch();
		}
		stmt.executeBatch();

	}

	private void dumpShopOwners(DBTransaction transaction) throws SQLException {
		transaction.execute("DELETE FROM shopownerinfo", null);

		List<ShopInventory<?, ?>> shops = getShops();
		PreparedStatement stmt = transaction.prepareStatement("INSERT INTO shopownerinfo "
				+ "(active, npcinfo_id, shopinfo_id, price_factor) "
				+ "VALUES (?, ?, ?, ?);", null);
		Map<String, Integer> shopIdMap = getShopIdMap(transaction);
		Map<String, Integer> npcIdMap = DAORegister.get().get(StendhalNPCDAO.class).getIdMap(transaction);
		for (ShopInventory<?, ?> shop : shops) {
			dumpShopOwner(stmt, shop, shopIdMap, npcIdMap);
		}
		stmt.executeBatch();
	}

	private List<ShopInventory<?, ?>> getShops() {
		List<ShopInventory<?, ?>> shops = new LinkedList<>();
		shops.addAll(ShopsList.get().getContents(ShopType.ITEM_BUY).values());
		shops.addAll(ShopsList.get().getContents(ShopType.ITEM_SELL).values());
		shops.addAll(OutfitShopsList.get().getContents().values());
		return shops;
	}

	private Map<String, Integer> getShopIdMap(DBTransaction transaction) throws SQLException {
		return transaction.queryAsMap("SELECT name, id FROM shopinfo", null);
	}


	public void dump(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();
		dumpShops(transaction);
		dumpShopIventories(transaction);
		dumpShopOwners(transaction);
		logger.debug("Completed dumping of shops in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}
}
