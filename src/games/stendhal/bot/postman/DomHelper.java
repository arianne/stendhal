package games.stendhal.bot.postman;

import java.io.IOException;
import java.util.Iterator;

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
		Iterator<Element> itr = getChildren(element, name).iterator();
		if (itr.hasNext()) {
			return itr.next();
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
		Iterator<Element> itr = getChildren(element, name).iterator();
		if (itr.hasNext()) {
			String res = itr.next().getTextContent();
			if (res != null) {
				return res.trim();
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

	/**
	 * gets the children
	 *
	 * @param parent parent node
	 * @param name optional name of children
	 * @return iterable
	 */
	public static NodeListIterable getChildren(Node parent, String name) {
		return new NodeListIterable(parent, name);
	}

	/**
	 * a node list iterable
	 */
	static class NodeListIterable implements Iterable<Element> {
		private final Node parent;
		private final String name;

		/**
		 * NodeListIterable
		 *
		 * @param parent parent node
		 * @param name name of interesting children
		 */
		public NodeListIterable(Node parent, String name) {
			this.parent = parent;
			this.name = name;
		}

		@Override
		public Iterator<Element> iterator() {
			return new NodeListIterator(parent, name);
		}

	}

	/**
	 * a node list iterator
	 */
	static class NodeListIterator implements Iterator<Element> {
		private final NodeList nodes;
		private final String name;
		private int nextNode = 0;

		/**
		 * NodeListIterator
		 *
		 * @param parent parent node
		 * @param name name of interesting children
		 */
		public NodeListIterator(Node parent, String name) {
			this.nodes = parent.getChildNodes();
			this.name = name;
			findNextNode();
		}

		@Override
		public boolean hasNext() {
			return nextNode < nodes.getLength();
		}

		@Override
		public Element next() {
			Element temp = (Element) nodes.item(nextNode);
			findNextNode();
			return temp;
		}

		private void findNextNode() {
			nextNode++;
			while (hasNext()) {
				Node node = nodes.item(nextNode);
				if (node instanceof Element) {
					if (name == null || name.equals(node.getNodeName())) {
						return;
					}
				}
				nextNode++;
			}

		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
