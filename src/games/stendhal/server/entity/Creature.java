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
import games.stendhal.server.*;
import games.stendhal.common.*;
import marauroa.common.*;
import marauroa.common.game.*;

public abstract class Creature extends NPC 
  {
  protected static double SPEED=1;

  private RespawnPoint point;
  private List<Path.Node> patrolPath;
  private RPEntity target;
  
  public Creature(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    init();
    }

  public Creature() throws AttributeNotFoundException
    {
    super();
    init();
    }
  
  private void init()
    {
    patrolPath=new LinkedList<Path.Node>();
    patrolPath.add(new Path.Node(0,0));
    patrolPath.add(new Path.Node(-6,0));
    patrolPath.add(new Path.Node(-6,6));
    patrolPath.add(new Path.Node(0,6));
    }

  public void setRespawnPoint(RespawnPoint point)
    {
    this.point=point;
    }
  
  public RespawnPoint getRespawnPoint()
    {
    return point;
    }

  public void onDead(RPEntity who)
    {
    if(point!=null) point.notifyDead(this);
    super.onDead(who);
    }

  private Player getNearestPlayer(double range)
    {
    int x=getx();
    int y=gety();
    
    double distance=range*range; // We save this way several sqrt operations
    Player chosen=null;
    
    for(Player player: rp.getPlayers())
      {
      if(player.get("zoneid").equals(get("zoneid")))
        {
        int fx=player.getx();
        int fy=player.gety();
        
        if(Math.abs(fx-x)<range && Math.abs(fy-y)<range)
          {
          if(distance(player)<distance)
            {
            chosen=player;
            distance=distance(player);
            }
          }
        }
      }
    
    return chosen;
    }  
  

  public void logic()
    {
    Logger.trace("Creature::logic",">");
    if(!hasPath() && !isAttacking())
      {
      Logger.trace("Creature::logic","D", "Creating Path for this entity");
      List<Path.Node> nodes=new LinkedList<Path.Node>();
      
      int size=patrolPath.size();
      
      for(int i=0; i<size;i++)
        {
        Path.Node actual=patrolPath.get(i);
        Path.Node next=patrolPath.get((i+1)%size);
        
        nodes.addAll(Path.searchPath(this,actual.x+getx(),actual.y+gety(),next.x+getx(),next.y+gety()));
        }
      
      setPath(nodes,true);
      }
    
    if(isAttacked())
      {
      target=this.getAttackSource();
      Logger.trace("Creature::logic","D","Creature("+get("type")+") has been attacked by "+target.get("type"));
      }
    else if(target==null || !target.get("zoneid").equals(get("zoneid")) || !world.has(target.getID()))
      {
      stopAttack();
      target=getNearestPlayer(8);
      if(target!=null)
        {
        Logger.trace("Creature::logic","D","Creature("+get("type")+") gets a new target.");
        }
      }
    
    if(target==null)
      {
      Logger.trace("Creature::logic","D","Following path");
      if(hasPath()) Path.followPath(this,SPEED);
      }
    else if(distance(target)>16*16)
      {
      Logger.trace("Creature::logic","D","Attacker is too far. Creature stops attack");
      clearPath();
      stopAttack();
      stop();
      }
    else if(!nextto(target,0.25) && !target.stopped())
      {
      Logger.trace("Creature::logic","D","Moving to target. Searching new path");
      clearPath();
      setMovement(target,0,0);
      moveto(SPEED);
      }
    else if(nextto(target,0.25))
      {
      Logger.trace("Creature::logic","D","Next to target. Creature stops and attacks");
      stop();
      attack(target);
      }
    else
      {
      Logger.trace("Creature::logic","D","Moving to target. Creature attacks");
      if(collided()) clearPath();
      attack(target);
      setMovement(target,0,0);
      moveto(SPEED);
      }

    if(!stopped())
      {
      StendhalRPAction.move(this);
      }
    
    if(rp.getTurn()%5==0 && isAttacking())
      {
      StendhalRPAction.attack(this,getAttackTarget());
      }
      
    world.modify(this);
    Logger.trace("Creature::logic","<");
    }
  }
