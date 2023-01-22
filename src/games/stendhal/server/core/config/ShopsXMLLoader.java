/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
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
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;


public class ShopsXMLLoader extends DefaultHandler {

	private final static Logger logger = Logger.getLogger(ShopsXMLLoader.class);

	/** The singleton instance. */
	private static ShopsXMLLoader instance;

	private final static ShopList shops = ShopList.get();

	private String shopName;
	private Map<String, Integer> items;
	private Boolean seller;
	private String npcNames;


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
		final String xml = "/data/conf/shops.xml";
		final InputStream in = getClass().getResourceAsStream(xml);

		if (in == null) {
			logger.info("Shops config (" + xml + ") not found, not loading");
			return;
		}

		try {
			load(new URI(xml));
			in.close();
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
			items = new LinkedHashMap<>();

			shopName = attrs.getValue("name");
			final String shopType = attrs.getValue("type");
			seller = shopType.equals("sell") ? true : shopType.equals("buy") ? false : null;
			npcNames = attrs.getValue("npcs");
		} else if (qName.equals("item")) {
			items.put(attrs.getValue("name"), Integer.parseInt(attrs.getValue("price")));
		}
	}

	@Override
	public void endElement(final String namespaceURI, final String sName, final String qName) {
		if (qName.equals("shop")) {
			if (seller != null && npcNames != null) {
				if (shops.get(seller, shopName) != null) {
					logger.warn("Tried to add duplicate shop \"" + shopName + "\" with contents " + items.toString());
					return;
				}

				for (final Entry<String, Integer> e: items.entrySet()) {
					shops.add(seller, shopName, e.getKey(), e.getValue());
				}

				final String shopNameCopy = shopName;
				final String npcNamesCopy = npcNames;
				final String stype = seller ? "sell" : "buy";
				SingletonRepository.getCachedActionManager().register(new Runnable() {
					public void run() {
						final NPCList npcs = NPCList.get();
						for (final String npcName: npcNamesCopy.split(",")) {
							final SpeakerNPC npc = npcs.get(npcName);
							if (npc == null) {
								logger.error("Cannot create " + stype + "er shop for non-existing NPC " + npcName);
								continue;
							}

							logger.info("Adding " + stype + "er shop \"" + shopNameCopy + "\" to NPC " + npcName);
							if (stype.equals("sell")) {
								new SellerAdder().addSeller(npc, new SellerBehaviour(shops.getSeller(shopNameCopy)));
							} else {
								new BuyerAdder().addBuyer(npc, new BuyerBehaviour(shops.getBuyer(shopNameCopy)));
							}
						}
					}
				});
			} else {
				for (final Entry<String, Integer> e: items.entrySet()) {
					shops.add(shopName, e.getKey(), e.getValue());
				}
			}
		}
	}
}
