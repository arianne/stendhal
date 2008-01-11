/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rule.defaultruleset;

import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DefaultCreature {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(DefaultCreature.class);

	/** Creature class. */
	private String clazz;

	/** Creature subclass. */
	private String subclass;

	/** Creature name. */
	private String name;

	/** optional creature description. * */
	private String description;

	/** Map Tile Id in the way tileset.png:pos. */
	private String tileid;

	/** hitpoints. */
	private int hp;

	/** Attack points. */
	private int atk;

	/** defense points. */
	private int def;

	/** experience points for killing this creature. */
	private int xp;

	private int level;

	private int respawn;

	/** size of the creature. */
	private int width;

	private int height;

	/** The list of items this creature may drop. */
	private List<DropItem> dropsItems;

	private List<EquipItem> equipsItems;

	private List<String> creatureSays;

	private Map<String, String> aiProfiles;

	/** speed relative to player [0.0 ... 1.0] */
	private double speed;

	public DefaultCreature(String clazz, String subclass, String name,
			String tileid) {
		this.clazz = clazz;
		this.subclass = subclass;
		this.name = name;

		this.tileid = tileid;
		dropsItems = new LinkedList<DropItem>();
		equipsItems = new LinkedList<EquipItem>();
		creatureSays = new LinkedList<String>();
		aiProfiles = new LinkedHashMap<String, String>();
	}

	public void setDescription(String text) {
		this.description = text;
	}

	public String getDescription() {
		return description;
	}

	public void setRPStats(int hp, int atk, int def, double speed) {
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.speed = speed;
	}

	public int getHP() {
		return hp;
	}

	public int getATK() {
		return atk;
	}

	public int getDEF() {
		return def;
	}

	public double getSpeed() {
		return speed;
	}

	public void setLevel(int level, int xp) {
		this.level = level;
		this.xp = xp;
	}

	public void setRespawnTime(int respawn) {
		this.respawn = respawn;
	}

	public int getRespawnTime() {
		return respawn;
	}

	public int getLevel() {
		return level;
	}

	public int getXP() {
		return xp;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setNoiseLines(List<String> creatureSays) {
		this.creatureSays = creatureSays;
	}

	public List<String> getNoiseLines() {
		return creatureSays;
	}

	public void setEquipedItems(List<EquipItem> equipsItems) {
		this.equipsItems = equipsItems;
	}

	public List<EquipItem> getEquipedItems() {
		return equipsItems;
	}

	public void setDropItems(List<DropItem> dropsItems) {
		this.dropsItems = dropsItems;
	}

	public List<DropItem> getDropItems() {
		return dropsItems;
	}

	public void setAIProfiles(Map<String, String> aiProfiles) {
		this.aiProfiles = aiProfiles;
	}

	/** returns a creature-instance. */
	public Creature getCreature() {
		Collections.sort(dropsItems, new Comparator<DropItem>() {

			public int compare(DropItem o1, DropItem o2) {
				if (o1.probability < o2.probability) {
					return -1;
				} else if (o1.probability > o2.probability) {
					return 1;
				} else {
					return 0;
				}
			}

		});

		Creature creature = new Creature(clazz, subclass, name, hp, atk, def,
				level, xp, width, height, speed, dropsItems, aiProfiles,
				creatureSays, respawn, description);
		creature.equip(equipsItems);
		return creature;
	}

	/** returns the tileid. */
	public String getTileId() {
		return tileid;
	}

	public void setTileId(String val) {
		tileid = val;
	}

	/** returns the class. */
	public String getCreatureClass() {
		return clazz;
	}

	public String getCreatureSubClass() {
		return subclass;
	}

	public String getCreatureName() {
		return name;
	}

	public void setCreatureClass(String val) {
		clazz = val;
	}

	public void setCreatureSubClass(String val) {
		subclass = val;
	}

	public void setCreatureName(String val) {
		name = val;
	}

	public boolean verifyItems(EntityManager manager) {
		for (DropItem item : dropsItems) {
			if (!manager.isItem(item.name)) {
				logger.warn("Item " + item.name + " doesnt exists");
				return false;
			}
		}

		for (EquipItem item : equipsItems) {
			if (!manager.isItem(item.name)) {
				logger.warn("Item " + item.name + " doesnt exists");
				return false;
			}
		}

		return true;
	}

	public String toXML() {
		StringBuffer os = new StringBuffer();
		os.append("  <creature name=\"" + name + "\">\n");
		os.append("    <type class=\"" + clazz + "\" subclass=\"" + subclass
				+ "\" tileid=\""
				+ tileid.replace("../../tileset/logic/creature/", "")
				+ "\"/>\n");
		if (description != null) {
			os.append("    <description>" + description + "</description>\n");
		}
		os.append("    <attributes>\n");
		os.append("      <atk value=\"" + atk + "\"/>\n");
		os.append("      <def value=\"" + def + "\"/>\n");
		os.append("      <hp value=\"" + hp + "\"/>\n");
		os.append("      <speed value=\"" + speed + "\"/>\n");
		os.append("      <size value=\"" + width + "," + height + "\"/>\n");
		os.append("    </attributes>\n");
		os.append("    <level value=\"" + level + "\"/>\n");
		os.append("    <experience value=\"" + (xp / 20) + "\"/>\n");
		os.append("    <respawn value=\"" + respawn + "\"/>\n");
		os.append("    <drops>\n");
		for (DropItem item : dropsItems) {
			os.append("      <item value=\"" + item.name + "\" quantity=\"["
					+ item.min + "," + item.max + "]\" probability=\""
					+ item.probability + "\"/>\n");
		}
		os.append("    </drops>\n");
		os.append("    <equips>\n");
		for (EquipItem item : equipsItems) {
			os.append("      <slot name=\"" + item.slot + "\" item=\""
					+ item.name + "\" quantity=\"" + item.quantity + "\"/>\n");
		}
		os.append("    </equips>\n");
		os.append("    <ai>\n");
		if (!creatureSays.isEmpty()) {
			os.append("      <says>\n");
			for (String say : creatureSays) {
				os.append("        <noise value=\"" + say + "\"/>\n");
			}
			os.append("      </says>\n");
		}
		for (Map.Entry<String, String> entry : aiProfiles.entrySet()) {
			os.append("      <profile name=\"" + entry.getKey() + "\"");
			if (entry.getValue() != null) {
				os.append(" params=\"" + entry.getValue() + "\"");
			}
			os.append("/>\n");
		}
		os.append("    </ai>\n");
		os.append("  </creature>\n");
		return os.toString();
	}

	public Map<String, String> getAIProfiles() {
		return aiProfiles;
	}
}
