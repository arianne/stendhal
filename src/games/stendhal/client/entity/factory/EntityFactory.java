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

package games.stendhal.client.entity.factory;

import org.apache.log4j.Logger;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.events.EventDispatcher;
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
	 * Creates an Entity of the correct type depending on the Marauroa object.
	 *
	 * @param object
	 *            the underlying server RPObject
	 * @return the created Entity
	 */
	public static IEntity createEntity(final RPObject object) {
		try {
			final String type = object.getRPClass().getName();
			if (type.equals("player") && object.has("name")) {
				if (StendhalClient.get().getCharacter().equalsIgnoreCase(
						object.get("name"))) {
					final User me = new User();
					me.initialize(object);
					EventDispatcher.dispatchEvents(object, me);
					return me;
				}
			}

			String eclass = null;
			if (object.has("class")) {
				eclass = object.get("class");
			}

			String subClass = null;
			if (object.has("subclass")) {
				subClass = object.get("subclass");
			}


			final Class< ? extends IEntity> entityClass = EntityMap.getClass(type, eclass, subClass);
			if (entityClass == null) {
					return null;
			}

			final IEntity en = entityClass.getDeclaredConstructor().newInstance();
			en.initialize(object);
			if (en instanceof Entity) {
				EventDispatcher.dispatchEvents(object, (Entity) en);
			}


			return en;
		} catch (final Exception e) {
			logger.error("Error creating entity for object: " + object, e);
			return null;
		}
	}
}
