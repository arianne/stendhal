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
package games.stendhal.client;

import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.entity.*;
import games.stendhal.common.*;
import java.util.*;
import java.awt.Graphics;
import java.awt.geom.*;

/** This class stores the objects that exists on the World right now */
public class GameObjects 
  {
  private HashMap<RPObject.ID, Entity> objects;
  private StaticGameLayers collisionMap;
  
  public GameObjects(StaticGameLayers collisionMap)
    {
    objects=new HashMap<RPObject.ID, Entity>();
    this.collisionMap=collisionMap;
    }
  
  /** Create a Entity of the correct type depending of the arianne object */
  private Entity entityType(RPObject object) 
    {
    try
      {
      /** TODO: Refactor this --> Factory pattern apply. */
      if(object.get("type").equals("player"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("wolf"))
        {
        return new Wolf(this, object);
        }
      else if(object.get("type").equals("sheep"))
        {
        return new Sheep(this, object);
        }
      else if(object.get("type").equals("sign"))
        {
        return new Sign(this, object);
        }
      else if(object.get("type").equals("sellernpc"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("buyernpc"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("welcomernpc"))
        {
        return new Player(this, object);
        }
      else if(object.get("type").equals("trainingdummy"))
        {
        return new TrainingDummy(this, object);
        }
      else if(object.get("type").equals("food"))
        {
        return new Food(this, object);
        }
      else
        {
        Logger.trace("GameObjects::entityType","X","Unknown entity type");
        return null;
        }
      }
    catch(AttributeNotFoundException e)
      {
      Logger.thrown("GameObjects::entityType","X",e);
      return null;
      }
    }
  
  /** Add a new Entity to the game */  
  public void add(RPObject object) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::add",">");

    Entity entity=entityType(object);
    // HACK: The first time the object is EMPTY! 
    entity.modifyAdded(new RPObject(), object);
    objects.put(entity.getID(),entity);
    
    Logger.trace("GameObjects::add","D",entity.toString());
    Logger.trace("GameObjects::add","<");
    }
  
  public Entity at(double x, double y)
    {
    for(Entity entity: objects.values())
      {
      if(entity.getDrawedArea().contains(x,y))
        {
        return entity;
        }
      }
    
    return null;
    }  

  /** Modify a existing Entity so its propierties change */  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::modifyAdded",">");
    Entity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modifyAdded(object, changes);
      }
      
    Logger.trace("GameObjects::modifyAdded","<");
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::modifyRemoved",">");
    Entity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modifyRemoved(object, changes);
      }
      
    Logger.trace("GameObjects::modifyRemoved","<");
    }

  public void attack(RPEntity source, RPObject.ID target, int risk, int damage) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::damage",">");
    Entity entity=objects.get(target);
    if(entity!=null && entity instanceof RPEntity)
      {
      RPEntity rpentity=(RPEntity)entity;
      rpentity.onAttack(source,risk, damage);
      }
      
    Logger.trace("GameObjects::damage","<");
    }

  public void attackStop(RPEntity source, RPObject.ID target) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::damage",">");
    Entity entity=objects.get(target);
    if(entity!=null && entity instanceof RPEntity)
      {
      RPEntity rpentity=(RPEntity)entity;
      rpentity.onAttackStop(source);
      }
      
    Logger.trace("GameObjects::damage","<");
    }
  
  public boolean has(Entity entity)
    {
    return objects.containsKey(entity.getID());
    }
    
  /** Removes a Entity from game */
  public void remove(RPObject.ID id)
    {
    Logger.trace("GameObjects::remove",">");
    Logger.trace("GameObjects::remove","D",id.toString());

    Entity entity=objects.get(id);
    if(entity!=null)
      {
      entity.removed();
      }

    objects.remove(id);
    Logger.trace("GameObjects::remove","<");
    }
  
  /** Removes all the object entities */
  public void clear()
    {
    Logger.trace("GameObjects::clear",">");
    objects.clear();
    Logger.trace("GameObjects::clear","<");
    }
  
  /** Move objects based on the lapsus of time ellapsed since the last call. */
  public void move(long delta)    
    {
    for(Entity entity: objects.values())
      {
      if(!entity.stopped() && !collisionMap.collides(entity.getArea()))
        {
        entity.move(delta);
        }      
      }
    }
   
  /** Draw all the objects in game */
  public void draw(GameScreen screen)
    {
    for(Entity entity: objects.values())
      {
      entity.draw(screen);
      }
    }
  }
  
