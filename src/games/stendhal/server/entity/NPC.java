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

public class NPC extends RPEntity 
  {
  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("npc");
      npc.isA("rpentity");
      npc.add("text",RPClass.STRING);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("NPC::generateRPClass","X",e);
      }
    }
  
  public NPC() throws AttributeNotFoundException
    {
    super();
    put("type","npc");
    put("x",0);
    put("y",0);
    put("dx",0);
    put("dy",0);
    }
  
  public boolean chat(Player player) throws AttributeNotFoundException
    {    
    return false;
    }

  public boolean move()
    {   
    return false; 
    }
  }