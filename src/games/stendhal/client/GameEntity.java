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
package games.stendhal.client;

import marauroa.common.game.*;
import java.awt.Graphics;

public class GameEntity extends Entity  
  {
  private RPObject.ID id;
  private String type;
  
  private Sprite sprite;
  
  private static String translate(String type)
    {
    return "sprites/"+type+".gif";
    }

  public GameEntity(RPObject object) throws AttributeNotFoundException
    {
    super(0,0);
    SpriteStore store=SpriteStore.get();    
    
    modify(object);
    
    id=object.getID();
    type=object.get("type");    
    
    sprite=store.getSprite(translate(type));
    }
  
  public void modify(RPObject object) throws AttributeNotFoundException
    {
// TODO: Remove when using game online    
    x=object.getDouble("x");
    y=object.getDouble("y");
    
    if(object.has("dx") && object.has("dy"))
      {
      dx=object.getDouble("dx");
      dy=object.getDouble("dy");
      }
    }
  
  public RPObject.ID getID()
    {
    return id;
    }

  public void draw(GameScreen screen)
    {
    screen.draw(sprite,x,y);
    }
  }
