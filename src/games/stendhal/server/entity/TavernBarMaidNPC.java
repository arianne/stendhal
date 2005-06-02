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
import java.util.*;

public class TavernBarMaidNPC extends SpeakerNPC 
  {
  final private double SPEED=0.1;
  private Random rand;

  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("tavernbarmaidnpc");
      npc.isA("npc");
      npc.add("text",RPClass.LONG_STRING);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("TavernBarMaidNPC::generateRPClass","X",e);
      }
    }
    
  public TavernBarMaidNPC() throws AttributeNotFoundException
    {
    super();
    put("type","tavernbarmaidnpc");
    
    rand=new Random();

    List<Path.Node> nodes=new LinkedList<Path.Node>();
    nodes.add(new Path.Node(17,12));
    nodes.add(new Path.Node(17,13));
    nodes.add(new Path.Node(16,8));
    nodes.add(new Path.Node(13,8));
    nodes.add(new Path.Node(13,6));
    nodes.add(new Path.Node(13,10));
    nodes.add(new Path.Node(23,10));
    nodes.add(new Path.Node(23,10));
    nodes.add(new Path.Node(23,13));
    nodes.add(new Path.Node(23,10));
    nodes.add(new Path.Node(17,10));
    setPath(nodes,true);
    }

  protected boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("hi") || text.contains("hello"))
      {
	    switch(rand.nextInt(4))
        {
        case 0:  
          say("Hello darling! Welcome to our tavern! Take a load of those weary legs of yours.");
          break;
        case 1:  
          say("Welcome! Would you like a nice refreshing beer? We have the finest Aldin Ales.");
          break;
        case 2:  
          say("Oh, hello, nice to see you again? How have your travels been?");
          break;
        case 3:  
          say("Hello my friend, how many I serve you?");
          break;
        }
      return true;
      }
    else if(text.contains("job"))
      {
      say("I am the bar maid for this fair tavern. We sell fine beers and food.");
      return true;
      }
    else if(text.contains("help"))
      {
      switch(rand.nextInt(4))
        {
        case 0:        
          say("At the tavern you can get drinks and take a break to meet new people!");
          break;
        case 1:        
          say("If you are looking for adventure there are always adventurers at our tavern.");
          break;
        case 2:        
          say("Help? Are you that desperate for a beer!?");
          break;
        case 3:        
          say("Alcohol is the only help! I will bring you some, just show me your gold!");
          break;
        }
      return true;
      }
    else if(text.contains("quest"))
      {
      switch(rand.nextInt(2))
        {
        case 0:        
          say("Quests? I have heard that Sato is looking for sheep rearers. Go ask him.");
          break;
        case 1:        
          say("The many adventurers that come to our tavern may have something for you.");
          break;
        }
      return true;
      }
    else if(text.contains("beer"))
      {
	    switch(rand.nextInt(3))
        {
        case 0:  
          say("Beer! Excellent choice! Coming right up!");
          break;
        case 1:  
          say("Of course! We have the finest Aldin Ales.");
          break;
        case 2:  
          say("More beer? Naughty! *giggle* Coming right up!");
          break;
        }
      return true;	      
      }
    return false;
    }
    
  protected boolean move()
    {
    Path.followPath(this,SPEED);
    return true;
    }
  }
