package games.stendhal.server.rule.defaultruleset;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import games.stendhal.common.Pair;
import org.apache.log4j.Logger;
import marauroa.common.Log4J;

public class ItemXMLLoader extends DefaultHandler {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(ItemXMLLoader.class);

	private String name;

	private String clazz;

	private String subclass;

	private String description;

	private String text;

	private double weight;

	private boolean stackable;

	/** slots where this item can be equiped */
	private List<String> slots;

	/** Attributes of the item */
	private List<Pair<String, String>> attributes;

	private List<DefaultItem> list;

	public static void main(String argv[]) {
		if (argv.length != 1) {
			System.err.println("Usage: cmd filename");
			System.exit(1);
		}
		try {
			List<DefaultItem> items = new ItemXMLLoader().load(argv[0]);
			for (DefaultItem item : items) {
				System.out.println(item.getItemName());
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

		System.exit(0);
	}

	private ItemXMLLoader() {
	}

	private static ItemXMLLoader instance;

	public static ItemXMLLoader get() {
		if (instance == null) {
			instance = new ItemXMLLoader();
		}

		return instance;
	}

	public List<DefaultItem> load(String ref) throws SAXException {
		list = new LinkedList<DefaultItem>();
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();

			InputStream is = getClass().getClassLoader().getResourceAsStream(
					ref);
			if (is == null) {
				throw new FileNotFoundException("cannot find resource '" + ref
						+ "' in classpath");
			}
			saxParser.parse(is, this);
		} catch (ParserConfigurationException t) {
			logger.error(t);
		} catch (IOException e) {
			logger.error(e);
			throw new SAXException(e);
		}

		return list;
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	private boolean attributesTag;

	public void startElement(String namespaceURI, String lName, String qName,
			Attributes attrs) throws SAXException {
		text = "";
		if (qName.equals("item")) {
			name = attrs.getValue("name");
			attributes = new LinkedList<Pair<String, String>>();
			slots = new LinkedList<String>();
			stackable = false;
			description = null;
		} else if (qName.equals("type")) {
			clazz = attrs.getValue("class");
			subclass = attrs.getValue("subclass");
		} else if (qName.equals("stackable")) {
			stackable = true;
		} else if (qName.equals("weight")) {
			weight = Double.parseDouble(attrs.getValue("value"));
		} else if (qName.equals("slot")) {
			slots.add(attrs.getValue("name"));
		} else if (qName.equals("attributes")) {
			attributesTag = true;
		} else if (attributesTag) {
			String name = qName;
			String value = attrs.getValue("value");

			attributes.add(new Pair<String, String>(name, value));
		}
	}

	public void endElement(String namespaceURI, String sName, String qName)
			throws SAXException {
		if (qName.equals("item")) {
			DefaultItem item = new DefaultItem(clazz, subclass, name, -1);
			item.setWeight(weight);
			item.setEquipableSlots(slots);
			item.setAttributes(attributes);
			item.setDescription(description);
			if (stackable) {
				item.setStackable();
			}

			list.add(item);
		} else if (qName.equals("attributes")) {
			attributesTag = false;
		} else if (qName.equals("description")) {
			if (text != null) {
				description = text.trim();
			}
			text = "";
		}
	}

	public void characters(char buf[], int offset, int len) throws SAXException {
		text = text + (new String(buf, offset, len)).trim() + " ";
	}
}