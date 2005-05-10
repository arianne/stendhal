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

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import java.util.*;

import games.stendhal.common.*;
import games.stendhal.server.*;

public class Wolf extends NPC
  {
  private static double SPEED=0.5;
  private List<Path.Node> path;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass wolf=new RPClass("wolf");
      wolf.isA("npc");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Wolf::generateRPClass","X",e);
      }
    }
  
  public Wolf() throws AttributeNotFoundException
    {
    super();
    put("type","wolf");
    put("x",0);
    put("y",0);
    put("dx",0);
    put("dy",0);

    path=new LinkedList<Path.Node>();
    path.add(new Path.Node(0,0));
    path.add(new Path.Node(-15,0));
    path.add(new Path.Node(-15,15));
    path.add(new Path.Node(0,15));

    Logger.trace("Wolf::Wolf","D","Created Wolf: "+this.toString());
    }
  
  public Sheep getNearestSheep(double range)
    {
    double x=getx();
    double y=gety();
    
    double distance=range*range; // We save this way several sqrt operations
    Sheep chosen=null;
    
    for(RPEntity entity: rp.getNPCs())
      {
      if(entity instanceof Sheep && entity.get("zoneid").equals(get("zoneid")))
        {
        double ex=entity.getx();
        double ey=entity.gety();
        
        if(Math.abs(ex-x)<range && Math.abs(ey-y)<range)
          {
          if(this.distance(entity)<distance)
            {
            chosen=(Sheep)entity;
            distance=this.distance(entity);
            }
          }
        }
      }
    
    return chosen;
    }
  
  private int escapeCollision;
  
  public void logic()
    {
    Logger.trace("Wolf::logic",">");
    if(!hasPath())
      {
      List<Path.Node> nodes=new LinkedList<Path.Node>();
      for(Path.Node node: path)
        {
        nodes.add(new Path.Node(node.x+getx(),node.y+gety()));
        }
      setPath(nodes,true);
      }
    
    if(isAttacked() && !isAttacking())
      {
      stop();
      clearPath();
      
      RPEntity target=getAttackSource();
      attack(target);

      setMovement(target.getx(),target.gety(),0,0);
      moveto(SPEED);

      world.modify(this);
      }    
    else if(isAttacked())
      {      
      RPEntity target=getAttackSource();
      if(nextto(target,1))
        {
        stop();
        clearPath();
        
        world.modify(this);
        }
      else
        {
        world.modify(this);
        setMovement(target.getx(),target.gety(),0,0);
        moveto(SPEED);
        }
      } 
    else
      {
      Sheep sheep=getNearestSheep(10);
      if(sheep!=null)
        {
        if(sheep.nextto(this,1))
          {
          attack(sheep);
          }
        else
          {
          setMovement(sheep.getx(),sheep.gety(),0,0);
          moveto(SPEED);
          }        
        }
      else
        {
        if(escapeCollision>0) escapeCollision--;
    
        if(collided() && escapeCollision==0)
          {
          moveRandomly(SPEED);
          }
        else if(escapeCollision==0)
          {
          Path.followPath(this,SPEED);
          }
        }
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      }
    
    if(rp.getTurn()%5==0 && isAttacking())
      {
      StendhalRPAction.attack(this,getAttackTarget());
      }
      
    Logger.trace("Wolf::logic","<");
    }
  }
