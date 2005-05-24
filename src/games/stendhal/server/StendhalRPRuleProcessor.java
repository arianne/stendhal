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
package games.stendhal.server;

import marauroa.common.*;
import marauroa.common.net.*;
import marauroa.common.game.*;
import marauroa.server.game.*;

import games.stendhal.common.*;
import games.stendhal.server.entity.*;

import java.util.*;
import java.io.*;

public class StendhalRPRuleProcessor implements IRPRuleProcessor
  {
  private RPServerManager rpman;
  private RPWorld world;

  private List<Player> playersObject;
  private List<Player> playersObjectRmText;

  private List<NPC> npcs;
  private List<NPC> npcsToAdd;
  private List<NPC> npcsToRemove;

  private List<RespawnPoint> respawnPoints;
  private List<Food> foodItems;
  private List<Corpse> corpses;
  private List<Corpse> corpsesToRemove;

  public StendhalRPRuleProcessor()
    {
    playersObject=new LinkedList<Player>();
    playersObjectRmText=new LinkedList<Player>();
    npcs=new LinkedList<NPC>();
    respawnPoints=new LinkedList<RespawnPoint>();
    foodItems=new LinkedList<Food>();
    npcsToAdd=new LinkedList<NPC>();
    npcsToRemove=new LinkedList<NPC>();

    corpses=new LinkedList<Corpse>();
    corpsesToRemove=new LinkedList<Corpse>();
    }


  /**
   * Set the context where the actions are executed.
   *
   * @param rpman
   * @param world
   */
  public void setContext(RPServerManager rpman, RPWorld world)
    {
    try
      {
      this.rpman=rpman;
      this.world=world;

      StendhalRPAction.initialize(rpman,world);
      Path.initialize(rpman,world);

      NPC.setRPContext(this, world);

      for(IRPZone zone: world)
        {
        StendhalRPZone szone=(StendhalRPZone)zone;
        npcs.addAll(szone.getNPCList());
        respawnPoints.addAll(szone.getRespawnPointList());
        foodItems.addAll(szone.getFoodItemList());
        }
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::setContext","!",e);
      System.exit(-1);
      }
    }

  public boolean checkGameVersion(String game, String version)
    {
    if(game.equals("stendhal"))
      {
      return true;
      }
    else
      {
      return false;
      }
    }


  public void addNPC(NPC npc)
    {
    npcsToAdd.add(npc);
    }

  public void addCorpse(Corpse corpse)
    {
    corpses.add(corpse);
    }

  public void removeCorpse(Corpse corpse)
    {
    corpsesToRemove.add(corpse);
    }

  public List<Player> getPlayers()
    {
    return playersObject;
    }

  public List<Food> getFoodItems()
    {
    return foodItems;
    }

  public List<NPC> getNPCs()
    {
    return npcs;
    }

  public boolean removeNPC(NPC npc)
    {
    return npcsToRemove.add(npc);
    }

  public boolean onActionAdd(RPAction action, List<RPAction> actionList)
    {
    Logger.trace("StendhalRPRuleProcessor::onActionAdd",">");
    try
      {
      if(action.get("type").equals("moveto") || action.get("type").equals("move") || action.get("type").equals("face"))
        {
        // Cancel previous moveto actions
        Iterator it=actionList.iterator();
        while(it.hasNext())
          {
          RPAction act=(RPAction)it.next();
          if(act.get("type").equals("moveto"))
            {
            Logger.trace("StendhalRPRuleProcessor::onActionAdd","D","Removed action: "+act.toString());
            it.remove();
            }
          }
        }

      return true;
      }
    catch(AttributeNotFoundException e)
      {
      Logger.thrown("StendhalRPRuleProcessor::onActionAdd","X",e);
      return false;
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::onActionAdd","<");
      }
    }

  public boolean onIncompleteActionAdd(RPAction action, List<RPAction> actionList)
    {
    Logger.trace("StendhalRPRuleProcessor::onIncompleteActionAdd",">");
    try
      {
      if(action.get("type").equals("moveto"))
        {
        // Cancel this action because there is a new more important one
        Iterator it=actionList.iterator();
        while(it.hasNext())
          {
          RPAction act=(RPAction)it.next();
          if(act.get("type").equals("moveto") || act.get("type").equals("move") || act.get("type").equals("face"))
            {
            Logger.trace("StendhalRPRuleProcessor::onActionAdd","D","Not readded action: "+action.toString());
            return false;
            }
          }
        }

      return true;
      }
    catch(AttributeNotFoundException e)
      {
      Logger.thrown("StendhalRPRuleProcessor::onIncompleteActionAdd","X",e);
      return false;
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::onIncompleteActionAdd","<");
      }
    }


  public RPAction.Status execute(RPObject.ID id, RPAction action)
    {
    Logger.trace("StendhalRPRuleProcessor::execute",">");

    RPAction.Status status=RPAction.Status.SUCCESS;

    try
      {
      Player player=(Player)world.get(id);

      if(action.get("type").equals("move"))
        {
        move(player,action);
        }
      else if(action.get("type").equals("chat"))
        {
        chat(player,action);
        }
      else if(action.get("type").equals("attack"))
        {
        attack(player,action);
        }
      else if(action.get("type").equals("stop"))
        {
        stop(player);
        }
      else if(action.get("type").equals("use"))
        {
        use(player,action);
        }
      else if(action.get("type").equals("face"))
        {
        face(player,action);
        }
      else if(action.get("type").equals("improve"))
        {
       	improve(player,action);
	      }
      else if(action.get("type").equals("who"))
        {
        who(player);
        }
      else if(action.get("type").equals("tell"))
        {
        tell(player, action);
        }
      }
    catch(Exception e)
      {
      Logger.trace("StendhalRPRuleProcessor::execute","X",action.toString());
      Logger.thrown("StendhalRPRuleProcessor::execute","X",e);
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::execute","<");
      }

    return status;
    }

  private void stop(Player player) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::stop",">");

    player.stop();
    player.stopAttack();

    world.modify(player);

    Logger.trace("StendhalRPRuleProcessor::stop","<");
    }

  private void move(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::move",">");
    if(action.has("dir"))
      {
      player.setDirection(Direction.build(action.getInt("dir")));
      player.setSpeed(1);
      }

    world.modify(player);

    Logger.trace("StendhalRPRuleProcessor::move","<");
    }

  private void attack(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Logger.trace("StendhalRPRuleProcessor::attack",">");
    if(action.has("target"))
      {
      int targetObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof RPEntity)
          {
          if(!player.equals(object))
            {
            player.attack((RPEntity)object);
            world.modify(player);
            }
          }
        }
      }

    Logger.trace("StendhalRPRuleProcessor::attack","<");
    }

  private void improve(Player player, RPAction action)
    {
    Logger.trace("StendhalRPRuleProcessor::improve", ">");
    Logger.trace("StendhalRPRuleProcessor::improve", "D",action.toString());
    if(action.has("stat"))
      {
      String stat = action.get("stat");
	    if(stat.equals("atk"))
	      {
	      player.improveATK();
        world.modify(player);
	      }
	    else if(stat.equals("def"))
	      {
	      player.improveDEF();
        world.modify(player);
	      }
	    else if(stat.equals("hp"))
	      {
	      player.improveHP();
        world.modify(player);
	      }
      }
    Logger.trace("StendhalRPRuleProcessor::improve", "<");
    }

  private void who(Player player)
    {
    Logger.trace("StendhalRPRuleProcessor::who",">");
    String online = "Players online: ";
    for(Player p : getPlayers())
      {
      online += p.getName() + " ";
      }
    player.setPrivateText(online);
    world.modify(player);
    Logger.trace("StendhalRPRuleProcessor::who","<");
    }

    private void tell(Player player, RPAction action)
      {
      Logger.trace("StendhalRPRuleProcessor::tell",">");
      if(action.has("who") && action.has("text"))
        {
        String message = player.getName() +  " tells you: " + action.get("text");
        for(Player p : getPlayers())
          {
          if(p.getName().equals(action.get("who")))
            {
              p.setPrivateText(message);
              world.modify(p);
              Logger.trace("StendhalRPRuleProcessor::tell","<");
              return;
            }
          }
        }
      Logger.trace("StendhalRPRuleProcessor::tell","<");
      }

  private void chat(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::chat",">");
    if(action.has("text"))
      {
      player.put("text",action.get("text"));
      world.modify(player);

      playersObjectRmText.add(player);
      }
    Logger.trace("StendhalRPRuleProcessor::chat","<");
    }

  private void face(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::face",">");

    if(action.has("dir"))
      {
      player.stop();
      player.setDirection(Direction.build(action.getInt("dir")));
      world.modify(player);
      }

    Logger.trace("StendhalRPRuleProcessor::face","<");
    }

  private void use(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::use",">");

    if(action.has("object"))
      {
      int usedObject=action.getInt("object");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(usedObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof Portal) // Can use only portal by now
          {
          Portal portal=(Portal)object;

          StendhalRPAction.usePortal(player, portal);
          StendhalRPAction.transferContent(player);
          }
        }
      }

    Logger.trace("StendhalRPRuleProcessor::use","<");
    }

  public int getTurn()
    {
    return rpman.getTurn();
    }

  /** Notify it when a new turn happens */
  synchronized public void beginTurn()
    {
    Logger.trace("StendhalRPRuleProcessor::beginTurn",">");
    try
      {
      // TODO: Done this way because a problem with comodification... :(
      npcs.addAll(npcsToAdd);
      npcs.removeAll(npcsToRemove);
      corpses.removeAll(corpsesToRemove);

      npcsToAdd.clear();
      npcsToRemove.clear();
      corpsesToRemove.clear();

      for(Player object: playersObject)
        {
        if(object.has("risk"))
          {
          object.remove("risk");
          world.modify(object);
          }

        if(object.has("damage"))
          {
          object.remove("damage");
          world.modify(object);
          }

        if(object.has("dead"))
          {
          object.remove("dead");
          world.modify(object);
          }

        if(!object.stopped())
          {
          StendhalRPAction.move(object);
          }

        if(getTurn()%5==0 && object.isAttacking()) //1 round = 5 turns
          {
          StendhalRPAction.attack(object,object.getAttackTarget());
          }
        }

      for(Player object: playersObjectRmText)
        {
        if(object.has("text"))
          {
          object.remove("text");
          world.modify(object);
          }
        }

      playersObjectRmText.clear();
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::beginTurn","X",e);
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::beginTurn","<");
      }
    }

  synchronized public void endTurn()
    {
    Logger.trace("StendhalRPRuleProcessor::endTurn",">");
    try
      {
      for(NPC npc: npcs) npc.logic();
      for(Food food: foodItems) food.regrow();
      for(RespawnPoint point: respawnPoints) point.nextTurn();
      for(Corpse corpse: corpses) corpse.logic();
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::endTurn","X",e);
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::endTurn","<");
      }
    }

  synchronized public boolean onInit(RPObject object) throws RPObjectInvalidException
    {
    Logger.trace("StendhalRPRuleProcessor::onInit",">");
    try
      {
      object.put("zoneid","city");

      // Port from 0.03 to 0.10
      if(!object.has("base_hp"))
        {
        object.put("base_hp","100");
        object.put("hp","100");
        }

      Player player=new Player(object);
      player.stop();
      player.stopAttack();

      world.add(player);
      StendhalRPAction.transferContent(player);

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      zone.placeObjectAtEntryPoint(player);

      int x=player.getInt("x");
      int y=player.getInt("y");

      while(zone.collides(player,x,y))
        {
        x=x+(int)(Math.random()*6.0-3);
        y=y+(int)(Math.random()*6.0-3);
        }

      player.setx((int)x);
      player.sety((int)y);

      if(player.hasSheep())
        {
        Logger.trace("StendhalRPRuleProcessor::onInit","D","Player has a sheep");
        Sheep sheep=player.retrieveSheep();
        sheep.put("zoneid",object.get("zoneid"));
        if(!sheep.has("base_hp"))
          {
          sheep.put("base_hp","10");
          sheep.put("hp","10");
          }

        world.add(sheep);

        Logger.trace("StendhalRPRuleProcessor::onInit","D","Setting new position for sheep");
        while(zone.collides(sheep,x,y))
          {
          x=x+(int)(Math.random()*6-3);
          y=y+(int)(Math.random()*6-3);
          }

        sheep.setx(x);
        sheep.sety(y);
        Logger.trace("StendhalRPRuleProcessor::onInit","D","Sheep located at ("+x+","+y+")");

        player.setSheep(sheep);
        }

      Logger.trace("StendhalRPRuleProcessor::onInit","D","Finally player is :"+player);

      playersObject.add(player);
      return true;
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::onInit","X",e);
      return false;
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::onInit","<");
      }
    }

  synchronized public boolean onExit(RPObject.ID id)
    {
    Logger.trace("StendhalRPRuleProcessor::onExit",">");
    try
      {
      for(Player object: playersObject)
        {
        if(object.getID().equals(id))
          {
          if(object.hasSheep())
            {
            Sheep sheep=(Sheep)world.remove(object.getSheep());
            object.storeSheep(sheep);
            npcs.remove(sheep);
            }
          else
            {
            // Bug on pre 0.20 released
            if(object.hasSlot("#flock"))
              {
              object.removeSlot("#flock");
              }
            }


          object.stop();
          object.stopAttack();
          playersObject.remove(object);
          Logger.trace("StendhalRPRuleProcessor::onExit","D",object.toString());
          break;
          }
        }

      return true;
      }
    catch(Exception e)
      {
      Logger.thrown("StendhalRPRuleProcessor::onExit","X",e);
      return true;
      }
    finally
      {
      world.remove(id);
      Logger.trace("StendhalRPRuleProcessor::onExit","<");
      }
    }

  synchronized public boolean onTimeout(RPObject.ID id)
    {
    Logger.trace("StendhalRPRuleProcessor::onTimeout",">");
    try
      {
      return onExit(id);
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::onTimeout","<");
      }
    }
  }


