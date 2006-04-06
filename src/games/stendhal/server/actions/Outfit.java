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
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class Outfit extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(Outfit.class);

  public static void register()
    {
    StendhalRPRuleProcessor.register("outfit",new Outfit());
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"outfit");

    if(action.has("value"))
      {
      rules.addGameEvent(player.getName(),"outfit",action.get("value"));
      
      player.put("outfit",action.get("value"));
      world.modify(player);
      }

    Log4J.finishMethod(logger,"outfit");
    }
  }
