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


import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Money;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class Equipment extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

  public static void register()
    {
    Equipment equip=new Equipment();
    StendhalRPRuleProcessor.register("equip",equip);
    StendhalRPRuleProcessor.register("drop",equip);    
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    if(action.get("type").equals("equip"))
      {
      onEquip(world,rules,player,action);
      }
    else
      {
      onDrop(world,rules,player,action);
      }    
    }
  
  private void onEquip(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"equip");    

    if(action.has("baseitem") && action.has("targetobject") && action.has("targetslot"))
      {
       // The base item
      Entity base=null;
      // The container that contains base item, a player, a chest, etc...
      Entity baseObject=null;
      // The slot of the container where base item is stored
      RPSlot baseSlot=null;
      
      if(action.has("baseobject") && action.has("baseslot"))
        {
        // Taking the item from another object or player
        int id=action.getInt("baseobject");

        StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
        RPObject.ID objectid=new RPObject.ID(id, zone.getID());
        if(!zone.has(objectid))
          {
          logger.debug("Rejected because zone doesn't have base object");
          logger.debug(action);
          return;
          }


        RPObject object=zone.get(objectid);
        if(!(object instanceof Player || 
             object instanceof Chest  ||
             object instanceof Corpse))
          {
          logger.debug("Rejected because base object isn't a passive entity or a player");
          logger.debug(object);
          logger.debug(action);
          return;
          }
        
        baseObject=(Entity)object;

        if(baseObject instanceof Player && !player.getID().equals(baseObject.getID()))
          {
          // Only allowed to equip our own player.
          logger.debug("Rejected because base object is a player that is not yours");
          logger.debug(baseObject);
          logger.debug(action);
          return;
          }

        if(!baseObject.hasSlot(action.get("baseslot")))
          {
          logger.debug("Rejected because base object don't have slot base slot");
          logger.debug(action);
          return;
          }        

        baseSlot=baseObject.getSlot(action.get("baseslot"));
        
        int baseItem=action.getInt("baseitem");
        for(RPObject item: baseSlot)
          {
          if(item.getID().getObjectID()==baseItem)
            {
            base=(Entity)item;            
            break;
            }
          }        
        
        if(base==null)
          {
          logger.debug("Rejected because base item isn't stored inside base slot");
          logger.debug(baseObject);
          logger.debug(action);
          return;
          }
        }
      else
        {
        // Taking item from floor
        int id=action.getInt("baseitem");

        StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
        RPObject.ID objectid=new RPObject.ID(id, zone.getID());
        if(!zone.has(objectid))
          {
          logger.debug("Rejected because zone doesn't have base item");
          logger.debug(action);
          return;
          }


        RPObject object=zone.get(objectid);
        if(!(object instanceof PassiveEntity))
          {
          logger.debug("Rejected because base item isn't a passive entity");
          logger.debug(object);
          logger.debug(action);
          return;
          }
        
        base=(Entity)object;
        }
        

      Entity target=null;
      // The container that contains target item, a player, a chest, etc...
      Entity targetObject=null;
      // The slot of the container where target item is stored
      RPSlot targetSlot=null;
      
      // Taking the item from another object or player
      int id=action.getInt("targetobject");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID objectid=new RPObject.ID(id, zone.getID());
      if(!zone.has(objectid))
        {
        logger.debug("Rejected because zone doesn't have target object");
        logger.debug(action);
        return;
        }


      RPObject object=zone.get(objectid);
      if(!(object instanceof Player || 
           object instanceof Chest  ||
           object instanceof Corpse))
        {
        logger.debug("Rejected because target object isn't a passive entity or a player");
        logger.debug(object);
        logger.debug(action);
        return;
        }
      
      targetObject=(Entity)object;

      if(targetObject instanceof Player && !player.getID().equals(targetObject.getID()))
        {
        // Only allowed to equip our own player.
        logger.debug("Rejected because target object is a player that is not yours");
        logger.debug(targetObject);
        logger.debug(action);
        return;
        }

      if(!targetObject.hasSlot(action.get("targetslot")))
        {
        logger.debug("Rejected because target object don't have slot target slot");
        logger.debug(action);
        return;
        }        

      targetSlot=targetObject.getSlot(action.get("targetslot"));
      
      if(action.has("targetitem"))
        {
        int targetItem=action.getInt("targetitem");
        for(RPObject item: targetSlot)
          {
          if(item.getID().getObjectID()==targetItem)
            {
            target=(Entity)item;            
            break;
            }
          }        
        
        if(target==null)
          {
          logger.debug("Rejected because target item isn't stored inside target slot");
          logger.debug(targetObject);
          logger.debug(action);
          return;
          }
        }
      else
        {
        logger.debug("No targetitem attribute");        
        }
        
      if(target!=null && base.equals(target))
        {
        //Stupid case, put an item in itself.
        logger.debug("Putting an item in itself");        
        return;
        }
        

      /* Now just do it :) Remove from base and add to target slot. */
      if(player.nextto(targetObject,0.25) && 
         ( baseObject!=null && player.nextto(baseObject,0.25)  // If base object is stored in a slot
         || 
           player.nextto(base,0.25) // If base object is on floor
        ))
        {
        
        if(target!=null)
          {
          // If we place a money item over another money item we add them
          if(base instanceof Money && target instanceof Money)
            {
            if(baseObject==null)
              {
              // Floor case
              world.remove(base.getID());
              }
            else
              {
              // From another slot
              baseSlot.remove(base.getID());
              world.modify(baseObject);
              }
              
            logger.debug("Adding money("+target+") + money("+base+")");
            int result=((Money)target).add((Money)base);
            logger.debug("Added money: "+result);
            logger.debug(target);            
            
            logger.debug("PLAYER: "+targetObject);
            
            world.modify(targetObject);
            return;
            }
          else
            {
            logger.debug("Can't add items ("+base+") and ("+target+")");
            }
          }
        else
          {
          logger.debug("There is no target item");
          }
          
        if(!targetSlot.isFull())
          {
          if(baseObject==null)
            {
            // Floor case
            world.remove(base.getID());
            
            // Gives a valid id inside the slot
            targetSlot.assignValidID(base);
            targetSlot.add(base);
              
            world.modify(targetObject);
            }
          else
            {
            // From another slot
            baseSlot.remove(base.getID());
            
            targetSlot.assignValidID(base);
            targetSlot.add(base);
            
            world.modify(baseObject);
            world.modify(targetObject);
            }
          }
        else
          {
          logger.debug("targetSlot is full");
          logger.debug(targetObject);
          }
        }
      else
        {
        logger.debug("baseObject or base or Target is not near the player");
        logger.debug(player);
        logger.debug(targetObject);
        logger.debug(baseObject);
        logger.debug(base);
        }
      }

    Log4J.finishMethod(logger,"equip");
    }

  private void onDrop(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"drop");    

    if(action.has("baseobject") && action.has("baseslot") && action.has("x") && action.has("y") && action.has("baseitem"))
      {
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
        // Only allow to drop objects from players, corpses or chests 
        return;
        }

      if(base instanceof Player && !player.getID().equals(base.getID()))
        {
        // Only allowed to drop item of our own player.
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
        
        int x=action.getInt("x");
        int y=action.getInt("y");
        
        if(player.nextto(baseEntity,0.25) && baseEntity.distance(x,y)<8*8 && !zone.simpleCollides(entity,x,y))
          {
          slot.remove(entity.getID());
  
          entity.setx(x);
          entity.sety(y);
          
          zone.assignRPObjectID(entity);
          zone.add(entity);
          
          // HACK: Avoid a problem on database 
          if(entity.has("#db_id"))
            {
            entity.remove("#db_id");
            }

          world.modify(baseEntity);
          }        
        }
      }

    Log4J.finishMethod(logger,"drop");
    }
  }
