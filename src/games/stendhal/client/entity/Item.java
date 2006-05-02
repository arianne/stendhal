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

public class Item extends PassiveEntity 
  {
  
  public Item(GameObjects gameObjects, RPObject object) throws AttributeNotFoundException
    {    
    super(gameObjects, object);
    }

  protected void loadSprite(RPObject object)
    {
    SpriteStore store=SpriteStore.get();   
    String name=null;
    
    if(object.has("subclass"))
      {
      name=object.get("class")+"/"+object.get("subclass");
      }     
    else
      {
      name=object.get("class");
      }     
    
    sprite=store.getSprite("data/sprites/items/"+name+".png");
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
    return "Use";
    }

  public String[] offeredActions()
    {
    String[] list={"Use", "Look"};
    return list;
    }

  public void onAction(StendhalClient client, String action, String... params)
    {
    if(action.equals("Use"))
      {
      RPAction rpaction=new RPAction();
      rpaction.put("type","use");
      int id=getID().getObjectID();
      
      if(params.length>0)
        {
        rpaction.put("baseobject",params[0]);   
        rpaction.put("baseslot",params[1]);   
        rpaction.put("baseitem",id);   
        }
      else
        {
        rpaction.put("target",id);         
        }
           
      client.send(rpaction);
      }
    else
      super.onAction(client, action, params);
    }

  public int compare(Entity entity)
    {
    if(entity instanceof RPEntity)
      {
      return -1;
      }
    else if(entity instanceof Item)
      {
      return 0;
      }
      
    return 1;
    }
  }
