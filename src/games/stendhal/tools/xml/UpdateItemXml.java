package games.stendhal.tools.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.io.PatternFilenameFilter;

import games.stendhal.server.core.config.XMLUtil;
import marauroa.common.io.UnicodeSupportingInputStream;

public class UpdateItemXml {
	private String attributeToModify = "min_level";
	private Map<String, String> newValues = new HashMap<>();

	private void update() throws SAXException, IOException, TransformerException {
		readConfig();
		System.out.println(newValues);
		String rootDirectory = this.getClass().getResource("/").getPath() + "/../";
		for (File file : new File(rootDirectory + "data/conf/items").listFiles(new PatternFilenameFilter(".*\\.xml"))) {
			processFile(file.getAbsolutePath());
		}

	}

	private void readConfig() throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader("/tmp/values.txt"))) {
			String line = br.readLine();
			while (line != null) {
				String[] entry = line.split("\t");
				if (entry.length == 2) {
					newValues.put(entry[0], entry[1]);
				}
				line = br.readLine();
			}
		}
	}

	private void processFile(String filename) throws SAXException, IOException, TransformerException {
		Document document = XMLUtil.parse(new UnicodeSupportingInputStream(new FileInputStream(filename)));
		Element root = document.getDocumentElement();
		NodeList items = root.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			processItem((Element) items.item(i));
		}
		XMLUtil.writeFile(document, filename);
	}

	private void processItem(Element item) {
		String newValue = newValues.get(item.getAttribute("name"));
		if (newValue == null) {
			return;
		}

		NodeList elements = item.getElementsByTagName(attributeToModify);
		Element originalElement = null;
		Element testserverElement = null;
		String originalValue = null;
		String testserverValue = null;
		for (int i = 0; i < elements.getLength(); i++) {
			Element element = (Element) elements.item(i);
			String value = element.getAttribute("value");
			String condition = element.getAttribute("condition");
			if ((condition.equals("")) || condition.equals("!stendhal.testserver")) {
				originalElement = element;
				originalValue = value;
			} else if (condition.equals("stendhal.testserver")) {
				testserverElement = element;
				testserverValue = value;
			}
		}

		if (newValue.equals(originalValue)) {
			if (testserverElement!=null) {
				testserverElement.getParentNode().removeChild(testserverElement);
			}
			originalElement.removeAttribute("condition");
		} else  if (!newValue.equals(testserverValue)) {
			if (testserverValue != null) {
				testserverElement.setAttribute("value", newValue);
			} else {
				if (originalElement != null) {
					originalElement.setAttribute("condition", "!stendhal.testserver");
				}
				List<String> order = Arrays.asList("infostring", "atk", "def", "ratk", "immunization", "amount", "frequency", "range", "quantity", "max_quantity", "regen", "rate", "lifesteal", "persistent", "min_level", "life_support", "slot_name", "undroppableondeath", "autobind", "slot_size", "menu", "durability", "status_resist", "itemset");
				testserverElement = XMLUtil.createChildElement(XMLUtil.getElements(item, "attributes").get(0), attributeToModify, order);
				testserverElement.setAttribute("value", newValue);
				testserverElement.setAttribute("condition", "stendhal.testserver");
			}
		}
	}


	public static void main(String[] args) throws SAXException, IOException, TransformerException {
		new UpdateItemXml().update();
	}


}
