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

import games.stendhal.common.Pair;
import games.stendhal.server.entity.item.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

/**
 * All default items which can be reduced to stuff that increase the attack
 * point and stuff that increase the defense points
 * 
 * @author Matthias Totz
 */
public class DefaultItem {
	private static final Logger logger = Log4J.getLogger(DefaultItem.class);

	/** items class */
	private String clazz;

	/** items sub class */
	private String subclazz;

	/** items type */
	private String name;

	/** optional item description * */
	private String description;

	// weight system is not yet implemented.
	@SuppressWarnings("unused")
	private double weight;

	/** slots where this item can be equiped */
	private List<String> slots;

	/** Map Tile Id */
	private int tileid;

	/** Attributes of the item */
	private Map<String, String> attributes;

	/** Implementation creator */
	protected Creator creator;

	public DefaultItem(String clazz, String subclazz, String name, int tileid) {
		this.clazz = clazz;
		this.subclazz = subclazz;
		this.name = name;
		this.tileid = tileid;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setAttributes(Pair<String, String> attribute) {
		this.attributes = new HashMap<String, String>();
		this.attributes.put(attribute.first(), attribute.second());
	}

	public void setAttributes(List<Pair<String, String>> attributes) {
		this.attributes = new HashMap<String, String>();
		for (Pair<String, String> attribute : attributes) {
			this.attributes.put(attribute.first(), attribute.second());
		}
	}

	public void setEquipableSlots(List<String> slots) {
		this.slots = slots;
	}

	public void setDescription(String text) {
		this.description = text;
	}

	public void setImplementation(Class implementation) {
		creator = buildCreator(implementation);
	}


	/**
	 * Build a creator for the class. It uses the following constructor
	 * search order:<br>
	 *
	 * <ul>
	 *  <li><em>Class</em>(<em>name</em>, <em>clazz</em>, <em>subclazz</em>, <em>attributes</em>)
	 *  <li><em>Class</em>(<em>attributes</em>)
	 *  <li><em>Class</em>()
	 * </ul>
	 *
	 * @param	implementation	The implementation class.
	 *
	 * @return	A creator, or <code>null</code> if none found.
	 */
	protected Creator buildCreator(Class implementation) {
		Constructor construct;


		/*
		 * <Class>(name, clazz, subclazz, attributes)
		 */
		try {
			construct = implementation.getConstructor(new Class[] { String.class, String.class, String.class, Map.class });

			return new FullCreator(construct);
		} catch (NoSuchMethodException ex) {
		}


		/*
		 * <Class>(attributes)
		 */
		try {
			construct = implementation.getConstructor(new Class[] { Map.class });

			return new AttributesCreator(construct);
		} catch (NoSuchMethodException ex) {
		}


		/*
		 * <Class>()
		 */
		try {
			construct = implementation.getConstructor(new Class[] {});

			return new DefaultCreator(construct);
		} catch (NoSuchMethodException ex) {
		}


		return null;
	}


	/**
	 * Returns an item-instance.
	 *
	 * @return	An item, or <code>null</code> on error.
	 */
	public Item getItem() {
		Item item;

		/*
		 * Just incase - Really should generate fatal error up front
		 * (in ItemXMLLoader).
		 */
		if (creator == null) return null;

		if ((item = creator.createItem()) != null) {
			item.setEquipableSlots(slots);
			item.setDescription(description);
		}

		return item;
	}

	/** returns the tileid */
	public int getTileId() {
		return tileid;
	}

	/** returns the class */
	public String getItemClass() {
		return clazz;
	}

	public String getItemName() {
		return name;
	}

	//
	//

	/**
	 * Base item creator (using a constructor)
	 */
	protected abstract class Creator {
		protected Constructor construct;


		public Creator(Constructor construct) {
			this.construct = construct;
		}


		protected abstract Object create() throws IllegalAccessException, InstantiationException, InvocationTargetException;


		public Item createItem() {
			try {
				return (Item) create();
			} catch (IllegalAccessException ex) {
				logger.error("Error creating item: " + name, ex);
			} catch (InstantiationException ex) {
				logger.error("Error creating item: " + name, ex);
			} catch (InvocationTargetException ex) {
				logger.error("Error creating item: " + name, ex);
			} catch (ClassCastException ex) {
				/*
				 * Wrong type (i.e. not [subclass of] Item)
				 */
				logger.error("Implementation for " + name + " is not an Item class");
			}

			return null;
		}
	}


	/**
	 * Create an item class via the <em>attributes</em> constructor.
	 */
	protected class AttributesCreator extends Creator {
		public AttributesCreator(Constructor construct) {
			super(construct);
		}


		@Override
		protected Object create() throws IllegalAccessException, InstantiationException, InvocationTargetException {
			return construct.newInstance(new Object[] { attributes });
		}
	}


	/**
	 * Create an item class via the default constructor.
	 */
	protected class DefaultCreator extends Creator {
		public DefaultCreator(Constructor construct) {
			super(construct);
		}

		@Override
		protected Object create() throws IllegalAccessException, InstantiationException, InvocationTargetException {
			// XXX - Is this a fast as <Class>.newInstance()
			return construct.newInstance(new Object[] {});
		}
	}


	/**
	 * Create an item class via the full arguments (<em>name, clazz,
	 * subclazz, attributes</em>) constructor.
	 */
	protected class FullCreator extends Creator {
		public FullCreator(Constructor construct) {
			super(construct);
		}

		@Override
		protected Object create() throws IllegalAccessException, InstantiationException, InvocationTargetException {
			return construct.newInstance(new Object[] { name, clazz, subclazz, attributes });
		}
	}
}
