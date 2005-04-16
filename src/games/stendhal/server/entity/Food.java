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
import games.stendhal.server.*;

public class Food extends Entity 
  {
  private int amount;
  
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

  }
