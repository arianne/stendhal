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
    MarauroaRPZone village=new MarauroaRPZone("village");
    RPObject object=new RPObject();
    village.assignRPObjectID(object);
    object.put("number","1");
    object.put("string","hola village");
    object.put("flag","");
    village.add(object);
    object=new RPObject();
    village.assignRPObjectID(object);
    object.put("number","2");
    object.put("string","mundo village");
    object.put("flag","");
    village.add(object);
    addRPZone(village);

    MarauroaRPZone city=new MarauroaRPZone("city");
    object=new RPObject();
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
