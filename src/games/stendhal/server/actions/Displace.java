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


import org.apache.log4j.Logger;

import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;

import marauroa.common.Log4J;

public class Displace extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

  public static void register()
    {
    StendhalRPRuleProcessor.register("displace",new Displace());
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"displace");
    if(action.has("target"))
      {
      int targetObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof RPEntity) /** Player, Creatures and NPCs */
          {
          RPEntity entity=(RPEntity)object;
          if(player.nextto(entity,0.25))
            {
            if(action.has("x") && action.has("y"))
              {
              int x=action.getInt("x");
              int y=action.getInt("y");
              
              /** TODO: Code displace here */
              }
            }
          }
        else if(object instanceof PassiveEntity)
          {
          if(action.has("x") && action.has("y"))
            {
            int x=action.getInt("x");
            int y=action.getInt("y");

            PassiveEntity entity=(PassiveEntity)object;

            if(player.nextto(entity,0.25) && player.distance(x,y)<8*8 && !zone.simpleCollides(entity,x,y))
              {
              entity.setx(x);
              entity.sety(y);
              world.modify(entity);
              }
            }
          }
        }
      }

    Log4J.finishMethod(logger,"displace");
    }
  }
