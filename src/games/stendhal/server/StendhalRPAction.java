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
  private static Rectangle2D getCollisionArea(String type, double x, double y)
    {
    if(type.equals("player"))
      {
      return new Rectangle.Double(x,y,1,2);
      }
    else
      {
      return new Rectangle.Double(x,y,1,2);
      }
    }

  static void move(RPWorld world, RPObject object) throws Exception
    {
    Logger.trace("StendhalRPAction::move",">");

    double x=object.getDouble("x");
    double y=object.getDouble("y");
    double dx=object.getDouble("dx");
    double dy=object.getDouble("dy");
    
    Rectangle2D collisionArea=getCollisionArea(object.get("type"),x+dx,y+dy);
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(object.getID());
    
    if(zone.collides(collisionArea)==false)
      {
      Logger.trace("StendhalRPAction::move","D","Moving to ("+(x+dx)+","+(y+dy)+")");
      object.put("x",x+dx);
      object.put("y",y+dy);
      world.modify(object);
      }        
    else
      {
      /* Collision */
      Logger.trace("StendhalRPAction::move","D","COLLISION!!! at ("+(x+dx)+","+(y+dy)+")");
      if(dx!=0 || dy!=0)
        {
        object.put("dx",0);
        object.put("dy",0);
        world.modify(object);
        }
      }
    
    Logger.trace("StendhalRPAction::move","<");
    }

  }
