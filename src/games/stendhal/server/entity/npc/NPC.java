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

import games.stendhal.common.Direction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.pathfinder.Path;

import java.util.List;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.apache.log4j.Logger;


public abstract class NPC extends RPEntity
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(NPC.class);

  private String idea;

  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("npc");
      npc.isA("rpentity");
      npc.add("class",RPClass.STRING);
      npc.add("subclass",RPClass.STRING);
      npc.add("text",RPClass.LONG_STRING, RPClass.VOLATILE);
      npc.add("idea",RPClass.STRING, RPClass.VOLATILE);
      npc.add("outfit",RPClass.INT);
      }
    catch(RPClass.SyntaxException e)
      {
      logger.error("cannot generate RPClass",e);
      }
    }

  public NPC(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    update();
    }

  public NPC() throws AttributeNotFoundException
    {
    super();
    put("type","npc");

    put("x",0);
    put("y",0);
    }

  public void setIdea(String idea)
    {
    if(idea.equals(this.idea))
      {
      return;
      }

    this.idea=idea;
    put("idea",idea);
    }

  public String getIdea()
    {
    return idea;
    }
   
  public void setOutfit(String outfit)
    {
    put("outfit",outfit);
    }

  public void say(String text)
    {
    put("text",text);
    }

  private int escapeCollision;


  /** 
   * Moves to the given entity. When the distance to the destination more than
   * <code>max</code> and this entity does not have a path already one is
   * searched and saved.
   * If the destination is less than min <code>min</code> the path is removed.
   * <p>
   *
   * @param destEntity the destination entity
   * @param min minimum distance to have a path
   * @param max minimum distance to find a path
   */
  public void setAsynchonousMovement(Entity destEntity, double min, double max)
  {
    setAsynchonousMovement(destEntity, min, max, -1.0);
  }
  /** 
   * Moves to the given entity. When the distance to the destination more than
   * <code>max</code> and this entity does not have a path already one is
   * searched and saved.
   * If the destination is less than min <code>min</code> the path is removed.
   * <p>
   *
   * @param destEntity the destination entity
   * @param min minimum distance to have a path
   * @param max minimum distance to find a path
   * @param maxPathRadius the maximum radius in which a path is searched
   */
  public void setAsynchonousMovement(Entity destEntity, double min, double max, double maxPathRadius)
  {
    int destX = destEntity.getx();
    int destY = destEntity.gety();
    if(nextto(destX,destY,min) && hasPath())
    {
      clearPath();
    }

    if(distance(destX,destY) > max && !hasPath())
    {
      Path.searchPathAsynchonous(this, destEntity);
    }
  }

  /** 
   * moves to the given entity. When the distance to the destination is between 
   * <code>min</code> and <code>max</code> and this entity does not have a path 
   * already one is searched and saved.
   * <p>
   * <b>Note:</b> When the distance to the destination is less than <code>min</code>
   *        the path is removed.
   * <b>Warning:</b> The pathfinder is not asynchonous, so this thread is blocked
   *        until a path is found.
   *
   * @param destEntity the destination entity
   * @param min minimum distance to the destination entity
   * @param max maximum distance to the destination entity
   * @param maxPathRadius the maximum radius in which a path is searched
   */
  public void setMovement(Entity destEntity, double min, double max, double maxPathRadius)
    {
    if(nextto(destEntity.getx(),destEntity.gety(),min) && hasPath())
      {
      logger.debug("Removing path because nextto("+destEntity.getx()+","+destEntity.gety()+","+min+") of ("+getx()+","+gety()+")");
      clearPath();
      }

    if(distance(destEntity.getx(),destEntity.gety()) > max && !hasPath())
      {
      logger.debug("Creating path because ("+getx()+","+gety()+") distance("+destEntity.getx()+","+destEntity.gety()+")>"+max);
      List<Path.Node> path=Path.searchPath(this,destEntity,maxPathRadius);
      setPath(path,false);
      }
    }

  /** follows the calculated path. */
  public void moveto(double speed)
  {
    if(hasPath() && Path.followPath(this,speed))
    {
      stop();
      clearPath();
    }
  }

  public void moveRandomly(double speed)
    {
    if(escapeCollision>0) escapeCollision--;

    if(stopped() || collides() || escapeCollision==0)
      {
      setDirection(Direction.rand());
      setSpeed(speed);
      escapeCollision=10;
      }
    }

  abstract public void logic();
  }
