/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import games.stendhal.server.core.config.ShopGroupsXMLLoader.MerchantConfigurator;
import marauroa.common.Pair;

/**
 * Represents contents & prices of a shop.
 */
public abstract class ShopInventory<T, V> extends LinkedHashMap<String, V> {

	private ShopType shopType;
	private String name;
	private List<MerchantConfigurator> merchantConfigurators = new LinkedList<>();

	/** Items & quantities that can be traded. */
	private Map<String, List<Pair<String, Integer>>> tradeFor;

	public ShopInventory(ShopType shopType, String name) {
		super();
		this.shopType = shopType;
		this.name = name;
	}

	/**
	 * Retrieves the price of an item sold by a shop.
	 *
	 * @param name Name or identifier of item sold.
	 * @return Amount of money required to buy item.
	 */
	public abstract Integer getPrice(final String name);

	public void addMerchantConfigurator(MerchantConfigurator merchantConfigurator) {
		this.merchantConfigurators.add(merchantConfigurator);
	}

	public ImmutableList<MerchantConfigurator> getMerchantConfigurators() {
		return ImmutableList.copyOf(merchantConfigurators);
	}

	public ShopType getShopType() {
		return shopType;
	}

	public String getName() {
		return name;
	}

	public Map<String, List<Pair<String, Integer>>> getTradeFor() {
		return tradeFor;
	}

	public List<Pair<String, Integer>> getTradeFor(final String name) {
		if (tradeFor == null || !tradeFor.containsKey(name)) {
			return null;
		}
		return tradeFor.get(name);
	}

	public void addTradeFor(final String name, final String required, final int count) {
		if (tradeFor == null) {
			tradeFor = new LinkedHashMap<>();
		}
		List<Pair<String, Integer>> temp = getTradeFor(name);
		if (temp == null) {
			temp = new LinkedList<>();
		}
		temp.add(new Pair<>(required, count));
		tradeFor.put(name, temp);
	}

	@Override
	public abstract String toString();
}
