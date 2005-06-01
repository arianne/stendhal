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


/** A Player entity */
public class Player extends Speaker
  {
  public Player(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects, object);
    }

  protected static Sprite setOutFitPlayer(SpriteStore store,RPObject object)
    {
    int outfit=object.getInt("outfit");
    
    System.out.println ("OUTFIT is ("+outfit+")");
    
    System.out.println ("sprites/outfit/player_base_"+outfit%10+".gif");
    Sprite player=store.getSprite("sprites/outfit/player_base_"+outfit%10+".gif");
    player=player.copy();
    outfit/=10;
    System.out.println ("sprites/outfit/head_"+outfit%10+".gif");
    Sprite head=store.getSprite("sprites/outfit/head_"+outfit%10+".gif");
    head.draw(player.getGraphics(),0,0);
    outfit/=10;
    System.out.println ("sprites/outfit/hair_"+outfit%10+".gif");
    Sprite hair=store.getSprite("sprites/outfit/hair_"+outfit%10+".gif");
    hair.draw(player.getGraphics(),0,0);
    outfit/=10;
    System.out.println ("sprites/outfit/dress_"+outfit%10+".gif");
    Sprite dress=store.getSprite("sprites/outfit/dress_"+outfit%10+".gif");
    dress.draw(player.getGraphics(),0,0);
    
    
    return player;
    }

  protected void buildAnimations(RPObject object)
    {
    SpriteStore store=SpriteStore.get();
    
    Sprite player=setOutFitPlayer(store,object);

    sprites.put("move_up", store.getAnimatedSprite(player,0,4,48,64));
    sprites.put("move_right", store.getAnimatedSprite(player,1,4,48,64));
    sprites.put("move_down", store.getAnimatedSprite(player,2,4,48,64));
    sprites.put("move_left", store.getAnimatedSprite(player,3,4,48,64));

    sprites.get("move_up")[3]=sprites.get("move_up")[1];
    sprites.get("move_right")[3]=sprites.get("move_right")[1];
    sprites.get("move_down")[3]=sprites.get("move_down")[1];
    sprites.get("move_left")[3]=sprites.get("move_left")[1];
    }
  
  public String[] offeredActions()
    {
    if(getID().equals(client.getPlayer().getID()))
      {
      String[] list={"Look","Attack","Stop attack","Set outfit"};
      return list;
      }
    else
      {
      return super.offeredActions();
      }
    }

  public void onAction(String action, StendhalClient client)
    {
    if(action.equals("Set outfit"))
      {
      }
    else
      {
      super.onAction(action,client);
      }
    }
  }
