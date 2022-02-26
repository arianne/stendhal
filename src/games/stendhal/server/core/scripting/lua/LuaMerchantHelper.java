/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import java.util.LinkedHashMap;
import java.util.Map;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.scripting.ScriptInLua.LuaLogger;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;


/**
 * Exposes merchant handling classes & functions to Lua.
 */
public class LuaMerchantHelper {

	private static LuaLogger logger = LuaLogger.get();

	/** The singleton instance. */
	private static LuaMerchantHelper instance;

	private final static EntityManager eManager = SingletonRepository.getEntityManager();

	public static ShopList shops = ShopList.get();


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static MerchantHelper instance.
	 */
	public static LuaMerchantHelper get() {
		if (instance == null) {
			instance = new LuaMerchantHelper();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaMerchantHelper() {
		// singleton
	}

	/**
	 * Adds merchant behavior to a SpeakerNPC.
	 *
	 * FIXME: LuaTable not working for "prices" object
	 *
	 * @param merchantType
	 * 		If set to "buyer", will add buyer behavior, otherwise will be "seller".
	 * @param npc
	 * 		The SpeakerNPC to add the behavior to.
	 * @param prices
	 * 		List of items & their prices (can be instance of either Map<String, Int> or LuaTable).
	 * @param addOffer
	 * 		If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).
	 */
	@SuppressWarnings("unchecked")
	public void add(final String merchantType, final SpeakerNPC npc, final Object prices, Boolean addOffer) {
		// default is to add an "offer" response
		if (addOffer == null) {
			addOffer = true;
		}

		Map<String, Integer> priceList = null;
		if (prices instanceof LuaTable) {
			priceList = new LinkedHashMap<>();
			final LuaTable priceTable = (LuaTable) prices;
			for (final LuaValue key: priceTable.keys()) {
				String itemName = key.tojstring();
				final int itemPrice = priceTable.get(key).toint();

				// special handling of underscore characters in item names
				if (itemName.contains("_")) {
					// check if item is real item
					if (!eManager.isItem(itemName)) {
						itemName = itemName.replace("_", " ");
					}
				}

				priceList.put(itemName, itemPrice);
			}
		} else if (prices instanceof Map<?, ?>) {
			priceList = (LinkedHashMap<String, Integer>) prices;
		}

		if (priceList == null) {
			logger.error("Invalid price list type: must be LuaTable or Map<String, Integer>");
			return;
		}

		//final MerchantBehaviour behaviour;
		if (merchantType != null && merchantType.equals("buyer")) {
			new BuyerAdder().addBuyer(npc, new BuyerBehaviour(priceList), addOffer);
		} else {
			new SellerAdder().addSeller(npc, new SellerBehaviour(priceList), addOffer);
		}
	}

	/**
	 * Adds merchant seller behavior to a SpeakerNPC.
	 *
	 * @param npc
	 * 		The SpeakerNPC to add the behavior to.
	 * @param prices
	 * 		List of items & their prices (can be instance of either Map<String, Int> or LuaTable).
	 * @param addOffer
	 * 		If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).
	 */
	public void addSeller(final SpeakerNPC npc, final Object prices, final boolean addOffer) {
		add("seller", npc, prices, addOffer);
	}

	/**
	 * Adds merchant buyer behavior to a SpeakerNPC.
	 *
	 * @param npc
	 * 		The SpeakerNPC to add the behavior to.
	 * @param prices
	 * 		List of items & their prices (can be instance of either Map<String, Int> or LuaTable).
	 * @param addOffer
	 * 		If <code>true</code>, will add default replies for "offer" (default: <code>true</code>).
	 */
	public void addBuyer(final SpeakerNPC npc, final Object prices, final boolean addOffer) {
		add("buyer", npc, prices, addOffer);
	}
}
