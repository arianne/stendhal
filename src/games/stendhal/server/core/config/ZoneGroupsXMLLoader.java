/*
 * @(#) src/games/stendhal/server/config/ZoneGroupsXMLLoader.java
 *
 * $Id$
 */

package games.stendhal.server.core.config;

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


	private static final Logger LOGGER = Logger.getLogger(ZoneGroupsXMLLoader.class);

	/** The main zone configuration file. */
	protected URI uri;

	/**
	 * Create an xml based loader of zone groups.
	 * 
	 * @param uri
	 *            The location of the configuration file.
	 */
	public ZoneGroupsXMLLoader(final URI uri) {
		this.uri = uri;
	}

	/**
	 * Load zones into a world.
	 * 
	 * @throws SAXException
	 *             If a SAX error occurred.
	 * @throws IOException
	 *             If an I/O error occurred.
	 */
	public void load() throws SAXException, IOException {
		final GroupsXMLLoader groupsLoader = new GroupsXMLLoader(uri);
		final List<URI> zoneGroups = groupsLoader.load();

		// just to speed up starting of the server in while developing
		// add -Dstendhal.zone.regex=".*semos.*" (for example) to your server start script just after the "java "
		// or for multiple regions: -Dstendhal.zone.regex="*semos.*|.*fado.*"
		final String regex = System.getProperty("stendhal.zone.regex", ".*");

		// Load each group
		for (final URI tempUri : zoneGroups) {
			if (tempUri.toString().matches(regex)) {
				LOGGER.debug("Loading zone group [" + tempUri + "]");
	
				final ZonesXMLLoader loader = new ZonesXMLLoader(tempUri);
	
				try {
					loader.load();
				} catch (final SAXException ex) {
					LOGGER.error("Error loading zone group: " + tempUri, ex);
				} catch (final IOException ex) {
					LOGGER.error("Error loading zone group: " + tempUri, ex);
				}
			}
		}
	}
}
