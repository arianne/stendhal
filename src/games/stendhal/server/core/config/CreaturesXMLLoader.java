/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.EnumMap;
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

import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;

public final class CreaturesXMLLoader extends DefaultHandler {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(CreaturesXMLLoader.class);

	private String name;

	private String clazz;

	private String subclass;

	private String shadow_style;

	private String description;

	private String text;

	private String tileid;

	private int atk;

	private Integer ratk = 0;

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

	/** List of possible sound events. */
	private List<String> sounds;

	/** Sound played on creature death */
	private String deathSound;

	/** Looped sound effect for moving creature */
	private String movementSound;

	private LinkedHashMap<String, LinkedList<String>> creatureSays;

	private Map<String, String> aiProfiles;

	private List<DefaultCreature> list;

	private String bloodName;

	private String corpseName;

	private String harmlessCorpseName;

	private int corpseWidth;

	private int corpseHeight;

	private boolean drops;

	private boolean equips;

	private boolean ai;

	private boolean says;

	private boolean attributes;

	private boolean abilities;

	private String statusAttack;

	private double statusAttackProbability;

	/** Susceptibilities of a creature */
	private EnumMap<Nature, Double> susceptibilities;

	/** Type of the damage caused by the creature */
	private Nature damageType;
	/** Type of the damage caused by the creature when using ranged attacks. */
	private Nature rangedDamageType;

	private String condition;

	CreaturesXMLLoader() {
		// hide constructor, use the CreatureGroupsXMLLoader instead
	}

	public List<DefaultCreature> load(final URI ref) throws SAXException {
		list = new LinkedList<DefaultCreature>();
		// Use the default (non-validating) parser
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			final SAXParser saxParser = factory.newSAXParser();

			InputStream is = CreaturesXMLLoader.class.getResourceAsStream(ref.getPath());

			if (is == null) {
				throw new FileNotFoundException("cannot find resource '" + ref
						+ "' in classpath");
			}

			try {
				saxParser.parse(is, this);
			} finally {
				is.close();
			}
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
			condition = attrs.getValue("condition");
			drops = false;
			ai = false;
			dropsItems = new LinkedList<DropItem>();
			equipsItems = new LinkedList<EquipItem>();
			sounds = new LinkedList<String>();
			creatureSays = new LinkedHashMap<String, LinkedList<String>>();
			aiProfiles = new LinkedHashMap<String, String>();
			description = null;
			damageType = Nature.CUT;
			susceptibilities = new EnumMap<Nature, Double>(Nature.class);
			deathSound = null;
			statusAttack = null;
			statusAttackProbability = 0;
		} else if (qName.equals("type")) {
			clazz = attrs.getValue("class");
			subclass = attrs.getValue("subclass");
			shadow_style = attrs.getValue("shadow");

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
		} else if (qName.equals("blood")) {
			bloodName = attrs.getValue("name");
		} else if (qName.equals("corpse")) {
			corpseName = attrs.getValue("name");
			harmlessCorpseName = attrs.getValue("harmless");
			String value = attrs.getValue("width");

			// Default to entity size for width and height to save the fingers
			// of the people writing the creatures
			if (value != null) {
				corpseWidth = Integer.parseInt(value);
			} else {
				corpseWidth = sizeWidth;
			}

			value = attrs.getValue("height");
			if (value != null) {
				corpseHeight = Integer.parseInt(value);
			} else {
				corpseHeight = sizeHeight;
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
		} else if (attributes && qName.equals("ratk")) {
			ratk = Integer.parseInt(attrs.getValue("value"));
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
		} else if (says) {
			if (qName.equals("text") || qName.equals("noise")) {
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
			} else if (qName.equals("sound")) {
				sounds.add(attrs.getValue("value"));
			} else if (qName.equals("movement")) {
				movementSound = attrs.getValue("value");
            } else if (qName.equals("death")) {
                deathSound = attrs.getValue("value");
			}
		} else if (qName.equals("abilities")) {
			abilities = true;
		} else if (abilities && qName.equals("damage")) {
			String value = attrs.getValue("type");
			if (value == null) {
				value = "cut";
			}
			damageType = Nature.parse(value);
			value = attrs.getValue("rangedType");
			if (value != null) {
				rangedDamageType = Nature.parse(value);
			}
		} else if (abilities && qName.equals("susceptibility")) {
			Nature type = Nature.parse(attrs.getValue("type"));
			Double value = Double.valueOf(attrs.getValue("value"));
			susceptibilities.put(type, value);
		} else if (abilities && qName.equals("statusattack")) {
		    statusAttack = attrs.getValue("type");
		    statusAttackProbability = Double.valueOf(attrs.getValue("value"));
		}
	}

	@Override
	public void endElement(final String namespaceURI, final String sName, final String qName) {
		if (qName.equals("creature")) {

			if (!XMLUtil.checkCondition(condition)) {
				return;
			}

			if (!tileid.contains(":")) {
				logger.error("Corrupt XML file: Bad tileid for creature(" + name + ")");
				return;
			}

			final DefaultCreature creature = new DefaultCreature(clazz, subclass, name, tileid);
			creature.setRPStats(hp, atk, ratk, def, speed);
			creature.setLevel(level, xp);
			creature.setSize(sizeWidth, sizeHeight);
			creature.setEquipedItems(equipsItems);

			creature.setShadowStyle(shadow_style);

			creature.setBlood(bloodName);
			bloodName = null;

			creature.setCorpse(corpseName, harmlessCorpseName, corpseWidth, corpseHeight);
			corpseName = null;
			harmlessCorpseName = null;
			corpseWidth = corpseHeight = 1;

			creature.setDropItems(dropsItems);
			creature.setAIProfiles(aiProfiles);
			creature.setNoiseLines(creatureSays);
			creature.setRespawnTime(respawn);
			creature.setDescription(description);
			creature.setSusceptibilities(susceptibilities);
			creature.setDamageTypes(damageType, rangedDamageType);
			creature.setCreatureSounds(sounds);
			creature.setCreatureDeathSound(deathSound);
			creature.setCreatureMovementSound(movementSound);

			if (statusAttack != null) {
			    creature.setStatusAttack(statusAttack, statusAttackProbability);
			}

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
		} else if (qName.equals("abilities")) {
			abilities = false;
		}
	}

	@Override
	public void characters(final char[] buf, final int offset, final int len) {
		text = text + (new String(buf, offset, len)).trim() + " ";
	}
}
