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

public class ActiveEntity extends Entity 
  {
  private double dx;
  private double dy;
  private boolean collides;
  private String name;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass entity=new RPClass("activeentity");
      entity.isA("entity");
      entity.add("name",RPClass.STRING);
      entity.add("dx",RPClass.FLOAT);
      entity.add("dy",RPClass.FLOAT); 
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("ActiveEntity::generateRPClass","X",e);
      }
    }
  
  public ActiveEntity(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    }
  
  public ActiveEntity() throws AttributeNotFoundException
    {
    super();
    }

  public void update() throws AttributeNotFoundException
    {
    super.update();
    if(has("dx")) dx=getDouble("dx");
    if(has("dy")) dy=getDouble("dy");
    }

  public void setName(String name)
    {
    this.name=name;
    put("name",name);   
    }
  
  public String getName()
    {
    return name;
    }
  
  public void setdx(double dx)
    {
    this.dx=dx;
    put("dx",dx);
    }
  
  public double getdx()
    {
    return dx;
    }
  
  public void setdy(double dy)
    {
    this.dy=dy;
    put("dy",dy);
    }

  public double getdy()
    {
    return dy;
    }

  public void stop()
    {
    setdx(0);
    setdy(0);
    }
    
  public boolean stopped()
    {
    return dx==0 && dy==0;
    }
  
  public void collides(boolean val)
    {
    collides=val;    
    }
  
  public boolean collided()
    {
    return collides;
    }
  }
    
