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
package games.stendhal.server.entity;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import games.stendhal.server.events.UseEvent;
import games.stendhal.common.Rand;

import marauroa.common.game.*;

public class Blood extends Entity
  {
  final public static int DEGRADATION_TIMEOUT=300; // 30 minutes at 300 ms
  private int degradation;
  
  public static void generateRPClass()
    {
    RPClass blood=new RPClass("blood");
    blood.isA("entity");
    blood.add("class",RPClass.BYTE);
    }
  
  public Blood(RPEntity entity) throws AttributeNotFoundException
    {
    super();
    put("type","blood");
    put("class",Rand.rand(4));
    
    degradation=DEGRADATION_TIMEOUT;
    
    Rectangle2D rect=entity.getArea(entity.getx(),entity.gety());
    
    set((int)rect.getX(),(int)rect.getY());
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }

  public boolean isCollisionable()
    {
    return false;
    }

  private int decDegradation()
    {
    return degradation--;
    }

  public void logic()
    {
    if(decDegradation()==0)
      {
      world.remove(getID());
      rp.removeBlood(this);
      }
    }
  
  public String describe()
    {
    return("You see a blood pool.");
    }
  }
