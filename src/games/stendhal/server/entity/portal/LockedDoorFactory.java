/*
 * @(#) src/games/stendhal/server/entity/portal/LockedDoorFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.server.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>LockedDoor</code> objects.
 */
public class LockedDoorFactory extends DoorFactory {

	//
	// LockedDoorFactory
	//

	/**
	 * Extract the portal key from a context.
	 *
	 * @param	ctx		The configuration context.
	 * @return	The key name.
	 * @throws	IllegalArgumentException If the class attribute is missing.
	 */
	protected String getKey(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return ctx.getRequiredString("key");
	}


	//
	// ConfigurableFactory
	//

	/**
	 * Create a locked door.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A LockedDoor.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		LockedDoor
	 */
	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new LockedDoor(getKey(ctx), getClass(ctx));
	}
}
