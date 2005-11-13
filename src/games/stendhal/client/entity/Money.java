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

import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;

import java.awt.Color;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

/**
 * This is the money item. Money is stackable  
 */
public class Money extends Item
  {
  private int quantity;
  private Sprite quantityImage;
  
  public Money(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    quantity=0;
    }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if(changes.has("quantity"))
      {
      quantity=changes.getInt("quantity");
      quantityImage=GameScreen.get().createString(Integer.toString(quantity),Color.white);
      }
    }
  
  public void draw(GameScreen screen)
    {
    super.draw(screen);    
    
    if(quantityImage!=null)
      {
      screen.draw(quantityImage,x,y);
      }
    }
  }
