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
package games.stendhal.server.entity.npc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;


/**
 * Singleton class that contains inventory and prices of NPC stores.
 */
public final class ShopList {

	private static final Logger logger = Logger.getLogger(ShopList.class);

	/** The singleton instance. */
	private static ShopList instance;

	@Deprecated
	private final Map<String, Map<String, Integer>> contents;

	private final Map<String, Map<String, Integer>> sellerContents;
	private final Map<String, Map<String, Integer>> buyerContents;


	/**
	 * Returns the Singleton instance.
	 *
	 * @return The instance
	 */
	public static ShopList get() {
		if (instance == null) {
			instance = new ShopList();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private ShopList() {
		contents = new HashMap<String, Map<String, Integer>>();
		sellerContents = new HashMap<String, Map<String, Integer>>();
		buyerContents = new HashMap<String, Map<String, Integer>>();
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
	private Map<String, Map<String, Integer>> getContents(final Boolean seller) {
		if (seller != null && seller.equals(true)) {
			return sellerContents;
		} else if (seller != null && seller.equals(false)) {
			return buyerContents;
		}
		return contents;
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
	public Map<String, Integer> getSeller(final String name) {
		return get(true, name);
	}

	/**
	 * Gets the items offered by a buyer shop with their prices.
	 *
	 * @param name
	 *     Shop name.
	 * @return
	 *     Item names and prices.
	 */
	public Map<String, Integer> getBuyer(final String name) {
		return get(false, name);
	}

	/**
	 * Gets a set of shop names.
	 *
	 * @param seller
	 *     Seller or buyer shops.
	 * @return
	 *     Set of shop names.
	 */
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
		return getShops(true);
	}

	/**
	 * Gets a set of all buyer shops.
	 *
	 * @return
	 *     Set of buyer shops.
	 */
	public Set<String> getBuyerShops() {
		return getShops(false);
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
	public void add(final Boolean seller, final String name, final String item,
			final int price) {
		Map<String, Integer> shop;
		final Map<String, Map<String, Integer>> selectedContents = getContents(seller);
		if (selectedContents.containsKey(name)) {
			shop = selectedContents.get(name);
		} else {
			shop = new LinkedHashMap<String, Integer>();
			selectedContents.put(name, shop);
		}
		shop.put(item, Integer.valueOf(price));
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
	public void add(final String name, final String item, final int price) {
		add(null, name, item, price);
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
		add(true, name, item, price);
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
		add(false, name, item, price);
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
	public void configureNPC(final SpeakerNPC npc, final String shopname,
			final boolean seller, final boolean offer) {
		String stype = "sell";
		if (!seller) {
			stype = "buy";
		}

		final String npcname = npc.getName();
		if (npc == null) {
			logger.error("Cannot configure " + stype + "er shop \""
					+ shopname + "\" for non-existing NPC " + npcname);
			return;
		}
		final Map<String, Integer> shoplist = get(seller, shopname);
		if (shoplist == null) {
			logger.error("Cannot configure non-existing " + stype
					+ "er shop \"" + shopname + "\" for NPC " + npcname);
			return;
		}

		String msg = "Configuring " + stype + "er shop \"" + shopname
				+ "\" for NPC " + npcname + " with offer ";
		if (offer) {
			msg += "enabled";
		} else {
			msg += "disabled";
		}
		logger.info(msg);
		if (seller) {
			new SellerAdder().addSeller(npc, new SellerBehaviour(shoplist), offer);
		} else {
			new BuyerAdder().addBuyer(npc, new BuyerBehaviour(shoplist), offer);
		}
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
	public void configureNPC(final String npcname, final String shopname,
			final boolean seller, final boolean offer) {
		configureNPC(SingletonRepository.getNPCList().get(npcname),
				shopname, seller, offer);
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
	public void configureNPC(final String npcname, final String shopname,
			final boolean seller) {
		configureNPC(npcname, shopname, seller, true);
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
	public String toString(final Boolean seller, final String name,
			final String header) {
		final Map<String, Integer> items = getContents(seller).get(name);

		final StringBuilder sb = new StringBuilder();
		if (seller != null && seller.equals(true)) {
			sb.append("Seller: ");
		} else if (seller != null && seller.equals(false)) {
			sb.append("Buyer: ");
		}
		sb.append(header + "\n");
		for (final Entry<String, Integer> entry : items.entrySet()) {
			sb.append(entry.getKey() + " \t" + entry.getValue() + "\n");
		}
		return sb.toString();
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
	public String toString(final String name, final String header) {
		return toString(null, name, header);
	}
}
