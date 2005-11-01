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

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Money;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import marauroa.common.Log4J;
import marauroa.common.game.*;

import org.apache.log4j.Logger;


public class Player extends RPEntity
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(Player.class);


  public static void generateRPClass()
    {
    try
      {
      RPClass player=new RPClass("player");
      player.isA("rpentity");
      player.add("text",RPClass.LONG_STRING, RPClass.VOLATILE);
      player.add("private_text",RPClass.LONG_STRING,(byte)(RPClass.PRIVATE|RPClass.VOLATILE));
      player.add("sheep",RPClass.INT);
      player.add("dead",RPClass.FLAG,RPClass.PRIVATE);
      player.add("reset",RPClass.FLAG,(byte)(RPClass.PRIVATE|RPClass.VOLATILE)); // The reset attribute is used to reset player position on next login

      // Use this for admin menus and usage.
      player.add("admin",RPClass.FLAG,RPClass.HIDDEN);
      player.add("invisible",RPClass.FLAG,RPClass.HIDDEN);

      player.addRPSlot("head",1);
      player.addRPSlot("rhand",1);
      player.addRPSlot("lhand",1);
      player.addRPSlot("armor",1);
      player.addRPSlot("legs",1);
      player.addRPSlot("feet",1);
      player.addRPSlot("bag",10);
      
      player.addRPSlot("!buddy",1,RPClass.HIDDEN);
      // We use this for the buddy system
      player.add("online",RPClass.LONG_STRING, (byte)(RPClass.PRIVATE|RPClass.VOLATILE));
      player.add("offline",RPClass.LONG_STRING, (byte)(RPClass.PRIVATE|RPClass.VOLATILE));
      

      player.add("outfit",RPClass.INT);
      }
    catch(RPClass.SyntaxException e)
      {
      logger.error("cannot generateRPClass",e);
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
    
    // Port from 0.20 to 0.30
    if(!object.hasSlot("rhand"))
      {
      object.addSlot(new RPSlot("rhand"));
      }

    if(!object.hasSlot("lhand"))
      {
      object.addSlot(new RPSlot("lhand"));
      }

    if(!object.hasSlot("armor"))
      {
      object.addSlot(new RPSlot("armor"));
      }

    if(!object.hasSlot("bag"))
      {
      object.addSlot(new RPSlot("bag"));
      }
    
    // Port from 0.30 to 0.35
    if(!object.hasSlot("!buddy"))
      {
      object.addSlot(new RPSlot("!buddy"));
      }
    
    if(!object.has("atk_xp"))
      {
      object.put("atk_xp","0");
      object.put("def_xp","0");
      }
    
    if(object.has("devel"))
      {
      object.remove("devel");
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
      logger.warn("cannot place player at its last position. reseting to city entry point",e);
      
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
        logger.debug("Player has a sheep");
        Sheep sheep=player.retrieveSheep();
        sheep.put("zoneid",object.get("zoneid"));
        if(!sheep.has("base_hp"))
          {
          sheep.put("base_hp","10");
          sheep.put("hp","10");
          }

        world.add(sheep);
        x=sheep.getx();
        y=sheep.gety();
        StendhalRPAction.placeat(zone,sheep,x,y);
        player.setSheep(sheep);
        }
      }
    catch(Exception e) /** No idea how but some players get a sheep but they don't have it really.
                           Me thinks that it is a player that has been running for a while the game and 
                           was kicked of server because shutdown on a pre 1.00 version of Marauroa.
                           We shouldn't see this anymore. */
      {
      logger.error("Pre 1.00 Marauroa sheep bug. (player = "+player.getName()+")",e);

      if(player.has("sheep"))
        {
        player.remove("sheep");
        }
      
      if(player.hasSlot("#flock"))
        {
        player.removeSlot("#flock");
        }          
      }
    
    String[] slots={"rhand","lhand","armor","bag"};
    
    for(String slotName: slots)
      {
      try
        {
        RPSlot slot=player.getSlot(slotName);
        
        if(slot.size()!=0)        
          {
          // BUG: Loads only one object
          RPObject item=slot.iterator().next();
          slot.clear();
          
          if(item.get("type").equals("item")) // We simply ignore corpses...
            {
            Item entity = world.getRuleManager().getEntityManager().getItem(item.get("class"));
            entity.put("#db_id",item.get("#db_id"));
            
            // HACK: We have to manually copy some attributes
            entity.setID(item.getID());
            
            if(entity instanceof Money)
              {
              Money money=(Money)entity;
              money.setQuantity(item.getInt("quantity"));
              }
            
            slot.add(entity);
            }
          }
        }
      catch(Exception e)
        {
        logger.error("cannot create player",e);
        RPSlot slot=player.getSlot(slotName);
        slot.clear();
        }
      }
    
    if(player.getSlot("!buddy").size()>0)
      {
      RPObject buddies=player.getSlot("!buddy").iterator().next();
      for(String name: buddies)
        {
        if(name.charAt(0)=='_')
          {
          boolean online=false;
          for(Player buddy: rp.getPlayers())
            {
            if(name.equals(buddy.getName()))
              {
              player.notifyOnline(buddy.getName());
              online=true;
              break;            
              }          
            }
          
          if(!online)
            {
            player.notifyOffline(name.substring(1));
            }
          }
        }
      }

    player.setPrivateText("This release is EXPERIMENTAL. We are trying new RP system. Please report problems, suggestions and bugs.");

    logger.debug("Finally player is :"+player);
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
      logger.error("Pre 1.00 Marauroa sheep bug. (player = "+player.getName()+")",e);

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
    update();
    }

  public void update() throws AttributeNotFoundException
    {
    super.update();
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
    setHP(getBaseHP());

    world.modify(who);

    StendhalRPAction.changeZone(this,"afterlife");
    StendhalRPAction.transferContent(this);
    }

  public void removeSheep(Sheep sheep)
    {
    Log4J.startMethod(logger, "removeSheep");
    remove("sheep");

    rp.removeNPC(sheep);

    Log4J.finishMethod(logger, "removeSheep");
    }

  public boolean hasSheep()
    {
    return has("sheep");
    }

  public void setSheep(Sheep sheep)
    {
    Log4J.startMethod(logger, "setSheep");
    put("sheep",sheep.getID().getObjectID());

    rp.addNPC(sheep);

    Log4J.finishMethod(logger, "setSheep");
    }

  public static class NoSheepException extends RuntimeException
    {
    private static final long serialVersionUID = -6689072547778842040L;

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
    Log4J.startMethod(logger, "storeSheep");
    if(!hasSlot("#flock"))
      {
      addSlot(new RPSlot("#flock"));
      }

    RPSlot slot=getSlot("#flock");
    slot.clear();
    slot.add(sheep);
    put("sheep",sheep.getID().getObjectID());
    Log4J.finishMethod(logger, "storeSheep");
    }

  public Sheep retrieveSheep() throws NoSheepException
    {
    Log4J.startMethod(logger, "retrieveSheep");
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
      Log4J.finishMethod(logger, "retrieveSheep");
      }
    }
  
  public void notifyOnline(String who)
    {    
    String playerOnline="_"+who;
    
    boolean found=false;
    RPSlot slot=getSlot("!buddy");
    if(slot.size()>0)
      {
      RPObject buddies=slot.iterator().next();
      for(String name: buddies)
        {
        if(playerOnline.equals(name))
          {
          buddies.put(playerOnline,1);
          world.modify(this);
          found=true;
          break;
          }
        }
      }
    
    if(found)
      {
      if(has("online"))
        {
        put("online",get("online")+","+who);
        }
      else
        {
        put("online",who);
        }
      }
    }
  
  public void notifyOffline(String who)
    {
    String playerOffline="_"+who;

    boolean found=false;
    RPSlot slot=getSlot("!buddy");
    if(slot.size()>0)
      {
      RPObject buddies=slot.iterator().next();
      for(String name: buddies)
        {
        if(playerOffline.equals(name))
          {
          buddies.put(playerOffline,0);
          world.modify(this);
          found=true;
          break;
          }
        }
      }
    
    if(found)
      {
      if(has("offline"))
        {
        put("offline",get("offline")+","+who);
        }
      else
        {
        put("offline",who);
        }
      }
    }
  }
