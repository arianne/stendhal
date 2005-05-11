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
    put("dx",0);
    put("dy",0);
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
  private int numCollision;
  
  public void setMovement(double x, double y, double min, double max)
    {
    if(nextto(x,y,min) && this.hasPath())
      {
      clearPath();
      }

    if((distance(x,y)>max && !hasPath()) || numCollision>20)
      {
      List<Path.Node> path=Path.searchPath(this,x,y);
      setPath(path,false);
      numCollision=0;
      }
    }
    
  public void moveto(double speed)
    {
    if(escapeCollision>0) escapeCollision--;
    
    if(hasPath() && collided())
      {
      numCollision++;
      setdx(Math.random()*speed*2-speed);
      setdy(Math.random()*speed*2-speed);
      escapeCollision=6;
      }
    else if(escapeCollision==0 && hasPath() && Path.followPath(this,speed))
      {
      clearPath();
      }
    }

  public void moveRandomly(double speed)
    {
    if(escapeCollision>0) escapeCollision--;
      
    if(stopped() || collided() || escapeCollision==0)
      {
      setdx(Math.random()*speed*2-speed);
      setdy(Math.random()*speed*2-speed);
      escapeCollision=10;
      }
    }

  public void onDead(RPEntity who)
    {
    super.onDead(who);
    }
    
  abstract public void logic();
  }