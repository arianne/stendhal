package games.stendhal.bot.postman;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * helper methods for dealing with W3C DOM
 *
 * @author hendrik
 */
public class DomHelper {

	/**
	 * gets a child element from an XML structure
	 *
	 * @param element Element
	 * @param name name of child
	 * @return Element
	 * @throws IOException if the child does not exist
	 */
	public static Element getChild(Node element, String name) throws IOException {
		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if ((node instanceof Element) && node.getNodeName().equals(name)) {
				return (Element) list.item(i);
			}
		}
		throw new IOException("Element " + element.getNodeName() + " does not have expected child " + name);
	}

	/**
	 * gets the content of a child node or null
	 *
	 * @param element parent element
	 * @param name name of child
	 * @return content
	 */
	public static String getChildText(Node element, String name) {
		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if ((node instanceof Element) && node.getNodeName().equals(name)) {
				Element e = (Element) list.item(i);
				String value = e.getTextContent();
				if (value != null) {
					value = value.trim();
				}
				return value;
			}
		}
		return null;
	}

	/**
	 * parses an xml document into a W3C DOM object tree
	 *
	 * @param is input source
	 * @return root Element
	 * @throws IOException in case of an error
	 */
	public static Element readXml(InputSource is) throws IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(is);
			return doc.getDocumentElement();
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

}
