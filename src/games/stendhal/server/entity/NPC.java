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
      npc.add("text",RPClass.STRING);
      npc.add("idea",RPClass.STRING);
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
    this.idea=idea;
    put("idea",idea);
    }
  
  public String getIdea()
    {
    return idea;
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


//  public void setMovement(int x, int y, double min, double max)
//    {
//    if(nextto(x,y,min) && hasPath())
//      {
//      Logger.trace("Sheep::setMovement","D","Removing path because nextto("+x+","+y+","+min+") of ("+getx()+","+gety()+")");
//      clearPath();
//      }
//
//    if((distance(x,y)>max && !hasPath()) || escapeCollision>20)
//      {
//      Logger.trace("Sheep::setMovement","D","Creating path because escapeCollision="+escapeCollision);
//      List<Path.Node> path=Path.searchPath(this,x,y);
//      setPath(path,false);
//      escapeCollision=6;
//      }
//    }
//    
//  public void moveto(double speed)
//    {
//    if(escapeCollision>0) escapeCollision--;
//    
//    if(hasPath() && collided())
//      {
//      moveRandomly(speed);
//      }
//    else if((escapeCollision==0 && hasPath()) || (hasPath() && Path.followPath(this,speed)))
//      {
//      Logger.trace("Sheep::moveto","D","Removing path because escapeCollision="+escapeCollision);
//      clearPath();
//      }
//    }

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