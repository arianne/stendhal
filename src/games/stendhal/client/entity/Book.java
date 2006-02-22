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

public class Book extends Item 
  {
  public Book(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    }

  protected void loadSprite(RPObject object)
    {
    SpriteStore store=SpriteStore.get();   
    
    String name=object.get("class")+"_"+object.get("subclass");
    
    sprite=store.getSprite("data/sprites/items/misc/"+name+".png");
    }


  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Look"))
      {
      String text="You see a book.";
      
      StendhalClient.get().addEventLine(text,Color.green);
      gameObjects.addText(this, text, Color.green);
      }
    else
      {
      super.onAction(client,action,params);
      }
    }
  }
