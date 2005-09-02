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
import games.stendhal.server.entity.item.Equipable;
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

  /** return true if there is a free slot for this item */
  public boolean onEquip(RPEntity entity, Equipable item)
  {
    // only item can be equiped at the moment
    if (!(item instanceof Item))
    {
      return false;
    }

    // get all possible slots for this item
    String[] slots = ((Equipable) item).getPossibleSlots();
    
    for (String slot : slots)
    {
      if (entity.hasSlot(slot))
      {
        RPSlot rpslot = entity.getSlot(slot);
        if (rpslot.size() == 0)
        {
          rpslot.add((Item) item);
          return true;
        }
      }
    }
    // No free slot found or no slot at all
    return false;
  }

  public void onTalk()
  {
  }

  public void onAttack()
  {
  }
  
}
