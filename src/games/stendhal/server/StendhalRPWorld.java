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
    RPClass player=new RPClass("player");
    player.add("name",RPClass.STRING);
    player.add("x",RPClass.FLOAT);
    player.add("y",RPClass.FLOAT);
    player.add("dx",RPClass.FLOAT);
    player.add("dy",RPClass.FLOAT);    
    player.add("xp",RPClass.SHORT);
    player.add("hp",RPClass.SHORT);
    player.add("atk",RPClass.SHORT);
    player.add("def",RPClass.SHORT);
    }
  
  public void onInit() throws Exception
    {
    StendhalRPZone village=new StendhalRPZone("village");
    village.setEntryPoint("10,10");
    village.addLayer("village_layer0","games/stendhal/server/maps/village_layer0.txt");
    addRPZone(village);


    StendhalRPZone city=new StendhalRPZone("city");
    city.setEntryPoint("34,30");
    
    for(int i=0;i<5;i++)
      {
      RPObject object=new RPObject();
      city.assignRPObjectID(object);
      object.put("type","pot");
      object.put("x",9+i);
      object.put("y",11);
      city.add(object);
      }
    
    city.addLayer("city_layer0","games/stendhal/server/maps/city_layer0.txt");
    city.addLayer("city_layer1","games/stendhal/server/maps/city_layer1.txt");
    addRPZone(city);
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
