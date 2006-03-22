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

public class Chest extends AnimatedEntity 
  {
  private boolean open;
  private RPSlot content;
  /** true means the user requested to open this chest */
  private boolean requestOpen;
  
  public Chest(GameObjects gameObjects, RPObject base) throws AttributeNotFoundException
    {
    super(gameObjects, base);
    requestOpen = false;
    }
  
  protected void buildAnimations(RPObject base)
    {
    SpriteStore store=SpriteStore.get();  

    sprites.put("close", store.getAnimatedSprite(translate(base.get("type")),0,1,1,1));      
    sprites.put("open", store.getAnimatedSprite(translate(base.get("type")),1,1,1,1));      
    }
  
  protected Sprite defaultAnimation()
    {
    animation="close";
    return sprites.get("close")[0];
    }

  public void onChangedAdded(RPObject base, RPObject diff) throws AttributeNotFoundException
    {
    super.onChangedAdded(base,diff);
    
    if(diff.has("open"))
      {
      open=true;
      animation="open";
      // we're wanted to open this?
      if (requestOpen)
        {
        client.getGameGUI().inspect(this,content,4,5);
        requestOpen = false;
        }
      }
    
    if(diff.hasSlot("content"))
      {      
      content=diff.getSlot("content");
      }

    if(base.hasSlot("content"))
      {      
      content=base.getSlot("content");
      }
    }

  public void onChangedRemoved(RPObject base, RPObject diff) throws AttributeNotFoundException
    {
    super.onChangedRemoved(base,diff);
    
    if(diff.has("open"))
      {
      open=false;
      animation="close";
      requestOpen = false;
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
    String[] list=null;
    if(open)
      {
      list=new String[]{"Look","Inspect","Close"};
      }
    else
      {
      list=new String[]{"Look","Open"};
      }
      
    return list;
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Look"))
      {
      String text="You see a chest that is "+(open?"open.":"closed.\n You can <<Inspect>> this item to see its content.");
      StendhalClient.get().addEventLine(text,Color.green);
      gameObjects.addText(this, text, Color.green);
      }
    else if(action.equals("Inspect"))
      {
      client.getGameGUI().inspect(this,content);
      }
    else if(action.equals("Open") || action.equals("Close"))
      {
      if(!open)
        {
        // If it was closed, open it and inspect it...
        requestOpen = true;
        }
        
      RPAction rpaction=new RPAction();
      rpaction.put("type","use");
      int id=getID().getObjectID();
      rpaction.put("target",id);      
      client.send(rpaction);
      }
    }

  public int compare(Entity entity)
    {
    if(entity instanceof RPEntity)
      {
      return -1;
      }
      
    return 1;
    }
  }
