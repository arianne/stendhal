/*
 * @(#) src/games/stendhal/server/config/ZoneGroupsXMLLoader.java
 *
 * $Id$
 */

package games.stendhal.server.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Load and configure zones via an XML configuration file.
 */
public class ZoneGroupsXMLLoader extends DefaultHandler {

	/** Logger */
	private static final Logger logger = Logger.getLogger(ZoneGroupsXMLLoader.class);

	/** The main zone configuration file. */
	protected URI uri;

	/**
	 * Create an xml based loader of zone groups.
	 * 
	 * @param uri
	 *            The location of the configuration file.
	 */
	public ZoneGroupsXMLLoader(URI uri) {
		this.uri = uri;
	}

	/**
	 * Load zones into a world.
	 * 
	 * @throws SAXException
	 *             If a SAX error occurred.
	 * @throws IOException
	 *             If an I/O error occurred.
	 * @throws FileNotFoundException
	 *             If the resource was not found.
	 */
	public void load() throws SAXException, IOException {
		GroupsXMLLoader groupsLoader = new GroupsXMLLoader(uri);
		List<URI> zoneGroups = groupsLoader.load();

		/*
		 * Load each group
		 */
		for (URI tempUri : zoneGroups) {
			logger.debug("Loading zone group [" + tempUri + "]");

			ZonesXMLLoader loader = new ZonesXMLLoader(tempUri);

			try {
				loader.load();
			} catch (SAXException ex) {
				logger.error("Error loading zone group: " + tempUri, ex);
			} catch (IOException ex) {
				logger.error("Error loading zone group: " + tempUri, ex);
			}
		}
	}
}