/*
 * @(#) src/games/stendhal/server/entity/ShopSign.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.sign;


import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.events.ShowItemListEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.RPClass;
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

import org.apache.log4j.Logger;

/**
 * A sign for a ShopList.
 */
public class ShopSign extends Entity implements UseListener {

	/** The shop list. */
	protected ShopList shops = SingletonRepository.getShopList();

	/** Name of shop */
	protected String shopName;

	/** Caption of sign */
	protected String title;

	/** Caption to display above the table */
	private String caption;

	/** true, if this sign is for items sold by an NPC */
	private boolean seller;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Sign.class);

	public static void generateRPClass() {
		try {
			final RPClass sign = new RPClass("shop_sign");
			sign.isA("entity");
			sign.addAttribute("class", Type.STRING);
		} catch (final SyntaxException e) {
			logger.error("cannot generate RPClass", e);
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
		this.shopName = name;
		this.title = title;
		this.caption = caption;
		this.seller = seller;

		setRPClass("shop_sign");
		put("type", "shop_sign");
		setResistance(100);
	}

	/**
	 * Handles use-actions.
	 */
	public boolean onUsed(RPEntity user) {
		List<Item> itemList = generateItemList();
		ShowItemListEvent event = new ShowItemListEvent(title, caption, itemList);
		user.addEvent(event);
		return true;
	}

	/**
	 * generates the item list for this shop
	 *
	 * @return ItemList
	 */
	private List<Item> generateItemList() {
		List<Item> itemList = new LinkedList<Item>();
		Map<String, Integer> items = shops.get(shopName);
		for (Map.Entry<String, Integer> entry : items.entrySet()) {
			itemList.add(prepareItem(entry.getKey(), Integer.valueOf(entry.getValue())));
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
		Item item = SingletonRepository.getEntityManager().getItem(name);
		if (seller) {
			item.put("price", -price);
		} else {
			item.put("price", price);
		}
		item.put("description", item.describe());
		return item;
	}
}
