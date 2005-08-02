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

import games.stendhal.common.Direction;
import games.stendhal.common.Pair;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.common.game.*;
import marauroa.server.game.*;
import org.apache.log4j.Logger;

public class StendhalRPRuleProcessor implements IRPRuleProcessor
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);
  
  private RPServerManager rpman;
  private RPWorld world;

  private List<Player> playersObject;
  private List<Player> playersObjectRmText;

  private List<NPC> npcs;
  private List<NPC> npcsToAdd;
  private List<NPC> npcsToRemove;

  private List<Pair<RPEntity,RPEntity> > entityToKill;

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
    
    entityToKill=new LinkedList<Pair<RPEntity,RPEntity> >();

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
    Log4J.startMethod(logger,"onActionAdd");
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
            logger.debug("Removed action: "+act);
            it.remove();
            }
          }
        }

      return true;
      }
    catch(AttributeNotFoundException e)
      {
      logger.error("error in onActionAdd",e);
      return false;
      }
    finally
      {
      Log4J.finishMethod(logger,"onActionAdd");
      }
    }

  public boolean onIncompleteActionAdd(RPAction action, List<RPAction> actionList)
    {
    Log4J.startMethod(logger,"onIncompleteActionAdd");
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
            logger.debug("Not readded action: "+action);
            return false;
            }
          }
        }

      return true;
      }
    catch(AttributeNotFoundException e)
      {
      logger.error("onIncompleteActionAdd",e);
      return false;
      }
    finally
      {
      Log4J.finishMethod(logger,"onIncompleteActionAdd");
      }
    }


  public RPAction.Status execute(RPObject.ID id, RPAction action)
    {
    Log4J.startMethod(logger,"execute");

    RPAction.Status status=RPAction.Status.SUCCESS;

    try
      {
      /** TODO: This stinks... I can(MUST) be done in a better way. */
      Player player=(Player)world.get(id);
      String type=action.get("type");

      if(type.equals("move"))
        {
        move(player,action);
        }
      else if(type.equals("chat"))
        {
        chat(player,action);
        }
      else if(type.equals("attack"))
        {
        attack(player,action);
        }
      else if(type.equals("stop"))
        {
        stop(player);
        }
      else if(type.equals("use"))
        {
        use(player,action);
        }
      else if(type.equals("face"))
        {
        face(player,action);
        }
      else if(type.equals("equip"))
        {
        equip(player,action);
        }
      else if(type.equals("moveequip"))
        {
        moveequip(player,action);
        }
      else if(type.equals("drop"))
        {
        drop(player,action);
        }
      else if(type.equals("displace"))
        {
        displace(player,action);
        }
      else if(type.equals("who"))
        {
        who(player);
        }
      else if(type.equals("own"))
        {
        own(player, action);
        }
      else if(type.equals("tell"))
        {
        tell(player, action);
        }
      else if(type.equals("where"))
        {
        where(player, action);
        }
      else if(type.equals("addbuddy"))
        {
        addBuddy(player, action);
        }
      else if(type.equals("outfit"))
        {
        outfit(player, action);
        }
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

  private void stop(Player player) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger,"stop");

    player.stop();
    player.stopAttack();

    world.modify(player);

    Log4J.finishMethod(logger,"stop");
    }

  private void move(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger,"move");
    if(action.has("dir"))
      {
      player.setDirection(Direction.build(action.getInt("dir")));
      player.setSpeed(1);
      }

    world.modify(player);

    Log4J.finishMethod(logger,"move");
    }

  private void attack(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Log4J.startMethod(logger,"attack");
    if(action.has("target"))
      {
      int targetObject=action.getInt("target");

      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      RPObject.ID targetid=new RPObject.ID(targetObject, zone.getID());
      if(zone.has(targetid))
        {
        RPObject object=zone.get(targetid);
        if(object instanceof RPEntity && !(object instanceof Player)) // Disabled Player
          {
          if(!player.equals(object))
            {
            player.attack((RPEntity)object);
            world.modify(player);
            }
          }
        }
      }

    Log4J.finishMethod(logger,"attack");
    }

  private void own(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Log4J.startMethod(logger,"own");

    // BUG: This features is potentially abusable right now. Consider removing it...
    if(player.hasSheep() && action.has("target") && action.getInt("target")==-1) // Allow release of sheep
      {
      Sheep sheep=(Sheep)world.get(player.getSheep());
      player.removeSheep(sheep);

      sheep.setOwner(null);
      addNPC(sheep);
      
      world.modify(player);
      return;
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

    Log4J.finishMethod(logger,"own");
    }

  private void displace(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException
    {
    Log4J.startMethod(logger,"displace");
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

    Log4J.finishMethod(logger,"displace");
    }

  private void who(Player player)
    {
    Log4J.startMethod(logger,"who");
    
    String online = "" + getPlayers().size() + " Players online: ";
    for(Player p : getPlayers())
      {
      online += p.getName() + "(" + p.getLevel() +") ";
      }
    player.setPrivateText(online);
    world.modify(player);
    playersObjectRmText.add(player);
    Log4J.finishMethod(logger,"who");
    }

  private void tell(Player player, RPAction action)
    {
    Log4J.startMethod(logger,"tell");
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
          logger.warn("Tell is not working right...");
        }
      }
    finally
      {
      Log4J.finishMethod(logger,"tell");
      }
    }

  private void where(Player player, RPAction action)
    {
    Log4J.startMethod(logger,"where");
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
      Log4J.finishMethod(logger,"where");
      }
    }

  private void addBuddy(Player player, RPAction action)
    {
    Log4J.startMethod(logger,"addBuddy");
    try
      {
      if(action.has("who"))
        {
        String who=action.get("who");
        RPSlot slot=player.getSlot("!buddy");
        
        RPObject listBuddies=null;
        
        if(slot.size()>0)
          {
          listBuddies=slot.iterator().next();
          }
        else
          {
          listBuddies=new RPObject();
          listBuddies.put("zoneid",player.get("zoneid"));           //Fixme: fix this at marauroa.
          slot.assignValidID(listBuddies);
          slot.add(listBuddies);
          }
        
        int online=0;
        for(Player p : getPlayers())
          {
          if(p.getName().equals(who))
            {
            online=1;            
            }
          }
          
        listBuddies.put("_"+who,online);
        world.modify(player);
        }
      }
    finally
      {
      Log4J.finishMethod(logger,"addBuddy");
      }
    }
  
  public void online(String who)
    {
    who="_"+who;
    for(Player p : getPlayers())
      {
      RPSlot slot=p.getSlot("!buddy");
      if(slot.size()>0)
        {
        RPObject buddies=slot.iterator().next();
        for(String name: buddies)
          {
          if(who.equals(name))
            {
            buddies.put(who,1);
            world.modify(p);
            break;
            }
          }
        }
      }
    }

  public void offline(String who)
    {
    who="_"+who;
    for(Player p : getPlayers())
      {
      RPSlot slot=p.getSlot("!buddy");
      if(slot.size()>0)
        {
        RPObject buddies=slot.iterator().next();
        for(String name: buddies)
          {
          if(who.equals(name))
            {
            buddies.put(who,0);
            world.modify(p);
            break;
            }
          }
        }
      }
    }  

  private void outfit(Player player, RPAction action)
    {
    Log4J.startMethod(logger,"outfit");
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
      Log4J.finishMethod(logger,"outfit");
      }
    }

  private void chat(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger,"chat");
    if(action.has("text"))
      {
      player.put("text",action.get("text"));
      world.modify(player);

      playersObjectRmText.add(player);
      }
    Log4J.finishMethod(logger,"chat");
    }

  private void face(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger,"face");

    if(action.has("dir"))
      {
      player.stop();
      player.setDirection(Direction.build(action.getInt("dir")));
      world.modify(player);
      }

    Log4J.finishMethod(logger,"face");
    }

  private void equip(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger,"equip");

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
              world.remove(entity.getID());
              
              // Gives a valid id inside the slot
              slot.assignValidID(entity);
              slot.add(entity);
              
              world.modify(player);
              }            
            }
          }
        }
      }

    Log4J.finishMethod(logger,"equip");
    }

  private void moveequip(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger,"moveequip");

    if(action.has("sourceslot") && action.has("targetslot"))
      {
      String sourceSlot=action.get("sourceslot");
      String targetSlot=action.get("targetslot");

      if(player.hasSlot(sourceSlot) && player.hasSlot(targetSlot))
        {
        RPSlot target=player.getSlot(targetSlot);
        if(target.size()==0)
          {
          RPSlot source=player.getSlot(sourceSlot);
          if(source.size()>0)
            {
            Entity item=(Entity)source.iterator().next();
            
            source.clear();
            
            target.assignValidID(item);
            target.add(item);
            
            world.modify(player);
            }
          }            
        }
      }

    Log4J.finishMethod(logger,"moveequip");
    }


  private void drop(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger,"drop");

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
          entity=Item.create(object.get("class"));
          }
        else if(object.get("type").equals("corpse"))
          {
          entity=new Corpse(object);
          entity.put("class",object.get("class"));
          }
        else
          {
          logger.debug("dropped "+object);
          entity=null;
          }
          
        int x=action.getInt("x");
        int y=action.getInt("y");
        
        if(player.distance(x,y)<8*8 && !zone.simpleCollides(entity,x,y))
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

    Log4J.finishMethod(logger,"drop");
    }

  private void use(Player player, RPAction action) throws AttributeNotFoundException, NoRPZoneException
    {
    Log4J.startMethod(logger,"use");

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
        else if(object instanceof Chest)
          {          
          Chest chest=(Chest)object;
          
          if(player.nextto(chest,0.25))
            {
            if(chest.isOpen())
              {
              chest.close();
              }
            else
              {
              chest.open();
              }
            
            world.modify(chest);
            }
          }
        }
      }

    Log4J.startMethod(logger,"use");
    }

  public int getTurn()
    {
    return rpman.getTurn();
    }

  /** Notify it when a new turn happens */
  synchronized public void beginTurn()
    {
    Log4J.startMethod(logger,"beginTurn");
    
    logger.debug("lists: "+corpses.size()+","+corpsesToRemove.size()+","+foodItems.size()+","+npcs.size()+","+npcsToAdd.size()+","+npcsToRemove.size()+","+playersObject.size()+","+playersObjectRmText.size()+","+respawnPoints.size());

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

        if(getTurn()%2==0 && object.isAttacking()) //1 round = 5 turns
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
      logger.error("error in beginTurn",e);
      }
    finally
      {
      Log4J.finishMethod(logger,"beginTurn");
      }
    }

  synchronized public void endTurn()
    {
    Log4J.startMethod(logger,"endTurn");
    try
      {
      for(Food food: foodItems) food.regrow();
      for(RespawnPoint point: respawnPoints) point.nextTurn();
      for(Corpse corpse: corpses) corpse.logic();
      }
    catch(Exception e)
      {
      logger.error("error in endTurn",e);
      }
    finally
      {
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
      
      online(player.getName());
      
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
          offline(object.getName());

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


