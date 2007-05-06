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
package games.stendhal.server.rule.defaultruleset;

import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;
import games.stendhal.server.rule.EntityManager;

import java.util.List;
import java.util.Map;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class DefaultCreature {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(DefaultCreature.class);

	/** Creature class */
	private String clazz;

	/** Creature subclass */
	private String subclass;

	/** Creature name */
	private String name;

	/** optional creature description * */
	private String description;

	/** Map Tile Id in the way tileset.png:pos */
	private String tileid;

	/** hitpoints */
	private int hp;

	/** Attack points */
	private int atk;

	/** defense points */
	private int def;

	/** experience points for killing this creature */
	private int xp;

	private int level;

	private int respawn;

	/** size of the creature. */
	private int width;

	private int height;

	/** Ths list of items this creature may drop */
	private List<DropItem> dropsItems;

	private List<EquipItem> equipsItems;

	private List<String> creatureSays;

	private Map<String, String> aiProfiles;

	/** speed relative to player [0.0 ... 1.0] */
	private double speed;

	public DefaultCreature(String clazz, String subclass, String name, String tileid) {
		this.clazz = clazz;
		this.subclass = subclass;
		this.name = name;

		this.tileid = tileid;
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

	public void setNoiseLines(List<String> creatureSays) {
		this.creatureSays = creatureSays;
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

	/** returns a creature-instance */
	public Creature getCreature() {
		Creature creature = new Creature(clazz, subclass, name, hp, atk, def, level, xp, width, height, speed,
		        dropsItems, aiProfiles, creatureSays, respawn, description);
		creature.equip(equipsItems);
		return creature;
	}
	
	/** returns the tileid */
	public String getTileId() {
		return tileid;
	}

	/** returns the class */
	public String getCreatureClass() {
		return clazz;
	}

	public String getCreatureSubClass() {
		return subclass;
	}

	public String getCreatureName() {
		return name;
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
}
