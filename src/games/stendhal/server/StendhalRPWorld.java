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
    ActiveEntity.generateRPClass();
    RPEntity.generateRPClass();
    NPC.generateRPClass();
    BuyerNPC.generateRPClass();
    SellerNPC.generateRPClass();
    Player.generateRPClass();
        
    Logger.trace("StendhalRPWorld::createRPClasses","<");
    }
  
  public void onInit() throws Exception
    {
    StendhalRPZone village=new StendhalRPZone("village");
    village.addLayer("village_0_floor","games/stendhal/server/maps/village_0_floor.stend");
    village.addLayer("village_1_object","games/stendhal/server/maps/village_1_object.stend");
    village.addLayer("village_2_roof","games/stendhal/server/maps/village_2_roof.stend");
    village.addCollisionLayer("village_collision","games/stendhal/server/maps/village_collision.stend");
    village.populate("games/stendhal/server/maps/village_objects.stend");
    addRPZone(village);


    StendhalRPZone city=new StendhalRPZone("city");
    city.addLayer("city_0_floor","games/stendhal/server/maps/city_0_floor.stend");
    city.addLayer("city_1_object","games/stendhal/server/maps/city_1_object.stend");
    city.addLayer("city_2_roof","games/stendhal/server/maps/city_2_roof.stend");
    city.addCollisionLayer("city_collision","games/stendhal/server/maps/city_collision.stend");
    city.populate("games/stendhal/server/maps/city_objects.stend");
    addRPZone(city);
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
