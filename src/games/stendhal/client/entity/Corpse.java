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
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.wt.EntityContainer;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.GameScreen;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class Corpse extends PassiveEntity 
  {
  private String clazz;
  private String name;
  private String killer;
  
  private RPSlot content;
  private EntityContainer contentWindow;

  public Corpse(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    }

  public Rectangle2D getArea()
    {
    
    return new Rectangle.Double(x,y,sprite.getWidth()/GameScreen.SIZE_UNIT_PIXELS,sprite.getHeight()/GameScreen.SIZE_UNIT_PIXELS);
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,sprite.getWidth()/GameScreen.SIZE_UNIT_PIXELS,sprite.getHeight()/GameScreen.SIZE_UNIT_PIXELS);
    }  

  protected void loadSprite(RPObject object)  
    {
    String corpseType=object.get("type");
    
    if(object.get("class").equals("player"))
      {
      corpseType=corpseType+"_player";
      }
      
    SpriteStore store=SpriteStore.get();        
    sprite=store.getSprite(translate(corpseType));
    }

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
    if (changes.has("class"))
      {
      clazz=changes.get("class");
      }
    
    if(changes.has("name"))
      {
      name=changes.get("name");
      }

    if(changes.has("killer"))
      {
      killer=changes.get("killer");
      }
    
    /* BUG: Possible bug. Please double check this later.
     * If an slot is modified the changes are not seen */
    if(changes.hasSlot("content"))
      {      
      content=changes.getSlot("content");
      }

    if(object.hasSlot("content"))
      {      
      content=object.getSlot("content");
      }
    }
    
  public String defaultAction()
    {
    return "Inspect";
    }

  public String[] offeredActions()
    {
    String[] list={"Look","Inspect"};
    return list;
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Look"))
      {
      String text=null;
      if(name==null)
        {
        text="You see a corpse";
        }
      else
        {
        text="You see "+name+". It was killed by "+killer;
        }
        
      StendhalClient.get().addEventLine(text,Color.green);
      gameObjects.addText(this, text, Color.green);
      }
    else if(action.equals("Inspect"))
      {
       if ( !isContentShowing() )
       {
          contentWindow = client.getGameGUI().inspect(this,content);
       }
      }
    }
  
  /** whether the inspect window is showing for this corpse. */
  public boolean isContentShowing ()
  {
     return contentWindow != null && !contentWindow.isClosed();
  }

  public int compare(Entity entity)
    {
    return -1;
    }
  }
