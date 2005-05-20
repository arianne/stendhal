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

public class SellerNPC extends SpeakerNPC 
  {
  final private double SPEED=0.5;
  private int amount;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("sellernpc");
      npc.isA("npc");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("SellerNPC::generateRPClass","X",e);
      }
    }
    
  public SellerNPC() throws AttributeNotFoundException
    {
    super();
    amount=0;
    put("type","sellernpc");
    }

  public void onDead(RPEntity who)
    {
    setHP(getbaseHP());
    world.modify(this);
    }

  public boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("hi"))
      {
      say("Come here to buy your sheeps!");
      return true;
      }
    else if(text.contains("buy"))
      {
      if(!player.hasSheep())
        {
        Logger.trace("SellerNPC::chat","D","Selling a sheep to player");
        say("Congratulations! Here is your sheep!");
        IRPZone zone=world.getRPZone(getID());
        
        Sheep sheep=new Sheep(player);
        sheep.setx(getx());
        sheep.sety(gety()+2);
        zone.assignRPObjectID(sheep);
        
        world.add(sheep);
        
        player.setSheep(sheep);        
        world.modify(player);
        Logger.trace("SellerNPC::chat","D","Sold a sheep to player");
        amount++;
        }
      else
        {
        say("You already have a sheep. Grow it up!");
        }
        
      return true;
      }
    else if(text.equals("help"))
      {
      say("I do sell sheeps, try to BUY me one.");
      return true;
      }
    else if(text.equals("job"))
      {
      say("I sell small sheeps. I have listen that someone buys them when bigger for a nice amount of gold, but it is so boring to grow them up!.");
      return true;
      }
    else if(text.contains("sold"))
      {
      say("I have sold "+amount+" sheeps");
      }
    else if(text.contains("bye"))
      {
      say("Bye "+player.get("name"));
      return true;
      }
    
    return false;
    }

  public boolean move()
    {    
    if(getDirection()==Direction.STOP) 
      {
      setDirection(Direction.DOWN);
      setSpeed(SPEED);
      }
      
    if(gety()<=28) 
      {
      setDirection(Direction.DOWN);
      setSpeed(SPEED);
      return true; 
      }
      
    if(gety()>=32) 
      {
      setDirection(Direction.UP);
      setSpeed(SPEED);
      return true; 
      }

    return false; 
    }
  }
