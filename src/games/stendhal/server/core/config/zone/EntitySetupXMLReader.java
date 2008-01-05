/*
 * @(#) src/games/stendhal/server/config/zone/EntitySetupXMLReader.java
 *
 * $Id$
 */

package games.stendhal.server.core.config.zone;

//
//

import games.stendhal.server.core.config.XMLUtil;

import java.util.List;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

/**
 * A generic entity setup xml reader.
 */
public class EntitySetupXMLReader extends SetupXMLReader {
	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(EntitySetupXMLReader.class);

	//
	// EntitySetupXMLReader
	//

	/**
	 * Create an entity setup descriptor.
	 * 
	 * @param element
	 *            The entity setup XML element.
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 * 
	 * @return An entity setup descriptor.
	 */
	protected EntitySetupDescriptor read(final Element element, final int x,
			final int y) {
		return new EntitySetupDescriptor(x, y);
	}

	/**
	 * Read attributes from an XML element.
	 * 
	 * @param desc
	 *            The descriptor to load.
	 * @param element
	 *            The XML element.
	 */
	protected void readAttributes(final EntitySetupDescriptor desc,
			final Element element) {
		List<Element> list = XMLUtil.getElements(element, "attribute");

		for (Element attr : list) {
			if (!attr.hasAttribute("name")) {
				logger.error("Unnamed attribute");
			} else {
				desc.setAttribute(attr.getAttribute("name"), XMLUtil.getText(
						attr).trim());
			}
		}
	}

	/**
	 * Read implementation information from an XML element.
	 * 
	 * @param desc
	 *            The descriptor to load.
	 * @param element
	 *            The XML element.
	 */
	protected void readImplementation(final EntitySetupDescriptor desc,
			final Element element) {
		if (!element.hasAttribute("class-name")) {
			logger.error("Implmentation without class-name");
		} else {
			desc.setImplementation(element.getAttribute("class-name"));
		}

		readParameters(desc, element);
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
		int x;
		int y;

		if (!element.hasAttribute("x")) {
			logger.error("Entity without 'x' coordinate");
			return null;
		} else {
			String s = element.getAttribute("x");

			try {
				x = Integer.parseInt(s);
			} catch (NumberFormatException ex) {
				logger.error("Invalid entity 'x' coordinate: " + s);
				return null;
			}
		}

		if (!element.hasAttribute("y")) {
			logger.error("Entity without 'y' coordinate");
			return null;
		} else {
			String s = element.getAttribute("y");

			try {
				y = Integer.parseInt(s);
			} catch (NumberFormatException ex) {
				logger.error("Invalid entity 'y' coordinate: " + s);
				return null;
			}
		}

		EntitySetupDescriptor desc = read(element, x, y);

		List<Element> list = XMLUtil.getElements(element, "implementation");

		if (!list.isEmpty()) {
			if (list.size() > 1) {
				logger.warn("More than one implementation specified");
			}

			readImplementation(desc, list.get(0));
		}

		readAttributes(desc, element);

		return desc;
	}
}
