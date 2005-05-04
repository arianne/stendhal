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
  private HashMap<RPObject.ID, GameEntity> objects;
  private StaticGameLayers collisionMap;
  
  public GameObjects(StaticGameLayers collisionMap)
    {
    objects=new HashMap<RPObject.ID, GameEntity>();
    this.collisionMap=collisionMap;
    }
  
  /** Create a GameEntity of the correct type depending of the arianne object */
  private GameEntity entityType(RPObject object) 
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
        return new GameEntity(this,object);
        }
      }
    catch(AttributeNotFoundException e)
      {
      e.printStackTrace();
      return null;
      }
    }
  
  /** Add a new GameEntity to the game */  
  public void add(RPObject object) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::add",">");

    GameEntity entity=entityType(object);
    // HACK: The first time the object is EMPTY! 
    entity.modifyAdded(new RPObject(), object);
    objects.put(entity.getID(),entity);
    
    Logger.trace("GameObjects::add","D",entity.toString());
    Logger.trace("GameObjects::add","<");
    }
  
  public GameEntity at(double x, double y)
    {
    for(GameEntity entity: objects.values())
      {
      if(entity.getDrawedArea().contains(x,y))
        {
        return entity;
        }
      }
    
    return null;
    }  

  /** Modify a existing GameEntity so its propierties change */  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::modifyAdded",">");
    GameEntity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modifyAdded(object, changes);
      }
      
    Logger.trace("GameObjects::modifyAdded","<");
    }

  public void modifyRemoved(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::modifyRemoved",">");
    GameEntity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modifyRemoved(object, changes);
      }
      
    Logger.trace("GameObjects::modifyRemoved","<");
    }

  public void attack(GameEntity source, RPObject.ID target, int risk, int damage) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::damage",">");
    GameEntity entity=objects.get(target);
    if(entity!=null)
      {
      entity.onAttack(source,risk, damage);
      }
      
    Logger.trace("GameObjects::damage","<");
    }

  public void attackStop(GameEntity source, RPObject.ID target) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::damage",">");
    GameEntity entity=objects.get(target);
    if(entity!=null)
      {
      entity.onAttackStop(source);
      }
      
    Logger.trace("GameObjects::damage","<");
    }
  
  /** Removes a GameEntity from game */
  public void remove(RPObject.ID id)
    {
    Logger.trace("GameObjects::remove",">");
    Logger.trace("GameObjects::remove","D",id.toString());
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
    for(GameEntity entity: objects.values())
      {
      if(entity.getHorizontalMovement()!=0 || entity.getVerticalMovement()!=0)
        {
        if(collisionMap.collides(entity.getArea())==false)
          {
          entity.move(delta);
          }      
        }
      }
    }
   
  /** Draw all the objects in game */
  public void draw(GameScreen screen)
    {
    for(GameEntity entity: objects.values())
      {
      entity.draw(screen);
      }
    }
  }
  
