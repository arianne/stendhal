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

import java.awt.*;
import java.awt.geom.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.*;

public class Food extends Entity 
  {
  private int amount;
  private double grow;
  
  public static void generateRPClass()
    {
    RPClass food=new RPClass("food");
    food.isA("entity");
    food.add("amount",RPClass.BYTE);
    }
  
  public Food(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    put("type","food");
    update();
    }

  public Food() throws AttributeNotFoundException
    {
    super();
    put("type","food");
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }  

  public void update()
    {
    super.update();
    if(has("amount")) amount=getInt("amount");
    }
  
  public void setAmount(int amount)
    {
    this.amount=amount;
    put("amount",amount);
    }
    
  public int getAmount()
    {
    return amount;
    }
  
  public void regrow()
    {
    if(amount<5)
      {
      grow+=0.0005; // 2000 turns

      if(grow>1)
        {
        grow=0;
        setAmount(amount+1);
        world.modify(this);
        }
      }
    }

  }
