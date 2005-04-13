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
    
  protected static StendhalRPRuleProcessor rp;
  
  public static void setRPContext(StendhalRPRuleProcessor rpContext)
    {
    rp=rpContext;
    }
  
  public NPC(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    update();
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
  
  public boolean chat(RPWorld world, Player player) throws AttributeNotFoundException
    {    
    return false;
    }

  public boolean move(RPWorld world)
    {   
    return false; 
    }
  }