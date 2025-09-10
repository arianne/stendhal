/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.MerchantNPC;
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

	private final Map<String, ItemShopInventory> sellerContents;
	private final Map<String, ItemShopInventory> buyerContents;
	private final Map<String, ItemShopInventory> traderContents;

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
		sellerContents = new HashMap<String, ItemShopInventory>();
		buyerContents = new HashMap<String, ItemShopInventory>();
		traderContents = new HashMap<String, ItemShopInventory>();
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
	public Map<String, ItemShopInventory> getContents(final ShopType stype) {
		if (ShopType.ITEM_SELL.equals(stype)) {
			return sellerContents;
		} else if (ShopType.ITEM_BUY.equals(stype)) {
			return buyerContents;
		} else if (ShopType.TRADE.equals(stype)) {
			return traderContents;
		}
		return null;
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
	 * Add items to a shop.
	 *
	 * @param name
	 *   Shop name.
	 * @param stype
	 *   Seller, buyer, or trader shop.
	 * @param inventory
	 *   Item list with prices.
	 */
	public void add(final String name, final ShopType stype, final ItemShopInventory inventory) {
		final Map<String, ItemShopInventory> selectedContents = getContents(stype);
		if (selectedContents == null) {
			logger.error("Unsupported shop type \"" + stype + "\"");
			return;
		}
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
	 *   Shop name.
	 * @param stype
	 *   Seller, buyer, or trader shop.
	 * @param item
	 *   Item name.
	 * @param price
	 *   Item price.
	 */
	public void add(final String name, final ShopType stype, final String item, final int price) {
		final ItemShopInventory inventory = new ItemShopInventory(stype, name);
		inventory.put(item, price);
		add(name, stype, inventory);
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
	 * @param priceFactor
	 *     Skews prices of all items for this merchant.
	 * @param offer
	 *     If <code>true</code>, adds reply to "offer".
	 */
	public void configureNPC(final SpeakerNPC npc, final String shopname, final ShopType stype,
			final Float priceFactor, final boolean offer) {
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
			new SellerAdder().addSeller(npc, new SellerBehaviour(inventory, priceFactor), offer);
		} else {
			new BuyerAdder().addBuyer(npc, new BuyerBehaviour(inventory, priceFactor), offer);
		}

		if (npc instanceof MerchantNPC) {
			// register with merchant NPC for retrieving shop inventory for interactive dialogs
			((MerchantNPC) npc).addShop(stype, shopname);
		}
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
		configureNPC(npc, shopname, stype, null, offer);
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
	 * @param priceFactor
	 *     Skews prices of all items for this merchant.
	 * @param offer
	 *     If <code>true</code>, adds reply to "offer".
	 */
	public void configureNPC(final String npcname, final String shopname, final ShopType stype,
			final Float priceFactor, final boolean offer) {
		configureNPC(SingletonRepository.getNPCList().get(npcname), shopname, stype, priceFactor,
				offer);
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
		configureNPC(npcname, shopname, stype, null, offer);
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
		configureNPC(npcname, shopname, seller ? ShopType.ITEM_SELL : ShopType.ITEM_BUY, true);
	}

}
