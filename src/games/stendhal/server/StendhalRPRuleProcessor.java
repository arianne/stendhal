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
import games.stendhal.server.entity.creature.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.npc.*;

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

      StendhalRPAction.initialize(rpman,this,world);
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
      /** TODO: This stinks... I can(MUST) be done in a better way. */
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
      else if(action.get("type").equals("equip"))
        {
        equip(player,action);
        }
      else if(action.get("type").equals("drop"))
        {
        drop(player,action);
        }
      else if(action.get("type").equals("improve"))
        {
       	improve(player,action);
	      }
      else if(action.get("type").equals("displace"))
        {
        displace(player,action);
        }
      else if(action.get("type").equals("who"))
        {
        who(player);
        }
      else if(action.get("type").equals("own"))
        {
        own(player, action);
        }
      else if(action.get("type").equals("tell"))
        {
        tell(player, action);
        }
      else if(action.get("type").equals("where"))
        {
        where(player, action);
        }
      else if(action.get("type").equals("outfit"))
        {
        outfit(player, action);
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

  private void own(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Logger.trace("StendhalRPRuleProcessor::own",">");

    // BUG: This features is potentially abusable right now. Consider removing it...
    if(player.hasSheep() && action.has("target") && action.getInt("target")==-1) // Allow release of sheep
      {
      Sheep sheep=(Sheep)world.get(player.getSheep());
      player.removeSheep(sheep);

      sheep.setOwner(null);
      addNPC(sheep);
      }

    if(player.hasSheep())
      {
      return;
      }

    if(action.has("target"))
      {
      int targetObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof Sheep)
          {
          Sheep sheep=(Sheep)object;
          if(sheep.getOwner()==null)
            {
            sheep.setOwner(player);
            removeNPC(sheep);

            player.setSheep(sheep);
            world.modify(player);
            }
          }
        }
      }

    Logger.trace("StendhalRPRuleProcessor::own","<");
    }

  private void displace(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Logger.trace("StendhalRPRuleProcessor::displace",">");
    if(action.has("target"))
      {
      int targetObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof RPEntity) /** Player, Creatures and NPCs */
          {
          if(!player.equals(object))
            {
            RPEntity entity=(RPEntity)object;
            if(player.nextto(entity,0.25))
              {
              /** TODO: No idea how to displace it */
              }
            }
          }
        else if(object instanceof PassiveEntity)
          {
          if(action.has("x") && action.has("y"))
            {
            int x=action.getInt("x");
            int y=action.getInt("y");

            PassiveEntity entity=(PassiveEntity)object;

            if(player.nextto(entity,0.25) && player.distance(x,y)<8*8 && !zone.simpleCollides(entity,x,y))
              {
              entity.setx(x);
              entity.sety(y);
              world.modify(entity);
              }
            }
          }
        }
      }

    Logger.trace("StendhalRPRuleProcessor::displace","<");
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
    String online = "" + getPlayers().size() + " Players online: ";
    for(Player p : getPlayers())
      {
      online += p.getName() + "(" + p.getLevel() +") ";
      }
    player.setPrivateText(online);
    world.modify(player);
    playersObjectRmText.add(player);
    Logger.trace("StendhalRPRuleProcessor::who","<");
    }

  private void tell(Player player, RPAction action)
    {
    Logger.trace("StendhalRPRuleProcessor::tell",">");
    try
      {
      if(action.has("who") && action.has("text"))
        {
        String message = player.getName() +  " tells you: " + action.get("text");
        for(Player p : getPlayers())
          {
          if(p.getName().equals(action.get("who")))
            {
            p.setPrivateText(message);
            player.setPrivateText("You tell " + p.getName() + ": " + action.get("text"));
            world.modify(p);
            world.modify(player);

            playersObjectRmText.add(player);
            playersObjectRmText.add(p);
            return;
            }
          }
        player.setPrivateText(action.get("who") + " is not currently logged.");
        }
      else
        {
          Logger.trace("StendhalRPRuleProcessor::tell","X", "Tell is not working right...");
        }
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::tell","<");
      }
    }

  private void where(Player player, RPAction action)
    {
    Logger.trace("StendhalRPRuleProcessor::where",">");
    try
      {
      if(action.has("who"))
        {
        String who=action.get("who");
        for(Player p : getPlayers())
          {
          if(p.getName().equals(who))
            {
            player.setPrivateText(p.getName() + " is in "+p.get("zoneid")+" at ("+p.getx()+","+p.gety()+")");
            world.modify(player);

            playersObjectRmText.add(player);
            return;
            }
          }
        
        if(who.equals("sheep") && player.hasSheep())
          {
          Sheep sheep=(Sheep)world.get(player.getSheep());
          player.setPrivateText("sheep is in "+sheep.get("zoneid")+" at ("+sheep.getx()+","+sheep.gety()+")");
          world.modify(player);

          playersObjectRmText.add(player);
          return;
          }
          
        player.setPrivateText(action.get("who") + " is not currently logged.");
        }
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::where","<");
      }
    }

  private void outfit(Player player, RPAction action)
    {
    Logger.trace("StendhalRPRuleProcessor::outfit",">");
    try
      {
      if(action.has("value"))
        {
        player.put("outfit",action.get("value"));
        world.modify(player);
        }
      }
    finally
      {
      Logger.trace("StendhalRPRuleProcessor::tell","<");
      }
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

  private void equip(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::equip",">");

    if(action.has("target") && action.has("slot"))
      {
      int targetObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof PassiveEntity)
          {
          PassiveEntity entity=(PassiveEntity)object;

          if(player.nextto(entity,0.25) && player.hasSlot(action.get("slot")))
            {
            RPSlot slot=player.getSlot(action.get("slot"));
            if(slot.size()==0)
              {
              slot.add(entity);
              world.remove(entity.getID());
              world.modify(player);
              }            
            }
          }
        }
      }

    Logger.trace("StendhalRPRuleProcessor::equip","<");
    }

  private void drop(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Logger.trace("StendhalRPRuleProcessor::drop",">");

    if(action.has("slot") && action.has("x") && action.has("y"))
      {
      if(player.hasSlot(action.get("slot")))
        {
        RPSlot slot=player.getSlot(action.get("slot"));
        
        if(slot.size()==0)
          {
          return;
          }
          
        StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
        
        RPObject object=slot.iterator().next();
        Entity entity;
        
        if(object.get("type").equals("item"))
          {
          entity=new Item();
          }
        else if(object.get("type").equals("corpse"))
          {
          entity=new Corpse(object);
          entity.put("class",object.get("class"));
          }
        else
          {
          Logger.trace("StendhalRPRuleProcessor::drop","X",object.toString());
          entity=null;
          }
          
        int x=action.getInt("x");
        int y=action.getInt("y");
        
        if(!zone.simpleCollides(entity,x,y))
          {
          slot.clear();
  
          entity.setx(x);
          entity.sety(y);
          zone.assignRPObjectID(entity);
          zone.add(entity);
          
          world.modify(player);        
          }        
        }
      }

    Logger.trace("StendhalRPRuleProcessor::drop","<");
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

          if(StendhalRPAction.usePortal(player, portal))
            {
            StendhalRPAction.transferContent(player);
            }
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
    
    Logger.trace("StendhalRPRuleProcessor::BugReportOnLists","D",corpses.size()+","+corpsesToRemove.size()+","+foodItems.size()+","+npcs.size()+","+npcsToAdd.size()+","+npcsToRemove.size()+","+playersObject.size()+","+playersObjectRmText.size()+","+respawnPoints.size());

    try
      {
      // We keep the number of players logged.
      Statistics.getStatistics().set("Players logged", playersObject.size());
      // TODO: Done this way because a problem with comodification... :(
      npcs.removeAll(npcsToRemove);
      corpses.removeAll(corpsesToRemove);
      npcs.addAll(npcsToAdd);

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

      for(NPC npc: npcs) npc.logic();

      for(Player object: playersObjectRmText)
        {
        if(object.has("text"))
          {
          object.remove("text");
          world.modify(object);
          }

        if(object.has("private_text"))
          {
          object.remove("private_text");
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
      Player player=Player.create(object);

      playersObject.add(player);
      return true;
      }
    catch(Exception e)
      {
      Logger.trace("StendhalRPRuleProcessor::onInit","X","There has been a severe problem loading player "+object.get("#db_id"));
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
          Player.destroy(object);

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


