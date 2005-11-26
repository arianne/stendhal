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
package games.stendhal.server.actions;


import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Food;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Portal;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class Use extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

  public static void register()
    {
    StendhalRPRuleProcessor.register("use",new Use());
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"use");

    if(action.has("baseitem") && action.has("baseobject") && action.has("baseslot"))
      {
      System.out.println (action);
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
        
      int baseObject=action.getInt("baseobject");

      RPObject.ID baseobjectid=new RPObject.ID(baseObject, zone.getID());
      if(!zone.has(baseobjectid))
        {
        return;
        }

      RPObject base=zone.get(baseobjectid);
      if(!(base instanceof Player || base instanceof Corpse || base instanceof Chest))
        {
        // Only allow to use objects from players, corpses or chests 
        return;
        }

      if(base instanceof Player && !player.getID().equals(base.getID()))
        {
        // Only allowed to use item of our own player.
        return;
        }        

      Entity baseEntity=(Entity)base;

      if(baseEntity.hasSlot(action.get("baseslot")))
        {
        RPSlot slot=baseEntity.getSlot(action.get("baseslot"));
        
        if(slot.size()==0)
          {
          return;
          }
          
        RPObject object = null;
        int item = action.getInt("baseitem");
        // scan through the slot to find the requested item
        for(RPObject rpobject : slot)
          {
          if(rpobject.getID().getObjectID() == item)
            {
            object = rpobject;
            break;
            }
          }
        
        // no item found...we take the first one
        if(object==null)
          {
          object = slot.iterator().next();
          }
        
        // It is always an entity
        Entity entity=(Entity)object;
        
        if(entity instanceof Food)
          {
          /* This will happen when item is on the player's slot */
          player.eat((Food)entity);
          world.modify(player);
          }
        }
      }
    else if(action.has("target"))
      {
      int usedObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(usedObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof Portal)
          {
          Portal portal=(Portal)object;

          if(StendhalRPAction.usePortal(player, portal))
            {
            StendhalRPAction.transferContent(player);
            }
          }
        else if(object instanceof Food)
          {
          /* This will happen when item is on the floor */
          player.eat((Food)object);
          world.modify(player);
          }
        else if(object instanceof Chest)
          {          
          Chest chest=(Chest)object;
          
          if(player.nextto(chest,0.25))
            {
            if(chest.isOpen())
              {
              chest.close();
              }
            else
              {
              chest.open();
              }
            
            world.modify(chest);
            }
          }
        }
      }

    Log4J.startMethod(logger,"use");
    }
  }
