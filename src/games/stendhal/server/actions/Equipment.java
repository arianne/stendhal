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
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Stackable;

import java.util.*;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

/**
 * This listener handles all entity movements from a slot to
 * either another slot or the ground.
 *  
 *  The source can be:
 *    baseitem   - object id of the item which should be moved
 *    
 *    baseobject - (only when the item is in a slot) object id of the object containing the slot where the item is in
 *    baseslot   - (only when the item is in a slot) slot name where the item is in
 *    
 *    
 *  The target can be either an 'equip':
 *    type         - "equip"
 *    targetobject - object id of the container object 
 *    targetslot   - slot name where the item should be moved to
 *    
 *  or a 'drop':
 *    type         - "drop"
 *    x            - the x-coordinate on the ground  
 *    y            - the y-coordinate on the ground  
 *  
 */
public class Equipment extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);
  private static final String BASE_ITEM = "baseitem";
  private static final String BASE_SLOT = "baseslot";
  private static final String BASE_OBJECT = "baseobject";
  private static final String TYPE = "type";
  private static final String TARGET_OBJECT = "targetobject";
  private static final String TARGET_SLOT = "targetslot";
  private static final String GROUND_X = "X";
  private static final String GROUND_Y = "Y";

  /** the list of valid container classes */
  private static final Class[] validContainerClasses = new Class[] {Player.class, Chest.class, Corpse.class};
  /** List of the valid container classes for easy access */
  private List<Class> validContainerClassesList;

  public static void register()
    {
    Equipment equip=new Equipment();
    StendhalRPRuleProcessor.register("equip",equip);
    StendhalRPRuleProcessor.register("drop",equip);    
    }
  
  /** constuctor */
  public Equipment()
  {
    validContainerClassesList = Arrays.asList(validContainerClasses);
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
  
  /** callback for the equip action */
  private void onEquip(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
  {
    double MAXDISTANCE = 0.25;

    Log4J.startMethod(logger,"equip");

    String type = action.get(TYPE);
    if (!type.equals("equip"))
    {
      logger.error("wrong type for action. is '"+type+"' and should be 'equip'");
      return;
    }
    
    // get source and check it
    SourceObject source = new SourceObject(action,world,player);
    if (!source.isValid() || !source.checkDistance(player,MAXDISTANCE) || !source.checkClass(validContainerClassesList))
    {
      // source is not valid
      return;
    }
    
    // get destination and check it
    DestinationObject dest = new DestinationObject(action,world,player);
    if (!dest.isValid() || !dest.checkDistance(player,MAXDISTANCE) || !dest.checkClass(validContainerClassesList))
    {
      // destination is not valid
      return;
    }
    
    // looks good
    source.moveTo(dest,world);
    
    Log4J.finishMethod(logger,"equip");
  }
//  private void onEquip(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
//    {
//    Log4J.startMethod(logger,"equip");    
//
//    if(action.has("baseitem") && action.has("targetobject") && action.has("targetslot"))
//      {
//       // The base item
//      Entity base=null;
//      // The container that contains base item, a player, a chest, etc...
//      Entity baseObject=null;
//      // The slot of the container where base item is stored
//      RPSlot baseSlot=null;
//      
//      if(action.has("baseobject") && action.has("baseslot"))
//        {
//        // Taking the item from another object or player
//        int id=action.getInt("baseobject");
//
//        StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
//        RPObject.ID objectid=new RPObject.ID(id, zone.getID());
//        if(!zone.has(objectid))
//          {
//          logger.debug("Rejected because zone doesn't have base object");
//          logger.debug(action);
//          return;
//          }
//
//
//        RPObject object=zone.get(objectid);
//        if(!(object instanceof Player || 
//             object instanceof Chest  ||
//             object instanceof Corpse))
//          {
//          logger.debug("Rejected because base object isn't a passive entity or a player");
//          logger.debug(object);
//          logger.debug(action);
//          return;
//          }
//        
//        baseObject=(Entity)object;
//
//        if(baseObject instanceof Player && !player.getID().equals(baseObject.getID()))
//          {
//          // Only allowed to equip our own player.
//          logger.debug("Rejected because base object is a player that is not yours");
//          logger.debug(baseObject);
//          logger.debug(action);
//          return;
//          }
//
//        if(!baseObject.hasSlot(action.get("baseslot")))
//          {
//          logger.debug("Rejected because base object don't have slot base slot");
//          logger.debug(action);
//          return;
//          }        
//
//        baseSlot=baseObject.getSlot(action.get("baseslot"));
//        
//        int baseItem=action.getInt("baseitem");
//        for(RPObject item: baseSlot)
//          {
//          if(item.getID().getObjectID()==baseItem)
//            {
//            base=(Entity)item;            
//            break;
//            }
//          }        
//        
//        if(base==null)
//          {
//          logger.debug("Rejected because base item isn't stored inside base slot");
//          logger.debug(baseObject);
//          logger.debug(action);
//          return;
//          }
//        }
//      else
//        {
//        // Taking item from floor
//        int id=action.getInt("baseitem");
//
//        StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
//        RPObject.ID objectid=new RPObject.ID(id, zone.getID());
//        if(!zone.has(objectid))
//          {
//          logger.debug("Rejected because zone doesn't have base item");
//          logger.debug(action);
//          return;
//          }
//
//
//        RPObject object=zone.get(objectid);
//        if(!(object instanceof PassiveEntity))
//          {
//          logger.debug("Rejected because base item isn't a passive entity");
//          logger.debug(object);
//          logger.debug(action);
//          return;
//          }
//        
//        base=(Entity)object;
//        }
//        
//
//      Entity target=null;
//      // The container that contains target item, a player, a chest, etc...
//      Entity targetObject=null;
//      // The slot of the container where target item is stored
//      RPSlot targetSlot=null;
//      
//      // Taking the item from another object or player
//      int id=action.getInt("targetobject");
//
//      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
//      RPObject.ID objectid=new RPObject.ID(id, zone.getID());
//      if(!zone.has(objectid))
//        {
//        logger.debug("Rejected because zone doesn't have target object");
//        logger.debug(action);
//        return;
//        }
//
//
//      RPObject object=zone.get(objectid);
//      if(!(object instanceof Player || 
//           object instanceof Chest  ||
//           object instanceof Corpse))
//        {
//        logger.debug("Rejected because target object isn't a passive entity or a player");
//        logger.debug(object);
//        logger.debug(action);
//        return;
//        }
//      
//      targetObject=(Entity)object;
//
//      if(targetObject instanceof Player && !player.getID().equals(targetObject.getID()))
//        {
//        // Only allowed to equip our own player.
//        logger.debug("Rejected because target object is a player that is not yours");
//        logger.debug(targetObject);
//        logger.debug(action);
//        return;
//        }
//
//      if(!targetObject.hasSlot(action.get("targetslot")))
//        {
//        logger.debug("Rejected because target object don't have slot target slot");
//        logger.debug(action);
//        return;
//        }        
//
//      targetSlot=targetObject.getSlot(action.get("targetslot"));
//      
//      if(action.has("targetitem"))
//        {
//        int targetItem=action.getInt("targetitem");
//        for(RPObject item: targetSlot)
//          {
//          if(item.getID().getObjectID()==targetItem)
//            {
//            target=(Entity)item;            
//            break;
//            }
//          }        
//        
//        if(target==null)
//          {
//          logger.debug("Rejected because target item isn't stored inside target slot");
//          logger.debug(targetObject);
//          logger.debug(action);
//          return;
//          }
//        }
//      else
//        {
//        logger.debug("No targetitem attribute");        
//        }
//        
//      if(target!=null && base.equals(target))
//        {
//        //Stupid case, put an item in itself.
//        logger.debug("Putting an item in itself");        
//        return;
//        }
//        
//
//      /* Now just do it :) Remove from base and add to target slot. */
//      if(player.nextto(targetObject,0.25) && 
//         ( baseObject!=null && player.nextto(baseObject,0.25)  // If base object is stored in a slot
//         || 
//           player.nextto(base,0.25) // If base object is on floor
//        ))
//        {
//        
//        if(target!=null)
//          {
//          // If we place a money item over another money item we add them
//          if(base instanceof Money && target instanceof Money)
//            {
//            if(baseObject==null)
//              {
//              // Floor case
//              world.remove(base.getID());
//              }
//            else
//              {
//              // From another slot
//              baseSlot.remove(base.getID());
//              world.modify(baseObject);
//              }
//              
//            logger.debug("Adding money("+target+") + money("+base+")");
//            int result=((Money)target).add((Money)base);
//            logger.debug("Added money: "+result);
//            logger.debug(target);            
//            
//            logger.debug("PLAYER: "+targetObject);
//            
//            world.modify(targetObject);
//            return;
//            }
//          else
//            {
//            logger.debug("Can't add items ("+base+") and ("+target+")");
//            }
//          }
//        else
//          {
//          logger.debug("There is no target item");
//          }
//          
//        if(!targetSlot.isFull())
//          {
//          if(baseObject==null)
//            {
//            // Floor case
//            world.remove(base.getID());
//            
//            // Gives a valid id inside the slot
//            targetSlot.assignValidID(base);
//            targetSlot.add(base);
//              
//            world.modify(targetObject);
//            }
//          else
//            {
//            // From another slot
//            baseSlot.remove(base.getID());
//            
//            targetSlot.assignValidID(base);
//            targetSlot.add(base);
//            
//            world.modify(baseObject);
//            world.modify(targetObject);
//            }
//          }
//        else
//          {
//          logger.debug("targetSlot is full");
//          logger.debug(targetObject);
//          }
//        }
//      else
//        {
//        logger.debug("baseObject or base or Target is not near the player");
//        logger.debug(player);
//        logger.debug(targetObject);
//        logger.debug(baseObject);
//        logger.debug(base);
//        }
//      }
//
//    Log4J.finishMethod(logger,"equip");
//    }

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
  
  /** 
   * this encapsulates the equip/drop source 
   */
  private class SourceObject
  {
    /** the item */
    private Entity base;
    /** optional, parent item */
    private Entity parent;
    /** optional, slot */
    private String slot;

    /** interprets the given action */
    public SourceObject(RPAction action, RPWorld world, Player player)
    {
      // base item must be there
      if (!action.has(BASE_ITEM))
      {
        logger.debug("action does not have a base item. action: "+action);
        return;
      }

      // get base item
      RPObject.ID baseItemId = new RPObject.ID(action.getInt(BASE_ITEM),player.getID().getZoneID());
      // is the item in a container?
      if (action.has(BASE_OBJECT))
      {
        // yes, contained
        
        // remove zone from id (contained items does not have a zone)
        baseItemId = new RPObject.ID(baseItemId.getObjectID(),"");
 
        parent = getEntityFromId(player,world,action.getInt(BASE_OBJECT));
        slot = action.get(BASE_SLOT);

        base = (Entity) parent.getSlot(slot).get(baseItemId.getObjectID());
      }
      else
      {
        // item is not contained
        base = (Entity) world.get(baseItemId);
      }
    }
    
    /** moves this entity to the destination */
    public boolean moveTo(DestinationObject dest, RPWorld world)
    {
      if (!dest.isValid() || !dest.preCheck(base,world))
      {
        return false;
      }
      
      removeFromWorld(world);
      dest.addToWorld(base,world);

      return false;
    }

    /** returns true when this SourceObject is valid */
    public boolean isValid()
    {
      if (base != null)
      {
        return true;
      }
      logger.error("source is not valid, base == null");
      return false;
    }
    
    /** returns true when this entity and the other is within the given distance */
    public boolean checkDistance(Entity other, double distance)
    {
      Entity checker = (parent != null) ? parent : base;
      if (other.nextto(checker,distance))
      {
        return true;
      }
      logger.error("distance check failed "+other.distance(checker));
      return false;
    }
    
    /** removes the entity from the world and returns it (so it may nbe added again) */
    public Entity removeFromWorld(RPWorld world)
    {
      if (parent == null)
      {
        world.remove(base.getID());
      }
      else
      {
        parent.getSlot(slot).remove(base.getID());
        world.modify(parent);
      }
      return base;
    }

    /** returns true when the rpobject is one of the classes in <i>validClasses</i> */
    public boolean checkClass(List<Class> validClasses)
    {
      if (parent != null)
      {
        if (!isCorrectClass(validClasses,parent))
        {
          logger.error("parent is the wrong class "+parent.getClass().getName());
        }
      }
      return true;
    }
  }

  /** 
   * this encapsulates the equip/drop destination 
   */
  private class DestinationObject
  {
    /** true when this object is valid */
    private boolean valid;
    /** optional, parent item */
    private Entity parent;
    /** optional, slot */
    private String slot;
    /** x coordinate when dropped on ground */
    private int x;
    /** y coordinate when dropped on ground */
    private int y;

    /** interprets the given action */
    public DestinationObject(RPAction action, RPWorld world, Player player)
    {
      valid = false;
      // droppped into another item 
      if (action.has(TARGET_OBJECT) && action.has(TARGET_SLOT))
      {
        // get base item and slot
        parent = getEntityFromId(player,world,action.getInt(TARGET_OBJECT));
        slot = action.get(TARGET_SLOT);

        // check slot
        if (!parent.hasSlot(slot))
        {
          return;
        }
        // ok, action is valid
        valid = true;
        return;
      }
      
      // dropped to the ground
      if (action.has(GROUND_X) && action.has(GROUND_Y))
      {
        x = action.getInt(GROUND_X); 
        y = action.getInt(GROUND_Y);
        valid = true;
      }

      // not valid
    }
    
    /** checks if it is possible to add the entity to the world */
    public boolean preCheck(Entity entity, RPWorld world)
    {
      StendhalRPZone zone = (StendhalRPZone) world.getRPZone(entity.getID());
      
      if (parent != null)
      {
        if (parent.getSlot(slot).isFull())
        {
          return false;
        }
      }
      else
      {
        // check if the destination is free and in reach
        if (zone.simpleCollides(entity,x,y) || entity.distance(x,y) > 8*8)
        {
          return false;
        }
      }
 
      return true;
    }

    /** returns true when this DestinationObject is valid */
    public boolean isValid()
    {
      return valid;
    }
    
    /** returns true when this entity and the other is within the given distance */
    public boolean checkDistance(Entity other, double distance)
    {
      if (parent != null)
      {
        return (other.nextto(parent,distance));
      }
      // should be dropped to the ground
      return (other.nextto(x,y,distance));  
    }

    /** add the entity to the world (specified by the action during constuction).
     * Note that you should call isValid(), preCheck(..) and checkDistance(..)
     * before adding an item to the world 
     * @return true when the item is added, false otherwise  
     */
    public boolean addToWorld(Entity entity, RPWorld world)
    {
      if (parent != null)
      {
        // drop the entity into a slot
        if (parent.getID().equals(entity.getID()))
        {
          // tried to add the item to itself
        }
        RPSlot rpslot = parent.getSlot(slot);

        // check if the item can be merged with one already in the slot
        if (entity instanceof Stackable)
        {
          Stackable stackEntity = (Stackable) entity;
          // find a stackable item of the same type
          Iterator<RPObject> it = rpslot.iterator();
          while (it.hasNext())
          {
            RPObject object = it.next();
            if (object instanceof Stackable)
            {
              // found another stackable
              Stackable other = (Stackable) object;
              if (other.isStackable(stackEntity))
              {
                // other is the same type...merge them
                other.add(stackEntity);
                entity  = null; // do not process the entity further
                break;
              }
            }
          }
        }
        
        // entity still there?
        if (entity != null)
        {
          // yep, so it is stacked. simplay add it
          rpslot.assignValidID(entity);
          rpslot.add(entity);
        }
        world.modify(parent);
      }
      else
      {
        // drop the entity to the ground
        StendhalRPZone zone = (StendhalRPZone) world.getRPZone(entity.getID());

        // HACK: Avoid a problem on database 
        if(entity.has("#db_id"))
        {
          entity.remove("#db_id");
        }
        
        entity.setx(x);
        entity.setx(y);
        zone.assignRPObjectID(entity);
        zone.add(entity);
      }
      return true;
    }

    /** returns true when the rpobject is one of the classes in <i>validClasses</i> */
    public boolean checkClass(List<Class> validClasses)
    {
      if (parent != null)
      {
        return isCorrectClass(validClasses,parent);
      }
      return true;
    }
  }
  
  
  }
