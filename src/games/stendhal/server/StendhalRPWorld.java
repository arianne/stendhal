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
    Portal.generateRPClass();
    Food.generateRPClass();
    
    RPEntity.generateRPClass();
    
    NPC.generateRPClass();
    BuyerNPC.generateRPClass();
    SellerNPC.generateRPClass();
    WelcomerNPC.generateRPClass();

    TrainingDummy.generateRPClass();

    Sheep.generateRPClass();
    Wolf.generateRPClass();
    Rat.generateRPClass();
    CaveRat.generateRPClass();
    Cobra.generateRPClass();
    Boar.generateRPClass();
    Kobold.generateRPClass();
    Ogre.generateRPClass();
    Goblin.generateRPClass();
    Gargoyle.generateRPClass();
    Troll.generateRPClass();
    Orc.generateRPClass();

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
    }
  
  private void addArea(String name) throws java.io.IOException
    {
    StendhalRPZone area=new StendhalRPZone(name);
    area.addLayer(name+"_0_floor","games/stendhal/server/maps/"+name+"_0_floor.stend");
    area.addLayer(name+"_1_object","games/stendhal/server/maps/"+name+"_1_object.stend");
    area.addLayer(name+"_2_roof","games/stendhal/server/maps/"+name+"_2_roof.stend");
    area.addCollisionLayer(name+"_collision","games/stendhal/server/maps/"+name+"_collision.stend");
    area.populate("games/stendhal/server/maps/"+name+"_objects.stend");
    addRPZone(area);
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
