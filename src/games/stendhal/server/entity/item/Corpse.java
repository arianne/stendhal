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

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Player;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import marauroa.common.game.*;

public class Corpse extends PassiveEntity
  {
  private static final Logger logger = Log4J.getLogger(Corpse.class);

  final public static int DEGRADATION_TIMEOUT=3000; // 30 minutes at 300 ms
  private int degradation;
  private int stage;

  public static void generateRPClass()
    {
    RPClass entity=new RPClass("corpse");
    entity.isA("entity");
    entity.add("class",RPClass.STRING);
    entity.add("stage",RPClass.BYTE);

    entity.add("name",RPClass.STRING);
    entity.add("killer",RPClass.STRING);
    
    entity.addRPSlot("content",4);
    }
  
  public Corpse(String clazz, int x, int y) throws AttributeNotFoundException
    {    
    put("type","corpse");
    put("class",clazz);

    setx(x);
    sety(y);
    degradation=DEGRADATION_TIMEOUT;
    stage=0;
    put("stage",stage);

    RPSlot slot=new RPSlot("content");
    slot.setCapacity(4);
    addSlot(slot);
    }
    
  public Corpse(RPEntity entity, RPEntity killer) throws AttributeNotFoundException
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

    if(killer!=null && entity instanceof Player)
      {
      put("name",entity.getName());        
      
      if(killer.has("name"))
        {
        put("killer",killer.getName());
        }
      else if(killer.has("subclass"))
        {
        put("killer",killer.get("subclass"));
        }
      else if(killer.has("class"))
        {
        put("killer",killer.get("class"));
        }
      else if(killer.has("type"))
        {
        put("killer",killer.get("type"));
        }
      }  

    // Corpses are 1,1 while other entities are 1.5,2.
    // This fix the problem
    Rectangle2D rect=entity.getArea(entity.getx(),entity.gety());
    setx((int)rect.getX());
    sety((int)rect.getY());
    degradation=DEGRADATION_TIMEOUT;
    stage=0;
    put("stage",stage);

    RPSlot slot=new RPSlot("content");
    slot.setCapacity(4);
    addSlot(slot);
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
      
      if(isContained())
        {
        // We modify the base container if the object change.
        RPObject base=getContainer();
        while(base.isContained())
          {
          if(base==base.getContainer())
            {
            logger.fatal("A corpse is contained by itself.");
            break;
            }
            
          base=base.getContainer();
          }
          
        world.modify(base);
        }
      else
        {
        world.modify(this);
        }
      }
    
    return degradation--;
    }
    
  public void logic()
    {
    if(decDegradation()==0)
      {
      if(isContained())
        {
        getContainerSlot().remove(this.getID());
        world.modify(getContainer());
        }
      else
        {
        world.remove(getID());
        }
        
      rp.removeCorpse(this);
      }
    }

  public void add(PassiveEntity entity)
    {
    RPSlot content=getSlot("content");
    content.assignValidID(entity);
    content.add(entity);
    }
  
  public int size()
    {
    return getSlot("content").size();
    }  
  
  public Iterator<RPObject> getContent()
    {
    RPSlot content=getSlot("content");
    return content.iterator();
    }
  }
