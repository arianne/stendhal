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

import marauroa.common.game.*;
import games.stendhal.client.entity.*;
import java.util.*;
import java.awt.Graphics;

/** This class stores the objects that exists on the World right now */
public class GameObjects 
  {
  HashMap<RPObject.ID, GameEntity> objects;
  
  public GameObjects()
    {
    objects=new HashMap<RPObject.ID, GameEntity>();
    }
  
  /** Create a GameEntity of the correct type depending of the arianne object */
  private GameEntity entityType(RPObject object) 
    {
    try
      {
      /** TODO: Refactor this
       *  Factory pattern apply. */
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
    GameEntity entity=entityType(object);
    objects.put(entity.getID(),entity);
    }
  
  /** Modify a existing GameEntity so its propierties change */  
  public void modify(RPObject object) throws AttributeNotFoundException
    {
    GameEntity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modify(object);
      }
    }
  
  /** Removes a GameEntity from game */
  public void remove(RPObject.ID id)
    {
    objects.remove(id);
    }
  
  /** Removes all the object entities */
  public void clear()
    {
    objects.clear();
    }
  
  /** Move objects based on the lapsus of time ellapsed since the last call. */
  public void move(long delta)    
    {
    for(GameEntity entity: objects.values())
      {
      entity.move(delta);
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
  
