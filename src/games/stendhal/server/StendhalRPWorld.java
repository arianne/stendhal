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

import marauroa.server.game.*;

import marauroa.common.game.*;
import marauroa.common.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.entity.creature.*;

public class StendhalRPWorld extends RPWorld
  {
  public StendhalRPWorld() throws Exception
    {
    super();

    Logger.trace("StendhalRPWorld::StendhalRPWorld",">");
    createRPClasses();
    Logger.trace("StendhalRPWorld::StendhalRPWorld","<");
    }
  
  private void createRPClasses()
    {
    Logger.trace("StendhalRPWorld::createRPClasses",">");
    
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
        
    Logger.trace("StendhalRPWorld::createRPClasses","<");
    }
  
  public void onInit() throws Exception
    {
    addArea("village");
    addArea("tavern");
    addArea("city");
    addArea("plains");
    addArea("dungeon_000");
    addArea("afterlive");
    addArea("forest");
    addArea("dungeon_001");
    }
  
  private void addArea(String name) throws java.io.IOException
    {
    StendhalRPZone area=new StendhalRPZone(name);
    area.addLayer(name+"_0_floor","games/stendhal/server/maps/"+name+"_0_floor.stend");
    area.addLayer(name+"_1_terrain","games/stendhal/server/maps/"+name+"_1_terrain.stend");
    area.addLayer(name+"_2_object","games/stendhal/server/maps/"+name+"_2_object.stend");
    area.addLayer(name+"_3_roof","games/stendhal/server/maps/"+name+"_3_roof.stend");
    area.addCollisionLayer(name+"_collision","games/stendhal/server/maps/"+name+"_collision.stend");
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
      Logger.trace("StendhalRPWorld::addArea","D","This zone doesn't have an extra populate method");
      }
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
