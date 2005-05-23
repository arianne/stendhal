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
      say("Welcome to Stendhal. My name is "+get("name")+" and I will help you, here, in Stendhal's world. What do you need?");
      return true;
      }
    else if(text.contains("job") || text.contains("help"))
      {
      say("I give INDICATIONs, I HEAL and I can tell you about QUESTs that need completing. What do you need?");
      return true;
      }
    else if(text.contains("indication"))
      {
      say("At the moment, you are in the City. You can travel West to find and talk with Nagashi to purchase a small sheep. You can travel South into the plains but I warn you, dangerous wolves live there. You can travel a short way East into the Dungeons but they are very dangerous too!. Scary, isn't it?");
      return true;
      }
    else if(text.contains("quest"))
      {
      say("I have been told that Sato will reward you for a fully grown sheep.|Purchase one from Nagashi and then feed it to make it nice and fat!");
      return true;
      }
    else if(text.contains("heal"))
      {
      say("Voila! You have been healed.|Don't forget to tell your friends about Stendhal :)");
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
