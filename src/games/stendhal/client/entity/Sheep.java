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
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalClient;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

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

    sprites.put("move_up", store.getAnimatedSprite(translate(object.get("type")),0,3,1,1));      
    sprites.put("move_right", store.getAnimatedSprite(translate(object.get("type")),1,3,1,1));      
    sprites.put("move_down", store.getAnimatedSprite(translate(object.get("type")),2,3,1,1));      
    sprites.put("move_left", store.getAnimatedSprite(translate(object.get("type")),3,3,1,1));      
    sprites.put("big_move_up", store.getAnimatedSprite(translate(object.get("type")),4,3,1,1));      
    sprites.put("big_move_right", store.getAnimatedSprite(translate(object.get("type")),5,3,1,1));      
    sprites.put("big_move_down", store.getAnimatedSprite(translate(object.get("type")),6,3,1,1));      
    sprites.put("big_move_left", store.getAnimatedSprite(translate(object.get("type")),7,3,1,1));      
    }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if(changes.has("weight"))
      {
      int oldWeight = weight;
      weight=changes.getInt("weight");
      if ( weight > oldWeight )
         playSound( "sheep-eat", 8, 15 );
      }
      
    if(changes.has("idea"))
    {
    String idea=changes.get("idea");
    if(idea.equals("eat"))
      {
       System.out.println( "- sheep idea: EAT");   
       probableChat( 15 );
      }
    else if(idea.equals("food"))
      {
       System.out.println( "- sheep idea: FOOD");       
       probableChat( 20 );
      }
    else if(idea.equals("walk"))
      {
       System.out.println( "- sheep idea: WALK");       
       probableChat( 20 );
      }
    else if(idea.equals("follow"))
      {
       System.out.println( "- sheep idea: FOLLOW");       
       probableChat( 20 );
      }
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
    java.util.ArrayList<String> list=new java.util.ArrayList<String>();
    for(String item: super.offeredActions())
      {
      list.add(item);
      }

    if(!client.getPlayer().has("sheep"))
      {
      list.add("Own");
      }
   
    return list.toArray(new String[0]);
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Look"))
      {
      String text="You see a sheep that weighs "+weight;
      StendhalClient.get().addEventLine(text,Color.green);
      gameObjects.addText(this, text, Color.green);
      playSound( (weight > 50 ? "sheep-chat-2" : "sheep-chat"), 15, 40 );
      }
    else if(action.equals("Own"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","own");
      int id=getID().getObjectID();
      rpaction.put("target",id);
      client.send(rpaction);
      playSound( "sheep-chat-2", 25, 60 );
      }
    else
      {
      super.onAction(client,action,params);
      }
    }
  
  private void probableChat ( int chance )
  {
     String token = weight > 50 ? "sheep-mix2" : "sheep-mix";
     playSound( token, 20, 35, chance );
  }
   
  }
