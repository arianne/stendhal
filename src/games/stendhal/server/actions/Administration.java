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
package games.stendhal.server.actions;


import games.stendhal.server.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.rule.EntityManager;
import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPSlot;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class Administration extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

  public static void register()
    {
    Administration Administration=new Administration();
    StendhalRPRuleProcessor.register("tellall",Administration);
    StendhalRPRuleProcessor.register("teleport",Administration);
    StendhalRPRuleProcessor.register("alter",Administration);
    StendhalRPRuleProcessor.register("summon",Administration);
    StendhalRPRuleProcessor.register("summonat",Administration);
    StendhalRPRuleProcessor.register("invisible",Administration);
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    if(!player.has("admin"))
      {
      // Admininistrador only commands
      logger.warn("Player "+player.getName()+" trying to run admin commands");
      return;
      }
      
    String type=action.get("type");
    
    if(type.equals("tellall"))
      {
      onTellEverybody(world,rules,player,action);
      }
    else if(type.equals("teleport"))
      {
      onTeleport(world,rules,player,action);
      }    
    else if(type.equals("alter"))
      {
      onChangePlayer(world,rules,player,action);
      }    
    else if(type.equals("summon"))
      {
      onSummon(world,rules,player,action);
      }          
    else if(type.equals("summonat"))
      {
      onSummonAt(world,rules,player,action);
      }          
    else if(type.equals("invisible"))
      {
      onInvisible(world,rules,player,action);
      }          
    }
  
  private void onTellEverybody(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onTellEverybody");

    if(action.has("text"))
      {
      String message = "Administrator SHOUTS: " + action.get("text");
      for(Player p : rules.getPlayers())
        {
        p.setPrivateText(message);
        rules.removePlayerText(p);
        world.modify(p);
        }

      }

    Log4J.finishMethod(logger,"onTellEverybody");
    }

  private void onTeleport(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onTeleport");

    if(action.has("target") && action.has("zone") && action.has("x") && action.has("y"))
      {
      Player teleported=null;
      
      String name=action.get("target");
      for(Player p : rules.getPlayers())
        {
        if(p.getName().equals(name))
          {
          teleported=p;
          break;
          }
        }
      
      if(teleported==null)
        {
        logger.debug("Player "+name+" not found");
        return;
        }
        
      IRPZone.ID zoneid=new IRPZone.ID(action.get("zone"));
      if(!world.hasRPZone(zoneid))
        {
        logger.debug("Zone "+zoneid+" not found");
        return;
        }
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(zoneid);
      int x=action.getInt("x");
      int y=action.getInt("y");
      
      if(!zone.collides(teleported,x,y))
        {
        StendhalRPAction.changeZone(teleported,zone.getID().getID());
        StendhalRPAction.transferContent(teleported);
        
        teleported.setx(x);
        teleported.sety(y); 
        world.modify(teleported);      
        }
      }

    Log4J.finishMethod(logger,"onTeleport");
    }

  private void onChangePlayer(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onChangePlayer");

    if(action.has("target") && action.has("stat") && action.has("mode") && action.has("value"))
      {
      Player changed=null;
      
      String name=action.get("target");
      for(Player p : rules.getPlayers())
        {
        if(p.getName().equals(name))
          {
          changed=p;
          break;
          }
        }      
 
      if(changed==null)
        {
        logger.debug("Player "+name+" not found");
        return;
        }
        
       
      String stat=action.get("stat");
       
      if(changed.getRPClass().hasAttribute(stat) && changed.has(stat))
        {
        String mode=action.get("mode");
        if(mode.equals("set"))
          {
          changed.put(stat, action.get("value"));
          }
        else if(mode.equals("add"))
          {
          changed.put(stat, changed.getInt(stat)+action.getInt("value"));
          }
        else if(mode.equals("sub"))
          {
          changed.put(stat, changed.getInt(stat)-action.getInt("value"));
          }
         
        changed.update();
        world.modify(changed);
        }      
      }

    Log4J.finishMethod(logger,"onChangePlayer");
    }

  private void onSummon(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onSummon");

    if(action.has("creature") && action.has("x") && action.has("y"))
      {
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      int x=action.getInt("x");
      int y=action.getInt("y");
      
      if(!zone.collides(player,x,y))
        {
        EntityManager manager = ((StendhalRPWorld)world).getRuleManager().getEntityManager();
        String type=action.get("creature");

        // Is the entity a creature
        if(manager.isCreature(type))
          {
          Creature creature=manager.getCreature(type);
          
          zone.assignRPObjectID(creature);
          StendhalRPAction.placeat(zone,creature,x,y);
          zone.add(creature);
          
          rules.addNPC(creature);
          }
        else if(manager.isItem(type))
          {
          Item item=manager.getItem(type);
          
          zone.assignRPObjectID(item);
          StendhalRPAction.placeat(zone,item,x,y);
          zone.add(item);
          }
        }        
      }

    Log4J.finishMethod(logger,"onSummon");
    }

  private void onSummonAt(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onSummonAt");

    if(action.has("target") && action.has("slot") && action.has("item"))
      {
      Player changed=null;
      
      String name=action.get("target");
      for(Player p : rules.getPlayers())
        {
        if(p.getName().equals(name))
          {
          changed=p;
          break;
          }
        }      
 
      if(changed==null)
        {
        logger.debug("Player "+name+" not found");
        return;
        }
      
      String slotName=action.get("slot");
      if(!changed.hasSlot(slotName))
        {
        logger.debug("Player "+name+" has not RPSlot "+slotName);
        return;
        }
      
      RPSlot slot=changed.getSlot(slotName);      
      
      if(!slot.isFull())
        {
        EntityManager manager = ((StendhalRPWorld)world).getRuleManager().getEntityManager();
        String type=action.get("item");

        // Is the entity an item
        if(manager.isItem(type))
          {
          Item item=manager.getItem(type);
          
          slot.assignValidID(item);
          slot.add(item);
          
          world.modify(changed);
          }
        }        
      }

    Log4J.finishMethod(logger,"onSummonAt");
    }

  private void onInvisible(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onTellEverybody");
    
    if(player.has("invisible"))
      {
      player.remove("invisible");
      }
    else
      {
      player.put("invisible","");
      }

    Log4J.finishMethod(logger,"onTellEverybody");
    }

  }
