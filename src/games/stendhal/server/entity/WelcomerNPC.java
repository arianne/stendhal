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

import games.stendhal.common.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.*;

public class WelcomerNPC extends SpeakerNPC 
  {
  final private static double SPEED=0.5;

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

  public void onDead(RPEntity who)
    {
    setHP(getbaseHP());
    world.modify(this);
    }
    
  protected boolean chat(Player player) throws AttributeNotFoundException
    {
    return false;
    }
    
  protected boolean move()
    {
    if(getDirection()==Direction.STOP) 
      {
      setDirection(Direction.LEFT);
      setSpeed(SPEED);
      }
      
    if(getx()<=5) 
      {
      setDirection(Direction.RIGHT);
      setSpeed(SPEED);
      }
      
    if(getx()>=14) 
      {
      setDirection(Direction.LEFT);
      setSpeed(SPEED);
      }
    
    return true;
    }
  }
