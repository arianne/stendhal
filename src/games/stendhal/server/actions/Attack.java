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
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class Attack extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

  public static void register()
    {
    StendhalRPRuleProcessor.register("attack",new Attack());
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"attack");
    if(action.has("target"))
      {
      int targetObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);

        if(object instanceof RPEntity) // Disabled Player
          {
          RPEntity entity=(RPEntity)object;
          
          if(!player.equals(entity))
            {
            // Disable attacking NPCS.
            // Just make sure no creature is instanceof SpeakerNPC... 
            if(entity instanceof SpeakerNPC)
              {
              logger.info(player.getName()+" is attacking "+entity.getName());
              return;
              }
              
            // Enabled PVP
            if(entity instanceof Player)
              {
              logger.info(player.getName()+" is attacking "+entity.getName());
              }

            player.bloodHappens();
            entity.bloodHappens();
            
            player.attack(entity);
            world.modify(player);
            }          
          }
        }
      }

    Log4J.finishMethod(logger,"attack");
    }
  }
