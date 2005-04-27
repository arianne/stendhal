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

public class SellerNPC extends SpeakerNPC 
  {
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

  public boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("hi"))
      {
      put("text","Come here to buy your sheeps!");
      return true;
      }
    else if(text.contains("buy"))
      {
      if(!player.hasSheep())
        {
        Logger.trace("SellerNPC::chat","D","Selling a sheep to player");
        put("text","Congratulations! Here is your sheep!");
        IRPZone zone=world.getRPZone(getID());
        
        Sheep sheep=new Sheep(player);
        sheep.setx(getx());
        sheep.sety(gety()+2);
        zone.assignRPObjectID(sheep);
        
        rp.addNPC(sheep);
        world.add(sheep);
        
        player.setSheep(sheep);        
        world.modify(player);
        Logger.trace("SellerNPC::chat","D","Sold a sheep to player");
        amount++;
        }
      else
        {
        put("text","You already have a sheep. Grow it up!");
        }
        
      return true;
      }
    else if(text.equals("help"))
      {
      put("text","I do sell sheeps, try to BUY me one.");
      return true;
      }
    else if(text.contains("sold"))
      {
      put("text","I have sold "+amount+" sheeps");
      }
    else if(text.contains("bye"))
      {
      put("text","Bye "+player.get("name"));
      return true;
      }
    
    return false;
    }

  public boolean move()
    {    
    if(getdy()==0)
      {
      setdy(Math.signum(Math.random()-0.5)*0.2);   
      }
      
    if(gety()<=28) 
      {
      setdy(0.1);   
      return true; 
      }
      
    if(gety()>=32) 
      {
      setdy(-0.1);    
      return true; 
      }

    return false; 
    }
  }
