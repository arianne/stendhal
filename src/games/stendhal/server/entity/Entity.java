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

public class Entity extends RPObject 
  {
  private double x;
  private double y;
  private int dir;
  
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
    x=getDouble("x");
    y=getDouble("y");
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

  public void setFacing(int facing)
    {
    dir=facing;
    put("dir",facing);
    }
  }
    
