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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class StendhalRPAction 
  {
  private static RPServerManager rpman;
  private static RPWorld world;
  
  public static void initialize(RPServerManager rpman, RPWorld world)
    {
    StendhalRPAction.rpman=rpman;
    StendhalRPAction.world=world;
    }
    
  private static Rectangle2D getCollisionArea(String type, double x, double y)
    {
    if(type.equals("player"))
      {
      return new Rectangle.Double(x+0.5,y+1.3,0.87,0.6);
      }
    else
      {
      return new Rectangle.Double(x,y,1,2);
      }
    }

  static void face(RPObject object,double dx,double dy) throws Exception
    {
    if(dx!=0)
      {
      if(dx<0)
        {
        Logger.trace("StendhalRPAction::face","D","Facing LEFT");
        object.put("dir",0);
        }
      else
        {
        Logger.trace("StendhalRPAction::face","D","Facing RIGHT");
        object.put("dir",1);
        }
      }
      
    if(dy!=0)
      {
      if(dy<0)
        {
        Logger.trace("StendhalRPAction::face","D","Facing UP");
        object.put("dir",2);
        }
      else
        {
        Logger.trace("StendhalRPAction::face","D","Facing DOWN");
        object.put("dir",3);
        }
      }
    }
    
  static boolean move(RPObject object) throws Exception
    {
    Logger.trace("StendhalRPAction::move",">");

    double x=object.getDouble("x");
    double y=object.getDouble("y");
    double dx=object.getDouble("dx");
    double dy=object.getDouble("dy");
    boolean stopped=(dx==0 && dy==0);
    
    Rectangle2D collisionArea=getCollisionArea(object.get("type"),x+dx,y+dy);
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(object.getID());
    
    if(zone.collides(collisionArea)==false)
      {
      Logger.trace("StendhalRPAction::move","D","Moving to ("+(x+dx)+","+(y+dy)+")");
      if(dx!=0) object.put("x",x+dx);
      if(dy!=0) object.put("y",y+dy);
      world.modify(object);
      }        
    else
      {
      /* Collision */
      Logger.trace("StendhalRPAction::move","D","COLLISION!!! at ("+(x+dx)+","+(y+dy)+")");
      if(dx!=0 || dy!=0)
        {
        if(dx!=0) object.put("dx",0);
        if(dy!=0) object.put("dy",0);
        world.modify(object);
        }
      }
    
    Logger.trace("StendhalRPAction::move","<");
    return !stopped;
    }

  static void transferContent(RPObject object) throws Exception
    {
    Logger.trace("StendhalRPAction::transferContent",">");

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(object.getID());
    rpman.transferContent(object.getID(),zone.getContents());

    Logger.trace("StendhalRPAction::transferContent","<");
    }

  static void changeZone(RPObject object, String destination) throws Exception
    {    
    Logger.trace("StendhalRPAction::changeZone",">");

    world.changeZone(object.getID().getZoneID(),destination,object);
    
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(object.getID());
    zone.placeObjectAtEntryPoint(object);

    Logger.trace("StendhalRPAction::changeZone","<");
    }
  }
