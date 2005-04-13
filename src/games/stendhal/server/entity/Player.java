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

import marauroa.common.*;
import marauroa.common.game.*;

public class Player extends RPEntity 
  {
  private int leave;
  private boolean hasLeave;
  
  public static void generateRPClass()
    {
    try
      {
      RPClass player=new RPClass("player");
      player.isA("rpentity");
      player.add("text",RPClass.STRING);
      player.add("target",RPClass.INT);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Player::generateRPClass","X",e);
      }
    }
  
  public Player(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    put("type","player");
    hasLeave=false;

    update();
    }
    
  public boolean hasLeave()
    {
    return hasLeave;
    }
    
  public void setLeave(int val)
    {
    if(val<0)
      {
      hasLeave=false;
      }
    else
      {
      hasLeave=true;
      }
    
    leave=val;
    }

  public int getLeave()
    {
    return leave;
    }
   
  public boolean hasSheep()
    {
    if(hasSlot("flock"))
      {
      RPSlot slot=getSlot("flock");
      return slot.size()>0;
      }
    else
      {
      return false;    
      }
    }
  
  public void setSheep(Sheep sheep)
    {
    if(!hasSlot("flock"))
      {
      addSlot(new RPSlot("flock"));
      }
     
    RPSlot slot=getSlot("flock");
    slot.add(sheep);
    }
  
  public static class NoSheepException extends Exception
    {
    public NoSheepException()
      {
      super();
      }
    }
  
  public Sheep getSheep() throws NoSheepException
    {
    if(hasSlot("flock"))
      {
      RPSlot slot=getSlot("flock");
      if(slot.size()>0)
        {
        Sheep sheep=new Sheep(slot.get(),this);
        return sheep;
        }
      }
    
    throw new NoSheepException();
    }
  }
