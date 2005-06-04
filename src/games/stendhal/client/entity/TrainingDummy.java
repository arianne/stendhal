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

public class TrainingDummy extends RPEntity 
  {
  public TrainingDummy(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    }

  protected void buildAnimations(RPObject object)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("default", store.getAnimatedSprite(translate(object.get("type")),0,1,32,64));      
    }
  
  protected Sprite defaultAnimation()
    {
    animation="default";
    return sprites.get("default")[0];
    }

  public Rectangle2D getArea()
    {
    return new Rectangle.Double(x,y+1,1,1);
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,1,2);
    }  
    
  }
