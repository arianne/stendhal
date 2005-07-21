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

import games.stendhal.server.Path;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Sheep;
import java.util.LinkedList;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import org.apache.log4j.Logger;

public class ButcherNPC extends SpeakerNPC 
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(ButcherNPC.class);

  final private double SPEED=0.2;
  private int amount;
  
  public ButcherNPC() throws AttributeNotFoundException
    {
    super();
    amount=0;
    put("class","butchernpc");
    }

  protected void createPath()
    {
    List<Path.Node> nodes=new LinkedList<Path.Node>();
    nodes.add(new Path.Node(22,28));
    nodes.add(new Path.Node(26,28));
    nodes.add(new Path.Node(26,30));
    nodes.add(new Path.Node(31,30));
    nodes.add(new Path.Node(31,28));
    nodes.add(new Path.Node(35,28));
    nodes.add(new Path.Node(35,14));
    nodes.add(new Path.Node(22,14));
    setPath(nodes,true);
    }

  public boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("hi"))
      {
      say("Come here and buy your lambs!Feed them up and sell them for a profit!");
      return true;
      }
    else if(text.contains("buy"))
      {
      if(!player.hasSheep())
        {
        logger.debug("Selling a sheep to player");
        say("Congratulations! Here is your sheep!Keep it safe!");
        IRPZone zone=world.getRPZone(getID());
        
        Sheep sheep=new Sheep(player);
        sheep.setx(getx());
        sheep.sety(gety()+2);
        zone.assignRPObjectID(sheep);
        
        world.add(sheep);
        
        player.setSheep(sheep);        
        world.modify(player);
        logger.debug("Sold a sheep to player");
        amount++;
        }
      else
        {
        say("You already have a sheep. Take care of it first!");
        }
        
      return true;
      }
    else if(text.contains("help"))
      {
      say("I sell sheep, try to BUY one from me.");
      return true;
      }
    else if(text.contains("job"))
      {
      say("I sell lambs. I have heard that someone buys them for meat when they are nice and plump for a good amount of gold.  But it is so boring to raise them that I will leave it up to you!");
      return true;
      }
    else if(text.contains("sold"))
      {
      say("I have sold "+amount+" sheep(s).");
      }
    else if(text.contains("bye"))
      {
      say("Bye "+player.get("name"));
      return true;
      }
    
    return false;
    }
  }
