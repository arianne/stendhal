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

import games.stendhal.server.config.CreaturesXMLLoader;
import games.stendhal.server.config.ItemGroupsXMLLoader;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.rule.EntityManager;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * entity manager for the default ruleset
 * 
 * @author Matthias Totz
 */
public class DefaultEntityManager implements EntityManager {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(DefaultEntityManager.class);

	/** the singleton instance, lazy initialisation */
	private static DefaultEntityManager manager;

	/** maps the tile ids to the classes */
	private Map<String, String> idToClass;

	/** maps the creature tile-ids to the actual creature enums */
	private Map<String, DefaultCreature> classToCreature;

	/** maps the item names to the actual item enums */
	private Map<String, DefaultItem> classToItem;

	/** lists all creatures that are being used at least once */
	private Map<String, Creature> createdCreature;

	/** lists all items that are being used at least once */
	private Map<String, Item> createdItem;

	/** no public constructor */
	private DefaultEntityManager() {
		idToClass = new HashMap<String, String>();

		// Build the items tables
		classToItem = new HashMap<String, DefaultItem>();
		createdItem = new HashMap<String, Item>();

		try {
			ItemGroupsXMLLoader loader = new ItemGroupsXMLLoader(new URI(
					"/data/conf/items.xml"));
			List<DefaultItem> items = loader.load();

			for (DefaultItem item : items) {
				String clazz = item.getItemName();

				if (classToItem.containsKey(clazz)) {
					logger.warn("Repeated item name: " + clazz);
				}

				classToItem.put(clazz, item);
			}
		} catch (Exception e) {
			logger.error("items.xml could not be loaded", e);
		}

		// Build the creatures tables
		classToCreature = new HashMap<String, DefaultCreature>();
		createdCreature = new HashMap<String, Creature>();

		try {
			CreaturesXMLLoader loader = CreaturesXMLLoader.get();
			List<DefaultCreature> creatures = loader.load("data/conf/creatures.xml");

			for (DefaultCreature creature : creatures) {
				String id = creature.getTileId();
				String clazz = creature.getCreatureName();

				if (classToCreature.containsKey(clazz)) {
					logger.warn("Repeated creature name: " + clazz);
				}

				if (!creature.verifyItems(this)) {
					logger.warn("Items dropped by creature name: " + clazz
							+ " doesn't exists");
				}

				classToCreature.put(clazz, creature);
				idToClass.put(id, clazz);
			}
		} catch (org.xml.sax.SAXException e) {
			e.printStackTrace();
		}
	}

	public boolean addItem(DefaultItem item) {
		String clazz = item.getItemName();

		if (classToItem.containsKey(clazz)) {
			logger.warn("Repeated item name: " + clazz);
			return false;
		}

		classToItem.put(clazz, item);

		return true;
	}

	public boolean addCreature(DefaultCreature creature) {
		String id = creature.getTileId();
		String clazz = creature.getCreatureName();

		if (classToCreature.containsKey(clazz)) {
			logger.warn("Repeated creature name: " + clazz);
		}

		if (!creature.verifyItems(this)) {
			logger.warn("Items dropped by creature name: " + clazz
					+ " doesn't exists");
		}

		classToCreature.put(clazz, creature);
		idToClass.put(id, clazz);

		return true;
	}

	/**
	 * returns a list of all Creatures that are instantiated
	 */
	public Collection<Creature> getCreatures() {
		return createdCreature.values();
	}

	/**
	 * returns a list of all Items that are instantiated
	 */
	public Collection<Item> getItems() {
		return createdItem.values();
	}

	/**
	 * returns the instance of this manager. Note: This method is synchonized.
	 */
	public static synchronized DefaultEntityManager getInstance() {
		if (manager == null) {
			manager = new DefaultEntityManager();
		}
		return manager;
	}

	/**
	 * returns the entity or <code>null</code> if the id is unknown
	 * 
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	public Entity getEntity(String clazz) {
		if (clazz == null) {
			throw new NullPointerException("entity class is null");
		}

		Entity entity;
		// Lookup the id in the creature table
		entity = getCreature(clazz);
		if (entity != null) {
			return entity;
		}

		// Lookup the id in the item table
		entity = getItem(clazz);
		if (entity != null) {
			return entity;
		}

		return null;
	}

	/**
	 * returns the creature or <code>null</code> if the id is unknown
	 */
	public Creature getCreature(String tileset, int id) {
		String clazz = idToClass.get(tileset + ":" + id);
		if (clazz == null) {
			return null;
		}

		return getCreature(clazz);
	}

	/**
	 * returns the creature or <code>null</code> if the clazz is unknown
	 * 
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	public Creature getCreature(String clazz) {
		if (clazz == null) {
			throw new NullPointerException("entity class is null");
		}

		// Lookup the clazz in the creature table
		DefaultCreature creature = classToCreature.get(clazz);
		if (creature != null) {
			if (createdCreature.get(clazz) == null) {
				createdCreature.put(clazz, creature.getCreature());
			}
			return creature.getCreature();
		}

		return null;
	}

	/**
	 * returns the DefaultCreature or <code>null</code> if the clazz is
	 * unknown
	 * 
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	public DefaultCreature getDefaultCreature(String clazz) {
		if (clazz == null) {
			throw new NullPointerException("entity class is null");
		}

		// Lookup the clazz in the creature table
		return classToCreature.get(clazz);
	}

	/** return true if the Entity is a creature */
	public boolean isCreature(String tileset, int id) {
		String clazz = idToClass.get(tileset + ":" + id);
		if (clazz == null) {
			return false;
		}

		return isCreature(clazz);
	}

	/** return true if the Entity is a creature */
	public boolean isCreature(String clazz) {
		if (clazz == null) {
			throw new NullPointerException("entity class is null");
		}
		return classToCreature.containsKey(clazz);
	}

	/** return true if the Entity is a creature */
	public boolean isItem(String clazz) {
		if (clazz == null) {
			throw new NullPointerException("entity class is null");
		}
		return classToItem.containsKey(clazz);
	}

	/**
	 * returns the item or <code>null</code> if the clazz is unknown
	 * 
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	public Item getItem(String clazz) {
		if (clazz == null) {
			throw new NullPointerException("entity class is null");
		}

		// Lookup the clazz in the item table
		DefaultItem item = classToItem.get(clazz);
		if (item != null) {
			if (createdItem.get(clazz) == null) {
				createdItem.put(clazz, item.getItem());
			}
			return item.getItem();
		}

		return null;
	}
}
