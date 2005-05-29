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

import java.awt.*;
import java.awt.geom.*;

/** A Player entity */
public class Player extends RPEntity
  {
  private final static int TEXT_PERSISTENCE_TIME=5000;

  private Sprite textImage;
  private long textImageTime;

  public Player(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {
    super(gameObjects, object);
    }

  protected void buildAnimations(String type)
    {
    SpriteStore store=SpriteStore.get();

    sprites.put("move_up", store.getAnimatedSprite(translate(type),0,4,64,32));
    sprites.put("move_right", store.getAnimatedSprite(translate(type),1,4,64,32));
    sprites.put("move_down", store.getAnimatedSprite(translate(type),2,4,64,32));
    sprites.put("move_left", store.getAnimatedSprite(translate(type),3,4,64,32));
    }

  protected Sprite defaultAnimation()
    {
    animation="move_up";
    return sprites.get("move_up")[0];
    }

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);

    /** Add text lines */
    if(changes.has("text") && distance(client.getPlayer())<15*15)
      {
      String text=changes.get("text");
      client.addEventLine(getName(),text);

      textImage=GameScreen.get().createTextBox(text,240,Color.black,Color.white);
      textImageTime=System.currentTimeMillis();
      }

    if(changes.has("private_text"))
      {
      client.addEventLine(changes.get("private_text"));
      }

    if(changes.has("dead"))// && (stendhal.showEveryoneXPInfo || getID().equals(client.getPlayer().getID())))
      {
      System.out.println (getID());
      if(client.getPlayer()!=null) System.out.println (client.getPlayer().getID());
      client.addEventLine(getName()+" has died. "+getName()+"'s new level is "+getLevel());
      }
    }

  public String[] offeredActions()
    {
    if(getID()==client.getPlayer().getID())
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

  public void draw(GameScreen screen)
    {
    if(textImage!=null)
      {
      screen.draw(textImage,x+0.7-(textImage.getWidth()/(32.0f*2.0f)),y+2.05);
      if(System.currentTimeMillis()-textImageTime>TEXT_PERSISTENCE_TIME) textImage=null;
      }

    super.draw(screen);
    }
  }
