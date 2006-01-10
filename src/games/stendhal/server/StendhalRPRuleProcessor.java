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

import games.stendhal.common.Pair;
import games.stendhal.server.actions.*;
import games.stendhal.server.entity.SheepFood;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;

import java.util.*;

import marauroa.common.Log4J;
import marauroa.common.game.*;
import marauroa.server.game.*;

import org.apache.log4j.Logger;

public class StendhalRPRuleProcessor implements IRPRuleProcessor
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);
  
  private static Map<String, ActionListener> actionsMap;
  static
    {
    actionsMap=new HashMap<String,ActionListener>();    
    }
  
  private RPServerManager rpman;
  private StendhalRPWorld world;

  private List<Player> playersObject;
  private List<Player> playersObjectRmText;

  private List<NPC> npcs;
  private List<NPC> npcsToAdd;
  private List<NPC> npcsToRemove;

  private List<Pair<RPEntity,RPEntity> > entityToKill;

  private List<RespawnPoint> respawnPoints;
  private List<SheepFood> foodItems;
  private List<Corpse> corpses;
  private List<Corpse> corpsesToRemove;
  
  public static void register(String action, ActionListener actionClass)
    {
    if(actionsMap.get(action)!=null)
      {
      logger.error("Registering twice the same action handler: "+action);
      }
      
    actionsMap.put(action, actionClass);
    }
  
  private void registerActions()
    {
    Administration.register();
    Attack.register();
    Buddy.register();
    Chat.register();
    Displace.register();
    Equipment.register();
    Face.register();
    Move.register();
    Outfit.register();
    Own.register();
    PlayersQuery.register();
    Stop.register();
    Use.register();    
    }

  public StendhalRPRuleProcessor()
    {
    playersObject=new LinkedList<Player>();
    playersObjectRmText=new LinkedList<Player>();
    npcs=new LinkedList<NPC>();
    respawnPoints=new LinkedList<RespawnPoint>();
    foodItems=new LinkedList<SheepFood>();
    npcsToAdd=new LinkedList<NPC>();
    npcsToRemove=new LinkedList<NPC>();
    
    entityToKill=new LinkedList<Pair<RPEntity,RPEntity> >();

    corpses=new LinkedList<Corpse>();
    corpsesToRemove=new LinkedList<Corpse>();
    
    registerActions();
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
      this.rpman = rpman;
      this.world = (StendhalRPWorld) world;

      StendhalRPAction.initialize(rpman,this,world);
      Behaviours.initialize(rpman,this,world);
      Path.initialize(world);
      
      NPC.setRPContext(this, this.world);

      for(IRPZone zone: world)
        {
        StendhalRPZone szone=(StendhalRPZone)zone;
        npcs.addAll(szone.getNPCList());
        respawnPoints.addAll(szone.getRespawnPointList());
        foodItems.addAll(szone.getFoodItemList());
        }

      /* Initialize quests*/ 
      StendhalQuestSystem quests=new StendhalQuestSystem(this.world,this);    
      }
    catch(Exception e)
      {
      logger.fatal("cannot set Context. exiting",e);
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
  
  private boolean isValidUsername(String username)
    {
    if(username.indexOf(' ')!=-1) return false;
    if(username.toLowerCase().contains("admin")) return false;
    
    return true;
    }
  
  public boolean createAccount(String username, String password, String email)
    {
    if(!isValidUsername(username))
      {
      return false;
      }
    
    
    stendhalcreateaccount account=new stendhalcreateaccount();
    return account.execute(username, password, email);
    }

  public void addNPC(NPC npc)
    {
    npcsToAdd.add(npc);
    }

  public void killRPEntity(RPEntity entity, RPEntity who)
    {
    entityToKill.add(new Pair<RPEntity,RPEntity>(entity,who));
    }
  
  public void removePlayerText(Player player)
    {
    playersObjectRmText.add(player);
    }

  public void addCorpse(Corpse corpse)
    {
    corpses.add(corpse);
    }

  public void removeCorpse(Corpse corpse)
    {
    for(RPSlot slot: corpse.slots())
      {
      for(RPObject object: slot)
        {
        if(object instanceof Corpse)
          {
          removeCorpse((Corpse)object);
          }
        }
      }
      
    corpsesToRemove.add(corpse);
    }

  public List<Player> getPlayers()
    {
    return playersObject;
    }

  public List<SheepFood> getFoodItems()
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
    return true;
    }

  public boolean onIncompleteActionAdd(RPAction action, List<RPAction> actionList)
    {
    return true;
    }


  public RPAction.Status execute(RPObject.ID id, RPAction action)
    {
    Log4J.startMethod(logger,"execute");

    RPAction.Status status=RPAction.Status.SUCCESS;
    
    try
      {
      Player player=(Player)world.get(id);
      String type=action.get("type");
      
      ActionListener actionListener=actionsMap.get(type);
      actionListener.onAction(world,this,player,action);
      }
    catch(Exception e)
      {
      logger.error("cannot execute action "+action,e);
      }
    finally
      {
      Log4J.finishMethod(logger,"execute");
      }

    return status;
    }

  public int getTurn()
    {
    return rpman.getTurn();
    }

  /** Notify it when a new turn happens */
  synchronized public void beginTurn()
    {
    Log4J.startMethod(logger,"beginTurn");
    long start=System.nanoTime();
    
    int creatures=0;
    for(RespawnPoint point: respawnPoints) creatures+=point.size();
    
    int objects=0;
    for(IRPZone zone: world) objects+=zone.size();

    logger.debug("lists: CO:"+corpses.size()+",F:"+foodItems.size()+",NPC:"+npcs.size()+",P:"+playersObject.size()+",CR:"+creatures+",OB:"+objects);
    logger.debug("lists: CO:"+corpsesToRemove.size()+",NPC:"+npcsToAdd.size()+",NPC:"+npcsToRemove.size()+",P:"+playersObjectRmText.size()+",R:"+respawnPoints.size());
    
    try
      {
      // We keep the number of players logged.
      Statistics.getStatistics().set("Players logged", playersObject.size());
      
      // In order for the last hit to be visible dead happens at two steps.
      for(Pair<RPEntity, RPEntity> entity: entityToKill) 
        {
        entity.first().onDead(entity.second());
        }
        
      entityToKill.clear();

      // Done this way because a problem with comodification... :(
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

        if(object.has("online"))
          {
          object.remove("online");
          world.modify(object);
          }

        if(object.has("offline"))
          {
          object.remove("offline");
          world.modify(object);
          }

        if(object.hasPath())
          {
          if(Path.followPath(object,1))
            {
            object.stop();
            object.clearPath();
            }
            
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
        
        if(getTurn()%30==0) //6 round = 30 turns
          {
          object.consume();
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
      logger.error("error in beginTurn",e);
      }
    finally
      {
      logger.debug("Begin turn: "+(System.nanoTime()-start)/1000000.0);
      Log4J.finishMethod(logger,"beginTurn");
      }
    }

  synchronized public void endTurn()
    {
    Log4J.startMethod(logger,"endTurn");
    long start=System.nanoTime();
    try
      {
      for(SheepFood food: foodItems) food.regrow();
      for(RespawnPoint point: respawnPoints) point.nextTurn();
      for(Corpse corpse: corpses) corpse.logic();
      }
    catch(Exception e)
      {
      logger.error("error in endTurn",e);
      }
    finally
      {
      logger.debug("End turn: "+(System.nanoTime()-start)/1000000.0);
      Log4J.finishMethod(logger,"endTurn");
      }
    }

  synchronized public boolean onInit(RPObject object) throws RPObjectInvalidException
    {
    Log4J.startMethod(logger,"onInit");
    try
      {
      Player player=Player.create(object);

      playersObjectRmText.add(player);
      playersObject.add(player);
      
      // Notify other players about this event
      for(Player p : getPlayers())
        {
        p.notifyOnline(player.getName());
        }
      
      return true;
      }
    catch(Exception e)
      {
      logger.error("There has been a severe problem loading player "+object.get("#db_id"),e);
      return false;
      }
    finally
      {
      Log4J.finishMethod(logger,"onInit");
      }
    }

  synchronized public boolean onExit(RPObject.ID id)
    {
    Log4J.startMethod(logger,"onExit");
    try
      {
      for(Player object: playersObject)
        {
        if(object.getID().equals(id))
          {
          // Notify other players about this event
          for(Player p : getPlayers())
            {
            p.notifyOffline(object.getName());
            }

          Player.destroy(object);

          playersObject.remove(object);

          logger.debug("removed player "+object);
          break;
          }
        }

      return true;
      }
    catch(Exception e)
      {
      logger.error("error in onExit",e);
      return true;
      }
    finally
      {
      Log4J.finishMethod(logger,"onExit");
      }
    }

  synchronized public boolean onTimeout(RPObject.ID id)
    {
    Log4J.startMethod(logger,"onTimeout");
    try
      {
      return onExit(id);
      }
    finally
      {
      Log4J.finishMethod(logger,"onTimeout");
      }
    }
  }


