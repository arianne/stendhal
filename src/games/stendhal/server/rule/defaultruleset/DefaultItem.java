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
 * @author Matthias Totz
 */
public class DefaultItem
{
  /** items class */
  private String clazz;
  /** items sub class */
  private String subclazz;
  /** items type */
  private String name;
  
  private double weight;
  /** slots where this item can be equiped */
  private List<String> slots;
  /** Map Tile Id */
  private int tileid;
  /** Attributes of the item */
  private Map<String, String> attributes;
  /** Is this item type stackable */
  private boolean stackable;
  
  public DefaultItem(String clazz, String subclazz, String name, int tileid)
  {
    this.clazz = clazz;
    this.subclazz = subclazz;
    this.name = name;
    this.tileid = tileid;
    this.stackable=false;
  }
  
  public void setWeight(double weight)
  {
    this.weight=weight;
  }
  
  public void setAttributes(Pair<String, String> attribute)
  {
    this.attributes = new HashMap<String, String>();
    this.attributes.put(attribute.first(), attribute.second());
  }

  public void setAttributes(List<Pair<String, String>> attributes)
  {
    this.attributes = new HashMap<String, String>();
    for (Pair<String, String> attribute : attributes)
    {
      this.attributes.put(attribute.first(), attribute.second());
    }
  }
  
  public void setConteinableSlots(List<String> slots)
  {
    this.slots=slots;
  }
  
  public void setStackable()
  {
    stackable=true;
  }

  /** returns an item-instance */
  public Item getItem()
    {
    if(clazz.equals("money"))
      {
      return new Money(slots, attributes);
      }
    else if(clazz.equals("food"))
      {
      return new Food(name, clazz, subclazz, slots, attributes);
      }
    else if(clazz.equals("drink"))
      {
      return new Drink(name, clazz, subclazz, slots, attributes);
      }
    else if(stackable)
      {
      return new StackableItem(name, clazz, subclazz, slots, attributes);
      }
    else
      {
      return new Item(name, clazz, subclazz, slots, attributes);
      }
    }
  
  /** returns the tileid */
  public int getTileId()
  {
    return tileid;
  }
  
  /** returns the class */
  public String  getItemClass()
  {
    return clazz;
  }
  
  public String  getItemName()
  {
    return name;
  }
}
