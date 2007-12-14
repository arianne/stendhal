package games.stendhal.server.core.config;

import games.stendhal.server.core.rule.defaultruleset.DefaultItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class ItemsXMLLoader extends DefaultHandler {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(ItemsXMLLoader.class);

	private String name;

	private String clazz;

	private String subclass;

	private String description;

	private String text;

	private double weight;

	private int value;

	/** slots where this item can be equipped */
	private List<String> slots;

	/** Attributes of the item */
	private Map<String, String> attributes;

	private List<DefaultItem> list;

	private boolean attributesTag;

	protected Class<?> implementation;

	public List<DefaultItem> load(URI uri) throws SAXException {
		list = new LinkedList<DefaultItem>();
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();

			InputStream is = getClass().getResourceAsStream(uri.getPath());

			if (is == null) {
				throw new FileNotFoundException("cannot find resource '" + uri
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

	@Override
	public void startDocument() {
		// do nothing
	}

	@Override
	public void endDocument() {
		// do nothing
	}

	@Override
	public void startElement(String namespaceURI, String lName, String qName,
			Attributes attrs) {
		text = "";
		if (qName.equals("item")) {
			name = attrs.getValue("name");
			attributes = new LinkedHashMap<String, String>();
			slots = new LinkedList<String>();
			description = "";
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
		} else if (qName.equals("value")) {
			value = Integer.parseInt(attrs.getValue("value"));
		} else if (qName.equals("slot")) {
			slots.add(attrs.getValue("name"));
		} else if (qName.equals("attributes")) {
			attributesTag = true;
		} else if (attributesTag) {
			attributes.put(qName, attrs.getValue("value"));
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
			item.setValue(value);

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
				// TODO: There are empty spaces on the middle of the
				// description.
			}
		}
	}

	@Override
	public void characters(char[] buf, int offset, int len) {
		text = text + (new String(buf, offset, len)).trim();
	}
}
