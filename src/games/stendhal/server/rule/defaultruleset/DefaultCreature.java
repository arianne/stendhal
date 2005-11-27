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
import games.stendhal.server.rule.EntityManager;
import games.stendhal.server.entity.item.Item;

import java.util.List;
import java.util.LinkedList;
import marauroa.common.Pair;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;

public class DefaultCreature
{
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(DefaultCreature.class);

  /** Creature class */
  private String clazz;
  /** Creature subclass */
  private String subclass;
  /** Creature name */
  private String name;
  
  /** Map Tile Id */
  private int tileid;
  /** hitpoints */
  private int hp;
  /** Attack points */
  private int atk;
  /** defense points */
  private int def;
  
  /** experience points for killing this creature*/
  private int xp;
  private int level;
  
  /** size of the creature.*/
  private int width;
  private int height;

  /** Ths list of items this creature may drop */
  private List<Creature.DropItem> dropsItems;
  
  /** speed relative to player [0.0 ... 1.0]*/
  private double speed;
  
  public DefaultCreature(String clazz, String subclass, String name, int tileid, int hp, int attack, int defense, int level, int xp, int width, int height, double speed, List<Creature.DropItem> dropsItems)
  {
    this.clazz = clazz;
    this.subclass = subclass;
    this.name = name;
    
    this.tileid = tileid;
    this.hp = hp;
    this.atk = attack;
    this.def = defense;
    
    this.level=level;
    this.xp = xp;
    
    this.width = width;
    this.height = height;
    this.speed = speed;
    
    this.dropsItems=dropsItems;
  }
  
  /** returns a creature-instance */
  public Creature getCreature()
  {
    return new Creature(clazz, subclass, name, hp, atk, def, level, xp, width, height, speed, dropsItems);
  }
  
  /** returns the tileid */
  public int getTileId()
  {
    return tileid;
  }
  
  /** returns the class */
  public String  getCreatureClass()
  {
    return clazz;
  }

  public String  getCreatureName()
  {
    return name;
  }
 
  public boolean verifyDroppedItems(EntityManager manager)
  {
    for(Creature.DropItem item: dropsItems)
    {
      if(!manager.isItem(item.name))
      {
        logger.warn("Item "+item.name+" doesnt exists");
        return false;  
      }      
    }
  
  return true;
  }
}
