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

public class Portal extends Entity 
  {
  private int number;
  private String destinationZone;
  private int destinationNumber;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass portal=new RPClass("portal");
      portal.isA("entity");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Player::generateRPClass","X",e);
      }
    }
  
  public Portal() throws AttributeNotFoundException
    {
    super();
    put("type","portal");
    }
  
  public void setNumber(int number)
    {
    this.number=number;
    }
  
  public int getNumber()
    {
    return number;
    }

  public void setDestination(String zone, int number)
    {
    this.destinationNumber=number;
    this.destinationZone=zone;
    }
  
  public int getDestinationNumber()
    {
    return destinationNumber;
    }

  public String getDestinationZone()
    {
    return destinationZone;
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }  
  }
