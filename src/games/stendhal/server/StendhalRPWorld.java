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
    RPClass sign=new RPClass("sign");
    sign.add("text",RPClass.STRING);
    sign.add("x",RPClass.FLOAT);
    sign.add("y",RPClass.FLOAT);
    
    RPClass player=new RPClass("player");
    player.add("name",RPClass.STRING);
    player.add("x",RPClass.FLOAT);
    player.add("y",RPClass.FLOAT);
    player.add("dx",RPClass.FLOAT);
    player.add("dy",RPClass.FLOAT); 
    player.add("dir",RPClass.BYTE);
    player.add("xp",RPClass.SHORT);
    player.add("hp",RPClass.SHORT);
    player.add("atk",RPClass.SHORT);
    player.add("def",RPClass.SHORT);
    player.add("text",RPClass.STRING);
    player.add("stopped",RPClass.FLAG,RPClass.HIDDEN);
    player.add("moving",RPClass.FLAG,RPClass.HIDDEN);
    Logger.trace("StendhalRPWorld::createRPClasses","<");
    }
  
  public void onInit() throws Exception
    {
    StendhalRPZone village=new StendhalRPZone("village");
    village.setEntryPoint("26,43");
    village.addLayer("village_0_floor","games/stendhal/server/maps/village_0_floor.stend");
    village.addLayer("village_1_object","games/stendhal/server/maps/village_1_object.stend");
    village.addLayer("village_2_roof","games/stendhal/server/maps/village_2_roof.stend",false);
    village.populate("games/stendhal/server/maps/village_objects.stend");
    addRPZone(village);


    StendhalRPZone city=new StendhalRPZone("city");
    city.setEntryPoint("10,34");
    city.addLayer("city_0_floor","games/stendhal/server/maps/city_0_floor.stend");
    city.addLayer("city_1_object","games/stendhal/server/maps/city_1_object.stend");
    city.addLayer("city_2_roof","games/stendhal/server/maps/city_2_roof.stend",false);
    city.populate("games/stendhal/server/maps/city_objects.stend");

    addRPZone(city);
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
