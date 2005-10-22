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

public class Corpse extends PassiveEntity 
  {
  private RPSlot content;

  public Corpse(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    }

  public Rectangle2D getArea()
    {
    return new Rectangle.Double(x,y,1,1);
    }
    
  public Rectangle2D getDrawedArea()
    {
    return new Rectangle.Double(x,y,1,1);
    }  

  public void modifyAdded(RPObject object, RPObject changes) throws AttributeNotFoundException
    {
    super.modifyAdded(object,changes);
    
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
    return "Look";
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
      String text="You see a corpse";
      StendhalClient.get().addEventLine(text,Color.green);
      gameObjects.addText(this, text, Color.green);
      }
    else if(action.equals("Inspect"))
      {
      client.getGameGUI().inspect(this,content);
      }
    else
      {
      super.onAction(client,action,params);
      }
    }

  public int compare(Entity entity)
    {
    return -1;
    }
  }
