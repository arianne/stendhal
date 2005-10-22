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
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import org.apache.log4j.Logger;


/** A Player entity */
public class Player extends Speaker
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Player.class);
  
  private int outfit;  
  
  public Player(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects, object);
    }

  protected static Sprite setOutFitPlayer(SpriteStore store,RPObject object)
    {
    int outfit=object.getInt("outfit");
    
    System.out.println ("OUTFIT is ("+outfit+")");
    
    Sprite player=store.getSprite("sprites/outfit/player_base_"+outfit%10+".png");
    player=player.copy();
    outfit/=100;

    if(outfit%10!=0)
      {
      Sprite dress=store.getSprite("sprites/outfit/dress_"+outfit%10+".png");
      dress.draw(player.getGraphics(),0,0);
      }
    outfit/=100;

    Sprite head=store.getSprite("sprites/outfit/head_"+outfit%10+".png");
    head.draw(player.getGraphics(),0,0);
    outfit/=100;

    if(outfit%10!=0)
      {
      Sprite hair=store.getSprite("sprites/outfit/hair_"+outfit%100+".png");
      hair.draw(player.getGraphics(),0,0);
      }
    
    return player;
    }

  protected void buildAnimations(RPObject object)
    {
    SpriteStore store=SpriteStore.get();
    
    Sprite player;
    
    try
      {
      if(outfit==object.getInt("outfit") && outfit!=0)
        {
        return;
        }
      
      outfit=object.getInt("outfit");
      player=setOutFitPlayer(store,object);      
      }
    catch(Exception e)
      {
      logger.error("cannot build Animations",e);
      object.put("outfit",0);
      player=setOutFitPlayer(store,object);            
      }

    sprites.put("move_up", store.getAnimatedSprite(player,0,4,48,64));
    sprites.put("move_right", store.getAnimatedSprite(player,1,4,48,64));
    sprites.put("move_down", store.getAnimatedSprite(player,2,4,48,64));
    sprites.put("move_left", store.getAnimatedSprite(player,3,4,48,64));

    sprites.get("move_up")[3]=sprites.get("move_up")[1];
    sprites.get("move_right")[3]=sprites.get("move_right")[1];
    sprites.get("move_down")[3]=sprites.get("move_down")[1];
    sprites.get("move_left")[3]=sprites.get("move_left")[1];
    }
  
  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);

    if(changes.has("outfit"))
      {      
      buildAnimations(changes);
      }
    
    if(object.hasSlot("!buddy") && changes.hasSlot("!buddy"))
      {
      RPSlot slot=changes.getSlot("!buddy");
      if(slot.size()>0)
        {
        RPObject buddies=slot.iterator().next();
        for(String name: buddies)
          {          
          if(name.startsWith("!") && buddies.getInt(name)==1)
            {
            client.addEventLine(name.substring(1)+" has joined Stendhal.",Color.orange);
            }
          }
        }
      }
    }
    
  public Rectangle2D getArea()
    {
    return new Rectangle.Double(x,y+1,1,1);
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,1,2);
    }  
    
  public String[] offeredActions()
    {
    if(getID().equals(client.getPlayer().getID()))
      {
      java.util.Vector<String> vector=new java.util.Vector<String>();
      for(String item: super.offeredActions())
        {
        vector.add(item);
        }

      vector.add("Set outfit");
      if(client.getPlayer().has("sheep"))
        {
        vector.add("Leave sheep");
        }
     
      return vector.toArray(new String[0]);
      }
    else
      {
      return super.offeredActions();
      }
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Set outfit"))
      {
      client.getOutfitDialog().setVisible(true);
      }
    else if(action.equals("Leave sheep"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","own");
      rpaction.put("target","-1");
      client.send(rpaction);
      }
    else
      {
      super.onAction(client,action,params);
      }
    }
  }
