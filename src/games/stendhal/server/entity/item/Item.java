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

public class Item extends PassiveEntity
  {
  public static void generateRPClass()
    {
    RPClass entity=new RPClass("item");
    entity.isA("entity");
    entity.add("class",RPClass.STRING);
    }
  
  public static Item create(String iclass)
    {
    if(iclass.equals("shield"))
      {
      return new Shield();
      }
    else if(iclass.equals("sword"))
      {
      return new Sword();
      }
    else if(iclass.equals("armor"))
      {
      return new Armor();
      }
    else
      {
      Logger.trace("Item::create","X",iclass+" doesn't exist");
      return null;
      }
    }

  public Item() throws AttributeNotFoundException
    {
    super();
    put("type","item");
    update();
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }
  }
  
  
