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
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.rule.EntityManager;
import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPSlot;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPClass;
import marauroa.server.game.RPWorld;
import games.stendhal.server.scripting.StendhalGroovyRunner;

import org.apache.log4j.Logger;

public class Administration extends ActionListener 
  {
  private static final Logger logger = Log4J.getLogger(Administration.class);

  public static void register()
    {
    Administration administration=new Administration();
    StendhalRPRuleProcessor.register("inspect",administration);
    StendhalRPRuleProcessor.register("destroy",administration);
    StendhalRPRuleProcessor.register("tellall",administration);
    StendhalRPRuleProcessor.register("teleport",administration);
    StendhalRPRuleProcessor.register("teleportto",administration);
    StendhalRPRuleProcessor.register("alter",administration);
    StendhalRPRuleProcessor.register("summon",administration);
    StendhalRPRuleProcessor.register("summonat",administration);
    StendhalRPRuleProcessor.register("invisible",administration);
    StendhalRPRuleProcessor.register("jail",administration);
    StendhalRPRuleProcessor.register("script",administration);
    }

  public void onAction(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    if(!player.isAdmin())
      {
      // Admininistrator only commands
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
    else if(type.equals("teleportto"))
      {
      onTeleportTo(world,rules,player,action);
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
    else if(type.equals("inspect"))
      {
      onInspect(world,rules,player,action);
      }
    else if(type.equals("destroy"))
      {
      onDestroy(world,rules,player,action);
      }
    else if(type.equals("jail"))
      {
      onJail(world,rules,player,action);
      }
    else if(type.equals("script"))
      {
      onScript(world,rules,player,action);
      }
    }
  
  private void onTellEverybody(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onTellEverybody");

    if(action.has("text"))
      {
      String message = "Administrator SHOUTS: " + action.get("text");
      rules.addGameEvent(player.getName(),"tellall",action.get("text"));
      
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
        String text="Player "+name+" not found";

        player.setPrivateText(text);
        rules.removePlayerText(player);

        logger.debug(text);
        return;
        }
        
      IRPZone.ID zoneid=new IRPZone.ID(action.get("zone"));
      if(!world.hasRPZone(zoneid))
        {
        String text="Zone "+zoneid+" not found";

        player.setPrivateText(text);
        rules.removePlayerText(player);

        logger.debug(text);
        return;
        }
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(zoneid);
      int x=action.getInt("x");
      int y=action.getInt("y");
      
      if(StendhalRPAction.placeat(zone,teleported,x,y))
        {
        StendhalRPAction.changeZone(teleported,zone.getID().getID());
        StendhalRPAction.transferContent(teleported);
        
        rules.addGameEvent(player.getName(),"teleport",teleported.getName());

        world.modify(teleported);      
        }
      else
        {
        player.setPrivateText("Position ["+x+","+y+"] is occupied");
        rules.removePlayerText(player);
        }
      }

    Log4J.finishMethod(logger,"onTeleport");
    }


  private void onTeleportTo(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onTeleportTo");

    if(action.has("target"))
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
        String text="Player "+name+" not found";

        player.setPrivateText(text);
        rules.removePlayerText(player);

        logger.debug(text);
        return;
        }
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(teleported.getID());
      int x=teleported.getx();
      int y=teleported.gety();
      
      if(StendhalRPAction.placeat(zone,player,x,y))
        {
        rules.addGameEvent(player.getName(),"teleportto",teleported.getName());

        StendhalRPAction.changeZone(player,zone.getID().getID());
        StendhalRPAction.transferContent(player);
        }

      world.modify(player);
      }

    Log4J.finishMethod(logger,"onTeleportTo");
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
      
      if(stat.equals("name"))
        {
        logger.error("DENIED: Admin "+player.getName()+" trying to change player "+name+"'s name");
        return;
        }
        
      RPClass clazz=changed.getRPClass();
      
      boolean isNumerical=false;
      
      byte type=clazz.getType(stat);      
      if(type==RPClass.BYTE || type==RPClass.SHORT ||type==RPClass.INT)
        {
        isNumerical=true;
        }
       
      if(changed.getRPClass().hasAttribute(stat) && changed.has(stat))
        {
        String value=action.get("value");
        String mode=action.get("mode");
        
        if(isNumerical)
          {
          int numberValue=Integer.parseInt(value);
          if(mode.equals("add"))
            {
            numberValue=changed.getInt(stat)+numberValue;
            }
            
          if(mode.equals("sub"))
            {
            numberValue=changed.getInt(stat)-numberValue;
            }
            
          if(stat.equals("hp") && changed.getInt("base_hp")<numberValue)
            {
            logger.error("DENIED: Admin "+player.getName()+" trying to set player "+name+"'s HP over its Base HP");
            return;
            }
          
          switch(type)
            {
            case RPClass.BYTE:
              if(numberValue>Byte.MAX_VALUE || numberValue<Byte.MIN_VALUE) return;
              break;
            case RPClass.SHORT:
              if(numberValue>Short.MAX_VALUE || numberValue<Short.MIN_VALUE) return;
              break;
            case RPClass.INT:
              if(numberValue>Integer.MAX_VALUE || numberValue<Integer.MIN_VALUE) return;
              break;
            }

          rules.addGameEvent(player.getName(),"alter",changed.getName(),stat,Integer.toString(numberValue));
          changed.put(stat, numberValue);
          }
        else
          {
          // Can be only setif value is not a number
          if(mode.equals("set"))
            {
            rules.addGameEvent(player.getName(),"alter",changed.getName(),stat,action.get("value"));
            changed.put(stat, action.get("value"));
            }
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
          rules.addGameEvent(player.getName(),"summon",type);
          Creature creature=manager.getCreature(type);
          
          zone.assignRPObjectID(creature);
          StendhalRPAction.placeat(zone,creature,x,y);
          zone.add(creature);
          
          rules.addNPC(creature);
          }
        else if(manager.isItem(type))
          {
          rules.addGameEvent(player.getName(),"summon",type);
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
          rules.addGameEvent(player.getName(),"summonat",changed.getName(),slot.getName(),type);
          Item item=manager.getItem(type);
          
          if(action.has("amount") && item instanceof StackableItem)
            {
            ((StackableItem)item).setQuantity(action.getInt("amount"));
            }
          
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
    Log4J.startMethod(logger,"onInvisible");
    
    if(player.has("invisible"))
      {
      player.remove("invisible");
      }
    else
      {
      player.put("invisible","");
      }

    Log4J.finishMethod(logger,"onInvisible");
    }

  private void onInspect(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onInspect");

    RPEntity inspected=null;
      
    if(action.has("target"))
      {
      String name=action.get("target");
      for(Player p : rules.getPlayers())
        {
        if(p.getName().equals(name))
          {
          inspected=p;
          break;
          }
        }
      }
    else if(action.has("targetid"))
      {
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      
      RPObject.ID id=new RPObject.ID(action.getInt("targetid"),zone.getID().getID());
      if(zone.has(id))
        {
        RPObject object=zone.get(id);
        
        if(object instanceof RPEntity)
          {
          inspected=(RPEntity)object;
          }
        }
      }
      
    if(inspected==null)
      {
      String text="Entity not found";

      player.setPrivateText(text);
      rules.removePlayerText(player);
      return;
      }
    
    StringBuffer st=new StringBuffer();
    st.append("Inspected "+inspected.getName()+" is:");
    st.append("\nID:     "+inspected.getID());
    st.append("\nATK:    "+inspected.getATK()+"("+inspected.getATKXP()+")");
    st.append("\nDEF:    "+inspected.getDEF()+"("+inspected.getDEFXP()+")");
    st.append("\nHP:     "+inspected.getHP()+" / "+inspected.getBaseHP());
    st.append("\nXP:     "+inspected.getXP());
    st.append("\nLevel:  "+inspected.getLevel());
    
    st.append("\nequips");
    for(RPSlot slot: inspected.slots())
      {
      if(slot.getName().equals("!buddy") )
        {
        continue;
        }
        
      st.append("\n    Slot "+slot.getName()+": ");
      
      if(slot.getName().equals("!quests")  || slot.getName().equals("!kills"))
        {
        for(RPObject object: slot)
          {
          st.append(object);
          }
        }
      else
        {
        for(RPObject object: slot)
          {
          String item=object.get("type");
          if(object.has("name"))
            {
            item=object.get("name");
            }
          
          if(object instanceof StackableItem)
            {
            st.append("["+item+" Q="+object.get("quantity")+"], ");
            }
          else
            {
            st.append("["+item+"], ");
            }
          }
        }
      }
    
    player.setPrivateText(st.toString());
    rules.removePlayerText(player);

    Log4J.finishMethod(logger,"onInspect");
    }


  private void onDestroy(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onDestroy");

    Entity inspected=null;
      
    if(action.has("targetid"))
      {
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(player.getID());
      
      RPObject.ID id=new RPObject.ID(action.getInt("targetid"),zone.getID().getID());
      if(zone.has(id))
        {
        RPObject object=zone.get(id);
        
        if(object instanceof Entity)
          {
          inspected=(Entity)object;
          }
        }
      }
      
    if(inspected==null)
      {
      String text="Entity not found";

      player.setPrivateText(text);
      rules.removePlayerText(player);
      return;
      }
    
    if(inspected instanceof Player)
      {
      String text="You can't remove players";

      player.setPrivateText(text);
      rules.removePlayerText(player);
      return;
      }
    
    if(inspected instanceof RPEntity)
      {
      ((RPEntity)inspected).onDead(player);
      }
    else if(inspected instanceof Item)
      {
      world.remove(inspected.getID());
      }    
    
    player.setPrivateText("Removed entity "+action.get("targetid"));;
    rules.removePlayerText(player);

    Log4J.finishMethod(logger,"onInspect");
    }

  private void onJail(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onTeleport");

    if(action.has("target"))
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
        String text="Player "+name+" not found";

        player.setPrivateText(text);
        rules.removePlayerText(player);

        logger.debug(text);
        return;
        }
        
      IRPZone.ID zoneid=new IRPZone.ID("int_admin_jail");
      if(!world.hasRPZone(zoneid))
        {
        String text="Zone "+zoneid+" not found";

        player.setPrivateText(text);
        rules.removePlayerText(player);

        logger.debug(text);
        return;
        }
      
      StendhalRPZone zone=(StendhalRPZone)world.getRPZone(zoneid);
      int x=20;
      int y=14;
      
      if(StendhalRPAction.placeat(zone,teleported,x,y))
        {
        StendhalRPAction.changeZone(teleported,zone.getID().getID());
        StendhalRPAction.transferContent(teleported);
        
        rules.addGameEvent(player.getName(),"teleport",teleported.getName());

        world.modify(teleported);      
        }
      else
        {
        player.setPrivateText("Position ["+x+","+y+"] is occupied");
        rules.removePlayerText(player);
        }
      }

    Log4J.finishMethod(logger,"onTeleport");
    }
  
  private void onScript(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action)
    {
    Log4J.startMethod(logger,"onScript");

    System.out.println("onScript " + action);
    
    if(action.has("target"))
      {
      String script=action.get("target");
      String text = "Groovy script system not initialized.";
      if(rules.getGroovyRunner()!=null)
        {
        text = "Script " + script + "not found!";
        if(rules.getGroovyRunner().load(script))
          {
          text = "Script " + script + " was successfully executed.";
          }
        else
          {
          String msg = rules.getGroovyRunner().getMessage(script);
          if (msg != null)
            {
            text = msg;            
            }
          else
            {
            text = "Script " + script + " not found or unexpected error!";
            }
          }
        }
      player.setPrivateText(text);
      rules.removePlayerText(player);
      }
    Log4J.finishMethod(logger,"onScript");
    }

  }
