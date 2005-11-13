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
package games.stendhal.server.actions;


import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Sheep;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class PlayersQuery extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

  public static void register()
    {
    PlayersQuery query=new PlayersQuery();
    StendhalRPRuleProcessor.register("who",query);
    StendhalRPRuleProcessor.register("where",query);    
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    if(action.get("type").equals("who"))
      {
      onWho(world,rules,player,action);
      }
    else
      {
      onWhere(world,rules,player,action);
      }    
    }

  public void onWho(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"who");
    
    String online = "" + rules.getPlayers().size() + " Players online: ";
    for(Player p : rules.getPlayers())
      {
      online += p.getName() + "(" + p.getLevel() +") ";
      }
    player.setPrivateText(online);
    world.modify(player);
    
    rules.removePlayerText(player);
    
    Log4J.finishMethod(logger,"who");
    }

  public void onWhere(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"where");

    if(action.has("target"))
      {
      String who=action.get("target");
      for(Player p : rules.getPlayers())
        {
        if(p.getName().equals(who))
          {
          player.setPrivateText(p.getName() + " is in "+p.get("zoneid")+" at ("+p.getx()+","+p.gety()+")");
          world.modify(player);

          rules.removePlayerText(player);
          return;
          }
        }
      
      if(who.equals("sheep") && player.hasSheep())
        {
        Sheep sheep=(Sheep)world.get(player.getSheep());
        player.setPrivateText("sheep is in "+sheep.get("zoneid")+" at ("+sheep.getx()+","+sheep.gety()+")");
        world.modify(player);

        rules.removePlayerText(player);
        return;
        }
        
      player.setPrivateText(action.get("target") + " is not currently logged.");
      rules.removePlayerText(player);
      }

    Log4J.finishMethod(logger,"where");
    }
  }
