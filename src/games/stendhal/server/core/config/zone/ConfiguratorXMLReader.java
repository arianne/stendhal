/*
 * @(#) src/games/stendhal/server/config/zone/ConfiguratorXMLReader.java
 *
 * $Id$
 */
/***************************************************************************
 *                 Copyright © 2007-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config.zone;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * A generic entity setup xml reader.
 */
public class ConfiguratorXMLReader extends SetupXMLReader {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ConfiguratorXMLReader.class);

	//
	// ConfiguratorXMLReader
	//

	/**
	 * Create a configurator descriptor.
	 *
	 * @param element
	 *            The configurator XML element.
	 * @param className
	 *            The handler class name.
	 *
	 * @return A configurator.
	 */
	protected ConfiguratorDescriptor read(final Element element,
			final String className) {
		return new ConfiguratorDescriptor(className);
	}

	//
	// SetupXMLReader
	//

	/**
	 * Create a setup descriptor from XML data.
	 *
	 * @param element
	 *            The descriptor XML element.
	 *
	 * @return A setup descriptor, or <code>null</code> if invalid.
	 */
	@Override
	public SetupDescriptor read(final Element element) {
		if (element.hasAttribute("class-name")) {
			final String className = element.getAttribute("class-name");

			final ConfiguratorDescriptor desc = read(element, className);

			readParameters(desc, element);

			return desc;
		} else {
			LOGGER.error("Implmentation without class-name");
			return null;
		}
	}
}
