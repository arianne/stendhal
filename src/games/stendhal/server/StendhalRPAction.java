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
import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObjectNotFoundException;
import marauroa.server.game.NoRPZoneException;
import marauroa.server.game.RPServerManager;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class StendhalRPAction
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(StendhalRPAction.class);
  
  /** server manager */
  private static RPServerManager rpman;
  
  /** the rule processor. it is not used at the moment */
  @SuppressWarnings("unused")
  private static StendhalRPRuleProcessor rules;
  
  /** our world */
  private static RPWorld world;

  public static void initialize(RPServerManager rpman, StendhalRPRuleProcessor rules, RPWorld world)
    {
    StendhalRPAction.rpman=rpman;
    StendhalRPAction.rules=rules;
    StendhalRPAction.world=world;
    }

  public static boolean attack(RPEntity source,RPEntity target) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Log4J.startMethod(logger, "attack");
    try
      {
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(source.getID());
      if(!zone.has(target.getID()) || target.getHP()==0)
        {
        logger.debug("Attack from "+source+" to "+target+" stopped because target was lost("+zone.has(target.getID())+") or dead.");
        target.onAttack(source, false);
        world.modify(source);
        return false;
        }

      target.onAttack(source, true);

      if(source.nextto(target,1))
        {
        int roll=Rand.roll1D20();
        int risk=0;
        
        risk=2*source.getATK()-target.getDEF()+roll-10;        
        
        if(risk<0)
          {
          risk=0;
          }
        
        if(risk>1)
          {
          risk=1;
          }
        
        logger.debug("attack from "+source+" to "+target+": Risk to strike: "+risk);
        source.put("risk",risk);

        int damage=0;
        
        if((target instanceof SpeakerNPC)==false)
          {
          // disabled attack xp for attacking NPC's
          source.incATKXP();
          }
        
        if(risk>0) //Hit
          {
          target.incDEFXP();
          
          int weapon=0;
          int shield=0;
          int armor=0;
          
          if(source.hasWeapon())
            {
            weapon=source.getWeapon().getAttack();
            }

          float maxDamage=(float)source.getATK()*(float)source.getATK()+4.0f*(float)source.getATK()*(float)weapon;
          float attackerComponent=0.8f*(float)Rand.roll1D100()/100.0f*(float)source.getATK()*(float)source.getATK()+4.0f*(float)source.getATK()*(float)weapon;

          if(target.hasShield())
            {
            shield=target.getShield().getDefense();
            }

          if(target.hasArmor())
            {
            armor=target.getArmor().getDefense();
            }

          float defenderComponent=0.6f*(float)Rand.roll1D100()/100.0f*(float)target.getDEF()*(float)target.getDEF()+4.0f*(float)target.getDEF()*(float)shield+2.0f*(float)target.getDEF()*(float)armor;
          
          damage=(int)(((attackerComponent-defenderComponent)/maxDamage)*(maxDamage/(float)source.getATK()));

          if(damage>0) // Hit
            {
            target.onDamage(source,damage);
            source.put("damage",damage);
            logger.debug("attack from "+source.getID()+" to "+target.getID()+": Damage: "+damage);
            }
          else // Blocked
            {
            source.put("damage",0);
            logger.debug("attack from "+source.getID()+" to "+target.getID()+": Damage: "+0);
            }
          }
        else // Missed
          {
          logger.debug("attack from "+source.getID()+" to "+target.getID()+": Missed");
          source.put("damage",0);
          }

        world.modify(source);
        return true;
        }
      else
        {
        logger.debug("Attack from "+source+" to "+target+" failed because target is not near.");
        return false;
        }
      }
    finally
      {
      Log4J.finishMethod(logger, "attack");
      }
    }

  public static void move(RPEntity entity) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger, "move");
    try
      {
      if(entity.stopped())
        {
        return;
        }

      if(!entity.isMoveCompleted())
        {
        logger.debug(entity.get("type")+") move not completed");
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
        logger.debug("Moving from ("+x+","+y+") to ("+(x+dx)+","+(y+dy)+")");

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
              logger.debug("Leaving zone from ("+x+","+y+") to ("+(x+dx)+","+(y+dy)+")");
              decideChangeZone(player);
              player.stop();
              world.modify(player);
              return;
              }
    
            for(Portal portal: zone.getPortals())
              {
              if(player.nextto(portal,0.25) && player.facingto(portal))
                {
                logger.debug("Using portal "+portal);
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
        logger.debug("Collision at ("+(x+dx)+","+(y+dy)+")");
        entity.collides(true);

        entity.stop();
        world.modify(entity);
        }
      }
    finally
      {
      Log4J.finishMethod(logger, "move");
      }
    }

  public static void transferContent(Player player) throws AttributeNotFoundException
    {
    Log4J.startMethod(logger, "transferContent");

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
    rpman.transferContent(player.getID(),zone.getContents());

    Log4J.finishMethod(logger, "transferContent");
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
      logger.warn("Unable to choose a new zone from ("+zoneid+":"+zone.getWidth()+","+zone.getHeight()+")");
      }
    }

  public static boolean usePortal(Player player, Portal portal) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger, "usePortal");

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

    logger.debug("Place player");
    placeat(zone,player,dest.getInt("x"),dest.getInt("y"));
    player.stop();

    if(player.hasSheep())
      {
      logger.debug("Place sheep");
      Sheep sheep=(Sheep)world.get(player.getSheep());
      placeat(zone,sheep,player.getInt("x")+1,player.getInt("y")+1);
      sheep.clearPath();
      sheep.stop();
      }

    /* There isn't any world.modify because there is already considered inside
     * the implicit world.add call at changeZone */

    Log4J.finishMethod(logger, "usePortal");
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

      logger.debug("Unable to place "+entity+" at ("+x+","+y+")");
      }
    else
      {
      entity.setx(x);
      entity.sety(y);
      }
    }

  public static void changeZone(Player player, String destination) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger, "changeZone");

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

    Log4J.finishMethod(logger, "changeZone");
    }
  }
