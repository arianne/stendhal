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

import games.stendhal.server.entity.*;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.TrainingDummy;
import games.stendhal.server.rule.RuleManager;
import games.stendhal.server.rule.RuleSetFactory;
import marauroa.common.Log4J;
import marauroa.common.game.RPClass;
import marauroa.server.game.RPWorld;
import org.apache.log4j.Logger;

public class StendhalRPWorld extends RPWorld
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(StendhalRPWorld.class);
  
  /** The pathfinder thread.*/
  private PathfinderThread pathfinderThread;
  
  /** The rule system manager */
  private RuleManager ruleManager;
  
  public StendhalRPWorld() throws Exception
    {
    super();

    Log4J.startMethod(logger,"StendhalRPWorld");
    createRPClasses();
    ruleManager = RuleSetFactory.getRuleSet("default");
    Log4J.finishMethod(logger,"StendhalRPWorld");
    }
  
  /** 
   * Returns the pathfinder. The return value is undefined until onInit() is
   * called.
   * @return the pathfinder
   */
  public PathfinderThread getPathfinder()
  {
    return pathfinderThread;
  }
  
  /** returns the current rulemanager. */
  public RuleManager getRuleManager()
  {
    return ruleManager;
  }
  
  private void createRPClasses()
    {
    Log4J.startMethod(logger,"createRPClasses");
    
    Entity.generateRPClass();

    Sign.generateRPClass();
    Portal.generateRPClass();
    Food.generateRPClass();
    Corpse.generateRPClass();
    Item.generateRPClass();
    
    RPEntity.generateRPClass();
    
    NPC.generateRPClass();
    TrainingDummy.generateRPClass();

    Creature.generateRPClass();
    Sheep.generateRPClass();

    Player.generateRPClass();
    
    RPClass chatAction=new RPClass("chat");
    chatAction.add("text",RPClass.LONG_STRING);
    
        
    Log4J.finishMethod(logger,"createRPClasses");
    }
  
  public void onInit() throws Exception
    {
    // create the pathfinder thread and start it
    pathfinderThread = new PathfinderThread(this);
    pathfinderThread.start();

    addArea("village");
    addArea("tavern");
    addArea("city");
    addArea("plains");
    addArea("dungeon_000");
    addArea("dungeon_001");
//    addArea("rat_dungeon_000");
//    addArea("rat_dungeon_001");
    addArea("afterlive");
    addArea("forest");
//    addArea("valley");
    }
  
  private void addArea(String name) throws java.io.IOException
    {
    StendhalRPZone area=new StendhalRPZone(name, this);
    area.addLayer(name+"_0_floor","games/stendhal/server/maps/"+name+"_0_floor.stend");
    area.addLayer(name+"_1_terrain","games/stendhal/server/maps/"+name+"_1_terrain.stend");
    area.addLayer(name+"_2_object","games/stendhal/server/maps/"+name+"_2_object.stend");
    area.addLayer(name+"_3_roof","games/stendhal/server/maps/"+name+"_3_roof.stend");
    area.addCollisionLayer(name+"_collision","games/stendhal/server/maps/"+name+"_collision.stend");
    area.addNavigationLayer(name+"_navigation","games/stendhal/server/maps/"+name+"_navigation.stend");
    area.populate("games/stendhal/server/maps/"+name+"_objects.stend");
    addRPZone(area);

    try
      {
      Class entityClass=Class.forName("games.stendhal.server.maps."+name);
      java.lang.reflect.Constructor constr=entityClass.getConstructor(StendhalRPZone.class);
      Object object=constr.newInstance(area);
      }
    catch(Exception e)
      {
      logger.info("Zone '"+name+"' doesn't have an extra populate method");
      }
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
