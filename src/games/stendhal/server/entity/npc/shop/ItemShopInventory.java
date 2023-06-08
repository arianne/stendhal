/***************************************************************************
 *                      (C) Copyright 2023 - Stendhal                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.shop;

/**
 * Represents contents & prices of an item shop.
 */
public class ItemShopInventory extends ShopInventory<String, Integer> {

	/**
	 * creates an ItemShopInventory
	 *
	 * @param shopType type of shop
	 * @param name name of shop
	 */
	public ItemShopInventory(ShopType shopType, String name) {
		super(shopType, name);
	}

	/**
	 * Retrieves the price of an item sold or bought by shop.
	 *
	 * @param name String identifier.
	 * @return Amount of money required to buy outfit or null if name not found.
	 */
	@Override
	public Integer getPrice(final String name) {
		return get(name);
	}
}
