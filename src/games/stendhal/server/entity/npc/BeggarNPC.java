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

public class BeggarNPC extends SpeakerNPC 
  {
  final private double SPEED=0.1;
  private Random rand;

  public BeggarNPC() throws AttributeNotFoundException
    {
    super();
    put("class","beggarnpc");
    
    rand=new Random();
    }

  protected void createPath()
    {
    List<Path.Node> nodes=new LinkedList<Path.Node>();
    nodes.add(new Path.Node(22,42));
    nodes.add(new Path.Node(26,42));
    nodes.add(new Path.Node(26,44));
    nodes.add(new Path.Node(31,44));
    nodes.add(new Path.Node(31,42));
    nodes.add(new Path.Node(35,42));
    nodes.add(new Path.Node(35,28));
    nodes.add(new Path.Node(22,28));
    setPath(nodes,true);
    }

  protected boolean chat(Player player) throws AttributeNotFoundException
    {
    String text=player.get("text").toLowerCase();
    if(text.contains("hi"))
      {
      say("Hello my friend! Couldn't ya spare a coin for old man?");
      return true;
      }
    else if(text.contains("job"))
      {
      say("Hehehe! Job! hehehe! Muahahaha!");
      return true;
      }
    else if(text.contains("help"))
      {
      switch(rand.nextInt(4))
        {
        case 0:        
          say("Do you want help? Help Arianne! Rate Stendhal at Happypenguin.org");
          break;
        case 1:        
          say("Do you want help? Help Arianne! Rate Stendhal at freshmeat.net");
          break;
        case 2:        
          say("Do you want help? Help Arianne! Write about it.");
          break;
        case 3:        
          say("Do you want help? Help Arianne! Help them to create new maps, and a big house for me! Muahahaha.");
          break;
        }
      return true;
      }
    else if(text.contains("quest"))
      {
      switch(rand.nextInt(2))
        {
        case 0:        
          say("Ah, quests... just like the old days when I was young! I remember one quest that was about... Oh look, a bird!hmm, what?! Oh, Oops! I forgot it! :(");
          break;
        case 1:
          say("I have been told that on the deepest place of the dungeon under this city someone also buy sheeps, but *it* pays better!.");
          break;
        }
      return true;
      }

    return false;
    }
  }
