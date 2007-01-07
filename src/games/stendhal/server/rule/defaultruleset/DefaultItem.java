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

	/** Is this item type stackable */
	private boolean stackable;

	/** Implementation class */
	protected Class	implementation;

	public DefaultItem(String clazz, String subclazz, String name, int tileid) {
		this.clazz = clazz;
		this.subclazz = subclazz;
		this.name = name;
		this.tileid = tileid;
		this.stackable = false;
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

	public void setStackable() {
		stackable = true;
	}

	public void setDescription(String text) {
		this.description = text;
	}

	public void
	setImplementation(Class implementation) {
		this.implementation = implementation;
	}


	/**
	 * Create the implementation class. It uses the following contructor
	 * search order:<br>
	 *
	 * <ul>
	 *  <li><em>Class</em>(<em>name</em>, <em>clazz</em>, <em>subclazz</em>, <em>attributes</em>)
	 *  <li><em>Class</em>(<em>attributes</em>)
	 *  <li><em>Class</em>()
	 * </ul>
	 *
	 * @return	A new item, or <code>null</code> on error.
	 */
	protected Item
	createItem()
	{
		Constructor	construct;


		/*
		 * For now this searches on the fly. To improve speed, create
		 * a wrapper interface with the various forms up front
		 * [in/from setImplementation()].
		 */

		try
		{
			/*
			 * <Class>(name, clazz, subclazz, attributes)
			 */
			try
			{
				construct = implementation.getConstructor(
					new Class []
					{
						String.class,
						String.class,
						String.class,
						Map.class
					});

				return (Item) construct.newInstance(
					new Object []
					{
						name,
						clazz,
						subclazz,
						attributes
					});
			}
			catch(NoSuchMethodException ex)
			{
			}


			/*
			 * <Class>(attributes)
			 */
			try
			{
				construct = implementation.getConstructor(
					new Class [] { Map.class });

				return (Item) construct.newInstance(
					new Object [] { attributes });
			}
			catch(NoSuchMethodException ex)
			{
			}


			/*
			 * <Class>()
			 */
			return (Item) implementation.newInstance();
		}
		catch(IllegalAccessException ex)
		{
			logger.error("Error creating item: " + name, ex);
		}
		catch(InstantiationException ex)
		{
			logger.error("Error creating item: " + name, ex);
		}
		catch(InvocationTargetException ex)
		{
			logger.error("Error creating item: " + name, ex);
		}
		catch(ClassCastException ex)
		{
			/*
			 * Wrong type (i.e. not [subclass of] Item)
			 */
			logger.error("Implementation for " + name
				+ " [" + implementation.getName()
					+ "] is not an Item class");
		}

		return null;
	}


	/** returns an item-instance */
	public Item getItem() {
		Item item = createItem();

		/*
		 * Safety-net for old code/config (for now)
		 */
		if(item == null)
		{
			logger.warn(
				"Item without defined implementation: " + name);

			if (stackable)
			{
				item = new StackableItem(
					name, clazz, subclazz, attributes);
			}
			else
			{
				item = new Item(
					name, clazz, subclazz, attributes);
			}
		}

		item.setEquipableSlots(slots);
		item.setDescription(description);

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
}
