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

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import java.awt.*;
import java.awt.geom.*;


public class Corpse extends PassiveEntity
  {
  final public static int DEGRADATION_TIMEOUT=6000; // 30 minutes at 300 ms
  private int degradation;

  public static void generateRPClass()
    {
    RPClass entity=new RPClass("corpse");
    entity.isA("entity");
    }
  
  public Corpse(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    put("type","corpse");
    update();
    }

  public Corpse(RPEntity entity) throws AttributeNotFoundException
    {
    put("type","corpse");
    Rectangle2D rect=entity.getArea(entity.getx(),entity.gety());
    setx((int)rect.getX());
    sety((int)rect.getY());
    degradation=DEGRADATION_TIMEOUT;
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }  
  
  public int getDegradation()
    {
    return degradation;
    } 
  
  public int decDegradation()
    {
    return degradation--;
    }
    
  public void logic()
    {
    if(decDegradation()==0)
      {
      world.remove(getID());
      rp.removeCorpse(this);
      }
    }
  }
