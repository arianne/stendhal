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
  final private double SPEED=0.5;

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
    }

  protected boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("hi"))
      {
      say("Welcome to Stendhal. My name is "+get("name")+" and I will help you to Stendhal's world. What do you need?");
      return true;
      }
    else if(text.contains("job") || text.contains("help"))
      {
      say("I give indications, I heal and I can suggest you quests to accomplish. What do you need?");
      return true;
      }
    else if(text.contains("indication"))
      {
      say("You are right now City. You can move to the left and talk with Nagashi to get a small sheep. You can move down into the plains, but I have been told that Wolves are dangerous there. You can move into the dungeons a bit to the right, but they are dangerous too!. Isn't scary?");
      return true;
      }
    else if(text.contains("quest"))
      {
      say("I have been told that Sato will reward you for a fully grow sheep");
      return true;
      }
    else if(text.contains("heal"))
      {
      say("Voila!. You have been healed. Don't forget to tell about Stendhal to your friends :)");
      player.setHP(player.getbaseHP());
      world.modify(player);
      return true;
      }

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
