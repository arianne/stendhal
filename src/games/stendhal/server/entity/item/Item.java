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
package games.stendhal.server.entity.item;
import games.stendhal.server.entity.PassiveEntity;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;

/**
 * This is an item.
 */
public class Item extends PassiveEntity
  {
  
  /** list of possible slots for this item */
  private List<String> possibleSlots;

  public static void generateRPClass()
    {
    RPClass entity=new RPClass("item");
    entity.isA("entity");
    entity.add("class",RPClass.STRING); // class, sword/armor/...
    entity.add("subclass",RPClass.STRING); // subclass, long sword/leather armor/...
    entity.add("name",RPClass.STRING);  // name of item (ie 'Kings Sword')
    entity.add("possibleslots",RPClass.STRING); // komma separated list of slots
    entity.add("atk",RPClass.SHORT);  // Some items has attack values
    entity.add("def",RPClass.SHORT);  // Some items has defense values
    entity.add("quantity",RPClass.INT); // Some items has quantity
    }

  /**
   * 
   * Creates a new Item.
   * @param name name of item
   * @param clazz class (or type) od item
   * @param slots slots where this item may be equipped. may be empty
   * @param attributes attributes (like attack). may be empty or <code>null</code>
   */
  public Item(String name, String clazz, String subclass, List<String> slots, Map<String, String> attributes)
  {
    this();
    put("class",clazz);
    put("subclass",subclass);
    put("name",name);
    // save slots
    possibleSlots = slots;

    if (attributes != null)
    {
      // store all attributes
      for (String key : attributes.keySet())
      {
        put(key,attributes.get(key));
      }
    }
  }

  /** no public 'default' item */
  private Item() throws AttributeNotFoundException
    {
    super();
    put("type","item");
    update();
    }

  /** copy constuctor */
  private Item(Item other) throws AttributeNotFoundException
    {
    super(other);
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }
  
  /**
   * Returns the attack points of this item. Positive and negative values are
   * allowed. If this item doesn't modify the attack it should return '0'.
   * @return attack points 
   */
  public int getAttack()
  {
    if (has("atk"))
      return getInt("atk");

    return 0;
  }

  /**
   * Returns the defense points of this item. Positive and negative values are
   * allowed. If this item doesn't modify the defense it should return '0'.
   * @return defense points 
   */
  public int getDefense()
  {
    if (has("def"))
      return getInt("def");

    return 0;
  }
  
  /**
   * Checks if the item is of type <i>type</i>
   * @param type the type to check
   * @return true if the type matches, else false 
   */
  public boolean isOfClass(String clazz)
  {
    return getItemClass().equals(clazz);
  }

  /** returns the type of the item */
  public String getItemClass()
  {
    if (has("class"))
      return get("class");

    throw new IllegalStateException("the item does not have a class: "+this);
  }

  /** returns the type of the item */
  public String getItemSubclass()
  {
    if (has("class"))
      return get("class");

    throw new IllegalStateException("the item does not have a class: "+this);
  }
  
  /** returns the name of the item */
  public String getName()
  {
    return get("name");
  }
  
  /** returns the list of possible slots for this item */
  public List<String> getPossibleSlots()
  {
    return possibleSlots;
  }
  
  /** creates a copy */
  public Object copy()
  {
    return new Item(this);
  }
  
  public String toString()
  {
    return "Item, "+super.toString();
  }

  }
