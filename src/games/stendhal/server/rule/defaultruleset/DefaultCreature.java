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

/**
 * Enum with all default creatures. This emum may be replaced with an external
 * configuration file.
 *
 * @author Matthias Totz
 */
public enum DefaultCreature
{
//                  ("clazz",       tileid,  HP, ATK, DEF, XP,size,SPEED);
  BOAR              ("boar"           , 20,  30,  8,  5,   300, 1, 0.5),
  CAVERAT           ("caverat"        , 13,  30,  7,  3,   200, 0, 0.5),
  COBRA             ("cobra"          , 15,  15, 15,  1,   200, 1, 1.0),
  GARGOYLE          ("gargoyle"       , 17, 100, 28, 15,  2160, 1, 0.5),
  DEMON_ORC         ("demon_orc"      , -1,  70, 25, 14,   940, 1, 1.0),
  DEMON_ORCHUNTER   ("demon_orchunter", -1,  70, 25, 14,   940, 1, 1.0),
  DEMON_ORCLORD     ("demon_orclord"  , -1,  70, 25, 14,   940, 1, 1.0),
  DEMON_ORCWARRIOR  ("demon_orcwar"   , -1,  70, 25, 14,   940, 1, 1.0),
  GIANTRAT          ("giantrat"       , 23, 800, 70, 30, 90000, 0, 0.7),
  GOBLIN            ("goblin"         , 22,  50, 20, 10,   580, 1, 0.5),
  KOBOLD            ("kobold"         , 19,  30, 15,  8,   400, 1, 0.5),
  OGRE              ("ogre"           , 18, 200, 30, 17,  2200, 1, 0.2),
  ORC               ("orc"            , 16,  70, 25, 14,   940, 1, 1.0),
  ORCHUNTER         ("orchunter"      , -1,  70, 25, 14,   940, 1, 1.0),
  ORCLORD           ("orclord"        , -1,  70, 25, 14,   940, 1, 1.0),
  ORCWARRIOR        ("orcwar"         , -1,  70, 25, 14,   940, 1, 1.0),
  RAT               ("rat"            , 12,  20,  3,  2,   100, 0, 0.5),
  TROLL             ("troll"          , 21,  40, 17,  9,   500, 1, 0.4),
  WOLF              ("wolf"           , 14,  30,  9,  4,   360, 1, 0.5);

  /** Creature class */
  private String clazz;
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
  /** size [0,1]*/
  private int size;
  /** speed relative to player [0.0 ... 1.0]*/
  private double speed;

  DefaultCreature(String clazz, int tileid, int hp, int attack, int defense, int xp, int size, double speed)
  {
    this.clazz = clazz;
    this.tileid = tileid;
    this.hp = hp;
    this.atk = attack;
    this.def = defense;
    this.xp = xp;
    this.size = size;
    this.speed = speed;
  }

  /** returns a creature-instance */
  public Creature getCreature()
  {
    return new Creature(clazz, hp, atk, def, xp, size, speed);
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
