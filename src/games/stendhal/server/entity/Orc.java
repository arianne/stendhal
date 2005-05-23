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

public class Orc extends Creature
  {
  final private double SPEED=0.5;
  
  final private static int HP=35;
  final private static int ATK=6;
  final private static int DEF=4;
  final private static int XP=1700;
  
  public static void generateRPClass()
    {    
    try
      {
      RPClass orc=new RPClass("orc");
      orc.isA("npc");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Orc::generateRPClass","X",e);
      }
    }
  
  public Orc() throws AttributeNotFoundException
    {
    super();
    put("type","orc");
    put("x",0);
    put("y",0);
    
    setATK(ATK);
    setDEF(DEF);
    setXP(XP);
    setbaseHP(HP);
    
    stop();

    Logger.trace("Orc::Orc","D","Created Orc: "+this.toString());
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }  
  
  public double getSpeed()
    {
    return SPEED;
    }
  }
