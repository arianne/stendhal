package games.stendhal.bot.postman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

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
 * listens for udp messages
 *
 * @author hendrik
 */
public class CiaHandler {

	/**
	 * processes an CIA email
	 *
	 * @param is input stream delivering the email
	 * @return IRC command
	 * @throws IOException in case of an input/output error
	 */
	public List<CiaMessage> read(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		try {
			skipHeader(br);
			Element root = readXml(br);

			// read header
			Element source = getChild(root, "source");
			String module = getChildText(source, "module");
			String branch = getChildText(source, "branch");

			// read commits
			List<CiaMessage> list = new LinkedList<CiaMessage>();
			NodeList nodes = getChild(root, "body").getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if ((node instanceof Element) && node.getLocalName().equals("commit")) {
					CiaMessage msg = new CiaMessage();
					msg.setModule(module);
					msg.setBranch(branch);
					msg.setAuthor(getChildText(node, "author"));
					msg.setRevision(getChildText(node, "revision"));

					NodeList fileNodes = node.getChildNodes();
					for (int j = 0; i < fileNodes.getLength(); i++) {
						Node fileNode = nodes.item(j);
						if ((fileNode instanceof Element) && node.getLocalName().equals("file")) {
							msg.addFile(fileNode.getNodeValue().trim());
						}
					}
					msg.setMessage(getChildText(node, "log"));
					list.add(msg);
				}
			}

			return list;
		} finally {
			br.close();
		}
	}

	/**
	 * gets a child element from an XML structure
	 *
	 * @param element Element
	 * @param name name of child
	 * @return Element
	 * @throws IOException if the child does not exist
	 */
	private static Element getChild(Element element, String name) throws IOException {
		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if ((node instanceof Element) && node.getLocalName().equals(name)) {
				return (Element) list.item(i);
			}
		}
		throw new IOException("Element " + element.getLocalName() + " does not have expected child " + name);
	}

	/**
	 * gets the content of a child node or null
	 *
	 * @param element parent element
	 * @param name name of child
	 * @return content
	 */
	private static String getChildText(Node element, String name) {
		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if ((node instanceof Element) && node.getLocalName().equals(name)) {
				Element e = (Element) list.item(i);
				String value = e.getNodeValue();
				if (value != null) {
					value = value.trim();
				}
				return value;
			}
		}
		return null;
	}

	/**
	 * @param br
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	private Element readXml(BufferedReader br) throws IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(br));
			return doc.getDocumentElement();
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

	/**
	 * skips the mail header
	 *
	 * @param br BufferedReader
	 * @throws IOException in case of an input/output error
	 */
	private void skipHeader(BufferedReader br) throws IOException {
		String line = br.readLine();
		while ((line != null) && (!line.trim().isEmpty())) {
			line = br.readLine();
		}
	}
}
