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

import games.stendhal.common.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.*;
import java.util.*;

import games.stendhal.server.entity.*;

public class WelcomerNPC extends SpeakerNPC 
  {
  final private double SPEED=0.5;
  private int healed;

  public WelcomerNPC() throws AttributeNotFoundException
    {
    super();
    put("class","welcomernpc");
    healed=0;
    }

  protected void createPath()
    {
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
    if(text.contains("hi")||text.contains("hello"))
      {
      say("Welcome to Stendhal. My name is "+get("name")+" and I will help you, here, in Stendhal's world. What do you need?");
      return true;
      }
    else if(text.contains("job") || text.contains("help"))
      {
      say("I give INDICATIONS, I HEAL and I can tell you about QUESTS that need completing. What do you need?");
      return true;
      }
    else if(text.contains("indication")||text.contains("position"))
      {
      say("At the moment, you are in the City. You can travel West to find and talk with Nishiya to purchase a small sheep. You can travel South into the plains but I warn you, dangerous wolves live there. You can travel a short way East into the Dungeons but they are very dangerous too!. Scary, isn't it?");
      return true;
      }
    else if(text.contains("quest")||text.contains("work"))
      {
      say("I have been told that Sato will reward you for a fully grown sheep.Purchase one from Nishiya and then feed it to make it nice and fat!");
      return true;
      }
    else if(text.contains("healed"))
      {
      say("I have healed "+healed+" players.");
      }
    else if(text.contains("heal"))
      {
      say("Voila! You have been healed.Don't forget to tell your friends about Stendhal :)");
      player.setHP(player.getbaseHP());
      world.modify(player);
      healed++;
      return true;
      }

    return false;
    }
  }
