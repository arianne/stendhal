/* $Id$ */
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
package games.stendhal.server.entity.mapstuff.sign;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.ItemInformation;
import games.stendhal.server.entity.npc.shop.OutfitShopsList;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.npc.shop.ShopsList;
import games.stendhal.server.events.ShowItemListEvent;

/**
 * A sign for a ShopList.
 */
public class ShopSign extends Sign implements UseListener {
	private static final Logger logger = Logger.getLogger(ShopSign.class);

	/** The shop list. */
	protected ShopsList shops = SingletonRepository.getShopsList();
	protected OutfitShopsList oshops = SingletonRepository.getOutfitShopsList();

	/** Name of shop */
	protected String shopName;

	/** Caption of sign */
	protected String title;

	/** Caption to display above the table */
	protected String caption;

	/** Type of shop this sign represents. */
	private ShopType shopType;


	/**
	 * Create a shop list sign.
	 *
	 * @param name
	 *            the shop name.
	 * @param title
	 *            the sign title.
	 * @param caption
	 *            the caption above the table
	 * @param shopType
	 *            the shop type
	 */
	public ShopSign(final String name, final String title, final String caption,
			final ShopType shopType) {
		super();
		this.shopName = name;
		this.title = title;
		this.caption = caption;
		this.shopType = shopType;

		put(Actions.ACTION, Actions.LOOK_CLOSELY);
		setResistance(100);

		boolean registered = false;
		if (ShopType.OUTFIT.equals(this.shopType)) {
			registered = oshops.get(this.shopName) != null;
		} else {
			registered = shops.get(this.shopName, this.shopType) != null;
		}
		// show warning for unregistered shops
		if (!registered) {
			logger.warn("Unknown shop '" + this.shopName + "'");
		}
	}

	/**
	 * Create a shop list sign.
	 *
	 * @param name
	 *            the shop name.
	 * @param title
	 *            the sign title.
	 * @param caption
	 *            the caption above the table
	 * @param seller
	 *            true, if this sign is for items sold by an NPC
	 */
	public ShopSign(final String name, final String title, final String caption, final boolean seller) {
		this(name, title, caption, seller ? ShopType.ITEM_SELL : ShopType.ITEM_BUY);
	}

	/**
	 * Handles use-actions.
	 */
	@Override
	public boolean onUsed(RPEntity user) {
		List<Item> itemList = generateItemList();
		ShowItemListEvent event = new ShowItemListEvent(title, caption, itemList);
		user.addEvent(event);
		user.notifyWorldAboutChanges();
		return true;
	}

	/**
	 * Generates the item list for this shop.
	 *
	 * @return ItemList
	 */
	protected List<Item> generateItemList() {
		return generateItemList(shops.get(shopName, shopType));
	}

	/**
	 * Generates the item list for this shop.
	 *
	 * @param items
	 * 		Items and prices to be added to sign.
	 * @return ItemList
	 */
	protected List<Item> generateItemList(final Map<String, Integer> items) {
		final List<Item> itemList = new LinkedList<>();
		if (items == null) {
			logger.error("Unknown shop '" + shopName + "'");
		} else {
			for (Map.Entry<String, Integer> entry : items.entrySet()) {
				itemList.add(prepareItem(entry.getKey(), entry.getValue()));
			}
		}
		return itemList;
	}

	/**
	 * prepares an item for displaying
	 *
	 * @param name   name of item
	 * @param price  price of item (negative is for cases in which the player has to pay money)
	 * @return Item
	 */
	private Item prepareItem(String name, int price) {
		Item prototype = SingletonRepository.getEntityManager().getItem(name);
		Item item = new ItemInformation(prototype);
		if (ShopType.ITEM_SELL.equals(shopType)) {
			item.put("price", -price);
		} else {
			item.put("price", price);
		}
		item.put("description_info", item.describe());
		// compatibility with 0.85 clients
		item.put("description", item.describe());
		return item;
	}
}
