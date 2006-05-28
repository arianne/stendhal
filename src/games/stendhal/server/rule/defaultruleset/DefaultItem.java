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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All default items which can be reduced to stuff that increase the attack
 * point and stuff that increase the defense points
 * 
 * @author Matthias Totz
 */
public class DefaultItem {
	/** items class */
	private String clazz;

	/** items sub class */
	private String subclazz;

	/** items type */
	private String name;

	/** optional item description * */
	private String description;

	private double weight;

	/** slots where this item can be equiped */
	private List<String> slots;

	/** Map Tile Id */
	private int tileid;

	/** Attributes of the item */
	private Map<String, String> attributes;

	/** Is this item type stackable */
	private boolean stackable;

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

	/** returns an item-instance */
	public Item getItem() {
		Item item = null;

		if (clazz.equals("money")) {
			item = new Money(attributes);
		} else if (clazz.equals("food")) {
			item = new Food(name, clazz, subclazz, attributes);
		} else if (clazz.equals("drink")) {
			item = new Drink(name, clazz, subclazz, attributes);
		} else if (clazz.equals("scroll")) {
			item = new Scroll(name, clazz, subclazz, attributes);
		} else if (stackable) {
			item = new StackableItem(name, clazz, subclazz, attributes);
		} else {
			item = new Item(name, clazz, subclazz, attributes);
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
