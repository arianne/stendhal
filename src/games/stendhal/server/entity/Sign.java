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

public class Sign extends Entity 
  {
  public static void generateRPClass()
    {
    try
      {
      RPClass sign=new RPClass("sign");
      sign.isA("entity");
      sign.add("text",RPClass.LONG_STRING);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Player::generateRPClass","X",e);
      }
    }
  
  public Sign() throws AttributeNotFoundException
    {
    super();
    put("type","sign");
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }  

  public void setText(String text)
    {
    put("text",text);
    }
  }
