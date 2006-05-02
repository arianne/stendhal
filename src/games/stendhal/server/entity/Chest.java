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
package games.stendhal.server.entity;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import games.stendhal.server.events.UseEvent;

import marauroa.common.game.*;

public class Chest extends Entity implements UseEvent
  {
  private boolean open;
  
  public static void generateRPClass()
    {
    RPClass chest=new RPClass("chest");
    chest.isA("entity");
    chest.add("open",RPClass.FLAG);
    chest.addRPSlot("content",20);
    }
  
  public Chest(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    put("type","chest");
    
    if(!hasSlot("content"))
      {
      RPSlot slot=new RPSlot("content");
      slot.setCapacity(4);
      addSlot(slot);
      }
      
    update();
    }

  public Chest() throws AttributeNotFoundException
    {
    super();
    put("type","chest");
    open=false;

    RPSlot slot=new RPSlot("content");
    slot.setCapacity(4);
    addSlot(slot);
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }

  public void update()
    {
    super.update();
    open=false;
    if(has("open")) open=true;
    }
  
  public void open()
    {
    this.open=true;
    put("open","");
    }
    
  public void close()
    {
    this.open=false;
    if(has("open"))
      {
      remove("open");
      }
    }
  
  public boolean isOpen()
    {
    return open;
    }
  
  public void add(PassiveEntity entity)
    {
    RPSlot content=getSlot("content");
    content.assignValidID(entity);
    content.add(entity);
    }
  
  public int size()
    {
    return getSlot("content").size();
    }  
  
  public Iterator<RPObject> getContent()
    {
    RPSlot content=getSlot("content");
    return content.iterator();
    }

  public void onUsed(RPEntity user)
    {
    Player player=(Player)user;
    
    if(player.nextto(this,0.25))
      {
      if(isOpen())
        {
        close();
        }
      else
        {
        open();
        }
      
      world.modify(this);
      }
    }
  
  public String describe()
    {
    String text="You see a chest.";
    if(hasDescription())
      {
      text = getDescription();
      }
    text +=" It is "+(isOpen()?"open.":"closed.");
    if(isOpen())
      {
      text +=" You can #inspect this item to see its content.";
      }
    return(text);
    }
  }
