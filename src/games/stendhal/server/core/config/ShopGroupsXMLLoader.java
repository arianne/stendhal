/***************************************************************************
 *                   (C) Copyright 2023-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.shop.ItemShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopsList;
import games.stendhal.server.entity.npc.shop.ShopInventory;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.npc.shop.ShopsList;


public class ShopGroupsXMLLoader extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(ShopGroupsXMLLoader.class);

	private static ShopsList shops = SingletonRepository.getShopsList();
	private static OutfitShopsList oshops = SingletonRepository.getOutfitShopsList();

	private static boolean loaded = false;
	protected final URI uri;


	public ShopGroupsXMLLoader(final URI uri) {
		this.uri = uri;
	}

	public ShopGroupsXMLLoader(final String path) {
		this(URI.create(path));
	}

	/**
	 * Loads shops from XML and configures NPC merchants.
	 */
	public void load() {
		if (loaded) {
			logger.warn("Tried to re-load shops from XML");
			return;
		}
		loaded = true;
		loadInternal(null);
	}

	/**
	 * Loads shops from XML and configures specific NPC merchants (used for testing).
	 *
	 * @param npcNames
	 *   List of NPC names.
	 */
	public void load(final String... npcNames) {
		loadInternal(Arrays.asList(npcNames));
	}

	/**
	 * Loads shops from XML and configures NPC merchants.
	 *
	 * @param npcNames
	 *   List of NPC names (if `null` will configure all parsed from XML).
	 */
	private void loadInternal(final List<String> npcNames) {
		try {
			final List<URI> groups = new GroupsXMLLoader(uri).load();
			final ShopsXMLLoader shopsLoader = new ShopsXMLLoader();
			final List<MerchantConfigurator> configurators = new ArrayList<>();
			for (final URI groupUri: groups) {
				shopsLoader.load(groupUri);
				addShops(shopsLoader.getInventories());
				for (final MerchantConfigurator mc: shopsLoader.getConfigurators()) {
					if (npcNames != null) {
						if (npcNames.contains(mc.npc)) {
							mc.configure();
						}
						// loading was called for specific NPCs only
						continue;
					}
					// don't configure NPCs for unnamed shops or that aren't explicitly declared for configure
					if (mc.id != null && mc.flags != null && mc.flags.contains("configure")) {
						configurators.add(mc);
					}
				}
			}

			if (configurators.size() == 0) {
				// don't cache runner if no merchants are to be configured at startup
				return;
			}

			SingletonRepository.getCachedActionManager().register(new Runnable() {
				private final List<MerchantConfigurator> _configurators = configurators;
				@Override
				public void run() {
					for (final MerchantConfigurator mc: _configurators) {
						mc.configure();
					}
				}
			});
		} catch (final SAXException e) {
			logger.error(e);
		} catch (final IOException e) {
			logger.error(e);
		}
	}

	private void addShops(final Map<ShopType, Map<String, ShopInventory<?, ?>>> inventories) {
		for (final ShopType stype: inventories.keySet()) {
			for (final Map.Entry<String, ShopInventory<?, ?>> sentry: inventories.get(stype).entrySet()) {
				final String id = sentry.getKey();
				final ShopInventory<?, ?> inv = sentry.getValue();
				if (ShopType.ITEM_SELL.equals(stype) || ShopType.ITEM_BUY.equals(stype) || ShopType.TRADE.equals(stype)) {
					shops.add(id, stype, (ItemShopInventory) inv);
				} else if (ShopType.OUTFIT.equals(stype)) {
					oshops.add(id, (OutfitShopInventory) inv);
				} else {
					logger.debug("Unknown shop type for ID: " + id);
				}
			}
		}
	}


	public static class MerchantConfigurator {
		public String npc;
		public String id;
		public ShopType stype;
		public List<String> flags;
		public Float factor; // skews prices for this merchant
		// outfit shop exclusive
		public String action;
		public Integer expiration;
		public String wearOffMessage;

		public void configure() {
			if (stype == null) {
				return;
			}
			if (ShopType.OUTFIT.equals(stype)) {
				configureOutfitShop();
			} else {
				configureItemShop();
			}
		}

		private void configureItemShop() {
			shops.configureNPC(npc, id, stype, factor, flags == null || !flags.contains("noOffer"));
		}

		private void configureOutfitShop() {
			oshops.configureNPC(npc, id, factor, action,
					expiration != null ? expiration : OutfitChangerBehaviour.NEVER_WEARS_OFF,
					wearOffMessage, flags);
		}
	}
}
