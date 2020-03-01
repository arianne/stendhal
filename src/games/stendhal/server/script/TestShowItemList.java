/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.ItemInformation;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowItemListEvent;

/**
 * Creates a portable NPC who gives ALL players powerful items, increases their
 * level and makes them admins. This is used on test-systems only. Therefore it
 * is disabled in default install and you have to use this parameter:
 * -Dstendhal.testserver=junk as a vm argument.
 *
 * As admin uses /script AdminMaker.class to summon her right next to him/her.
 * Please unload it with /script -unload AdminMaker.class
 */

public class TestShowItemList extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		List<Item> itemList = new LinkedList<Item>();

		if (args.isEmpty()) {
			itemList.add(prepareItem("club", 100));
			itemList.add(prepareItem("leather armor", -100));
			itemList.add(prepareItem("ice sword", -10000));
		} else {
			final String shopName = args.get(0);
			ShopList shops = SingletonRepository.getShopList();
			Map<String, Integer> items = shops.get(shopName);

			if (items == null) {
				admin.sendPrivateText(NotificationType.ERROR, "Shop \"" + shopName + "\" not found");
				return;
			}

			for (Map.Entry<String, Integer> entry : items.entrySet()) {
				itemList.add(prepareItem(entry.getKey(), Integer.valueOf(entry.getValue())));
			}
		}

		ShowItemListEvent event = new ShowItemListEvent("Aramyk Shop",
				"Please talk to Aramyk to buy or sell items.",
				itemList);
		admin.addEvent(event);
		admin.notifyWorldAboutChanges();
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
		item.put("price", -price);
		item.put("description_info", item.describe());
		// compatibility with 0.85 clients
		item.put("description", item.describe());
		return item;
	}
}
