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
package games.stendhal.server.rule;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPSlot;
import games.stendhal.server.entity.item.Item;

/**
 * Ruleset Interface for processing actions in Stendhal.
 *
 * @author Matthias Totz
 */
public interface ActionManager
  {
  /** PRE 0.40 */
  String canEquip(RPEntity entity, Item item);  
  boolean onEquip(RPEntity entity, String slotName, Item item);

//  /* Version 0.40 Action Manager new interface */
//  /** called when attacking a target with the given weapon. */
//  boolean onAttack(RPEntity source, RPEntity target, Item weapon);
//  /** called when we stop attacking target */
//  void onStopAttack(RPEntity source, RPEntity target);
//  /** called when entity is damaged with the damage type (physical, fire, poison, magical, ... ) and the amount of damage done. */
//  boolean onDamage(RPEntity source, RPEntity target, int amount);
//  /** called when entity kills target  */
//  boolean onKill(RPEntity killer, RPEntity killed);
//  
//  /** called when entity moves to x,y ( before moving in fact ) */
//  boolean onMove(RPEntity source, int x, int y);
//  /** called when entity collide at position x,y */
//  boolean onCollide(RPEntity source, int x, int y);
//  /** called when entity collide with another entity */
//  boolean onCollideWith(RPEntity source, Entity target);
//  
//  /** called when entity equips item on the given slot */
//  boolean onEquip(RPEntity source, RPSlot slot, Item item);
//  /** called when entity drops item from the given slot to floor */ 
//  boolean onDrop(RPEntity source, RPSlot slot, Item item);
//  
//  /** called when someone speaks near entity or write to entity */
//  void onChat(RPEntity source, String text);
//  /** called when someone speaks near entity or write to entity */
//  void onTell(RPEntity source, RPEntity target, String text);
//  
//  /** called when entity use item */
//  boolean onUse(RPEntity source, Item item);
//  
//  /** called when entity stops moving, attacking, etc... */
//  boolean onStop(RPEntity source);
  }
