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
public enum DefaultItem
{
//       class    ,  name,     all possible slots,         tileid,  properties
  SWORD ("sword" , "weapon", new String[] {"rhand","lhand"},       -1, new Pair<String, String>("atk","14")),
  CLUB  ("club"  , "weapon", new String[] {"rhand","lhand"},       -1, new Pair<String, String>("atk","7")),
  ARMOR ("armor" , "armor" , new String[] {"armor"}        ,       -1, new Pair<String, String>("def","14")),
  SHIELD("shield", "armor" , new String[] {"lhand","rhand"},       -1, new Pair<String, String>("def","7")),
  MONEY ("money",  "money" , new String[] {"lhand","rhand","bag"}, -1, new Pair<String, String>("quantity","1"));

  /** items class */
  private String clazz;
  /** items type */
  private String name;
  /** slots where this item can be equiped */
  private String[] slots;
  /** Map Tile Id */
  private int tileid;
  /** Attributes of the item */
  private Map<String, String> attributes;
  
  DefaultItem(String clazz, String name, String[] slots, int tileid, Pair<String, String> attribute)
  {
    this.clazz = clazz;
    this.name = name;
    this.slots = slots;
    this.tileid = tileid;
    this.attributes = new HashMap<String, String>();
    this.attributes.put(attribute.first(), attribute.second());
  }

  DefaultItem(String clazz, String name, String[] slots, int tileid, List<Pair<String, String>> attributes)
  {
    this.clazz = clazz;
    this.name = name;
    this.slots = slots;
    this.tileid = tileid;
    this.attributes = new HashMap<String, String>();
    for (Pair<String, String> attribute : attributes)
    {
      this.attributes.put(attribute.first(), attribute.second());
    }
  }
  
  /** returns an item-instance */
  public Item getItem()
    {
    if(clazz.equals("money"))
      {
      return new Money(name, clazz, slots, attributes);
      }
    else
      {
      return new Item(name, clazz, slots, attributes);
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
  
}
