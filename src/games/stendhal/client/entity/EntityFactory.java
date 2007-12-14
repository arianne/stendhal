/***************************************************************************
 *                   (C) Copyright 2003-2007 - Marauroa                    *
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

import org.apache.log4j.Logger;
import marauroa.common.game.RPObject;

/**
 * creates a Stendhal Entity object based on a Marauroa RPObject.
 * 
 * @author astridemma
 */
public class EntityFactory {
	private static final Logger logger = Logger.getLogger(EntityFactory.class);

	protected EntityFactory() {
	}

	/**
	 * Create a Entity of the correct type depending of the arianne object
	 * 
	 * @param object
	 *            the underlying server RPObject
	 * @return the created Entity
	 */
	public static Entity createEntity(final RPObject object) {
		try {
			String type = object.get("type");

			if (type.equals("player") && object.has("name")) {
				if (StendhalClient.get().getAccountUsername().equalsIgnoreCase(
						object.get("name"))) {
					User me = new User();
					me.initialize(object);
					return me;
				}
			}

			String eclass = null;
			if (object.has("class")) {
				eclass = object.get("class");
			}

			Class<? extends Entity> entityClass = EntityMap.getClass(type,
					eclass);
			if (entityClass == null) {
				// If there is no entity, let's try without using class.
				entityClass = EntityMap.getClass(type, null);

				if (entityClass == null) {
					return null;
				}
			}

			Entity en = entityClass.newInstance();
			en.initialize(object);

			return en;
		} catch (Exception e) {
			logger.error("Error creating entity for object: " + object, e);
			return null;
		}
	}
}
