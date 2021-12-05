package games.stendhal.tools.xml;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import games.stendhal.server.core.config.XMLUtil;

public class XmlMergerAndSplitter {
	TreeMap<String, Element> elements = new TreeMap<>();

	private void readFiles(String dirname) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		File dir = new File(dirname);
		for (File file : dir.listFiles()) {
			Document doc = dBuilder.parse(file);
			Element root = doc.getDocumentElement();
			NodeList nodes = root.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node item = nodes.item(i);
				if (item instanceof Element) {
					String name = ((Element) item).getAttribute("name");
					elements.put(name, (Element) item);
				}
			}
		}
	}

	private void writeSingleFile(String filename) throws IOException, ParserConfigurationException, TransformerException {
		Document resultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = resultDocument.createElement("root");
		resultDocument.appendChild(root);

		for (Map.Entry<String, Element> entry : elements.entrySet()) {
			resultDocument.adoptNode(entry.getValue());
			root.appendChild(entry.getValue());
		}
		XMLUtil.writeFile(resultDocument, filename);
	}


	private void writeFiles(String folder) throws IOException, TransformerException, ParserConfigurationException {
		for (Map.Entry<String, Element> entry : elements.entrySet()) {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = document.createElement("root");
			document.appendChild(root);

			document.adoptNode(entry.getValue());
			root.appendChild(entry.getValue());
			XMLUtil.writeFile(document, folder + "/" + entry.getKey());
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
/*		XmlMerger xmlMerger = new XmlMerger();
		xmlMerger.readFiles("/home/hendrik/workspace/stendhal/data/conf/creatures");
		xmlMerger.writeFiles("/tmp/new");

		xmlMerger = new XmlMerger();
		xmlMerger.readFiles("/tmp/oldstendhal/data/conf/creatures");
		xmlMerger.writeFiles("/tmp/old");
*/
		XmlMergerAndSplitter xmlMerger = new XmlMergerAndSplitter();
		xmlMerger.readFiles("stendhal/data/conf/items");
		xmlMerger.writeFiles("/tmp/newitems");

		xmlMerger = new XmlMergerAndSplitter();
		xmlMerger.readFiles("oldstendhal/data/conf/items");
		xmlMerger.writeFiles("/tmp/olditems");
	}
}
