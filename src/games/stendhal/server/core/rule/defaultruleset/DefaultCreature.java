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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;
import games.stendhal.server.entity.status.Status;
import games.stendhal.server.entity.status.StatusAttacker;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class DefaultCreature {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(DefaultCreature.class);

	/** Creature class. */
	private String clazz;

	/** Creature subclass. */
	private String subclass;

	/** shadow to use for entity */
	private String shadow_style = null;

	/** Creature name. */
	private String name;

	/** optional creature description. * */
	private String description;

	/** Map Tile Id in the way tileset.png:pos. */
	private String tileid;

	/** hit points. */
	private int hp;

	/** attack points. */
	private int atk;

	/** ranged attack points */
	private int ratk;

	/** defense points. */
	private int def;

	/** experience points for killing this creature. */
	private int xp;

	private int level;

	private int respawn;

	/** size of the creature. */
	private int width;

	private int height;

	private String bloodClass;

	private String corpseName;
	private String harmlessCorpseName;
	private int corpseWidth;
	private int corpseHeight;

	/** The list of items this creature may drop. */
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

	/** Susceptibilities of the creature */
	private Map<Nature, Double> susceptibilities;

	/** Status attack types */
	private String statusAttack;
	private double statusAttackProbability;

	/** Type of damage caused by the creature */
	private Nature damageType;
	/** Type of damage caused by the creature when using ranged attacks. if
	 * <code>null<code>, then the melee type is used for all attacks. */
	private Nature rangedDamageType;

	/** speed relative to player [0.0 ... 1.0] */
	private double speed;

	public DefaultCreature(final String clazz, final String subclass, final String name,
			final String tileid) {
		this.clazz = clazz;
		this.subclass = subclass;
		this.name = name;

		this.tileid = tileid;
		dropsItems = new LinkedList<DropItem>();
		equipsItems = new LinkedList<EquipItem>();
		creatureSays = new LinkedHashMap<String, LinkedList<String>>();

		aiProfiles = new LinkedHashMap<String, String>();
	}

	public void setDescription(final String text) {
		this.description = text;
	}

	public String getDescription() {
		return description;
	}

	public void setRPStats(final int hp, final int atk, final int ratk, final int def, final double speed) {
		this.hp = hp;
		this.atk = atk;
		this.ratk = ratk;
		this.def = def;
		this.speed = speed;
	}

	public int getHP() {
		return hp;
	}

	public int getAtk() {
		return atk;
	}

	public int getRatk() {
		return ratk;
	}

	public int getDef() {
		return def;
	}

	public double getSpeed() {
		return speed;
	}

	public void setLevel(final int level, final int xp) {
		this.level = level;
		this.xp = xp;
	}

	public void setRespawnTime(final int respawn) {
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

	public void setSize(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setNoiseLines(final LinkedHashMap<String, LinkedList<String>> creatureSays) {
		this.creatureSays = creatureSays;
	}

	public HashMap<String, LinkedList<String>> getNoiseLines() {
		return creatureSays;
	}

	public void setEquipedItems(final List<EquipItem> equipsItems) {
		this.equipsItems = equipsItems;
	}

	public List<EquipItem> getEquipedItems() {
		return equipsItems;
	}

	public void setBlood(final String name) {
		this.bloodClass = name;
	}

	public void setCorpse(final String name, final String harmless, final int width, final int height) {
		corpseName = name;
		harmlessCorpseName = harmless;
		corpseWidth = width;
		corpseHeight = height;
	}

	public void setDropItems(final List<DropItem> dropsItems) {
		this.dropsItems = dropsItems;
	}

	public List<DropItem> getDropItems() {
		return dropsItems;
	}

	public void setAIProfiles(final Map<String, String> aiProfiles) {
		this.aiProfiles = aiProfiles;
	}

	/**
	 * Set the susceptibility mapping.
	 *
	 * @param susceptibilities creature susceptibilities
	 */
	public void setSusceptibilities(final Map<Nature, Double> susceptibilities) {
		this.susceptibilities = susceptibilities;
	}

	/**
	 * Set the damage types.
	 *
	 * @param type
	 * @param rangedType if <code>null</code>, then melee type is used for both
	 * 	attack modes
	 */
	public void setDamageTypes(Nature type, Nature rangedType) {
		damageType = type;
		rangedDamageType = rangedType;
	}

	/** @return a creature-instance.
	 */
	public Creature getCreature() {
		Collections.sort(dropsItems, new Comparator<DropItem>() {
			@Override
			public int compare(final DropItem o1, final DropItem o2) {
				if (o1.probability < o2.probability) {
					return -1;
				} else if (o1.probability > o2.probability) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		final Creature creature = new Creature(clazz, subclass, name, hp, atk, ratk, def,
				level, xp, width, height, speed, dropsItems, aiProfiles,
				creatureSays, respawn, description);
		creature.equip(equipsItems);

		creature.setCorpse(corpseName, harmlessCorpseName, corpseWidth, corpseHeight);
		creature.setBlood(bloodClass);
		creature.setSusceptibilities(susceptibilities);
		creature.setDamageTypes(damageType, rangedDamageType);
		creature.setSounds(Collections.unmodifiableList(sounds));
		creature.setDeathSound(deathSound);
		creature.setMovementSound(movementSound);

		if (shadow_style != null) {
			creature.setShadowStyle(shadow_style);
		}

		// Status attack types
		if (statusAttack != null) {
			Binding groovyBinding = new Binding();
			final GroovyShell interp = new GroovyShell(groovyBinding);
			try {
				String code = "import games.stendhal.server.entity.status.*;\r\n" + statusAttack;
				StatusAttacker attacker = new StatusAttacker((Status) interp.evaluate(code), statusAttackProbability);
				creature.addStatusAttacker(attacker);
			} catch (CompilationFailedException e) {
				throw new IllegalArgumentException(e);
			}
		}

		return creature;
	}

	/** @return the tileid. */
	public String getTileId() {
		return tileid;
	}

	public void setTileId(final String val) {
		tileid = val;
	}

	/** @return the class. */
	public String getCreatureClass() {
		return clazz;
	}

	public String getCreatureSubclass() {
		return subclass;
	}

	public String getCreatureName() {
		return name;
	}

	public void setCreatureClass(final String val) {
		clazz = val;
	}

	public void setCreatureSubclass(final String val) {
		subclass = val;
	}

	public void setCreatureName(final String val) {
		name = val;
	}

	/**
	 * Set the possible sound names.
	 *
	 * @param sounds list of sounds
	 */
	public void setCreatureSounds(List<String> sounds) {
		this.sounds = sounds;
	}

	/**
	 * Set the sound played when a creature dies
	 *
	 * @param sound Name of sound
	 */
	public void setCreatureDeathSound(String sound) {
	    this.deathSound = sound;
	}

	/**
	 * Set a looped sound effect for creature when moving
	 *
	 * @param sound
	 * 				desired sound effect
	 */
	public void setCreatureMovementSound(String sound) {
		this.movementSound = sound;
	}

	/**
	 *
	 * @param name
	 * @param probability
	 */
	public void setStatusAttack(final String name, final double probability) {
	    statusAttack = name;
	    statusAttackProbability = probability;
	}

	/**
	 * Sets the style of shadow to use for this entity.
	 *
	 * @param style
	 * 		Name of the style.
	 */
	public void setShadowStyle(final String style) {
		shadow_style = style;
	}

	public boolean verifyItems(final EntityManager defaultEntityManager) {
		for (final DropItem item : dropsItems) {
			if (!defaultEntityManager.isItem(item.name)) {
				logger.warn("Item " + item.name + " doesnt exists");
				return false;
			}
		}

		for (final EquipItem item : equipsItems) {
			if (!defaultEntityManager.isItem(item.name)) {
				logger.warn("Item " + item.name + " doesnt exists");
				return false;
			}
		}

		return true;
	}

	public String toXML() {
		final StringBuilder os = new StringBuilder();
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
		os.append("      <ratk value=\"" + ratk + "\"/>\n");
		os.append("      <def value=\"" + def + "\"/>\n");
		os.append("      <hp value=\"" + hp + "\"/>\n");
		os.append("      <speed value=\"" + speed + "\"/>\n");
		os.append("      <size value=\"" + width + "," + height + "\"/>\n");
		os.append("    </attributes>\n");
		os.append("    <level value=\"" + level + "\"/>\n");
		os.append("    <experience value=\"" + (xp / 20) + "\"/>\n");
		os.append("    <respawn value=\"" + respawn + "\"/>\n");
		os.append("    <drops>\n");
		for (final DropItem item : dropsItems) {
			os.append("      <item value=\"" + item.name + "\" quantity=\"["
					+ item.min + "," + item.max + "]\" probability=\""
					+ item.probability + "\"/>\n");
		}
		os.append("    </drops>\n");
		os.append("    <equips>\n");
		for (final EquipItem item : equipsItems) {
			os.append("      <slot name=\"" + item.slot + "\" item=\""
					+ item.name + "\" quantity=\"" + item.quantity + "\"/>\n");
		}
		os.append("    </equips>\n");
		os.append("    <ai>\n");
		if (!creatureSays.isEmpty()) {
			os.append("      <says>\n");

			while(creatureSays.entrySet().iterator().hasNext()) {
				final Entry<String, LinkedList<String>> entry =
					creatureSays.entrySet().iterator().next();
				for (int i=0; i<entry.getValue().size(); i++) {
					os.append("        <noise state=\""+entry.getKey()+
							"\" value=\"" + entry.getValue().get(i) + "\"/>\n");
				}
			}

		os.append("      </says>\n");
		}
		for (final Map.Entry<String, String> entry : aiProfiles.entrySet()) {
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
