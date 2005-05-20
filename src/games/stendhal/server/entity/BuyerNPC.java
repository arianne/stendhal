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
import java.util.*;

public class BuyerNPC extends SpeakerNPC 
  {
  final private double SPEED=0.25;
  
  private int amount;
  
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
    amount=0;
    
    List<Path.Node> nodes=new LinkedList<Path.Node>();
    nodes.add(new Path.Node(40,25));
    nodes.add(new Path.Node(40,29));
    nodes.add(new Path.Node(44,29));
    nodes.add(new Path.Node(40,29));
    setPath(nodes,true);
    }

  public boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("hi"))
      {
      say("Come here to sell your sheeps! I have the best prices at this side of the Ourvalon!");
      return true;
      }
    else if(text.contains("sell"))
      {
      if(player.hasSheep())
        {
        Sheep sheep=(Sheep)world.get(player.getSheep());
        if(distance(sheep)>5*5)
          {
          say("Your sheep is too far. I can't see it from here. Go and grab it here.");
          }
        else
          {
          say("Thanks! Here is your money.");
          world.remove(player.getSheep());
          player.removeSheep(sheep);
          
          player.setXP(player.getXP()+100*(sheep.getWeight()/100));
          
          world.modify(player);
          amount++;
          }
  
        return true;
        }
      else
        {
        say("You don't have any sheep!!. Who do you think you are talking to, "+player.get("name")+"?");
        return true;
        }      
      }
    else if(text.equals("help"))
      {
      say("I do buy sheeps, try to SELL me one.");
      return true;
      }
    else if(text.contains("job"))
      {
      say("I work here buying sheeps for a Meat factory near Capital. Have you visited our Capital?");
      }    
    else if(text.contains("bought"))
      {
      say("I have bougth "+amount+" sheeps");
      }
    else if(text.equals("bye"))
      {
      say("Bye "+player.get("name"));
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
    Path.followPath(this,SPEED);
    return true;
    }
  }
