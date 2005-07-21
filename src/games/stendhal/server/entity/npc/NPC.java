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
import games.stendhal.server.Path;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
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
      npc.add("text",RPClass.LONG_STRING, RPClass.VOLATILE);
      npc.add("idea",RPClass.STRING, RPClass.VOLATILE);
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

  public void say(String text)
    {
    put("text",text);
    }

  private int escapeCollision;


  public void setMovement(Entity entity, double min, double max)
    {
    if(nextto(entity.getx(),entity.gety(),min) && hasPath())
      {
      logger.debug("Removing path because nextto("+entity.getx()+","+entity.gety()+","+min+") of ("+getx()+","+gety()+")");
      clearPath();
      }

    if(distance(entity.getx(),entity.gety())>max && !hasPath())
      {
      logger.debug("Creating path because ("+getx()+","+gety()+") distance("+entity.getx()+","+entity.gety()+")>"+max);
      List<Path.Node> path=Path.searchPath(this,entity);
      setPath(path,false);
      }
    }

  public void moveto(double speed)
    {
    if(hasPath() && Path.followPath(this,speed))
      {
      logger.debug("Removing path because it is completed");
      clearPath();
      stop();
      }
    }

  public void moveRandomly(double speed)
    {
    if(escapeCollision>0) escapeCollision--;

    if(stopped() || collided() || escapeCollision==0)
      {
      setDirection(Direction.rand());
      setSpeed(speed);
      escapeCollision=10;
      }
    }

  public void onDead(RPEntity who)
    {
    super.onDead(who);
    }

  abstract public void logic();
  }
