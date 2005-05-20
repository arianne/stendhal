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
package games.stendhal.common;

import marauroa.common.*;
import java.awt.*;
import java.awt.geom.*;

/** This stinks... move to class at client. Server already done */

public class EntityAreas 
  {
  static public void getArea(Rectangle2D rect, String type, double x, double y)
    {
    if(type.equals("food"))
      {
      rect.setRect(x,y,1,1);
      }
    else if(type.equals("sheep"))
      {
      rect.setRect(x,y,1,1);
      }
    else if(type.equals("wolf"))
      {
      rect.setRect(x,y,1,1);
      }
    else if(type.equals("rat"))
      {
      rect.setRect(x,y,1,1);
      }
    else if(type.equals("caverat"))
      {
      rect.setRect(x,y,1,1);
      }
    else if(type.equals("player"))
      {
      rect.setRect(x,y+1,1,1);
      }
    else if(type.equals("buyernpc"))
      {
      rect.setRect(x,y+1,1,1);
      }
    else if(type.equals("sellernpc"))
      {
      rect.setRect(x,y+1,1,1);
      }
    else if(type.equals("welcomernpc"))
      {
      rect.setRect(x,y+1,1,1);
      }
    else if(type.equals("beggarnpc"))
      {
      rect.setRect(x,y+1,1,1);
      }
    else if(type.equals("trainingdummy"))
      {
      rect.setRect(x,y+1,1,1);
      }
    else if(type.equals("corpse"))
      {
      rect.setRect(x,y,0,0);
      }
    else if(type.equals("sign"))
      {
      rect.setRect(x,y,1,1);
      }
    else if(type.equals("portal"))
      {
      rect.setRect(x,y,1,1);
      }
    else
      {
      Logger.trace("EntityAreas::getArea","D","No area found for ("+type+")");
      }
    }
    
  static public Rectangle2D getArea(String type, double x, double y)
    {
    Rectangle2D rect=new Rectangle.Double();
    getArea(rect,type,x,y);
    return rect;
    }
    
  static public Rectangle2D getDrawedArea(String type, double x, double y)
    {
    if(type.equals("food"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else if(type.equals("sheep"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else if(type.equals("wolf"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else if(type.equals("rat"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else if(type.equals("caverat"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else if(type.equals("player"))
      {
      return new Rectangle.Double(x,y,1,2);
      }
    else if(type.equals("buyernpc"))
      {
      return new Rectangle.Double(x,y,1,2);
      }
    else if(type.equals("sellernpc"))
      {
      return new Rectangle.Double(x,y,1,2);
      }
    else if(type.equals("welcomernpc"))
      {
      return new Rectangle.Double(x,y,1,2);
      }
    else if(type.equals("beggarnpc"))
      {
      return new Rectangle.Double(x,y,1,2);
      }
    else if(type.equals("corpse"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else if(type.equals("sign"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else if(type.equals("trainingdummy"))
      {
      return new Rectangle.Double(x,y,1,2);
      }
    else if(type.equals("portal"))
      {
      return new Rectangle.Double(x,y,1,1);
      }
    else
      {
      return new Rectangle.Double(x,y,1,1);
      }
    }
  }
