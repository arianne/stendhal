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
import games.stendhal.server.pathfinder.PathfinderThread;
import games.stendhal.server.rule.RuleManager;
import games.stendhal.server.rule.RuleSetFactory;
import games.stendhal.server.maps.IContent;
import marauroa.common.Log4J;
import marauroa.common.game.RPClass;
import marauroa.common.game.IRPZone;
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
    
    // Chat action class
    RPClass chatAction=new RPClass("chat");
    chatAction.add("text",RPClass.LONG_STRING);

    // Tell action class
    chatAction=new RPClass("tell");
    chatAction.add("text",RPClass.LONG_STRING);
    chatAction.add("target",RPClass.STRING);
    
        
    Log4J.finishMethod(logger,"createRPClasses");
    }
    
  public void onInit() throws Exception
    {
    // create the pathfinder thread and start it
    pathfinderThread = new PathfinderThread(this);
    pathfinderThread.start();
   
    // Load zones
    // Ground level
    addArea("0_semos_west_plains");
    addArea("0_semos_village");
    addArea("0_semos_city");
      addArea("int_semos_tavern");
      addArea("int_semos_temple");
      addArea("int_semos_blacksmith");
      addArea("int_semos_library");
    addArea("0_semos_south_plains");
    addArea("0_semos_road_ados");
    addArea("0_semos_south_road_ados");
    addArea("0_ados_west_forest");

    addArea("0_orril_north_forest");
    addArea("0_semos_forest");
    addArea("0_nalwor_forest_north");
    addArea("0_nalwor_north_forest");

    addArea("0_orril_castle");
    addArea("0_orril_forest");
    addArea("0_nalwor_forest");
    addArea("0_nalwor_city");

    addArea("0_orril_river");
    addArea("0_orril_forest_river");
    addArea("0_nalwor_forest_river");
    addArea("0_nalwor_river");
    
    // Level -1
    addArea("-1_semos_dungeon");
    addArea("-1_orril_forest_dungeon");

    // Level -2
    addArea("-2_semos_dungeon");
    addArea("-2_kotoch_entrance");
    addArea("-2_orril_forest_dungeon");

    // Level -3
    addArea("-3_semos_dungeon");
    addArea("-3_orril_forest_dungeon");

    // Level -4
    addArea("-4_semos_dungeon");

    // Level -5
    addArea("-5_kanmararn_entrace");

    // Level -6
    // addArea("-6_kanmararn_city");

    // Level -7
    // addArea("-7_kanmararn_jail");
    
    // Interiors
    addArea("int_afterlife");
    addArea("int_admin_jail");
    
    
    populateZone("Afterlife");
    populateZone("Jail");
    populateZone("Semos");

    /** After all the zones has been loaded, check how many portals are unpaired */
    for(IRPZone zone: this)
      {
      for(Portal portal: ((StendhalRPZone)zone).getPortals())
        {
        if(!portal.loaded())
          {
          logger.warn(portal+" has no destination");
          }
        }
      }
    }
  
  private boolean populateZone(String name)
    {
    try
      {
      Class entityClass=Class.forName("games.stendhal.server.maps."+name);
      
      boolean implementsIContent=false;
      
      Class[] interfaces=entityClass.getInterfaces();
      for(Class interf: interfaces)
        {
        if(interf.equals(IContent.class))
          {
          implementsIContent=true;
          break;
          }        
        }
      
      if(implementsIContent==false)
        {
        logger.debug("Class don't implement IContent interface.");
        return false;
        }
      
      logger.info("Loading Zone populate class: "+name);
      java.lang.reflect.Constructor constr=entityClass.getConstructor(StendhalRPWorld.class);

      // simply creatre a new instance. The constructor creates all additionally objects  
      constr.newInstance(this);
      return true;
      }
    catch(Exception e)
      {
      logger.warn("Zone Populate class("+name+") loading failed.",e);
      return false;
      }
    }

  public StendhalRPZone addArea(String name) throws org.xml.sax.SAXException, java.io.IOException
    {
    return addArea(name,name.replace("-","sub_"));
    }
    
  public StendhalRPZone addArea(String name, String content) throws org.xml.sax.SAXException, java.io.IOException
    {
    logger.info("Loading area: "+name);
    StendhalRPZone area=new StendhalRPZone(name, this);
    
    ZoneXMLLoader instance=ZoneXMLLoader.get();
    ZoneXMLLoader.XMLZone xmlzone=instance.load("data/maps/"+content+".xstend");
    
    area.addLayer(name+"_0_floor",xmlzone.getLayer("0_floor"));
    area.addLayer(name+"_1_terrain",xmlzone.getLayer("1_terrain"));
    area.addLayer(name+"_2_object",xmlzone.getLayer("2_object"));
    area.addLayer(name+"_3_roof",xmlzone.getLayer("3_roof"));

    String layer=xmlzone.getLayer("4_roof_add");
    if(layer!=null)
      {
      area.addLayer(name+"_4_roof_add",layer);
      }
    
    area.addCollisionLayer(name+"_collision",xmlzone.getLayer("collision"));
    area.addProtectionLayer(name+"_protection",xmlzone.getLayer("protection"));
    area.addNavigationLayer(name+"_navigation",xmlzone.getLayer("navigation"));
    
    if(xmlzone.isInterior())
      {
      area.setPosition();
      }
    else
      {
      area.setPosition(xmlzone.getLevel(),xmlzone.getx(),xmlzone.gety());
      }
    
    area.populate(xmlzone.getLayer("objects"));
    addRPZone(area);
    
    return area;
    }
    
  /** Creates a new house and add it to the zone.
   *  num is the unique idenfier for portals 
   *  x and y are the position of the door of the house. 
   */
  public void createHouse(StendhalRPZone zone, int x, int y) throws org.xml.sax.SAXException, java.io.IOException
    {
    Door door=new Door();
    door.setx(x);
    door.sety(y);
    int dest=zone.assignPortalID(door);

    String name="int_"+zone.getID().getID()+"_house_"+Integer.toString(dest);
    
    door.setDestination(name,0);
    zone.assignRPObjectID(door);
    zone.add(door);
    zone.addPortal(door);
    
    StendhalRPZone house=addArea(name,"int_house_000");
    Portal portal=new Portal();
    portal.setDestination(zone.getID().getID(),dest);
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
