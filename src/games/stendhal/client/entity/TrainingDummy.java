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
package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.*;

public class TrainingDummy extends GameEntity 
  {
  public TrainingDummy(RPObject object) throws AttributeNotFoundException
    {    
    super(object);
    }

  public Rectangle2D getArea()
    {
    return new Rectangle.Double(x,y,1,2);
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,1,2);
    }
       
  public void onLeftClick()
    {
    System.out.println("You are attacking a poor training dummy!");
    }

  public void modify(RPObject object) throws AttributeNotFoundException
    {
    super.modify(object);
    }

  public void draw(GameScreen screen)
    {
    super.draw(screen);
    }
  }
