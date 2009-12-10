package games.stendhal.server.core.config;

import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

public class CreaturesXMLLoader extends DefaultHandler {

	/** The Singleton instance. */
	private static CreaturesXMLLoader instance;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(CreaturesXMLLoader.class);

	private String name;

	private String clazz;

	private String subclass;

	private String description;

	private String text;

	private String tileid;

	private int atk;

	private int def;

	private int hp;

	private double speed;

	private int sizeWidth;

	private int sizeHeight;

	private int xp;

	private int level;

	private int respawn;

	private List<DropItem> dropsItems;

	private List<EquipItem> equipsItems;

	private LinkedHashMap<String, LinkedList<String>> creatureSays;

	private Map<String, String> aiProfiles;

	private List<DefaultCreature> list;
	
	private String corpseName;
	
	private int corpseWidth;
	
	private int corpseHeight;

	private boolean drops;

	private boolean equips;

	private boolean ai;

	private boolean says;

	private boolean attributes;

	public static void main(final String[] argv) {
		if (argv.length != 1) {
			System.err.println("Usage: cmd filename");
			System.exit(1);
		}

		try {
			System.out.println(new CreaturesXMLLoader().load(argv[0]).size());
		} catch (final Throwable e) {
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

	public List<DefaultCreature> load(final String ref) throws SAXException {
		list = new LinkedList<DefaultCreature>();
		// Use the default (non-validating) parser
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			final SAXParser saxParser = factory.newSAXParser();

			InputStream is = getClass().getClassLoader().getResourceAsStream(
					ref);

			if (is == null) {
				is = new File(ref).toURI().toURL().openStream();
			}

			if (is == null) {
				throw new FileNotFoundException("cannot find resource '" + ref
						+ "' in classpath");
			}
			saxParser.parse(is, this);
		} catch (final ParserConfigurationException t) {
			logger.error(t);
		} catch (final IOException e) {
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
	public void startElement(final String namespaceURI, final String lName, final String qName,
			final Attributes attrs) {
		text = "";
		if (qName.equals("creature")) {
			name = attrs.getValue("name");
			drops = false;
			ai = false;
			dropsItems = new LinkedList<DropItem>();
			equipsItems = new LinkedList<EquipItem>();
			creatureSays = new LinkedHashMap<String, LinkedList<String>>();
			aiProfiles = new LinkedHashMap<String, String>();
			description = null;
		} else if (qName.equals("type")) {
			clazz = attrs.getValue("class");
			subclass = attrs.getValue("subclass");

			tileid = "../../tileset/logic/creature/" + attrs.getValue("tileid");
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
				equipsItems.add(new EquipItem(slot, item, quantity));
			}
		} else if (qName.equals("respawn")) {
			respawn = Integer.parseInt(attrs.getValue("value"));
		} else if (qName.equals("corpse")) {
			corpseName = attrs.getValue("name");
			String value = attrs.getValue("width");
			
			// Default to 1 for width and height to save the fingers 
			// of the people writing the creatures
			if (value != null) {
				corpseWidth = Integer.parseInt(value);
			} else {
				corpseWidth = 1;
			}

			value = attrs.getValue("height");
			if (value != null) {
				corpseHeight = Integer.parseInt(value);
			} else {
				corpseHeight = 1;
			}
		} else if (qName.equals("drops")) {
			drops = true;
		} else if (qName.equals("item") && drops) {
			String tempName = null;
			Double probability = null;
			String range = null;

			for (int i = 0; i < attrs.getLength(); i++) {
				if (attrs.getQName(i).equals("value")) {
					tempName = attrs.getValue(i);
				} else if (attrs.getQName(i).equals("probability")) {
					probability = Double.parseDouble(attrs.getValue(i));
				} else if (attrs.getQName(i).equals("quantity")) {
					range = attrs.getValue(i);
				}
			}

			if ((tempName != null) && (probability != null) && (range != null)) {
				logger.debug(tempName + ":" + probability + ":" + range);
				if (range.contains("[")) {
					range = range.replace("[", "");
					range = range.replace("]", "");
					final String[] amount = range.split(",");

					dropsItems.add(new DropItem(tempName, probability,
							Integer.parseInt(amount[0]),
							Integer.parseInt(amount[1])));
				} else {
					dropsItems.add(new DropItem(tempName, probability,
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
			final String[] size = attrs.getValue("value").split(",");

			sizeWidth = Integer.parseInt(size[0]);
			sizeHeight = Integer.parseInt(size[1]);
		} else if (qName.equals("ai")) {
			ai = true;
		} else if (ai && qName.equals("profile")) {
			aiProfiles.put(attrs.getValue("name"), attrs.getValue("params"));
		} else if (ai && qName.equals("says")) {
			says = true;
		} else if (says && qName.equals("noise")) {
			final String states = attrs.getValue("state");
			final String value = attrs.getValue("value");
			final List<String> keys=Arrays.asList(states.split(" "));
			// no such state in noises, will add it
			for (int i=0; i<keys.size(); i++) {
				final String key=keys.get(i);
				if(creatureSays.get(key)==null) {
					final LinkedList<String> ll=new LinkedList<String>();
					ll.add(value);
					creatureSays.put(key, ll);
					// no such value in existing state, will add it
				} else if (creatureSays.get(key).indexOf(value)==-1) {
					creatureSays.get(key).add(value);
					// both state and value already exists
				} else {
					logger.warn("CreatureXMLLoader: creature ("+name+
								"): double definition for noise \""+key+"\" ("+value+")");
				}
			}
		} 
	}

	@Override
	public void endElement(final String namespaceURI, final String sName, final String qName) {
		if (qName.equals("creature")) {
			if (!tileid.contains(":")) {
				logger.error("Corrupt XML file: Bad tileid for creature("
						+ name + ")");
				return;
			}

			final DefaultCreature creature = new DefaultCreature(clazz, subclass,
					name, tileid);
			creature.setRPStats(hp, atk, def, speed);
			creature.setLevel(level, xp);
			creature.setSize(sizeWidth, sizeHeight);
			creature.setEquipedItems(equipsItems);
			
			creature.setCorpse(corpseName, corpseWidth, corpseHeight);
			corpseName = null;
			corpseWidth = corpseHeight = 1;
			
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
	public void characters(final char[] buf, final int offset, final int len) {
		text = text + (new String(buf, offset, len)).trim() + " ";
	}
}
