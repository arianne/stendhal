/*
 * @(#) src/games/stendhal/server/config/zone/SetupXMLReader.java
 *
 * $Id$
 */

package games.stendhal.server.core.config.zone;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

//
//

import games.stendhal.server.core.config.XMLUtil;

/**
 * A base setup xml reader.
 */
public abstract class SetupXMLReader {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SetupXMLReader.class);

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
	public abstract SetupDescriptor read(final Element element);

	/**
	 * Read paramaters from an XML element.
	 *
	 * @param desc
	 *            The descriptor to load.
	 * @param element
	 *            The XML element.
	 */
	protected void readParameters(final SetupDescriptor desc, final Element element) {
		final List<Element> list = XMLUtil.getElements(element, "parameter");

		for (final Element param : list) {

			if (!XMLUtil.checkCondition(param.getAttribute("condition"))) {
				continue;
			}

			if (param.hasAttribute("name")) {
				desc.setParameter(param.getAttribute("name"), XMLUtil.getText(
						param).trim());
			} else {
				LOGGER.error("Unnamed parameter");
			}
		}
	}
}
