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
package games.stendhal.server.entity;

import java.util.*;
import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.*;
import games.stendhal.common.*;

public abstract class NPC extends RPEntity
  {
  private String idea;

  public static void generateRPClass()
    {
    try
      {
      RPClass npc=new RPClass("npc");
      npc.isA("rpentity");
      npc.add("text",RPClass.LONG_STRING, RPClass.VOLATILE);
      npc.add("idea",RPClass.STRING, RPClass.VOLATILE);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("NPC::generateRPClass","X",e);
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
      Logger.trace("NPC::setMovement","D","Removing path because nextto("+entity.getx()+","+entity.gety()+","+min+") of ("+getx()+","+gety()+")");
      clearPath();
      }

    if(distance(entity.getx(),entity.gety())>max && !hasPath())
      {
      Logger.trace("NPC::setMovement","D","Creating path because ("+getx()+","+gety()+") distance("+entity.getx()+","+entity.gety()+")>"+max);
      List<Path.Node> path=Path.searchPath(this,entity);
      setPath(path,false);
      }
    }

  public void moveto(double speed)
    {
    if(hasPath() && Path.followPath(this,speed))
      {
      Logger.trace("NPC::moveto","D","Removing path because it is completed");
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
