/*
 * @(#) src/games/stendhal/server/config/XMLUtil.java
 *
 * $Id$
 */

package games.stendhal.server.core.config;

//
//

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML utility methods for DOM reading.
 */
public class XMLUtil {
	//
	// XMLUtil
	//

	/**
	 * Get all the direct children elements of an element.
	 *
	 * @param parent
	 *            The parent element.
	 *
	 * @return A list of Element's.
	 */
	public static List<Element> getElements(final Element parent) {
		final LinkedList<Element> list = new LinkedList<Element>();

		Node node = parent.getFirstChild();

		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				list.add((Element) node);
			}

			node = node.getNextSibling();
		}

		return list;
	}

	/**
	 * Get all the direct children elements of an element that have a specific
	 * tag name.
	 *
	 * @param parent
	 *            The parent element.
	 * @param name
	 *            The tag name to match.
	 *
	 * @return A list of Element's.
	 */
	public static List<Element> getElements(final Element parent,
			final String name) {
		final LinkedList<Element> list = new LinkedList<Element>();

		Node node = parent.getFirstChild();

		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				final Element element = (Element) node;

				if (element.getTagName().equals(name)) {
					list.add(element);
				}
			}

			node = node.getNextSibling();
		}

		return list;
	}

	/**
	 * Get the direct text content of an element.
	 *
	 * @param element
	 *            The element.
	 *
	 * @return The contained text.
	 */
	public static String getText(final Element element) {
		final StringBuilder sbuf = new StringBuilder();

		getText(element, sbuf, false);

		return sbuf.toString();
	}

	/**
	 * Get the text content of an element.
	 *
	 * @param element
	 *            The element.
	 * @param sbuf
	 *            The buffer to append to.
	 * @param decend
	 *            Whether to descend into child elements.
	 */
	public static void getText(final Element element, final StringBuilder sbuf,
			final boolean decend) {
		Node node = element.getFirstChild();

		while (node != null) {
			switch (node.getNodeType()) {
			case Node.TEXT_NODE:
				sbuf.append(node.getNodeValue());
				break;

			case Node.ELEMENT_NODE:
				if (decend) {
					getText((Element) node, sbuf, decend);
				}

				break;
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Parse an XML document.
	 *
	 * @param in
	 *            The input stream.
	 *
	 * @return A Document.
	 *
	 * @throws SAXException
	 *             If there is a parsing error.
	 * @throws IOException
	 *             If there is an I/O error.
	 * @throws IllegalArgumentException
	 *             If there is a parser configuration error.
	 */
	public static Document parse(final InputStream in) throws SAXException,
			IOException {
		return parse(new InputSource(in));
	}

	/**
	 * Parse an XML document.
	 *
	 * @param is
	 *            The input source.
	 *
	 * @return A Document.
	 *
	 * @throws SAXException
	 *             If there is a parsing error.
	 * @throws IOException
	 *             If there is an I/O error.
	 * @throws IllegalArgumentException
	 *             If there is a parser configuration error.
	 */
	public static Document parse(final InputSource is) throws SAXException,
			IOException {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			return builder.parse(is);
		} catch (final ParserConfigurationException ex) {
			throw new IllegalArgumentException(
					"DOM parser configuration error: " + ex.getMessage());
		}
	}


	/**
	 * checks if a condition is true
	 *
	 * @param condition value of the condition attribute
	 * @return result of the evaluation of the condition
	 */
	public static boolean checkCondition(String condition) {
		if ((condition == null) || condition.trim().equals("")) {
			return true;
		}

		String value  = condition.trim();
		if (value.charAt(0) == '!') {
			return System.getProperty(value.substring(1)) == null;
		}

		return System.getProperty(value) != null;
	}
}
