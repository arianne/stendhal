/*
 * @(#) src/games/stendhal/server/entity/portal/OnePlayerRoomDoorFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.common.ConfigurableFactoryContext;

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
	 * @param	ctx		Configuration context.
	 *
	 * @return	A OnePlayerRoomDoor.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		OnePlayerRoomDoor
	 */
	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new OnePlayerRoomDoor(getClass(ctx), getDirection(ctx));
	}
}
