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

import java.util.List;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;

public abstract class ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(ActionListener.class);
  
  /** callback for the registered action */
  public abstract void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action);

  /** Checks if a specific Attribute is set. returns false when it is not set.
   * Additionally the logger is informed about the missing attribute
   *  
   * @param action the action to check
   * @param name name of the attribute
   * @return true when the attrib is there, false if it is missing
   */
  protected boolean checkAttribute(RPAction action, String name)
  {
    if (action.has(name))
    {
      // the attrib is there
      return true;
    }
    
    // attrib is not there
    logger.debug("action attribute "+name+" is missing");
    return false;
  }
  
  /**
   * Gets the object for the given id. Returns null when the item is not available.
   * Failure is written to the logger.
   * 
   * @param player the player
   * @param world the world
   * @param objectId the objects id
   * @return the object with the given id or null if the object is not available.
   */
  protected Entity getEntityFromId(Player player, RPWorld world, int objectId)
  {
    StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());
    RPObject.ID id = new RPObject.ID(objectId, zone.getID());
    
    if(!zone.has(id))
    {
      logger.debug("Rejected because zone doesn't have object "+objectId);
      return null;
    }

    return (Entity) zone.get(id);
  }
  
  /**
   * checks if the entity is near the player
   * 
   * @param player the player
   * @param entity the entity 
   * @param distance the valid distance considered 'near'
   * @return true when the entity is near the player, false otherwise 
   */
  protected boolean checkDistance(Entity first, Entity second, double distance)
  {
    if (first.nextto(second,distance))
    {
      return true;
    }
    
    logger.debug("object is to far. distance+ "+distance+" first: "+first.getID()+" second: "+second.getID());
    return false;
  }

  /**
   * Checks if the object is of one of the given class.
   * 
   * @param validClasses list of valid class-objects
   * @param baseObject the object to check
   * @return true when the class is in the list, else false
   */
  protected boolean isCorrectClass(List<Class> validClasses, RPObject object)
  {
  for(Class clazz: validClasses)
    {
    if(clazz.isInstance(object))
      {
      return true;
      }
    }
//    if (validClasses.contains(object.getClass()))
//    {
//      return true;
//    }
    
    logger.debug("object "+object.getID()+" is not of the correct class. it is "+object.getClass().getName());
    return false;
  }
  
  
  }
