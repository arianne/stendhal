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

public class BuyerNPC extends SpeakerNPC 
  {
  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("buyernpc");
      npc.isA("npc");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("BuyerNPC::generateRPClass","X",e);
      }
    }
    
  public BuyerNPC() throws AttributeNotFoundException
    {
    super();
    put("type","buyernpc");
    }

  public boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("hi"))
      {
      put("text","Come here to sell your sheeps! I have the best prices at this side of the Ourvalon!");
      return true;
      }
    else if(text.contains("sell"))
      {
      if(player.hasSheep())
        {
        Sheep sheep=(Sheep)world.get(player.getSheep());
        if(distance(sheep)>5*5)
          {
          put("text","Your sheep is too far. I can't see it from here. Go and grab it here.");
          }
        else
          {
          put("text","Thanks! Here is your money");
          world.remove(player.getSheep());
          player.removeSheep();
          world.modify(player);
          }
        }
      else
        {
        put("text","You don't have any sheep!!. Who do you think you are talking to, "+player.get("name")+"?");
        }
      
      return true;
      }
    else if(text.equals("help"))
      {
      put("text","I do buy sheeps, try to SELL me one.");
      return true;
      }
    else if(text.equals("bye"))
      {
      put("text","Bye "+player.get("name"));
      return true;
      }
    else if(has("text")) 
      {
      remove("text");
      return true;  
      }
      
    return false;
    }

  public boolean move()
    {
    if(getdy()==0)
      {
      setdy(0.2);   
      }
      
    if(gety()<=25) 
      {
      setdy(0.2);   
      return true; 
      }
      
    if(gety()>=28) 
      {
      setdy(-0.2);    
      return true; 
      }

    return false; 
    }
  }
