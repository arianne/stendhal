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

/** A Sheep entity */
public class Sheep extends AnimatedGameEntity 
  {
  public Sheep(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    }
  
  protected void buildAnimations(String type)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("move_up", store.getAnimatedSprite(translate(type),0,3,64,48));      
    sprites.put("move_right", store.getAnimatedSprite(translate(type),1,3,64,48));      
    sprites.put("move_down", store.getAnimatedSprite(translate(type),2,3,64,48));      
    sprites.put("move_left", store.getAnimatedSprite(translate(type),3,3,64,48));      
    }
  
  protected Sprite defaultAnimation()
    {
    animation="move_up";
    return sprites.get("move_up")[0];
    }
  }
