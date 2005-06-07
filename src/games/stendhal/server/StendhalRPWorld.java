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
    AngelNPC.generateRPClass();
    BeggarNPC.generateRPClass();
    BuyerNPC.generateRPClass();
    JournalistNPC.generateRPClass();
    SellerNPC.generateRPClass();
    TavernBarMaidNPC.generateRPClass();
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
    addArea("village",1);
    addArea("tavern",1);
    addArea("city",1);
    addArea("plains",1);
    addArea("dungeon_000",1);
    addArea("afterlive",1);
    addArea("forest",1);
    addArea("dungeon_001",1);
    }
  
  private void addArea(String name, int version) throws java.io.IOException
    {
    StendhalRPZone area=new StendhalRPZone(name);
    area.addLayer(name+"_0_floor","games/stendhal/server/maps/"+name+"_0_floor.stend", version);
    area.addLayer(name+"_1_object","games/stendhal/server/maps/"+name+"_1_object.stend", version);
    area.addLayer(name+"_2_roof","games/stendhal/server/maps/"+name+"_2_roof.stend", version);
    area.addCollisionLayer(name+"_collision","games/stendhal/server/maps/"+name+"_collision.stend", version);
    area.populate("games/stendhal/server/maps/"+name+"_objects.stend");
    addRPZone(area);
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
