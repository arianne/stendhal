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

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.ExpressionType;
import games.stendhal.common.parser.WordList;
import games.stendhal.server.core.config.CreatureGroupsXMLLoader;
import games.stendhal.server.core.config.ItemGroupsXMLLoader;
import games.stendhal.server.core.config.ShopsXMLLoader;
import games.stendhal.server.core.config.SpellGroupsXMLLoader;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.spell.Spell;

/**
 * entity manager for the default ruleset.
 *
 * @author Matthias Totz
 */
public class DefaultEntityManager implements EntityManager {

	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(DefaultEntityManager.class);

	/** maps the tile ids to the classes. */
	private final Map<String, String> idToClass;

	/** maps the item names to the actual item enums. */
	private final Map<String, DefaultItem> classToItem;

	/** lists all creatures that are being used at least once. */
	private final Map<String, Creature> createdCreature;

	/** lists all items that are being used at least once . */
	private final Map<String, Item> createdItem;

	/** lists all spell that are being used at least once . */
	private final Map<String, Spell> createdSpell;

	/**
	 * lists all loaded default spells that are usable
	 */
	private final Map<String, DefaultSpell> nameToSpell;

	private LowerCaseMap<DefaultCreature> classToCreature;

	/** no public constructor. */
	public DefaultEntityManager() {
		idToClass = new HashMap<String, String>();
		createdCreature = new HashMap<String, Creature>();
		createdItem = new HashMap<String, Item>();
		createdSpell = new HashMap<String, Spell>();
		classToItem = new HashMap<String, DefaultItem>();
		nameToSpell = new HashMap<String, DefaultSpell>();
		buildItemTables();
		buildCreatureTables();
		buildSpellTables();

		ShopsXMLLoader.get().init();
	}

	/**
	 * builds the spell tables
	 */
	private void buildSpellTables() {
		try {
			final SpellGroupsXMLLoader loader = new SpellGroupsXMLLoader(new URI("/data/conf/spells.xml"));
			List<DefaultSpell> loadedDefaultSpells = loader.load();
			for (DefaultSpell defaultSpell : loadedDefaultSpells) {
				addSpell(defaultSpell);
			}
		} catch (Exception e) {
			LOGGER.error("spells.xml could not be loaded", e);
		}
	}

	/**
	 * Build the items tables
	 */
	private void buildItemTables() {
		try {
			final ItemGroupsXMLLoader loader = new ItemGroupsXMLLoader(new URI("/data/conf/items.xml"));
			final List<DefaultItem> items = loader.load();

			for (final DefaultItem item : items) {
				final String clazz = item.getItemName();

				if (classToItem.containsKey(clazz)) {
					LOGGER.warn("Repeated item name: " + clazz);
				}

				classToItem.put(clazz, item);

				String typeString = ExpressionType.OBJECT;

				if (item.getItemClass().equals("food")) {
					typeString += ExpressionType.SUFFIX_FOOD;
				} else if (item.getItemClass().equals("drink")) {
					typeString += ExpressionType.SUFFIX_FOOD;
					typeString += ExpressionType.SUFFIX_FLUID;
				}

				WordList.getInstance().registerName(item.getItemName(), typeString);
			}
		} catch (final Exception e) {
			LOGGER.error("items.xml could not be loaded", e);
		}
	}

	/**
	 * Build the creatures tables
	 */
	private void buildCreatureTables() {
		classToCreature = new LowerCaseMap<DefaultCreature>();

		final CreatureGroupsXMLLoader loader = new CreatureGroupsXMLLoader("/data/conf/creatures.xml");
		final List<DefaultCreature> creatures = loader.load();

		for (final DefaultCreature creature : creatures) {
			final String id = creature.getTileId();
			final String clazz = creature.getCreatureName();

			if (classToCreature.containsKey(clazz)) {
				LOGGER.warn("Repeated creature name: " + clazz);
			}

			if (!creature.verifyItems(this)) {
				LOGGER.warn("Items dropped by creature name: " + clazz + " doesn't exists");
			}

			classToCreature.put(clazz, creature);
			idToClass.put(id, clazz);

			WordList.getInstance().registerName(creature.getCreatureName(), ExpressionType.SUBJECT);
		}
	}

	@Override
	public boolean addItem(final DefaultItem item) {
		final String clazz = item.getItemName();

		if (classToItem.containsKey(clazz)) {
			LOGGER.warn("Repeated item name: " + clazz);
			return false;
		}

		classToItem.put(clazz, item);

		return true;
	}

	@Override
	public boolean addCreature(final DefaultCreature creature) {
		final String id = creature.getTileId();
		final String clazz = creature.getCreatureName();

		if (classToCreature.containsKey(clazz)) {
			LOGGER.warn("Repeated creature name: " + clazz);
		}

		if (!creature.verifyItems(this)) {
			LOGGER.warn("Items dropped by creature name: " + clazz + " doesn't exists");
		}
		classToCreature.put(clazz, creature);
		idToClass.put(id, clazz);

		return true;
	}

	/**
	 * For manually populating the creature list.
	 *
	 * Useful for tests.
	 */
	@Override
	public void populateCreatureList() {
		for (final DefaultCreature cr: getDefaultCreatures()) {
			createdCreature.put(cr.getCreatureName(), cr.getCreature());
		}
	}

	@Override
	public boolean addSpell(DefaultSpell spell) {
		if(nameToSpell.containsKey(spell.getName())) {
			LOGGER.warn("Repeated spell name: "+ spell.getName());
		}
		nameToSpell.put(spell.getName(), spell);
		return true;
	}

	/**
	 * @return a list of all Creatures that are instantiated.
	 */
	@Override
	public Collection<Creature> getCreatures() {
		return createdCreature.values();
	}

	/**
	 * @return a list of all Items that are instantiated.
	 */
	@Override
	public Collection<Item> getItems() {
		return createdItem.values();
	}

	/**
	 * returns the entity or <code>null</code> if the id is unknown.
	 *
	 * @param clazz
	 *            RPClass
	 * @return the new created entity or null if class not found
	 *
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	@Override
	public Entity getEntity(final String clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("entity class is null");
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
	 * @param tileset
	 * @param id
	 * @return the creature or <code>null</code> if the id is unknown.
	 */
	@Override
	public Creature getCreature(final String tileset, final int id) {
		final String clazz = idToClass.get(tileset + ":" + id);
		if (clazz == null) {
			return null;
		}

		return getCreature(clazz);
	}

	/**
	 * @param clazz
	 * @return the creature or <code>null</code> if the clazz is unknown.
	 *
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	@Override
	public Creature getCreature(final String clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("entity class is null");
		}

		// Lookup the clazz in the creature table
		final DefaultCreature creature = classToCreature.get(clazz);
		if (creature != null) {
			if (createdCreature.get(clazz) == null) {
				createdCreature.put(clazz, creature.getCreature());
			}
			return creature.getCreature();
		}

		return null;
	}

	/**
	 * @param clazz
	 * @return the DefaultCreature or <code>null</code> if the clazz is
	 *         unknown.
	 *
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	@Override
	public DefaultCreature getDefaultCreature(final String clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("entity class is null");
		}

		// Lookup the clazz in the creature table
		return classToCreature.get(clazz);
	}

	/** @param tileset
	 * @param id
	 * @return true if the Entity is a creature. */
	@Override
	public boolean isCreature(final String tileset, final int id) {
		final String clazz = idToClass.get(tileset + ":" + id);
		if (clazz == null) {
			return false;
		}

		return isCreature(clazz);
	}

	/** @param clazz
	 * @return true if the Entity is a creature . */
	@Override
	public boolean isCreature(final String clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("entity class is null");
		}

		return classToCreature.containsKey(clazz);
	}

	/** @param clazz
	 * @return true if the Entity is a creature. */
	@Override
	public boolean isItem(final String clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("entity class is null");
		}

		return classToItem.containsKey(clazz);
	}

	/**
	 * @param clazz
	 * @return the item or <code>null</code> if the clazz is unknown.
	 *
	 * @throws NullPointerException
	 *             if clazz is <code>null</code>
	 */
	@Override
	public Item getItem(final String clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("entity class is null");
		}

		// Lookup the clazz in the item table
		final DefaultItem item = classToItem.get(clazz);
		if (item != null) {
			if (createdItem.get(clazz) == null) {
				createdItem.put(clazz, item.getItem());
			}
			return item.getItem();
		}

		return null;
	}

	@Override
	public Spell getSpell(String spell) {
		if(spell == null) {
			throw new IllegalArgumentException("spell name is null");
		}
		DefaultSpell defaultSpell = nameToSpell.get(spell);
		if (defaultSpell != null) {
			Spell spellEntity = defaultSpell.getSpell();
			if(!createdSpell.containsKey(spell)) {
				createdSpell.put(spell, spellEntity);
			}
			return spellEntity;
		}
		return null;
	}

	@Override
	public boolean isSpell(String spellName) {
		return nameToSpell.containsKey(spellName);
	}

	@Override
	public Collection<Spell> getSpells() {
		return createdSpell.values();
	}

	@Override
	public Collection<DefaultCreature> getDefaultCreatures() {
		return classToCreature.values();
	}

	@Override
	public Collection<DefaultItem> getDefaultItems() {
		return classToItem.values();
	}

	public Collection<String> getConfiguredItems() {
		return classToItem.keySet();
	}

	@Override
	public Collection<String> getConfiguredSpells() {
		return nameToSpell.keySet();
	}
}
