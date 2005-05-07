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

public class Entity extends RPObject 
  {
  private double x;
  private double y;
  private int dir;
  
  protected static StendhalRPRuleProcessor rp;
  protected static RPWorld world;
  
  public static void setRPContext(StendhalRPRuleProcessor rpContext,RPWorld worldContext)
    {
    rp=rpContext;
    world=worldContext;
    }
  
  public static void generateRPClass()
    {
    RPClass entity=new RPClass("entity");
    entity.add("x",RPClass.FLOAT);
    entity.add("y",RPClass.FLOAT);
    entity.add("dir",RPClass.BYTE);
    }
  
  public Entity(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    update();
    }
  
  public Entity() throws AttributeNotFoundException
    {
    super();
    }

  public void update() throws AttributeNotFoundException
    {
    if(has("x")) x=getDouble("x");
    if(has("y")) y=getDouble("y");
    }
  
  public void setx(double x)
    {
    this.x=x;
    put("x",x);
    }
  
  public double getx()
    {
    return x;
    }
  
  public void sety(double y)
    {
    this.y=y;
    put("y",y);
    }

  public double gety()
    {
    return y;
    }  
  
  /** This returns the manhattan distance.
   *  It is faster than real distance */
  public double distance(Entity entity)
    {
    return (x-entity.x)*(x-entity.x)+(y-entity.y)*(y-entity.y);
    }

  public double distance(double x, double y)
    {
    return (x-this.x)*(x-this.x)+(y-this.y)*(y-this.y);
    }

  public boolean nextto(double x, double y, double step)
    {
    Rectangle2D this_area=EntityAreas.getArea(get("type"),x,y);
    this_area.setRect(this_area.getX()-step,this_area.getY()-step,this_area.getWidth()+step,this_area.getHeight()+step);
    
    return this_area.contains(x,y);
    }

  public boolean nextto(Entity entity, double step)
    {
    Rectangle2D this_area=EntityAreas.getArea(get("type"),x,y);
    Rectangle2D other_area=EntityAreas.getArea(entity.get("type"),entity.x,entity.y);
    
    this_area.setRect(this_area.getX()-step,this_area.getY()-step,this_area.getWidth()+step,this_area.getHeight()+step);
    other_area.setRect(other_area.getX()-step,other_area.getY()-step,other_area.getWidth()+step,other_area.getHeight()+step);
    
    return this_area.intersects(other_area);    
    }

  public void setFacing(int facing)
    {
    dir=facing;
    put("dir",facing);
    }
  }
    
