/*
 * @(#) src/games/stendhal/server/config/ZoneGroupsXMLLoader.java
 *
 * $Id$
 */

package games.stendhal.server.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Load and configure zones via an XML configuration file.
 */
public class ZoneGroupsXMLLoader extends DefaultHandler {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(ZoneGroupsXMLLoader.class);

	/**
	 * The main zone configuration file.
	 */
	protected URI uri;

	/**
	 * A list of zone group files.
	 */
	protected LinkedList<URI> zoneGroups;

	/**
	 * Create an xml based loader of zone groups.
	 *
	 * @param	uri		The location of the configuration file.
	 */
	public ZoneGroupsXMLLoader(URI uri) {
		this.uri = uri;
	}

	//
	// ZoneGroupsXMLLoader
	//

	/**
	 * Load zones into a world.
	 *
	 * @throws	SAXException	If a SAX error occurred.
	 * @throws	IOException	If an I/O error occurred.
	 * @throws	FileNotFoundException
	 *				If the resource was not found.
	 */
	public void load() throws SAXException, IOException {
		InputStream in = getClass().getResourceAsStream(uri.getPath());

		if (in == null) {
			throw new FileNotFoundException("Cannot find resource: " + uri.getPath());
		}

		try {
			load(in);
		} finally {
			in.close();
		}
	}

	/**
	 * Load zones into a world using a config file.
	 *
	 * @param	world		The world to load into.
	 * @param	in		The config file stream.
	 *
	 * @throws	SAXException	If a SAX error occurred.
	 * @throws	IOException	If an I/O error occurred.
	 */
	protected void load(InputStream in) throws SAXException, IOException {
		SAXParser saxParser;
		ZonesXMLLoader loader;

		/*
		 * Use the default (non-validating) parser
		 */
		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException ex) {
			throw new SAXException(ex);
		}

		/*
		 * Parse the XML
		 */
		zoneGroups = new LinkedList<URI>();
		saxParser.parse(in, this);

		/*
		 * Load each group
		 */
		for (URI tempUri : zoneGroups) {
			logger.debug("Loading zone group [" + tempUri + "]");

			loader = new ZonesXMLLoader(tempUri);

			try {
				loader.load();
			} catch (SAXException ex) {
				logger.error("Error loading zone group: " + tempUri, ex);
			} catch (IOException ex) {
				logger.error("Error loading zone group: " + tempUri, ex);
			}
		}
	}

	//
	// ContentHandler
	//

	@Override
	public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) {
		if (qName.equals("groups")) {
			// Ignore
		} else if (qName.equals("group")) {
			String s = attrs.getValue("uri");
			if (s == null) {
				logger.warn("group without 'uri'");
			} else {
				try {
					zoneGroups.add(uri.resolve(s));
				} catch (IllegalArgumentException ex) {
					logger.error("Invalid group reference: " + s + " [" + ex.getMessage() + "]");
				}

			}
		} else {
			logger.warn("Unknown XML element: " + qName);
		}
	}

}