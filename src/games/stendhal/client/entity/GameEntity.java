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
import java.awt.Graphics;

public class GameEntity extends Entity  
  {
  private RPObject.ID id;
  private String type;
  
  protected Sprite sprite;
  
  protected static String translate(String type)
    {
    return "sprites/"+type+".gif";
    }

  public GameEntity(RPObject object) throws AttributeNotFoundException
    {
    super(0,0);    
    modify(object);
    
    id=object.getID();
    type=object.get("type");    
    
    System.out.println ("GAME ENTITY: "+type);
    
    loadSprite(type);
    }
    
  protected void loadSprite(String type)
    {
    SpriteStore store=SpriteStore.get();        
    sprite=store.getSprite(translate(type));
    }
  
  public void modify(RPObject object) throws AttributeNotFoundException
    {
    x=object.getDouble("x");
    y=object.getDouble("y");
    
    if(object.has("dx") && object.has("dy"))
      {
      dx=object.getDouble("dx");
      dy=object.getDouble("dy");
      }
    System.out.println("GAMEENTITY modified: dx"+dx+"\tdy:"+dy);
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
