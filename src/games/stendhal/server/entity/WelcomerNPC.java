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

public class WelcomerNPC extends NPC 
  {
  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("welcomernpc");
      npc.isA("npc");
      npc.add("text",RPClass.LONG_STRING);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("WelcomerNPC::generateRPClass","X",e);
      }
    }
    
  public WelcomerNPC() throws AttributeNotFoundException
    {
    super();
    put("type","welcomernpc");
    put("name","Carmen");
    put("text","Welcome to Stendhal. My name is "+get("name")+" and I will introduce you to Stendhal's world. You should go to village to get a new sheep and then move to Plains to raise it then return back to here and sell it to Sato");
    }

  public void logic()
    {    
    if(getdx()==0)
      {
      setdx(Math.signum(Math.random()-0.5)*0.2);   
      }
      
    if(getx()<=5) 
      {
      setdx(0.1);   
      world.modify(this);
      }
      
    if(getx()>=14) 
      {
      setdx(-0.1);    
      world.modify(this);
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      }
    }
  }
