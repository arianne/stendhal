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
import java.util.*;
import java.awt.Graphics;


public class StaticGameObjects 
  {
  HashMap<RPObject.ID, GameEntity> objects;
  
  public StaticGameObjects()
    {
    objects=new HashMap<RPObject.ID, GameEntity>();
    }
   
  void add(RPObject object) throws AttributeNotFoundException
    {
    GameEntity entity=new GameEntity(object);
    objects.put(entity.getID(),entity);
    }
  
  void modify(RPObject object) throws AttributeNotFoundException
    {
    GameEntity entity=objects.get(object.getID());
    if(entity!=null)
      {
      entity.modify(object);
      }
    }
  
  void remove(RPObject.ID id)
    {
    objects.remove(id);
    }
  
  void clear()
    {
    objects.clear();
    }
    
  public void draw(GameScreen screen)
    {
    for(GameEntity entity: objects.values())
      {
      entity.draw(screen);
      }
    }
  }
  
