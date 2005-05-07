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
  private int weight;
  private Sprite ideaImage;

  private static Sprite eat;
  private static Sprite food;
  private static Sprite walk;
  private static Sprite follow;
  
  static
    {
    SpriteStore st=SpriteStore.get();
    
    eat=st.getSprite("sprites/ideas/eat.gif");
    food=st.getSprite("sprites/ideas/food.gif");
    walk=st.getSprite("sprites/ideas/walk.gif");
    follow=st.getSprite("sprites/ideas/follow.gif");
    }

  
  public Sheep(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects, object);
    }
  
  protected void buildAnimations(String type)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("move_up", store.getAnimatedSprite(translate(type),0,3,64,48));      
    sprites.put("move_right", store.getAnimatedSprite(translate(type),1,3,64,48));      
    sprites.put("move_down", store.getAnimatedSprite(translate(type),2,3,64,48));      
    sprites.put("move_left", store.getAnimatedSprite(translate(type),3,3,64,48));      
    sprites.put("big_move_up", store.getAnimatedSprite(translate(type),4,3,64,48));      
    sprites.put("big_move_right", store.getAnimatedSprite(translate(type),5,3,64,48));      
    sprites.put("big_move_down", store.getAnimatedSprite(translate(type),6,3,64,48));      
    sprites.put("big_move_left", store.getAnimatedSprite(translate(type),7,3,64,48));      
    }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if(changes.has("idea"))
      {
      String idea=changes.get("idea");
      if(idea.equals("eat"))
        {
        ideaImage=eat;
        }
      else if(idea.equals("food"))
        {
        ideaImage=food;
        }
      else if(idea.equals("walk"))
        {
        ideaImage=walk;
        }
      else if(idea.equals("follow"))
        {
        ideaImage=follow;
        }
      }
    
    if(changes.has("weight"))
      {
      weight=changes.getInt("weight");
      }
      
    if(weight>60 && !animation.startsWith("big_"))
      {      
      animation="big_"+animation;
      }
    }
    
  protected Sprite defaultAnimation()
    {
    animation="move_up";
    return sprites.get("move_up")[0];
    }

  public void onLeftClick(StendhalClient client)
    {
    StendhalClient.get().addEventLine("* Sheep weights "+weight);
    System.out.println ("Sheep weights "+weight);
    }
  
  public void draw(GameScreen screen)
    {
    super.draw(screen);
    
    if(ideaImage!=null)
      {
      Rectangle2D rect=getArea();
      double sx=rect.getMaxX();
      double sy=rect.getY();
      screen.draw(ideaImage,sx-0.25,sy-0.25);
      }
    }
  }
