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

import java.awt.*;
import java.awt.geom.*;

import marauroa.common.game.*;
import games.stendhal.client.*;

/** A Sheep entity */
public class Sheep extends NPC 
  {
  private int weight;
  
  public Sheep(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects, object);
    }
  
  protected void buildAnimations(RPObject object)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("move_up", store.getAnimatedSprite(translate(object.get("type")),0,3,32,32));      
    sprites.put("move_right", store.getAnimatedSprite(translate(object.get("type")),1,3,32,32));      
    sprites.put("move_down", store.getAnimatedSprite(translate(object.get("type")),2,3,32,32));      
    sprites.put("move_left", store.getAnimatedSprite(translate(object.get("type")),3,3,32,32));      
    sprites.put("big_move_up", store.getAnimatedSprite(translate(object.get("type")),4,3,32,32));      
    sprites.put("big_move_right", store.getAnimatedSprite(translate(object.get("type")),5,3,32,32));      
    sprites.put("big_move_down", store.getAnimatedSprite(translate(object.get("type")),6,3,32,32));      
    sprites.put("big_move_left", store.getAnimatedSprite(translate(object.get("type")),7,3,32,32));      
    }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if(changes.has("weight"))
      {
      weight=changes.getInt("weight");
      }
      
    if(weight>60 && !animation.startsWith("big_"))
      {      
      animation="big_"+animation;
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
    
  protected Sprite defaultAnimation()
    {
    animation="move_up";
    return sprites.get("move_up")[0];
    }

  public String[] offeredActions()
    {
    java.util.Vector<String> vector=new java.util.Vector<String>();
    for(String item: super.offeredActions())
      {
      vector.add(item);
      }

    if(!client.getPlayer().has("sheep"))
      {
      vector.add("Own");
      }
   
      return vector.toArray(new String[0]);
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Look"))
      {
      String text="You see a sheep that weights "+weight;
      StendhalClient.get().addEventLine(text,Color.green);
      gameObjects.addText(this, text, Color.green);
      }
    else if(action.equals("Own"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","own");
      int id=getID().getObjectID();
      rpaction.put("target",id);
      client.send(rpaction);
      }
    else
      {
      super.onAction(client,action,params);
      }
    }
  }
