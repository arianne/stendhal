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
package games.stendhal.server;

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;

import games.stendhal.common.*;
import games.stendhal.server.entity.*;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.*;

import games.stendhal.server.entity.npc.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.creature.*;

public class StendhalRPAction
  {
  private static RPServerManager rpman;
  private static StendhalRPRuleProcessor rules;
  private static RPWorld world;

  public static void initialize(RPServerManager rpman, StendhalRPRuleProcessor rules, RPWorld world)
    {
    StendhalRPAction.rpman=rpman;
    StendhalRPAction.rules=rules;
    StendhalRPAction.world=world;
    }

  /** This method returns the average damage done by Entity1 to Entity2 considering its levels.
   *  It is a way of determine optimal XP. */
  public static double averageDamageAttack(double atk1, double def1, double atk2, double def2)
    {
    double damage = 0;

    for(int d1 = 1; d1 < 7; d1++)
      {
      for(int d2 = 0; d2 < 7; d2++)
        {
        if(d1 == 6 && d2 == 6) damage += atk1;
        else
          {
          double risk = atk1 - def2/6 - d1;
          double dam = atk1/6 - def2 + d2;
          if(risk > 0 && dam > 0) damage += dam;
          }
        }
      }
      
    return damage/36.0;
    }


  public static boolean attack(RPEntity source,RPEntity target) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Logger.trace("StendhalRPAction::attack",">");
    try
      {
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(source.getID());
      if(!zone.has(target.getID()))
        {
        target.onAttack(source, false);
        world.modify(source);
        return false;
        }

      target.onAttack(source, true);

      if(source.nextto(target,1))
        {
        int roll=Rand.roll1D20();
        int risk=0;
        
        if(roll>18) // Critical success
          {
          risk=1;
          }
        else if(roll<2) // Critical failure
          {
          risk=0;
          }
        else
          {
          risk=source.getATK()-target.getDEF()+roll-10;
          }
        
        Logger.trace("StendhalRPAction::attack","D","Risk to strike: "+risk);
        source.put("risk",risk);

        int damage=0;

        if(risk>0) //Hit
          {  
          //TODO: Code attack
          int weapon=0;
          int shield=0;
          int armor=0;
          
          if(source.hasWeapon())
            {
            weapon=source.getWeapon().getATK();
            }

          for(int i=0;i<source.getATK()+weapon;i++)
            {
            damage+=Rand.roll1D6();
            }
            
          if(target.hasShield())
            {
            shield=target.getShield().getDEF();
            }

          if(target.hasArmor())
            {
            armor=target.getArmor().getDEF();
            }
          
          for(int i=0;i<source.getDEF()+shield+armor*2;i++)
            {
            damage-=Rand.roll1D6();
            }
          
          damage=damage>>2;

          if(damage>0) // Hit
            {
            target.onDamage(source,damage);
            source.put("damage",damage);
            Logger.trace("StendhalRPAction::attack","D","Damage done: "+(damage>0?damage:0));
            }
          else // Blocked
            {
            source.put("damage",0);
            }
          }
        else // Missed
          {
          source.put("damage",0);
          }

        world.modify(source);
        return true;
        }
      else
        {
        return false;
        }
      }
    finally
      {
      Logger.trace("StendhalRPAction::attack","<");
      }
    }

  public static void move(RPEntity entity) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPAction::move",">");
    try
      {
      if(entity.stopped())
        {
        return;
        }

      if(!entity.isMoveCompleted())
        {
        Logger.trace("StendhalRPAction::move","D","("+entity.get("type")+") move not completed");
        return;
        }

      int x=entity.getx();
      int y=entity.gety();

      Direction dir=entity.getDirection();
      int dx=dir.getdx();
      int dy=dir.getdy();

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(entity.getID());

      if(zone.collides(entity,x+dx,y+dy)==false)
        {
        Logger.trace("StendhalRPAction::move","D","Moving from ("+x+","+y+") to ("+(x+dx)+","+(y+dy)+")");

        entity.setx(x+dx);
        entity.sety(y+dy);

        entity.collides(false);
        world.modify(entity);
        }
      else
        {
        if(entity instanceof Player)
          {
          Player player=(Player)entity;

          // If we are too far from sheep skip zone change
          Sheep sheep=null;
          if(player.hasSheep())
            {
            sheep=(Sheep)world.get(player.getSheep());
            }

          if(!(sheep!=null && player.distance(sheep)>7*7))
            {
            if(zone.leavesZone(player,x+dx,y+dy))
              {
              Logger.trace("StendhalRPAction::move","D","Leaving zone from ("+x+","+y+") to ("+(x+dx)+","+(y+dy)+")");
              decideChangeZone(player);
              player.stop();
              world.modify(player);
              return;
              }
    
            for(Portal portal: zone.getPortals())
              {
              if(player.nextto(portal,0.25) && player.facingto(portal))
                {
                Logger.trace("StendhalRPAction::move","D","Using portal "+portal);
                if(usePortal(player, portal))
                  {
                  transferContent(player);
                  }
                return;
                }
              }
            }
          }

        /* Collision */
        Logger.trace("StendhalRPAction::move","D","Collision: at ("+(x+dx)+","+(y+dy)+")");
        entity.collides(true);

        entity.stop();
        world.modify(entity);
        }
      }
    finally
      {
      Logger.trace("StendhalRPAction::move","<");
      }
    }

  public static void transferContent(Player player) throws AttributeNotFoundException
    {
    Logger.trace("StendhalRPAction::transferContent",">");

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
    rpman.transferContent(player.getID(),zone.getContents());

    Logger.trace("StendhalRPAction::transferContent","<");
    }

  public static void decideChangeZone(Player player) throws AttributeNotFoundException, NoRPZoneException
    {
    String zoneid=player.get("zoneid");
    int x=player.getx();
    int y=player.gety();

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());

    if(zoneid.equals("village") && x>zone.getWidth()-4)
      {
      changeZone(player,"city");
      transferContent(player);
      }
    else if(zoneid.equals("city") && x<4)
      {
      changeZone(player,"village");
      transferContent(player);
      }
    else if((zoneid.equals("city") || zoneid.equals("village")) && y>zone.getHeight()-4)
      {
      if(zoneid.equals("city")) player.setx(player.getx()+64);
      changeZone(player,"plains");
      transferContent(player);
      }
    else if(zoneid.equals("plains") && y<4 && x<zone.getWidth()/2)
      {
      changeZone(player,"village");
      transferContent(player);
      }
    else if(zoneid.equals("plains") && y<4 && x>=zone.getWidth()/2)
      {
      changeZone(player,"city");
      transferContent(player);
      }
    else if(zoneid.equals("plains") && y>zone.getHeight()-4)
      {
      changeZone(player,"forest");
      transferContent(player);
      }
    else if(zoneid.equals("forest") && y<4)
      {
      changeZone(player,"plains");
      transferContent(player);
      }
    else
      {
      Logger.trace("StendhalRPAction::decideChangeZone","D","Unable to choose a new zone ("+zone.getWidth()+","+zone.getHeight()+")");
      }
    }

  public static boolean usePortal(Player player, Portal portal) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPAction::usePortal",">");

    if(!player.nextto(portal,0.25)) // Too far to use the portal
      {
      return false;
      }

    StendhalRPZone destZone=(StendhalRPZone)world.getRPZone(new IRPZone.ID(portal.getDestinationZone()));
    Portal dest=destZone.getPortal(portal.getDestinationNumber());

    String source=player.getID().getZoneID();

    if(player.hasSheep())
      {
      Sheep sheep=(Sheep)world.get(player.getSheep());

      player.removeSheep(sheep);
      
      world.changeZone(source,portal.getDestinationZone(),sheep);
      world.changeZone(source,portal.getDestinationZone(),player);

      player.setSheep(sheep);
      player.stop();
      player.stopAttack();
      }
    else
      {
      world.changeZone(source,portal.getDestinationZone(),player);
      }

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());

    Logger.trace("StendhalRPAction::usePortal","D","Place player");
    placeat(zone,player,dest.getInt("x"),dest.getInt("y"));
    player.stop();

    if(player.hasSheep())
      {
      Logger.trace("StendhalRPAction::usePortal","D","Place sheep");
      Sheep sheep=(Sheep)world.get(player.getSheep());
      placeat(zone,sheep,player.getInt("x")+1,player.getInt("y")+1);
      sheep.clearPath();
      sheep.stop();
      }

    /* There isn't any world.modify because there is already considered inside
     * the implicit world.add call at changeZone */

    Logger.trace("StendhalRPAction::usePortal","<");
    return true;
    }

  public static void placeat(StendhalRPZone zone, Entity entity, int x, int y)
    {
    if(zone.collides(entity,x,y))
      {
      for(int k=2;k<5;k++)
        {
        for(int i=-k;i<k;i++)
          {
          for(int j=-k;j<k;j++)
            {
            if(!zone.collides(entity,x+i,y+j))
              {
              entity.setx(x+i);
              entity.sety(y+j);
              return;
              }
            }
          }
        }

      Logger.trace("StendhalRPAction::placeat","D","Unable to place "+entity+" at ("+x+","+y+")");
      }
    else
      {
      entity.setx(x);
      entity.sety(y);
      }
    }

  public static void changeZone(Player player, String destination) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPAction::changeZone",">");

    String source=player.getID().getZoneID();

    StendhalRPZone oldzone=(StendhalRPZone)world.getRPZone(player.getID());

    if(player.hasSheep())
      {
      Sheep sheep=(Sheep)world.get(player.getSheep());
      
      player.removeSheep(sheep);
      
      world.changeZone(source,destination,sheep);
      world.changeZone(source,destination,player);

      player.setSheep(sheep);
      }
    else
      {
      world.changeZone(source,destination,player);
      }

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
    zone.placeObjectAtZoneChangePoint(oldzone,player);

    placeat(zone,player,player.getInt("x"),player.getInt("y"));
    player.stop();
    player.stopAttack();

    if(player.hasSheep())
      {
      Sheep sheep=(Sheep)world.get(player.getSheep());
      placeat(zone,sheep,player.getInt("x")+1,player.getInt("y")+1);
      sheep.clearPath();
      sheep.stop();
      }

    /* There isn't any world.modify because there is already considered inside
     * the implicit world.add call at changeZone */

    Logger.trace("StendhalRPAction::changeZone","<");
    }
  }
