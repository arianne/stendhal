/*
 * @(#) src/games/stendhal/server/config/ZoneGroupsXMLLoader.java
 *
 * $Id$
 */

package games.stendhal.server.config;

//
//

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.server.game.RPWorld;

import games.stendhal.server.StendhalRPWorld;

/**
 * Load and configure zones via an XML configuration file.
 */
public class ZoneGroupsXMLLoader extends DefaultHandler {

	/**
	 * Logger
	 */
	private static final Logger logger = Log4J.getLogger(ZoneGroupsXMLLoader.class);

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
	 * @param	world		The world to load into.
	 *
	 * @throws	SAXException	If a SAX error occured.
	 * @throws	IOException	If an I/O error occured.
	 * @throws	FileNotFoundException
	 *				If the resource was not found.
	 */
	public void load(RPWorld world) throws SAXException, IOException {
		InputStream in = getClass().getResourceAsStream(uri.getPath());

		if (in == null) {
			throw new FileNotFoundException("Cannot find resource: " + uri.getPath());
		}

		try {
			load(world, in);
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
	 * @throws	SAXException	If a SAX error occured.
	 * @throws	IOException	If an I/O error occured.
	 */
	protected void load(RPWorld world, InputStream in) throws SAXException, IOException {
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
		for (URI uri : zoneGroups) {
			logger.debug("Loading zone group [" + uri + "]");

			loader = new ZonesXMLLoader(uri);

			try {
				loader.load(world);
			} catch (SAXException ex) {
				logger.error("Error loading zone group: " + uri, ex);
			} catch (IOException ex) {
				logger.error("Error loading zone group: " + uri, ex);
			}
		}
	}

	//
	// ContentHandler
	//

	@Override
	public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) {
		String s;

		if (qName.equals("zone-groups")) {
			// Ignore
		} else if (qName.equals("zone-group")) {
			if ((s = attrs.getValue("uri")) == null) {
				logger.warn("Zone group without 'uri'");
			} else {
				try {
					zoneGroups.add(uri.resolve(s));
				} catch (IllegalArgumentException ex) {
					logger.error("Invalid zone group reference: " + s + " [" + ex.getMessage() + "]");
				}

			}
		} else {
			logger.warn("Unknown XML element: " + qName);
		}
	}

	//
	//

	/*
	 * XXX - THIS REQUIRES StendhalRPWorld SETUP (i.e. marauroa.ini)
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: java " + ZoneGroupsXMLLoader.class.getName() + " <filename>");
			System.exit(1);
		}

		ZoneGroupsXMLLoader loader = new ZoneGroupsXMLLoader(URI.create(args[0]));

		try {
			loader.load(StendhalRPWorld.get());
		} catch (org.xml.sax.SAXParseException ex) {
			System.err.print("Source " + args[0] + ":" + ex.getLineNumber() + "<" + ex.getColumnNumber() + ">");

			throw ex;
		}
	}
}
