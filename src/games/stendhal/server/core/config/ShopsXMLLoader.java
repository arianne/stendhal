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

import games.stendhal.server.entity.npc.ShopList;


public class ShopsXMLLoader extends DefaultHandler {

	private final static Logger logger = Logger.getLogger(ShopsXMLLoader.class);

	/** The singleton instance. */
	private static ShopsXMLLoader instance;

	private final static ShopList shops = ShopList.get();

	private String shopName;
	private Map<String, Integer> items;
	//private boolean seller = true;


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
			//seller = attrs.getValue("type").equals("sells");
		} else if (qName.equals("item")) {
			items.put(attrs.getValue("name"), Integer.parseInt(attrs.getValue("price")));
		}
	}

	@Override
	public void endElement(final String namespaceURI, final String sName, final String qName) {
		if (qName.equals("shop")) {
			for (final Entry<String, Integer> e: items.entrySet()) {
				shops.add(shopName, e.getKey(), e.getValue());
			}
		}
	}
}
