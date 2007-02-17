package games.stendhal.client.entity;

import org.apache.log4j.Logger;

import games.stendhal.client.GameObjects;
import marauroa.common.Log4J;
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

//private static Entity createPlayer(GameObjects gameObjects, RPObject object){
//	Player pl = new Player(gameObjects,object);
//		return pl;
//}
/** Create a Entity of the correct type depending of the arianne object */
public static Entity createEntity(RPObject object) {
	try {
//		if (object.get("type").equals("player")) {
//			return EntityFabric.createPlayer(GameObjects.getInstance(),  object);
//		}

		String type = object.get("type");
		String eclass = null;
		if (object.has("class")) {
			eclass = object.get("class");
		}

		Class entityClass = EntityMap.getClass(type,eclass);

		if (entityClass == null) {
			// If there is no entity, let's try without using class.
			entityClass = EntityMap.getClass(type, null);
		}

		java.lang.reflect.Constructor constr = entityClass.getConstructor(
				GameObjects.class, RPObject.class);
		return (Entity) constr.newInstance(GameObjects.getInstance(), object);
	} catch (Exception e) {
		Logger logger= Log4J.getLogger(EntityFabric.class );
		logger.error("cannot create entity for object " + object, e);
		return null;
	}
}
	
	
	
	
	
	
	
}
