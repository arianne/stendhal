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

	/** slots where this item can be equiped */
	private List<String> slots;

	/** Attributes of the item */
	private List<Pair<String, String>> attributes;

	private List<DefaultItem> list;

	private boolean attributesTag;

	protected Class implementation;

	public static void main(String argv[]) {
		if (argv.length != 1) {
			System.err.println("Usage: cmd filename");
			System.exit(1);
		}
		try {
			List<DefaultItem> items = new ItemXMLLoader().load(argv[0]);
			for (DefaultItem item : items) {
				System.out.println(item.getItemName());
				//				System.out.println(" -- " + item.getItem());
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

		System.exit(0);
	}

	private ItemXMLLoader() {
		// hide constructor, this is a Singleton
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

			InputStream is = getClass().getClassLoader().getResourceAsStream(ref);
			if (is == null) {
				throw new FileNotFoundException("cannot find resource '" + ref + "' in classpath");
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

	@Override
	public void startDocument() {
		// do nothing
	}

	@Override
	public void endDocument() {
		// do nothing
	}

	@Override
	public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) {
		text = "";
		if (qName.equals("item")) {
			name = attrs.getValue("name");
			attributes = new LinkedList<Pair<String, String>>();
			slots = new LinkedList<String>();
			description = null;
			implementation = null;
		} else if (qName.equals("type")) {
			clazz = attrs.getValue("class");
			subclass = attrs.getValue("subclass");
		} else if (qName.equals("implementation")) {

			String className = attrs.getValue("class-name");

			try {
				implementation = Class.forName(className);
			} catch (ClassNotFoundException ex) {
				logger.error("Unable to load class: " + className);
			}
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

	@Override
	public void endElement(String namespaceURI, String sName, String qName) {
		if (qName.equals("item")) {
			DefaultItem item = new DefaultItem(clazz, subclass, name, -1);
			item.setWeight(weight);
			item.setEquipableSlots(slots);
			item.setAttributes(attributes);
			item.setDescription(description);

			if (implementation == null) {
				logger.error("Item without defined implementation: " + name);
				return;
			}

			item.setImplementation(implementation);

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

	@Override
	public void characters(char buf[], int offset, int len) {
		text = text + (new String(buf, offset, len)).trim() + " ";
	}
}
