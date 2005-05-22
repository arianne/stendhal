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

import games.stendhal.server.*;
import marauroa.common.*;
import marauroa.common.game.*;
import java.awt.*;
import java.awt.geom.*;

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
      player.add("sheep",RPClass.INT);
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

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y+1,1,1);
    }  
    
  public void onDead(RPEntity who)
    {
    if(hasSheep())
      {
      Sheep sheep=(Sheep)world.remove(getSheep());
      removeSheep(sheep);
      }


    who.setXP(who.getXP()+(int)(getXP()*0.1));

    // Add a corpse
    Corpse corpse=new Corpse(this);
    IRPZone zone=world.getRPZone(getID());
    zone.assignRPObjectID(corpse);
    zone.add(corpse);
    
    rp.addCorpse(corpse);

    world.modify(who);    
    
    // Stats about dead 
    setXP((int)(getXP()*0.9));        
    setHP(getbaseHP());

    stats.add("Killed "+get("type"),1);
    
    StendhalRPAction.changeZone(this,"city");
    StendhalRPAction.transferContent(this);
    }
  
  public void removeSheep(Sheep sheep)
    {
    Logger.trace("Player::removeSheep",">");
    remove("sheep");

    rp.removeNPC(sheep);
    
    // FIXME: Change this to have coherence with storeSheep and retrieveSheep
    if(has("#flock")) getSlot("#flock").clear();
    Logger.trace("Player::removeSheep","<");
    }
     
  public boolean hasSheep()
    {
    return has("sheep");
    }
  
  public void setSheep(Sheep sheep)
    {
    Logger.trace("Player::setSheep",">");
    put("sheep",sheep.getID().getObjectID());
    
    rp.addNPC(sheep);

    Logger.trace("Player::setSheep","<");
    }
  
  public static class NoSheepException extends RuntimeException
    {
    public NoSheepException()
      {
      super();
      }
    }
  
  public RPObject.ID getSheep() throws NoSheepException
    {
    return new RPObject.ID(getInt("sheep"),get("zoneid"));
    }
    
  public void storeSheep(Sheep sheep)
    {
    Logger.trace("Player::storeSheep",">");
    if(!hasSlot("#flock"))
      {
      addSlot(new RPSlot("#flock"));
      }
     
    RPSlot slot=getSlot("#flock");
    slot.clear();
    slot.add(sheep);
    put("sheep",sheep.getID().getObjectID());
    Logger.trace("Player::storeSheep","<");
    }

  public Sheep retrieveSheep() throws NoSheepException
    {
    Logger.trace("Player::retrieveSheep",">");
    try
      {
      if(hasSlot("#flock"))    
        {
        RPSlot slot=getSlot("#flock");
        if(slot.size()>0)
          {
          Sheep sheep=new Sheep(slot.get(),this);
          return sheep;
          }
        }
      
      throw new NoSheepException();
      }
    finally
      {
      Logger.trace("Player::retrieveSheep","<");
      }
    }
  }
