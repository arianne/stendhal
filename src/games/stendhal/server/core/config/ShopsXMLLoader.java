/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config;

import static games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour.NEVER_WEARS_OFF;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.shop.ItemShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopsList;
import games.stendhal.server.entity.npc.shop.ShopInventory;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.npc.shop.ShopsList;


public class ShopsXMLLoader extends DefaultHandler {

	private final static Logger logger = Logger.getLogger(ShopsXMLLoader.class);

	private static boolean initialized = false;

	private final static ShopsList shops = ShopsList.get();
	private final static OutfitShopsList oshops = OutfitShopsList.get();

	private ShopType shopType;
	private String shopName;
	private ShopInventory inventory;
	// configures whether merchant responds to "offer"
	private Map<String, Boolean> offers;
	// terms that outfit merchants respond to
	private Map<String, String> actions;
	// outfit exiration times
	private Map<String, Integer> expirations;
	// determines if outfits are returnable
	private Map<String, Boolean> returnables;

	/** The singleton instance. */
	private static ShopsXMLLoader instance;


	/**
	 * Singleton access method.
	 *
	 * @return
	 *     The static instance.
	 */
	public static ShopsXMLLoader get() {
		if (instance == null) {
			instance = new ShopsXMLLoader();
		}

		return instance;
	}

	/**
	 * Private singleton constructor.
	 */
	private ShopsXMLLoader() {}

	public void init() {
		if (initialized) {
			logger.warn("Tried to re-initialize shops loader");
			return;
		}

		final String xml = "/data/conf/shops.xml";
		final InputStream in = getClass().getResourceAsStream(xml);

		if (in == null) {
			logger.info("Shops config (" + xml + ") not found, not loading");
			return;
		}

		try {
			load(new URI(xml));
			in.close();
			initialized = true;
		} catch (final SAXException | URISyntaxException | IOException e) {
			logger.error(e);
		}
	}

	public void load(final URI uri) throws SAXException {
		try {
			// parse the input
			final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			final InputStream is = ShopsXMLLoader.class.getResourceAsStream(uri.getPath());

			if (is == null) {
				throw new FileNotFoundException("cannot find resource '" + uri
						+ "' in classpath");
			}

			try {
				saxParser.parse(is, this);
			} finally {
				is.close();
			}
		} catch (final ParserConfigurationException e) {
			logger.error(e);
		} catch (final IOException e) {
			logger.error(e);
			throw new SAXException(e);
		}
	}

	@Override
	public void startElement(final String namespaceURI, final String lName, final String qName,
			final Attributes attrs) {
		if (qName.equals("shop")) {
			shopType = attrs.getValue("type").equals("sell") ? ShopType.ITEM_SELL : ShopType.ITEM_BUY;
			shopName = attrs.getValue("name");
			inventory = new ItemShopInventory();
			offers = new LinkedHashMap<>();
		} else if (qName.equals("outfitshop")) {
			shopType = ShopType.OUTFIT;
			shopName = attrs.getValue("name");
			inventory = new OutfitShopInventory();
			offers = new LinkedHashMap<>();
			actions = new HashMap<>();
			expirations = new HashMap<>();
			returnables = new HashMap<>();
		} else if (qName.equals("trade")) {
			shopType = ShopType.TRADE;
		}

		if (shopType == null) {
			return;
		}

		String merchant = null;
		if (qName.equals("merchant")) {
			if (!"true".equals(attrs.getValue("configure"))) {
				return;
			}
			merchant = attrs.getValue("name");
			offers.put(attrs.getValue("name"), !"false".equals(attrs.getValue("offer")));
		}

		if (isItemShop()) {
			if (qName.equals("item")) {
				((ItemShopInventory) inventory).put(attrs.getValue("name"), Integer.parseInt(attrs.getValue("price")));
			}
		} else if (isOutfitShop()) {
			if (qName.equals("outfit")) {
				((OutfitShopInventory) inventory).put(attrs.getValue("name"), attrs.getValue("layers"),
						Integer.parseInt(attrs.getValue("price")));
			} else if (merchant != null) {
				final String action = attrs.getValue("action");
				// "buy" is default
				actions.put(merchant, action != null ? action : "buy");
				int expires = NEVER_WEARS_OFF;
				final String tmp = attrs.getValue("expires");
				if (tmp != null) {
					expires = Integer.parseInt(tmp);
				}
				expirations.put(merchant, expires);
				// default is not returnable
				returnables.put(merchant, "true".equals(attrs.getValue("returnable")));
			}
		}
	}

	@Override
	public void endElement(final String namespaceURI, final String sName, final String qName) {
		if (shopName == null) {
			// cannot configure shops without identifiers
			return;
		}
		if (!qName.equals("shop") && !qName.equals("outfitshop")) {
			return;
		}

		if (isItemShop()) {
			if (shops.get(shopName, shopType) != null) {
				logger.warn("Tried to add duplicate " + shopType.toString() + "er shop \"" + shopName
						+ "\" with contents " + inventory.toString());
				return;
			}
			shops.add(shopName, shopType, (ItemShopInventory) inventory);
		} else if (isOutfitShop()) {
			if (oshops.get(shopName) != null) {
				logger.warn("Tried to add duplicate outfit shop \"" + shopName + "\" with contents "
						+ inventory.toString());
				return;
			}
			oshops.add(shopName, (OutfitShopInventory) inventory);
		}

		if (!offers.isEmpty()) {
			SingletonRepository.getCachedActionManager().register(new Runnable() {
				private final String _name = shopName;
				private final ShopType _type = shopType;
				private final Map<String, Boolean> _offers = offers;
				private final Map<String, String> _actions = actions;
				private final Map<String, Integer> _expirations = expirations;
				private final Map<String, Boolean> _returnables = returnables;

				public void run() {
					for (final Map.Entry<String, Boolean> entry: _offers.entrySet()) {
						if (ShopType.ITEM_SELL.equals(_type) || ShopType.ITEM_BUY.equals(_type)) {
							shops.configureNPC(entry.getKey(), _name, _type, entry.getValue());
						} else if (ShopType.OUTFIT.equals(_type)) {
							final String npc = entry.getKey();
							oshops.configureNPC(npc, _name, _actions.get(npc), entry.getValue(),
									_returnables.get(npc), _expirations.get(npc));
						}
					}
				}
			});
		}
	}

	private boolean isItemShop() {
		return ShopType.ITEM_SELL.equals(shopType) || ShopType.ITEM_BUY.equals(shopType);
	}

	private boolean isOutfitShop() {
		return ShopType.OUTFIT.equals(shopType);
	}
}
