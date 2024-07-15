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
package games.stendhal.server.events;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.npc.MerchantNPC;
import games.stendhal.server.entity.npc.shop.ShopInventory;
import games.stendhal.server.entity.npc.shop.ShopType;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;


/**
 * Event that retrieves an NPC's shop inventory.
 */
public class ShopInventoryEvent extends RPEvent {

	private static Logger logger = Logger.getLogger(ShopInventoryEvent.class);


	public static void generateRPClass() {
		final RPClass rpclass = new RPClass("shop_inventory");
		rpclass.addAttribute("type", Type.STRING);
		rpclass.addAttribute("contents", Type.STRING);
	}

	public ShopInventoryEvent(final MerchantNPC npc, final ShopType type) {
		final ShopInventory<?, ?> inv = npc.getInventory(type);
		if (inv == null) {
			logger.warn("Shop type " + type.toString() + " does not exist for NPC " + npc.getName());
			return;
		}
		put("type", type.toString());
		put("contents", buildInventoryList(inv));
	}

	private String buildInventoryList(final ShopInventory<?, ?> inv) {
		return inv.toString().split("(")[1].split(")")[0];
	}
}
