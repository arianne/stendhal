/***************************************************************************
 *                    Copyright © 2003-2024 - Arianne                      *
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.config.ShopGroupsXMLLoader.MerchantConfigurator;
import games.stendhal.server.entity.npc.shop.ItemShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopInventory;
import games.stendhal.server.entity.npc.shop.ShopInventory;
import games.stendhal.server.entity.npc.shop.ShopType;


public class ShopsXMLLoader extends DefaultHandler {

	private final static Logger logger = Logger.getLogger(ShopsXMLLoader.class);

	private Map<ShopType, Map<String, ShopInventory<?, ?>>> inventories;
	private List<MerchantConfigurator> configurators;

	private ShopType currentType;
	private String currentName;
	private ShopInventory<?, ?> currentInventory;
	private String currentItem;



	public void load(final URI uri) throws SAXException {
		try {
			// reset data
			inventories = new HashMap<>();
			configurators = new ArrayList<>();

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
			final String tmp = attrs.getValue("type");
			if (tmp != null) {
				currentType = ShopType.fromString(tmp);
			}
			currentName = attrs.getValue("name");
		} else if (qName.equals("item") && (isItemShop() || isTradeShop())) {
			if (currentInventory == null) {
				currentInventory = new ItemShopInventory(currentType, currentName);
			}
			currentItem = attrs.getValue("name");
			if (isItemShop()) {
				final String iprice = attrs.getValue("price");
				if (iprice == null) {
					logger.error("Cannot add item \"" + currentItem + "\" to shop without price");
				} else {
					((ItemShopInventory) currentInventory).put(currentItem, Integer.parseInt(iprice));
				}
			}
		} else if (qName.equals("outfit") && isOutfitShop()) {
			if (currentInventory == null) {
				currentInventory = new OutfitShopInventory(currentType, currentName);
			}
			currentItem = attrs.getValue("name");
			((OutfitShopInventory) currentInventory).put(currentItem, attrs.getValue("layers"),
					Integer.parseInt(attrs.getValue("price")));
		} else if (qName.equals("for")) {
			currentInventory.addTradeFor(currentItem, attrs.getValue("name"),
					Integer.valueOf(attrs.getValue("count")));
		} else if (qName.equals("merchant")) {
			final MerchantConfigurator mc = new MerchantConfigurator();
			mc.npc = attrs.getValue("name");
			mc.id = currentName;
			mc.stype = currentType;
			final String flags = attrs.getValue("flags");
			if (flags != null) {
				mc.flags = Arrays.asList(flags.split(","));
			}
			final String factor = attrs.getValue("factor");
			if (factor != null) {
				mc.factor = (float) MathHelper.parseDoubleDefault(factor, 1.0);
			}
			mc.action = attrs.getValue("action");
			if (mc.action == null) {
				// default action
				mc.action = "buy";
			}
			final String expiration = attrs.getValue("expiration");
			if (expiration != null) {
				mc.expiration = Integer.parseInt(expiration);
			}
			mc.wearOffMessage = attrs.getValue("wearOffMessage");
			configurators.add(mc);
			if (currentInventory != null) {
				currentInventory.addMerchantConfigurator(mc);
			}
		}
	}

	@Override
	public void endElement(final String namespaceURI, final String sName, final String qName) {
		if (!qName.equals("shop")) {
			return;
		}

		if (!inventories.containsKey(currentType)) {
			inventories.put(currentType, new HashMap<String, ShopInventory<?, ?>>());
		}
		inventories.get(currentType).put(currentName, currentInventory);

		currentType = null;
		currentName = null;
		currentInventory = null;
		currentItem = null;
	}

	private boolean isItemShop() {
		return ShopType.ITEM_SELL.equals(currentType) || ShopType.ITEM_BUY.equals(currentType);
	}

	private boolean isTradeShop() {
		return ShopType.TRADE.equals(currentType);
	}

	private boolean isOutfitShop() {
		return ShopType.OUTFIT.equals(currentType);
	}

	public Map<ShopType, Map<String, ShopInventory<?, ?>>> getInventories() {
		return inventories;
	}

	public List<MerchantConfigurator> getConfigurators() {
		return configurators;
	}
}
