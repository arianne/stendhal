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
        return new Player(object);
        }
      else if(object.get("type").equals("wolf"))
        {
        return new Wolf(object);
        }
      else if(object.get("type").equals("sheep"))
        {
        return new Sheep(object);
        }
      else if(object.get("type").equals("sign"))
        {
        return new Sign(object);
        }
      else
        {
        return new GameEntity(object);
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
    entity.modify(object);
    objects.put(entity.getID(),entity);
    System.out.println (object);
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
  public void modify(RPObject object) throws AttributeNotFoundException
    {
    Logger.trace("GameObjects::modify",">");
    GameEntity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modify(object);
      }
    Logger.trace("GameObjects::modify","<");
    }
  
  /** Removes a GameEntity from game */
  public void remove(RPObject.ID id)
    {
    Logger.trace("GameObjects::remove",">");
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
  
