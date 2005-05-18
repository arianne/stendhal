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
    
  public static boolean attack(RPEntity source,RPEntity target) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Logger.trace("StendhalRPAction::attack",">");
    try
      {
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(source.getID());
      if(!zone.has(target.getID()))
        {
        target.onAttack(source, false);
        world.modify(source);
        return false;
        }
      
      target.onAttack(source, true);
      
      if(source.nextto(target,1))
        {
        int risk=roll1D6()+1-roll1D6();
        source.put("risk",risk);
        
        Logger.trace("StendhalRPAction::attack","D","Risk to strike: "+risk);
        
        if(risk>0) //Hit
          {
          int damage=roll1D6()-3;            
          
          if(damage>0) // Damaged
            {
            target.onDamage(source,damage);              
            source.put("damage",damage);
            }
          else
            {
            source.put("damage",0);
            }
  
          Logger.trace("StendhalRPAction::attack","D","Damage done: "+(damage>0?damage:0));            
          }
        
        world.modify(source);
        return true;      
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
    
  public static void move(RPEntity entity) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPAction::move",">");
    try
      {
      if(entity.stopped())
        {
        return;
        }

      if(!entity.isMoveCompleted())
        {
        Logger.trace("StendhalRPAction::move","D","("+entity.get("type")+") move not completed");
        return;
        }
      
      int x=entity.getx();
      int y=entity.gety();
      
      Direction dir=entity.getDirection();
      int dx=dir.getdx();
      int dy=dir.getdy();
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(entity.getID());
      
      if(entity instanceof Player)
        {
        Player player=(Player)entity;
        
        if(zone.leavesZone(player,x+dx,y+dy))
          {
          Logger.trace("StendhalRPAction::move","D","Leaving zone from ("+x+","+y+") to ("+(x+dx)+","+(y+dy)+")");
          decideChangeZone(player);
          player.stop();
          world.modify(player);
          return;
          }
        }
      
      if(zone.collides(entity,x+dx,y+dy)==false)
        {
        Logger.trace("StendhalRPAction::move","D","Moving from ("+x+","+y+") to ("+(x+dx)+","+(y+dy)+")");
        
        //TODO: Fix me ( world.modify issue )
        entity.setx(x+dx);
        entity.sety(y+dy);
        
        entity.collides(false);
        world.modify(entity);
        }        
      else
        {
        /* Collision */
        Logger.trace("StendhalRPAction::move","D","COLLISION!!! at ("+(x+dx)+","+(y+dy)+")");      
        entity.collides(true);
        
        entity.stop();
        world.modify(entity);
        }
      }
    finally
      {
      Logger.trace("StendhalRPAction::move","<");
      }
    }

  public static void transferContent(Player player) throws AttributeNotFoundException 
    {
    Logger.trace("StendhalRPAction::transferContent",">");

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
    rpman.transferContent(player.getID(),zone.getContents());

    Logger.trace("StendhalRPAction::transferContent","<");
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
    else
      {
      Logger.trace("StendhalRPAction::decideChangeZone","D","Unable to choose a new zone ("+zone.getWidth()+","+zone.getHeight()+")");
      }
    }
    
  public static void changeZone(Player player, String destination) throws AttributeNotFoundException, NoRPZoneException
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
    zone.placeObjectAtZoneChangePoint(player);
      
    int x=player.getInt("x");
    int y=player.getInt("y");
    
    while(zone.collides(player,x,y))
      {
      x=x+(int)(Math.random()*6-3);
      y=y+(int)(Math.random()*6-3);      
      }
    
    player.setx((int)x);
    player.sety((int)y);
      
    if(player.hasSheep())
      {
      Sheep sheep=(Sheep)world.get(player.getSheep());
      while(zone.collides(sheep,x,y))
        {
        x=x+(int)(Math.random()*6-3);
        y=y+(int)(Math.random()*6-3);        
        }
          
      sheep.setx((int)x);
      sheep.sety((int)y);
      sheep.clearPath();
      }
      
    /* There isn't any world.modify because there is already considered inside
     * the implicit world.add call at changeZone */
     player.stop();

    Logger.trace("StendhalRPAction::changeZone","<");
    }
  }
