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
    Logger.trace("StendhalRPWorld::StendhalRPWorld","<");
    }
  
  public void onInit() throws Exception
    {
    StendhalRPZone village=new StendhalRPZone("village");
    for(int i=0;i<5;i++)
      {
      RPObject object=new RPObject();
      village.assignRPObjectID(object);
      object.put("type","pot");
      object.put("x",9+i);
      object.put("y",11);
      village.add(object);
      }
    village.addLayer("city_layer0","games/stendhal/server/maps/city_layer0.txt");
    village.addLayer("city_layer1","games/stendhal/server/maps/city_layer1.txt");
    addRPZone(village);


    StendhalRPZone city=new StendhalRPZone("city");
    RPObject object=new RPObject();
    city.assignRPObjectID(object);
    object.put("number","1");
    object.put("string","hola city");
    object.put("flag","");
    city.add(object);
    object=new RPObject();
    city.assignRPObjectID(object);
    object.put("number","2");
    object.put("string","mundo  city");
    object.put("flag","");
    city.add(object);
    addRPZone(city);
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
