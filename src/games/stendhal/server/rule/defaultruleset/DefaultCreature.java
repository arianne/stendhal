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

public class DefaultCreature
{
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
  
  /** speed relative to player [0.0 ... 1.0]*/
  private double speed;
  
  public DefaultCreature(String clazz, String subclass, String name, int tileid, int hp, int attack, int defense, int level, int xp, int width, int height, double speed)
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
  }
  
  /** returns a creature-instance */
  public Creature getCreature()
  {
    return new Creature(clazz, hp, atk, def, xp, width, height, speed);
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
}
