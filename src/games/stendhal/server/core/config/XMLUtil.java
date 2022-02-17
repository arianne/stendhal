package games.stendhal.server.core.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML utility methods for DOM reading.
 */
public class XMLUtil {

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
	 * Retrieve first element found.
	 */
	public static Element getElement(final Element parent,
			final String name) {
		final List<Element> es = getElements(parent, name);
		if (!es.isEmpty()) {
			return es.get(0);
		}

		return null;
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
	 * creates a child element at the specified position
	 *
	 * @param parent parent element
	 * @param name   name of new child element
	 * @param order  order of elements to insert the new child element at the correct position
	 * @return newly created child element
	 */
	public static Element createChildElement(Element parent, String name, List<String> order) {
		Element newChild = parent.getOwnerDocument().createElement(name);

		int pos = order.indexOf(name);
		if (pos < 0) {
			parent.appendChild(newChild);
			return newChild;
		}

		Set<String> before = new HashSet<>(order.subList(0, pos));

		for (Element child : XMLUtil.getElements(parent)) {
			if (!before.contains(child.getTagName())) {
				parent.insertBefore(newChild, child);
				return newChild;
			}
		}

		parent.appendChild(newChild);
		return newChild;
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
	 * writes an xml document to a file
	 *
	 * @param document DOM document
	 * @param filename filename
	 * @throws IOException in case of an input/output error
	 * @throws TransformerException in case of an transformation issue
	 */
	public static void writeFile(Document document, String filename) throws IOException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(document);
		FileWriter writer = new FileWriter(new File(filename));
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		writer.close();
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
