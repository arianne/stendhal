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

public class StendhalRPAction 
  {
  private static RPServerManager rpman;
  private static RPWorld world;
  
  public static int roll1D6()
    {
    return Math.round((float)(Math.random()*5.0))+1;
    }
    
  public static void initialize(RPServerManager rpman, RPWorld world)
    {
    StendhalRPAction.rpman=rpman;
    StendhalRPAction.world=world;
    }
    
  static void face(ActiveEntity entity,double dx,double dy)
    {
    if(dx!=0)
      {
      if(dx<0)
        {
        Logger.trace("StendhalRPAction::face","D","Facing LEFT");
        entity.setFacing(0);
        }
      else
        {
        Logger.trace("StendhalRPAction::face","D","Facing RIGHT");
        entity.setFacing(1);
        }
      }
      
    if(dy!=0)
      {
      if(dy<0)
        {
        Logger.trace("StendhalRPAction::face","D","Facing UP");
        entity.setFacing(2);
        }
      else
        {
        Logger.trace("StendhalRPAction::face","D","Facing DOWN");
        entity.setFacing(3);
        }
      }
    }
    
  static void leaveZone(Player player) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPAction::leaveZone",">");
    try
      {
      double x=player.getx();
      double y=player.gety();
      double dx=player.getdx();
      double dy=player.getdy();
      boolean stopped=player.stopped();
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      
      if(zone.leavesZone(player,x+dx,y+dy))
        {
        if(!player.hasLeave())
          {
          player.setLeave(10);
          }
          
        int turnsToLeave=player.getLeave(); 
        if(turnsToLeave==0)
          {
          player.setLeave(-1);
          decideChangeZone(player);
          return;
          }
        
        player.setLeave(turnsToLeave-1);
          
        world.modify(player);
        }
      else
        {
        player.setLeave(-1);
        }
      }
    finally
      {
      Logger.trace("StendhalRPAction::leaveZone","<");
      }
    }
 
  static boolean attack(Player player,int targetObject) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Logger.trace("StendhalRPAction::attack",">");
    try
      {
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object.has("hp")) //Instance of RPEntity 
          {
          RPEntity target=(RPEntity)object;
          
          int risk=roll1D6()+1-roll1D6();
          player.put("risk",risk);
          
          Logger.trace("StendhalRPAction::attack","D","Risk to strike: "+risk);
          
          if(risk>0) //Hit
            {
            int damage=roll1D6()-3;            
            
            if(damage>0) // Damaged
              {
              target.onDamage(player,damage);              
              player.put("damage",damage);
              }
            else
              {
              player.put("damage",0);
              }

            Logger.trace("StendhalRPAction::attack","D","Damage done: "+(damage>0?damage:0));            
            }
          
          world.modify(player);
          return true;
          }
        else
          {
          player.remove("target");
          return false;
          }
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

  static void move(ActiveEntity entity) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPAction::move",">");
    try
      {
      double x=entity.getx();
      double y=entity.gety();
      double dx=entity.getdx();
      double dy=entity.getdy();
      boolean stopped=entity.stopped();
      
      if(stopped)
        {
        return;
        }
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(entity.getID());
      
      if(entity instanceof Player)
        {
        Player player=(Player)entity;
        
        if(zone.leavesZone(player,x+dx,y+dy))
          {
          if(!player.hasLeave()) 
            {
            player.setLeave(10);
            }
          }
        }
      
      if(zone.collides(entity,x+dx,y+dy)==false)
        {
        Logger.trace("StendhalRPAction::move","D","Moving from ("+x+","+y+") to ("+(x+dx)+","+(y+dy)+")");
        if(dx!=0) entity.setx(x+dx);
        if(dy!=0) entity.sety(y+dy);
        entity.collides(false);
        world.modify(entity);
        }        
      else
        {
        /* Collision */
        Logger.trace("StendhalRPAction::move","D","COLLISION!!! at ("+(x+dx)+","+(y+dy)+")");      
        entity.collides(true);
        // HACK: Needed to make 0.02 client to work. FIXME
        entity.setx(x);
        entity.sety(y);
        entity.stop();
        world.modify(entity);
        }
      }
    finally
      {
      Logger.trace("StendhalRPAction::move","<");
      }
    }

  static void transferContent(Player player) throws AttributeNotFoundException 
    {
    Logger.trace("StendhalRPAction::transferContent",">");

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
    rpman.transferContent(player.getID(),zone.getContents());

    Logger.trace("StendhalRPAction::transferContent","<");
    }

  static void decideChangeZone(Player player) throws AttributeNotFoundException, NoRPZoneException
    {
    String zoneid=player.get("zoneid");
    double x=player.getx();
    double y=player.gety();

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
    
    if(zoneid.equals("village") && x>zone.getWidth()-2) 
      {
      changeZone(player,"city");
      transferContent(player);
      }
    else if(zoneid.equals("city") && x<1) 
      {
      changeZone(player,"village");
      transferContent(player);
      }
    }
    
  static void changeZone(Player player, String destination) throws AttributeNotFoundException, NoRPZoneException
    {    
    Logger.trace("StendhalRPAction::changeZone",">");

    String source=player.getID().getZoneID();
    
    if(player.hasSheep())
      {
      Sheep sheep=(Sheep)world.get(player.getSheep());
      world.changeZone(source,destination,sheep);    
      world.changeZone(source,destination,player);    
      
      player.setSheep(sheep);
      }
    else
      {
      world.changeZone(source,destination,player);    
      }
    
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
    zone.placeObjectAtEntryPoint(player);
      
    double x=player.getDouble("x");
    double y=player.getDouble("y");
    
    while(zone.collides(player,x,y))
      {
      x=x+(Math.random()*6-3);
      y=y+(Math.random()*6-3);      
      }
    
    player.setx(x);
    player.sety(y);
      
    /* There isn't any world.modify because there is already considered inside
     * the implicit world.add call at changeZone */
     player.stop();

    Logger.trace("StendhalRPAction::changeZone","<");
    }
  }
