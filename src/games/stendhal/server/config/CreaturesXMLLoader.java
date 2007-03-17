package games.stendhal.server.config;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.rule.defaultruleset.DefaultCreature;
import org.apache.log4j.Logger;
import marauroa.common.Log4J;

public class CreaturesXMLLoader extends DefaultHandler {

	/** The Singleton instance. */
	private static CreaturesXMLLoader instance;

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(CreaturesXMLLoader.class);

	private String name;

	private String clazz;

	private String subclass;

	private String description;

	private String text;

	private int tileid;

	private int atk;

	private int def;

	private int hp;

	private double speed;

	private int sizeWidth;

	private int sizeHeight;

	private int xp;

	private int level;

	private int respawn;

	private List<Creature.DropItem> dropsItems;

	private List<Creature.EquipItem> equipsItems;

	private List<String> creatureSays;

	private Map<String, String> aiProfiles;

	private List<DefaultCreature> list;

	private boolean drops;

	private boolean equips;

	private boolean ai;

	private boolean says;

	private boolean attributes;

	public static void main(String argv[]) {
		if (argv.length != 1) {
			System.err.println("Usage: cmd filename");
			System.exit(1);
		}

		try {
			System.out.println(new CreaturesXMLLoader().load(argv[0]).size());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	private CreaturesXMLLoader() {
		// hide constructor, this is a Singleton
	}

	public static CreaturesXMLLoader get() {
		if (instance == null) {
			instance = new CreaturesXMLLoader();
		}
		return instance;
	}

	public List<DefaultCreature> load(String ref) throws SAXException {
		list = new LinkedList<DefaultCreature>();
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
		if (qName.equals("creature")) {
			name = attrs.getValue("name");
			drops = false;
			ai = false;
			dropsItems = new LinkedList<Creature.DropItem>();
			equipsItems = new LinkedList<Creature.EquipItem>();
			creatureSays = new LinkedList<String>();
			aiProfiles = new HashMap<String, String>();
			description = null;
		} else if (qName.equals("type")) {
			clazz = attrs.getValue("class");
			subclass = attrs.getValue("subclass");

			String tileid_value = attrs.getValue("tileid");
			tileid = (tileid_value != null ? Integer.parseInt(tileid_value)
					: -1);
		} else if (qName.equals("level")) {
			level = Integer.parseInt(attrs.getValue("value"));
		} else if (qName.equals("experience")) {
			// XP rewarded is right now 5% of the creature real XP
			xp = Integer.parseInt(attrs.getValue("value")) * 20;
		} else if (qName.equals("equips")) {
			equips = true;
		} else if (qName.equals("slot") && equips) {
			String item = null;
			String slot = null;
			int quantity = 1;

			for (int i = 0; i < attrs.getLength(); i++) {
				if (attrs.getQName(i).equals("item")) {
					item = attrs.getValue(i);
				} else if (attrs.getQName(i).equals("name")) {
					slot = attrs.getValue(i);
				} else if (attrs.getQName(i).equals("quantity")) {
					quantity = Integer.parseInt(attrs.getValue(i));
				}
			}
			if ((item != null) && (slot != null)) {
				equipsItems.add(new Creature.EquipItem(slot, item, quantity));
			}
		} else if (qName.equals("respawn")) {
			respawn = Integer.parseInt(attrs.getValue("value"));
		} else if (qName.equals("drops")) {
			drops = true;
		} else if (qName.equals("item") && drops) {
			String name = null;
			Double probability = null;
			String range = null;

			for (int i = 0; i < attrs.getLength(); i++) {
				if (attrs.getQName(i).equals("value")) {
					name = attrs.getValue(i);
				} else if (attrs.getQName(i).equals("probability")) {
					probability = Double.parseDouble(attrs.getValue(i));
				} else if (attrs.getQName(i).equals("quantity")) {
					range = attrs.getValue(i);
				}
			}

			if ((name != null) && (probability != null) && (range != null)) {
				logger.debug(name + ":" + probability + ":" + range);
				if (range.contains("[")) {
					range = range.replace("[", "");
					range = range.replace("]", "");
					String[] amount = range.split(",");

					dropsItems.add(new Creature.DropItem(name, probability,
							Integer.parseInt(amount[0]), Integer
									.parseInt(amount[1])));
				} else {
					dropsItems.add(new Creature.DropItem(name, probability,
							Integer.parseInt(range)));
				}
			}
		} else if (qName.equals("attributes")) {
			attributes = true;
		} else if (attributes && qName.equals("atk")) {
			atk = Integer.parseInt(attrs.getValue("value"));
		} else if (attributes && qName.equals("def")) {
			def = Integer.parseInt(attrs.getValue("value"));
		} else if (attributes && qName.equals("hp")) {
			hp = Integer.parseInt(attrs.getValue("value"));
		} else if (attributes && qName.equals("speed")) {
			speed = Double.parseDouble(attrs.getValue("value"));
		} else if (attributes && qName.equals("size")) {
			String[] size = attrs.getValue("value").split(",");

			sizeWidth = Integer.parseInt(size[0]);
			sizeHeight = Integer.parseInt(size[1]);
		} else if (qName.equals("ai")) {
			ai = true;
		} else if (ai && qName.equals("profile")) {
			aiProfiles.put(attrs.getValue("name"), attrs.getValue("params"));
		} else if (ai && qName.equals("says")) {
			says = true;
		} else if (says && qName.equals("noise")) {
			creatureSays.add(attrs.getValue("value"));
		}
	}

	@Override
	public void endElement(String namespaceURI, String sName, String qName) {
		if (qName.equals("creature")) {
			DefaultCreature creature = new DefaultCreature(clazz, subclass,
					name, tileid);
			creature.setRPStats(hp, atk, def, speed);
			creature.setLevel(level, xp);
			creature.setSize(sizeWidth, sizeHeight);
			creature.setEquipedItems(equipsItems);
			creature.setDropItems(dropsItems);
			creature.setAIProfiles(aiProfiles);
			creature.setNoiseLines(creatureSays);
			creature.setRespawnTime(respawn);
			creature.setDescription(description);
			list.add(creature);
		} else if (qName.equals("attributes")) {
			attributes = false;
		} else if (qName.equals("equips")) {
			equips = false;
		} else if (qName.equals("drops")) {
			drops = false;
		} else if (ai && qName.equals("says")) {
			says = false;
		} else if (qName.equals("ai")) {
			ai = false;
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
