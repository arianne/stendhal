/*
 * @(#) src/games/stendhal/server/config/zone/PortalSetupXMLReader.java
 *
 * $Id$
 */

package games.stendhal.server.config.zone;

//
//

import games.stendhal.server.config.XMLUtil;

import java.util.List;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

/**
 * A portal entity setup xml reader.
 */
public class PortalSetupXMLReader extends EntitySetupXMLReader {
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(PortalSetupXMLReader.class);

	//
	// PortalSetupXMLReader
	//

	/**
	 * Create a portal setup descriptor.
	 * 
	 * @param element
	 *            The entity setup XML element.
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 * @param identifier
	 *            The portal identifier.
	 * 
	 * @return A portal setup descriptor.
	 */
	protected PortalSetupDescriptor read(final Element element, final int x,
			final int y, final Object identifier) {
		return new PortalSetupDescriptor(x, y, identifier);
	}

	/**
	 * Read destination information from an XML element.
	 * 
	 * @param desc
	 *            The descriptor to load.
	 * @param element
	 *            The XML element.
	 */
	protected void readDestination(final PortalSetupDescriptor desc,
			final Element element) {
		String zone;
		Object identifier;

		if (!element.hasAttribute("zone")) {
			logger.error("Portal destination without zone");
			return;
		} else {
			zone = element.getAttribute("zone");
		}

		if (!element.hasAttribute("ref")) {
			logger.error("Portal destination without 'ref' value");
			return;
		} else {
			String s = element.getAttribute("ref");

			/*
			 * For now, treat valid number strings as Integer refs
			 */
			try {
				identifier = new Integer(s);
			} catch (NumberFormatException ex) {
				identifier = s;
			}
		}

		desc.setDestination(zone, identifier);
	}

	//
	// EntitySetupXMLReader
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
	protected EntitySetupDescriptor read(final Element element, final int x,
			final int y) {
		Object identifier;

		if (!element.hasAttribute("ref")) {
			logger.warn("Portal without 'ref' value");
			return null;
		} else {
			String s = element.getAttribute("ref");

			/*
			 * For now, treat valid number strings as Integer refs
			 */
			try {
				identifier = new Integer(s);
			} catch (NumberFormatException ex) {
				identifier = s;
			}
		}

		PortalSetupDescriptor desc = read(element, x, y, identifier);

		/*
		 * Destination
		 */
		List<Element> list = XMLUtil.getElements(element, "destination");

		if (!list.isEmpty()) {
			if (list.size() > 1) {
				logger.warn("More than one destination specified");
			}

			readDestination(desc, list.get(0));
		}

		/*
		 * Replacing
		 */
		if (element.hasAttribute("replacing")) {
			String s = element.getAttribute("replacing");

			if (s.equals("true")) {
				desc.setReplacing(true);
			} else if (s.equals("false")) {
				desc.setReplacing(false);
			} else {
				logger.error("Invalid 'replacing' value: " + s);
			}
		}

		return desc;
	}
}
