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
  HashMap<RPObject.ID, RPObject> objects;
  
  public StaticGameObjects()
    {
    objects=new HashMap<RPObject.ID, RPObject>();
    }
   
  void add(RPObject object) throws AttributeNotFoundException
    {
    objects.put(object.getID(),object);
    }
  
  void modify(RPObject object) throws AttributeNotFoundException
    {
    objects.put(object.getID(),object);
    }
  
  void remove(RPObject.ID id)
    {
    objects.remove(id);
    }
  
  void clear()
    {
    objects.clear();
    }
    
  public void draw(Graphics g)
    {
    for(RPObject object: objects.values())
      {
      
      }
    }
  }
  
