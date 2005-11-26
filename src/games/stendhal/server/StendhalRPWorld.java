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
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.npc.NPC;
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
  
  /** 
   * checks if the pathfinder thread is still alive. If it is not, it is 
   * restarted.
   */
  public void checkPathfinder()
  {
    if (pathfinderThread == null || !pathfinderThread.isAlive())
    {
      logger.fatal("Pathfinderthread died");
      pathfinderThread = new PathfinderThread(this);
      pathfinderThread.start();
    }
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
    Door.generateRPClass();
    SheepFood.generateRPClass();
    Corpse.generateRPClass();
    Item.generateRPClass();
    Chest.generateRPClass();
        
    RPEntity.generateRPClass();
    
    NPC.generateRPClass();

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
    addArea("afterlife");
    addArea("forest");
//    addArea("rat_dungeon_000");
//    addArea("rat_dungeon_001");
//    addArea("valley");
//    addArea("template");
    }
  
  public StendhalRPZone addArea(String name) throws java.io.IOException
    {
    return addArea(name,name,true);
    }
    
  public StendhalRPZone addArea(String name, String content, boolean populate) throws java.io.IOException
    {
    StendhalRPZone area=new StendhalRPZone(name, this);
    area.addLayer(name+"_0_floor","games/stendhal/server/maps/"+content+"_0_floor.stend");
    area.addLayer(name+"_1_terrain","games/stendhal/server/maps/"+content+"_1_terrain.stend");
    area.addLayer(name+"_2_object","games/stendhal/server/maps/"+content+"_2_object.stend");
    area.addLayer(name+"_3_roof","games/stendhal/server/maps/"+content+"_3_roof.stend");
    area.addCollisionLayer(name+"_collision","games/stendhal/server/maps/"+content+"_collision.stend");
    area.addNavigationLayer(name+"_navigation","games/stendhal/server/maps/"+content+"_navigation.stend");
    area.populate("games/stendhal/server/maps/"+content+"_objects.stend");
    addRPZone(area);
    
    if(populate)
      {
      try
        {
        Class entityClass=Class.forName("games.stendhal.server.maps."+name);
        java.lang.reflect.Constructor constr=entityClass.getConstructor(StendhalRPWorld.class, StendhalRPZone.class);
  
        // simply creatre a new instance. The constructor creates all additionally objects  
        constr.newInstance(this, area);
        }
      catch(Exception e)
        {
        logger.info("Zone '"+name+"' doesn't have an extra populate method",e);
        }
      }

    return area;
    }
  
  /** Creates a new house and add it to the zone.
   *  num is the unique idenfier for portals 
   *  x and y are the position of the door of the house. 
   */
  public void createHouse(int num, StendhalRPZone zone, int x, int y) throws java.io.IOException
    {
    String name=zone.getID().getID()+"_house_"+Integer.toString(num);
    
    Door door=new Door();
    door.setx(x);
    door.sety(y);
    door.setNumber(num);
    door.setDestination(name,0);
    zone.assignRPObjectID(door);
    zone.add(door);
    zone.addPortal(door);
    
    StendhalRPZone house=addArea(name,"house_000",false);
    Portal portal=new Portal();
    portal.setDestination(zone.getID().getID(),num);
    portal.setx(7);
    portal.sety(1);
    portal.setNumber(0);
    house.assignRPObjectID(portal);
    house.add(portal);
    house.addPortal(portal);    
    }

  public void onFinish() throws Exception
    {
    }
   
  }
