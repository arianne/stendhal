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

import games.stendhal.server.entity.creature.Creature;
import java.util.LinkedList;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.server.game.RPWorld;
import org.apache.log4j.Logger;


public class RespawnPoint
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(RespawnPoint.class);
  
  private int x;
  private int y;
  private double radius;

  private int maximum;
  private Creature entity;
  private List<Creature> entities;

  private boolean respawning;
  final public static int TURNSTORESPAWN=9; // Five minute at 300ms
  private int turnsToRespawn;

  private StendhalRPZone zone;

  protected static StendhalRPRuleProcessor rp;
  protected static RPWorld world;

  public static void setRPContext(StendhalRPRuleProcessor rpContext,RPWorld worldContext)
    {
    rp=rpContext;
    world=worldContext;
    }

  public RespawnPoint(int x, int y, int radius)
    {
    this.x=x;
    this.y=y;
    this.radius=radius;
    maximum=0;

    respawning=true;
    turnsToRespawn=TURNSTORESPAWN;
    }

  public void set(StendhalRPZone zone,Creature entity, int maximum)
    {
    this.entity=entity;
    this.entities=new LinkedList<Creature>();
    this.maximum=maximum;
    this.zone=zone;
    }

  public void notifyDead(Creature dead)
    {
    Log4J.startMethod(logger, "notifyDead");
    if(!respawning)
      {
      respawning=true;
      turnsToRespawn=TURNSTORESPAWN;
      }

    entities.remove(dead);
    Log4J.finishMethod(logger, "notifyDead");
    }

  public void nextTurn()
    {
    Log4J.startMethod(logger, "nextTurn");
    if(respawning)
      {
      logger.debug("Turns to respawn: "+turnsToRespawn);
      turnsToRespawn--;
      }

    if(turnsToRespawn==0)
      {
      turnsToRespawn=TURNSTORESPAWN;
      if(entities.size()<maximum)
        {
        respawn();
        }
      else
        {
        respawning=false;
        }
      }

    for(Creature creature: entities)
      {
      creature.logic();
      }

    Log4J.finishMethod(logger, "nextTurn");
    }

  private void respawn()
    {
    Log4J.startMethod(logger, "respawn");
    try
      {
      Creature newentity=entity.getClass().newInstance();

//      // We randomly modify the creature to up to 3 levels up
//      int rand = (new Random()).nextInt(100);
//      int nbLevel = 0;
//      if(rand == 0)
//        {
//        nbLevel = 3;
//        }
//      else if(rand < 10)
//        {
//        nbLevel = 2;
//        }
//      else if(rand < 30)
//        {
//        nbLevel = 1;
//        }
//
//      if(nbLevel>0)
//        {
//        int newLevel = newentity.getLevel() + nbLevel;
//        int newXP = Level.getXP(newLevel);
//        newentity.addXP(newXP - newentity.getXP());
//        }

      zone.assignRPObjectID(newentity);
      StendhalRPAction.placeat(zone,newentity,x,y);

      newentity.setRespawnPoint(this);
      entities.add(newentity);

      zone.add(newentity);
      }
    catch(Exception e)
      {
      logger.error("error respawning entity "+entity,e);
      }
    finally
      {
      Log4J.finishMethod(logger, "respawn");
      }
    }
  }
