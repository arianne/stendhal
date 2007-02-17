package games.stendhal.client.entity;

import games.stendhal.client.GameObjects;
import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPObject;

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

public class EntityFabric {

public static Entity createPlayer(GameObjects gameObjects, RPObject object){
	Player pl = new Player(gameObjects,object);
		return pl;
}
	
	
	
	
	
	
	
}
