/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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
import java.util.Map;
import java.util.Set;

import games.stendhal.server.entity.npc.shop.OutfitShopsList;
import games.stendhal.server.entity.npc.shop.ShopInventory;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.npc.shop.ShopsList;


/**
 * An NPC that manages one or more shops.
 */
public class MerchantNPC extends SpeakerNPC {

	/** Shops that this NPC manages. */
	private final Map<ShopType, String> shops;


	/**
	 * Creates a new shopkeeper.
	 *
	 * @param name
	 *   NPC's name.
	 */
	public MerchantNPC(final String name) {
		super(name);
		shops = new HashMap<>();
	}

	/**
	 * Adds a shop to this NPC.
	 *
	 * @param type
	 *   Shop type (buy, sell, outfit).
	 * @param name
	 *   Shop name identifier used to retrieve inventory from shops list.
	 */
	public void addShop(final ShopType type, final String name) {
		shops.put(type, name);
	}

	/**
	 * Retrieves NPC's shop inventory list.
	 *
	 * @param type
	 *   Shop type (buy, sell, outfit).
	 * @return
	 *   Shop inventory or `null` if NPC does not support shop type.
	 */
	public ShopInventory<?, ?> getInventory(final ShopType type) {
		final String name = shops.get(type);
		if (name == null) {
			return null;
		}
		if (ShopType.OUTFIT.equals(type)) {
			return OutfitShopsList.get().get(name);
		}
		return ShopsList.get().get(name, type);
	}

	/**
	 * Retrieves shop types supported by this NPC.
	 *
	 * @return
	 *   List of available shop types.
	 */
	public Set<ShopType> getShopTypes() {
		return shops.keySet();
	}
}
