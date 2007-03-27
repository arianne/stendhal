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

package games.stendhal.client.entity;

import games.stendhal.client.StendhalClient;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * @author astridemma
 * 
 */
public class EntityFabric {

	/**
	 *  Create a Entity of the correct type depending of the arianne object
	 *
	 * @param object the underlying server RPObject
	 * @return the created Entity
	 */
	public static Entity createEntity(RPObject object) {
		try {
			if (object.has("name")){
				if (StendhalClient.userName.equals(object.get("name"))){
			          return new User(object);
				}
			}
			String type = object.get("type");
			String eclass = null;
			if (object.has("class")) {
				eclass = object.get("class");
			}

			Class entityClass = EntityMap.getClass(type, eclass);

			if (entityClass == null) {
				// If there is no entity, let's try without using class.
				entityClass = EntityMap.getClass(type, null);
			}

			java.lang.reflect.Constructor constr = entityClass.getConstructor(RPObject.class);
			Entity en = (Entity) constr.newInstance(object);
			if (en instanceof Inspectable) {
				((Inspectable) en).setInspector(StendhalClient.get().getGameGUI());
			}
			return en;
		} catch (Exception e) {
			Logger logger = Log4J.getLogger(EntityFabric.class);
			logger.error("cannot create entity for object " + object, e);
			return null;
		}
	}

}
