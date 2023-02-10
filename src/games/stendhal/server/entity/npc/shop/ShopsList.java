/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;


/**
 * Singleton class that contains inventory and prices of NPC stores.
 */
public final class ShopsList {

	private static final Logger logger = Logger.getLogger(ShopsList.class);

	@Deprecated
	private final Map<String, ItemShopInventory> contents;

	private final Map<String, ItemShopInventory> sellerContents;
	private final Map<String, ItemShopInventory> buyerContents;

	/** The singleton instance. */
	private static ShopsList instance;


	/**
	 * Returns the Singleton instance.
	 *
	 * @return The instance
	 */
	public static ShopsList get() {
		if (instance == null) {
			instance = new ShopsList();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private ShopsList() {
		contents = new HashMap<String, ItemShopInventory>();
		sellerContents = new HashMap<String, ItemShopInventory>();
		buyerContents = new HashMap<String, ItemShopInventory>();
	}

	/**
	 * Get list of shop contents.
	 *
	 * @param stype
	 *     Seller or buyer shop.
	 * @return
	 *     Seller or buyer shop list (or deprecated generic shop list if
	 *     <code>seller</code> is null.
	 */
	private Map<String, ItemShopInventory> getContents(final ShopType stype) {
		if (ShopType.ITEM_SELL.equals(stype)) {
			return sellerContents;
		} else if (ShopType.ITEM_BUY.equals(stype)) {
			return buyerContents;
		}
		return contents;
	}

	/**
	 * Get list of shop contents.
	 *
	 * @param seller
	 *     Seller or buyer shop.
	 * @return
	 *     Seller or buyer shop list (or deprecated generic shop list if
	 *     <code>seller</code> is null.
	 */
	@Deprecated
	private Map<String, ItemShopInventory> getContents(final Boolean seller) {
		return getContents(seller == true ? ShopType.ITEM_SELL : seller == false ? ShopType.ITEM_BUY : null);
	}

	/**
	 * Gets the items offered by a shop with their prices.
	 *
	 * @param name
	 *     Shop name.
	 * @param seller
	 *     Seller or buyer shop.
	 * @return
	 *     Item names and prices.
	 */
	public ItemShopInventory get(final String name, final ShopType stype) {
		return getContents(stype).get(name);
	}

	/**
	 * Gets the items offered by a shop with their prices.
	 *
	 * @param seller
	 *     Seller or buyer shop.
	 * @param name
	 *     Shop name.
	 * @return
	 *     Item names and prices.
	 */
	@Deprecated
	public Map<String, Integer> get(final Boolean seller, final String name) {
		return getContents(seller).get(name);
	}

	/**
	 * Gets the items offered by a shop with their prices.
	 *
	 * @param name
	 *     Shop name.
	 * @return
	 *     Item names and prices.
	 */
	@Deprecated
	public Map<String, Integer> get(final String name) {
		if (contents.containsKey(name)) {
			return contents.get(name);
		} else if (sellerContents.containsKey(name)) {
			return sellerContents.get(name);
		}
		return buyerContents.get(name);
	}

	/**
	 * Gets the items offered by a seller shop with their prices.
	 *
	 * @param name
	 *     Shop name.
	 * @return
	 *     Item names and prices.
	 */
	public ItemShopInventory getSeller(final String name) {
		return get(name, ShopType.ITEM_SELL);
	}

	/**
	 * Gets the items offered by a buyer shop with their prices.
	 *
	 * @param name
	 *     Shop name.
	 * @return
	 *     Item names and prices.
	 */
	public ItemShopInventory getBuyer(final String name) {
		return get(name, ShopType.ITEM_BUY);
	}

	/**
	 * Gets a set of shop names.
	 *
	 * @param stype
	 *     Seller or buyer shops.
	 * @return
	 *     Set of shop names.
	 */
	public Set<String> getShops(final ShopType stype) {
		return getContents(stype).keySet();
	}

	/**
	 * Gets a set of shop names.
	 *
	 * @param seller
	 *     Seller or buyer shops.
	 * @return
	 *     Set of shop names.
	 */
	@Deprecated
	public Set<String> getShops(final Boolean seller) {
		return getContents(seller).keySet();
	}

	/**
	 * Gets a set of all shop names.
	 *
	 * @return
	 *     Set of shop names.
	 */
	public Set<String> getShops() {
		final Set<String> shopNames = contents.keySet();
		shopNames.addAll(sellerContents.keySet());
		shopNames.addAll(buyerContents.keySet());
		return shopNames;
	}

	/**
	 * Gets a set of all seller shops.
	 *
	 * @return
	 *     Set of seller shops.
	 */
	public Set<String> getSellerShops() {
		return getShops(ShopType.ITEM_SELL);
	}

	/**
	 * Gets a set of all buyer shops.
	 *
	 * @return
	 *     Set of buyer shops.
	 */
	public Set<String> getBuyerShops() {
		return getShops(ShopType.ITEM_BUY);
	}

	/**
	 * Add an items to a shop.
	 *
	 * @param name
	 *     Shop name.
	 * @param stype
	 *     Seller or buyer shop.
	 * @param inventory
	 *     Item list with prices.
	 */
	public void add(final String name, final ShopType stype, final ItemShopInventory inventory) {
		final Map<String, ItemShopInventory> selectedContents = getContents(stype);
		if (selectedContents.containsKey(name)) {
			selectedContents.get(name).putAll(inventory);
		} else {
			selectedContents.put(name, inventory);
		}
	}

	/**
	 * Add an item to a shop.
	 *
	 * @param name
	 *     Shop name.
	 * @param stype
	 *     Seller or buyer shop.
	 * @param item
	 *     Name of item to add.
	 * @param price
	 *     Value of the item.
	 */
	public void add(final String name, final ShopType stype, final String item, final int price) {
		final ItemShopInventory inventory = new ItemShopInventory();
		inventory.put(item, price);
		add(name, stype, inventory);
	}

	/**
	 * Add an item to a shop.
	 *
	 * @param seller
	 *     Seller or buyer shop.
	 * @param name
	 *     Shop name.
	 * @param item
	 *     Name of item to add.
	 * @param price
	 *     Value of the item.
	 */
	@Deprecated
	public void add(final Boolean seller, final String name, final String item,
			final int price) {
		add(name, seller == true ? ShopType.ITEM_SELL : seller == false ? ShopType.ITEM_BUY : null,
				item, price);
	}

	/**
	 * Add an item to a shop.
	 *
	 * @param name
	 *     Shop name.
	 * @param item
	 *     Name of item to add.
	 * @param price
	 *     Value of the item.
	 */
	@Deprecated
	public void add(final String name, final String item, final int price) {
		add(name, null, item, price);
	}

	/**
	 * Add an item to a seller shop.
	 *
	 * @param name
	 *     Shop name.
	 * @param item
	 *     Name of item to add.
	 * @param price
	 *     Value of the item.
	 */
	public void addSeller(final String name, final String item, final int price) {
		add(name, ShopType.ITEM_SELL, item, price);
	}

	/**
	 * Add an item to a buyer shop.
	 *
	 * @param name
	 *     Shop name.
	 * @param item
	 *     Name of item to add.
	 * @param price
	 *     Value of the item.
	 */
	public void addBuyer(final String name, final String item, final int price) {
		add(name, ShopType.ITEM_BUY, item, price);
	}

	/**
	 * Configures an NPC for a shop.
	 *
	 * @param npc
	 *     NPC being configured.
	 * @param shopname
	 *     Shop string identifier.
	 * @param stype
	 *     Seller or buyer shop.
	 * @param offer
	 *     If <code>true</code>, adds reply to "offer".
	 */
	public void configureNPC(final SpeakerNPC npc, final String shopname, final ShopType stype,
			final boolean offer) {
		if (npc == null) {
			logger.error("Cannot configure " + stype + "er shop \""
					+ shopname + "\" for non-existing NPC");
			return;
		}

		final String npcname = npc.getName();
		final ItemShopInventory inventory = get(shopname, stype);
		if (inventory == null) {
			logger.error("Cannot configure non-existing " + stype
					+ "er shop \"" + shopname + "\" for NPC " + npcname);
			return;
		}

		String msg = "Configuring " + stype + "er shop \"" + shopname + "\" for NPC " + npcname
				+ " with offer " + (offer ? "enabled" : "disabled");
		logger.info(msg);
		if (ShopType.ITEM_SELL.equals(stype)) {
			new SellerAdder().addSeller(npc, new SellerBehaviour(inventory), offer);
		} else {
			new BuyerAdder().addBuyer(npc, new BuyerBehaviour(inventory), offer);
		}
	}

	/**
	 * Configures an NPC for a shop.
	 *
	 * @param npc
	 *     NPC being configured.
	 * @param shopname
	 *     Shop string identifier.
	 * @param seller
	 *     Seller or buyer shop.
	 * @param offer
	 *     If <code>true</code>, adds reply to "offer".
	 */
	@Deprecated
	public void configureNPC(final SpeakerNPC npc, final String shopname,
			final boolean seller, final boolean offer) {
		configureNPC(npc, shopname, seller == true ? ShopType.ITEM_SELL : ShopType.ITEM_BUY, offer);
	}

	/**
	 * Configures an NPC for a shop.
	 *
	 * @param npcname
	 *     Name of NPC being configured.
	 * @param shopname
	 *     Shop string identifier.
	 * @param stype
	 *     Seller or buyer shop.
	 * @param offer
	 *     If <code>true</code>, adds reply to "offer".
	 */
	public void configureNPC(final String npcname, final String shopname, final ShopType stype,
			final boolean offer) {
		configureNPC(SingletonRepository.getNPCList().get(npcname), shopname, stype, offer);
	}

	/**
	 * Configures an NPC for a shop.
	 *
	 * @param npcname
	 *     Name of NPC being configured.
	 * @param shopname
	 *     Shop string identifier.
	 * @param seller
	 *     Seller or buyer shop.
	 * @param offer
	 *     If <code>true</code>, adds reply to "offer".
	 */
	@Deprecated
	public void configureNPC(final String npcname, final String shopname,
			final boolean seller, final boolean offer) {
		configureNPC(npcname, shopname, seller ? ShopType.ITEM_SELL : ShopType.ITEM_BUY, offer);
	}

	/**
	 * Configures an NPC for a shop.
	 *
	 * @param npcname
	 *     Name of NPC being configured.
	 * @param shopname
	 *     Shop string identifier.
	 * @param stype
	 *     Seller or buyer shop.
	 */
	public void configureNPC(final String npcname, final String shopname, final ShopType stype) {
		configureNPC(npcname, shopname, stype, true);
	}

	/**
	 * Configures an NPC for a shop.
	 *
	 * @param seller
	 *     Seller or buyer shop.
	 * @param shopname
	 *     Shop string identifier.
	 * @param npcname
	 *     Name of NPC being configured.
	 */
	@Deprecated
	public void configureNPC(final String npcname, final String shopname,
			final boolean seller) {
		configureNPC(npcname, shopname, seller ? ShopType.ITEM_SELL : ShopType.ITEM_BUY);
	}

	/**
	 * Converts a shop into a human readable form.
	 *
	 * @param name
	 *     Shop name.
	 * @param stype
	 *     Seller or buyer shop.
	 * @param header
	 *     Prefix.
	 * @return
	 *     Human readable description.
	 */
	public String toString(final String name, final ShopType stype, final String header) {
		final ItemShopInventory inventory = getContents(stype).get(name);

		final StringBuilder sb = new StringBuilder();
		if (ShopType.ITEM_SELL.equals(stype)) {
			sb.append("Seller: ");
		} else if (ShopType.ITEM_BUY.equals(stype)) {
			sb.append("Buyer: ");
		}
		sb.append(header + "\n");
		for (final Entry<String, Integer> entry: inventory.entrySet()) {
			sb.append(entry.getKey() + " \t" + entry.getValue() + "\n");
		}
		return sb.toString();
	}

	/**
	 * Converts a shop into a human readable form.
	 *
	 * @param seller
	 *     Seller or buyer shop.
	 * @param name
	 *     Shop name.
	 * @param header
	 *     Prefix.
	 * @return
	 *     Human readable description.
	 */
	@Deprecated
	public String toString(final Boolean seller, final String name,
			final String header) {
		return toString(name,
				seller == true ? ShopType.ITEM_SELL : seller == false ? ShopType.ITEM_BUY : null, header);
	}

	/**
	 * Converts a shop into a human readable form.
	 *
	 * @param name
	 *     Shop name.
	 * @param header
	 *     Prefix.
	 * @return
	 *     Human readable description.
	 */
	@Deprecated
	public String toString(final String name, final String header) {
		return toString(null, name, header);
	}
}
