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
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

import games.stendhal.common.*;
import games.stendhal.server.*;

public class Wolf extends Creature
  {
  final private static int HP=10;
  
  public static void generateRPClass()
    {
    SPEED=0.5;
    
    try
      {
      RPClass wolf=new RPClass("wolf");
      wolf.isA("npc");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Wolf::generateRPClass","X",e);
      }
    }
  
  public Wolf() throws AttributeNotFoundException
    {
    super();
    put("type","wolf");
    put("x",0);
    put("y",0);

    setbaseHP(HP);
    stop();

    Logger.trace("Wolf::Wolf","D","Created Wolf: "+this.toString());
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }  
  }
