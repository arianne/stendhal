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
package games.stendhal.server.entity.item;

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import java.awt.*;
import java.awt.geom.*;

import games.stendhal.server.entity.*;

public class Corpse extends PassiveEntity
  {
  final public static int DEGRADATION_TIMEOUT=6000; // 30 minutes at 300 ms
  private int degradation;
  private int stage;

  public static void generateRPClass()
    {
    RPClass entity=new RPClass("corpse");
    entity.isA("entity");
    entity.add("class",RPClass.STRING);
    entity.add("stage",RPClass.BYTE);
    }
  
  public Corpse(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    put("type","corpse");
    
    if(object.has("class"))
      {
      put("class",object.get("class"));
      }
    else
      {
      put("class",object.get("type"));
      }

    stage=0;
    degradation=DEGRADATION_TIMEOUT;
    update();
    put("stage",stage);
    }

  public Corpse(RPEntity entity) throws AttributeNotFoundException
    {
    put("type","corpse");

    if(entity.has("class"))
      {
      put("class",entity.get("class"));
      }
    else
      {
      put("class",entity.get("type"));
      }

    Rectangle2D rect=entity.getArea(entity.getx(),entity.gety());
    setx((int)rect.getX());
    sety((int)rect.getY());
    degradation=DEGRADATION_TIMEOUT;
    stage=0;
    put("stage",stage);
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
    int new_stage=5-(int)(((float)degradation/(float)DEGRADATION_TIMEOUT)*6.0);
    if(stage!=new_stage)
      {
      stage=new_stage;
      put("stage",stage);
      world.modify(this);
      }
    
    return degradation--;
    }
    
  public void logic()
    {
    if(decDegradation()==0)
      {
      if(isContained())
        {
        getContainer().remove(this.getID());
        }
      else
        {
        world.remove(getID());
        }
        
      rp.removeCorpse(this);
      }
    }
  }
