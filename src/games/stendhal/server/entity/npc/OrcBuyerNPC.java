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
package games.stendhal.server.entity.npc;

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;

import games.stendhal.server.*;
import java.util.*;

import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.*;

public class OrcBuyerNPC extends SpeakerNPC 
  {
  final private double SPEED=0.25;
  
  private int amount;
  
  public OrcBuyerNPC() throws AttributeNotFoundException
    {
    super();
    put("class","orcbuyernpc");
    amount=0;
    }
    
  protected void createPath()
    {
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
      say("Hi human.");
      return true;
      }
    else if(text.contains("sell"))
      {
      if(player.hasSheep())
        {
        Sheep sheep=(Sheep)world.get(player.getSheep());
        if(distance(sheep)>5*5)
          {
          say("*drool* Sheep flesh! Bring da sheep here!");
          }
        else
          {
          say("*LOVELY*. Take dis money!.");
          rp.removeNPC(sheep);
          world.remove(sheep.getID());
          player.removeSheep(sheep);
          
          player.addXP(sheep.getWeight()*20);
          
          world.modify(player);
          amount++;
          }
  
        return true;
        }
      else
        {
        say("Sell what? Don't cheat me or I might 'ave to hurt you!");
        return true;
        }      
      }
    else if(text.contains("help"))
      {
      say("I buy sheep! Sell me sheep! I'm hungry!");
      return true;
      }
    else if(text.contains("job")||text.contains("work")||text.contains("quest"))
      {
      say("Uarhg?! What is job, I want food! Sheep flesh!");
      }    
    else if(text.contains("bought"))
      {
      say("Me have bought "+amount+" sheep(s).");
      }
    else if(text.contains("bye")||text.contains("cya"))
      {
      say("Bye human");
      return true;
      }
    else if(has("text")) 
      {
      remove("text");
      return true;  
      }
      
    return false;
    }
  }
