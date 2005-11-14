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

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.rule.ActionManager;
import marauroa.common.game.RPSlot;

/**
 *
 * @author Matthias Totz
 */
public class DefaultActionManager implements ActionManager
{
  
  /** the singleton instance, lazy initialisation */
  private static DefaultActionManager manager;
  
  /** no public constuctor */
  private DefaultActionManager()
  {
  }

  /** 
   * returns the instance of this manager.
   * Note: This method is synchonized.
   */
  public static synchronized DefaultActionManager getInstance()
  {
    if (manager == null)
    {
      manager = new DefaultActionManager();
    }
    return manager;
  }
  
  /**
   * returns the slot the entity can equip the item. This checks if the entity 
   * has a slot for this item, not if the slot is used already.
   *
   * @return the slot name for the item or null if there is no matching slot
   *                 in the entity
   */
  public String canEquip(RPEntity entity, Item item)
  {
    // get all possible slots for this item
    String[] slots = item.getPossibleSlots();

    for (String slot : slots)
    {
      if (entity.hasSlot(slot))
        {
        RPSlot rpslot=entity.getSlot(slot);
        if(!rpslot.isFull())   
          {
          return slot;
          }
        }
        
    }
    return null;
  }

  /** equipes the item in the specified slot */
  public boolean onEquip(RPEntity entity, String slotName, Item item)
  {
    if (!entity.hasSlot(slotName))
      return false;

// TODO: 
//    // recheck if the item can be equipped
//    if (!item.getPossibleSlots().contains(slotName))
//    {
//      logger.warn("tried to equip the item ["+item.getName()+"] in a nonsupported slot ["+slotName+"]");
//      return false;
//    }
    
    RPSlot slot = entity.getSlot(slotName);
    slot.assignValidID(item);
    slot.add(item);
    return true;
  }

}
