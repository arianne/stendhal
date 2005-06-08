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
import games.stendhal.common.*;
import marauroa.common.*;
import marauroa.common.game.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Player extends RPEntity
  {
  private int devel;
  private int leave;
  private boolean hasLeave;

  public static void generateRPClass()
    {
    try
      {
      RPClass player=new RPClass("player");
      player.isA("rpentity");
      player.add("text",RPClass.LONG_STRING, RPClass.VOLATILE);
      player.add("private_text",RPClass.LONG_STRING,(byte)(RPClass.HIDDEN|RPClass.VOLATILE));
      player.add("sheep",RPClass.INT);
      player.add("devel",RPClass.INT,RPClass.HIDDEN);
      player.add("dead",RPClass.FLAG,RPClass.HIDDEN);
      player.add("reset",RPClass.FLAG,(byte)(RPClass.HIDDEN|RPClass.VOLATILE)); // The reset attribute is used to reset player position on next login

      player.add("outfit",RPClass.INT);
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Player::generateRPClass","X",e);
      }
    }
  
  public static Player create(RPObject object)
    {
    // Port from 0.03 to 0.10
    if(!object.has("base_hp"))
      {
      object.put("base_hp","100");
      object.put("hp","100");
      }

    // Port from 0.13 to 0.20
    if(!object.has("outfit"))
      {
      object.put("outfit",0);
      }

    Player player=new Player(object);
    player.stop();
    player.stopAttack();
    
    boolean firstVisit=false;
    
    try
      {
      if(!object.has("zoneid")|| !object.has("x") || !object.has("y") || object.has("reset"))
        {
        firstVisit=true;
        }

      if(firstVisit)
        {
        player.put("zoneid","city");        
        }

      world.add(player);
      }
    catch(Exception e) // If placing the player at its last position fails we reset it to city entry point
      {
      Logger.thrown("Player::create","X",e);
      
      firstVisit=true;
      player.put("zoneid","city");        

      world.add(player);
      }

    StendhalRPAction.transferContent(player);

    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
   
    if(firstVisit)
      {
      zone.placeObjectAtEntryPoint(player);
      }

    int x=player.getx();
    int y=player.gety();
        
    StendhalRPAction.placeat(zone,player,x,y);
    
    try
      {
      if(player.hasSheep())
        {
        Logger.trace("Player::create","D","Player has a sheep");
        Sheep sheep=player.retrieveSheep();
        sheep.put("zoneid",object.get("zoneid"));
        if(!sheep.has("base_hp"))
          {
          sheep.put("base_hp","10");
          sheep.put("hp","10");
          }

        world.add(sheep);
        StendhalRPAction.placeat(zone,sheep,x,y);
        player.setSheep(sheep);
        }
      }
    catch(Exception e) /** No idea how but some players get a sheep but they don't have it really.
                           Me thinks that it is a player that has been running for a while the game and 
                           was kicked of server because shutdown on a pre 1.00 version of Marauroa.
                           We shouldn't see this anymore. */
      {
      Logger.thrown("Player::create","X",e);

      if(player.has("sheep"))
        {
        player.remove("sheep");
        }
      
      if(player.hasSlot("#flock"))
        {
        player.removeSlot("#flock");
        }          
      }

    Logger.trace("Player::create","D","Finally player is :"+player);
    return player;
    }
    
  
  public static void destroy(Player player)
    {
    try
      {
      if(player.hasSheep())
        {
        Sheep sheep=(Sheep)world.remove(player.getSheep());
        player.storeSheep(sheep);
        rp.removeNPC(sheep);
        }
      else
        {
        // Bug on pre 0.20 released
        if(player.hasSlot("#flock"))
          {
          player.removeSlot("#flock");
          }
        }
      }
    catch(Exception e) /** No idea how but some players get a sheep but they don't have it really.
                           Me thinks that it is a player that has been running for a while the game and 
                           was kicked of server because shutdown on a pre 1.00 version of Marauroa.
                           We shouldn't see this anymore. */
      {
      Logger.thrown("Player::destroy","X",e);

      if(player.has("sheep"))
        {
        player.remove("sheep");
        }
      
      if(player.hasSlot("#flock"))
        {
        player.removeSlot("#flock");
        }          
      }

    player.stop();
    player.stopAttack();
    
    world.remove(player.getID());
    }

  public Player(RPObject object) throws AttributeNotFoundException
    {
    super(object);
    put("type","player");
    hasLeave=false;

    update();
    }

  public void update() throws AttributeNotFoundException
    {
    super.update();
    if(has("devel")) devel=getInt("devel");
    }

  public void addXP(int newxp)
    {
    super.addXP(newxp);

    int newLevel=Level.getLevel(getXP());
    int levels=newLevel-getLevel();

    if(levels>0)
      {
      addDevel(levels);
      setLevel(newLevel);
      }
    }

  public void addDevel(int n)
    {
    devel+=n;
    put("devel",devel);
    }

  public void improveATK()
    {
    if(devel>0)
      {
      addDevel(-1);
      setATK(getATK()+1);
      }
    }

  public void improveDEF()
    {
    if(devel>0)
      {
      addDevel(-1);
      setDEF(getDEF()+1);
      }
    }

  public void improveHP()
    {
    if(devel>0)
      {
      addDevel(-1);
      setbaseHP(getbaseHP()+10);
      }
    }

  public void setPrivateText(String text)
    {
    put("private_text", text);
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y+1,1,1);
    }

  public void onDead(RPEntity who)
    {
    put("dead","");

    if(hasSheep())
      {
      // We make the sheep ownerless so someone can use it 
      Sheep sheep=(Sheep)world.get(getSheep());
      sheep.setOwner(null);
      
      remove("sheep");
      }

    super.onDead(who, false);

    // Penalize: Respawn on afterlive zone and 10% less experience
    setXP((int)(getXP()*0.9));
    setHP(getbaseHP());

    world.modify(who);

    StendhalRPAction.changeZone(this,"afterlive");
    StendhalRPAction.transferContent(this);
    }

  public void removeSheep(Sheep sheep)
    {
    Logger.trace("Player::removeSheep",">");
    remove("sheep");

    rp.removeNPC(sheep);

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
          Iterator<RPObject> it=slot.iterator();

          Sheep sheep=new Sheep(it.next(),this);

          removeSlot("#flock");
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
