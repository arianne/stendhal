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

import marauroa.common.*;
import marauroa.common.game.*;
import games.stendhal.client.*;
import java.awt.Graphics;

/** This class is a link between client graphical objects and server attributes objects.<br>
 *  You need to extend this object in order to add new elements to the game. */
public class GameEntity extends Entity  
  {
  /** The arianne object associated with this game entity */
  private RPObject.ID id;
  /** The object sprite. Animationless, just one frame */
  protected Sprite sprite;
  
  /** This methods returns the object graphical representation file from its type. */
  protected static String translate(String type)
    {
    return "sprites/"+type+".gif";
    }

  /** Create a new game entity based on the arianne object passed */
  public GameEntity(RPObject object) throws AttributeNotFoundException
    {
    super(0,0);    
    modify(object);
    
    id=object.getID();    
    loadSprite(object.get("type"));
    }
    
  /** Loads the sprite that represent this entity */
  protected void loadSprite(String type)
    {
    SpriteStore store=SpriteStore.get();        
    sprite=store.getSprite(translate(type));
    }
  
  /** This method is called to modify the propierties of the game entity when the object
   *  that it represent has changed. */
  public void modify(RPObject object) throws AttributeNotFoundException
    {
    Logger.trace("GameEntity::modify","D",object.toString());
    x=object.getDouble("x");
    y=object.getDouble("y");
    dx=object.getDouble("dx");
    dy=object.getDouble("dy");
    }
  
  /** Returns the represented arianne object id */
  public RPObject.ID getID()
    {
    return id;
    }
 
  /** Draws this entity in the screen */
  public void draw(GameScreen screen)
    {
    screen.draw(sprite,x,y);
    }
  }
