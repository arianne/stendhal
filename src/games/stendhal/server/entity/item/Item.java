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

import games.stendhal.server.entity.PassiveEntity;
import java.awt.geom.Rectangle2D;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import org.apache.log4j.Logger;

public class Item extends PassiveEntity
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Item.class);

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
    else if(iclass.equals("club"))
      {
      return new Club();
      }
    else if(iclass.equals("armor"))
      {
      return new Armor();
      }
    else
      {
      logger.warn(iclass+" doesn't exist");
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
  
  
