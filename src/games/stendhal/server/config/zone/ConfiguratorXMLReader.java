/*
 * @(#) src/games/stendhal/server/config/zone/ConfiguratorXMLReader.java
 *
 * $Id$
 */

package games.stendhal.server.config.zone;

//
//

import org.w3c.dom.Element;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * A generic entity setup xml reader.
 */
public class ConfiguratorXMLReader extends SetupXMLReader {
	/**
	 * Logger
	 */
	private static final Logger logger = Log4J.getLogger(ConfiguratorXMLReader.class);


	//
	// ConfiguratorXMLReader
	//

	protected ConfiguratorDescriptor read(final Element element, final String className) {
		return new ConfiguratorDescriptor(className);
	}


	//
	// SetupXMLReader
	//

	public SetupDescriptor read(final Element element) {
		if(!element.hasAttribute("class-name")) {
			logger.error("Implmentation without class-name");
			return null;
		} else {
			String className = element.getAttribute("class-name");

			ConfiguratorDescriptor desc = read(element, className);

			readParameters(desc, element);

			return desc;
		}
	}
}
