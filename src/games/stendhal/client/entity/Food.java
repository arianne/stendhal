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

public class Food extends AnimatedEntity 
  {
  private int amount;
  
  public Food(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects, object);
    }
  
  protected void buildAnimations(RPObject object)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("0", store.getAnimatedSprite(translate(object.get("type")),0,1,32,32));      
    sprites.put("1", store.getAnimatedSprite(translate(object.get("type")),1,1,32,32));      
    sprites.put("2", store.getAnimatedSprite(translate(object.get("type")),2,1,32,32));      
    sprites.put("3", store.getAnimatedSprite(translate(object.get("type")),3,1,32,32));      
    sprites.put("4", store.getAnimatedSprite(translate(object.get("type")),4,1,32,32));      
    sprites.put("5", store.getAnimatedSprite(translate(object.get("type")),5,1,32,32));      
    }
  
  protected Sprite defaultAnimation()
    {
    animation="0";
    return sprites.get("0")[0];
    }

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if(changes.has("amount"))
      {
      animation=changes.get("amount");
      amount=changes.getInt("amount");
      }
    else if(object.has("amount"))
      {
      animation=object.get("amount");
      amount=changes.getInt("amount");
      }
    }

  public Rectangle2D getArea()
    {
    return new Rectangle.Double(x,y,1,1);
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,1,1);
    }  

  public String defaultAction()
    {
    return "Look";
    }

  public String[] offeredActions()
    {
    String[] list={"Look"};
    return list;
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Look"))
      {
      String text="You see a bush with "+amount+" fruits.";
      StendhalClient.get().addEventLine(text,Color.green);
      gameObjects.addText(this, text, Color.green);
      }
    }
  }
