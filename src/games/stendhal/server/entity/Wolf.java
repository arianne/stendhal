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

import games.stendhal.common.*;
import games.stendhal.server.*;

public class Wolf extends NPC
  {
  public static void generateRPClass()
    {
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
  
  public Wolf(Player owner) throws AttributeNotFoundException
    {
    super();
    put("type","wolf");
    put("x",0);
    put("y",0);
    put("dx",0);
    put("dy",0);

    Logger.trace("Wolf::Wolf","D","Created Wolf: "+this.toString());
    }
  
  public Wolf(RPObject object, Player owner) throws AttributeNotFoundException
    {
    super(object);
    put("type","wolf");
    
    update();
    Logger.trace("Wolf::Wolf","D","Created Wolf: "+this.toString());
    }
  
  public void logic()
    {
    Logger.trace("Wolf::logic",">");
    Logger.trace("Wolf::logic","<");
    }
  }
