/*
 * @(#) src/games/stendhal/server/entity/portal/OnePlayerRoomDoorFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>OnePlayerRoomDoor</code> objects.
 */
public class OnePlayerRoomDoorFactory extends DoorFactory {

	//
	// ConfigurableFactory
	//

	/**
	 * Create a door that lets one player at a time to enter.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return A OnePlayerRoomDoor.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 *
	 * @see OnePlayerRoomDoor
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		return new OnePlayerRoomDoor(getClass(ctx));
	}
}
