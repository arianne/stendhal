/*
 * @(#) src/games/stendhal/server/entity/portal/KeyDestroyingDoorFactory.java
 *
 * $Id$
 */
/***************************************************************************
 *                 Copyright © 2007-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>KeyDestroyingDoor</code> objects.
 */
public class KeyDestroyingDoorFactory extends LockedDoorFactory {

	/**
	 * Create a locked door.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return A KeyDestroyingDoor.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 *
	 * @see KeyDestroyingDoor
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		KeyDestroyingDoor door = new KeyDestroyingDoor(getKey(ctx), getClass(ctx));
		door.setRejectedMessage(getRejectedMessage(ctx));
		return door;
	}

}
